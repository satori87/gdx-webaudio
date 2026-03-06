package com.github.satori87.gdx.webaudio.spatial;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * Manages a 3D spatial audio scene with listener positioning and source creation.
 *
 * <p>Provides full 3D spatialization, including listener orientation for accurate HRTF rendering.
 * Supports integration with libGDX's {@link Camera} for automatic listener position and
 * orientation updates. Newly created sources inherit the scene's default panning and distance models.</p>
 */
public interface SpatialAudioScene3D {

    /**
     * Updates the listener position and orientation to match the camera.
     *
     * @param camera the camera to track
     */
    void updateListenerFromCamera(Camera camera);

    /**
     * Sets the listener position in 3D world coordinates.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     */
    void setListenerPosition(float x, float y, float z);

    /**
     * Sets the listener position from a 3D vector.
     *
     * @param position the listener position
     */
    void setListenerPosition(Vector3 position);

    /**
     * Sets the listener orientation using forward and up direction vectors.
     *
     * @param forwardX the X-component of the forward direction
     * @param forwardY the Y-component of the forward direction
     * @param forwardZ the Z-component of the forward direction
     * @param upX      the X-component of the up direction
     * @param upY      the Y-component of the up direction
     * @param upZ      the Z-component of the up direction
     */
    void setListenerOrientation(float forwardX, float forwardY, float forwardZ,
                                 float upX, float upY, float upZ);

    /**
     * Creates a new spatial audio source at the origin.
     *
     * @return a new {@link SpatialAudioSource}
     */
    SpatialAudioSource createSource();

    /**
     * Creates a new spatial audio source at the specified 3D position.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     * @return a new {@link SpatialAudioSource} positioned at (x, y, z)
     */
    SpatialAudioSource createSource(float x, float y, float z);

    /**
     * Creates a new spatial audio source at the specified position.
     *
     * @param position the source position
     * @return a new {@link SpatialAudioSource} at the given position
     */
    SpatialAudioSource createSource(Vector3 position);

    /**
     * Sets the default panning model for newly created sources in this scene.
     *
     * @param model the panning model to use by default
     */
    void setDefaultPanningModel(PanningModel model);

    /**
     * Sets the default distance model for newly created sources in this scene.
     *
     * @param model the distance model to use by default
     */
    void setDefaultDistanceModel(DistanceModel model);

    /**
     * Sets the scale factor mapping world units to meters for distance calculations.
     *
     * @param unitsPerMeter the number of world units per meter
     */
    void setWorldScale(float unitsPerMeter);

    /**
     * Sets the master volume for the entire scene.
     *
     * @param volume the master volume level, where 1.0 is full volume
     */
    void setMasterVolume(float volume);

    /**
     * Returns the underlying Web Audio context used by this scene.
     *
     * @return the {@link WebAudioContext}
     */
    WebAudioContext getContext();

    /**
     * Sets the listener velocity for Doppler effect computation.
     *
     * @param x the X-component of velocity in world units per second
     * @param y the Y-component of velocity in world units per second
     * @param z the Z-component of velocity in world units per second
     */
    void setListenerVelocity(float x, float y, float z);

    /**
     * Sets the Doppler effect intensity factor. A value of 0 disables Doppler,
     * 1.0 is physically accurate, and values greater than 1 exaggerate the effect.
     *
     * @param factor the Doppler factor, default 0 (disabled)
     */
    void setDopplerFactor(float factor);

    /**
     * Sets the speed of sound in world units per second, used for Doppler calculations.
     *
     * @param speed the speed of sound, default 343.3 (meters per second)
     */
    void setSpeedOfSound(float speed);

    /**
     * Updates Doppler effect calculations for all sources with a registered Doppler target.
     * Call this once per frame from your game loop.
     */
    void update();

    /**
     * Releases all resources associated with this scene.
     */
    void dispose();
}
