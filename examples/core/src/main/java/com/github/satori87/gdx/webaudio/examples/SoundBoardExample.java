package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Sound board with all generated sound effects.
 * Each button plays a different sound effect loaded from WAV files.
 */
public class SoundBoardExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private static final String[] SOUND_FILES = {
        "click.wav", "coin.wav", "laser.wav", "explosion.wav",
        "jump.wav", "hit.wav", "powerup.wav", "notification.wav",
        "footstep.wav", "swoosh.wav", "drip.wav", "heartbeat.wav"
    };

    private static final String[] SOUND_LABELS = {
        "Click", "Coin", "Laser", "Explosion",
        "Jump", "Hit", "Powerup", "Notification",
        "Footstep", "Swoosh", "Drip", "Heartbeat"
    };

    private final AudioBuffer[] buffers = new AudioBuffer[SOUND_FILES.length];
    private int loaded = 0;
    private String status = "Loading sounds...";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        for (int i = 0; i < SOUND_FILES.length; i++) {
            final int idx = i;
            byte[] data = Gdx.files.internal(SOUND_FILES[i]).readBytes();
            ctx.decodeAudioData(data, buf -> {
                buffers[idx] = buf;
                loaded++;
                if (loaded >= SOUND_FILES.length) {
                    status = "All " + loaded + " sounds loaded!";
                } else {
                    status = "Loading... " + loaded + "/" + SOUND_FILES.length;
                }
            }, () -> Gdx.app.error("SoundBoard", "Failed to decode " + SOUND_FILES[idx]));
        }
    }

    private void playSound(int index) {
        if (buffers[index] == null) return;
        AudioBufferSourceNode src = ctx.createBufferSource();
        src.setBuffer(buffers[index]);
        src.connect(ctx.getDestination());
        src.start();
        status = "Playing: " + SOUND_FILES[index];
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Sound Board (12 Sound Effects)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 70);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, SOUND_LABELS);
        if (clicked >= 0) playSound(clicked);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
