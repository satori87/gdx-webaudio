package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.PeriodicWave;
import com.github.satori87.gdx.webaudio.teavm.jso.JSPeriodicWave;

/**
 * TeaVM/browser implementation of {@link PeriodicWave}.
 *
 * <p>Wraps a {@link JSPeriodicWave} that defines a custom waveform for use with an
 * {@link com.github.satori87.gdx.webaudio.source.OscillatorNode}.</p>
 */
public class TeaVMPeriodicWave implements PeriodicWave {
    public final JSPeriodicWave jsWave;

    public TeaVMPeriodicWave(JSPeriodicWave jsWave) {
        this.jsWave = jsWave;
    }
}
