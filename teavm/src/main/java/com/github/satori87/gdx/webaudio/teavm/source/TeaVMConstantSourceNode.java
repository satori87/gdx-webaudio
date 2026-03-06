package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.ConstantSourceNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSConstantSourceNode;

/**
 * TeaVM/browser implementation of {@link ConstantSourceNode}.
 *
 * <p>Wraps a {@link JSConstantSourceNode} to output a constant audio signal whose value
 * can be automated via the offset {@link com.github.satori87.gdx.webaudio.AudioParam}.</p>
 */
public class TeaVMConstantSourceNode extends TeaVMAudioNode implements ConstantSourceNode {
    private final JSConstantSourceNode jsNode;

    public TeaVMConstantSourceNode(JSConstantSourceNode jsNode, WebAudioContext context) {
        super(jsNode, context);
        this.jsNode = jsNode;
    }

    @Override public AudioParam getOffset() { return new TeaVMAudioParam(jsNode.getOffset()); }
    @Override public void start() { jsNode.start(); }
    @Override public void start(double when) { jsNode.start(when); }
    @Override public void stop() { jsNode.stop(); }
    @Override public void stop(double when) { jsNode.stop(when); }
    @Override public void setOnEnded(Runnable listener) {
        jsNode.setOnended(() -> listener.run());
    }
}
