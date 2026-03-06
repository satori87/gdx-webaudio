package com.github.satori87.gdx.webaudio.teavm.channel;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSChannelSplitterNode;

/**
 * TeaVM/browser implementation of {@link ChannelSplitterNode}.
 *
 * <p>Wraps a {@link JSChannelSplitterNode} to separate a multi-channel audio stream into
 * individual mono outputs via the browser's Web Audio API.</p>
 */
public class TeaVMChannelSplitterNode extends TeaVMAudioNode implements ChannelSplitterNode {
    public TeaVMChannelSplitterNode(JSChannelSplitterNode jsNode, WebAudioContext context) {
        super(jsNode, context);
    }
}
