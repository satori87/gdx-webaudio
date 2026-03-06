package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioBufferSourceNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioBufferSourceNode extends JSAudioNode {
    @JSProperty JSAudioBuffer getBuffer();
    @JSProperty void setBuffer(JSAudioBuffer buffer);

    @JSBody(script = "return this.playbackRate;")
    JSAudioParam getPlaybackRate();

    @JSBody(script = "return this.detune;")
    JSAudioParam getDetune();

    @JSProperty boolean getLoop();
    @JSProperty void setLoop(boolean loop);
    @JSProperty double getLoopStart();
    @JSProperty void setLoopStart(double loopStart);
    @JSProperty double getLoopEnd();
    @JSProperty void setLoopEnd(double loopEnd);

    @JSMethod void start();
    @JSBody(params = {"when"}, script = "this.start(when);")
    void start(double when);
    @JSBody(params = {"when", "offset"}, script = "this.start(when, offset);")
    void start(double when, double offset);
    @JSBody(params = {"when", "offset", "duration"}, script = "this.start(when, offset, duration);")
    void start(double when, double offset, double duration);
    @JSMethod void stop();
    @JSBody(params = {"when"}, script = "this.stop(when);")
    void stop(double when);
    @JSProperty void setOnended(JSOscillatorNode.OnEndedCallback callback);
}
