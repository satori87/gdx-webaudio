package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code PannerNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSPannerNode extends JSAudioNode {
    @JSBody(script = "return this.positionX;")
    JSAudioParam getPositionX();

    @JSBody(script = "return this.positionY;")
    JSAudioParam getPositionY();

    @JSBody(script = "return this.positionZ;")
    JSAudioParam getPositionZ();

    @JSBody(script = "return this.orientationX;")
    JSAudioParam getOrientationX();

    @JSBody(script = "return this.orientationY;")
    JSAudioParam getOrientationY();

    @JSBody(script = "return this.orientationZ;")
    JSAudioParam getOrientationZ();

    @JSProperty String getDistanceModel();
    @JSProperty void setDistanceModel(String model);
    @JSProperty String getPanningModel();
    @JSProperty void setPanningModel(String model);
    @JSProperty float getRefDistance();
    @JSProperty void setRefDistance(float distance);
    @JSProperty float getMaxDistance();
    @JSProperty void setMaxDistance(float distance);
    @JSProperty float getRolloffFactor();
    @JSProperty void setRolloffFactor(float factor);
    @JSProperty float getConeInnerAngle();
    @JSProperty void setConeInnerAngle(float angle);
    @JSProperty float getConeOuterAngle();
    @JSProperty void setConeOuterAngle(float angle);
    @JSProperty float getConeOuterGain();
    @JSProperty void setConeOuterGain(float gain);
}
