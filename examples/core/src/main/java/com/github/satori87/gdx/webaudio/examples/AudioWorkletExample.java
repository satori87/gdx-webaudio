package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode;

public class AudioWorkletExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private String status = "Ready";
    private AudioWorkletNode workletNode;
    private GainNode volume;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
    }

    private void loadAndStart() {
        status = "Loading worklet module...";
        ctx.addWorkletModule("assets/white-noise-processor.js", () -> {
            status = "Module loaded! Creating node...";
            workletNode = ctx.createWorkletNode("white-noise-processor");
            volume = ctx.createGain();
            volume.getGain().setValue(0.1f);
            workletNode.connect(volume);
            volume.connect(ctx.getDestination());
            status = "White noise playing via AudioWorklet (10% volume)";
        }, () -> {
            status = "Failed to load worklet module.";
        });
    }

    private void stop() {
        if (workletNode != null) {
            workletNode.disconnect();
            workletNode = null;
            status = "Stopped";
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "AudioWorklet Example (Custom DSP)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Status: " + status, 20, Gdx.graphics.getHeight() - 70);
        font.setColor(0.7f, 0.7f, 0.7f, 1);
        font.draw(batch, "Loads white-noise-processor.js from assets/", 20,
                Gdx.graphics.getHeight() - 100);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Start Noise", "Stop");
        if (clicked == 0) loadAndStart();
        else if (clicked == 1) stop();
    }

    @Override
    public void dispose() {
        stop();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
