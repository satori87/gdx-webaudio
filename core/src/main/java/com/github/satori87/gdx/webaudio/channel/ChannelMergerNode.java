package com.github.satori87.gdx.webaudio.channel;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Merges multiple mono audio inputs into a single multi-channel output stream.
 * Corresponds to the Web Audio API {@code ChannelMergerNode} interface.
 *
 * <p>Each input of this node becomes one channel of the output. Use
 * {@link com.github.satori87.gdx.webaudio.AudioNode#connect(com.github.satori87.gdx.webaudio.AudioNode, int, int)}
 * to route audio into specific input channels.</p>
 */
public interface ChannelMergerNode extends AudioNode {
}
