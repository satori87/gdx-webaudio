package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.*;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * TeaVM/browser implementation of {@link WebMusic}.
 *
 * <p>Manages a single playback instance with persistent {@link GainNode} and
 * {@link StereoPannerNode} for volume and pan control. The underlying
 * {@link AudioBufferSourceNode} is recreated on each play, resume, or seek
 * because Web Audio source nodes are one-shot.</p>
 */
public class TeaVMWebMusic implements WebMusic {
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
    private float pan = 0f;
    private double startContextTime;
    private double startOffset;
    private boolean stoppedManually;
    private boolean disposed;
    private OnCompletionListener completionListener;

    public TeaVMWebMusic(WebAudioContext context, AudioBuffer buffer) {
        this.context = context;
        this.buffer = buffer;
        this.gainNode = context.createGain();
        this.pannerNode = context.createStereoPanner();
        this.outputNode = context.getDestination();
        gainNode.connect(pannerNode);
        pannerNode.connect(outputNode);
    }

    @Override
    public void play() {
        if (disposed) return;
        if (paused) {
            paused = false;
            startPlayback(startOffset);
            return;
        }
        if (playing) {
            stopSource();
        }
        startPlayback(startOffset);
    }

    private void startPlayback(double offset) {
        stoppedManually = false;
        currentSource = context.createBufferSource();
        currentSource.setBuffer(buffer);
        currentSource.setLoop(looping);
        currentSource.connect(gainNode);
        currentSource.setOnEnded(() -> {
            if (!stoppedManually && !disposed) {
                playing = false;
                paused = false;
                startOffset = 0;
                currentSource = null;
                if (!looping && completionListener != null) {
                    completionListener.onCompletion(this);
                }
            }
        });
        startContextTime = context.getCurrentTime();
        startOffset = offset;
        currentSource.start(0, offset);
        playing = true;
    }

    private void stopSource() {
        stoppedManually = true;
        if (currentSource != null) {
            try { currentSource.stop(); } catch (Exception ignored) {}
            currentSource = null;
        }
    }

    @Override
    public void pause() {
        if (!playing || paused || disposed) return;
        double elapsed = context.getCurrentTime() - startContextTime;
        startOffset += elapsed;
        if (looping && buffer.getDuration() > 0) {
            startOffset = startOffset % buffer.getDuration();
        }
        stopSource();
        playing = false;
        paused = true;
    }

    @Override
    public void stop() {
        if (disposed) return;
        stopSource();
        playing = false;
        paused = false;
        startOffset = 0;
    }

    @Override
    public boolean isPlaying() { return playing; }

    @Override
    public void setLooping(boolean isLooping) {
        this.looping = isLooping;
        if (currentSource != null) {
            currentSource.setLoop(isLooping);
        }
    }

    @Override
    public boolean isLooping() { return looping; }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        gainNode.getGain().setValue(volume);
    }

    @Override
    public float getVolume() { return volume; }

    @Override
    public void setPan(float pan, float volume) {
        this.pan = pan;
        this.volume = volume;
        pannerNode.getPan().setValue(pan);
        gainNode.getGain().setValue(volume);
    }

    @Override
    public float getPosition() {
        if (playing) {
            double elapsed = context.getCurrentTime() - startContextTime;
            double pos = startOffset + elapsed;
            float duration = buffer.getDuration();
            if (looping && duration > 0) {
                pos = pos % duration;
            }
            return (float) pos;
        }
        if (paused) {
            return (float) startOffset;
        }
        return 0;
    }

    @Override
    public void setPosition(float position) {
        if (disposed) return;
        double clamped = Math.max(0, Math.min(position, buffer.getDuration()));
        if (playing) {
            stopSource();
            startPlayback(clamped);
        } else {
            startOffset = clamped;
        }
    }

    @Override
    public float getDuration() { return buffer.getDuration(); }

    @Override
    public void setOutput(AudioNode destination) {
        AudioNode target = destination != null ? destination : context.getDestination();
        pannerNode.disconnect();
        pannerNode.connect(target);
        this.outputNode = target;
    }

    @Override
    public AudioBuffer getBuffer() { return buffer; }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.completionListener = listener;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        stop();
        try { pannerNode.disconnect(); } catch (Exception ignored) {}
        try { gainNode.disconnect(); } catch (Exception ignored) {}
    }
}
