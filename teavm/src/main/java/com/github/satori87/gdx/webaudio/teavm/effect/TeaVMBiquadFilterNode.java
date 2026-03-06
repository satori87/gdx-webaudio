package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.BiquadFilterNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSBiquadFilterNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;

/**
 * TeaVM/browser implementation of {@link BiquadFilterNode}.
 *
 * <p>Wraps a {@link JSBiquadFilterNode} to provide low-order audio filtering (lowpass,
 * highpass, bandpass, etc.) via the browser's Web Audio API. Supports frequency
 * response analysis through {@link #getFrequencyResponse(float[], float[], float[])}.</p>
 */
public class TeaVMBiquadFilterNode extends TeaVMAudioNode implements BiquadFilterNode {
    private final JSBiquadFilterNode jsFilter;
    public TeaVMBiquadFilterNode(JSBiquadFilterNode jsFilter, WebAudioContext context) {
        super(jsFilter, context); this.jsFilter = jsFilter;
    }
    @Override public AudioParam getFrequency() { return new TeaVMAudioParam(jsFilter.getFrequency()); }
    @Override public AudioParam getDetune() { return new TeaVMAudioParam(jsFilter.getDetune()); }
    @Override public AudioParam getQ() { return new TeaVMAudioParam(jsFilter.getQ()); }
    @Override public AudioParam getGain() { return new TeaVMAudioParam(jsFilter.getGain()); }
    @Override public BiquadFilterType getType() { return BiquadFilterType.fromJsValue(jsFilter.getType()); }
    @Override public void setType(BiquadFilterType type) { jsFilter.setType(type.toJsValue()); }
    @Override public void getFrequencyResponse(float[] freqHz, float[] magResp, float[] phaseResp) {
        JSFloat32Array jsFreq = JSFloat32Array.fromArray(freqHz);
        JSFloat32Array jsMag = new JSFloat32Array(magResp.length);
        JSFloat32Array jsPhase = new JSFloat32Array(phaseResp.length);
        jsFilter.getFrequencyResponse(jsFreq, jsMag, jsPhase);
        for (int i = 0; i < magResp.length; i++) magResp[i] = jsMag.get(i);
        for (int i = 0; i < phaseResp.length; i++) phaseResp[i] = jsPhase.get(i);
    }
}
