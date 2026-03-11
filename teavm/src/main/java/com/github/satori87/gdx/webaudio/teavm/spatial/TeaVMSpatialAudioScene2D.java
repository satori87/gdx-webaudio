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

import java.util.ArrayList;
import java.util.List;

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
    private final List<TeaVMSpatialAudioSource> sources = new ArrayList<>();
    private float listenerX, listenerY;
    private float listenerVelX, listenerVelY;
    private float dopplerFactor = 0f;
    private float speedOfSound = 343.3f;

    public TeaVMSpatialAudioScene2D(WebAudioContext context) {
        this.context = context;
        this.masterGain = context.createGain();
        this.masterGain.connect(context.getDestination());
        AudioListener listener = context.getListener();
        listener.setPosition(0, 0, 0);
        listener.setOrientation(0, 0, -1, 0, 1, 0);
    }

    @Override public void setListenerPosition(float x, float y) {
        this.listenerX = x; this.listenerY = y;
        // Map 2D game coords to 3D audio: game X -> audio X, game Y -> audio -Z
        // The listener faces -Z, so game "up" (Y+) = audio "in front" (-Z)
        AudioListener listener = context.getListener();
        listener.setPosition(x * worldScale, 0, -y * worldScale);
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
        panner.setPosition(x * worldScale, 0, -y * worldScale);
        GainNode gain = context.createGain();
        panner.connect(gain);
        gain.connect(masterGain);
        TeaVMSpatialAudioSource source = new TeaVMSpatialAudioSource(panner, gain, worldScale);
        source.posX = x; source.posY = y; source.posZ = 0;
        sources.add(source);
        return source;
    }
    @Override public void setWorldScale(float unitsPerMeter) { this.worldScale = 1.0f / unitsPerMeter; }
    @Override public WebAudioContext getContext() { return context; }
    @Override public void setMasterVolume(float volume) { masterGain.getGain().setValue(volume); }
    @Override public void setListenerVelocity(float x, float y) {
        this.listenerVelX = x; this.listenerVelY = y;
    }
    @Override public void setDopplerFactor(float factor) { this.dopplerFactor = factor; }
    @Override public void setSpeedOfSound(float speed) { this.speedOfSound = speed; }
    @Override public void update() {
        if (dopplerFactor == 0) return;
        for (int i = 0; i < sources.size(); i++) {
            TeaVMSpatialAudioSource source = sources.get(i);
            if (source.dopplerTarget == null) continue;
            float dx = source.posX - listenerX;
            float dy = source.posY - listenerY;
            float dist = (float)Math.sqrt(dx * dx + dy * dy);
            if (dist < 0.0001f) continue;
            float invDist = 1.0f / dist;
            dx *= invDist; dy *= invDist;
            float vListener = listenerVelX * dx + listenerVelY * dy;
            float vSource = source.velX * dx + source.velY * dy;
            float ratio = (speedOfSound + vListener) / (speedOfSound + vSource);
            float shift = 1.0f + dopplerFactor * (ratio - 1.0f);
            source.dopplerTarget.setValue(shift);
        }
    }
    @Override public void dispose() { masterGain.disconnect(); }
}
