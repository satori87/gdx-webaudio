package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;

/**
 * TeaVM JSO binding for the JavaScript {@code IIRFilterNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSIIRFilterNode extends JSAudioNode {
    @JSBody(params = {"freqHz", "magResponse", "phaseResponse"},
            script = "this.getFrequencyResponse(freqHz, magResponse, phaseResponse);")
    void getFrequencyResponse(JSFloat32Array freqHz, JSFloat32Array magResponse,
                               JSFloat32Array phaseResponse);
}
