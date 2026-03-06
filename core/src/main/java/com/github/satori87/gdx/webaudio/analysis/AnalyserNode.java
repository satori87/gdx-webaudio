package com.github.satori87.gdx.webaudio.analysis;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Provides real-time frequency and time-domain audio analysis.
 * Corresponds to the Web Audio API {@code AnalyserNode} interface.
 *
 * <p>Connect an audio source to this node to extract frequency spectra or waveform data.
 * The FFT size determines the resolution of the frequency data. This node passes audio
 * through unchanged, so it can be inserted into a processing chain without affecting output.</p>
 */
public interface AnalyserNode extends AudioNode {

    /**
     * Returns the FFT size used for frequency-domain analysis. Must be a power of two.
     *
     * @return the current FFT size
     */
    int getFftSize();

    /**
     * Sets the FFT size used for frequency-domain analysis. Must be a power of two between 32 and 32768.
     *
     * @param fftSize the FFT size to use
     */
    void setFftSize(int fftSize);

    /**
     * Returns the number of frequency bins, equal to half the FFT size.
     *
     * @return the number of frequency bins
     */
    int getFrequencyBinCount();

    /**
     * Returns the minimum decibel value for scaling byte frequency data.
     *
     * @return the minimum decibel value
     */
    float getMinDecibels();

    /**
     * Sets the minimum decibel value for scaling byte frequency data.
     *
     * @param minDecibels the minimum decibel value
     */
    void setMinDecibels(float minDecibels);

    /**
     * Returns the maximum decibel value for scaling byte frequency data.
     *
     * @return the maximum decibel value
     */
    float getMaxDecibels();

    /**
     * Sets the maximum decibel value for scaling byte frequency data.
     *
     * @param maxDecibels the maximum decibel value
     */
    void setMaxDecibels(float maxDecibels);

    /**
     * Returns the averaging constant with the last analysis frame, between 0 and 1.
     *
     * @return the smoothing time constant
     */
    float getSmoothingTimeConstant();

    /**
     * Sets the averaging constant with the last analysis frame, between 0 (no averaging) and 1 (maximum smoothing).
     *
     * @param smoothing the smoothing time constant
     */
    void setSmoothingTimeConstant(float smoothing);

    /**
     * Copies the current frequency data into the provided byte array, scaled between minDecibels and maxDecibels.
     *
     * @param array the byte array to receive the frequency data
     */
    void getByteFrequencyData(byte[] array);

    /**
     * Copies the current frequency data in decibels into the provided float array.
     *
     * @param array the float array to receive the frequency data in dB
     */
    void getFloatFrequencyData(float[] array);

    /**
     * Copies the current time-domain (waveform) data into the provided byte array, scaled to the range [0, 255].
     *
     * @param array the byte array to receive the time-domain data
     */
    void getByteTimeDomainData(byte[] array);

    /**
     * Copies the current time-domain (waveform) data into the provided float array, in the range [-1, 1].
     *
     * @param array the float array to receive the time-domain data
     */
    void getFloatTimeDomainData(float[] array);
}
