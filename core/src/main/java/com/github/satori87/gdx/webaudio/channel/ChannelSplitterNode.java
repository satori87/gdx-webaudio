package com.github.satori87.gdx.webaudio.channel;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Splits a multi-channel audio stream into separate mono output channels.
 * Corresponds to the Web Audio API {@code ChannelSplitterNode} interface.
 *
 * <p>Each output of this node corresponds to one channel of the input. Use
 * {@link com.github.satori87.gdx.webaudio.AudioNode#connect(com.github.satori87.gdx.webaudio.AudioNode, int, int)}
 * to route individual output channels to other nodes.</p>
 */
public interface ChannelSplitterNode extends AudioNode {
}
