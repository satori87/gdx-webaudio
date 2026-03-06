package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.ConvolverNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioBuffer;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSConvolverNode;

/**
 * TeaVM/browser implementation of {@link ConvolverNode}.
 *
 * <p>Wraps a {@link JSConvolverNode} to apply linear convolution (typically for reverb effects)
 * using an impulse response buffer via the browser's Web Audio API.</p>
 */
public class TeaVMConvolverNode extends TeaVMAudioNode implements ConvolverNode {
    private final JSConvolverNode jsConvolver;
    public TeaVMConvolverNode(JSConvolverNode jsConvolver, WebAudioContext context) {
        super(jsConvolver, context); this.jsConvolver = jsConvolver;
    }
    @Override public AudioBuffer getBuffer() {
        return jsConvolver.getBuffer() != null ? new TeaVMAudioBuffer(jsConvolver.getBuffer()) : null;
    }
    @Override public void setBuffer(AudioBuffer buffer) {
        jsConvolver.setBuffer(((TeaVMAudioBuffer) buffer).jsBuffer);
    }
    @Override public boolean isNormalize() { return jsConvolver.getNormalize(); }
    @Override public void setNormalize(boolean normalize) { jsConvolver.setNormalize(normalize); }
}
