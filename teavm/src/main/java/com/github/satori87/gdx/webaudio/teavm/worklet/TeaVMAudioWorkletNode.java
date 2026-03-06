package com.github.satori87.gdx.webaudio.teavm.worklet;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.worklet.AudioParamMap;
import com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioWorkletNode;

/**
 * TeaVM/browser implementation of {@link AudioWorkletNode}.
 *
 * <p>Wraps a {@link JSAudioWorkletNode} to interface with custom audio processing code
 * running in an {@code AudioWorkletProcessor} on the browser's audio rendering thread.</p>
 */
public class TeaVMAudioWorkletNode extends TeaVMAudioNode implements AudioWorkletNode {
    private final JSAudioWorkletNode jsWorklet;

    public TeaVMAudioWorkletNode(JSAudioWorkletNode jsWorklet, WebAudioContext context) {
        super(jsWorklet, context); this.jsWorklet = jsWorklet;
    }

    @Override public AudioParamMap getParameters() {
        return new TeaVMAudioParamMap(jsWorklet.getParameters());
    }
    @Override public void setOnProcessorError(Runnable listener) {
        // AudioWorkletNode processorerror event handling
    }
}
