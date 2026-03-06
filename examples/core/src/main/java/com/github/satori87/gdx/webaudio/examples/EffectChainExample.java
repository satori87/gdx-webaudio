package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;
import com.github.satori87.gdx.webaudio.types.OscillatorType;
import com.github.satori87.gdx.webaudio.types.OverSampleType;

/**
 * Demonstrates a full effect chain using loaded audio files:
 * Filter + Distortion + Delay + Compression + Auto-Pan.
 * Also supports dry playback and oscillator source for comparison.
 */
public class EffectChainExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private AudioBuffer fanfareBuffer, chiptuneBuf;
    private OscillatorNode osc;
    private boolean oscPlaying;
    private String status = "Loading audio...";
    private int loaded = 0;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        byte[] data1 = Gdx.files.internal("fanfare.wav").readBytes();
        ctx.decodeAudioData(data1, buf -> {
            fanfareBuffer = buf;
            loaded++;
            if (loaded >= 2) status = "Ready! Choose a sound source.";
        }, () -> Gdx.app.error("EffectChain", "Failed to decode fanfare.wav"));

        byte[] data2 = Gdx.files.internal("music-chiptune.wav").readBytes();
        ctx.decodeAudioData(data2, buf -> {
            chiptuneBuf = buf;
            loaded++;
            if (loaded >= 2) status = "Ready! Choose a sound source.";
        }, () -> Gdx.app.error("EffectChain", "Failed to decode music-chiptune.wav"));
    }

    private void buildEffectChain(com.github.satori87.gdx.webaudio.AudioNode source) {
        BiquadFilterNode eq = ctx.createBiquadFilter();
        eq.setType(BiquadFilterType.LOWPASS);
        eq.getFrequency().setValue(1500);
        eq.getQ().setValue(5);

        WaveShaperNode shaper = ctx.createWaveShaper();
        int samples = 44100;
        float[] curve = new float[samples];
        float amount = 50;
        for (int i = 0; i < samples; i++) {
            float x = i * 2f / samples - 1;
            curve[i] = (float) ((3 + amount) * x * 20 * (Math.PI / 180)
                    / (Math.PI + amount * Math.abs(x)));
        }
        shaper.setCurve(curve);
        shaper.setOversample(OverSampleType.FOUR_X);

        DelayNode delay = ctx.createDelay(1.0f);
        delay.getDelayTime().setValue(0.3f);
        GainNode feedback = ctx.createGain();
        feedback.getGain().setValue(0.35f);
        delay.connect(feedback);
        feedback.connect(delay);

        DynamicsCompressorNode compressor = ctx.createDynamicsCompressor();
        compressor.getThreshold().setValue(-20);
        compressor.getKnee().setValue(30);
        compressor.getRatio().setValue(8);
        compressor.getAttack().setValue(0.003f);
        compressor.getRelease().setValue(0.25f);

        StereoPannerNode panner = ctx.createStereoPanner();
        OscillatorNode panLfo = ctx.createOscillator();
        panLfo.setType(OscillatorType.SINE);
        panLfo.getFrequency().setValue(0.5f);
        panLfo.connectParam(panner.getPan());
        panLfo.start();

        GainNode master = ctx.createGain();
        master.getGain().setValue(0.25f);

        source.connect(eq);
        eq.connect(shaper);
        shaper.connect(delay);
        shaper.connect(compressor);
        delay.connect(compressor);
        compressor.connect(panner);
        panner.connect(master);
        master.connect(ctx.getDestination());
    }

    private void playFileWithEffects(AudioBuffer buffer, String name) {
        stopOsc();
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(buffer);
        buildEffectChain(src);
        src.start();
        status = "Playing " + name + " through effect chain";
    }

    private void playFileDry(AudioBuffer buffer, String name) {
        stopOsc();
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(buffer);
        src.connect(ctx.getDestination());
        src.start();
        status = "Playing " + name + " (dry, no effects)";
    }

    private void playOscWithEffects() {
        stopOsc();
        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SAWTOOTH);
        osc.getFrequency().setValue(220);
        buildEffectChain(osc);
        osc.start();
        oscPlaying = true;
        status = "Oscillator through effect chain";
    }

    private void stopOsc() {
        if (osc != null && oscPlaying) {
            osc.stop();
            osc = null;
            oscPlaying = false;
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Effect Chain: Filter+Distortion+Delay+Compression+AutoPan", 20,
                Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 70);
        batch.end();

        int clicked = drawButtons(shapes, batch, font,
                "Fanfare + FX", "Fanfare (dry)", "Chiptune + FX",
                "Chiptune (dry)", "Oscillator + FX", "Stop Osc");
        if (clicked == 0 && fanfareBuffer != null) playFileWithEffects(fanfareBuffer, "fanfare");
        else if (clicked == 1 && fanfareBuffer != null) playFileDry(fanfareBuffer, "fanfare");
        else if (clicked == 2 && chiptuneBuf != null) playFileWithEffects(chiptuneBuf, "chiptune");
        else if (clicked == 3 && chiptuneBuf != null) playFileDry(chiptuneBuf, "chiptune");
        else if (clicked == 4) playOscWithEffects();
        else if (clicked == 5) stopOsc();
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
