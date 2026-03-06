package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code ArrayBuffer}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
@JSClass(name = "ArrayBuffer")
public class JSArrayBuffer implements JSObject {
    public JSArrayBuffer(int byteLength) {
    }

    @JSProperty
    public native int getByteLength();

    @JSBody(params = {"bytes"}, script =
        "var buf = new ArrayBuffer(bytes.length);" +
        "var view = new Uint8Array(buf);" +
        "for (var i = 0; i < bytes.length; i++) view[i] = bytes[i] & 0xff;" +
        "return buf;")
    public static native JSArrayBuffer fromByteArray(byte[] bytes);
}
