package com.github.satori87.gdx.webaudio.teavm.spatial;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioSource;
import com.github.satori87.gdx.webaudio.types.DistanceModel;

/**
 * TeaVM/browser implementation of {@link SpatialAudioSource}.
 *
 * <p>Combines a {@link PannerNode} and {@link GainNode} to represent a positionable audio source
 * in 3D space. Applies world-scale transformations to position coordinates so that
 * game-world units map correctly to the Web Audio API's meter-based coordinate system.</p>
 */
public class TeaVMSpatialAudioSource implements SpatialAudioSource {
    private final PannerNode panner;
    private final GainNode gain;
    private final float worldScale;
    float posX, posY, posZ;
    float velX, velY, velZ;
    AudioParam dopplerTarget;

    public TeaVMSpatialAudioSource(PannerNode panner, GainNode gain, float worldScale) {
        this.panner = panner;
        this.gain = gain;
        this.worldScale = worldScale;
    }

    @Override public void setPosition(float x, float y, float z) {
        this.posX = x; this.posY = y; this.posZ = z;
        panner.setPosition(x * worldScale, y * worldScale, z * worldScale);
    }
    @Override public void setPosition(float x, float y) {
        // Map 2D game coords to 3D audio: game X → audio X, game Y → audio -Z
        // The listener faces -Z, so game "up" (Y+) = audio "in front" (-Z)
        this.posX = x; this.posY = y; this.posZ = 0;
        panner.setPosition(x * worldScale, 0, -y * worldScale);
    }
    @Override public void setOrientation(float x, float y, float z) { panner.setOrientation(x, y, z); }
    @Override public void setVolume(float volume) { gain.getGain().setValue(volume); }
    @Override public float getVolume() { return gain.getGain().getValue(); }
    @Override public void setDistanceModel(DistanceModel model) { panner.setDistanceModel(model); }
    @Override public void setRefDistance(float distance) { panner.setRefDistance(distance); }
    @Override public void setMaxDistance(float distance) { panner.setMaxDistance(distance); }
    @Override public void setRolloffFactor(float factor) { panner.setRolloffFactor(factor); }
    @Override public void setCone(float inner, float outer, float outerGain) {
        panner.setConeInnerAngle(inner); panner.setConeOuterAngle(outer); panner.setConeOuterGain(outerGain);
    }
    @Override public AudioNode getInput() { return panner; }
    @Override public AudioNode getOutput() { return gain; }
    @Override public PannerNode getPannerNode() { return panner; }
    @Override public void setVelocity(float x, float y, float z) {
        this.velX = x; this.velY = y; this.velZ = z;
    }
    @Override public void setVelocity(float x, float y) { setVelocity(x, y, 0); }
    @Override public void setDopplerTarget(AudioParam playbackRate) {
        this.dopplerTarget = playbackRate;
    }
}
