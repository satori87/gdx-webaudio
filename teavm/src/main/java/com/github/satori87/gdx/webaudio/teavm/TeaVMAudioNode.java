package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioNode;
import com.github.satori87.gdx.webaudio.types.ChannelCountMode;
import com.github.satori87.gdx.webaudio.types.ChannelInterpretation;

/**
 * TeaVM/browser implementation of {@link AudioNode}.
 *
 * <p>Base class for all TeaVM audio node implementations. Wraps a {@link JSAudioNode} and
 * delegates connection, disconnection, and channel configuration to the browser's native node.
 * Provides the static {@link #unwrap(AudioNode)} utility to extract the underlying JSO object
 * from any {@code AudioNode}.</p>
 */
public class TeaVMAudioNode implements AudioNode {
    protected final JSAudioNode jsNode;
    protected final WebAudioContext context;

    public TeaVMAudioNode(JSAudioNode jsNode, WebAudioContext context) {
        this.jsNode = jsNode;
        this.context = context;
    }

    @Override public AudioNode connect(AudioNode destination) {
        jsNode.connect(unwrap(destination)); return destination;
    }
    @Override public AudioNode connect(AudioNode destination, int outputIndex, int inputIndex) {
        jsNode.connectWithIndices(unwrap(destination), outputIndex, inputIndex); return destination;
    }
    @Override public void connectParam(AudioParam destination) {
        jsNode.connectToParam(((TeaVMAudioParam) destination).jsParam);
    }
    @Override public void connectParam(AudioParam destination, int outputIndex) {
        jsNode.connectToParam(((TeaVMAudioParam) destination).jsParam, outputIndex);
    }
    @Override public void disconnect() { jsNode.disconnect(); }
    @Override public void disconnect(int outputIndex) { jsNode.disconnectOutput(outputIndex); }
    @Override public void disconnect(AudioNode destination) { jsNode.disconnectNode(unwrap(destination)); }
    @Override public void disconnect(AudioNode destination, int outputIndex) {
        jsNode.disconnectNode(unwrap(destination), outputIndex);
    }
    @Override public void disconnect(AudioNode destination, int outputIndex, int inputIndex) {
        jsNode.disconnectNode(unwrap(destination), outputIndex, inputIndex);
    }
    @Override public void disconnectParam(AudioParam destination) {
        jsNode.disconnectParam(((TeaVMAudioParam) destination).jsParam);
    }
    @Override public void disconnectParam(AudioParam destination, int outputIndex) {
        jsNode.disconnectParam(((TeaVMAudioParam) destination).jsParam, outputIndex);
    }
    @Override public int getNumberOfInputs() { return jsNode.getNumberOfInputs(); }
    @Override public int getNumberOfOutputs() { return jsNode.getNumberOfOutputs(); }
    @Override public int getChannelCount() { return jsNode.getChannelCount(); }
    @Override public void setChannelCount(int count) { jsNode.setChannelCount(count); }
    @Override public ChannelCountMode getChannelCountMode() {
        return ChannelCountMode.fromJsValue(jsNode.getChannelCountMode());
    }
    @Override public void setChannelCountMode(ChannelCountMode mode) {
        jsNode.setChannelCountMode(mode.toJsValue());
    }
    @Override public ChannelInterpretation getChannelInterpretation() {
        return ChannelInterpretation.fromJsValue(jsNode.getChannelInterpretation());
    }
    @Override public void setChannelInterpretation(ChannelInterpretation interp) {
        jsNode.setChannelInterpretation(interp.toJsValue());
    }
    @Override public WebAudioContext getContext() { return context; }

    /** Extracts the underlying {@link JSAudioNode} from the given {@link AudioNode}. */
    static JSAudioNode unwrap(AudioNode node) {
        return ((TeaVMAudioNode) node).jsNode;
    }
}
