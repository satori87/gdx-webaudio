package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code ConstantSourceNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSConstantSourceNode extends JSAudioNode {
    @JSBody(script = "return this.offset;")
    JSAudioParam getOffset();

    @JSMethod void start();
    @JSBody(params = {"when"}, script = "this.start(when);")
    void start(double when);
    @JSMethod void stop();
    @JSBody(params = {"when"}, script = "this.stop(when);")
    void stop(double when);
    @JSProperty void setOnended(JSOscillatorNode.OnEndedCallback callback);
}
