package com.github.satori87.gdx.webaudio;

import com.github.satori87.gdx.webaudio.types.ChannelCountMode;
import com.github.satori87.gdx.webaudio.types.ChannelInterpretation;

/**
 * Base interface for all nodes in an audio processing graph.
 * Maps to the Web Audio API {@code AudioNode} interface.
 * <p>
 * Audio nodes can be connected together to form an audio routing graph. Each node
 * has inputs, outputs, and channel configuration properties that control how audio
 * signals are up-mixed or down-mixed between channels.
 *
 * @see AudioNode (Web Audio API)
 */
public interface AudioNode {

    /**
     * Connects this node's output to another node's input.
     *
     * @param destination the node to connect to
     * @return the destination node, for chaining
     */
    AudioNode connect(AudioNode destination);

    /**
     * Connects a specific output of this node to a specific input of the destination node.
     *
     * @param destination the node to connect to
     * @param outputIndex the index of this node's output to connect from
     * @param inputIndex  the index of the destination node's input to connect to
     * @return the destination node, for chaining
     */
    AudioNode connect(AudioNode destination, int outputIndex, int inputIndex);

    /**
     * Connects this node's output to an {@link AudioParam}, allowing the audio signal
     * to control the parameter value.
     *
     * @param destination the audio parameter to connect to
     */
    void connectParam(AudioParam destination);

    /**
     * Connects a specific output of this node to an {@link AudioParam}.
     *
     * @param destination the audio parameter to connect to
     * @param outputIndex the index of this node's output to connect from
     */
    void connectParam(AudioParam destination, int outputIndex);

    /**
     * Disconnects all outgoing connections from this node.
     */
    void disconnect();

    /**
     * Disconnects all connections from the specified output of this node.
     *
     * @param outputIndex the index of the output to disconnect
     */
    void disconnect(int outputIndex);

    /**
     * Disconnects all connections from this node to the specified destination node.
     *
     * @param destination the node to disconnect from
     */
    void disconnect(AudioNode destination);

    /**
     * Disconnects the specified output from the given destination node.
     *
     * @param destination the node to disconnect from
     * @param outputIndex the index of this node's output to disconnect
     */
    void disconnect(AudioNode destination, int outputIndex);

    /**
     * Disconnects a specific output from a specific input of the destination node.
     *
     * @param destination the node to disconnect from
     * @param outputIndex the index of this node's output to disconnect
     * @param inputIndex  the index of the destination node's input to disconnect from
     */
    void disconnect(AudioNode destination, int outputIndex, int inputIndex);

    /**
     * Disconnects all connections from this node to the specified {@link AudioParam}.
     *
     * @param destination the audio parameter to disconnect from
     */
    void disconnectParam(AudioParam destination);

    /**
     * Disconnects a specific output from the specified {@link AudioParam}.
     *
     * @param destination the audio parameter to disconnect from
     * @param outputIndex the index of this node's output to disconnect
     */
    void disconnectParam(AudioParam destination, int outputIndex);

    /**
     * Returns the number of inputs this node accepts.
     *
     * @return the number of inputs
     */
    int getNumberOfInputs();

    /**
     * Returns the number of outputs this node produces.
     *
     * @return the number of outputs
     */
    int getNumberOfOutputs();

    /**
     * Returns the number of channels used when up-mixing or down-mixing connections.
     *
     * @return the channel count
     */
    int getChannelCount();

    /**
     * Sets the number of channels used when up-mixing or down-mixing connections.
     *
     * @param count the channel count
     */
    void setChannelCount(int count);

    /**
     * Returns the mode that determines how channels are counted for this node.
     *
     * @return the current channel count mode
     */
    ChannelCountMode getChannelCountMode();

    /**
     * Sets the mode that determines how channels are counted for this node.
     *
     * @param mode the channel count mode
     */
    void setChannelCountMode(ChannelCountMode mode);

    /**
     * Returns the interpretation of how channels are mapped when up-mixing or down-mixing.
     *
     * @return the current channel interpretation
     */
    ChannelInterpretation getChannelInterpretation();

    /**
     * Sets the interpretation of how channels are mapped when up-mixing or down-mixing.
     *
     * @param interpretation the channel interpretation
     */
    void setChannelInterpretation(ChannelInterpretation interpretation);

    /**
     * Returns the audio context that owns this node.
     *
     * @return the owning {@link WebAudioContext}
     */
    WebAudioContext getContext();
}
