package com.github.satori87.gdx.webaudio;

/**
 * The terminal node of an audio graph representing the final audio output destination.
 * Maps to the Web Audio API {@code AudioDestinationNode} interface.
 * <p>
 * Every {@link WebAudioContext} has exactly one destination node, obtained via
 * {@link WebAudioContext#getDestination()}. All audio that should be heard must
 * be routed to this node.
 *
 * @see AudioDestinationNode (Web Audio API)
 */
public interface AudioDestinationNode extends AudioNode {

    /**
     * Returns the maximum number of channels that the audio hardware supports.
     *
     * @return the maximum channel count
     */
    int getMaxChannelCount();
}
