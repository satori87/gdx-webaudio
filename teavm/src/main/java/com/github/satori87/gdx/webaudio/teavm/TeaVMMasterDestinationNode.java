package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioDestinationNode;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioDestinationNode;
import com.github.satori87.gdx.webaudio.teavm.jso.JSGainNode;

/**
 * A wrapper that presents a master {@link JSGainNode} as an {@link AudioDestinationNode}.
 *
 * <p>When nodes connect to this destination, they connect to the master gain node,
 * which then routes to the real audio destination. This enables context-level
 * master volume control.</p>
 */
class TeaVMMasterDestinationNode extends TeaVMAudioNode implements AudioDestinationNode {
    private final JSAudioDestinationNode realDest;

    TeaVMMasterDestinationNode(JSGainNode masterGain, JSAudioDestinationNode realDest,
                               WebAudioContext context) {
        super(masterGain, context);
        this.realDest = realDest;
    }

    @Override
    public int getMaxChannelCount() {
        return realDest.getMaxChannelCount();
    }
}
