package com.github.satori87.gdx.webaudio;

import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;
import com.github.satori87.gdx.webaudio.channel.ChannelMergerNode;
import com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode;
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.source.*;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene3D;
import com.github.satori87.gdx.webaudio.types.AudioContextState;
import com.github.satori87.gdx.webaudio.types.NoiseType;
import com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode;

/**
 * Main audio context interface representing a real-time audio processing graph.
 * Maps to the Web Audio API {@code AudioContext} interface.
 * <p>
 * Provides factory methods for creating all types of audio nodes, as well as
 * methods for managing the context lifecycle, decoding audio data, and querying
 * context state and timing information.
 *
 * @see AudioContext (Web Audio API)
 */
public interface WebAudioContext {

    // -- State --

    /**
     * Returns the current state of the audio context.
     *
     * @return the current {@link AudioContextState}
     */
    AudioContextState getState();

    /**
     * Returns the current time of the audio context in seconds, advancing in real time.
     *
     * @return the current context time in seconds
     */
    double getCurrentTime();

    /**
     * Returns the sample rate of the audio context in Hz.
     *
     * @return the sample rate in Hz
     */
    float getSampleRate();

    /**
     * Returns the destination node representing the final audio output.
     *
     * @return the audio destination node
     */
    AudioDestinationNode getDestination();

    /**
     * Returns the listener used for 3D audio spatialization.
     *
     * @return the audio listener
     */
    AudioListener getListener();

    // -- Lifecycle --

    /**
     * Resumes a suspended audio context, enabling audio processing.
     *
     * @param onResumed callback invoked when the context has resumed, or {@code null}
     */
    void resume(Runnable onResumed);

    /**
     * Suspends the audio context, temporarily halting audio processing.
     *
     * @param onSuspended callback invoked when the context has suspended, or {@code null}
     */
    void suspend(Runnable onSuspended);

    /**
     * Closes the audio context, releasing all resources. The context cannot be reused after closing.
     *
     * @param onClosed callback invoked when the context has closed, or {@code null}
     */
    void close(Runnable onClosed);

    /**
     * Registers a listener to be called whenever the context state changes.
     *
     * @param listener the callback to invoke on state change, or {@code null} to remove
     */
    void setOnStateChange(Runnable listener);

    // -- Latency --

    /**
     * Returns the base latency of the audio context in seconds, representing the processing delay
     * of the audio graph.
     *
     * @return the base latency in seconds
     */
    double getBaseLatency();

    /**
     * Returns the output latency of the audio context in seconds, representing the delay
     * from the audio output to the speakers.
     *
     * @return the output latency in seconds
     */
    double getOutputLatency();

    // -- Buffer --

    /**
     * Creates an empty audio buffer with the specified parameters.
     *
     * @param numberOfChannels the number of channels (e.g., 1 for mono, 2 for stereo)
     * @param length           the buffer length in sample frames
     * @param sampleRate       the sample rate in Hz
     * @return a new empty {@link AudioBuffer}
     */
    AudioBuffer createBuffer(int numberOfChannels, int length, float sampleRate);

    /**
     * Asynchronously decodes encoded audio data (e.g., MP3, OGG) into an {@link AudioBuffer}.
     *
     * @param audioData the encoded audio data as a byte array
     * @param onSuccess callback invoked with the decoded buffer on success
     * @param onError   callback invoked if decoding fails
     */
    void decodeAudioData(byte[] audioData, AudioBuffer.DecodeCallback onSuccess, Runnable onError);

    // -- Source nodes --

    /**
     * Creates a new oscillator node that generates a periodic waveform.
     *
     * @return a new {@link OscillatorNode}
     */
    OscillatorNode createOscillator();

    /**
     * Creates a new audio buffer source node for playing back an {@link AudioBuffer}.
     *
     * @return a new {@link AudioBufferSourceNode}
     */
    AudioBufferSourceNode createBufferSource();

    /**
     * Creates a new constant source node that outputs a constant value.
     *
     * @return a new {@link ConstantSourceNode}
     */
    ConstantSourceNode createConstantSource();

    // -- Effect nodes --

    /**
     * Creates a new gain node for controlling audio volume.
     *
     * @return a new {@link GainNode}
     */
    GainNode createGain();

    /**
     * Creates a new biquad filter node for common audio filtering operations.
     *
     * @return a new {@link BiquadFilterNode}
     */
    BiquadFilterNode createBiquadFilter();

    /**
     * Creates a new IIR filter node with the specified feedforward and feedback coefficients.
     *
     * @param feedforward the feedforward (numerator) coefficients
     * @param feedback    the feedback (denominator) coefficients
     * @return a new {@link IIRFilterNode}
     */
    IIRFilterNode createIIRFilter(float[] feedforward, float[] feedback);

    /**
     * Creates a new delay node with a default maximum delay time of 1 second.
     *
     * @return a new {@link DelayNode}
     */
    DelayNode createDelay();

    /**
     * Creates a new delay node with the specified maximum delay time.
     *
     * @param maxDelayTime the maximum delay time in seconds
     * @return a new {@link DelayNode}
     */
    DelayNode createDelay(float maxDelayTime);

    /**
     * Creates a new convolver node for applying convolution reverb effects.
     *
     * @return a new {@link ConvolverNode}
     */
    ConvolverNode createConvolver();

    /**
     * Creates a new wave shaper node for applying nonlinear distortion.
     *
     * @return a new {@link WaveShaperNode}
     */
    WaveShaperNode createWaveShaper();

