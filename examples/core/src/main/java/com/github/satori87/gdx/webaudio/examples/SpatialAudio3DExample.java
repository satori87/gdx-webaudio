package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.OscillatorType;
import com.github.satori87.gdx.webaudio.types.PanningModel;

public class SpatialAudio3DExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private boolean playing;
    private OscillatorNode osc1, osc2;
    private PannerNode panner1, panner2;
    private float time;
    private float listenerAngle;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
    }

    private void startSounds() {
        if (playing) return;

        panner1 = ctx.createPanner();
        panner1.setPanningModel(PanningModel.HRTF);
        panner1.setDistanceModel(DistanceModel.INVERSE);
        panner1.setRefDistance(1);
        panner1.setMaxDistance(100);
        panner1.setRolloffFactor(1);

        osc1 = ctx.createOscillator();
        osc1.setType(OscillatorType.SINE);
        osc1.getFrequency().setValue(440);
        GainNode g1 = ctx.createGain();
        g1.getGain().setValue(0.4f);
        osc1.connect(g1);
        g1.connect(panner1);
        panner1.connect(ctx.getDestination());

        panner2 = ctx.createPanner();
        panner2.setPanningModel(PanningModel.HRTF);
        panner2.setDistanceModel(DistanceModel.INVERSE);
        panner2.setRefDistance(1);
        panner2.setMaxDistance(50);
        panner2.setPosition(0, 5, -10);
        panner2.setOrientation(0, -1, 0);
        panner2.setConeInnerAngle(60);
        panner2.setConeOuterAngle(120);
        panner2.setConeOuterGain(0.1f);

        osc2 = ctx.createOscillator();
        osc2.setType(OscillatorType.SQUARE);
        osc2.getFrequency().setValue(330);
        GainNode g2 = ctx.createGain();
        g2.getGain().setValue(0.2f);
        osc2.connect(g2);
        g2.connect(panner2);
        panner2.connect(ctx.getDestination());

        osc1.start();
        osc2.start();
        playing = true;
    }

    private void stopSounds() {
        if (osc1 != null) { osc1.stop(); osc1 = null; }
        if (osc2 != null) { osc2.stop(); osc2 = null; }
        playing = false;
    }

    private void rotateLeft() { listenerAngle += 0.3f; updateListener(); }
    private void rotateRight() { listenerAngle -= 0.3f; updateListener(); }

    private void updateListener() {
        AudioListener listener = ctx.getListener();
        listener.setPosition(0, 0, 0);
        listener.setOrientation(
                (float) Math.sin(listenerAngle), 0, (float) -Math.cos(listenerAngle),
                0, 1, 0);
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        if (playing) {
            time += Gdx.graphics.getDeltaTime();
            float x = (float) Math.cos(time) * 8;
            float z = (float) Math.sin(time) * 8;
            panner1.setPosition(x, 0, z);
        }

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "3D Spatial Audio (HRTF)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Source 1 (sine) orbits horizontally", 20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, "Source 2 (square) is above with directional cone", 20, Gdx.graphics.getHeight() - 90);
        font.draw(batch, String.format("Listener angle: %.1f rad", listenerAngle), 20, Gdx.graphics.getHeight() - 120);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Start", "Stop", "Rotate Left", "Rotate Right");
        if (clicked == 0) startSounds();
        else if (clicked == 1) stopSounds();
        else if (clicked == 2) rotateLeft();
        else if (clicked == 3) rotateRight();
    }

    @Override
    public void dispose() {
        stopSounds();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
