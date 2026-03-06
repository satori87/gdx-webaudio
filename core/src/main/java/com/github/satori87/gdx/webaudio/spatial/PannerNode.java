package com.github.satori87.gdx.webaudio.spatial;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * Spatializes an audio source in 3D space relative to the listener.
 * Corresponds to the Web Audio API {@code PannerNode} interface.
 *
 * <p>Supports configurable panning models (equal-power or HRTF), distance models
 * (linear, inverse, exponential), and directional cone attenuation. Position and
 * orientation are controllable via automatable {@link AudioParam} instances.</p>
 */
public interface PannerNode extends AudioNode {

    /**
     * Returns the X-coordinate of the audio source position.
     *
     * @return the position X {@link AudioParam}
     */
    AudioParam getPositionX();

    /**
     * Returns the Y-coordinate of the audio source position.
     *
     * @return the position Y {@link AudioParam}
     */
    AudioParam getPositionY();

    /**
     * Returns the Z-coordinate of the audio source position.
     *
     * @return the position Z {@link AudioParam}
     */
    AudioParam getPositionZ();

    /**
     * Returns the X-component of the direction the audio source is pointing.
     *
     * @return the orientation X {@link AudioParam}
     */
    AudioParam getOrientationX();

    /**
     * Returns the Y-component of the direction the audio source is pointing.
     *
     * @return the orientation Y {@link AudioParam}
     */
    AudioParam getOrientationY();

    /**
     * Returns the Z-component of the direction the audio source is pointing.
     *
     * @return the orientation Z {@link AudioParam}
     */
    AudioParam getOrientationZ();

    /**
     * Returns the algorithm used to calculate volume reduction based on distance.
     *
     * @return the current distance model
     */
    DistanceModel getDistanceModel();

    /**
     * Sets the algorithm used to calculate volume reduction based on distance.
     *
     * @param model the distance model to use
     */
    void setDistanceModel(DistanceModel model);

    /**
     * Returns the reference distance for the distance model, within which volume is at full level.
     *
     * @return the reference distance
     */
    float getRefDistance();

    /**
     * Sets the reference distance for the distance model.
     *
     * @param distance the reference distance
     */
    void setRefDistance(float distance);

    /**
     * Returns the maximum distance beyond which the audio will not be further attenuated.
     *
     * @return the maximum distance
     */
    float getMaxDistance();

    /**
     * Sets the maximum distance beyond which the audio will not be further attenuated.
     *
     * @param distance the maximum distance
     */
    void setMaxDistance(float distance);

    /**
     * Returns the rolloff factor controlling how quickly volume decreases with distance.
     *
     * @return the rolloff factor
     */
    float getRolloffFactor();

    /**
     * Sets the rolloff factor controlling how quickly volume decreases with distance.
     *
     * @param factor the rolloff factor
     */
    void setRolloffFactor(float factor);

    /**
     * Returns the inner angle of the directional cone in degrees, inside which there is no attenuation.
     *
     * @return the cone inner angle in degrees
     */
    float getConeInnerAngle();

    /**
     * Sets the inner angle of the directional cone in degrees.
     *
     * @param angle the cone inner angle in degrees
     */
    void setConeInnerAngle(float angle);

    /**
     * Returns the outer angle of the directional cone in degrees, outside which the audio is attenuated by the cone outer gain.
     *
     * @return the cone outer angle in degrees
     */
    float getConeOuterAngle();

    /**
     * Sets the outer angle of the directional cone in degrees.
     *
     * @param angle the cone outer angle in degrees
     */
    void setConeOuterAngle(float angle);

    /**
     * Returns the gain applied to audio outside the cone outer angle.
     *
     * @return the cone outer gain, in the range [0, 1]
     */
    float getConeOuterGain();

    /**
     * Sets the gain applied to audio outside the cone outer angle.
     *
     * @param gain the cone outer gain, in the range [0, 1]
     */
    void setConeOuterGain(float gain);

    /**
     * Returns the spatialization algorithm used (equal-power or HRTF).
     *
     * @return the current panning model
     */
    PanningModel getPanningModel();

    /**
     * Sets the spatialization algorithm to use.
     *
     * @param model the panning model to use
     */
    void setPanningModel(PanningModel model);

    /**
     * Convenience method to set the 3D position of the audio source.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     */
    void setPosition(float x, float y, float z);

    /**
     * Convenience method to set the 3D orientation of the audio source.
     *
     * @param x the X-component of the direction vector
     * @param y the Y-component of the direction vector
     * @param z the Z-component of the direction vector
     */
    void setOrientation(float x, float y, float z);
}
