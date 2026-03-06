package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.OfflineAudioContext;
import com.github.satori87.gdx.webaudio.WebAudio;
import com.github.satori87.gdx.webaudio.WebAudioContext;

/**
 * TeaVM/browser implementation of {@link WebAudio.WebAudioPlatform}.
 *
 * <p>Serves as the platform factory that creates audio contexts backed by the browser's
 * Web Audio API. Call {@link #initialize()} once at application startup to register
 * this platform with the core {@link WebAudio} facade.</p>
 */
public class TeaVMWebAudio implements WebAudio.WebAudioPlatform {

    /** Registers this TeaVM platform implementation with the core {@link WebAudio} facade. */
    public static void initialize() {
        WebAudio.setPlatform(new TeaVMWebAudio());
    }

    @Override
    public WebAudioContext createContext() {
        return new TeaVMWebAudioContext();
    }

    @Override
    public OfflineAudioContext createOfflineContext(int channels, int length, float sampleRate) {
        return new TeaVMOfflineAudioContext(channels, length, sampleRate);
    }
}
