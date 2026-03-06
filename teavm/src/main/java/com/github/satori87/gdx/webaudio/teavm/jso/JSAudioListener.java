package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioListener}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioListener extends JSObject {
    @JSBody(script = "return this.positionX;")
    JSAudioParam getPositionX();

    @JSBody(script = "return this.positionY;")
    JSAudioParam getPositionY();

    @JSBody(script = "return this.positionZ;")
    JSAudioParam getPositionZ();

    @JSBody(script = "return this.forwardX;")
    JSAudioParam getForwardX();

    @JSBody(script = "return this.forwardY;")
    JSAudioParam getForwardY();

    @JSBody(script = "return this.forwardZ;")
    JSAudioParam getForwardZ();

    @JSBody(script = "return this.upX;")
    JSAudioParam getUpX();

    @JSBody(script = "return this.upY;")
    JSAudioParam getUpY();

    @JSBody(script = "return this.upZ;")
    JSAudioParam getUpZ();
}
