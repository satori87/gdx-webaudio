package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AnalyserNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAnalyserNode extends JSAudioNode {
    @JSProperty int getFftSize();
    @JSProperty void setFftSize(int fftSize);
    @JSProperty int getFrequencyBinCount();
    @JSProperty float getMinDecibels();
    @JSProperty void setMinDecibels(float minDecibels);
    @JSProperty float getMaxDecibels();
    @JSProperty void setMaxDecibels(float maxDecibels);
    @JSProperty float getSmoothingTimeConstant();
    @JSProperty void setSmoothingTimeConstant(float smoothing);

    @JSBody(params = {"array"}, script = "this.getByteFrequencyData(array);")
    void getByteFrequencyData(JSUint8Array array);

    @JSBody(params = {"array"}, script = "this.getFloatFrequencyData(array);")
    void getFloatFrequencyData(JSFloat32Array array);

    @JSBody(params = {"array"}, script = "this.getByteTimeDomainData(array);")
    void getByteTimeDomainData(JSUint8Array array);

    @JSBody(params = {"array"}, script = "this.getFloatTimeDomainData(array);")
    void getFloatTimeDomainData(JSFloat32Array array);
}
