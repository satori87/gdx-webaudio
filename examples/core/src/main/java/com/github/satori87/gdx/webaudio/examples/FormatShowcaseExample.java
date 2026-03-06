package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates loading and playing audio in all formats supported by Web Audio API:
 * WAV, MP3, OGG (Vorbis), FLAC, M4A (AAC), and WebM (Opus).
 */
public class FormatShowcaseExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private static final String[] FILES = {
        "explosion.wav",        // WAV
        "coin.mp3",             // MP3
        "laser.ogg",            // OGG Vorbis
        "powerup.flac",         // FLAC
        "jump.m4a",             // M4A (AAC)
        "hit.webm",             // WebM (Opus)
        "music-calm.mp3",       // MP3 music
        "music-combat.ogg",     // OGG music
        "music-calm.flac",      // FLAC music
    };

    private static final String[] LABELS = {
        "WAV: explosion",
        "MP3: coin",
        "OGG: laser",
        "FLAC: powerup",
        "M4A: jump",
        "WebM: hit",
        "MP3: music-calm",
        "OGG: music-combat",
        "FLAC: music-calm",
    };

    private final AudioBuffer[] buffers = new AudioBuffer[FILES.length];
    private final String[] loadStatus = new String[FILES.length];
    private int loaded = 0;
    private int failed = 0;
    private AudioBufferSourceNode currentSource;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        for (int i = 0; i < FILES.length; i++) {
            loadStatus[i] = "loading...";
            final int idx = i;
            try {
                byte[] data = Gdx.files.internal(FILES[i]).readBytes();
                ctx.decodeAudioData(data, buf -> {
                    buffers[idx] = buf;
                    loadStatus[idx] = "OK (" + String.format("%.1f", buf.getDuration()) + "s)";
                    loaded++;
                }, () -> {
                    loadStatus[idx] = "DECODE FAIL";
                    failed++;
                });
            } catch (Exception e) {
                loadStatus[idx] = "FILE NOT FOUND";
                failed++;
            }
        }
    }

    private void playSound(int index) {
        stopCurrent();
        if (buffers[index] == null) return;
        currentSource = ctx.createBufferSource();
        currentSource.setBuffer(buffers[index]);
        currentSource.connect(ctx.getDestination());
        currentSource.start();
    }

    private void stopCurrent() {
        if (currentSource != null) {
            try { currentSource.stop(); } catch (Exception ignored) {}
            currentSource = null;
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        int h = Gdx.graphics.getHeight();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Format Showcase (WAV, MP3, OGG, FLAC, M4A, WebM)", 20, h - 40);
        font.draw(batch, "Loaded: " + loaded + "/" + FILES.length +
                (failed > 0 ? "  Failed: " + failed : ""), 20, h - 65);

        // Draw load status for each format
        for (int i = 0; i < FILES.length; i++) {
            String statusColor = loadStatus[i].startsWith("OK") ? "OK" :
                                 loadStatus[i].equals("loading...") ? "LOAD" : "FAIL";
            if (statusColor.equals("OK")) font.setColor(0.4f, 1f, 0.4f, 1f);
            else if (statusColor.equals("FAIL")) font.setColor(1f, 0.4f, 0.4f, 1f);
            else font.setColor(0.7f, 0.7f, 0.7f, 1f);
            font.draw(batch, FILES[i] + " - " + loadStatus[i], 20, h - 90 - i * 18);
        }
        batch.end();

        int clicked = drawButtons(shapes, batch, font, LABELS);
        if (clicked >= 0) playSound(clicked);
    }

    @Override
    public void dispose() {
        stopCurrent();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
