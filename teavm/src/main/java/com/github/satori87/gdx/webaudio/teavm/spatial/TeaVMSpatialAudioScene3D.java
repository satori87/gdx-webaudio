package com.github.satori87.gdx.webaudio.teavm.spatial;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene3D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioSource;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * TeaVM/browser implementation of {@link SpatialAudioScene3D}.
 *
 * <p>Manages a 3D spatial audio environment using the browser's Web Audio API. Sets up
 * a master gain node and provides listener positioning/orientation control. Integrates
 * with libGDX's {@link Camera} for easy listener tracking in 3D scenes.</p>
 */
public class TeaVMSpatialAudioScene3D implements SpatialAudioScene3D {
    private final WebAudioContext context;
    private final GainNode masterGain;
    private float worldScale = 1.0f;
    private PanningModel defaultPanningModel = PanningModel.HRTF;
    private DistanceModel defaultDistanceModel = DistanceModel.INVERSE;

    public TeaVMSpatialAudioScene3D(WebAudioContext context) {
        this.context = context;
        this.masterGain = context.createGain();
        this.masterGain.connect(context.getDestination());
    }

    @Override public void updateListenerFromCamera(Camera camera) {
        AudioListener listener = context.getListener();
        listener.setPosition(camera.position.x * worldScale, camera.position.y * worldScale, camera.position.z * worldScale);
        listener.setOrientation(camera.direction.x, camera.direction.y, camera.direction.z, camera.up.x, camera.up.y, camera.up.z);
    }
    @Override public void setListenerPosition(float x, float y, float z) {
        context.getListener().setPosition(x * worldScale, y * worldScale, z * worldScale);
    }
    @Override public void setListenerPosition(Vector3 position) { setListenerPosition(position.x, position.y, position.z); }
    @Override public void setListenerOrientation(float fx, float fy, float fz, float ux, float uy, float uz) {
        context.getListener().setOrientation(fx, fy, fz, ux, uy, uz);
    }
    @Override public SpatialAudioSource createSource() { return createSource(0, 0, 0); }
    @Override public SpatialAudioSource createSource(float x, float y, float z) {
        PannerNode panner = context.createPanner();
        panner.setPanningModel(defaultPanningModel);
        panner.setDistanceModel(defaultDistanceModel);
        panner.setRefDistance(1); panner.setMaxDistance(10000); panner.setRolloffFactor(1);
        panner.setPosition(x * worldScale, y * worldScale, z * worldScale);
        GainNode gain = context.createGain();
        panner.connect(gain); gain.connect(masterGain);
        return new TeaVMSpatialAudioSource(panner, gain, worldScale);
    }
    @Override public SpatialAudioSource createSource(Vector3 pos) { return createSource(pos.x, pos.y, pos.z); }
    @Override public void setDefaultPanningModel(PanningModel model) { this.defaultPanningModel = model; }
    @Override public void setDefaultDistanceModel(DistanceModel model) { this.defaultDistanceModel = model; }
    @Override public void setWorldScale(float scale) { this.worldScale = scale; }
    @Override public void setMasterVolume(float volume) { masterGain.getGain().setValue(volume); }
    @Override public WebAudioContext getContext() { return context; }
    @Override public void dispose() { masterGain.disconnect(); }
}
