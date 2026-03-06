package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSAudioListener;

/**
 * TeaVM/browser implementation of {@link AudioListener}.
 *
 * <p>Wraps a {@link JSAudioListener} to control the listener's position and orientation
 * in the browser's 3D audio space. Position and orientation are exposed as automatable
 * {@link AudioParam} values.</p>
 */
public class TeaVMAudioListener implements AudioListener {
    private final JSAudioListener jsListener;

    public TeaVMAudioListener(JSAudioListener jsListener) {
        this.jsListener = jsListener;
    }

    @Override public AudioParam getPositionX() { return new TeaVMAudioParam(jsListener.getPositionX()); }
    @Override public AudioParam getPositionY() { return new TeaVMAudioParam(jsListener.getPositionY()); }
    @Override public AudioParam getPositionZ() { return new TeaVMAudioParam(jsListener.getPositionZ()); }
    @Override public AudioParam getForwardX() { return new TeaVMAudioParam(jsListener.getForwardX()); }
    @Override public AudioParam getForwardY() { return new TeaVMAudioParam(jsListener.getForwardY()); }
    @Override public AudioParam getForwardZ() { return new TeaVMAudioParam(jsListener.getForwardZ()); }
    @Override public AudioParam getUpX() { return new TeaVMAudioParam(jsListener.getUpX()); }
    @Override public AudioParam getUpY() { return new TeaVMAudioParam(jsListener.getUpY()); }
    @Override public AudioParam getUpZ() { return new TeaVMAudioParam(jsListener.getUpZ()); }

    @Override public void setPosition(float x, float y, float z) {
        jsListener.getPositionX().setValue(x);
        jsListener.getPositionY().setValue(y);
        jsListener.getPositionZ().setValue(z);
    }
    @Override public void setOrientation(float fx, float fy, float fz, float ux, float uy, float uz) {
        jsListener.getForwardX().setValue(fx);
        jsListener.getForwardY().setValue(fy);
        jsListener.getForwardZ().setValue(fz);
        jsListener.getUpX().setValue(ux);
        jsListener.getUpY().setValue(uy);
        jsListener.getUpZ().setValue(uz);
    }
}
