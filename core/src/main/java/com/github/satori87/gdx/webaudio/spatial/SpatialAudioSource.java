package com.github.satori87.gdx.webaudio.spatial;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.types.DistanceModel;

/**
 * High-level wrapper around a {@link PannerNode} representing a positioned audio source in space.
 *
 * <p>Provides a simplified API for positioning, volume control, and distance model
 * configuration without directly managing the underlying panner node. Audio is routed
 * through an input node, the panner, and an output node.</p>
 */
public interface SpatialAudioSource {

    /**
     * Sets the 3D position of this audio source.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     */
    void setPosition(float x, float y, float z);

    /**
     * Sets the 2D position of this audio source, with Z set to 0.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     */
    void setPosition(float x, float y);

    /**
     * Sets the orientation direction of this audio source for cone-based attenuation.
     *
     * @param x the X-component of the direction vector
     * @param y the Y-component of the direction vector
     * @param z the Z-component of the direction vector
     */
    void setOrientation(float x, float y, float z);

    /**
     * Sets the volume (gain) of this audio source.
     *
     * @param volume the volume level, where 1.0 is full volume and 0.0 is silent
     */
    void setVolume(float volume);

    /**
     * Returns the current volume (gain) of this audio source.
     *
     * @return the current volume level
     */
    float getVolume();

    /**
     * Sets the distance model used for volume attenuation based on distance from the listener.
     *
     * @param model the distance model to use
     */
    void setDistanceModel(DistanceModel model);

    /**
     * Sets the reference distance within which the source is at full volume.
     *
     * @param distance the reference distance
     */
    void setRefDistance(float distance);

    /**
     * Sets the maximum distance beyond which the source will not be further attenuated.
     *
     * @param distance the maximum distance
     */
    void setMaxDistance(float distance);

    /**
     * Sets the rolloff factor controlling how quickly the volume decreases with distance.
     *
     * @param factor the rolloff factor
     */
    void setRolloffFactor(float factor);

    /**
     * Configures the directional cone for this audio source.
     *
     * @param innerAngleDeg the inner cone angle in degrees (no attenuation within this angle)
     * @param outerAngleDeg the outer cone angle in degrees (full attenuation outside this angle)
     * @param outerGain     the gain applied outside the outer cone angle, in the range [0, 1]
     */
    void setCone(float innerAngleDeg, float outerAngleDeg, float outerGain);

    /**
     * Returns the input node where audio should be connected before spatialization.
     *
     * @return the input {@link AudioNode}
     */
    AudioNode getInput();

    /**
     * Returns the output node that delivers the spatialized audio signal.
     *
     * @return the output {@link AudioNode}
     */
    AudioNode getOutput();

    /**
     * Returns the underlying panner node used for spatialization.
     *
     * @return the {@link PannerNode}
     */
    PannerNode getPannerNode();
}
