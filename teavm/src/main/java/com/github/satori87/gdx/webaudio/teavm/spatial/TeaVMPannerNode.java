package com.github.satori87.gdx.webaudio.teavm.spatial;

import com.github.satori87.gdx.webaudio.AudioParam;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioParam;
import com.github.satori87.gdx.webaudio.teavm.jso.JSPannerNode;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * TeaVM/browser implementation of {@link PannerNode}.
 *
 * <p>Wraps a {@link JSPannerNode} to spatialize audio in 3D space via the browser's Web Audio API.
 * Supports configurable distance models, panning models, and directional cone parameters.</p>
 */
public class TeaVMPannerNode extends TeaVMAudioNode implements PannerNode {
    private final JSPannerNode jsPanner;

    public TeaVMPannerNode(JSPannerNode jsPanner, WebAudioContext context) {
        super(jsPanner, context); this.jsPanner = jsPanner;
    }

    @Override public AudioParam getPositionX() { return new TeaVMAudioParam(jsPanner.getPositionX()); }
    @Override public AudioParam getPositionY() { return new TeaVMAudioParam(jsPanner.getPositionY()); }
    @Override public AudioParam getPositionZ() { return new TeaVMAudioParam(jsPanner.getPositionZ()); }
    @Override public AudioParam getOrientationX() { return new TeaVMAudioParam(jsPanner.getOrientationX()); }
    @Override public AudioParam getOrientationY() { return new TeaVMAudioParam(jsPanner.getOrientationY()); }
    @Override public AudioParam getOrientationZ() { return new TeaVMAudioParam(jsPanner.getOrientationZ()); }
    @Override public DistanceModel getDistanceModel() { return DistanceModel.fromJsValue(jsPanner.getDistanceModel()); }
    @Override public void setDistanceModel(DistanceModel m) { jsPanner.setDistanceModel(m.toJsValue()); }
    @Override public float getRefDistance() { return jsPanner.getRefDistance(); }
    @Override public void setRefDistance(float d) { jsPanner.setRefDistance(d); }
    @Override public float getMaxDistance() { return jsPanner.getMaxDistance(); }
    @Override public void setMaxDistance(float d) { jsPanner.setMaxDistance(d); }
    @Override public float getRolloffFactor() { return jsPanner.getRolloffFactor(); }
    @Override public void setRolloffFactor(float f) { jsPanner.setRolloffFactor(f); }
    @Override public float getConeInnerAngle() { return jsPanner.getConeInnerAngle(); }
    @Override public void setConeInnerAngle(float a) { jsPanner.setConeInnerAngle(a); }
    @Override public float getConeOuterAngle() { return jsPanner.getConeOuterAngle(); }
    @Override public void setConeOuterAngle(float a) { jsPanner.setConeOuterAngle(a); }
    @Override public float getConeOuterGain() { return jsPanner.getConeOuterGain(); }
    @Override public void setConeOuterGain(float g) { jsPanner.setConeOuterGain(g); }
    @Override public PanningModel getPanningModel() { return PanningModel.fromJsValue(jsPanner.getPanningModel()); }
    @Override public void setPanningModel(PanningModel m) { jsPanner.setPanningModel(m.toJsValue()); }
    @Override public void setPosition(float x, float y, float z) {
        jsPanner.getPositionX().setValue(x); jsPanner.getPositionY().setValue(y); jsPanner.getPositionZ().setValue(z);
    }
    @Override public void setOrientation(float x, float y, float z) {
        jsPanner.getOrientationX().setValue(x); jsPanner.getOrientationY().setValue(y); jsPanner.getOrientationZ().setValue(z);
    }
}
