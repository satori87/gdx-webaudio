package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * A custom infinite impulse response (IIR) filter defined by feedforward and feedback coefficients.
 * Corresponds to the Web Audio API {@code IIRFilterNode} interface.
 *
 * <p>Unlike {@link BiquadFilterNode}, this node allows specifying arbitrary-order IIR filters.
 * The filter coefficients are set at creation time and cannot be changed afterward.</p>
 */
public interface IIRFilterNode extends AudioNode {

    /**
     * Computes the magnitude and phase response of the filter at the given frequencies.
     *
     * @param frequencyHz   array of frequencies in hertz at which to evaluate the response
     * @param magResponse   array to receive the magnitude response values (linear scale)
     * @param phaseResponse array to receive the phase response values in radians
     */
    void getFrequencyResponse(float[] frequencyHz, float[] magResponse, float[] phaseResponse);
}
