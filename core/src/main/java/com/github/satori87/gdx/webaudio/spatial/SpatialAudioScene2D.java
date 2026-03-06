package com.github.satori87.gdx.webaudio.spatial;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.github.satori87.gdx.webaudio.WebAudioContext;

/**
 * Manages a 2D spatial audio scene with listener positioning and source creation.
 *
 * <p>Provides a convenient abstraction for 2D games, automatically mapping world coordinates
 * to the Web Audio API's 3D spatialization (with Z fixed at 0). Supports integration with
 * libGDX's {@link OrthographicCamera} for automatic listener updates.</p>
 */
public interface SpatialAudioScene2D {

    /**
     * Sets the listener position in 2D world coordinates.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     */
    void setListenerPosition(float x, float y);

    /**
     * Sets the listener position from a 2D vector.
     *
     * @param position the listener position
     */
    void setListenerPosition(Vector2 position);

    /**
     * Updates the listener position to match the camera's position.
     *
     * @param camera the orthographic camera to track
     */
    void updateListenerFromCamera(OrthographicCamera camera);

    /**
     * Creates a new spatial audio source at the origin.
     *
     * @return a new {@link SpatialAudioSource}
     */
    SpatialAudioSource createSource();

    /**
     * Creates a new spatial audio source at the specified 2D position.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @return a new {@link SpatialAudioSource} positioned at (x, y)
     */
    SpatialAudioSource createSource(float x, float y);

    /**
     * Sets the scale factor mapping world units to meters for distance calculations.
     *
     * @param unitsPerMeter the number of world units per meter
     */
    void setWorldScale(float unitsPerMeter);

    /**
     * Returns the underlying Web Audio context used by this scene.
     *
     * @return the {@link WebAudioContext}
     */
    WebAudioContext getContext();

    /**
     * Sets the master volume for the entire scene.
     *
     * @param volume the master volume level, where 1.0 is full volume
     */
    void setMasterVolume(float volume);

    /**
     * Sets the listener velocity for Doppler effect computation.
     *
     * @param x the X-component of velocity in world units per second
     * @param y the Y-component of velocity in world units per second
     */
    void setListenerVelocity(float x, float y);

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
