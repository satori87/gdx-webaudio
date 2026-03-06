package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code OfflineAudioContext}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public abstract class JSOfflineAudioContext extends JSAudioContext {
    @JSProperty
    public native int getLength();

    @JSBody(params = {"onComplete"},
            script = "this.startRendering().then(onComplete);")
    public native void startRendering(JSAudioContext.DecodeSuccessCallback onComplete);
}
