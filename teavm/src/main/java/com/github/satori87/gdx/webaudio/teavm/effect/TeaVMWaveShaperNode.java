package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.WaveShaperNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;
import com.github.satori87.gdx.webaudio.teavm.jso.JSWaveShaperNode;
import com.github.satori87.gdx.webaudio.types.OverSampleType;

/**
 * TeaVM/browser implementation of {@link WaveShaperNode}.
 *
 * <p>Wraps a {@link JSWaveShaperNode} to apply nonlinear waveshaping distortion
 * via the browser's Web Audio API. The distortion curve and oversampling rate are
 * configurable.</p>
 */
public class TeaVMWaveShaperNode extends TeaVMAudioNode implements WaveShaperNode {
    private final JSWaveShaperNode jsShaper;
    public TeaVMWaveShaperNode(JSWaveShaperNode jsShaper, WebAudioContext context) {
        super(jsShaper, context); this.jsShaper = jsShaper;
    }
    @Override public float[] getCurve() {
        JSFloat32Array c = jsShaper.getCurve();
        return c != null ? JSFloat32Array.toArray(c) : null;
    }
    @Override public void setCurve(float[] curve) {
        jsShaper.setCurve(JSFloat32Array.fromArray(curve));
    }
    @Override public OverSampleType getOversample() { return OverSampleType.fromJsValue(jsShaper.getOversample()); }
    @Override public void setOversample(OverSampleType oversample) { jsShaper.setOversample(oversample.toJsValue()); }
}
