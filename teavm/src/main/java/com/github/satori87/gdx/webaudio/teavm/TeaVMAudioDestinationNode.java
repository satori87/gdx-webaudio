package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioDestinationNode;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioDestinationNode;

/**
 * TeaVM/browser implementation of {@link AudioDestinationNode}.
 *
 * <p>Wraps a {@link JSAudioDestinationNode} representing the final audio output destination
 * (typically the user's speakers) in the browser's Web Audio API.</p>
 */
public class TeaVMAudioDestinationNode extends TeaVMAudioNode implements AudioDestinationNode {
    private final JSAudioDestinationNode jsDest;

    public TeaVMAudioDestinationNode(JSAudioDestinationNode jsDest, WebAudioContext context) {
        super(jsDest, context);
        this.jsDest = jsDest;
    }

    @Override public int getMaxChannelCount() { return jsDest.getMaxChannelCount(); }
}
