package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

public class RhythmGameExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private AudioBuffer kickBuffer, snareBuffer, hihatBuffer;
    private float bpm = 120;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
        synthesizeDrumSounds();
    }

    private void synthesizeDrumSounds() {
        float sr = ctx.getSampleRate();

        int kickLen = (int) (sr * 0.2f);
        kickBuffer = ctx.createBuffer(1, kickLen, sr);
        float[] kickData = kickBuffer.getChannelData(0);
        for (int i = 0; i < kickLen; i++) {
            float t = (float) i / sr;
            float freq = 150 * (float) Math.exp(-t * 20);
            float env = (float) Math.exp(-t * 8);
            kickData[i] = (float) Math.sin(2 * Math.PI * freq * t) * env * 0.8f;
        }
        kickBuffer.copyToChannel(kickData, 0);

        int snareLen = (int) (sr * 0.15f);
        snareBuffer = ctx.createBuffer(1, snareLen, sr);
        float[] snareData = snareBuffer.getChannelData(0);
        for (int i = 0; i < snareLen; i++) {
            float t = (float) i / sr;
            float env = (float) Math.exp(-t * 15);
            float noise = (float) (Math.random() * 2 - 1) * 0.5f;
            float tone = (float) Math.sin(2 * Math.PI * 200 * t) * 0.3f;
            snareData[i] = (noise + tone) * env;
        }
        snareBuffer.copyToChannel(snareData, 0);

        int hhLen = (int) (sr * 0.05f);
        hihatBuffer = ctx.createBuffer(1, hhLen, sr);
        float[] hhData = hihatBuffer.getChannelData(0);
        for (int i = 0; i < hhLen; i++) {
            float t = (float) i / sr;
            float env = (float) Math.exp(-t * 40);
            hhData[i] = (float) (Math.random() * 2 - 1) * env * 0.3f;
        }
        hihatBuffer.copyToChannel(hhData, 0);
    }

    private void playBufferAt(AudioBuffer buffer, double time, float volume) {
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(buffer);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(volume);
        src.connect(gain);
        gain.connect(ctx.getDestination());
        src.start(time);
    }

    private void playDrumPattern() {
        double beatDuration = 60.0 / bpm;
        double startTime = ctx.getCurrentTime() + 0.05;

        for (int bar = 0; bar < 2; bar++) {
            double barStart = startTime + bar * 4 * beatDuration;
            playBufferAt(kickBuffer, barStart, 1.0f);
            playBufferAt(kickBuffer, barStart + 2 * beatDuration, 1.0f);
            playBufferAt(snareBuffer, barStart + beatDuration, 0.8f);
            playBufferAt(snareBuffer, barStart + 3 * beatDuration, 0.8f);
            for (int i = 0; i < 8; i++) {
                float accent = i % 2 == 0 ? 0.7f : 0.35f;
                playBufferAt(hihatBuffer, barStart + i * beatDuration * 0.5, accent);
            }
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Rhythm Game (Precise Scheduling)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "BPM: " + (int) bpm, 20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, "Drums are synthesized in-memory (no audio files needed)", 20,
                Gdx.graphics.getHeight() - 100);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Play Pattern", "BPM +10", "BPM -10");
        if (clicked == 0) playDrumPattern();
        else if (clicked == 1) bpm = Math.min(240, bpm + 10);
        else if (clicked == 2) bpm = Math.max(60, bpm - 10);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
