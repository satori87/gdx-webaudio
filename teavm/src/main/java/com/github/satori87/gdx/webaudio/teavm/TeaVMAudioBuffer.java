package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioBuffer;
import com.github.satori87.gdx.webaudio.teavm.jso.JSFloat32Array;

/**
 * TeaVM/browser implementation of {@link AudioBuffer}.
 *
 * <p>Wraps a {@link JSAudioBuffer} to provide access to in-memory audio data stored by the
 * browser. Handles conversion between Java {@code float[]} arrays and JavaScript typed arrays
 * for channel data operations.</p>
 */
public class TeaVMAudioBuffer implements AudioBuffer {
    public final JSAudioBuffer jsBuffer;

    public TeaVMAudioBuffer(JSAudioBuffer jsBuffer) {
        this.jsBuffer = jsBuffer;
    }

    @Override public float getDuration() { return jsBuffer.getDuration(); }
    @Override public int getLength() { return jsBuffer.getLength(); }
    @Override public int getNumberOfChannels() { return jsBuffer.getNumberOfChannels(); }
    @Override public float getSampleRate() { return jsBuffer.getSampleRate(); }

    @Override public float[] getChannelData(int channel) {
        return JSFloat32Array.toArray(jsBuffer.getChannelData(channel));
    }
    @Override public void copyFromChannel(float[] destination, int channelNumber) {
        JSFloat32Array dest = new JSFloat32Array(destination.length);
        jsBuffer.copyFromChannel(dest, channelNumber);
        for (int i = 0; i < destination.length; i++) destination[i] = dest.get(i);
    }
    @Override public void copyFromChannel(float[] destination, int channelNumber, int startInChannel) {
        JSFloat32Array dest = new JSFloat32Array(destination.length);
        jsBuffer.copyFromChannel(dest, channelNumber, startInChannel);
        for (int i = 0; i < destination.length; i++) destination[i] = dest.get(i);
    }
    @Override public void copyToChannel(float[] source, int channelNumber) {
        jsBuffer.copyToChannel(JSFloat32Array.fromArray(source), channelNumber);
    }
    @Override public void copyToChannel(float[] source, int channelNumber, int startInChannel) {
        jsBuffer.copyToChannel(JSFloat32Array.fromArray(source), channelNumber, startInChannel);
    }
}
