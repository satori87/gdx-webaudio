package com.github.satori87.gdx.webaudio.teavm.analysis;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAnalyserNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;
import com.github.satori87.gdx.webaudio.teavm.jso.JSUint8Array;

/**
 * TeaVM/browser implementation of {@link AnalyserNode}.
 *
 * <p>Wraps a {@link JSAnalyserNode} to provide real-time frequency and time-domain audio
 * analysis via the browser's Web Audio API. Handles conversion between JavaScript typed
 * arrays and Java arrays for analysis data retrieval.</p>
 */
public class TeaVMAnalyserNode extends TeaVMAudioNode implements AnalyserNode {
    private final JSAnalyserNode jsAnalyser;

    public TeaVMAnalyserNode(JSAnalyserNode jsAnalyser, WebAudioContext context) {
        super(jsAnalyser, context); this.jsAnalyser = jsAnalyser;
    }

    @Override public int getFftSize() { return jsAnalyser.getFftSize(); }
    @Override public void setFftSize(int fftSize) { jsAnalyser.setFftSize(fftSize); }
    @Override public int getFrequencyBinCount() { return jsAnalyser.getFrequencyBinCount(); }
    @Override public float getMinDecibels() { return jsAnalyser.getMinDecibels(); }
    @Override public void setMinDecibels(float min) { jsAnalyser.setMinDecibels(min); }
    @Override public float getMaxDecibels() { return jsAnalyser.getMaxDecibels(); }
    @Override public void setMaxDecibels(float max) { jsAnalyser.setMaxDecibels(max); }
    @Override public float getSmoothingTimeConstant() { return jsAnalyser.getSmoothingTimeConstant(); }
    @Override public void setSmoothingTimeConstant(float s) { jsAnalyser.setSmoothingTimeConstant(s); }

    @Override public void getByteFrequencyData(byte[] array) {
        JSUint8Array jsArr = new JSUint8Array(array.length);
        jsAnalyser.getByteFrequencyData(jsArr);
        for (int i = 0; i < array.length; i++) array[i] = (byte) jsArr.get(i);
    }
    @Override public void getFloatFrequencyData(float[] array) {
        JSFloat32Array jsArr = new JSFloat32Array(array.length);
        jsAnalyser.getFloatFrequencyData(jsArr);
        for (int i = 0; i < array.length; i++) array[i] = jsArr.get(i);
    }
    @Override public void getByteTimeDomainData(byte[] array) {
        JSUint8Array jsArr = new JSUint8Array(array.length);
        jsAnalyser.getByteTimeDomainData(jsArr);
        for (int i = 0; i < array.length; i++) array[i] = (byte) jsArr.get(i);
    }
    @Override public void getFloatTimeDomainData(float[] array) {
        JSFloat32Array jsArr = new JSFloat32Array(array.length);
        jsAnalyser.getFloatTimeDomainData(jsArr);
        for (int i = 0; i < array.length; i++) array[i] = jsArr.get(i);
    }
}
