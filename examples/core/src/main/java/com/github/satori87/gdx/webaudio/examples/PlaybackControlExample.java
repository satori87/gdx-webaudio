package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates playback controls: pitch/rate, volume fading, loop points,
 * and buffer properties (duration, sample rate, channels).
 */
public class PlaybackControlExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer musicBuffer;
    private AudioBufferSourceNode currentSource;
    private GainNode masterGain;
    private boolean loaded = false;
    private String status = "Loading music-calm.wav...";

    private float currentRate = 1.0f;
    private float currentVolume = 1.0f;
    private boolean looping = false;
    private double startedAt = -1;
    private String bufferInfo = "";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        masterGain = ctx.createGain();
        masterGain.connect(ctx.getDestination());

        // Load audio file
        try {
            byte[] data = Gdx.files.internal("music-calm.wav").readBytes();
            ctx.decodeAudioData(data, buffer -> {
                musicBuffer = buffer;
                loaded = true;
                bufferInfo = String.format("Duration: %.2fs | Channels: %d | Rate: %.0f Hz | Samples: %d",
                    buffer.getDuration(), buffer.getNumberOfChannels(),
                    buffer.getSampleRate(), buffer.getLength());
                status = "Loaded! Tap Play to start.";
            }, () -> {
                status = "Failed to decode audio";
            });
        } catch (Exception e) {
            status = "File not found: " + e.getMessage();
        }
    }

    private void play() {
        stop();
        if (musicBuffer == null) return;
        currentSource = ctx.createBufferSource();
        currentSource.setBuffer(musicBuffer);
        currentSource.getPlaybackRate().setValueAtTime(currentRate, ctx.getCurrentTime());
        currentSource.setLoop(looping);
        if (looping) {
            // Loop middle 50% of the buffer
            double dur = musicBuffer.getDuration();
            currentSource.setLoopStart(dur * 0.25);
            currentSource.setLoopEnd(dur * 0.75);
        }
        currentSource.connect(masterGain);
        masterGain.getGain().setValueAtTime(currentVolume, ctx.getCurrentTime());
        currentSource.start();
        startedAt = ctx.getCurrentTime();
        currentSource.setOnEnded(() -> {
            status = "Playback ended";
            startedAt = -1;
        });
        status = "Playing (rate=" + currentRate + "x, vol=" + (int)(currentVolume * 100) + "%)";
    }

    private void stop() {
        try {
            if (currentSource != null) {
                currentSource.stop();
                currentSource = null;
            }
        } catch (Exception ignored) {}
        startedAt = -1;
    }

    private void fadeOut() {
        if (masterGain == null) return;
        double now = ctx.getCurrentTime();
        masterGain.getGain().setValueAtTime(currentVolume, now);
        masterGain.getGain().linearRampToValueAtTime(0, now + 2.0);
        status = "Fading out over 2s...";
    }

    private void fadeIn() {
        if (masterGain == null) return;
        double now = ctx.getCurrentTime();
        masterGain.getGain().setValueAtTime(0, now);
        masterGain.getGain().linearRampToValueAtTime(currentVolume, now + 1.0);
        status = "Fading in over 1s...";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // Calculate elapsed time
        String elapsed = "--";
        String duration = "--";
        if (musicBuffer != null) {
            duration = String.format("%.1fs", musicBuffer.getDuration());
        }
        if (startedAt >= 0) {
            double elapsedSec = (ctx.getCurrentTime() - startedAt) * currentRate;
            elapsed = String.format("%.1fs", elapsedSec);
        }

        // Draw info
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Playback Control", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        if (!bufferInfo.isEmpty()) {
            font.setColor(0.7f, 0.85f, 1, 1);
            font.draw(batch, bufferInfo, 20, Gdx.graphics.getHeight() - 85);
        }
        font.setColor(0.8f, 1, 0.8f, 1);
        font.draw(batch, "Elapsed: " + elapsed + " / " + duration
                + "  |  Rate: " + currentRate + "x"
                + "  |  Volume: " + (int)(currentVolume * 100) + "%"
                + "  |  Loop: " + (looping ? "ON" : "OFF"),
            20, Gdx.graphics.getHeight() - 105);
        batch.end();

        // Draw buttons
        int btn = drawButtons(shapes, batch, font,
            "Play", "Stop", "Toggle Loop",
            "Rate -0.25", "Rate +0.25", "Reset Rate",
            "Vol -25%", "Vol +25%", "Fade Out (2s)",
            "Fade In (1s)", "Half Speed", "Double Speed"
        );

        if (!loaded && btn >= 0) {
            return; // ignore clicks while loading
        }

        try {
            switch (btn) {
                case 0: play(); break;
                case 1:
                    stop();
                    status = "Stopped";
                    break;
                case 2:
                    looping = !looping;
                    status = "Loop: " + (looping ? "ON (middle 50%)" : "OFF");
                    if (currentSource != null) {
                        currentSource.setLoop(looping);
                        if (looping) {
                            double dur = musicBuffer.getDuration();
                            currentSource.setLoopStart(dur * 0.25);
                            currentSource.setLoopEnd(dur * 0.75);
                        }
                    }
                    break;
                case 3:
                    currentRate = Math.max(0.25f, currentRate - 0.25f);
                    applyRate();
                    break;
                case 4:
                    currentRate = Math.min(4.0f, currentRate + 0.25f);
                    applyRate();
                    break;
                case 5:
                    currentRate = 1.0f;
                    applyRate();
                    break;
                case 6:
                    currentVolume = Math.max(0, currentVolume - 0.25f);
                    masterGain.getGain().setValueAtTime(currentVolume, ctx.getCurrentTime());
                    status = "Volume: " + (int)(currentVolume * 100) + "%";
                    break;
                case 7:
                    currentVolume = Math.min(2.0f, currentVolume + 0.25f);
                    masterGain.getGain().setValueAtTime(currentVolume, ctx.getCurrentTime());
                    status = "Volume: " + (int)(currentVolume * 100) + "%";
                    break;
                case 8: fadeOut(); break;
                case 9: fadeIn(); break;
                case 10:
                    currentRate = 0.5f;
                    applyRate();
                    break;
                case 11:
                    currentRate = 2.0f;
                    applyRate();
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("Playback", "Error", e);
        }
    }

    private void applyRate() {
        if (currentSource != null) {
            currentSource.getPlaybackRate().setValueAtTime(currentRate, ctx.getCurrentTime());
        }
        status = "Rate: " + currentRate + "x";
    }

    @Override
    public void dispose() {
        stop();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
