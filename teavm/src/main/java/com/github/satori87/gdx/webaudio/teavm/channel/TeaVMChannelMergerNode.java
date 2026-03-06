package com.github.satori87.gdx.webaudio.teavm.channel;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.channel.ChannelMergerNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSChannelMergerNode;

/**
 * TeaVM/browser implementation of {@link ChannelMergerNode}.
 *
 * <p>Wraps a {@link JSChannelMergerNode} to combine multiple mono audio inputs into a
 * single multi-channel output via the browser's Web Audio API.</p>
 */
public class TeaVMChannelMergerNode extends TeaVMAudioNode implements ChannelMergerNode {
    public TeaVMChannelMergerNode(JSChannelMergerNode jsNode, WebAudioContext context) {
        super(jsNode, context);
    }
}
