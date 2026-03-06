package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code Uint8Array}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
@JSClass(name = "Uint8Array")
public class JSUint8Array implements JSObject {
    public JSUint8Array(int length) {
    }

    @JSProperty
    public native int getLength();

    @JSIndexer
    public native int get(int index);

    @JSIndexer
    public native void set(int index, int value);

    @JSBody(params = {"typedArray", "dest"}, script =
        "for (var i = 0; i < typedArray.length; i++) dest[i] = typedArray[i];")
    public static native void copyToByteArray(JSUint8Array typedArray, byte[] dest);
}
