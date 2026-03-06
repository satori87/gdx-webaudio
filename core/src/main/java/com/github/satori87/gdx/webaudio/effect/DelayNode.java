package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Delays the incoming audio signal by a specified amount of time.
 * Corresponds to the Web Audio API {@code DelayNode} interface.
 *
 * <p>The delay time is controlled by an automatable {@link AudioParam}, enabling
 * smooth transitions and effects such as flanging or echo.</p>
 */
public interface DelayNode extends AudioNode {

    /**
     * Returns the amount of delay to apply, in seconds.
     *
     * @return the delay time {@link AudioParam}, default value 0
     */
    AudioParam getDelayTime();
}
