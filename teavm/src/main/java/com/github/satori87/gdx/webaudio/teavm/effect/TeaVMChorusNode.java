package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.ChorusNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMCompositeAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.*;

/**
 * TeaVM/browser implementation of {@link ChorusNode}.
 *
 * <p>Built from a delay line modulated by an LFO oscillator, mixed with the dry signal.</p>
 */
public class TeaVMChorusNode extends TeaVMCompositeAudioNode implements ChorusNode {
    private final JSOscillatorNode lfo;
    private final JSGainNode depthGain;
    private final JSGainNode wetGain;
    private final JSGainNode dryGain;
    private final JSDelayNode delay;
    private float rate = 0.5f;
    private float depth = 2.0f;
    private float wet = 0.5f;
    private float dry = 0.5f;
    private float delayMs = 25.0f;

    public TeaVMChorusNode(JSAudioContext jsCtx, WebAudioContext context) {
        this(jsCtx, context, jsCtx.createGain(), jsCtx.createGain());
    }

    private TeaVMChorusNode(JSAudioContext jsCtx, WebAudioContext context,
                            JSGainNode inputGain, JSGainNode outputGain) {
        super(inputGain, outputGain, context);

        this.dryGain = jsCtx.createGain();
        this.wetGain = jsCtx.createGain();
        this.delay = jsCtx.createDelay(0.1f);
        this.lfo = jsCtx.createOscillator();
        this.depthGain = jsCtx.createGain();

        dryGain.getGain().setValue(dry);
        wetGain.getGain().setValue(wet);
        delay.getDelayTime().setValue(delayMs / 1000f);
        lfo.getFrequency().setValue(rate);
        depthGain.getGain().setValue(depth / 1000f);

        // Dry path: input -> dryGain -> output
        inputGain.connect(dryGain);
        dryGain.connect(outputGain);

        // Wet path: input -> delay -> wetGain -> output
        inputGain.connect(delay);
        delay.connect(wetGain);
        wetGain.connect(outputGain);

        // LFO modulation: lfo -> depthGain -> delay.delayTime
        lfo.connect(depthGain);
        depthGain.connectToParam(delay.getDelayTime());

        lfo.start();
    }

    @Override public float getRate() { return rate; }
    @Override public void setRate(float rate) { this.rate = rate; lfo.getFrequency().setValue(rate); }
    @Override public float getDepth() { return depth; }
    @Override public void setDepth(float depth) { this.depth = depth; depthGain.getGain().setValue(depth / 1000f); }
    @Override public float getWet() { return wet; }
    @Override public void setWet(float wet) { this.wet = wet; wetGain.getGain().setValue(wet); }
    @Override public float getDry() { return dry; }
    @Override public void setDry(float dry) { this.dry = dry; dryGain.getGain().setValue(dry); }
    @Override public float getDelay() { return delayMs; }
    @Override public void setDelay(float delay) { this.delayMs = delay; this.delay.getDelayTime().setValue(delay / 1000f); }
}
