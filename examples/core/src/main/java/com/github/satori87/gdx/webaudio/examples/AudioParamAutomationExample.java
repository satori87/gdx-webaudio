package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates all AudioParam automation methods:
 * exponentialRamp, setTargetAtTime, setValueCurveAtTime,
 * cancelScheduledValues, cancelAndHoldAtTime, and param info.
 */
public class AudioParamAutomationExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private OscillatorNode osc;
    private GainNode gain;
    private String status = "Click a button to hear automation";
    private String paramInfo = "";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
        showParamInfo();
    }

    private void showParamInfo() {
        OscillatorNode temp = ctx.createOscillator();
        AudioParam freq = temp.getFrequency();
        paramInfo = String.format("OscillatorNode.frequency — default: %.0f, min: %.0f, max: %.0f",
            freq.getDefaultValue(), freq.getMinValue(), freq.getMaxValue());
        AudioParam detune = temp.getDetune();
        paramInfo += String.format("\nOscillatorNode.detune — default: %.0f, min: %.0f, max: %.0f",
            detune.getDefaultValue(), detune.getMinValue(), detune.getMaxValue());
        GainNode tempGain = ctx.createGain();
        AudioParam g = tempGain.getGain();
        paramInfo += String.format("\nGainNode.gain — default: %.1f, min: %.4f, max: %.0f",
            g.getDefaultValue(), g.getMinValue(), g.getMaxValue());
    }

    private void stopCurrent() {
        try { if (osc != null) { osc.stop(); osc = null; } } catch (Exception ignored) {}
        if (gain != null) { gain.disconnect(); gain = null; }
    }

    private void startOsc() {
        stopCurrent();
        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SINE);
        gain = ctx.createGain();
        gain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());
        osc.connect(gain);
        gain.connect(ctx.getDestination());
    }

    private void demoExponentialRamp() {
        startOsc();
        double now = ctx.getCurrentTime();
        // exponentialRamp requires non-zero starting value
        osc.getFrequency().setValueAtTime(200, now);
        osc.getFrequency().exponentialRampToValueAtTime(800, now + 2.0);
        // Auto-stop after 2.5s
        gain.getGain().setValueAtTime(0.3f, now);
        gain.getGain().linearRampToValueAtTime(0, now + 2.5);
        osc.start(now);
        osc.stop(now + 2.5);
        osc = null;
        status = "exponentialRamp: 200Hz -> 800Hz over 2s";
    }

    private void demoSetTargetAtTime() {
        startOsc();
        double now = ctx.getCurrentTime();
        osc.getFrequency().setValueAtTime(200, now);
        // Exponential approach to 800Hz with time constant 0.5s
        osc.getFrequency().setTargetAtTime(800, now, 0.5);
        gain.getGain().setValueAtTime(0.3f, now);
        gain.getGain().linearRampToValueAtTime(0, now + 3.0);
        osc.start(now);
        osc.stop(now + 3.0);
        osc = null;
        status = "setTargetAtTime: approach 800Hz, timeConstant=0.5s";
    }

    private void demoSetValueCurveAtTime() {
        startOsc();
        double now = ctx.getCurrentTime();
        // Custom frequency curve: wobble between 300 and 600 Hz
        float[] curve = new float[20];
        for (int i = 0; i < curve.length; i++) {
            curve[i] = 300 + 300 * (float) Math.sin(i * Math.PI / 4);
        }
        osc.getFrequency().setValueCurveAtTime(curve, now, 2.0);
        gain.getGain().setValueAtTime(0.3f, now);
        gain.getGain().linearRampToValueAtTime(0, now + 2.5);
        osc.start(now);
        osc.stop(now + 2.5);
        osc = null;
        status = "setValueCurveAtTime: custom wobble curve over 2s";
    }

    private void demoCancelScheduledValues() {
        startOsc();
        double now = ctx.getCurrentTime();
        // Schedule a long ramp, then cancel it partway
        osc.getFrequency().setValueAtTime(200, now);
        osc.getFrequency().linearRampToValueAtTime(1200, now + 4.0);
        // Cancel at 1.5s — frequency should freeze ~mid-ramp
        osc.getFrequency().cancelScheduledValues(now + 1.5);
        gain.getGain().setValueAtTime(0.3f, now);
        gain.getGain().linearRampToValueAtTime(0, now + 3.0);
        osc.start(now);
        osc.stop(now + 3.0);
        osc = null;
        status = "cancelScheduledValues: ramp 200->1200Hz, cancelled at 1.5s";
    }

    private void demoCancelAndHold() {
        startOsc();
        double now = ctx.getCurrentTime();
        osc.getFrequency().setValueAtTime(200, now);
        osc.getFrequency().linearRampToValueAtTime(1200, now + 4.0);
        // Cancel and hold at 1.5s — frequency freezes at whatever value it reached
        osc.getFrequency().cancelAndHoldAtTime(now + 1.5);
        gain.getGain().setValueAtTime(0.3f, now);
        gain.getGain().linearRampToValueAtTime(0, now + 3.0);
        osc.start(now);
        osc.stop(now + 3.0);
        osc = null;
        status = "cancelAndHoldAtTime: ramp cancelled+held at 1.5s";
    }

    private void demoExpRampGain() {
        startOsc();
        double now = ctx.getCurrentTime();
        osc.getFrequency().setValueAtTime(440, now);
        // Exponential volume swell (must start from non-zero)
        gain.getGain().setValueAtTime(0.001f, now);
        gain.getGain().exponentialRampToValueAtTime(0.5f, now + 1.0);
        gain.getGain().exponentialRampToValueAtTime(0.001f, now + 2.0);
        osc.start(now);
        osc.stop(now + 2.0);
        osc = null;
        status = "exponentialRamp on gain: swell up then down";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "AudioParam Automation Methods", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        String[] infoLines = paramInfo.split("\n");
        for (int i = 0; i < infoLines.length; i++) {
            font.draw(batch, infoLines[i], 20, Gdx.graphics.getHeight() - 90 - i * 16);
        }
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "exponentialRamp (freq)", "setTargetAtTime", "setValueCurveAtTime",
            "cancelScheduledValues", "cancelAndHoldAtTime", "exponentialRamp (gain)",
            "Stop"
        );

        try {
            switch (btn) {
                case 0: demoExponentialRamp(); break;
                case 1: demoSetTargetAtTime(); break;
                case 2: demoSetValueCurveAtTime(); break;
                case 3: demoCancelScheduledValues(); break;
                case 4: demoCancelAndHold(); break;
                case 5: demoExpRampGain(); break;
                case 6: stopCurrent(); status = "Stopped"; break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("ParamAuto", "Error", e);
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
