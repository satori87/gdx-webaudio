package com.github.satori87.gdx.webaudio;

import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.types.ChannelCountMode;
import com.github.satori87.gdx.webaudio.types.ChannelInterpretation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SoundPoolTest {

    /** Minimal stub that tracks calls relevant to SoundPool tests. */
    static class StubBufferSourceNode implements AudioBufferSourceNode {
        AudioBuffer bufferSet;
        AudioNode connectedTo;
        boolean started;
        Runnable onEnded;

        @Override public void setBuffer(AudioBuffer buffer) { this.bufferSet = buffer; }
        @Override public AudioBuffer getBuffer() { return bufferSet; }
        @Override public AudioNode connect(AudioNode destination) { this.connectedTo = destination; return destination; }
        @Override public void start() { this.started = true; }
        @Override public void setOnEnded(Runnable listener) { this.onEnded = listener; }

        // Unused methods
        @Override public AudioParam getPlaybackRate() { return null; }
        @Override public AudioParam getDetune() { return null; }
        @Override public boolean isLoop() { return false; }
        @Override public void setLoop(boolean loop) {}
        @Override public double getLoopStart() { return 0; }
        @Override public void setLoopStart(double loopStart) {}
        @Override public double getLoopEnd() { return 0; }
        @Override public void setLoopEnd(double loopEnd) {}
        @Override public void start(double when) {}
        @Override public void start(double when, double offset) {}
        @Override public void start(double when, double offset, double duration) {}
        @Override public void stop() {}
        @Override public void stop(double when) {}
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

    /** Minimal stub context that creates StubBufferSourceNodes. */
    static class StubContext implements WebAudioContext {
        final List<StubBufferSourceNode> created = new ArrayList<>();

        @Override public AudioBufferSourceNode createBufferSource() {
            StubBufferSourceNode node = new StubBufferSourceNode();
            created.add(node);
            return node;
        }

        // Unused methods — return null / no-op
        @Override public com.github.satori87.gdx.webaudio.types.AudioContextState getState() { return null; }
        @Override public double getCurrentTime() { return 0; }
        @Override public float getSampleRate() { return 0; }
        @Override public AudioDestinationNode getDestination() { return null; }
        @Override public AudioListener getListener() { return null; }
        @Override public void resume(Runnable cb) {}
        @Override public void suspend(Runnable cb) {}
        @Override public void close(Runnable cb) {}
        @Override public void setOnStateChange(Runnable l) {}
        @Override public double getBaseLatency() { return 0; }
        @Override public double getOutputLatency() { return 0; }
        @Override public AudioBuffer createBuffer(int ch, int len, float sr) { return null; }
        @Override public void decodeAudioData(byte[] d, AudioBuffer.DecodeCallback s, Runnable e) {}
        @Override public com.github.satori87.gdx.webaudio.source.OscillatorNode createOscillator() { return null; }
        @Override public com.github.satori87.gdx.webaudio.source.ConstantSourceNode createConstantSource() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.GainNode createGain() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.BiquadFilterNode createBiquadFilter() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.IIRFilterNode createIIRFilter(float[] ff, float[] fb) { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.DelayNode createDelay() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.DelayNode createDelay(float max) { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.ConvolverNode createConvolver() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.WaveShaperNode createWaveShaper() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.DynamicsCompressorNode createDynamicsCompressor() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.StereoPannerNode createStereoPanner() { return null; }
        @Override public com.github.satori87.gdx.webaudio.spatial.PannerNode createPanner() { return null; }
        @Override public com.github.satori87.gdx.webaudio.analysis.AnalyserNode createAnalyser() { return null; }
        @Override public com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode createChannelSplitter() { return null; }
        @Override public com.github.satori87.gdx.webaudio.channel.ChannelSplitterNode createChannelSplitter(int n) { return null; }
        @Override public com.github.satori87.gdx.webaudio.channel.ChannelMergerNode createChannelMerger() { return null; }
        @Override public com.github.satori87.gdx.webaudio.channel.ChannelMergerNode createChannelMerger(int n) { return null; }
        @Override public PeriodicWave createPeriodicWave(float[] r, float[] i) { return null; }
        @Override public PeriodicWave createPeriodicWave(float[] r, float[] i, boolean d) { return null; }
        @Override public void addWorkletModule(String url, Runnable ok, Runnable err) {}
        @Override public com.github.satori87.gdx.webaudio.worklet.AudioWorkletNode createWorkletNode(String name) { return null; }
        @Override public com.github.satori87.gdx.webaudio.source.MediaStreamAudioDestinationNode createMediaStreamDestination() { return null; }
        @Override public float getMasterVolume() { return 0; }
        @Override public void setMasterVolume(float v) {}
        @Override public com.github.satori87.gdx.webaudio.source.NoiseNode createNoise(com.github.satori87.gdx.webaudio.types.NoiseType type) { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.ChorusNode createChorus() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.FlangerNode createFlanger() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.PhaserNode createPhaser() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.ReverbNode createReverb() { return null; }
        @Override public com.github.satori87.gdx.webaudio.effect.LimiterNode createLimiter() { return null; }
        @Override public SoundGroup createSoundGroup() { return null; }
        @Override public com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D createSpatialScene2D() { return null; }
        @Override public com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene3D createSpatialScene3D() { return null; }
    }

    /** Minimal stub used as a destination node. */
    static class StubDestinationNode implements AudioNode {
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

    private StubContext context;
    private AudioBuffer buffer;
    private SoundPool pool;

    @BeforeEach
    void setUp() {
        context = new StubContext();
        buffer = new AudioBuffer() {
            @Override public float getDuration() { return 1f; }
            @Override public int getLength() { return 44100; }
            @Override public int getNumberOfChannels() { return 1; }
            @Override public float getSampleRate() { return 44100f; }
            @Override public float[] getChannelData(int ch) { return new float[0]; }
            @Override public void copyFromChannel(float[] dst, int ch) {}
            @Override public void copyFromChannel(float[] dst, int ch, int off) {}
            @Override public void copyToChannel(float[] src, int ch) {}
            @Override public void copyToChannel(float[] src, int ch, int off) {}
        };
        pool = new SoundPool(context, buffer);
    }

    @Test void constructorStoresContext() {
        assertSame(context, pool.getContext());
    }

    @Test void constructorStoresBuffer() {
        assertSame(buffer, pool.getBuffer());
    }

    @Test void obtainCreatesSourceViaContext() {
        AudioBufferSourceNode node = pool.obtain();
        assertNotNull(node);
        assertEquals(1, context.created.size());
        assertSame(node, context.created.get(0));
    }

    @Test void obtainSetsBufferOnSource() {
        AudioBufferSourceNode node = pool.obtain();
        StubBufferSourceNode stub = (StubBufferSourceNode) node;
        assertSame(buffer, stub.bufferSet);
    }

    @Test void freeDoesNotReturnToPool() {
        AudioBufferSourceNode first = pool.obtain();
        pool.free(first);
        // A second obtain should create a new object, not reuse the freed one
        AudioBufferSourceNode second = pool.obtain();
        assertEquals(2, context.created.size());
        assertNotSame(first, second);
    }

    @Test void obtainWithDestinationConnects() {
        StubDestinationNode dest = new StubDestinationNode();
        AudioBufferSourceNode node = pool.obtain(dest);
        StubBufferSourceNode stub = (StubBufferSourceNode) node;
        assertSame(dest, stub.connectedTo);
    }

    @Test void obtainWithDestinationSetsBuffer() {
        StubDestinationNode dest = new StubDestinationNode();
        AudioBufferSourceNode node = pool.obtain(dest);
        StubBufferSourceNode stub = (StubBufferSourceNode) node;
        assertSame(buffer, stub.bufferSet);
    }

    @Test void playConnectsAndStarts() {
        StubDestinationNode dest = new StubDestinationNode();
        AudioBufferSourceNode node = pool.play(dest);
        StubBufferSourceNode stub = (StubBufferSourceNode) node;
        assertSame(dest, stub.connectedTo);
        assertTrue(stub.started);
    }

    @Test void playSetsOnEnded() {
        StubDestinationNode dest = new StubDestinationNode();
        AudioBufferSourceNode node = pool.play(dest);
        StubBufferSourceNode stub = (StubBufferSourceNode) node;
        assertNotNull(stub.onEnded);
    }

    @Test void multipleObtainsCreateMultipleSources() {
        pool.obtain();
        pool.obtain();
        pool.obtain();
        assertEquals(3, context.created.size());
    }
}
