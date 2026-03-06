package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioBuffer}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioBuffer extends JSObject {
    @JSProperty
    float getDuration();

    @JSProperty
    int getLength();

    @JSProperty
    int getNumberOfChannels();

    @JSProperty
    float getSampleRate();

    @JSMethod
    JSFloat32Array getChannelData(int channel);

    @JSBody(params = {"dest", "channel"}, script = "this.copyFromChannel(dest, channel);")
    void copyFromChannel(JSFloat32Array dest, int channel);

    @JSBody(params = {"dest", "channel", "start"},
            script = "this.copyFromChannel(dest, channel, start);")
    void copyFromChannel(JSFloat32Array dest, int channel, int start);

    @JSBody(params = {"src", "channel"}, script = "this.copyToChannel(src, channel);")
    void copyToChannel(JSFloat32Array src, int channel);

    @JSBody(params = {"src", "channel", "start"},
            script = "this.copyToChannel(src, channel, start);")
    void copyToChannel(JSFloat32Array src, int channel, int start);
}
