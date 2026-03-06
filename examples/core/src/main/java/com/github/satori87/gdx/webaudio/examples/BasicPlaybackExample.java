package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates loading and playing audio files using decodeAudioData.
 * Loads WAV files from assets, decodes them, and plays them on demand.
 */
public class BasicPlaybackExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer coinBuffer, explosionBuffer, fanfareBuffer, musicBuffer;
    private AudioBufferSourceNode musicSource;
    private String status = "Loading audio files...";
    private int loaded = 0;
    private boolean musicPlaying;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        loadSound("coin.wav", buf -> { coinBuffer = buf; onLoaded("coin.wav"); });
        loadSound("explosion.wav", buf -> { explosionBuffer = buf; onLoaded("explosion.wav"); });
        loadSound("fanfare.wav", buf -> { fanfareBuffer = buf; onLoaded("fanfare.wav"); });
        loadSound("music-calm.wav", buf -> { musicBuffer = buf; onLoaded("music-calm.wav"); });
    }

    private void loadSound(String filename, AudioBuffer.DecodeCallback callback) {
        byte[] data = Gdx.files.internal(filename).readBytes();
        ctx.decodeAudioData(data, callback, () ->
                Gdx.app.error("BasicPlayback", "Failed to decode " + filename));
    }

    private void onLoaded(String name) {
        loaded++;
        if (loaded >= 4) {
            status = "All sounds loaded! Click buttons to play.";
        } else {
            status = "Loaded " + name + " (" + loaded + "/4)";
        }
    }

    private void playCoin() {
        if (coinBuffer == null) return;
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(coinBuffer);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(0.5f);
        src.connect(gain);
        gain.connect(ctx.getDestination());
        src.start();
        status = "Playing: coin.wav";
    }

    private void playExplosion() {
        if (explosionBuffer == null) return;
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(explosionBuffer);
        src.connect(ctx.getDestination());
        src.start();
        status = "Playing: explosion.wav";
    }

    private void playFanfare() {
        if (fanfareBuffer == null) return;
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(fanfareBuffer);
        src.connect(ctx.getDestination());
        src.start();
        status = "Playing: fanfare.wav";
    }

    private void playMusic() {
        if (musicBuffer == null || musicPlaying) return;
        musicSource = ctx.createBufferSource();
        musicSource.setBuffer(musicBuffer);
        musicSource.setLoop(true);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(0.4f);
        musicSource.connect(gain);
        gain.connect(ctx.getDestination());
        musicSource.start();
        musicPlaying = true;
        status = "Music playing (looped)";
    }

    private void stopMusic() {
        if (musicSource != null && musicPlaying) {
            musicSource.stop();
            musicSource = null;
            musicPlaying = false;
            status = "Music stopped";
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Basic Playback (Load & Play Audio Files)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Status: " + status, 20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, "Files loaded: " + loaded + "/4", 20, Gdx.graphics.getHeight() - 90);
        batch.end();

        int clicked = drawButtons(shapes, batch, font,
                "Play Coin", "Play Explosion", "Play Fanfare",
                "Play Music (loop)", "Stop Music");
        if (clicked == 0) playCoin();
        else if (clicked == 1) playExplosion();
        else if (clicked == 2) playFanfare();
        else if (clicked == 3) playMusic();
        else if (clicked == 4) stopMusic();
    }

    @Override
    public void dispose() {
        stopMusic();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
