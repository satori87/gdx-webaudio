package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code OscillatorNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSOscillatorNode extends JSAudioNode {
    @JSBody(script = "return this.frequency;")
    JSAudioParam getFrequency();

    @JSBody(script = "return this.detune;")
    JSAudioParam getDetune();

    @JSProperty String getType();
    @JSProperty void setType(String type);
    @JSMethod void setPeriodicWave(JSPeriodicWave wave);
    @JSMethod void start();
    @JSBody(params = {"when"}, script = "this.start(when);")
    void start(double when);
    @JSMethod void stop();
    @JSBody(params = {"when"}, script = "this.stop(when);")
    void stop(double when);
    @JSProperty void setOnended(OnEndedCallback callback);

    @JSFunctor
    interface OnEndedCallback extends JSObject {
        void onEnded();
    }
}
