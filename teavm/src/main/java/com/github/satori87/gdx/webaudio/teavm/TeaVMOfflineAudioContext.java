package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.OfflineAudioContext;
import com.github.satori87.gdx.webaudio.teavm.jso.JSOfflineAudioContext;
import org.teavm.jso.JSBody;

/**
 * TeaVM/browser implementation of {@link OfflineAudioContext}.
 *
 * <p>Wraps a {@link JSOfflineAudioContext} to perform offline (non-realtime) audio rendering
 * in the browser. Extends {@link TeaVMWebAudioContext} to inherit standard context functionality.</p>
 */
public class TeaVMOfflineAudioContext extends TeaVMWebAudioContext implements OfflineAudioContext {
    private final JSOfflineAudioContext jsOffline;

    public TeaVMOfflineAudioContext(int channels, int length, float sampleRate) {
        this(createOfflineCtx(channels, length, sampleRate));
    }

    private TeaVMOfflineAudioContext(JSOfflineAudioContext jsOffline) {
        super(jsOffline);
        this.jsOffline = jsOffline;
    }

    @JSBody(params = {"channels", "length", "sampleRate"},
            script = "return new OfflineAudioContext(channels, length, sampleRate);")
    private static native JSOfflineAudioContext createOfflineCtx(int channels, int length, float sampleRate);

    @Override public int getLength() { return jsOffline.getLength(); }
    @Override public void startRendering(AudioBuffer.DecodeCallback onComplete) {
        jsOffline.startRendering(buffer -> onComplete.onDecoded(new TeaVMAudioBuffer(buffer)));
    }
}
