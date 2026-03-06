package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.*;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TeaVM/browser implementation of {@link WebSound}.
 *
 * <p>Manages multiple concurrent playback instances, each with its own
 * {@link AudioBufferSourceNode}, {@link GainNode}, and {@link StereoPannerNode}.
 * Instances are tracked by unique long IDs and cleaned up automatically
 * when playback ends.</p>
 */
public class TeaVMWebSound implements WebSound {
    private final WebAudioContext context;
    private final AudioBuffer buffer;
    private final Map<Long, SoundInstance> instances = new HashMap<>();
    private AudioNode outputNode;
    private long nextId = 1;
    private boolean disposed = false;

    private static class SoundInstance {
        AudioBufferSourceNode source;
        GainNode gain;
        StereoPannerNode panner;
        float volume;
        float pitch;
        float pan;
        boolean looping;
        boolean paused;
        double pauseOffset;
        double startTime;
    }

    public TeaVMWebSound(WebAudioContext context, AudioBuffer buffer) {
        this.context = context;
        this.buffer = buffer;
        this.outputNode = context.getDestination();
    }

    @Override
    public long play() { return play(1f); }

    @Override
    public long play(float volume) { return play(volume, 1f, 0f); }

    @Override
    public long play(float volume, float pitch, float pan) {
        return startInstance(volume, pitch, pan, false);
    }

    @Override
    public long loop() { return loop(1f); }

    @Override
    public long loop(float volume) { return loop(volume, 1f, 0f); }

    @Override
    public long loop(float volume, float pitch, float pan) {
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
        inst.paused = false;
        inst.pauseOffset = 0;
        inst.startTime = context.getCurrentTime();

        inst.gain = context.createGain();
        inst.gain.getGain().setValue(volume);

        inst.panner = context.createStereoPanner();
        inst.panner.getPan().setValue(pan);

        inst.gain.connect(inst.panner);
        inst.panner.connect(outputNode);

        inst.source = createSource(inst, id);
        inst.source.start();

        instances.put(id, inst);
        return id;
    }

    private AudioBufferSourceNode createSource(SoundInstance inst, long id) {
        AudioBufferSourceNode source = context.createBufferSource();
        source.setBuffer(buffer);
        source.setLoop(inst.looping);
        source.getPlaybackRate().setValue(inst.pitch);
        source.connect(inst.gain);
        source.setOnEnded(() -> {
            if (!inst.paused) {
                cleanupInstance(id);
            }
        });
        return source;
    }

    private void cleanupInstance(long id) {
        SoundInstance inst = instances.remove(id);
        if (inst != null) {
            try { inst.panner.disconnect(); } catch (Exception ignored) {}
            try { inst.gain.disconnect(); } catch (Exception ignored) {}
        }
    }

    @Override
    public void stop() {
        List<Long> ids = new ArrayList<>(instances.keySet());
        for (Long id : ids) stop(id);
    }

    @Override
    public void stop(long soundId) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null) return;
        inst.paused = false;
        try { inst.source.stop(); } catch (Exception ignored) {}
        cleanupInstance(soundId);
    }

    @Override
    public void pause() {
        for (Long id : new ArrayList<>(instances.keySet())) pause(id);
    }

    @Override
    public void pause(long soundId) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null || inst.paused) return;
        double elapsed = (context.getCurrentTime() - inst.startTime) * inst.pitch;
        inst.pauseOffset += elapsed;
        if (inst.looping && buffer.getDuration() > 0) {
            inst.pauseOffset = inst.pauseOffset % buffer.getDuration();
        }
        inst.paused = true;
        try { inst.source.stop(); } catch (Exception ignored) {}
    }

    @Override
    public void resume() {
        for (Long id : new ArrayList<>(instances.keySet())) resume(id);
    }

    @Override
    public void resume(long soundId) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null || !inst.paused) return;
        inst.paused = false;
        inst.startTime = context.getCurrentTime();
        inst.source = createSource(inst, soundId);
        inst.source.start(0, inst.pauseOffset);
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null) return;
        inst.looping = looping;
        inst.source.setLoop(looping);
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null) return;
        inst.pitch = pitch;
        inst.source.getPlaybackRate().setValue(pitch);
    }

    @Override
    public void setVolume(long soundId, float volume) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null) return;
        inst.volume = volume;
        inst.gain.getGain().setValue(volume);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        SoundInstance inst = instances.get(soundId);
        if (inst == null) return;
        inst.pan = pan;
        inst.volume = volume;
        inst.panner.getPan().setValue(pan);
        inst.gain.getGain().setValue(volume);
    }

    @Override
    public void setOutput(AudioNode destination) {
        this.outputNode = destination != null ? destination : context.getDestination();
    }

    @Override
    public AudioBuffer getBuffer() { return buffer; }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        stop();
    }
}
