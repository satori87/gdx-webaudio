package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Applies a parametric reverb effect using a synthetically generated impulse response.
 *
 * <p>Unlike {@link ConvolverNode} which requires a pre-recorded impulse response,
 * this node generates its own IR based on configurable room size and damping
 * parameters. The IR is regenerated whenever parameters change.</p>
 */
public interface ReverbNode extends AudioNode {

    /** Returns the room size (0.0 to 1.0). @return the room size, default 0.5 */
    float getRoomSize();
    /** Sets the room size (0.0 to 1.0), controlling reverb tail length. @param roomSize the room size */
    void setRoomSize(float roomSize);

    /** Returns the damping factor (0.0 to 1.0). @return the damping, default 0.5 */
    float getDamping();
    /** Sets the damping factor (0.0 to 1.0), controlling high-frequency absorption. @param damping the damping */
    void setDamping(float damping);

    /** Returns the wet (reverb) signal level. @return the wet level, default 0.5 */
    float getWet();
    /** Sets the wet (reverb) signal level. @param wet the wet level */
    void setWet(float wet);

    /** Returns the dry (original) signal level. @return the dry level, default 0.5 */
    float getDry();
    /** Sets the dry (original) signal level. @param dry the dry level */
    void setDry(float dry);
}
