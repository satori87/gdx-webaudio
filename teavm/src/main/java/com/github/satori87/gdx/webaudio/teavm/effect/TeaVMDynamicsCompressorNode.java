package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.DynamicsCompressorNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSDynamicsCompressorNode;

/**
 * TeaVM/browser implementation of {@link DynamicsCompressorNode}.
 *
 * <p>Wraps a {@link JSDynamicsCompressorNode} to provide dynamic range compression
 * via the browser's Web Audio API. Exposes threshold, knee, ratio, attack, release,
 * and current gain reduction.</p>
 */
public class TeaVMDynamicsCompressorNode extends TeaVMAudioNode implements DynamicsCompressorNode {
    private final JSDynamicsCompressorNode jsComp;
    public TeaVMDynamicsCompressorNode(JSDynamicsCompressorNode jsComp, WebAudioContext context) {
        super(jsComp, context); this.jsComp = jsComp;
    }
    @Override public AudioParam getThreshold() { return new TeaVMAudioParam(jsComp.getThreshold()); }
    @Override public AudioParam getKnee() { return new TeaVMAudioParam(jsComp.getKnee()); }
    @Override public AudioParam getRatio() { return new TeaVMAudioParam(jsComp.getRatio()); }
    @Override public AudioParam getAttack() { return new TeaVMAudioParam(jsComp.getAttack()); }
    @Override public AudioParam getRelease() { return new TeaVMAudioParam(jsComp.getRelease()); }
    @Override public float getReduction() { return jsComp.getReduction(); }
}
