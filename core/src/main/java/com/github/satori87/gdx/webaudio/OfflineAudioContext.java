package com.github.satori87.gdx.webaudio;

/**
 * An audio context for offline (non-real-time) rendering of an audio graph to an {@link AudioBuffer}.
 * Maps to the Web Audio API {@code OfflineAudioContext} interface.
 * <p>
 * Unlike {@link WebAudioContext}, the audio graph is rendered as fast as possible rather than
 * in real time, making this suitable for pre-processing or exporting audio.
 *
 * @see OfflineAudioContext (Web Audio API)
 */
public interface OfflineAudioContext extends WebAudioContext {

    /**
     * Returns the length of the rendering buffer in sample frames.
     *
     * @return the buffer length in sample frames
     */
    int getLength();

    /**
     * Begins rendering the audio graph to a buffer. The result is delivered asynchronously
     * via the provided callback.
     *
     * @param onComplete callback invoked with the rendered {@link AudioBuffer} when rendering completes
     */
    void startRendering(AudioBuffer.DecodeCallback onComplete);
}
