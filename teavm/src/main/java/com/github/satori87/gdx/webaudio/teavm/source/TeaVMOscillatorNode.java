package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.PeriodicWave;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.TeaVMPeriodicWave;
import com.github.satori87.gdx.webaudio.teavm.jso.JSOscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * TeaVM/browser implementation of {@link OscillatorNode}.
 *
 * <p>Wraps a {@link JSOscillatorNode} to generate periodic waveforms (sine, square, sawtooth,
 * triangle, or custom) via the browser's Web Audio API.</p>
 */
public class TeaVMOscillatorNode extends TeaVMAudioNode implements OscillatorNode {
    private final JSOscillatorNode jsOsc;

    public TeaVMOscillatorNode(JSOscillatorNode jsOsc, WebAudioContext context) {
        super(jsOsc, context);
        this.jsOsc = jsOsc;
    }

    @Override public AudioParam getFrequency() { return new TeaVMAudioParam(jsOsc.getFrequency()); }
    @Override public AudioParam getDetune() { return new TeaVMAudioParam(jsOsc.getDetune()); }
    @Override public OscillatorType getType() { return OscillatorType.fromJsValue(jsOsc.getType()); }
    @Override public void setType(OscillatorType type) { jsOsc.setType(type.toJsValue()); }
    @Override public void setPeriodicWave(PeriodicWave wave) {
        jsOsc.setPeriodicWave(((TeaVMPeriodicWave) wave).jsWave);
    }
    @Override public void start() { jsOsc.start(); }
    @Override public void start(double when) { jsOsc.start(when); }
    @Override public void stop() { jsOsc.stop(); }
    @Override public void stop(double when) { jsOsc.stop(when); }
    @Override public void setOnEnded(Runnable listener) {
        jsOsc.setOnended(() -> listener.run());
    }
}
