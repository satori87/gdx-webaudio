package com.github.satori87.gdx.webaudio;

import com.badlogic.gdx.utils.Pool;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Object pool for {@link AudioBufferSourceNode} instances sharing a common {@link AudioBuffer}.
 *
 * <p>Since Web Audio source nodes are one-shot (cannot be restarted after stopping),
 * this pool creates fresh nodes on demand rather than reusing stopped ones. Calling
 * {@link #free(AudioBufferSourceNode)} discards the used node — the pool automatically
 * replenishes itself when new nodes are obtained.</p>
 *
 * <p>For most sound effect use cases, prefer {@link WebSound} (via
 * {@link WebAudioContext#loadSound}) which provides a simpler API with built-in
 * volume, pitch, pan, pause/resume, and automatic instance management.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * SoundPool pool = new SoundPool(context, buffer);
 * pool.play(context.getDestination()); // fire-and-forget
 * }</pre>
 */
public class SoundPool extends Pool<AudioBufferSourceNode> {
    private final WebAudioContext context;
    private final AudioBuffer buffer;

    /**
     * Creates a sound pool with default initial capacity (4) and unlimited maximum.
     *
     * @param context the audio context used to create source nodes
     * @param buffer  the audio buffer to assign to each source node
     */
    public SoundPool(WebAudioContext context, AudioBuffer buffer) {
        this(context, buffer, 4, Integer.MAX_VALUE);
    }

    /**
     * Creates a sound pool with the specified capacity settings.
     *
     * @param context         the audio context used to create source nodes
     * @param buffer          the audio buffer to assign to each source node
     * @param initialCapacity the initial number of pre-created source nodes
     * @param max             the maximum number of source nodes to keep in the pool
     */
    public SoundPool(WebAudioContext context, AudioBuffer buffer, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.context = context;
        this.buffer = buffer;
    }

    @Override
    protected AudioBufferSourceNode newObject() {
        AudioBufferSourceNode source = context.createBufferSource();
        source.setBuffer(buffer);
        return source;
    }

    /**
     * One-shot source nodes cannot be reused. This method discards the used node.
     * The pool creates fresh nodes via {@link #newObject()} when {@link #obtain()} is called.
     *
     * @param object the used source node (discarded)
     */
    @Override
    public void free(AudioBufferSourceNode object) {
        // Do not return one-shot nodes to the pool — they can't be restarted.
        // Pool.obtain() calls newObject() when the free list is empty.
    }

    /**
     * Obtains a ready-to-play source node and connects it to the given destination.
     *
     * @param destination the node to connect the source to
     * @return a source node connected to the destination, ready to start
     */
    public AudioBufferSourceNode obtain(AudioNode destination) {
        AudioBufferSourceNode source = obtain();
        source.connect(destination);
        return source;
    }

    /**
     * Fire-and-forget: obtains a source, connects it to the destination, starts playback,
     * and automatically frees it when playback ends.
     *
     * @param destination the node to route the audio through
     * @return the playing source node
     */
    public AudioBufferSourceNode play(AudioNode destination) {
        AudioBufferSourceNode source = obtain(destination);
        source.setOnEnded(() -> free(source));
        source.start();
        return source;
    }

    /**
     * Returns the audio buffer shared by all source nodes in this pool.
     *
     * @return the audio buffer
     */
    public AudioBuffer getBuffer() {
        return buffer;
    }

    /**
     * Returns the audio context used to create source nodes.
     *
     * @return the audio context
     */
    public WebAudioContext getContext() {
        return context;
    }
}
