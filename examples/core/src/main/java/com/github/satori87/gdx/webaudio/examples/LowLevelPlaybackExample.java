package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.SoundGroup;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates the same features as {@link SimplePlaybackExample} but using
 * the low-level Web Audio API directly.
 *
 * <p>Compare this with {@link SimplePlaybackExample} to see how the high-level
 * {@code WebSound}/{@code WebMusic} API simplifies common audio tasks. Here,
 * every play requires manually creating an {@link AudioBufferSourceNode},
 * connecting a {@link GainNode} and {@link StereoPannerNode}, tracking nodes
 * for stop/pause, and computing playback position by hand.</p>
 */
public class LowLevelPlaybackExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer coinBuffer, explosionBuffer, musicBuffer;
    private SoundGroup sfxGroup;
    private String status = "Loading...";
    private int loaded = 0;
    private static final int TOTAL = 3;

    // Looping coin tracking
    private AudioBufferSourceNode loopSource;

    // Music state — managed manually since AudioBufferSourceNode is one-shot
    private AudioBufferSourceNode musicSource;
    private GainNode musicGain;
    private StereoPannerNode musicPanner;
    private boolean musicPlaying = false;
    private boolean musicLooping = true;
    private float musicVolume = 1.0f;
    private float musicPan = 0f;
    private double musicStartTime = 0;  // ctx time when playback started
    private double musicOffset = 0;     // offset into buffer when playback started

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Create a SoundGroup to route all SFX through (same as high-level example)
        sfxGroup = ctx.createSoundGroup();
        sfxGroup.getOutput().connect(ctx.getDestination());

        // Persistent music gain + panner chain (reused across play/pause/seek)
        musicGain = ctx.createGain();
        musicPanner = ctx.createStereoPanner();
        musicPanner.connect(musicGain);
        musicGain.connect(ctx.getDestination());

        // Load sounds manually: read bytes, then decode asynchronously
        loadAndDecode("coin.wav", buf -> { coinBuffer = buf; onLoaded("coin.wav"); });
        loadAndDecode("explosion.wav", buf -> { explosionBuffer = buf; onLoaded("explosion.wav"); });
        loadAndDecode("music-calm.wav", buf -> { musicBuffer = buf; onLoaded("music-calm.wav"); });
    }

    private void loadAndDecode(String filename, AudioBuffer.DecodeCallback onDecoded) {
        byte[] data = Gdx.files.internal(filename).readBytes();
        ctx.decodeAudioData(data, onDecoded,
            () -> Gdx.app.error("LowLevelPlayback", "Failed to decode " + filename));
    }

    private void onLoaded(String name) {
        loaded++;
        if (loaded >= TOTAL) {
            status = "All loaded! Click buttons to play.";
        } else {
            status = "Loaded " + name + " (" + loaded + "/" + TOTAL + ")";
        }
    }

    /** Fire a one-shot sound through the SFX group with the given parameters. */
    private void playOneShot(AudioBuffer buffer, float volume, float pitch, float pan, AudioNode output) {
        AudioBufferSourceNode source = ctx.createBufferSource();
        source.setBuffer(buffer);
        source.getPlaybackRate().setValue(pitch);

        GainNode gain = ctx.createGain();
        gain.getGain().setValue(volume);

        StereoPannerNode panner = ctx.createStereoPanner();
        panner.getPan().setValue(pan);

        // Wire: source → panner → gain → output
        source.connect(panner);
        panner.connect(gain);
        gain.connect(output);
        source.start();
    }

    /** Start (or restart) the music source from the given offset. */
    private void startMusicSource(double offset) {
        musicSource = ctx.createBufferSource();
        musicSource.setBuffer(musicBuffer);
        musicSource.setLoop(musicLooping);
        musicSource.connect(musicPanner);
        musicSource.setOnEnded(() -> {
            // Only update state if this wasn't a manual stop/pause/seek
            if (musicPlaying && musicSource != null) {
                musicPlaying = false;
                musicOffset = 0;
                status = "Music finished naturally";
            }
        });
        musicOffset = offset;
        musicStartTime = ctx.getCurrentTime();
        musicSource.start(0, offset);
        musicPlaying = true;
    }

    private void stopMusicSource() {
        if (musicSource != null) {
            AudioBufferSourceNode old = musicSource;
            musicSource = null; // clear before stop so onEnded doesn't reset state
            old.stop();
        }
    }

    private float getMusicPosition() {
        if (!musicPlaying || musicBuffer == null) return (float) musicOffset;
        double elapsed = ctx.getCurrentTime() - musicStartTime;
        double pos = musicOffset + elapsed;
        if (musicLooping && musicBuffer.getDuration() > 0) {
            pos = pos % musicBuffer.getDuration();
        }
        return (float) pos;
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);

        int h = Gdx.graphics.getHeight();
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Low-Level Playback (Using Low-Level API)", 20, h - 40);
        font.draw(batch, "Status: " + status, 20, h - 70);

        if (musicBuffer != null) {
            String state = musicPlaying ? "Playing" : "Stopped";
            font.draw(batch, "Music: " + state
                + "  Vol: " + String.format("%.1f", musicVolume)
                + "  Pos: " + String.format("%.1f", getMusicPosition())
                + "s / " + String.format("%.1f", musicBuffer.getDuration()) + "s", 20, h - 90);
        }

        font.draw(batch, "SFX Group Vol: " + String.format("%.1f", sfxGroup.getVolume())
            + (loopSource != null ? "  (coin looping)" : ""), 20, h - 110);
        batch.end();

        int clicked = drawButtons(shapes, batch, font,
            "Play Coin", "Play Explosion", "Coin Hi-Pitch",
            "Loop Coin", "Stop Loop", "SFX Vol +",
            "SFX Vol -", "Play Music", "Pause Music",
            "Stop Music", "Music Vol +", "Music Vol -",
            "Seek +5s", "Pan Left", "Pan Right");

        switch (clicked) {
            case 0: // Play Coin
                if (coinBuffer != null) {
                    playOneShot(coinBuffer, 1f, 1f, 0f, sfxGroup.getInput());
                    status = "Coin!";
                }
                break;
            case 1: // Play Explosion
                if (explosionBuffer != null) {
                    playOneShot(explosionBuffer, 1f, 1f, 0f, sfxGroup.getInput());
                    status = "Explosion!";
                }
                break;
            case 2: // Coin Hi-Pitch
                if (coinBuffer != null) {
                    playOneShot(coinBuffer, 0.8f, 2.0f, 0.5f, sfxGroup.getInput());
                    status = "Coin (high pitch, panned right)";
                }
                break;
            case 3: // Loop Coin
                if (coinBuffer != null && loopSource == null) {
                    AudioBufferSourceNode source = ctx.createBufferSource();
                    source.setBuffer(coinBuffer);
                    source.setLoop(true);
                    GainNode gain = ctx.createGain();
                    gain.getGain().setValue(0.4f);
                    source.connect(gain);
                    gain.connect(sfxGroup.getInput());
                    source.start();
                    loopSource = source;
                    status = "Coin looping";
                }
                break;
            case 4: // Stop Loop
                if (loopSource != null) {
                    loopSource.stop();
                    loopSource = null;
                    status = "Loop stopped";
                }
                break;
            case 5: // SFX Vol +
                sfxGroup.setVolume(Math.min(1f, sfxGroup.getVolume() + 0.1f));
                status = "SFX Vol: " + String.format("%.1f", sfxGroup.getVolume());
                break;
            case 6: // SFX Vol -
                sfxGroup.setVolume(Math.max(0f, sfxGroup.getVolume() - 0.1f));
                status = "SFX Vol: " + String.format("%.1f", sfxGroup.getVolume());
                break;
            case 7: // Play Music
                if (musicBuffer != null) {
                    if (musicPlaying) {
                        stopMusicSource();
                    }
                    musicGain.getGain().setValue(musicVolume);
                    musicPanner.getPan().setValue(musicPan);
                    startMusicSource(musicPlaying ? getMusicPosition() : musicOffset);
                    status = "Music playing";
                }
                break;
            case 8: // Pause Music
                if (musicPlaying) {
                    double pos = getMusicPosition();
                    stopMusicSource();
                    musicPlaying = false;
                    musicOffset = pos;
                    status = "Music paused at " + String.format("%.1f", pos) + "s";
                }
                break;
            case 9: // Stop Music
                if (musicPlaying) {
                    stopMusicSource();
                    musicPlaying = false;
                }
                musicOffset = 0;
                status = "Music stopped";
                break;
            case 10: // Music Vol +
                musicVolume = Math.min(1f, musicVolume + 0.1f);
                musicGain.getGain().setValue(musicVolume);
                status = "Music Vol: " + String.format("%.1f", musicVolume);
                break;
            case 11: // Music Vol -
                musicVolume = Math.max(0f, musicVolume - 0.1f);
                musicGain.getGain().setValue(musicVolume);
                status = "Music Vol: " + String.format("%.1f", musicVolume);
                break;
            case 12: // Seek +5s
                if (musicBuffer != null) {
                    double newPos = getMusicPosition() + 5f;
                    if (musicPlaying) {
                        stopMusicSource();
                        startMusicSource(newPos);
                    } else {
                        musicOffset = newPos;
                    }
                    status = "Seeked to " + String.format("%.1f", getMusicPosition()) + "s";
                }
                break;
            case 13: // Pan Left
                musicPan = -1f;
                musicPanner.getPan().setValue(musicPan);
                status = "Music panned left";
                break;
            case 14: // Pan Right
                musicPan = 1f;
                musicPanner.getPan().setValue(musicPan);
                status = "Music panned right";
                break;
        }
    }

    @Override
    public void dispose() {
        if (loopSource != null) loopSource.stop();
        stopMusicSource();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
