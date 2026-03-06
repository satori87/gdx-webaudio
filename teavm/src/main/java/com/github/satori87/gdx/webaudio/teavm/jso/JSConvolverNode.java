package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code ConvolverNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSConvolverNode extends JSAudioNode {
    @JSProperty JSAudioBuffer getBuffer();
    @JSProperty void setBuffer(JSAudioBuffer buffer);
    @JSProperty boolean getNormalize();
    @JSProperty void setNormalize(boolean normalize);
}
