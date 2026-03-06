package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Provides simple stereo panning of an audio signal.
 * Corresponds to the Web Audio API {@code StereoPannerNode} interface.
 *
 * <p>The pan value ranges from -1.0 (full left) to 1.0 (full right), with 0.0 representing center.
 * This is a simpler alternative to the full 3D spatialization offered by {@link com.github.satori87.gdx.webaudio.spatial.PannerNode}.</p>
 */
public interface StereoPannerNode extends AudioNode {

    /**
     * Returns the stereo panning position.
     *
     * @return the pan {@link AudioParam}, ranging from -1.0 (full left) to 1.0 (full right), default 0.0
     */
    AudioParam getPan();
}
