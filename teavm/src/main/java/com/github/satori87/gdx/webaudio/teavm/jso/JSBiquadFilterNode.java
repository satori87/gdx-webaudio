package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code BiquadFilterNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSBiquadFilterNode extends JSAudioNode {
    @JSBody(script = "return this.frequency;")
    JSAudioParam getFrequency();

    @JSBody(script = "return this.detune;")
    JSAudioParam getDetune();

    @JSBody(script = "return this.Q;")
    JSAudioParam getQ();

    @JSBody(script = "return this.gain;")
    JSAudioParam getGain();

    @JSProperty String getType();
    @JSProperty void setType(String type);

    @JSBody(params = {"freqHz", "magResponse", "phaseResponse"},
            script = "this.getFrequencyResponse(freqHz, magResponse, phaseResponse);")
    void getFrequencyResponse(JSFloat32Array freqHz, JSFloat32Array magResponse,
                               JSFloat32Array phaseResponse);
}
