package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioNode}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
public interface JSAudioNode extends JSObject {
    @JSMethod
    JSAudioNode connect(JSAudioNode destination);

    @JSBody(params = {"destination", "output", "input"},
            script = "return this.connect(destination, output, input);")
    JSAudioNode connectWithIndices(JSAudioNode destination, int output, int input);

    @JSBody(params = {"param"}, script = "this.connect(param);")
    void connectToParam(JSAudioParam param);

    @JSBody(params = {"param", "output"}, script = "this.connect(param, output);")
    void connectToParam(JSAudioParam param, int output);

    @JSMethod
    void disconnect();

    @JSBody(params = {"output"}, script = "this.disconnect(output);")
    void disconnectOutput(int output);

    @JSBody(params = {"destination"}, script = "this.disconnect(destination);")
    void disconnectNode(JSAudioNode destination);

    @JSBody(params = {"destination", "output"}, script = "this.disconnect(destination, output);")
    void disconnectNode(JSAudioNode destination, int output);

    @JSBody(params = {"destination", "output", "input"},
            script = "this.disconnect(destination, output, input);")
    void disconnectNode(JSAudioNode destination, int output, int input);

    @JSBody(params = {"param"}, script = "this.disconnect(param);")
    void disconnectParam(JSAudioParam param);

    @JSBody(params = {"param", "output"}, script = "this.disconnect(param, output);")
    void disconnectParam(JSAudioParam param, int output);

    @JSProperty
    int getNumberOfInputs();

    @JSProperty
    int getNumberOfOutputs();

    @JSProperty
    int getChannelCount();

    @JSProperty
    void setChannelCount(int count);

    @JSProperty
    String getChannelCountMode();

    @JSProperty
    void setChannelCountMode(String mode);

    @JSProperty
    String getChannelInterpretation();

    @JSProperty
    void setChannelInterpretation(String interp);
}
