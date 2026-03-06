package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.PhaserNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMCompositeAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.*;

/**
 * TeaVM/browser implementation of {@link PhaserNode}.
 *
 * <p>Built from cascaded allpass BiquadFilterNodes with LFO-modulated frequencies,
 * mixed with the dry signal. Supports dynamic stage count changes.</p>
 */
public class TeaVMPhaserNode extends TeaVMCompositeAudioNode implements PhaserNode {
    private final JSAudioContext jsCtx;
    private final JSGainNode inputGain;
    private final JSGainNode outputGain;
    private final JSOscillatorNode lfo;
    private final JSGainNode depthGain;
    private final JSGainNode wetGain;
    private final JSGainNode dryGain;
    private final JSGainNode feedbackGain;
    private final JSGainNode wetInput;
    private JSBiquadFilterNode[] allpassFilters;
    private float rate = 0.5f;
    private float depth = 0.5f;
    private float wet = 0.5f;
    private float dry = 0.5f;
    private int stages = 4;
    private float feedback = 0.0f;
    private float freqMin = 200f;
    private float freqMax = 4000f;

    public TeaVMPhaserNode(JSAudioContext jsCtx, WebAudioContext context) {
        this(jsCtx, context, jsCtx.createGain(), jsCtx.createGain());
    }

    private TeaVMPhaserNode(JSAudioContext jsCtx, WebAudioContext context,
                            JSGainNode inputGain, JSGainNode outputGain) {
        super(inputGain, outputGain, context);
        this.jsCtx = jsCtx;
        this.inputGain = inputGain;
        this.outputGain = outputGain;

        this.dryGain = jsCtx.createGain();
        this.wetGain = jsCtx.createGain();
        this.feedbackGain = jsCtx.createGain();
        this.wetInput = jsCtx.createGain();
        this.lfo = jsCtx.createOscillator();
        this.depthGain = jsCtx.createGain();

        dryGain.getGain().setValue(dry);
        wetGain.getGain().setValue(wet);
        feedbackGain.getGain().setValue(feedback);
        lfo.getFrequency().setValue(rate);

        // Dry path
        inputGain.connect(dryGain);
        dryGain.connect(outputGain);

        // Wet input
        inputGain.connect(wetInput);

        lfo.start();
        rebuildAllpassChain();
    }

    private void rebuildAllpassChain() {
        // Disconnect old chain
        if (allpassFilters != null) {
            wetInput.disconnect();
            inputGain.connect(wetInput); // reconnect input to wetInput
            for (JSBiquadFilterNode f : allpassFilters) {
                f.disconnect();
            }
            feedbackGain.disconnect();
            depthGain.disconnect();
        }

        float center = (freqMin + freqMax) / 2f;
        float sweep = (freqMax - freqMin) / 2f;
        depthGain.getGain().setValue(depth * sweep);

        // Create allpass filters
        allpassFilters = new JSBiquadFilterNode[stages];
        for (int i = 0; i < stages; i++) {
            allpassFilters[i] = jsCtx.createBiquadFilter();
            allpassFilters[i].setType("allpass");
            allpassFilters[i].getFrequency().setValue(center);
            allpassFilters[i].getQ().setValue(0.707f);
            depthGain.connectToParam(allpassFilters[i].getFrequency());
        }

        // Chain: wetInput -> allpass[0] -> ... -> allpass[N-1] -> wetGain -> output
        wetInput.connect(allpassFilters[0]);
        for (int i = 0; i < stages - 1; i++) {
            allpassFilters[i].connect(allpassFilters[i + 1]);
        }
        allpassFilters[stages - 1].connect(wetGain);
        wetGain.connect(outputGain);

        // Feedback
        if (feedback != 0) {
            allpassFilters[stages - 1].connect(feedbackGain);
            feedbackGain.connect(wetInput);
        }

        // LFO -> depthGain (already connected to allpass frequencies above)
        lfo.connect(depthGain);
    }

    @Override public float getRate() { return rate; }
    @Override public void setRate(float rate) { this.rate = rate; lfo.getFrequency().setValue(rate); }
    @Override public float getDepth() { return depth; }
    @Override public void setDepth(float depth) {
        this.depth = depth;
        float sweep = (freqMax - freqMin) / 2f;
        depthGain.getGain().setValue(depth * sweep);
    }
    @Override public float getWet() { return wet; }
    @Override public void setWet(float wet) { this.wet = wet; wetGain.getGain().setValue(wet); }
    @Override public float getDry() { return dry; }
    @Override public void setDry(float dry) { this.dry = dry; dryGain.getGain().setValue(dry); }
    @Override public int getStages() { return stages; }
    @Override public void setStages(int stages) {
        if (stages < 1) stages = 1;
        if (stages == this.stages) return;
        this.stages = stages;
        rebuildAllpassChain();
    }
    @Override public float getFeedback() { return feedback; }
    @Override public void setFeedback(float feedback) {
        this.feedback = feedback;
        feedbackGain.getGain().setValue(feedback);
        if (feedback != 0 && allpassFilters != null) {
            allpassFilters[stages - 1].connect(feedbackGain);
            feedbackGain.connect(wetInput);
        }
    }
    @Override public float getFrequencyRangeMin() { return freqMin; }
    @Override public void setFrequencyRangeMin(float min) {
        this.freqMin = min;
        rebuildAllpassChain();
    }
    @Override public float getFrequencyRangeMax() { return freqMax; }
    @Override public void setFrequencyRangeMax(float max) {
        this.freqMax = max;
        rebuildAllpassChain();
    }
}
