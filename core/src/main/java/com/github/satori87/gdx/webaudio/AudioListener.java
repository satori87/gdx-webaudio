package com.github.satori87.gdx.webaudio;

/**
 * Represents the position and orientation of the listener in 3D audio space.
 * Maps to the Web Audio API {@code AudioListener} interface.
 * <p>
 * The listener defines the point of view for spatial audio rendering used by
 * {@code PannerNode}. Position, forward direction, and up direction are each
 * exposed as automatable {@link AudioParam} values, as well as convenience
 * setter methods.
 *
 * @see AudioListener (Web Audio API)
 */
public interface AudioListener {

    /**
     * Returns the automatable X-position of the listener.
     *
     * @return the X-position parameter
     */
    AudioParam getPositionX();

    /**
     * Returns the automatable Y-position of the listener.
     *
     * @return the Y-position parameter
     */
    AudioParam getPositionY();

    /**
     * Returns the automatable Z-position of the listener.
     *
     * @return the Z-position parameter
     */
    AudioParam getPositionZ();

    /**
     * Returns the automatable X-component of the listener's forward direction.
     *
     * @return the forward X parameter
     */
    AudioParam getForwardX();

    /**
     * Returns the automatable Y-component of the listener's forward direction.
     *
     * @return the forward Y parameter
     */
    AudioParam getForwardY();

    /**
     * Returns the automatable Z-component of the listener's forward direction.
     *
     * @return the forward Z parameter
     */
    AudioParam getForwardZ();

    /**
     * Returns the automatable X-component of the listener's up direction.
     *
     * @return the up X parameter
     */
    AudioParam getUpX();

    /**
     * Returns the automatable Y-component of the listener's up direction.
     *
     * @return the up Y parameter
     */
    AudioParam getUpY();

    /**
     * Returns the automatable Z-component of the listener's up direction.
     *
     * @return the up Z parameter
     */
    AudioParam getUpZ();

    /**
     * Sets the position of the listener in 3D space.
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     */
    void setPosition(float x, float y, float z);

    /**
     * Sets the orientation of the listener using forward and up direction vectors.
     *
     * @param forwardX the X-component of the forward direction
     * @param forwardY the Y-component of the forward direction
     * @param forwardZ the Z-component of the forward direction
     * @param upX      the X-component of the up direction
     * @param upY      the Y-component of the up direction
     * @param upZ      the Z-component of the up direction
     */
    void setOrientation(float forwardX, float forwardY, float forwardZ,
                        float upX, float upY, float upZ);
}
