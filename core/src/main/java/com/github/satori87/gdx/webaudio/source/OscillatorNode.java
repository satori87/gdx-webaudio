package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.PeriodicWave;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Generates a periodic waveform such as sine, square, sawtooth, or triangle.
 * Corresponds to the Web Audio API {@code OscillatorNode} interface.
 *
 * <p>The oscillator's frequency and detune can be controlled via their respective
 * {@link AudioParam} instances, allowing both direct value setting and automation scheduling.</p>
 */
public interface OscillatorNode extends AudioScheduledSourceNode {

    /**
     * Returns the frequency of the oscillation in hertz.
     *
     * @return the frequency {@link AudioParam}, default value 440 Hz
     */
    AudioParam getFrequency();

    /**
     * Returns the detuning of the oscillation in cents.
     *
     * @return the detune {@link AudioParam}, default value 0
     */
    AudioParam getDetune();

    /**
     * Returns the current waveform type of the oscillator.
     *
     * @return the oscillator waveform type
     */
    OscillatorType getType();

    /**
     * Sets the waveform type of the oscillator.
     *
     * @param type the oscillator waveform type to use
     */
    void setType(OscillatorType type);

    /**
     * Sets a custom periodic waveform for the oscillator, replacing the built-in waveform type.
     *
     * @param wave the custom periodic waveform to use
     */
    void setPeriodicWave(PeriodicWave wave);
}
