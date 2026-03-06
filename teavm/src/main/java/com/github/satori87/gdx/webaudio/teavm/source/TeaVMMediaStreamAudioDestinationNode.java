package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.MediaStreamAudioDestinationNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSMediaStreamAudioDestinationNode;

/**
 * TeaVM/browser implementation of {@link MediaStreamAudioDestinationNode}.
 *
 * <p>Wraps a {@link JSMediaStreamAudioDestinationNode} to capture audio output from the
 * Web Audio API graph into a {@code MediaStream} for recording or streaming.</p>
 */
public class TeaVMMediaStreamAudioDestinationNode extends TeaVMAudioNode implements MediaStreamAudioDestinationNode {
    public TeaVMMediaStreamAudioDestinationNode(JSMediaStreamAudioDestinationNode jsNode, WebAudioContext context) {
        super(jsNode, context);
    }
}
