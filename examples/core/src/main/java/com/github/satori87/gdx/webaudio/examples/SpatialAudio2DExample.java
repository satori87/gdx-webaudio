package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioSource;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * 2D spatial audio with a sound orbiting around the listener.
 * Uses SpatialAudioScene2D for proper 2D-to-3D coordinate mapping.
 */
public class SpatialAudio2DExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private SpatialAudioScene2D scene;
    private SpatialAudioSource source;
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

        scene = ctx.createSpatialScene2D();
        scene.setListenerPosition(0, 0);

        byte[] data = Gdx.files.internal("fire-loop.wav").readBytes();
        ctx.decodeAudioData(data, buf -> fireBuffer = buf,
                () -> Gdx.app.error("Spatial2D", "Failed to load fire-loop.wav"));
    }

    private void startWithFile() {
        if (playing || fireBuffer == null) return;
        stopSound();
        source = scene.createSource(0, 0);
        source.setRefDistance(10);
        source.setMaxDistance(100);
        source.setRolloffFactor(1);
        loopSrc = ctx.createBufferSource();
        loopSrc.setBuffer(fireBuffer);
        loopSrc.setLoop(true);
        loopSrc.connect(source.getInput());
        loopSrc.start();
        playing = true;
        mode = "Fire loop (loaded file)";
    }

    private void startWithOsc() {
        if (playing) return;
        stopSound();
        source = scene.createSource(0, 0);
        source.setRefDistance(10);
        source.setMaxDistance(100);
        source.setRolloffFactor(1);
        source.setVolume(0.5f);
        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SINE);
        osc.getFrequency().setValue(440);
        osc.connect(source.getInput());
        osc.start();
        playing = true;
        mode = "Oscillator (440Hz sine)";
    }

    private void stopSound() {
        try { if (osc != null) { osc.stop(); osc = null; } } catch (Exception ignored) {}
        try { if (loopSrc != null) { loopSrc.stop(); loopSrc = null; } } catch (Exception ignored) {}
        playing = false;
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        if (playing && source != null) {
            time += Gdx.graphics.getDeltaTime();
            sourceX = (float) Math.cos(time) * 10;
            sourceY = (float) Math.sin(time) * 10;
            source.setPosition(sourceX, sourceY);
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
        font.draw(batch, "2D Spatial Audio (SpatialAudioScene2D)", 20, Gdx.graphics.getHeight() - 40);
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
        if (scene != null) scene.dispose();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
