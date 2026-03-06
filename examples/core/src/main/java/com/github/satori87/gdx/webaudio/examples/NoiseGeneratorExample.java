package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.NoiseNode;
import com.github.satori87.gdx.webaudio.types.NoiseType;

/**
 * Demonstrates the NoiseNode factory method for generating white, pink, and brownian noise.
 * Unlike the WaveformNoiseExample which manually creates noise buffers, this uses
 * the built-in createNoise() for one-line noise generation.
 */
public class NoiseGeneratorExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private NoiseNode currentNoise;
    private GainNode gainNode;
    private String status = "Ready — choose a noise type";
    private float volume = 0.3f;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
    }

    private void playNoise(NoiseType type) {
        stopNoise();
        gainNode = ctx.createGain();
        gainNode.getGain().setValue(volume);
        gainNode.connect(ctx.getDestination());

        currentNoise = ctx.createNoise(type);
        currentNoise.connect(gainNode);
        currentNoise.start();
        status = "Playing: " + type.toJsValue().toUpperCase() + " noise (vol " + (int)(volume * 100) + "%)";
    }

    private void stopNoise() {
        try {
            if (currentNoise != null) { currentNoise.stop(); currentNoise = null; }
        } catch (Exception ignored) {}
        if (gainNode != null) { gainNode.disconnect(); gainNode = null; }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Noise Generator (NoiseNode)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, "Volume: " + (int)(volume * 100) + "%", 20, Gdx.graphics.getHeight() - 85);
        font.draw(batch, "White = flat spectrum, Pink = -3dB/oct, Brownian = -6dB/oct",
                20, Gdx.graphics.getHeight() - 105);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
                "White Noise", "Pink Noise", "Brownian Noise",
                "Volume -", "Volume +", "Stop");

        switch (btn) {
            case 0: playNoise(NoiseType.WHITE); break;
            case 1: playNoise(NoiseType.PINK); break;
            case 2: playNoise(NoiseType.BROWNIAN); break;
            case 3:
                volume = Math.max(0, volume - 0.1f);
                if (gainNode != null) gainNode.getGain().setValue(volume);
                status = "Volume: " + (int)(volume * 100) + "%";
                break;
            case 4:
                volume = Math.min(1.0f, volume + 0.1f);
                if (gainNode != null) gainNode.getGain().setValue(volume);
                status = "Volume: " + (int)(volume * 100) + "%";
                break;
            case 5:
                stopNoise();
                status = "Stopped";
                break;
        }
    }

    @Override
    public void dispose() {
        stopNoise();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
