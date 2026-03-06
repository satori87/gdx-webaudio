package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.ConvolverNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates ConvolverNode for convolution reverb using both a loaded
 * impulse response file and a procedurally generated one.
 */
public class ConvolutionReverbExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer irBuffer;       // loaded impulse response
    private AudioBuffer synthIrBuffer;  // synthesized impulse response
    private AudioBuffer sfxBuffer;      // sound effect to convolve
    private ConvolverNode convolver;
    private GainNode dryGain, wetGain, masterGain;
    private boolean loaded = false;
    private int loadCount = 0;
    private boolean normalize = true;
    private float dryLevel = 0.5f, wetLevel = 0.5f;
    private String status = "Loading audio...";
    private OscillatorNode currentOsc;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Synthesize an impulse response (exponential decay noise = room reverb)
        float sr = ctx.getSampleRate();
        int irLen = (int) (sr * 2); // 2-second reverb tail
        synthIrBuffer = ctx.createBuffer(2, irLen, sr);
        for (int ch = 0; ch < 2; ch++) {
            float[] irData = synthIrBuffer.getChannelData(ch);
            for (int i = 0; i < irLen; i++) {
                float t = (float) i / sr;
                float decay = (float) Math.exp(-t * 3.0); // 3.0 = decay rate
                irData[i] = ((float) (Math.random() * 2 - 1)) * decay;
            }
            synthIrBuffer.copyToChannel(irData, ch);
        }

        // Setup routing: source -> dry/wet split -> master -> destination
        masterGain = ctx.createGain();
        masterGain.getGain().setValueAtTime(0.6f, ctx.getCurrentTime());
        masterGain.connect(ctx.getDestination());

        dryGain = ctx.createGain();
        dryGain.getGain().setValueAtTime(dryLevel, ctx.getCurrentTime());
        dryGain.connect(masterGain);

        wetGain = ctx.createGain();
        wetGain.getGain().setValueAtTime(wetLevel, ctx.getCurrentTime());
        wetGain.connect(masterGain);

        convolver = ctx.createConvolver();
        convolver.setBuffer(synthIrBuffer);
        convolver.setNormalize(normalize);
        convolver.connect(wetGain);

        // Load impulse response file
        try {
            byte[] irData = Gdx.files.internal("impulse-response.wav").readBytes();
            ctx.decodeAudioData(irData, buf -> {
                irBuffer = buf;
                loadCount++;
                checkLoaded();
            }, () -> {
                loadCount++;
                checkLoaded();
            });
        } catch (Exception e) {
            loadCount++;
            checkLoaded();
        }

        // Load a sound effect
        try {
            byte[] sfxData = Gdx.files.internal("coin.wav").readBytes();
            ctx.decodeAudioData(sfxData, buf -> {
                sfxBuffer = buf;
                loadCount++;
                checkLoaded();
            }, () -> {
                loadCount++;
                checkLoaded();
            });
        } catch (Exception e) {
            loadCount++;
            checkLoaded();
        }
    }

    private void checkLoaded() {
        if (loadCount >= 2) {
            loaded = true;
            status = "Ready! Synthetic IR active. Dry/Wet: " + (int)(dryLevel*100) + "/" + (int)(wetLevel*100);
        }
    }

    private void playSfxThrough() {
        if (sfxBuffer == null) { status = "SFX not loaded"; return; }
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(sfxBuffer);
        src.connect(dryGain);
        src.connect(convolver);
        src.start();
        status = "Coin SFX with reverb";
    }

    private void playOscThrough() {
        stopOsc();
        currentOsc = ctx.createOscillator();
        currentOsc.setType(OscillatorType.SAWTOOTH);
        currentOsc.getFrequency().setValueAtTime(220, ctx.getCurrentTime());
        GainNode oscGain = ctx.createGain();
        oscGain.getGain().setValueAtTime(0.2f, ctx.getCurrentTime());
        currentOsc.connect(oscGain);
        oscGain.connect(dryGain);
        oscGain.connect(convolver);
        currentOsc.start();
        status = "Oscillator with reverb (sawtooth 220Hz)";
    }

    private void playSfxDry() {
        if (sfxBuffer == null) { status = "SFX not loaded"; return; }
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(sfxBuffer);
        src.connect(masterGain);
        src.start();
        status = "Coin SFX dry (no reverb)";
    }

    private void stopOsc() {
        try { if (currentOsc != null) { currentOsc.stop(); currentOsc = null; } } catch (Exception ignored) {}
    }

    private void useSyntheticIR() {
        convolver.setBuffer(synthIrBuffer);
        status = "Using SYNTHETIC impulse response (2s decay)";
    }

    private void useLoadedIR() {
        if (irBuffer == null) { status = "IR file not loaded!"; return; }
        convolver.setBuffer(irBuffer);
        status = "Using LOADED impulse response (impulse-response.wav)";
    }

    private void toggleNormalize() {
        normalize = !normalize;
        convolver.setNormalize(normalize);
        status = "Normalize: " + (normalize ? "ON" : "OFF");
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Convolution Reverb (ConvolverNode)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, "Dry: " + (int)(dryLevel*100) + "%  Wet: " + (int)(wetLevel*100)
            + "%  Normalize: " + (normalize ? "ON" : "OFF"), 20, Gdx.graphics.getHeight() - 85);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "Coin + Reverb", "Coin (Dry)", "Osc + Reverb",
            "Stop Osc", "Synthetic IR", "Loaded IR",
            "Normalize Toggle", "Dry +", "Wet +"
        );

        try {
            switch (btn) {
                case 0: playSfxThrough(); break;
                case 1: playSfxDry(); break;
                case 2: playOscThrough(); break;
                case 3: stopOsc(); status = "Oscillator stopped"; break;
                case 4: useSyntheticIR(); break;
                case 5: useLoadedIR(); break;
                case 6: toggleNormalize(); break;
                case 7:
                    dryLevel = Math.min(1, dryLevel + 0.1f);
                    wetLevel = Math.max(0, 1 - dryLevel);
                    dryGain.getGain().setValueAtTime(dryLevel, ctx.getCurrentTime());
                    wetGain.getGain().setValueAtTime(wetLevel, ctx.getCurrentTime());
                    status = "Dry: " + (int)(dryLevel*100) + "% Wet: " + (int)(wetLevel*100) + "%";
                    break;
                case 8:
                    wetLevel = Math.min(1, wetLevel + 0.1f);
                    dryLevel = Math.max(0, 1 - wetLevel);
                    dryGain.getGain().setValueAtTime(dryLevel, ctx.getCurrentTime());
                    wetGain.getGain().setValueAtTime(wetLevel, ctx.getCurrentTime());
                    status = "Dry: " + (int)(dryLevel*100) + "% Wet: " + (int)(wetLevel*100) + "%";
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("Reverb", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopOsc();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
