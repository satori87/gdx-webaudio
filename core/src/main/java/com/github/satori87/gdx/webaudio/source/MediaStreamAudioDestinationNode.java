package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Represents a destination node that outputs its audio to a {@code MediaStream}.
 * Corresponds to the Web Audio API {@code MediaStreamAudioDestinationNode} interface.
 *
 * <p>This is a marker interface. Audio routed to this node is captured as a media stream,
 * which can be used for recording or transmission via WebRTC.</p>
 */
public interface MediaStreamAudioDestinationNode extends AudioNode {
}
