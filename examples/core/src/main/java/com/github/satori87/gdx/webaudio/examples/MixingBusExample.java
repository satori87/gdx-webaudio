package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.channel.ChannelMergerNode;
import com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates mixing buses (sound groups), channel splitting/merging,
 * and sidechain ducking with DynamicsCompressorNode.
 */
public class MixingBusExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    // Audio buffers
    private AudioBuffer musicBuf;
    private AudioBuffer sfxBuf;
    private boolean loaded = false;
    private int loadCount = 0;
    private String status = "Loading audio...";

    // Mix bus nodes
    private GainNode musicBus;
    private GainNode sfxBus;
    private GainNode masterBus;

    // Active sources
    private AudioBufferSourceNode musicSource;
    private AudioBufferSourceNode sfxSource;

    // Ducking
    private GainNode duckGain;
    private GainNode duckSidechain;
    private OscillatorNode duckOsc;
    private AudioBufferSourceNode duckMusicSource;
    private boolean duckingActive = false;

    // Channel demo
    private AudioBufferSourceNode channelSource;
    private boolean channelDemoActive = false;

    // Bus volumes
    private float musicVol = 0.7f;
    private float sfxVol = 1.0f;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Create mix buses: Music Bus + SFX Bus -> Master Bus -> Destination
        masterBus = ctx.createGain();
        masterBus.getGain().setValueAtTime(0.8f, ctx.getCurrentTime());
        masterBus.connect(ctx.getDestination());

        musicBus = ctx.createGain();
        musicBus.getGain().setValueAtTime(musicVol, ctx.getCurrentTime());
        musicBus.connect(masterBus);

        sfxBus = ctx.createGain();
        sfxBus.getGain().setValueAtTime(sfxVol, ctx.getCurrentTime());
        sfxBus.connect(masterBus);

        // Load audio
        try {
            byte[] musicData = Gdx.files.internal("music-calm.wav").readBytes();
            ctx.decodeAudioData(musicData, buffer -> {
                musicBuf = buffer;
                checkLoaded();
            }, () -> status = "Failed to decode music");

            byte[] sfxData = Gdx.files.internal("explosion.wav").readBytes();
            ctx.decodeAudioData(sfxData, buffer -> {
                sfxBuf = buffer;
                checkLoaded();
            }, () -> status = "Failed to decode SFX");
        } catch (Exception e) {
            status = "File error: " + e.getMessage();
        }
    }

    private void checkLoaded() {
        loadCount++;
        if (loadCount >= 2) {
            loaded = true;
            status = "Ready! Music Bus + SFX Bus -> Master Bus -> Output";
        }
    }

    private void playMusic() {
        stopMusic();
        if (musicBuf == null) return;
        musicSource = ctx.createBufferSource();
        musicSource.setBuffer(musicBuf);
        musicSource.setLoop(true);
        musicSource.connect(musicBus);
        musicSource.start();
        status = "Music playing on Music Bus";
    }

    private void stopMusic() {
        try {
            if (musicSource != null) { musicSource.stop(); musicSource = null; }
        } catch (Exception ignored) {}
    }

    private void playSfx() {
        if (sfxBuf == null) return;
        // Fire-and-forget: each SFX creates a new source
        AudioBufferSourceNode sfx = ctx.createBufferSource();
        sfx.setBuffer(sfxBuf);
        sfx.connect(sfxBus);
        sfx.start();
        status = "SFX triggered on SFX Bus";
    }

    /** Sidechain ducking: LFO modulates a gain node to rhythmically duck the music volume */
    private void startDucking() {
        stopDucking();
        if (musicBuf == null) return;

        // Music source -> duckGain -> destination
        // An LFO (slow oscillator) modulates duckGain to create the pumping effect
        duckGain = ctx.createGain();
        duckGain.getGain().setValueAtTime(1.0f, ctx.getCurrentTime());
        duckGain.connect(ctx.getDestination());

        duckMusicSource = ctx.createBufferSource();
        duckMusicSource.setBuffer(musicBuf);
        duckMusicSource.setLoop(true);
        duckMusicSource.connect(duckGain);
        duckMusicSource.start();

        // LFO: sine wave at 2Hz modulates the gain (pumping effect)
        // Gain oscillates between ~0.1 and ~1.0
        duckOsc = ctx.createOscillator();
        duckOsc.setType(OscillatorType.SINE);
        duckOsc.getFrequency().setValueAtTime(2, ctx.getCurrentTime());

        // Scale the LFO: offset 0.55, amplitude 0.45 → range [0.1, 1.0]
        duckSidechain = ctx.createGain();
        duckSidechain.getGain().setValueAtTime(0.45f, ctx.getCurrentTime());
        duckOsc.connect(duckSidechain);
        duckSidechain.connectParam(duckGain.getGain());

        // Set the DC offset for the gain so it centers around 0.55
        duckGain.getGain().setValueAtTime(0.55f, ctx.getCurrentTime());

        duckOsc.start();

        duckingActive = true;
        status = "Ducking: 2Hz LFO pumping the music volume";
    }

    private void stopDucking() {
        try { if (duckOsc != null) { duckOsc.stop(); duckOsc = null; } } catch (Exception ignored) {}
        try { if (duckMusicSource != null) { duckMusicSource.stop(); duckMusicSource = null; } } catch (Exception ignored) {}
        if (duckSidechain != null) { duckSidechain.disconnect(); duckSidechain = null; }
        if (duckGain != null) { duckGain.disconnect(); duckGain = null; }
        duckingActive = false;
    }

    /** Channel demo: swap L/R using splitter/merger, or pan hard-left using StereoPannerNode */
    private void startChannelSwap() {
        stopChannelDemo();
        if (musicBuf == null) return;

        channelSource = ctx.createBufferSource();
        channelSource.setBuffer(musicBuf);
        channelSource.setLoop(true);

        // Split into 2 channels, merge with swapped routing
        ChannelSplitterNode splitter = ctx.createChannelSplitter(2);
        channelSource.connect(splitter);

        ChannelMergerNode merger = ctx.createChannelMerger(2);
        splitter.connect(merger, 0, 1); // L -> R
        splitter.connect(merger, 1, 0); // R -> L

        GainNode outGain = ctx.createGain();
        outGain.getGain().setValueAtTime(0.5f, ctx.getCurrentTime());
        merger.connect(outGain);
        outGain.connect(ctx.getDestination());

        channelSource.start();
        channelDemoActive = true;
        status = "Channel Demo: L/R swapped (Splitter → Merger)";
    }

    /** Pan hard-left using StereoPannerNode — audio only in left ear */
    private void startLeftOnly() {
        stopChannelDemo();
        if (musicBuf == null) return;

        channelSource = ctx.createBufferSource();
        channelSource.setBuffer(musicBuf);
        channelSource.setLoop(true);

        StereoPannerNode panner = ctx.createStereoPanner();
        panner.getPan().setValueAtTime(-1.0f, ctx.getCurrentTime()); // full left

        GainNode outGain = ctx.createGain();
        outGain.getGain().setValueAtTime(0.5f, ctx.getCurrentTime());

        channelSource.connect(panner);
        panner.connect(outGain);
        outGain.connect(ctx.getDestination());

        channelSource.start();
        channelDemoActive = true;
        status = "Channel Demo: Left ear only (StereoPannerNode pan=-1)";
    }

    private void stopChannelDemo() {
        try { if (channelSource != null) { channelSource.stop(); channelSource = null; } } catch (Exception ignored) {}
        channelDemoActive = false;
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // Draw info
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Mixing Bus / Channel Routing / Ducking", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, "Music Bus: " + (int)(musicVol * 100) + "%  |  SFX Bus: " + (int)(sfxVol * 100) + "%",
            20, Gdx.graphics.getHeight() - 85);
        if (duckingActive) {
            font.setColor(1, 0.8f, 0.3f, 1);
            font.draw(batch, "LFO ducking active — listen for the rhythmic pumping",
                20, Gdx.graphics.getHeight() - 105);
        }
        batch.end();

        // Buttons
        int btn = drawButtons(shapes, batch, font,
            "Play Music", "Trigger SFX", "Stop Music",
            "Music Vol -", "Music Vol +", "SFX Vol -",
            "SFX Vol +", "Mute Music Bus", "Mute SFX Bus",
            "Start Ducking", "Stop Ducking", "Chan: Swap L/R",
            "Chan: Left Only", "Stop Chan Demo", "Stop All"
        );

        if (!loaded && btn >= 0 && btn != 14) return;

        try {
            switch (btn) {
                case 0: playMusic(); break;
                case 1: playSfx(); break;
                case 2: stopMusic(); status = "Music stopped"; break;
                case 3:
                    musicVol = Math.max(0, musicVol - 0.1f);
                    musicBus.getGain().setValueAtTime(musicVol, ctx.getCurrentTime());
                    break;
                case 4:
                    musicVol = Math.min(2.0f, musicVol + 0.1f);
                    musicBus.getGain().setValueAtTime(musicVol, ctx.getCurrentTime());
                    break;
                case 5:
                    sfxVol = Math.max(0, sfxVol - 0.1f);
                    sfxBus.getGain().setValueAtTime(sfxVol, ctx.getCurrentTime());
                    break;
                case 6:
                    sfxVol = Math.min(2.0f, sfxVol + 0.1f);
                    sfxBus.getGain().setValueAtTime(sfxVol, ctx.getCurrentTime());
                    break;
                case 7:
                    musicBus.getGain().setValueAtTime(0, ctx.getCurrentTime());
                    musicVol = 0;
                    status = "Music Bus muted";
                    break;
                case 8:
                    sfxBus.getGain().setValueAtTime(0, ctx.getCurrentTime());
                    sfxVol = 0;
                    status = "SFX Bus muted";
                    break;
                case 9: startDucking(); break;
                case 10: stopDucking(); status = "Ducking stopped"; break;
                case 11: startChannelSwap(); break;
                case 12: startLeftOnly(); break;
                case 13: stopChannelDemo(); status = "Channel demo stopped"; break;
                case 14:
                    stopMusic();
                    stopDucking();
                    stopChannelDemo();
                    status = "All stopped";
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("MixBus", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopMusic();
        stopDucking();
        stopChannelDemo();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
