package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.types.OverSampleType;

/**
 * Applies non-linear waveshaping distortion to the audio signal.
 * Corresponds to the Web Audio API {@code WaveShaperNode} interface.
 *
 * <p>The distortion is defined by a shaping curve that maps input sample values to output
 * values. Oversampling can be used to reduce aliasing artifacts introduced by the distortion.</p>
 */
public interface WaveShaperNode extends AudioNode {

    /**
     * Returns the shaping curve used for distortion.
     *
     * @return the current shaping curve as a float array, or {@code null} if none is set
     */
    float[] getCurve();

    /**
     * Sets the shaping curve used for distortion. The curve maps input values in the range
     * {@code [-1, 1]} to output values.
     *
     * @param curve the shaping curve as a float array
     */
    void setCurve(float[] curve);

    /**
     * Returns the current oversampling mode used to reduce aliasing.
     *
     * @return the oversampling type
     */
    OverSampleType getOversample();

    /**
     * Sets the oversampling mode used to reduce aliasing artifacts.
     *
     * @param oversample the oversampling type to use
     */
    void setOversample(OverSampleType oversample);
}
