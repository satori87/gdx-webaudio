package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code DynamicsCompressorNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSDynamicsCompressorNode extends JSAudioNode {
    @JSBody(script = "return this.threshold;")
    JSAudioParam getThreshold();

    @JSBody(script = "return this.knee;")
    JSAudioParam getKnee();

    @JSBody(script = "return this.ratio;")
    JSAudioParam getRatio();

    @JSBody(script = "return this.attack;")
    JSAudioParam getAttack();

    @JSBody(script = "return this.release;")
    JSAudioParam getRelease();

    @JSProperty float getReduction();
}
