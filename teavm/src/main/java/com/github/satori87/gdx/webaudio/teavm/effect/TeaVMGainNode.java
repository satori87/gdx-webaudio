package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSGainNode;

/**
 * TeaVM/browser implementation of {@link GainNode}.
 *
 * <p>Wraps a {@link JSGainNode} to apply volume changes to the audio signal
 * via the browser's Web Audio API.</p>
 */
public class TeaVMGainNode extends TeaVMAudioNode implements GainNode {
    private final JSGainNode jsGain;
    public TeaVMGainNode(JSGainNode jsGain, WebAudioContext context) {
        super(jsGain, context); this.jsGain = jsGain;
    }
    @Override public AudioParam getGain() { return new TeaVMAudioParam(jsGain.getGain()); }
}
