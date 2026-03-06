package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioNode;

/**
 * Base class for composite audio nodes built from multiple Web Audio API nodes.
 *
 * <p>Routes incoming connections to the input node and outgoing connections from
 * the output node, allowing the composite to be used transparently in an audio graph.</p>
 */
public class TeaVMCompositeAudioNode extends TeaVMAudioNode {
    protected final JSAudioNode outputNode;

    public TeaVMCompositeAudioNode(JSAudioNode inputNode, JSAudioNode outputNode, WebAudioContext context) {
        super(inputNode, context);
        this.outputNode = outputNode;
    }

    @Override public AudioNode connect(AudioNode destination) {
        outputNode.connect(unwrap(destination)); return destination;
    }
    @Override public AudioNode connect(AudioNode destination, int outputIndex, int inputIndex) {
        outputNode.connectWithIndices(unwrap(destination), outputIndex, inputIndex); return destination;
    }
    @Override public void connectParam(AudioParam destination) {
        outputNode.connectToParam(((TeaVMAudioParam) destination).jsParam);
    }
    @Override public void connectParam(AudioParam destination, int outputIndex) {
        outputNode.connectToParam(((TeaVMAudioParam) destination).jsParam, outputIndex);
    }
    @Override public void disconnect() { outputNode.disconnect(); }
    @Override public void disconnect(int outputIndex) { outputNode.disconnectOutput(outputIndex); }
    @Override public void disconnect(AudioNode destination) { outputNode.disconnectNode(unwrap(destination)); }
    @Override public void disconnect(AudioNode destination, int outputIndex) {
        outputNode.disconnectNode(unwrap(destination), outputIndex);
    }
    @Override public void disconnect(AudioNode destination, int outputIndex, int inputIndex) {
        outputNode.disconnectNode(unwrap(destination), outputIndex, inputIndex);
    }
    @Override public void disconnectParam(AudioParam destination) {
        outputNode.disconnectParam(((TeaVMAudioParam) destination).jsParam);
    }
    @Override public void disconnectParam(AudioParam destination, int outputIndex) {
        outputNode.disconnectParam(((TeaVMAudioParam) destination).jsParam, outputIndex);
    }
    @Override public int getNumberOfOutputs() { return outputNode.getNumberOfOutputs(); }
}
