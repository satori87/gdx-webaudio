package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.OscillatorType;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * Compares all 3 distance models (inverse, linear, exponential),
 * both panning models (HRTF vs equalpower), and StereoPannerNode
 * with static pan values.
 */
public class DistanceModelCompareExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private OscillatorNode osc;
    private PannerNode panner;
    private GainNode gain;
    private OscillatorNode panOsc;
    private GainNode panGain;
    private StereoPannerNode stereoPanner;

    private float time;
    private boolean playing;
    private boolean stereoPanActive;
    private float sourceX;
    private String status = "Compare distance models and panning";
    private String modelInfo = "";
    private float currentPan = 0;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        AudioListener listener = ctx.getListener();
        listener.setPosition(0, 0, 0);
        listener.setOrientation(0, 0, -1, 0, 1, 0);
    }

    private void stopPanner() {
        try { if (osc != null) { osc.stop(); osc = null; } } catch (Exception ignored) {}
        if (gain != null) { gain.disconnect(); gain = null; }
        panner = null;
        playing = false;
    }

    private void stopStereoPan() {
        try { if (panOsc != null) { panOsc.stop(); panOsc = null; } } catch (Exception ignored) {}
        if (panGain != null) { panGain.disconnect(); panGain = null; }
        stereoPanner = null;
        stereoPanActive = false;
    }

    private void startWithModel(DistanceModel distModel, PanningModel panModel) {
        stopPanner();
        stopStereoPan();

        panner = ctx.createPanner();
        panner.setDistanceModel(distModel);
        panner.setPanningModel(panModel);
        panner.setRefDistance(1);
        panner.setMaxDistance(50);
        panner.setRolloffFactor(1);

        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SINE);
        osc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

        gain = ctx.createGain();
        gain.getGain().setValueAtTime(0.4f, ctx.getCurrentTime());

        osc.connect(gain);
        gain.connect(panner);
        panner.connect(ctx.getDestination());
        osc.start();

        playing = true;
        time = 0;
        modelInfo = "Distance: " + distModel.toJsValue() + "  |  Panning: " + panModel.toJsValue();
        status = "Source moves left↔right. Listen to volume falloff pattern.";
    }

    private void startStereoPan(float pan) {
        stopPanner();
        stopStereoPan();

        panOsc = ctx.createOscillator();
        panOsc.setType(OscillatorType.SINE);
        panOsc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

        stereoPanner = ctx.createStereoPanner();
        stereoPanner.getPan().setValueAtTime(pan, ctx.getCurrentTime());

        panGain = ctx.createGain();
        panGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());

        panOsc.connect(stereoPanner);
        stereoPanner.connect(panGain);
        panGain.connect(ctx.getDestination());
        panOsc.start();

        currentPan = pan;
        stereoPanActive = true;
        modelInfo = "StereoPannerNode pan: " + pan;
        status = "Static stereo pan position";
    }

    private void adjustPan(float delta) {
        if (stereoPanner == null) return;
        currentPan = Math.max(-1, Math.min(1, currentPan + delta));
        stereoPanner.getPan().setValueAtTime(currentPan, ctx.getCurrentTime());
        modelInfo = "StereoPannerNode pan: " + String.format("%.1f", currentPan);
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (playing && panner != null) {
            time += Gdx.graphics.getDeltaTime();
            // Move source along X axis: -20 to +20 over 6 seconds
            sourceX = (float) Math.sin(time * 0.5) * 20;
            panner.setPosition(sourceX, 0, -5);
        }

        // Draw visualization
        int cx = Gdx.graphics.getWidth() / 2;
        int cy = Gdx.graphics.getHeight() / 2 + 30;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Listener
        shapes.setColor(0.3f, 0.3f, 0.8f, 1f);
        shapes.circle(cx, cy, 8);
        if (playing) {
            // Source position mapped to screen
            float sx = cx + sourceX * 8;
            shapes.setColor(1f, 0.3f, 0.3f, 1f);
            shapes.circle(sx, cy, 6);
        }
        if (stereoPanActive) {
            // Show pan position on a bar
            float barW = 300;
            float barX = cx - barW / 2;
            float barY = cy - 30;
            shapes.setColor(0.3f, 0.3f, 0.3f, 1);
            shapes.rect(barX, barY, barW, 8);
            shapes.setColor(1, 0.8f, 0.2f, 1);
            float indicatorX = barX + (currentPan + 1) / 2 * barW;
            shapes.circle(indicatorX, barY + 4, 6);
        }
        shapes.end();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Distance Model & Panning Compare", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 60);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, modelInfo, 20, Gdx.graphics.getHeight() - 80);
        if (playing) {
            font.setColor(0.3f, 0.3f, 0.8f, 1);
            font.draw(batch, "Listener", cx + 12, cy + 5);
            font.setColor(1, 0.3f, 0.3f, 1);
            font.draw(batch, "Source", cx + 12, cy - 15);
        }
        if (stereoPanActive) {
            font.setColor(0.7f, 0.7f, 0.7f, 1);
            font.draw(batch, "L", cx - 158, cy - 22);
            font.draw(batch, "R", cx + 150, cy - 22);
        }
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "Inverse + HRTF", "Linear + HRTF", "Exponential + HRTF",
            "Inverse + Equalpower", "Linear + Equalpower", "Exp + Equalpower",
            "Stereo Pan: Left", "Stereo Pan: Center", "Stereo Pan: Right",
            "Pan -0.2", "Pan +0.2", "Stop All"
        );

        try {
            switch (btn) {
                case 0: startWithModel(DistanceModel.INVERSE, PanningModel.HRTF); break;
                case 1: startWithModel(DistanceModel.LINEAR, PanningModel.HRTF); break;
                case 2: startWithModel(DistanceModel.EXPONENTIAL, PanningModel.HRTF); break;
                case 3: startWithModel(DistanceModel.INVERSE, PanningModel.EQUALPOWER); break;
                case 4: startWithModel(DistanceModel.LINEAR, PanningModel.EQUALPOWER); break;
                case 5: startWithModel(DistanceModel.EXPONENTIAL, PanningModel.EQUALPOWER); break;
                case 6: startStereoPan(-1.0f); break;
                case 7: startStereoPan(0.0f); break;
                case 8: startStereoPan(1.0f); break;
                case 9: adjustPan(-0.2f); break;
                case 10: adjustPan(0.2f); break;
                case 11:
                    stopPanner();
                    stopStereoPan();
                    status = "Stopped";
                    modelInfo = "";
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("DistModel", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopPanner();
        stopStereoPan();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
