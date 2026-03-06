package com.github.satori87.gdx.webaudio.teavm.jso;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO binding for the JavaScript {@code AudioContext}.
 * Raw JavaScript interop interface — not intended for direct use by library consumers.
 */
@JSClass(name = "AudioContext")
public class JSAudioContext implements JSObject {
    public JSAudioContext() {
    }

    @JSProperty
    public native JSAudioDestinationNode getDestination();

    @JSProperty
    public native float getSampleRate();

    @JSProperty
    public native double getCurrentTime();

    @JSProperty
    public native String getState();

    @JSProperty
    public native JSAudioListener getListener();

    @JSProperty
    public native double getBaseLatency();

    @JSProperty
    public native double getOutputLatency();

    @JSProperty
    public native JSAudioWorklet getAudioWorklet();

    @JSProperty
    public native void setOnstatechange(JSAudioWorklet.VoidCallback callback);

    @JSMethod
    public native JSGainNode createGain();

    @JSMethod
    public native JSOscillatorNode createOscillator();

    @JSMethod
    public native JSAudioBufferSourceNode createBufferSource();

    @JSMethod
    public native JSConstantSourceNode createConstantSource();

    @JSMethod
    public native JSBiquadFilterNode createBiquadFilter();

    @JSBody(params = {"maxDelayTime"}, script = "return this.createDelay(maxDelayTime);")
    public native JSDelayNode createDelay(float maxDelayTime);

    @JSBody(script = "return this.createDelay();")
    public native JSDelayNode createDelay();

    @JSMethod
    public native JSConvolverNode createConvolver();

    @JSMethod
    public native JSWaveShaperNode createWaveShaper();

    @JSMethod
    public native JSDynamicsCompressorNode createDynamicsCompressor();

    @JSMethod
    public native JSStereoPannerNode createStereoPanner();

    @JSMethod
    public native JSPannerNode createPanner();

    @JSMethod
    public native JSAnalyserNode createAnalyser();

    @JSBody(params = {"numberOfOutputs"},
            script = "return this.createChannelSplitter(numberOfOutputs);")
    public native JSChannelSplitterNode createChannelSplitter(int numberOfOutputs);

    @JSBody(script = "return this.createChannelSplitter();")
    public native JSChannelSplitterNode createChannelSplitter();

    @JSBody(params = {"numberOfInputs"},
            script = "return this.createChannelMerger(numberOfInputs);")
    public native JSChannelMergerNode createChannelMerger(int numberOfInputs);

    @JSBody(script = "return this.createChannelMerger();")
    public native JSChannelMergerNode createChannelMerger();

    @JSBody(params = {"numberOfChannels", "length", "sampleRate"},
            script = "return this.createBuffer(numberOfChannels, length, sampleRate);")
    public native JSAudioBuffer createBuffer(int numberOfChannels, int length, float sampleRate);

    @JSBody(params = {"feedforward", "feedback"},
            script = "return this.createIIRFilter(feedforward, feedback);")
    public native JSIIRFilterNode createIIRFilter(JSFloat32Array feedforward,
                                                   JSFloat32Array feedback);

    @JSBody(params = {"real", "imag"},
            script = "return this.createPeriodicWave(real, imag);")
    public native JSPeriodicWave createPeriodicWave(JSFloat32Array real, JSFloat32Array imag);

    @JSBody(params = {"real", "imag", "disableNormalization"},
            script = "return this.createPeriodicWave(real, imag, " +
                     "{disableNormalization: disableNormalization});")
    public native JSPeriodicWave createPeriodicWaveWithConstraints(JSFloat32Array real,
                                                                    JSFloat32Array imag,
                                                                    boolean disableNormalization);

    @JSBody(params = {"arrayBuffer", "onSuccess", "onError"},
            script = "this.decodeAudioData(arrayBuffer).then(onSuccess).catch(onError);")
    public native void decodeAudioData(JSArrayBuffer arrayBuffer,
                                        DecodeSuccessCallback onSuccess,
                                        DecodeErrorCallback onError);

    @JSMethod
    public native JSMediaStreamAudioDestinationNode createMediaStreamDestination();

    @JSBody(params = {"onDone"}, script = "this.resume().then(onDone);")
    public native void resume(JSAudioWorklet.VoidCallback onDone);

    @JSBody(params = {"onDone"}, script = "this.suspend().then(onDone);")
    public native void suspend(JSAudioWorklet.VoidCallback onDone);

    @JSBody(params = {"onDone"}, script = "this.close().then(onDone);")
    public native void close(JSAudioWorklet.VoidCallback onDone);

    @JSBody(params = {"context", "name"},
            script = "return new AudioWorkletNode(context, name);")
    public static native JSAudioWorkletNode createWorkletNode(JSAudioContext context, String name);

    @JSFunctor
    public interface DecodeSuccessCallback extends JSObject {
        void onSuccess(JSAudioBuffer buffer);
    }

    @JSFunctor
    public interface DecodeErrorCallback extends JSObject {
        void onError(JSObject error);
    }
}
