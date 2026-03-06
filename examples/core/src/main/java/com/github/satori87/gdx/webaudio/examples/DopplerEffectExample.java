package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioScene2D;
import com.github.satori87.gdx.webaudio.spatial.SpatialAudioSource;

/**
 * Demonstrates the Doppler effect with a sound source moving past a stationary listener.
 * The pitch rises as the source approaches and drops as it recedes.
 * Uses SpatialAudioScene2D with velocity tracking and per-frame update().
 */
public class DopplerEffectExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private SpatialAudioScene2D scene;
    private SpatialAudioSource source;
    private AudioBufferSourceNode loopSrc;
    private AudioBuffer fireBuffer;
    private boolean playing = false;
    private boolean loaded = false;
    private String status = "Loading audio...";

    // Source motion
    private float sourceX, sourceY;
    private float sourceVelX, sourceVelY;
    private float time;
    private float dopplerFactor = 3.0f; // exaggerated for audibility
    private int motionMode = 0; // 0=flyby, 1=orbit, 2=approach-recede

    // Listener position (center)
    private static final float LISTENER_X = 0;
    private static final float LISTENER_Y = 0;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();

        scene = ctx.createSpatialScene2D();
        scene.setListenerPosition(LISTENER_X, LISTENER_Y);
        scene.setDopplerFactor(dopplerFactor);
        scene.setSpeedOfSound(100); // 100 world units/sec — makes effect more dramatic

        ctx.resume(null);

        byte[] data = Gdx.files.internal("tone-steady.wav").readBytes();
        ctx.decodeAudioData(data, buf -> {
            fireBuffer = buf;
            loaded = true;
            status = "Ready — start the Doppler demo";
        }, () -> { status = "Failed to load audio"; });
    }

    private void startSound() {
        stopSound();
        if (!loaded) return;

        source = scene.createSource(0, 0);
        source.setRefDistance(5);
        source.setMaxDistance(200);
        source.setRolloffFactor(1);

        loopSrc = ctx.createBufferSource();
        loopSrc.setBuffer(fireBuffer);
        loopSrc.setLoop(true);
        loopSrc.connect(source.getInput());

        source.setDopplerTarget(loopSrc.getPlaybackRate());

        loopSrc.start();
        playing = true;
        time = 0;
    }

    private void stopSound() {
        try { if (loopSrc != null) { loopSrc.stop(); loopSrc = null; } } catch (Exception ignored) {}
        if (source != null) source.setDopplerTarget(null);
        playing = false;
    }

    private void updateMotion(float dt) {
        time += dt;
        switch (motionMode) {
            case 0: // Flyby: source moves left to right repeatedly
                sourceX = -50 + (time * 30) % 100;
                sourceY = 15;
                sourceVelX = 30;
                sourceVelY = 0;
                break;
            case 1: // Orbit: source circles a point offset from listener
                float orbitRadius = 25;
                float orbitSpeed = 1.5f;
                float orbitCenterX = 20; // offset so source approaches and recedes
                sourceX = orbitCenterX + (float) Math.cos(time * orbitSpeed) * orbitRadius;
                sourceY = (float) Math.sin(time * orbitSpeed) * orbitRadius;
                sourceVelX = (float) (-Math.sin(time * orbitSpeed) * orbitRadius * orbitSpeed);
                sourceVelY = (float) (Math.cos(time * orbitSpeed) * orbitRadius * orbitSpeed);
                break;
            case 2: // Approach-recede: source oscillates along X axis, never crossing listener
                float bounceSpeed = 1.5f;
                float bounceCenter = 30;
                float bounceRange = 25; // oscillates between 5 and 55
                sourceX = bounceCenter + (float) Math.sin(time * bounceSpeed) * bounceRange;
                sourceVelX = (float) Math.cos(time * bounceSpeed) * bounceRange * bounceSpeed;
                sourceY = 0;
                sourceVelY = 0;
                break;
        }

        source.setPosition(sourceX, sourceY);
        source.setVelocity(sourceVelX, sourceVelY);
        scene.update();
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (playing) {
            updateMotion(Gdx.graphics.getDeltaTime());
            float dist = (float) Math.sqrt(sourceX * sourceX + sourceY * sourceY);
            status = String.format("Pos=(%.0f,%.0f) Vel=(%.0f,%.0f) Dist=%.0f",
                    sourceX, sourceY, sourceVelX, sourceVelY, dist);
        }

        // Draw visualization
        int cx = Gdx.graphics.getWidth() / 2;
        int cy = Gdx.graphics.getHeight() / 2 + 30;
        float scale = 4f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Listener (blue)
        shapes.setColor(0.3f, 0.3f, 0.8f, 1f);
        shapes.circle(cx, cy, 10);
        if (playing) {
            // Source (red)
            shapes.setColor(1f, 0.3f, 0.3f, 1f);
            shapes.circle(cx + sourceX * scale, cy + sourceY * scale, 7);
            // Velocity arrow (yellow)
            shapes.setColor(1f, 1f, 0.3f, 0.8f);
            float arrowLen = 0.5f;
            shapes.rectLine(cx + sourceX * scale, cy + sourceY * scale,
                    cx + (sourceX + sourceVelX * arrowLen) * scale,
                    cy + (sourceY + sourceVelY * arrowLen) * scale, 2);
        }
        shapes.end();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Doppler Effect (2D Spatial)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        String[] modes = {"Flyby (L->R)", "Orbit (Circle)", "Bounce (Approach/Recede)"};
        font.draw(batch, "Mode: " + modes[motionMode] + "  |  Doppler: " + String.format("%.1f", dopplerFactor) + "x",
                20, Gdx.graphics.getHeight() - 85);
        font.setColor(0.3f, 0.3f, 0.8f, 1f);
        font.draw(batch, "Listener", cx + 15, cy + 5);
        if (playing) {
            font.setColor(1f, 0.3f, 0.3f, 1f);
            font.draw(batch, "Source", cx + sourceX * scale + 12, cy + sourceY * scale + 5);
        }
        batch.end();

        int btn = drawButtons(shapes, batch, font,
                "Start", "Stop", "Mode: Flyby",
                "Mode: Orbit", "Mode: Bounce", "Doppler +",
                "Doppler -", "Doppler Off");

        switch (btn) {
            case 0: startSound(); break;
            case 1: stopSound(); status = "Stopped"; break;
            case 2: motionMode = 0; time = 0; break;
            case 3: motionMode = 1; time = 0; break;
            case 4: motionMode = 2; time = 0; break;
            case 5:
                dopplerFactor = Math.min(10, dopplerFactor + 1);
                scene.setDopplerFactor(dopplerFactor);
                break;
            case 6:
                dopplerFactor = Math.max(0, dopplerFactor - 1);
                scene.setDopplerFactor(dopplerFactor);
                break;
            case 7:
                dopplerFactor = 0;
                scene.setDopplerFactor(0);
                break;
        }
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
