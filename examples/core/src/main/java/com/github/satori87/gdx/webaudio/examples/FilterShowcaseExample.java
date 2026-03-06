package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.effect.BiquadFilterNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.IIRFilterNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;

/**
 * Demonstrates all 8 BiquadFilter types, IIR filter, filter gain/detune,
 * and getFrequencyResponse() visualization.
 */
public class FilterShowcaseExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer noiseBuf;
    private AudioBufferSourceNode currentSrc;
    private GainNode currentGain;
    private BiquadFilterNode currentFilter;
    private String status = "Generating noise...";
    private float[] magResponse;
    private float[] phaseResponse;
    private boolean showResponse = false;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Generate white noise buffer for filter demo
        float sr = ctx.getSampleRate();
        int len = (int) (sr * 3);
        noiseBuf = ctx.createBuffer(1, len, sr);
        float[] data = noiseBuf.getChannelData(0);
        for (int i = 0; i < len; i++) {
            data[i] = (float) (Math.random() * 2 - 1);
        }
        noiseBuf.copyToChannel(data, 0);
        status = "Ready! Click a filter type to hear noise through it.";
    }

    private void stopCurrent() {
        try { if (currentSrc != null) { currentSrc.stop(); currentSrc = null; } } catch (Exception ignored) {}
        if (currentGain != null) { currentGain.disconnect(); currentGain = null; }
        currentFilter = null;
        showResponse = false;
    }

    private void playWithBiquad(BiquadFilterType type, float freq, float q, float filterGain, float detune) {
        stopCurrent();
        currentSrc = ctx.createBufferSource();
        currentSrc.setBuffer(noiseBuf);
        currentSrc.setLoop(true);

        currentFilter = ctx.createBiquadFilter();
        currentFilter.setType(type);
        currentFilter.getFrequency().setValueAtTime(freq, ctx.getCurrentTime());
        currentFilter.getQ().setValueAtTime(q, ctx.getCurrentTime());
        if (filterGain != 0) {
            currentFilter.getGain().setValueAtTime(filterGain, ctx.getCurrentTime());
        }
        if (detune != 0) {
            currentFilter.getDetune().setValueAtTime(detune, ctx.getCurrentTime());
        }

        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());

        currentSrc.connect(currentFilter);
        currentFilter.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentSrc.start();

        status = type.toJsValue().toUpperCase() + " — freq:" + (int)freq + "Hz Q:" + q
            + (filterGain != 0 ? " gain:" + filterGain + "dB" : "")
            + (detune != 0 ? " detune:" + (int)detune + "cents" : "");
    }

    private void playWithIIR() {
        stopCurrent();
        currentSrc = ctx.createBufferSource();
        currentSrc.setBuffer(noiseBuf);
        currentSrc.setLoop(true);

        // Simple 2nd-order lowpass IIR coefficients
        float[] feedforward = {0.0675f, 0.1349f, 0.0675f};
        float[] feedback = {1.0f, -1.1430f, 0.4128f};
        IIRFilterNode iir = ctx.createIIRFilter(feedforward, feedback);

        // Get frequency response
        float[] freqHz = new float[128];
        magResponse = new float[128];
        phaseResponse = new float[128];
        for (int i = 0; i < 128; i++) {
            freqHz[i] = 20 * (float) Math.pow(1000, (double) i / 127); // 20Hz to 20kHz log scale
        }
        iir.getFrequencyResponse(freqHz, magResponse, phaseResponse);
        showResponse = true;

        currentGain = ctx.createGain();
        currentGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());
        currentSrc.connect(iir);
        iir.connect(currentGain);
        currentGain.connect(ctx.getDestination());
        currentSrc.start();

        status = "IIR Filter (2nd-order lowpass) — see frequency response below";
    }

    private void showBiquadResponse() {
        if (currentFilter == null) {
            status = "Start a biquad filter first!";
            return;
        }
        float[] freqHz = new float[128];
        magResponse = new float[128];
        phaseResponse = new float[128];
        for (int i = 0; i < 128; i++) {
            freqHz[i] = 20 * (float) Math.pow(1000, (double) i / 127);
        }
        currentFilter.getFrequencyResponse(freqHz, magResponse, phaseResponse);
        showResponse = true;
        status += " [Response shown]";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // Draw frequency response if available
        if (showResponse && magResponse != null) {
            int w = Gdx.graphics.getWidth();
            int graphBase = BTN_START_Y + 4 * (BTN_H + BTN_GAP) + 20;
            int graphHeight = 120;

            shapes.begin(ShapeRenderer.ShapeType.Line);
            // Magnitude response (green)
            shapes.setColor(0, 1, 0, 1);
            for (int i = 1; i < magResponse.length; i++) {
                float x1 = (float)(i - 1) / magResponse.length * w;
                float x2 = (float)i / magResponse.length * w;
                // Convert magnitude to dB scale, clamp
                float db1 = Math.max(-40, Math.min(20, 20 * (float) Math.log10(Math.max(0.0001f, magResponse[i - 1]))));
                float db2 = Math.max(-40, Math.min(20, 20 * (float) Math.log10(Math.max(0.0001f, magResponse[i]))));
                float y1 = graphBase + (db1 + 40) / 60 * graphHeight;
                float y2 = graphBase + (db2 + 40) / 60 * graphHeight;
                shapes.line(x1, y1, x2, y2);
            }
            // Phase response (yellow)
            shapes.setColor(1, 1, 0, 0.5f);
            for (int i = 1; i < phaseResponse.length; i++) {
                float x1 = (float)(i - 1) / phaseResponse.length * w;
                float x2 = (float)i / phaseResponse.length * w;
                float y1 = graphBase + graphHeight / 2f + phaseResponse[i - 1] / (float)Math.PI * graphHeight / 2f;
                float y2 = graphBase + graphHeight / 2f + phaseResponse[i] / (float)Math.PI * graphHeight / 2f;
                shapes.line(x1, y1, x2, y2);
            }
            shapes.end();

            batch.begin();
            font.setColor(0, 1, 0, 1);
            font.draw(batch, "Mag (dB)", 5, graphBase + graphHeight + 15);
            font.setColor(1, 1, 0, 1);
            font.draw(batch, "Phase", 80, graphBase + graphHeight + 15);
            font.setColor(0.5f, 0.5f, 0.5f, 1);
            font.draw(batch, "20Hz", 5, graphBase - 2);
            font.draw(batch, "20kHz", w - 40, graphBase - 2);
            batch.end();
        }

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Filter Showcase (All BiquadFilter Types + IIR)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "Lowpass", "Highpass", "Bandpass",
            "Lowshelf", "Highshelf", "Peaking",
            "Notch", "Allpass", "IIR Filter",
            "Freq Response", "Stop"
        );

        try {
            switch (btn) {
                case 0: playWithBiquad(BiquadFilterType.LOWPASS, 800, 5, 0, 0); break;
                case 1: playWithBiquad(BiquadFilterType.HIGHPASS, 2000, 5, 0, 0); break;
                case 2: playWithBiquad(BiquadFilterType.BANDPASS, 1000, 10, 0, 0); break;
                case 3: playWithBiquad(BiquadFilterType.LOWSHELF, 500, 1, 15, 0); break;
                case 4: playWithBiquad(BiquadFilterType.HIGHSHELF, 3000, 1, -10, 0); break;
                case 5: playWithBiquad(BiquadFilterType.PEAKING, 1000, 5, 12, 100); break;
                case 6: playWithBiquad(BiquadFilterType.NOTCH, 1000, 10, 0, 0); break;
                case 7: playWithBiquad(BiquadFilterType.ALLPASS, 1000, 5, 0, 0); break;
                case 8: playWithIIR(); break;
                case 9: showBiquadResponse(); break;
                case 10: stopCurrent(); status = "Stopped"; break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("Filter", "Error", e);
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
