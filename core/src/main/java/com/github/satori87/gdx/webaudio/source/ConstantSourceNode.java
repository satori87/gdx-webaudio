package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Outputs a constant audio signal whose value is controlled by an {@link AudioParam}.
 * Corresponds to the Web Audio API {@code ConstantSourceNode} interface.
 *
 * <p>Useful for providing a DC offset, controlling multiple parameters simultaneously
 * via the offset param's automation, or as a modulation source in audio graphs.</p>
 */
public interface ConstantSourceNode extends AudioScheduledSourceNode {

    /**
     * Returns the constant output value of this source node.
     *
     * @return the offset {@link AudioParam}, default value 1.0
     */
    AudioParam getOffset();
}
