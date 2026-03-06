package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioParamMap}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioParamMap extends JSObject {
    @JSBody(params = {"name"}, script = "return this.get(name);")
    JSAudioParam get(String name);

    @JSBody(params = {"name"}, script = "return this.has(name);")
    boolean has(String name);

    @JSProperty
    int getSize();

    @JSBody(script = "return Array.from(this.keys());")
    String[] keys();
}
