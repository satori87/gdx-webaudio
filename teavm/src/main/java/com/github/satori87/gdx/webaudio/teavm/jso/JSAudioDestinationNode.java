package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioDestinationNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioDestinationNode extends JSAudioNode {
    @JSProperty
    int getMaxChannelCount();
}
