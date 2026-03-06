package com.github.satori87.gdx.webaudio;

/**
 * Static factory and entry point for the gdx-webaudio library.
 * <p>
 * Before creating any audio contexts, a platform backend must be registered via
 * {@link #setPlatform(WebAudioPlatform)}. When using the TeaVM backend, call
 * {@code TeaVMWebAudio.initialize()} which handles this automatically.
 *
 * @see WebAudioContext
 * @see OfflineAudioContext
 */
public class WebAudio {
    private static WebAudioPlatform platform;

    /**
     * Registers the platform-specific backend implementation.
     *
     * @param platform the platform backend to use for creating audio contexts
     */
    public static void setPlatform(WebAudioPlatform platform) {
        WebAudio.platform = platform;
    }

    /**
     * Creates a new real-time audio context for audio processing and rendering.
     *
     * @return a new {@link WebAudioContext} instance
     * @throws IllegalStateException if no platform has been registered via {@link #setPlatform(WebAudioPlatform)}
     */
    public static WebAudioContext createContext() {
        if (platform == null) throw new IllegalStateException(
            "WebAudio platform not initialized. Call TeaVMWebAudio.initialize() first.");
        return platform.createContext();
    }

    /**
     * Creates a new offline audio context for non-real-time audio rendering.
     *
     * @param channels   the number of channels for the offline context
     * @param length     the length of the rendered buffer in sample frames
     * @param sampleRate the sample rate in Hz for the offline context
     * @return a new {@link OfflineAudioContext} instance
     * @throws IllegalStateException if no platform has been registered via {@link #setPlatform(WebAudioPlatform)}
     */
    public static OfflineAudioContext createOfflineContext(int channels, int length, float sampleRate) {
        if (platform == null) throw new IllegalStateException(
            "WebAudio platform not initialized. Call TeaVMWebAudio.initialize() first.");
        return platform.createOfflineContext(channels, length, sampleRate);
    }

    /**
     * Service provider interface for platform-specific Web Audio API implementations.
     * <p>
     * Implementations of this interface supply the concrete audio context factories
     * for a given runtime environment (e.g., TeaVM/browser).
     */
    public interface WebAudioPlatform {
        /**
         * Creates a new real-time audio context.
         *
         * @return a new {@link WebAudioContext} instance
         */
        WebAudioContext createContext();

        /**
         * Creates a new offline audio context for non-real-time rendering.
         *
         * @param channels   the number of channels
         * @param length     the buffer length in sample frames
         * @param sampleRate the sample rate in Hz
         * @return a new {@link OfflineAudioContext} instance
         */
        OfflineAudioContext createOfflineContext(int channels, int length, float sampleRate);
    }
}
