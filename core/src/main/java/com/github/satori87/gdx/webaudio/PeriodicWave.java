package com.github.satori87.gdx.webaudio;

/**
 * Represents a custom periodic waveform that can be used with an {@code OscillatorNode}.
 * Maps to the Web Audio API {@code PeriodicWave} interface.
 * <p>
 * Instances are created via {@link WebAudioContext#createPeriodicWave(float[], float[])}
 * from Fourier series coefficients. This is a marker interface; the waveform data
 * is managed by the underlying platform implementation.
 *
 * @see PeriodicWave (Web Audio API)
 */
public interface PeriodicWave {
}
