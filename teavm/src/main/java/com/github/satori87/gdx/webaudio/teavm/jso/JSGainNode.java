package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;

/**
 * TeaVM JSO binding for the JavaScript {@code GainNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSGainNode extends JSAudioNode {
    @JSBody(script = "return this.gain;")
    JSAudioParam getGain();
}
