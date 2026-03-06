package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSProperty;
import org.teavm.jso.JSObject;

/**
 * TeaVM JSO binding for the JavaScript {@code MediaStreamAudioDestinationNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSMediaStreamAudioDestinationNode extends JSAudioNode {
    @JSProperty
    JSObject getStream();
}
