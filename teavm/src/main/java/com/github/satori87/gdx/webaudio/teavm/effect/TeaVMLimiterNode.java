package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.LimiterNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMCompositeAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.*;

/**
 * TeaVM/browser implementation of {@link LimiterNode}.
 *
 * <p>Uses a {@link JSDynamicsCompressorNode} configured with a hard knee and maximum
 * ratio to approximate a brickwall limiter.</p>
 */
public class TeaVMLimiterNode extends TeaVMCompositeAudioNode implements LimiterNode {
    private final JSGainNode inputGainNode;
    private final JSDynamicsCompressorNode compressor;
    private float ceiling = -1.0f;
    private float inputGainDb = 0.0f;
    private float attackMs = 1.0f;
    private float releaseMs = 100.0f;

    public TeaVMLimiterNode(JSAudioContext jsCtx, WebAudioContext context) {
        this(jsCtx, context, jsCtx.createGain(), jsCtx.createGain());
    }

    private TeaVMLimiterNode(JSAudioContext jsCtx, WebAudioContext context,
                             JSGainNode inputGain, JSGainNode outputGain) {
        super(inputGain, outputGain, context);

        this.inputGainNode = jsCtx.createGain();
        this.compressor = jsCtx.createDynamicsCompressor();

        // Configure as limiter
        compressor.getThreshold().setValue(ceiling);
        compressor.getKnee().setValue(0);
        compressor.getRatio().setValue(20); // max ratio in Web Audio
        compressor.getAttack().setValue(attackMs / 1000f);
        compressor.getRelease().setValue(releaseMs / 1000f);

        // Convert dB to linear gain
        inputGainNode.getGain().setValue((float)Math.pow(10, inputGainDb / 20));

        // Wire: input -> inputGainNode -> compressor -> output
        inputGain.connect(inputGainNode);
        inputGainNode.connect(compressor);
        compressor.connect(outputGain);
    }

    @Override public float getCeiling() { return ceiling; }
    @Override public void setCeiling(float ceilingDb) {
        this.ceiling = ceilingDb;
        compressor.getThreshold().setValue(ceilingDb);
    }
    @Override public float getInputGain() { return inputGainDb; }
    @Override public void setInputGain(float gainDb) {
        this.inputGainDb = gainDb;
        inputGainNode.getGain().setValue((float)Math.pow(10, gainDb / 20));
    }
    @Override public float getAttack() { return attackMs; }
    @Override public void setAttack(float attackMs) {
        this.attackMs = attackMs;
        compressor.getAttack().setValue(attackMs / 1000f);
    }
    @Override public float getRelease() { return releaseMs; }
    @Override public void setRelease(float releaseMs) {
        this.releaseMs = releaseMs;
        compressor.getRelease().setValue(releaseMs / 1000f);
    }
}
