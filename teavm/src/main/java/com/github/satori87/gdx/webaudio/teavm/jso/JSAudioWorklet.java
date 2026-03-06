package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioWorklet}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioWorklet extends JSObject {
    @JSBody(params = {"url", "onSuccess", "onError"},
            script = "this.addModule(url).then(onSuccess).catch(onError);")
    void addModule(String url, VoidCallback onSuccess, VoidCallback onError);

    @JSFunctor
    interface VoidCallback extends JSObject {
        void invoke();
    }
}
