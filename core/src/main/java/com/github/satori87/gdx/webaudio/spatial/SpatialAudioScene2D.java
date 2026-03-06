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
     * Releases all resources associated with this scene.
     */
    void dispose();
}
