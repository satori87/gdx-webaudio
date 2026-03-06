package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioListener;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.types.DistanceModel;
import com.github.satori87.gdx.webaudio.types.OscillatorType;
import com.github.satori87.gdx.webaudio.types.PanningModel;

/**
 * 2D spatial audio with a sound orbiting around the listener.
 * Uses a loaded fire-loop sound or oscillator fallback.
 */
public class SpatialAudio2DExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private PannerNode panner;
    private OscillatorNode osc;
    private AudioBufferSourceNode loopSrc;
    private AudioBuffer fireBuffer;
    private boolean playing;
    private float sourceX, sourceY;
    private float time;
    private String mode = "";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        AudioListener listener = ctx.getListener();
        listener.setPosition(0, 0, 0);
        listener.setOrientation(0, 0, -1, 0, 1, 0);

        byte[] data = Gdx.files.internal("fire-loop.wav").readBytes();
        ctx.decodeAudioData(data, buf -> fireBuffer = buf,
                () -> Gdx.app.error("Spatial2D", "Failed to load fire-loop.wav"));
    }

    private void setupPanner() {
        panner = ctx.createPanner();
        panner.setPanningModel(PanningModel.HRTF);
        panner.setDistanceModel(DistanceModel.INVERSE);
        panner.setRefDistance(10);
        panner.setMaxDistance(100);
        panner.setRolloffFactor(1);
    }

    private void startWithFile() {
        if (playing || fireBuffer == null) return;
        stopSound();
        setupPanner();
        loopSrc = ctx.createBufferSource();
        loopSrc.setBuffer(fireBuffer);
        loopSrc.setLoop(true);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(1.0f);
        loopSrc.connect(gain);
        gain.connect(panner);
        panner.connect(ctx.getDestination());
        loopSrc.start();
        playing = true;
        mode = "Fire loop (loaded file)";
    }

    private void startWithOsc() {
        if (playing) return;
        stopSound();
        setupPanner();
        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SINE);
        osc.getFrequency().setValue(440);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(0.5f);
        osc.connect(gain);
        gain.connect(panner);
        panner.connect(ctx.getDestination());
        osc.start();
        playing = true;
        mode = "Oscillator (440Hz sine)";
    }

    private void stopSound() {
        if (osc != null) { osc.stop(); osc = null; }
        if (loopSrc != null) {
            try { loopSrc.stop(); } catch (Exception ignored) {}
            loopSrc = null;
        }
        playing = false;
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        if (playing) {
            time += Gdx.graphics.getDeltaTime();
            sourceX = (float) Math.cos(time) * 10;
            sourceY = (float) Math.sin(time) * 10;
            // Map 2D game coords to 3D audio: game X → audio X, game Y → audio -Z
            // Listener faces -Z, so game "up" = audio "in front"
            panner.setPosition(sourceX, 0, -sourceY);
        }

        int cx = Gdx.graphics.getWidth() / 2;
        int cy = Gdx.graphics.getHeight() / 2;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.3f, 0.3f, 0.8f, 1f);
        shapes.circle(cx, cy, 8);
        if (playing) {
            shapes.setColor(1f, 0.3f, 0.3f, 1f);
            shapes.circle(cx + sourceX * 15, cy + sourceY * 15, 6);
        }
        shapes.end();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "2D Spatial Audio", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Sound orbits around the listener (center)", 20, Gdx.graphics.getHeight() - 60);
        if (playing) font.draw(batch, "Source: " + mode, 20, Gdx.graphics.getHeight() - 80);
        font.setColor(0.3f, 0.3f, 0.8f, 1f);
        font.draw(batch, "Blue = Listener", cx + 15, cy + 5);
        if (playing) {
            font.setColor(1f, 0.3f, 0.3f, 1f);
            font.draw(batch, "Red = Source", cx + 15, cy - 15);
        }
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Start (Fire Loop)", "Start (Oscillator)", "Stop");
        if (clicked == 0) startWithFile();
        else if (clicked == 1) startWithOsc();
        else if (clicked == 2) stopSound();
    }

    @Override
    public void dispose() {
        stopSound();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
