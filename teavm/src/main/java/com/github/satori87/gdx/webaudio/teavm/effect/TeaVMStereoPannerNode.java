package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSStereoPannerNode;

/**
 * TeaVM/browser implementation of {@link StereoPannerNode}.
 *
 * <p>Wraps a {@link JSStereoPannerNode} to pan audio left or right in the stereo field
 * via the browser's Web Audio API.</p>
 */
public class TeaVMStereoPannerNode extends TeaVMAudioNode implements StereoPannerNode {
    private final JSStereoPannerNode jsPanner;
    public TeaVMStereoPannerNode(JSStereoPannerNode jsPanner, WebAudioContext context) {
        super(jsPanner, context); this.jsPanner = jsPanner;
    }
    @Override public AudioParam getPan() { return new TeaVMAudioParam(jsPanner.getPan()); }
}
