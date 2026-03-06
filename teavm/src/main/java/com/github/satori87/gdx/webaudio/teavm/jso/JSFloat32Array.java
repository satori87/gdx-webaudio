package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code Float32Array}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
@JSClass(name = "Float32Array")
public class JSFloat32Array implements JSObject {
    public JSFloat32Array(int length) {
    }

    @JSProperty
    public native int getLength();

    @JSIndexer
    public native float get(int index);

    @JSIndexer
    public native void set(int index, float value);

    @JSBody(params = {"array"}, script = "return new Float32Array(array);")
    public static native JSFloat32Array fromArray(float[] array);

    @JSBody(params = {"typedArray"}, script =
        "var arr = new Array(typedArray.length);" +
        "for (var i = 0; i < typedArray.length; i++) arr[i] = typedArray[i];" +
        "return arr;")
    public static native float[] toArray(JSFloat32Array typedArray);
}
