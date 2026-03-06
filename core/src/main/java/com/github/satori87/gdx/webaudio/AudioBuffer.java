package com.github.satori87.gdx.webaudio;

/**
 * An in-memory container for audio sample data consisting of one or more channels.
 * Maps to the Web Audio API {@code AudioBuffer} interface.
 * <p>
 * Each channel contains PCM float sample data. Buffers are created via
 * {@link WebAudioContext#createBuffer(int, int, float)} or obtained by decoding
 * audio data with {@link WebAudioContext#decodeAudioData(byte[], DecodeCallback, Runnable)}.
 *
 * @see AudioBuffer (Web Audio API)
 */
public interface AudioBuffer {

    /**
     * Returns the duration of the buffer in seconds.
     *
     * @return the duration in seconds
     */
    float getDuration();

    /**
     * Returns the number of sample frames in the buffer.
     *
     * @return the length in sample frames
     */
    int getLength();

    /**
     * Returns the number of audio channels in the buffer.
     *
     * @return the number of channels
     */
    int getNumberOfChannels();

    /**
     * Returns the sample rate of the buffer in Hz.
     *
     * @return the sample rate in Hz
     */
    float getSampleRate();

    /**
     * Returns the PCM sample data for the specified channel as a float array.
     *
     * @param channel the zero-based channel index
     * @return an array of float samples for the channel
     */
    float[] getChannelData(int channel);

    /**
     * Copies samples from the specified channel into the destination array, starting from the beginning.
     *
     * @param destination   the array to copy samples into
     * @param channelNumber the zero-based channel index to copy from
     */
    void copyFromChannel(float[] destination, int channelNumber);

    /**
     * Copies samples from the specified channel into the destination array, starting at the given offset.
     *
     * @param destination    the array to copy samples into
     * @param channelNumber  the zero-based channel index to copy from
     * @param startInChannel the sample frame offset within the channel to start copying from
     */
    void copyFromChannel(float[] destination, int channelNumber, int startInChannel);

    /**
     * Copies samples from the source array into the specified channel, starting from the beginning.
     *
     * @param source        the array of samples to copy from
     * @param channelNumber the zero-based channel index to copy into
     */
    void copyToChannel(float[] source, int channelNumber);

    /**
     * Copies samples from the source array into the specified channel, starting at the given offset.
     *
     * @param source         the array of samples to copy from
     * @param channelNumber  the zero-based channel index to copy into
     * @param startInChannel the sample frame offset within the channel to start copying to
     */
    void copyToChannel(float[] source, int channelNumber, int startInChannel);

    /**
     * Callback interface for receiving the result of an asynchronous audio decode operation.
     */
    interface DecodeCallback {
        /**
         * Called when audio data has been successfully decoded.
         *
         * @param buffer the decoded audio buffer
         */
        void onDecoded(AudioBuffer buffer);
    }
}
