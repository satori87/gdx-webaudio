package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.MediaElementAudioSourceNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSMediaElementAudioSourceNode;

/**
 * TeaVM/browser implementation of {@link MediaElementAudioSourceNode}.
 *
 * <p>Wraps a {@link JSMediaElementAudioSourceNode} to route audio from an HTML media element
 * (such as {@code <audio>} or {@code <video>}) into the Web Audio API graph.</p>
 */
public class TeaVMMediaElementAudioSourceNode extends TeaVMAudioNode implements MediaElementAudioSourceNode {
    public TeaVMMediaElementAudioSourceNode(JSMediaElementAudioSourceNode jsNode, WebAudioContext context) {
        super(jsNode, context);
    }
}
