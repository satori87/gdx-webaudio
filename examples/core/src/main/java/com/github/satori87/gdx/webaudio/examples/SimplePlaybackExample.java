package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.SoundGroup;
import com.github.satori87.gdx.webaudio.WebMusic;
import com.github.satori87.gdx.webaudio.WebSound;

/**
 * Demonstrates the simplified high-level WebSound and WebMusic API.
 *
 * <p>Shows fire-and-forget sound effects via {@code ctx.loadSound()}, long-form
 * music playback via {@code ctx.loadMusic()}, per-instance pitch/pan control,
 * looping, completion callbacks, and routing through a {@link SoundGroup}.</p>
 */
public class SimplePlaybackExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private WebSound coinSound, explosionSound;
    private WebMusic music;
    private SoundGroup sfxGroup;
    private long loopId = -1;
    private String status = "Loading...";
    private int loaded = 0;
    private static final int TOTAL = 3;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Create a SoundGroup to route all SFX through
        sfxGroup = ctx.createSoundGroup();
        sfxGroup.getOutput().connect(ctx.getDestination());

        ctx.loadSound(Gdx.files.internal("coin.wav"), sound -> {
            coinSound = sound;
            coinSound.setOutput(sfxGroup.getInput()); // Route through SFX group
            onLoaded("coin.wav");
        }, () -> Gdx.app.error("SimplePlayback", "Failed to load coin.wav"));

        ctx.loadSound(Gdx.files.internal("explosion.wav"), sound -> {
            explosionSound = sound;
            explosionSound.setOutput(sfxGroup.getInput());
            onLoaded("explosion.wav");
        }, () -> Gdx.app.error("SimplePlayback", "Failed to load explosion.wav"));

        ctx.loadMusic(Gdx.files.internal("music-calm.wav"), m -> {
            music = m;
            music.setLooping(true);
            music.setOnCompletionListener(finished ->
                status = "Music finished naturally");
            onLoaded("music-calm.wav");
        }, () -> Gdx.app.error("SimplePlayback", "Failed to load music-calm.wav"));
    }

    private void onLoaded(String name) {
        loaded++;
        if (loaded >= TOTAL) {
            status = "All loaded! Click buttons to play.";
        } else {
            status = "Loaded " + name + " (" + loaded + "/" + TOTAL + ")";
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);

        int h = Gdx.graphics.getHeight();
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Simple Playback (WebSound & WebMusic)", 20, h - 40);
        font.draw(batch, "Status: " + status, 20, h - 70);

        if (music != null) {
            String state = music.isPlaying() ? "Playing" : "Stopped";
            font.draw(batch, "Music: " + state
                + "  Vol: " + String.format("%.1f", music.getVolume())
                + "  Pos: " + String.format("%.1f", music.getPosition())
                + "s / " + String.format("%.1f", music.getDuration()) + "s", 20, h - 90);
        }

        font.draw(batch, "SFX Group Vol: " + String.format("%.1f", sfxGroup.getVolume())
            + (loopId >= 0 ? "  (coin looping)" : ""), 20, h - 110);
        batch.end();

        int clicked = drawButtons(shapes, batch, font,
            "Play Coin", "Play Explosion", "Coin Hi-Pitch",
            "Loop Coin", "Stop Loop", "SFX Vol +",
            "SFX Vol -", "Play Music", "Pause Music",
            "Stop Music", "Music Vol +", "Music Vol -",
            "Seek +5s", "Pan Left", "Pan Right");

        switch (clicked) {
            case 0:
                if (coinSound != null) { coinSound.play(); status = "Coin!"; }
                break;
            case 1:
                if (explosionSound != null) { explosionSound.play(); status = "Explosion!"; }
                break;
            case 2:
                // Play with custom volume, pitch, and pan
                if (coinSound != null) {
                    coinSound.play(0.8f, 2.0f, 0.5f);
                    status = "Coin (high pitch, panned right)";
                }
                break;
            case 3:
                // Loop with instance control
                if (coinSound != null && loopId < 0) {
                    loopId = coinSound.loop(0.4f);
                    status = "Coin looping (id=" + loopId + ")";
                }
                break;
            case 4:
                if (coinSound != null && loopId >= 0) {
                    coinSound.stop(loopId);
                    status = "Loop stopped";
                    loopId = -1;
                }
                break;
            case 5:
                sfxGroup.setVolume(Math.min(1f, sfxGroup.getVolume() + 0.1f));
                status = "SFX Vol: " + String.format("%.1f", sfxGroup.getVolume());
                break;
            case 6:
                sfxGroup.setVolume(Math.max(0f, sfxGroup.getVolume() - 0.1f));
                status = "SFX Vol: " + String.format("%.1f", sfxGroup.getVolume());
                break;
            case 7:
                if (music != null) { music.play(); status = "Music playing"; }
                break;
            case 8:
                if (music != null) { music.pause(); status = "Music paused"; }
                break;
            case 9:
                if (music != null) { music.stop(); status = "Music stopped"; }
                break;
            case 10:
                if (music != null) {
                    music.setVolume(Math.min(1f, music.getVolume() + 0.1f));
                    status = "Music Vol: " + String.format("%.1f", music.getVolume());
                }
                break;
            case 11:
                if (music != null) {
                    music.setVolume(Math.max(0f, music.getVolume() - 0.1f));
                    status = "Music Vol: " + String.format("%.1f", music.getVolume());
                }
                break;
            case 12:
                if (music != null) {
                    music.setPosition(music.getPosition() + 5f);
                    status = "Seeked to " + String.format("%.1f", music.getPosition()) + "s";
                }
                break;
            case 13:
                if (music != null) {
                    music.setPan(-1f, music.getVolume());
                    status = "Music panned left";
                }
                break;
            case 14:
                if (music != null) {
                    music.setPan(1f, music.getVolume());
                    status = "Music panned right";
                }
                break;
        }
    }

    @Override
    public void dispose() {
        if (music != null) music.dispose();
        if (coinSound != null) coinSound.dispose();
        if (explosionSound != null) explosionSound.dispose();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
