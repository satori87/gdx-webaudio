package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioWorkletNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioWorkletNode extends JSAudioNode {
    @JSProperty
    JSAudioParamMap getParameters();
}
