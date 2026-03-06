package com.github.satori87.gdx.webaudio;

/**
 * An automatable audio parameter that can be scheduled to change over time.
 * Maps to the Web Audio API {@code AudioParam} interface.
 * <p>
 * Values can be set directly or scheduled using automation methods such as
 * {@link #linearRampToValueAtTime(float, double)} and {@link #setTargetAtTime(float, double, double)}.
 * All scheduling methods return {@code this} for method chaining.
 *
 * @see AudioParam (Web Audio API)
 */
public interface AudioParam {

    /**
     * Returns the current value of this parameter.
     *
     * @return the current value
     */
    float getValue();

    /**
     * Sets the value of this parameter immediately, without scheduling.
     *
     * @param value the new value
     */
    void setValue(float value);

    /**
     * Returns the default value of this parameter as defined by the owning node.
     *
     * @return the default value
     */
    float getDefaultValue();

    /**
     * Returns the minimum allowable value for this parameter.
     *
     * @return the minimum value
     */
    float getMinValue();

    /**
     * Returns the maximum allowable value for this parameter.
     *
     * @return the maximum value
     */
    float getMaxValue();

    /**
     * Schedules the parameter to change to the given value at the specified time.
     *
     * @param value     the value to set
     * @param startTime the time in seconds (relative to the context's current time) at which to set the value
     * @return this parameter, for chaining
     */
    AudioParam setValueAtTime(float value, double startTime);

    /**
     * Schedules a linear ramp from the current value to the target value, ending at the specified time.
     *
     * @param value   the target value to ramp to
     * @param endTime the time in seconds at which the ramp completes
     * @return this parameter, for chaining
     */
    AudioParam linearRampToValueAtTime(float value, double endTime);

    /**
     * Schedules an exponential ramp from the current value to the target value, ending at the specified time.
     * The current and target values must be positive.
     *
     * @param value   the target value to ramp to (must be positive)
     * @param endTime the time in seconds at which the ramp completes
     * @return this parameter, for chaining
     */
    AudioParam exponentialRampToValueAtTime(float value, double endTime);

    /**
     * Schedules an exponential approach to the target value, starting at the specified time.
     * The value asymptotically approaches the target with the given time constant.
     *
     * @param target       the target value to approach
     * @param startTime    the time in seconds at which the transition begins
     * @param timeConstant the time constant in seconds controlling the rate of approach
     * @return this parameter, for chaining
     */
    AudioParam setTargetAtTime(float target, double startTime, double timeConstant);

    /**
     * Schedules the parameter to follow a curve of values over the specified duration.
     *
     * @param values    an array of values defining the automation curve
     * @param startTime the time in seconds at which the curve begins
     * @param duration  the duration in seconds over which the curve is applied
     * @return this parameter, for chaining
     */
    AudioParam setValueCurveAtTime(float[] values, double startTime, double duration);

    /**
     * Cancels all scheduled parameter changes at and after the specified time.
     *
     * @param startTime the time in seconds from which to cancel scheduled changes
     * @return this parameter, for chaining
     */
    AudioParam cancelScheduledValues(double startTime);

    /**
     * Cancels scheduled parameter changes at the specified time while holding the value
     * the parameter would have had at that moment.
     *
     * @param cancelTime the time in seconds at which to cancel and hold
     * @return this parameter, for chaining
     */
    AudioParam cancelAndHoldAtTime(double cancelTime);
}
