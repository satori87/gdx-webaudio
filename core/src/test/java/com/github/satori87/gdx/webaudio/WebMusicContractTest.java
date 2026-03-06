package com.github.satori87.gdx.webaudio;

import com.badlogic.gdx.files.FileHandle;
import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;
import com.github.satori87.gdx.webaudio.channel.ChannelMergerNode;
import com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode;
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.source.*;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene3D;
import com.github.satori87.gdx.webaudio.types.AudioContextState;
import com.github.satori87.gdx.webaudio.types.ChannelCountMode;
import com.github.satori87.gdx.webaudio.types.ChannelInterpretation;
import com.github.satori87.gdx.webaudio.types.NoiseType;
import com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebMusicContractTest {

    // ---- Stub AudioParam ----

    static class StubAudioParam implements AudioParam {
        float value;

        StubAudioParam() { this(0f); }
        StubAudioParam(float initial) { this.value = initial; }

        @Override public float getValue() { return value; }
        @Override public void setValue(float value) { this.value = value; }
        @Override public float getDefaultValue() { return 0; }
        @Override public float getMinValue() { return -Float.MAX_VALUE; }
        @Override public float getMaxValue() { return Float.MAX_VALUE; }
        @Override public AudioParam setValueAtTime(float value, double startTime) { return this; }
        @Override public AudioParam linearRampToValueAtTime(float value, double endTime) { return this; }
        @Override public AudioParam exponentialRampToValueAtTime(float value, double endTime) { return this; }
        @Override public AudioParam setTargetAtTime(float target, double startTime, double timeConstant) { return this; }
        @Override public AudioParam setValueCurveAtTime(float[] values, double startTime, double duration) { return this; }
        @Override public AudioParam cancelScheduledValues(double startTime) { return this; }
        @Override public AudioParam cancelAndHoldAtTime(double cancelTime) { return this; }
    }

    // ---- Stub AudioNode base ----

    static class StubAudioNodeBase implements AudioNode {
        @Override public AudioNode connect(AudioNode dest) { return dest; }
        @Override public AudioNode connect(AudioNode dest, int out, int in) { return dest; }
        @Override public void connectParam(AudioParam dest) {}
        @Override public void connectParam(AudioParam dest, int out) {}
        @Override public void disconnect() {}
        @Override public void disconnect(int out) {}
        @Override public void disconnect(AudioNode dest) {}
        @Override public void disconnect(AudioNode dest, int out) {}
        @Override public void disconnect(AudioNode dest, int out, int in) {}
        @Override public void disconnectParam(AudioParam dest) {}
        @Override public void disconnectParam(AudioParam dest, int out) {}
        @Override public int getNumberOfInputs() { return 0; }
        @Override public int getNumberOfOutputs() { return 0; }
        @Override public int getChannelCount() { return 0; }
        @Override public void setChannelCount(int count) {}
        @Override public ChannelCountMode getChannelCountMode() { return null; }
        @Override public void setChannelCountMode(ChannelCountMode mode) {}
        @Override public ChannelInterpretation getChannelInterpretation() { return null; }
        @Override public void setChannelInterpretation(ChannelInterpretation interp) {}
        @Override public WebAudioContext getContext() { return null; }
    }

    // ---- Stub GainNode ----

    static class StubGainNode extends StubAudioNodeBase implements GainNode {
        final StubAudioParam gain = new StubAudioParam(1f);
        @Override public AudioParam getGain() { return gain; }
    }

    // ---- Stub StereoPannerNode ----

    static class StubStereoPannerNode extends StubAudioNodeBase implements StereoPannerNode {
        final StubAudioParam pan = new StubAudioParam(0f);
        @Override public AudioParam getPan() { return pan; }
    }

    // ---- Stub BufferSourceNode ----

    static class StubBufferSourceNode extends StubAudioNodeBase implements AudioBufferSourceNode {
        AudioBuffer bufferSet;
        boolean started;
        boolean stopped;
        boolean loop;
        Runnable onEnded;
        final StubAudioParam playbackRate = new StubAudioParam(1f);

        @Override public void setBuffer(AudioBuffer buffer) { this.bufferSet = buffer; }
        @Override public AudioBuffer getBuffer() { return bufferSet; }
        @Override public AudioParam getPlaybackRate() { return playbackRate; }
        @Override public AudioParam getDetune() { return null; }
        @Override public boolean isLoop() { return loop; }
        @Override public void setLoop(boolean loop) { this.loop = loop; }
        @Override public double getLoopStart() { return 0; }
        @Override public void setLoopStart(double loopStart) {}
        @Override public double getLoopEnd() { return 0; }
        @Override public void setLoopEnd(double loopEnd) {}
        @Override public void start() { this.started = true; }
        @Override public void start(double when) { this.started = true; }
        @Override public void start(double when, double offset) { this.started = true; }
        @Override public void start(double when, double offset, double duration) { this.started = true; }
        @Override public void stop() { this.stopped = true; }
        @Override public void stop(double when) { this.stopped = true; }
        @Override public void setOnEnded(Runnable listener) { this.onEnded = listener; }
    }

    // ---- Stub DestinationNode ----

    static class StubDestinationNode extends StubAudioNodeBase implements AudioDestinationNode {
        @Override public int getMaxChannelCount() { return 2; }
    }

    // ---- Stub Context ----

    static class StubContext implements WebAudioContext {
        final List<StubBufferSourceNode> sources = new ArrayList<>();
        final List<StubGainNode> gains = new ArrayList<>();
        final List<StubStereoPannerNode> panners = new ArrayList<>();
        final StubDestinationNode destination = new StubDestinationNode();
        double currentTime = 0;

        @Override public AudioBufferSourceNode createBufferSource() {
            StubBufferSourceNode n = new StubBufferSourceNode();
            sources.add(n);
            return n;
        }
        @Override public GainNode createGain() {
            StubGainNode n = new StubGainNode();
            gains.add(n);
            return n;
        }
        @Override public StereoPannerNode createStereoPanner() {
            StubStereoPannerNode n = new StubStereoPannerNode();
            panners.add(n);
            return n;
        }
        @Override public AudioDestinationNode getDestination() { return destination; }
        @Override public double getCurrentTime() { return currentTime; }

        // Unused methods
        @Override public AudioContextState getState() { return null; }
        @Override public float getSampleRate() { return 0; }
        @Override public AudioListener getListener() { return null; }
        @Override public void resume(Runnable cb) {}
        @Override public void suspend(Runnable cb) {}
        @Override public void close(Runnable cb) {}
        @Override public void setOnStateChange(Runnable l) {}
        @Override public double getBaseLatency() { return 0; }
        @Override public double getOutputLatency() { return 0; }
        @Override public AudioBuffer createBuffer(int ch, int len, float sr) { return null; }
        @Override public void decodeAudioData(byte[] d, AudioBuffer.DecodeCallback s, Runnable e) {}
        @Override public OscillatorNode createOscillator() { return null; }
        @Override public ConstantSourceNode createConstantSource() { return null; }
        @Override public BiquadFilterNode createBiquadFilter() { return null; }
        @Override public IIRFilterNode createIIRFilter(float[] ff, float[] fb) { return null; }
        @Override public DelayNode createDelay() { return null; }
        @Override public DelayNode createDelay(float max) { return null; }
        @Override public ConvolverNode createConvolver() { return null; }
        @Override public WaveShaperNode createWaveShaper() { return null; }
        @Override public DynamicsCompressorNode createDynamicsCompressor() { return null; }
        @Override public PannerNode createPanner() { return null; }
        @Override public AnalyserNode createAnalyser() { return null; }
        @Override public ChannelSplitterNode createChannelSplitter() { return null; }
        @Override public ChannelSplitterNode createChannelSplitter(int n) { return null; }
        @Override public ChannelMergerNode createChannelMerger() { return null; }
        @Override public ChannelMergerNode createChannelMerger(int n) { return null; }
        @Override public PeriodicWave createPeriodicWave(float[] r, float[] i) { return null; }
        @Override public PeriodicWave createPeriodicWave(float[] r, float[] i, boolean d) { return null; }
        @Override public void addWorkletModule(String url, Runnable ok, Runnable err) {}
        @Override public AudioWorkletNode createWorkletNode(String name) { return null; }
        @Override public MediaStreamAudioDestinationNode createMediaStreamDestination() { return null; }
        @Override public float getMasterVolume() { return 0; }
        @Override public void setMasterVolume(float v) {}
        @Override public NoiseNode createNoise(NoiseType type) { return null; }
        @Override public ChorusNode createChorus() { return null; }
        @Override public FlangerNode createFlanger() { return null; }
        @Override public PhaserNode createPhaser() { return null; }
        @Override public ReverbNode createReverb() { return null; }
        @Override public LimiterNode createLimiter() { return null; }
        @Override public SoundGroup createSoundGroup() { return null; }
        @Override public SpatialAudioScene2D createSpatialScene2D() { return null; }
        @Override public SpatialAudioScene3D createSpatialScene3D() { return null; }
        @Override public void loadSound(FileHandle file, WebSound.LoadCallback onLoaded, Runnable onError) {}
        @Override public void loadMusic(FileHandle file, WebMusic.LoadCallback onLoaded, Runnable onError) {}
    }

    // ---- Lightweight WebMusic implementation mirroring TeaVMWebMusic logic ----

    static class TestWebMusic implements WebMusic {
        private final WebAudioContext context;
        private final AudioBuffer buffer;
        private final GainNode gainNode;
        private final StereoPannerNode pannerNode;
        private AudioNode outputNode;

        private AudioBufferSourceNode currentSource;
        private boolean playing;
        private boolean paused;
        private boolean looping;
        private float volume = 1.0f;
        private boolean stoppedManually;
        private boolean disposed;
        private OnCompletionListener completionListener;

        TestWebMusic(WebAudioContext context, AudioBuffer buffer) {
            this.context = context;
            this.buffer = buffer;
            this.gainNode = context.createGain();
            this.pannerNode = context.createStereoPanner();
            this.outputNode = context.getDestination();
            gainNode.connect(pannerNode);
            pannerNode.connect(outputNode);
        }

        @Override public void play() {
            if (disposed) return;
            if (paused) { paused = false; }
            if (playing) { stopSource(); }
            startPlayback();
        }

        private void startPlayback() {
            stoppedManually = false;
            currentSource = context.createBufferSource();
            currentSource.setBuffer(buffer);
            currentSource.setLoop(looping);
            currentSource.connect(gainNode);
            currentSource.setOnEnded(() -> {
                if (!stoppedManually && !disposed) {
                    playing = false;
                    paused = false;
                    currentSource = null;
                    if (!looping && completionListener != null) {
                        completionListener.onCompletion(this);
                    }
                }
            });
            currentSource.start();
            playing = true;
        }

        private void stopSource() {
            stoppedManually = true;
            if (currentSource != null) {
                try { currentSource.stop(); } catch (Exception ignored) {}
                currentSource = null;
            }
        }

        @Override public void pause() {
            if (!playing || paused || disposed) return;
            stopSource();
            playing = false;
            paused = true;
        }

        @Override public void stop() {
            if (disposed) return;
            stopSource();
            playing = false;
            paused = false;
        }

        @Override public boolean isPlaying() { return playing; }

        @Override public void setLooping(boolean isLooping) {
            this.looping = isLooping;
            if (currentSource != null) {
                currentSource.setLoop(isLooping);
            }
        }

        @Override public boolean isLooping() { return looping; }

        @Override public void setVolume(float volume) {
            this.volume = volume;
            gainNode.getGain().setValue(volume);
        }

        @Override public float getVolume() { return volume; }

        @Override public void setPan(float pan, float volume) {
            this.volume = volume;
            pannerNode.getPan().setValue(pan);
            gainNode.getGain().setValue(volume);
        }

        @Override public float getPosition() { return 0; }
        @Override public void setPosition(float position) {}

        @Override public float getDuration() { return buffer.getDuration(); }

        @Override public void setOutput(AudioNode destination) {
            this.outputNode = destination != null ? destination : context.getDestination();
        }

        @Override public AudioBuffer getBuffer() { return buffer; }

        @Override public void setOnCompletionListener(OnCompletionListener listener) {
            this.completionListener = listener;
        }

        @Override public void dispose() {
            if (disposed) return;
            disposed = true;
            stopSource();
            playing = false;
            paused = false;
        }

        boolean isDisposed() { return disposed; }
    }

    // ---- Test fields ----

    private StubContext context;
    private AudioBuffer buffer;
    private TestWebMusic music;

    @BeforeEach
    void setUp() {
        context = new StubContext();
        buffer = new AudioBuffer() {
            @Override public float getDuration() { return 5f; }
            @Override public int getLength() { return 220500; }
            @Override public int getNumberOfChannels() { return 2; }
            @Override public float getSampleRate() { return 44100f; }
            @Override public float[] getChannelData(int ch) { return new float[0]; }
            @Override public void copyFromChannel(float[] dst, int ch) {}
            @Override public void copyFromChannel(float[] dst, int ch, int off) {}
            @Override public void copyToChannel(float[] src, int ch) {}
            @Override public void copyToChannel(float[] src, int ch, int off) {}
        };
        music = new TestWebMusic(context, buffer);
    }

    // ---- Tests ----

    @Test void initialStateNotPlaying() { assertFalse(music.isPlaying()); }

    @Test void initialStateNotLooping() { assertFalse(music.isLooping()); }

    @Test void initialVolumeIsOne() { assertEquals(1.0f, music.getVolume(), 0.001f); }

    @Test void playSetsPlayingTrue() {
        music.play();
        assertTrue(music.isPlaying());
    }

    @Test void pauseThenIsPlayingReturnsFalse() {
        music.play();
        music.pause();
        assertFalse(music.isPlaying());
    }

    @Test void stopResetsState() {
        music.play();
        music.stop();
        assertFalse(music.isPlaying());
    }

    @Test void setLoopingIsLoopingRoundTrip() {
        music.setLooping(true);
        assertTrue(music.isLooping());
        music.setLooping(false);
        assertFalse(music.isLooping());
    }

    @Test void setVolumeGetVolumeRoundTrip() {
        music.setVolume(0.42f);
        assertEquals(0.42f, music.getVolume(), 0.001f);
        StubGainNode gain = context.gains.get(0);
        assertEquals(0.42f, gain.gain.getValue(), 0.001f);
    }

    @Test void getDurationReturnsBufferDuration() {
        assertEquals(5f, music.getDuration(), 0.001f);
    }

    @Test void getBufferReturnsBuffer() {
        assertSame(buffer, music.getBuffer());
    }

    @Test void disposeStopsPlayback() {
        music.play();
        music.dispose();
        assertFalse(music.isPlaying());
        assertTrue(music.isDisposed());
    }
}
