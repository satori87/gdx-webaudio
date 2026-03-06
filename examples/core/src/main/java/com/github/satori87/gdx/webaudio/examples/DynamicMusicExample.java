package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;

/**
 * Demonstrates crossfading between two music layers (calm/combat)
 * loaded from actual audio files, with smooth parameter automation.
 */
public class DynamicMusicExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private GainNode calmGain, intenseGain;
    private AudioBuffer calmBuffer, combatBuffer;
    private boolean playing;
    private boolean inCombat;
    private String status = "Loading music files...";
    private int loaded = 0;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        byte[] calmData = Gdx.files.internal("music-calm.wav").readBytes();
        ctx.decodeAudioData(calmData, buf -> {
            calmBuffer = buf;
            loaded++;
            checkLoaded();
        }, () -> Gdx.app.error("DynamicMusic", "Failed to decode music-calm.wav"));

        byte[] combatData = Gdx.files.internal("music-combat.wav").readBytes();
        ctx.decodeAudioData(combatData, buf -> {
            combatBuffer = buf;
            loaded++;
            checkLoaded();
        }, () -> Gdx.app.error("DynamicMusic", "Failed to decode music-combat.wav"));
    }

    private void checkLoaded() {
        if (loaded >= 2) {
            status = "Music loaded! Click Start Music.";
        }
    }

    private void startMusic() {
        if (playing || calmBuffer == null || combatBuffer == null) return;

        calmGain = ctx.createGain();
        calmGain.getGain().setValue(1.0f);
        calmGain.connect(ctx.getDestination());

        intenseGain = ctx.createGain();
        intenseGain.getGain().setValue(0.0f);
        intenseGain.connect(ctx.getDestination());

        AudioBufferSourceNode calmSrc = ctx.createBufferSource();
        calmSrc.setBuffer(calmBuffer);
        calmSrc.setLoop(true);
        calmSrc.connect(calmGain);

        AudioBufferSourceNode combatSrc = ctx.createBufferSource();
        combatSrc.setBuffer(combatBuffer);
        combatSrc.setLoop(true);
        combatSrc.connect(intenseGain);

        double now = ctx.getCurrentTime();
        calmSrc.start(now);
        combatSrc.start(now);

        playing = true;
        inCombat = false;
        status = "Playing: CALM layer active";
    }

    private void enterCombat() {
        if (!playing || inCombat) return;
        double now = ctx.getCurrentTime();
        float fadeDuration = 1.5f;
        calmGain.getGain().setValueAtTime(1.0f, now);
        calmGain.getGain().linearRampToValueAtTime(0.0f, now + fadeDuration);
        intenseGain.getGain().setValueAtTime(0.0f, now);
        intenseGain.getGain().linearRampToValueAtTime(1.0f, now + fadeDuration);
        inCombat = true;
        status = "Crossfading to COMBAT layer...";
    }

    private void exitCombat() {
        if (!playing || !inCombat) return;
        double now = ctx.getCurrentTime();
        float fadeDuration = 2.0f;
        calmGain.getGain().setValueAtTime(0.0f, now);
        calmGain.getGain().linearRampToValueAtTime(1.0f, now + fadeDuration);
        intenseGain.getGain().setValueAtTime(1.0f, now);
        intenseGain.getGain().linearRampToValueAtTime(0.0f, now + fadeDuration);
        inCombat = false;
        status = "Crossfading to CALM layer...";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Dynamic Music (Crossfade Layers)", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "State: " + (playing ? (inCombat ? "COMBAT" : "CALM") : "STOPPED"),
                20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 90);
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Start Music", "Enter Combat", "Exit Combat");
        if (clicked == 0) startMusic();
        else if (clicked == 1) enterCombat();
        else if (clicked == 2) exitCombat();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
