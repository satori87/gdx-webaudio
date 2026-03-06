package com.github.satori87.gdx.webaudio.teavm.spatial;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioSource;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * TeaVM/browser implementation of {@link SpatialAudioScene2D}.
 *
 * <p>Manages a 2D spatial audio environment using the browser's Web Audio API. Sets up
 * a master gain node and configures the listener for a top-down 2D perspective (Z=0).
 * Integrates with libGDX's {@link OrthographicCamera} for easy listener tracking.</p>
 */
public class TeaVMSpatialAudioScene2D implements SpatialAudioScene2D {
    private final WebAudioContext context;
    private final GainNode masterGain;
    private float worldScale = 1.0f;

    public TeaVMSpatialAudioScene2D(WebAudioContext context) {
        this.context = context;
        this.masterGain = context.createGain();
        this.masterGain.connect(context.getDestination());
        AudioListener listener = context.getListener();
        listener.setPosition(0, 0, 0);
        listener.setOrientation(0, 0, -1, 0, 1, 0);
    }

    @Override public void setListenerPosition(float x, float y) {
        AudioListener listener = context.getListener();
        listener.getPositionX().setValue(x * worldScale);
        listener.getPositionY().setValue(y * worldScale);
        listener.getPositionZ().setValue(0);
    }
    @Override public void setListenerPosition(Vector2 position) { setListenerPosition(position.x, position.y); }
    @Override public void updateListenerFromCamera(OrthographicCamera camera) {
        setListenerPosition(camera.position.x, camera.position.y);
    }
    @Override public SpatialAudioSource createSource() { return createSource(0, 0); }
    @Override public SpatialAudioSource createSource(float x, float y) {
        PannerNode panner = context.createPanner();
        panner.setPanningModel(PanningModel.HRTF);
        panner.setDistanceModel(DistanceModel.INVERSE);
        panner.setRefDistance(1);
        panner.setMaxDistance(10000);
        panner.setRolloffFactor(1);
        panner.setPosition(x * worldScale, y * worldScale, 0);
        GainNode gain = context.createGain();
        panner.connect(gain);
        gain.connect(masterGain);
        return new TeaVMSpatialAudioSource(panner, gain, worldScale);
    }
    @Override public void setWorldScale(float unitsPerMeter) { this.worldScale = unitsPerMeter; }
    @Override public WebAudioContext getContext() { return context; }
    @Override public void setMasterVolume(float volume) { masterGain.getGain().setValue(volume); }
    @Override public void dispose() { masterGain.disconnect(); }
}
