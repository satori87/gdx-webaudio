package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.PeriodicWave;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.ConstantSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates ConstantSourceNode, PeriodicWave (custom waveforms),
 * AudioBufferSourceNode.detune, start(when,offset), start(when,offset,duration),
 * and onended callbacks.
 */
public class AdvancedSourcesExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer musicBuf;
    private boolean loaded = false;
    private String status = "Loading audio...";
    private String endedInfo = "";

    private OscillatorNode currentOsc;
    private ConstantSourceNode currentConst;
    private AudioBufferSourceNode currentBufSrc;
    private GainNode currentGain;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        try {
            byte[] data = Gdx.files.internal("music-calm.wav").readBytes();
            ctx.decodeAudioData(data, buf -> {
                musicBuf = buf;
                loaded = true;
                status = "Ready! Duration: " + String.format("%.2f", buf.getDuration()) + "s";
            }, () -> status = "Failed to decode audio");
        } catch (Exception e) {
            status = "File error: " + e.getMessage();
        }
    }

    private void stopAll() {
        try { if (currentOsc != null) { currentOsc.stop(); currentOsc = null; } } catch (Exception ignored) {}
        try { if (currentConst != null) { currentConst.stop(); currentConst = null; } } catch (Exception ignored) {}
        try { if (currentBufSrc != null) { currentBufSrc.stop(); currentBufSrc = null; } } catch (Exception ignored) {}
        if (currentGain != null) { currentGain.disconnect(); currentGain = null; }
    }

    /** ConstantSourceNode: DC offset that modulates a GainNode (tremolo effect) */
    private void demoConstantSource() {
        stopAll();
        // Main oscillator
        currentOsc = ctx.createOscillator();
        currentOsc.setType(OscillatorType.SINE);
        currentOsc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

        // ConstantSourceNode as a DC bias for gain modulation
        currentConst = ctx.createConstantSource();
        currentConst.getOffset().setValueAtTime(0.5f, ctx.getCurrentTime());

        // LFO modulates the constant source offset
        OscillatorNode lfo = ctx.createOscillator();
        lfo.setType(OscillatorType.SINE);
        lfo.getFrequency().setValueAtTime(4, ctx.getCurrentTime()); // 4Hz tremolo

        GainNode lfoDepth = ctx.createGain();
        lfoDepth.getGain().setValueAtTime(0.4f, ctx.getCurrentTime());
        lfo.connect(lfoDepth);
        lfoDepth.connectParam(currentConst.getOffset());

        // Route: osc -> gain (modulated by constant source)
        currentGain = ctx.createGain();
        currentConst.connectParam(currentGain.getGain());

        currentOsc.connect(currentGain);
        currentGain.connect(ctx.getDestination());

        currentOsc.start();
        currentConst.start();
        lfo.start();

        double stopTime = ctx.getCurrentTime() + 4;
        currentOsc.stop(stopTime);
        currentConst.stop(stopTime);
        lfo.stop(stopTime);
        currentOsc = null;
        currentConst = null;

        status = "ConstantSourceNode: DC offset + LFO = tremolo (4s)";
    }

    /** PeriodicWave: custom waveform with specific harmonics */
    private void demoPeriodicWave() {
        stopAll();
        currentOsc = ctx.createOscillator();

        // Create a custom waveform: fundamental + 3rd harmonic + 5th harmonic (organ-like)
        float[] real = {0, 0, 0, 0, 0, 0}; // cosine components (all zero for symmetric wave)
        float[] imag = {0, 1, 0, 0.5f, 0, 0.25f}; // sine components: fundamental + 3rd + 5th

        PeriodicWave wave = ctx.createPeriodicWave(real, imag);
        currentOsc.setPeriodicWave(wave);
        currentOsc.getFrequency().setValueAtTime(220, ctx.getCurrentTime());

        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.25f, ctx.getCurrentTime());
        currentOsc.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentOsc.start();

        status = "PeriodicWave: custom harmonics (1st + 3rd + 5th) at 220Hz";
    }

    /** PeriodicWave with disableNormalization */
    private void demoPeriodicWaveNoNorm() {
        stopAll();
        currentOsc = ctx.createOscillator();

        // Heavy harmonics — disableNormalization keeps original amplitudes
        float[] real = {0, 0, 0, 0, 0};
        float[] imag = {0, 1, 0.8f, 0.6f, 0.4f};

        PeriodicWave wave = ctx.createPeriodicWave(real, imag, true);
        currentOsc.setPeriodicWave(wave);
        currentOsc.getFrequency().setValueAtTime(110, ctx.getCurrentTime());

        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.15f, ctx.getCurrentTime());
        currentOsc.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentOsc.start();

        status = "PeriodicWave (disableNormalization=true) at 110Hz";
    }

    /** Buffer start with offset — skip into the middle */
    private void demoBufferOffset() {
        stopAll();
        if (musicBuf == null) { status = "Audio not loaded"; return; }
        currentBufSrc = ctx.createBufferSource();
        currentBufSrc.setBuffer(musicBuf);
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.5f, ctx.getCurrentTime());
        currentBufSrc.connect(currentGain);
        currentGain.connect(ctx.getDestination());

        // Start 2 seconds into the buffer
        double offset = Math.min(2.0, musicBuf.getDuration() / 2);
        currentBufSrc.start(ctx.getCurrentTime(), offset);

        status = "BufferSource start(when, offset=" + String.format("%.1f", offset) + "s) — skipping ahead";
    }

    /** Buffer start with offset and duration — play only a slice */
    private void demoBufferDuration() {
        stopAll();
        if (musicBuf == null) { status = "Audio not loaded"; return; }
        currentBufSrc = ctx.createBufferSource();
        currentBufSrc.setBuffer(musicBuf);
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.5f, ctx.getCurrentTime());
        currentBufSrc.connect(currentGain);
        currentGain.connect(ctx.getDestination());

        // Play a 1.5s slice starting at 1s offset
        double offset = Math.min(1.0, musicBuf.getDuration() / 3);
        double duration = 1.5;
        currentBufSrc.start(ctx.getCurrentTime(), offset, duration);

        currentBufSrc.setOnEnded(() -> endedInfo = "Slice playback ended at " + String.format("%.2f", ctx.getCurrentTime()) + "s");
        status = "BufferSource start(when, offset=" + String.format("%.1f", offset) + "s, duration=1.5s)";
    }

    /** Buffer detune — pitch shift without changing playbackRate */
    private void demoBufferDetune() {
        stopAll();
        if (musicBuf == null) { status = "Audio not loaded"; return; }
        currentBufSrc = ctx.createBufferSource();
        currentBufSrc.setBuffer(musicBuf);
        currentBufSrc.getDetune().setValueAtTime(700, ctx.getCurrentTime()); // +700 cents = up a 5th
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.5f, ctx.getCurrentTime());
        currentBufSrc.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentBufSrc.start();

        status = "BufferSource detune: +700 cents (up a perfect 5th)";
    }

    /** onended callback demo */
    private void demoOnEnded() {
        stopAll();
        endedInfo = "";
        currentOsc = ctx.createOscillator();
        currentOsc.setType(OscillatorType.SINE);
        currentOsc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());
        currentOsc.connect(currentGain);
        currentGain.connect(ctx.getDestination());

        currentOsc.setOnEnded(() -> endedInfo = "onended fired! Oscillator finished at " + String.format("%.2f", ctx.getCurrentTime()) + "s");

        double now = ctx.getCurrentTime();
        currentOsc.start(now);
        currentOsc.stop(now + 1.5);
        currentOsc = null;

        status = "Oscillator plays for 1.5s — watch for onended callback";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Advanced Sources", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        if (!endedInfo.isEmpty()) {
            font.setColor(0.3f, 1, 0.3f, 1);
            font.draw(batch, endedInfo, 20, Gdx.graphics.getHeight() - 85);
        }
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "ConstantSource", "PeriodicWave", "PeriodicWave (noNorm)",
            "Buffer: Offset", "Buffer: Duration", "Buffer: Detune",
            "onended Callback", "Stop All"
        );

        try {
            switch (btn) {
                case 0: demoConstantSource(); break;
                case 1: demoPeriodicWave(); break;
                case 2: demoPeriodicWaveNoNorm(); break;
                case 3: demoBufferOffset(); break;
                case 4: demoBufferDuration(); break;
                case 5: demoBufferDetune(); break;
                case 6: demoOnEnded(); break;
                case 7: stopAll(); status = "Stopped"; break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("AdvSrc", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopAll();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
