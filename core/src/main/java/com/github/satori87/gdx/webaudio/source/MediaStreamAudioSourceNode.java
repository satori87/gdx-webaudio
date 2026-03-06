package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Represents an audio source originating from a {@code MediaStream} (e.g., microphone input or WebRTC stream).
 * Corresponds to the Web Audio API {@code MediaStreamAudioSourceNode} interface.
 *
 * <p>This is a marker interface that allows a media stream's audio to be routed
 * into a Web Audio processing graph.</p>
 */
public interface MediaStreamAudioSourceNode extends AudioNode {
}
