package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.SoundPool;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates SoundPool for rapid fire-and-forget sound playback.
 * Multiple pools loaded with different sounds allow rapid triggering
 * with overlapping playback — ideal for game SFX.
 */
public class SoundPoolExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private SoundPool laserPool;
    private SoundPool explosionPool;
    private SoundPool coinPool;
    private GainNode masterGain;
    private int loadCount = 0;
    private int playCount = 0;
    private String status = "Loading sounds...";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        masterGain = ctx.createGain();
        masterGain.getGain().setValue(0.6f);
        masterGain.connect(ctx.getDestination());

        loadSound("laser.wav", buf -> { laserPool = new SoundPool(ctx, buf); checkLoaded(); });
        loadSound("explosion.wav", buf -> { explosionPool = new SoundPool(ctx, buf); checkLoaded(); });
        loadSound("coin.wav", buf -> { coinPool = new SoundPool(ctx, buf); checkLoaded(); });
    }

    private void loadSound(String filename, AudioBuffer.DecodeCallback onDone) {
        byte[] data = Gdx.files.internal(filename).readBytes();
        ctx.decodeAudioData(data, onDone, () -> {
            status = "Failed to decode " + filename;
            checkLoaded();
        });
    }

    private void checkLoaded() {
        if (++loadCount >= 3) {
            status = "Ready! Tap buttons rapidly to test overlapping playback.";
        }
    }

    private void fire(SoundPool pool, String name) {
        if (pool == null) return;
        pool.play(masterGain);
        playCount++;
        status = name + " fired! (total: " + playCount + ")";
    }

    private void burstFire(SoundPool pool, String name, int count) {
        if (pool == null) return;
        for (int i = 0; i < count; i++) {
            AudioBufferSourceNode src = pool.obtain(masterGain);
            // Stagger start times slightly for a burst effect
            src.start(ctx.getCurrentTime() + i * 0.05);
        }
        playCount += count;
        status = name + " burst x" + count + "! (total: " + playCount + ")";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "SoundPool — Fire-and-Forget Playback", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, "Total sounds played: " + playCount, 20, Gdx.graphics.getHeight() - 85);
        font.draw(batch, "Pools create fresh nodes on demand (Web Audio nodes are one-shot)",
                20, Gdx.graphics.getHeight() - 105);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
                "Laser", "Explosion", "Coin",
                "Laser Burst x5", "Explosion Burst x3", "Coin Burst x8",
                "Reset Counter");

        switch (btn) {
            case 0: fire(laserPool, "Laser"); break;
            case 1: fire(explosionPool, "Explosion"); break;
            case 2: fire(coinPool, "Coin"); break;
            case 3: burstFire(laserPool, "Laser", 5); break;
            case 4: burstFire(explosionPool, "Explosion", 3); break;
            case 5: burstFire(coinPool, "Coin", 8); break;
            case 6:
                playCount = 0;
                status = "Counter reset";
                break;
        }
    }

    @Override
    public void dispose() {
        if (masterGain != null) masterGain.disconnect();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
