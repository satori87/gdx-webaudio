package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioParam}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioParam extends JSObject {
    @JSBody(script = "return this.value;")
    float getValue();

    @JSBody(params = {"v"}, script = "this.value = v;")
    void setValue(float v);

    @JSBody(script = "return this.defaultValue;")
    float getDefaultValue();

    @JSBody(script = "return this.minValue;")
    float getMinValue();

    @JSBody(script = "return this.maxValue;")
    float getMaxValue();

    @JSMethod
    JSAudioParam setValueAtTime(float value, double startTime);

    @JSMethod
    JSAudioParam linearRampToValueAtTime(float value, double endTime);

    @JSMethod
    JSAudioParam exponentialRampToValueAtTime(float value, double endTime);

    @JSMethod
    JSAudioParam setTargetAtTime(float target, double startTime, double timeConstant);

    @JSBody(params = {"values", "startTime", "duration"},
            script = "return this.setValueCurveAtTime(values, startTime, duration);")
    JSAudioParam setValueCurveAtTime(JSFloat32Array values, double startTime, double duration);

    @JSMethod
    JSAudioParam cancelScheduledValues(double startTime);

    @JSMethod
    JSAudioParam cancelAndHoldAtTime(double cancelTime);
}
