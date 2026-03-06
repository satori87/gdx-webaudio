package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioBuffer;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioBufferSourceNode;

/**
 * TeaVM/browser implementation of {@link AudioBufferSourceNode}.
 *
 * <p>Wraps a {@link JSAudioBufferSourceNode} to play back audio data from an
 * {@link com.github.satori87.gdx.webaudio.AudioBuffer} via the browser's Web Audio API.
 * Supports looping, playback rate control, and scheduled start/stop.</p>
 */
public class TeaVMAudioBufferSourceNode extends TeaVMAudioNode implements AudioBufferSourceNode {
    private final JSAudioBufferSourceNode jsSrc;

    public TeaVMAudioBufferSourceNode(JSAudioBufferSourceNode jsSrc, WebAudioContext context) {
        super(jsSrc, context);
        this.jsSrc = jsSrc;
    }

    @Override public AudioBuffer getBuffer() {
        return jsSrc.getBuffer() != null ? new TeaVMAudioBuffer(jsSrc.getBuffer()) : null;
    }
    @Override public void setBuffer(AudioBuffer buffer) {
        jsSrc.setBuffer(((TeaVMAudioBuffer) buffer).jsBuffer);
    }
    @Override public AudioParam getPlaybackRate() { return new TeaVMAudioParam(jsSrc.getPlaybackRate()); }
    @Override public AudioParam getDetune() { return new TeaVMAudioParam(jsSrc.getDetune()); }
    @Override public boolean isLoop() { return jsSrc.getLoop(); }
    @Override public void setLoop(boolean loop) { jsSrc.setLoop(loop); }
    @Override public double getLoopStart() { return jsSrc.getLoopStart(); }
    @Override public void setLoopStart(double loopStart) { jsSrc.setLoopStart(loopStart); }
    @Override public double getLoopEnd() { return jsSrc.getLoopEnd(); }
    @Override public void setLoopEnd(double loopEnd) { jsSrc.setLoopEnd(loopEnd); }
    @Override public void start() { jsSrc.start(); }
    @Override public void start(double when) { jsSrc.start(when); }
    @Override public void start(double when, double offset) { jsSrc.start(when, offset); }
    @Override public void start(double when, double offset, double duration) { jsSrc.start(when, offset, duration); }
    @Override public void stop() { jsSrc.stop(); }
    @Override public void stop(double when) { jsSrc.stop(when); }
    @Override public void setOnEnded(Runnable listener) {
        jsSrc.setOnended(() -> listener.run());
    }
}
