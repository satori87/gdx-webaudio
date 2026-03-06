package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Applies a linear convolution effect to the audio signal, typically used for reverb.
 * Corresponds to the Web Audio API {@code ConvolverNode} interface.
 *
 * <p>The convolution is performed using an impulse response stored in an {@link AudioBuffer}.
 * Normalization can be toggled to automatically scale the impulse response.</p>
 */
public interface ConvolverNode extends AudioNode {

    /**
     * Returns the impulse response buffer used for convolution.
     *
     * @return the current {@link AudioBuffer}, or {@code null} if none is set
     */
    AudioBuffer getBuffer();

    /**
     * Sets the impulse response buffer used for convolution.
     *
     * @param buffer the {@link AudioBuffer} containing the impulse response
     */
    void setBuffer(AudioBuffer buffer);

    /**
     * Returns whether the impulse response buffer is automatically normalized.
     *
     * @return {@code true} if normalization is enabled
     */
    boolean isNormalize();

    /**
     * Sets whether the impulse response buffer should be automatically normalized.
     *
     * @param normalize {@code true} to enable normalization, {@code false} to disable
     */
    void setNormalize(boolean normalize);
}
