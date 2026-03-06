package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.MediaStreamAudioSourceNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSMediaStreamAudioSourceNode;

/**
 * TeaVM/browser implementation of {@link MediaStreamAudioSourceNode}.
 *
 * <p>Wraps a {@link JSMediaStreamAudioSourceNode} to route audio from a {@code MediaStream}
 * (e.g., microphone input) into the Web Audio API graph.</p>
 */
public class TeaVMMediaStreamAudioSourceNode extends TeaVMAudioNode implements MediaStreamAudioSourceNode {
    public TeaVMMediaStreamAudioSourceNode(JSMediaStreamAudioSourceNode jsNode, WebAudioContext context) {
        super(jsNode, context);
    }
}
