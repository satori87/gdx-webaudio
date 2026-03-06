package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code WaveShaperNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSWaveShaperNode extends JSAudioNode {
    @JSBody(script = "return this.curve;")
    JSFloat32Array getCurve();

    @JSBody(params = {"curve"}, script = "this.curve = curve;")
    void setCurve(JSFloat32Array curve);

    @JSProperty String getOversample();
    @JSProperty void setOversample(String oversample);
}
