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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebSoundContractTest {

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

    // ---- Stub AudioNode base (no-op for all AudioNode methods) ----

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

    // ---- Lightweight WebSound implementation mirroring TeaVMWebSound logic ----

    static class TestWebSound implements WebSound {
        private final WebAudioContext context;
        private final AudioBuffer buffer;
        private final Map<Long, SoundInstance> instances = new HashMap<>();
        private AudioNode outputNode;
        private long nextId = 1;
        private boolean disposed = false;

        static class SoundInstance {
            AudioBufferSourceNode source;
            GainNode gain;
            StereoPannerNode panner;
            float volume;
            float pitch;
            float pan;
            boolean looping;
        }

        TestWebSound(WebAudioContext context, AudioBuffer buffer) {
            this.context = context;
            this.buffer = buffer;
            this.outputNode = context.getDestination();
        }

        @Override public long play() { return play(1f); }
        @Override public long play(float volume) { return play(volume, 1f, 0f); }
        @Override public long play(float volume, float pitch, float pan) {
            return startInstance(volume, pitch, pan, false);
        }

        @Override public long loop() { return loop(1f); }
        @Override public long loop(float volume) { return loop(volume, 1f, 0f); }
        @Override public long loop(float volume, float pitch, float pan) {
            return startInstance(volume, pitch, pan, true);
        }

        private long startInstance(float volume, float pitch, float pan, boolean looping) {
            if (disposed) return -1;
            long id = nextId++;

            SoundInstance inst = new SoundInstance();
            inst.volume = volume;
            inst.pitch = pitch;
            inst.pan = pan;
            inst.looping = looping;

            inst.gain = context.createGain();
            inst.gain.getGain().setValue(volume);

            inst.panner = context.createStereoPanner();
            inst.panner.getPan().setValue(pan);

            inst.gain.connect(inst.panner);
            inst.panner.connect(outputNode);

            inst.source = context.createBufferSource();
            inst.source.setBuffer(buffer);
            inst.source.setLoop(looping);
            inst.source.getPlaybackRate().setValue(pitch);
            inst.source.connect(inst.gain);
            inst.source.start();

            instances.put(id, inst);
            return id;
        }

        @Override public void stop() {
            List<Long> ids = new ArrayList<>(instances.keySet());
            for (Long id : ids) stop(id);
        }

        @Override public void stop(long soundId) {
            SoundInstance inst = instances.remove(soundId);
            if (inst == null) return;
            try { inst.source.stop(); } catch (Exception ignored) {}
        }

        @Override public void pause() {}
        @Override public void pause(long soundId) {}
        @Override public void resume() {}
        @Override public void resume(long soundId) {}

        @Override public void setLooping(long soundId, boolean looping) {
            SoundInstance inst = instances.get(soundId);
            if (inst == null) return;
            inst.looping = looping;
            inst.source.setLoop(looping);
        }

        @Override public void setPitch(long soundId, float pitch) {
            SoundInstance inst = instances.get(soundId);
            if (inst == null) return;
            inst.pitch = pitch;
            inst.source.getPlaybackRate().setValue(pitch);
        }

        @Override public void setVolume(long soundId, float volume) {
            SoundInstance inst = instances.get(soundId);
            if (inst == null) return;
            inst.volume = volume;
            inst.gain.getGain().setValue(volume);
        }

        @Override public void setPan(long soundId, float pan, float volume) {
            SoundInstance inst = instances.get(soundId);
            if (inst == null) return;
            inst.pan = pan;
            inst.volume = volume;
            inst.panner.getPan().setValue(pan);
            inst.gain.getGain().setValue(volume);
        }

        @Override public void setOutput(AudioNode destination) {
            this.outputNode = destination != null ? destination : context.getDestination();
        }

        @Override public AudioBuffer getBuffer() { return buffer; }

        @Override public void dispose() {
            if (disposed) return;
            disposed = true;
            stop();
        }

        boolean isDisposed() { return disposed; }
        int instanceCount() { return instances.size(); }
    }

    // ---- Test fields ----

    private StubContext context;
    private AudioBuffer buffer;
    private TestWebSound sound;

    @BeforeEach
    void setUp() {
        context = new StubContext();
        buffer = new AudioBuffer() {
            @Override public float getDuration() { return 2f; }
            @Override public int getLength() { return 88200; }
            @Override public int getNumberOfChannels() { return 1; }
            @Override public float getSampleRate() { return 44100f; }
            @Override public float[] getChannelData(int ch) { return new float[0]; }
            @Override public void copyFromChannel(float[] dst, int ch) {}
            @Override public void copyFromChannel(float[] dst, int ch, int off) {}
            @Override public void copyToChannel(float[] src, int ch) {}
            @Override public void copyToChannel(float[] src, int ch, int off) {}
        };
        sound = new TestWebSound(context, buffer);
    }

    // ---- Tests ----

    @Test void playReturnsUniqueIncrementingIds() {
        long id1 = sound.play();
        long id2 = sound.play();
        long id3 = sound.play();
        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test void playReturnsNonNegativeId() {
        assertNotEquals(-1, sound.play());
    }

    @Test void playWithParamsCreatesSourceWithCorrectSettings() {
        long id = sound.play(0.5f, 2f, -0.75f);
        assertTrue(id > 0);
        StubGainNode gain = context.gains.get(0);
        StubStereoPannerNode panner = context.panners.get(0);
        StubBufferSourceNode source = context.sources.get(0);
        assertEquals(0.5f, gain.gain.getValue(), 0.001f);
        assertEquals(-0.75f, panner.pan.getValue(), 0.001f);
        assertEquals(2f, source.playbackRate.getValue(), 0.001f);
        assertTrue(source.started);
    }

    @Test void stopByIdStopsSpecificInstance() {
        long id1 = sound.play();
        long id2 = sound.play();
        sound.stop(id1);
        StubBufferSourceNode source1 = context.sources.get(0);
        assertTrue(source1.stopped);
        assertEquals(1, sound.instanceCount());
    }

    @Test void stopAllStopsAllInstances() {
        sound.play();
        sound.play();
        sound.play();
        assertEquals(3, sound.instanceCount());
        sound.stop();
        assertEquals(0, sound.instanceCount());
        for (StubBufferSourceNode src : context.sources) {
            assertTrue(src.stopped);
        }
    }

    @Test void setVolumeUpdatesGain() {
        long id = sound.play(1f);
        sound.setVolume(id, 0.3f);
        StubGainNode gain = context.gains.get(0);
        assertEquals(0.3f, gain.gain.getValue(), 0.001f);
    }

    @Test void setPitchUpdatesPlaybackRate() {
        long id = sound.play();
        sound.setPitch(id, 1.5f);
        StubBufferSourceNode source = context.sources.get(0);
        assertEquals(1.5f, source.playbackRate.getValue(), 0.001f);
    }

    @Test void disposeStopsAllInstances() {
        sound.play();
        sound.play();
        sound.dispose();
        assertEquals(0, sound.instanceCount());
        assertTrue(sound.isDisposed());
    }

    @Test void loopSetsLoopingOnSource() {
        long id = sound.loop();
        assertTrue(id > 0);
        StubBufferSourceNode source = context.sources.get(0);
        assertTrue(source.loop);
    }

    @Test void getBufferReturnsBuffer() {
        assertSame(buffer, sound.getBuffer());
    }

    @Test void playAfterDisposeReturnsNegativeOne() {
        sound.dispose();
        assertEquals(-1, sound.play());
    }
}
