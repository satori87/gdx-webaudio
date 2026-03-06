package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.IIRFilterNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;
import com.github.satori87.gdx.webaudio.teavm.jso.JSIIRFilterNode;

/**
 * TeaVM/browser implementation of {@link IIRFilterNode}.
 *
 * <p>Wraps a {@link JSIIRFilterNode} to apply a general infinite impulse response filter
 * via the browser's Web Audio API. The filter coefficients are specified at creation time.</p>
 */
public class TeaVMIIRFilterNode extends TeaVMAudioNode implements IIRFilterNode {
    private final JSIIRFilterNode jsFilter;
    public TeaVMIIRFilterNode(JSIIRFilterNode jsFilter, WebAudioContext context) {
        super(jsFilter, context); this.jsFilter = jsFilter;
    }
    @Override public void getFrequencyResponse(float[] freqHz, float[] magResp, float[] phaseResp) {
        JSFloat32Array jsFreq = JSFloat32Array.fromArray(freqHz);
        JSFloat32Array jsMag = new JSFloat32Array(magResp.length);
        JSFloat32Array jsPhase = new JSFloat32Array(phaseResp.length);
        jsFilter.getFrequencyResponse(jsFreq, jsMag, jsPhase);
        for (int i = 0; i < magResp.length; i++) magResp[i] = jsMag.get(i);
        for (int i = 0; i < phaseResp.length; i++) phaseResp[i] = jsPhase.get(i);
    }
}
