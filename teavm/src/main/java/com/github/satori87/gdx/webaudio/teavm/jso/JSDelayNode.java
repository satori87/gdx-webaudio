package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;

/**
 * TeaVM JSO binding for the JavaScript {@code DelayNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSDelayNode extends JSAudioNode {
    @JSBody(script = "return this.delayTime;")
    JSAudioParam getDelayTime();
}
