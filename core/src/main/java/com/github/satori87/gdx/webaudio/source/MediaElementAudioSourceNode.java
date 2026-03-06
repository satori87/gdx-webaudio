package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Represents an audio source originating from an HTML media element (e.g., {@code <audio>} or {@code <video>}).
 * Corresponds to the Web Audio API {@code MediaElementAudioSourceNode} interface.
 *
 * <p>This is a marker interface that allows a media element's audio output to be routed
 * into a Web Audio processing graph.</p>
 */
public interface MediaElementAudioSourceNode extends AudioNode {
}
