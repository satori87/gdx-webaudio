package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.*;
import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;
import com.github.satori87.gdx.webaudio.channel.ChannelMergerNode;
import com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode;
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.source.*;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.teavm.analysis.TeaVMAnalyserNode;
import com.github.satori87.gdx.webaudio.teavm.channel.TeaVMChannelMergerNode;
import com.github.satori87.gdx.webaudio.teavm.channel.TeaVMChannelSplitterNode;
import com.github.satori87.gdx.webaudio.teavm.effect.*;
import com.github.satori87.gdx.webaudio.teavm.jso.*;
import com.github.satori87.gdx.webaudio.teavm.source.*;
import com.github.satori87.gdx.webaudio.teavm.spatial.TeaVMPannerNode;
import com.github.satori87.gdx.webaudio.teavm.worklet.TeaVMAudioWorkletNode;
import com.github.satori87.gdx.webaudio.types.AudioContextState;
import com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode;

/**
 * TeaVM/browser implementation of {@link WebAudioContext}.
 *
 * <p>Wraps a {@link JSAudioContext} to delegate all audio context operations to the
 * browser's native {@code AudioContext}. Also serves as a base class for
 * {@link TeaVMOfflineAudioContext}.</p>
 */
public class TeaVMWebAudioContext implements WebAudioContext {
    protected final JSAudioContext jsCtx;

    public TeaVMWebAudioContext() {
        this.jsCtx = new JSAudioContext();
    }

    protected TeaVMWebAudioContext(JSAudioContext jsCtx) {
        this.jsCtx = jsCtx;
    }

    @Override public AudioContextState getState() { return AudioContextState.fromJsValue(jsCtx.getState()); }
    @Override public double getCurrentTime() { return jsCtx.getCurrentTime(); }
    @Override public float getSampleRate() { return jsCtx.getSampleRate(); }
    @Override public AudioDestinationNode getDestination() { return new TeaVMAudioDestinationNode(jsCtx.getDestination(), this); }
    @Override public AudioListener getListener() { return new TeaVMAudioListener(jsCtx.getListener()); }
    @Override public void resume(Runnable onResumed) { jsCtx.resume(() -> { if (onResumed != null) onResumed.run(); }); }
    @Override public void suspend(Runnable onSuspended) { jsCtx.suspend(() -> { if (onSuspended != null) onSuspended.run(); }); }
    @Override public void close(Runnable onClosed) { jsCtx.close(() -> { if (onClosed != null) onClosed.run(); }); }
    @Override public void setOnStateChange(Runnable listener) { jsCtx.setOnstatechange(() -> listener.run()); }
    @Override public double getBaseLatency() { return jsCtx.getBaseLatency(); }
    @Override public double getOutputLatency() { return jsCtx.getOutputLatency(); }

    @Override public AudioBuffer createBuffer(int numberOfChannels, int length, float sampleRate) {
        return new TeaVMAudioBuffer(jsCtx.createBuffer(numberOfChannels, length, sampleRate));
    }
    @Override public void decodeAudioData(byte[] audioData, AudioBuffer.DecodeCallback onSuccess, Runnable onError) {
        JSArrayBuffer jsBuffer = JSArrayBuffer.fromByteArray(audioData);
        jsCtx.decodeAudioData(jsBuffer,
            buffer -> onSuccess.onDecoded(new TeaVMAudioBuffer(buffer)),
            error -> { if (onError != null) onError.run(); });
    }

    @Override public OscillatorNode createOscillator() { return new TeaVMOscillatorNode(jsCtx.createOscillator(), this); }
    @Override public AudioBufferSourceNode createBufferSource() { return new TeaVMAudioBufferSourceNode(jsCtx.createBufferSource(), this); }
    @Override public ConstantSourceNode createConstantSource() { return new TeaVMConstantSourceNode(jsCtx.createConstantSource(), this); }
    @Override public GainNode createGain() { return new TeaVMGainNode(jsCtx.createGain(), this); }
    @Override public BiquadFilterNode createBiquadFilter() { return new TeaVMBiquadFilterNode(jsCtx.createBiquadFilter(), this); }
    @Override public IIRFilterNode createIIRFilter(float[] feedforward, float[] feedback) {
        return new TeaVMIIRFilterNode(jsCtx.createIIRFilter(JSFloat32Array.fromArray(feedforward), JSFloat32Array.fromArray(feedback)), this);
    }
    @Override public DelayNode createDelay() { return new TeaVMDelayNode(jsCtx.createDelay(), this); }
    @Override public DelayNode createDelay(float maxDelayTime) { return new TeaVMDelayNode(jsCtx.createDelay(maxDelayTime), this); }
    @Override public ConvolverNode createConvolver() { return new TeaVMConvolverNode(jsCtx.createConvolver(), this); }
    @Override public WaveShaperNode createWaveShaper() { return new TeaVMWaveShaperNode(jsCtx.createWaveShaper(), this); }
    @Override public DynamicsCompressorNode createDynamicsCompressor() { return new TeaVMDynamicsCompressorNode(jsCtx.createDynamicsCompressor(), this); }
    @Override public StereoPannerNode createStereoPanner() { return new TeaVMStereoPannerNode(jsCtx.createStereoPanner(), this); }
    @Override public PannerNode createPanner() { return new TeaVMPannerNode(jsCtx.createPanner(), this); }
    @Override public AnalyserNode createAnalyser() { return new TeaVMAnalyserNode(jsCtx.createAnalyser(), this); }
    @Override public ChannelSplitterNode createChannelSplitter() { return new TeaVMChannelSplitterNode(jsCtx.createChannelSplitter(), this); }
    @Override public ChannelSplitterNode createChannelSplitter(int numberOfOutputs) { return new TeaVMChannelSplitterNode(jsCtx.createChannelSplitter(numberOfOutputs), this); }
    @Override public ChannelMergerNode createChannelMerger() { return new TeaVMChannelMergerNode(jsCtx.createChannelMerger(), this); }
    @Override public ChannelMergerNode createChannelMerger(int numberOfInputs) { return new TeaVMChannelMergerNode(jsCtx.createChannelMerger(numberOfInputs), this); }
    @Override public PeriodicWave createPeriodicWave(float[] real, float[] imag) {
        return new TeaVMPeriodicWave(jsCtx.createPeriodicWave(JSFloat32Array.fromArray(real), JSFloat32Array.fromArray(imag)));
    }
    @Override public PeriodicWave createPeriodicWave(float[] real, float[] imag, boolean disableNormalization) {
        return new TeaVMPeriodicWave(jsCtx.createPeriodicWaveWithConstraints(JSFloat32Array.fromArray(real), JSFloat32Array.fromArray(imag), disableNormalization));
    }
    @Override public void addWorkletModule(String moduleUrl, Runnable onLoaded, Runnable onError) {
        jsCtx.getAudioWorklet().addModule(moduleUrl, () -> onLoaded.run(), () -> onError.run());
    }
    @Override public AudioWorkletNode createWorkletNode(String processorName) {
        return new TeaVMAudioWorkletNode(JSAudioContext.createWorkletNode(jsCtx, processorName), this);
    }
    @Override public MediaStreamAudioDestinationNode createMediaStreamDestination() {
        return new TeaVMMediaStreamAudioDestinationNode(jsCtx.createMediaStreamDestination(), this);
    }
}
