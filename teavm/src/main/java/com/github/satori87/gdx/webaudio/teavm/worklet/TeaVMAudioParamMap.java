package com.github.satori87.gdx.webaudio.teavm.worklet;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.worklet.AudioParamMap;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioParamMap;

/**
 * TeaVM/browser implementation of {@link AudioParamMap}.
 *
 * <p>Wraps a {@link JSAudioParamMap} to provide named access to the
 * {@link com.github.satori87.gdx.webaudio.AudioParam} instances exposed by an
 * {@link AudioWorkletNode}'s processor.</p>
 */
public class TeaVMAudioParamMap implements AudioParamMap {
    private final JSAudioParamMap jsMap;

    public TeaVMAudioParamMap(JSAudioParamMap jsMap) {
        this.jsMap = jsMap;
    }

    @Override public AudioParam get(String name) { return new TeaVMAudioParam(jsMap.get(name)); }
    @Override public boolean has(String name) { return jsMap.has(name); }
    @Override public int size() { return jsMap.getSize(); }
    @Override public String[] keys() { return jsMap.keys(); }
}
