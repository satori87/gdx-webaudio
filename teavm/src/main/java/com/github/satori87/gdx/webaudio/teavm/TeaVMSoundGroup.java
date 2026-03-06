package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.SoundGroup;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;

/**
 * TeaVM/browser implementation of {@link SoundGroup}.
 *
 * <p>Routes audio through a {@link StereoPannerNode} and {@link GainNode} to provide
 * shared volume, pan, and fade controls for grouped audio sources.</p>
 */
public class TeaVMSoundGroup implements SoundGroup {
    private final WebAudioContext context;
    private final StereoPannerNode panner;
    private final GainNode volumeGain;
    private float pan = 0f;

    public TeaVMSoundGroup(WebAudioContext context) {
        this.context = context;
        this.panner = context.createStereoPanner();
        this.volumeGain = context.createGain();
        panner.connect(volumeGain);
    }

    @Override public void setVolume(float volume) {
        volumeGain.getGain().cancelScheduledValues(0);
        volumeGain.getGain().setValueAtTime(volume, 0);
    }
    @Override public float getVolume() { return volumeGain.getGain().getValue(); }
    @Override public void setPan(float pan) {
        this.pan = pan;
        panner.getPan().cancelScheduledValues(0);
        panner.getPan().setValueAtTime(pan, 0);
    }
    @Override public float getPan() { return pan; }

    @Override public void fadeIn(float milliseconds) { fadeIn(milliseconds, 1.0f); }
    @Override public void fadeIn(float milliseconds, float targetVolume) {
        double now = context.getCurrentTime();
        volumeGain.getGain().cancelScheduledValues(now);
        volumeGain.getGain().setValueAtTime(0, now);
        volumeGain.getGain().linearRampToValueAtTime(targetVolume, now + milliseconds / 1000.0);
    }
    @Override public void fadeOut(float milliseconds) { fadeOut(milliseconds, 0.0f); }
    @Override public void fadeOut(float milliseconds, float targetVolume) {
        double now = context.getCurrentTime();
        volumeGain.getGain().cancelScheduledValues(now);
        volumeGain.getGain().setValueAtTime(volumeGain.getGain().getValue(), now);
        volumeGain.getGain().linearRampToValueAtTime(targetVolume, now + milliseconds / 1000.0);
    }

    @Override public AudioNode getInput() { return panner; }
    @Override public AudioNode getOutput() { return volumeGain; }
}