    /**
     * Creates a new dynamics compressor node for controlling audio dynamic range.
     *
     * @return a new {@link DynamicsCompressorNode}
     */
    DynamicsCompressorNode createDynamicsCompressor();

    /**
     * Creates a new stereo panner node for panning audio between left and right channels.
     *
     * @return a new {@link StereoPannerNode}
     */
    StereoPannerNode createStereoPanner();

    // -- Spatial --

    /**
     * Creates a new panner node for 3D spatial audio positioning.
     *
     * @return a new {@link PannerNode}
     */
    PannerNode createPanner();

    // -- Analysis --

    /**
     * Creates a new analyser node for extracting frequency and time-domain data.
     *
     * @return a new {@link AnalyserNode}
     */
    AnalyserNode createAnalyser();

    // -- Channel --

    /**
     * Creates a new channel splitter node with a default of 6 outputs.
     *
     * @return a new {@link ChannelSplitterNode}
     */
    ChannelSplitterNode createChannelSplitter();

    /**
     * Creates a new channel splitter node with the specified number of outputs.
     *
     * @param numberOfOutputs the number of output channels
     * @return a new {@link ChannelSplitterNode}
     */
    ChannelSplitterNode createChannelSplitter(int numberOfOutputs);

    /**
     * Creates a new channel merger node with a default of 6 inputs.
     *
     * @return a new {@link ChannelMergerNode}
     */
    ChannelMergerNode createChannelMerger();

    /**
     * Creates a new channel merger node with the specified number of inputs.
     *
     * @param numberOfInputs the number of input channels
     * @return a new {@link ChannelMergerNode}
     */
    ChannelMergerNode createChannelMerger(int numberOfInputs);

    // -- Waveform --

    /**
     * Creates a periodic wave from real and imaginary frequency-domain components.
     *
     * @param real the real (cosine) coefficients of the Fourier series
     * @param imag the imaginary (sine) coefficients of the Fourier series
     * @return a new {@link PeriodicWave}
     */
    PeriodicWave createPeriodicWave(float[] real, float[] imag);

    /**
     * Creates a periodic wave from real and imaginary frequency-domain components,
     * with optional normalization control.
     *
     * @param real                  the real (cosine) coefficients of the Fourier series
     * @param imag                  the imaginary (sine) coefficients of the Fourier series
     * @param disableNormalization  if {@code true}, the waveform is not normalized to [-1, 1]
     * @return a new {@link PeriodicWave}
     */
    PeriodicWave createPeriodicWave(float[] real, float[] imag, boolean disableNormalization);

    // -- Worklet --

    /**
     * Loads an AudioWorklet module from the specified URL.
     *
     * @param moduleUrl the URL of the AudioWorklet JavaScript module
     * @param onLoaded  callback invoked when the module has been loaded successfully
     * @param onError   callback invoked if loading fails
     */
    void addWorkletModule(String moduleUrl, Runnable onLoaded, Runnable onError);

    /**
     * Creates a new AudioWorklet node using a previously loaded processor.
     *
     * @param processorName the name of the registered AudioWorkletProcessor
     * @return a new {@link AudioWorkletNode}
     */
    AudioWorkletNode createWorkletNode(String processorName);

    // -- Media --

    /**
     * Creates a media stream destination node for capturing audio output as a media stream.
     *
     * @return a new {@link MediaStreamAudioDestinationNode}
     */
    MediaStreamAudioDestinationNode createMediaStreamDestination();

    // -- Master volume --

    /**
     * Returns the master volume level for the entire context.
     *
     * @return the master volume (1.0 = full, 0.0 = silent)
     */
    float getMasterVolume();

    /**
     * Sets the master volume level for the entire context.
     * All audio routed to the destination is scaled by this value.
     *
     * @param volume the master volume level
     */
    void setMasterVolume(float volume);

    // -- Noise --

    /**
     * Creates a noise source node of the specified type.
     *
     * @param type the type of noise to generate
     * @return a new {@link NoiseNode}
     */
    NoiseNode createNoise(NoiseType type);

    // -- Composite effects --

    /**
     * Creates a chorus effect node.
     *
     * @return a new {@link ChorusNode}
     */
    ChorusNode createChorus();

    /**
     * Creates a flanger effect node.
     *
     * @return a new {@link FlangerNode}
     */
    FlangerNode createFlanger();

    /**
     * Creates a phaser effect node.
     *
     * @return a new {@link PhaserNode}
     */
    PhaserNode createPhaser();

    /**
     * Creates a parametric reverb effect node.
     *
     * @return a new {@link ReverbNode}
     */
    ReverbNode createReverb();

    /**
     * Creates a limiter effect node.
     *
     * @return a new {@link LimiterNode}
     */
    LimiterNode createLimiter();

    // -- Sound group --

    /**
     * Creates a sound group (mixing bus) for grouping audio sources with shared controls.
     *
     * @return a new {@link SoundGroup}
     */
    SoundGroup createSoundGroup();

    // -- Spatial scenes --

    /**
     * Creates a 2D spatial audio scene with listener positioning and source management.
     *
     * @return a new {@link SpatialAudioScene2D}
     */
    SpatialAudioScene2D createSpatialScene2D();

    /**
     * Creates a 3D spatial audio scene with listener positioning and source management.
     *
     * @return a new {@link SpatialAudioScene3D}
     */
    SpatialAudioScene3D createSpatialScene3D();
}
