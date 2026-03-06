package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.OfflineAudioContext;
import com.github.satori87.gdx.webaudio.WebAudio;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

public class OfflineRenderExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private AudioBuffer renderedBuffer;
    private String status = "Ready";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
    }

    private void renderOffline() {
        status = "Rendering...";
        float sampleRate = 44100;
        int duration = 2;
        int lengthInSamples = (int) (sampleRate * duration);

        OfflineAudioContext offline = WebAudio.createOfflineContext(2, lengthInSamples, sampleRate);

        float[] freqs = {261.63f, 329.63f, 392.00f};
        for (float freq : freqs) {
            OscillatorNode osc = offline.createOscillator();
            osc.setType(OscillatorType.SINE);
            osc.getFrequency().setValue(freq);

            GainNode fade = offline.createGain();
            fade.getGain().setValueAtTime(0.2f, 0);
            fade.getGain().linearRampToValueAtTime(0, duration);

            osc.connect(fade);
            fade.connect(offline.getDestination());
            osc.start();
            osc.stop(duration);
        }

        offline.startRendering(buffer -> {
            renderedBuffer = buffer;
            status = "Rendered " + buffer.getLength() + " samples (" +
                    buffer.getNumberOfChannels() + "ch, " +
                    (int) buffer.getSampleRate() + "Hz)";
        });
    }

    private void playRendered() {
        if (renderedBuffer == null) {
            status = "Nothing rendered yet! Click Render first.";
            return;
        }
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(renderedBuffer);
        src.connect(ctx.getDestination());
        src.start();
        status = "Playing rendered buffer...";
        src.setOnEnded(() -> status = "Playback complete.");
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Offline Rendering (OfflineAudioContext)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Status: " + status, 20, Gdx.graphics.getHeight() - 70);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Render Chord", "Play Rendered");
        if (clicked == 0) renderOffline();
        else if (clicked == 1) playRendered();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
