package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;

/**
 * TeaVM/browser implementation of {@link AudioParam}.
 *
 * <p>Wraps a {@link JSAudioParam} to provide automatable audio parameter control via the
 * browser's Web Audio API. All scheduling methods return {@code this} for chaining.</p>
 */
public class TeaVMAudioParam implements AudioParam {
    final JSAudioParam jsParam;

    public TeaVMAudioParam(JSAudioParam jsParam) {
        this.jsParam = jsParam;
    }

    @Override public float getValue() { return jsParam.getValue(); }
    @Override public void setValue(float v) { jsParam.setValue(v); }
    @Override public float getDefaultValue() { return jsParam.getDefaultValue(); }
    @Override public float getMinValue() { return jsParam.getMinValue(); }
    @Override public float getMaxValue() { return jsParam.getMaxValue(); }

    @Override public AudioParam setValueAtTime(float value, double startTime) {
        jsParam.setValueAtTime(value, startTime); return this;
    }
    @Override public AudioParam linearRampToValueAtTime(float value, double endTime) {
        jsParam.linearRampToValueAtTime(value, endTime); return this;
    }
    @Override public AudioParam exponentialRampToValueAtTime(float value, double endTime) {
        jsParam.exponentialRampToValueAtTime(value, endTime); return this;
    }
    @Override public AudioParam setTargetAtTime(float target, double startTime, double timeConstant) {
        jsParam.setTargetAtTime(target, startTime, timeConstant); return this;
    }
    @Override public AudioParam setValueCurveAtTime(float[] values, double startTime, double duration) {
        jsParam.setValueCurveAtTime(JSFloat32Array.fromArray(values), startTime, duration); return this;
    }
    @Override public AudioParam cancelScheduledValues(double startTime) {
        jsParam.cancelScheduledValues(startTime); return this;
    }
    @Override public AudioParam cancelAndHoldAtTime(double cancelTime) {
        jsParam.cancelAndHoldAtTime(cancelTime); return this;
    }
}
