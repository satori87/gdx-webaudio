package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;

/**
 * TeaVM JSO binding for the JavaScript {@code StereoPannerNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSStereoPannerNode extends JSAudioNode {
    @JSBody(script = "return this.pan;")
    JSAudioParam getPan();
}
