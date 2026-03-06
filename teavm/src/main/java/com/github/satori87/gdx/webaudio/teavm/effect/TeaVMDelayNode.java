package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.DelayNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSDelayNode;

/**
 * TeaVM/browser implementation of {@link DelayNode}.
 *
 * <p>Wraps a {@link JSDelayNode} to apply a configurable time delay to the audio signal
 * via the browser's Web Audio API.</p>
 */
public class TeaVMDelayNode extends TeaVMAudioNode implements DelayNode {
    private final JSDelayNode jsDelay;
    public TeaVMDelayNode(JSDelayNode jsDelay, WebAudioContext context) {
        super(jsDelay, context); this.jsDelay = jsDelay;
    }
    @Override public AudioParam getDelayTime() { return new TeaVMAudioParam(jsDelay.getDelayTime()); }
}
