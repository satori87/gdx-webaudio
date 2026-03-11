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

    @JSBody(params = {"x", "y", "z"}, script =
        "if (this.positionX) { this.positionX.value = x; this.positionY.value = y; this.positionZ.value = z; }" +
        "else if (this.setPosition) { this.setPosition(x, y, z); }")
    void jsSetPosition(float x, float y, float z);

    @JSBody(params = {"fx", "fy", "fz", "ux", "uy", "uz"}, script =
        "if (this.forwardX) { this.forwardX.value = fx; this.forwardY.value = fy; this.forwardZ.value = fz;" +
        " this.upX.value = ux; this.upY.value = uy; this.upZ.value = uz; }" +
        "else if (this.setOrientation) { this.setOrientation(fx, fy, fz, ux, uy, uz); }")
    void jsSetOrientation(float fx, float fy, float fz, float ux, float uy, float uz);
}
