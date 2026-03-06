package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates all oscillator waveform types and procedural noise generation.
 * Covers: sine, square, sawtooth, triangle waveforms + white, pink, brown noise.
 */
public class WaveformNoiseExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private OscillatorNode currentOsc;
    private GainNode currentGain;
    private AudioBufferSourceNode currentNoise;
    private String playing = "Nothing";

    private AudioBuffer whiteNoiseBuf;
    private AudioBuffer pinkNoiseBuf;
    private AudioBuffer brownNoiseBuf;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
        generateNoiseBuffers();
    }

    private void generateNoiseBuffers() {
        float sampleRate = ctx.getSampleRate();
        int length = (int) (sampleRate * 2); // 2 seconds of noise

        // White noise
        whiteNoiseBuf = ctx.createBuffer(1, length, sampleRate);
        float[] white = whiteNoiseBuf.getChannelData(0);
        for (int i = 0; i < length; i++) {
            white[i] = (float) (Math.random() * 2 - 1);
        }
        whiteNoiseBuf.copyToChannel(white, 0);

        // Pink noise (Voss-McCartney approximation)
        pinkNoiseBuf = ctx.createBuffer(1, length, sampleRate);
        float[] pink = pinkNoiseBuf.getChannelData(0);
        float b0 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
        for (int i = 0; i < length; i++) {
            float w = (float) (Math.random() * 2 - 1);
            b0 = 0.99886f * b0 + w * 0.0555179f;
            b1 = 0.99332f * b1 + w * 0.0750759f;
            b2 = 0.96900f * b2 + w * 0.1538520f;
            b3 = 0.86650f * b3 + w * 0.3104856f;
            b4 = 0.55000f * b4 + w * 0.5329522f;
            b5 = -0.7616f * b5 - w * 0.0168980f;
            pink[i] = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + w * 0.5362f) * 0.11f;
            b6 = w * 0.115926f;
        }
        pinkNoiseBuf.copyToChannel(pink, 0);

        // Brown noise (integrated white noise)
        brownNoiseBuf = ctx.createBuffer(1, length, sampleRate);
        float[] brown = brownNoiseBuf.getChannelData(0);
        float last = 0;
        for (int i = 0; i < length; i++) {
            float w = (float) (Math.random() * 2 - 1);
            last = (last + 0.02f * w) / 1.02f;
            brown[i] = last * 3.5f;
        }
        brownNoiseBuf.copyToChannel(brown, 0);
    }

    private void stopCurrent() {
        try {
            if (currentOsc != null) {
                currentOsc.stop();
                currentOsc = null;
            }
        } catch (Exception ignored) {}
        try {
            if (currentNoise != null) {
                currentNoise.stop();
                currentNoise = null;
            }
        } catch (Exception ignored) {}
        if (currentGain != null) {
            currentGain.disconnect();
            currentGain = null;
        }
    }

    private void playOscillator(OscillatorType type) {
        stopCurrent();
        currentOsc = ctx.createOscillator();
        currentOsc.setType(type);
        currentOsc.getFrequency().setValueAtTime(220, ctx.getCurrentTime());
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());
        currentOsc.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentOsc.start();
        playing = type.toJsValue().toUpperCase() + " (220 Hz)";
    }

    private void playNoise(AudioBuffer buffer, String name) {
        stopCurrent();
        currentNoise = ctx.createBufferSource();
        currentNoise.setBuffer(buffer);
        currentNoise.setLoop(true);
        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());
        currentNoise.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentNoise.start();
        playing = name;
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // Draw info text
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Waveforms & Noise", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Playing: " + playing, 20, Gdx.graphics.getHeight() - 65);
        batch.end();

        // Draw buttons and handle input
        int btn = drawButtons(shapes, batch, font,
            "Sine", "Square", "Sawtooth",
            "Triangle", "White Noise", "Pink Noise",
            "Brown Noise", "Stop"
        );

        try {
            switch (btn) {
                case 0: playOscillator(OscillatorType.SINE); break;
                case 1: playOscillator(OscillatorType.SQUARE); break;
                case 2: playOscillator(OscillatorType.SAWTOOTH); break;
                case 3: playOscillator(OscillatorType.TRIANGLE); break;
                case 4: playNoise(whiteNoiseBuf, "WHITE NOISE"); break;
                case 5: playNoise(pinkNoiseBuf, "PINK NOISE"); break;
                case 6: playNoise(brownNoiseBuf, "BROWN NOISE"); break;
                case 7:
                    stopCurrent();
                    playing = "Nothing";
                    break;
            }
        } catch (Exception e) {
            playing = "Error: " + e.getMessage();
            Gdx.app.error("Waveform", "Playback error", e);
        }
    }

    @Override
    public void dispose() {
        stopCurrent();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
