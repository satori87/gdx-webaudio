package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;

/**
 * A configurable second-order (biquad) filter for common audio filtering operations.
 * Corresponds to the Web Audio API {@code BiquadFilterNode} interface.
 *
 * <p>Supports filter types such as lowpass, highpass, bandpass, notch, allpass, peaking,
 * lowshelf, and highshelf. Filter characteristics are controlled via automatable parameters.</p>
 */
public interface BiquadFilterNode extends AudioNode {

    /**
     * Returns the center or cutoff frequency of the filter in hertz.
     *
     * @return the frequency {@link AudioParam}, default value 350 Hz
     */
    AudioParam getFrequency();

    /**
     * Returns the detuning of the filter frequency in cents.
     *
     * @return the detune {@link AudioParam}, default value 0
     */
    AudioParam getDetune();

    /**
     * Returns the quality factor (Q) controlling the filter's bandwidth or resonance.
     *
     * @return the Q {@link AudioParam}, default value 1.0
     */
    AudioParam getQ();

    /**
     * Returns the gain used by certain filter types (peaking, lowshelf, highshelf) in dB.
     *
     * @return the gain {@link AudioParam}, default value 0
     */
    AudioParam getGain();

    /**
     * Returns the current filter type.
     *
     * @return the biquad filter type
     */
    BiquadFilterType getType();

    /**
     * Sets the filter type.
     *
     * @param type the biquad filter type to use
     */
    void setType(BiquadFilterType type);

    /**
     * Computes the magnitude and phase response of the filter at the given frequencies.
     *
     * @param frequencyHz   array of frequencies in hertz at which to evaluate the response
     * @param magResponse   array to receive the magnitude response values (linear scale)
     * @param phaseResponse array to receive the phase response values in radians
     */
    void getFrequencyResponse(float[] frequencyHz, float[] magResponse, float[] phaseResponse);
}
