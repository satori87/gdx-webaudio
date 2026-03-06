package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates all composite effect nodes: Chorus, Flanger, Phaser, Reverb, and Limiter.
 * Each effect can be applied to either an oscillator or a loaded audio file.
 * Parameters can be tweaked in real time.
 */
public class CompositeEffectsExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer musicBuf;
    private OscillatorNode osc;
    private AudioBufferSourceNode fileSrc;
    private GainNode sourceGain;
    private boolean loaded = false;
    private String status = "Loading audio...";

    // Current effect
    private ChorusNode chorus;
    private FlangerNode flanger;
    private PhaserNode phaser;
    private ReverbNode reverb;
    private LimiterNode limiter;
    private String activeEffect = "none";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        byte[] data = Gdx.files.internal("music-chiptune.wav").readBytes();
        ctx.decodeAudioData(data, buf -> {
            musicBuf = buf;
            loaded = true;
            status = "Ready! Choose source + effect.";
        }, () -> {
            status = "Decode failed — oscillator only";
            loaded = true;
        });
    }

    private void stopSource() {
        try { if (osc != null) { osc.stop(); osc = null; } } catch (Exception ignored) {}
        try { if (fileSrc != null) { fileSrc.stop(); fileSrc = null; } } catch (Exception ignored) {}
        if (sourceGain != null) { sourceGain.disconnect(); sourceGain = null; }
        chorus = null; flanger = null; phaser = null; reverb = null; limiter = null;
    }

    private GainNode createSource(boolean useFile) {
        stopSource();
        sourceGain = ctx.createGain();
        sourceGain.getGain().setValue(0.5f);

        if (useFile && musicBuf != null) {
            fileSrc = ctx.createBufferSource();
            fileSrc.setBuffer(musicBuf);
            fileSrc.setLoop(true);
            fileSrc.connect(sourceGain);
            fileSrc.start();
        } else {
            osc = ctx.createOscillator();
            osc.setType(OscillatorType.SAWTOOTH);
            osc.getFrequency().setValue(220);
            osc.connect(sourceGain);
            osc.start();
        }
        return sourceGain;
    }

    private void playWithChorus(boolean useFile) {
        GainNode src = createSource(useFile);
        chorus = ctx.createChorus();
        src.connect(chorus);
        chorus.connect(ctx.getDestination());
        activeEffect = "Chorus";
        status = "Chorus: rate=" + chorus.getRate() + "Hz depth=" + chorus.getDepth() + "ms";
    }

    private void playWithFlanger(boolean useFile) {
        GainNode src = createSource(useFile);
        flanger = ctx.createFlanger();
        src.connect(flanger);
        flanger.connect(ctx.getDestination());
        activeEffect = "Flanger";
        status = "Flanger: rate=" + flanger.getRate() + "Hz feedback=" + flanger.getFeedback();
    }

    private void playWithPhaser(boolean useFile) {
        GainNode src = createSource(useFile);
        phaser = ctx.createPhaser();
        src.connect(phaser);
        phaser.connect(ctx.getDestination());
        activeEffect = "Phaser";
        status = "Phaser: stages=" + phaser.getStages() + " rate=" + phaser.getRate() + "Hz";
    }

    private void playWithReverb(boolean useFile) {
        GainNode src = createSource(useFile);
        reverb = ctx.createReverb();
        src.connect(reverb);
        reverb.connect(ctx.getDestination());
        activeEffect = "Reverb";
        status = "Reverb: room=" + reverb.getRoomSize() + " damping=" + reverb.getDamping();
    }

    private void playWithLimiter(boolean useFile) {
        GainNode src = createSource(useFile);
        // Boost the signal heavily so the limiter has something to clamp
        sourceGain.getGain().setValue(2.0f);
        limiter = ctx.createLimiter();
        src.connect(limiter);
        limiter.connect(ctx.getDestination());
        activeEffect = "Limiter";
        status = "Limiter: ceiling=" + limiter.getCeiling() + "dB gain=" + limiter.getInputGain() + "dB";
    }

    private void tweakUp() {
        if (chorus != null) {
            chorus.setRate(Math.min(5, chorus.getRate() + 0.5f));
            chorus.setDepth(Math.min(10, chorus.getDepth() + 1));
            status = "Chorus: rate=" + chorus.getRate() + "Hz depth=" + chorus.getDepth() + "ms";
        } else if (flanger != null) {
            flanger.setFeedback(Math.min(0.95f, flanger.getFeedback() + 0.1f));
            flanger.setRate(Math.min(3, flanger.getRate() + 0.2f));
            status = "Flanger: rate=" + flanger.getRate() + "Hz feedback=" + String.format("%.1f", flanger.getFeedback());
        } else if (phaser != null) {
            phaser.setStages(Math.min(12, phaser.getStages() + 2));
            phaser.setFeedback(Math.min(0.9f, phaser.getFeedback() + 0.15f));
            status = "Phaser: stages=" + phaser.getStages() + " feedback=" + String.format("%.2f", phaser.getFeedback());
        } else if (reverb != null) {
            reverb.setRoomSize(Math.min(1.0f, reverb.getRoomSize() + 0.1f));
            reverb.setWet(Math.min(1.0f, reverb.getWet() + 0.1f));
            status = "Reverb: room=" + String.format("%.1f", reverb.getRoomSize()) + " wet=" + String.format("%.1f", reverb.getWet());
        } else if (limiter != null) {
            limiter.setInputGain(Math.min(20, limiter.getInputGain() + 3));
            status = "Limiter: ceiling=" + limiter.getCeiling() + "dB gain=" + limiter.getInputGain() + "dB";
        }
    }

    private void tweakDown() {
        if (chorus != null) {
            chorus.setRate(Math.max(0.1f, chorus.getRate() - 0.5f));
            chorus.setDepth(Math.max(0.5f, chorus.getDepth() - 1));
            status = "Chorus: rate=" + chorus.getRate() + "Hz depth=" + chorus.getDepth() + "ms";
        } else if (flanger != null) {
            flanger.setFeedback(Math.max(0, flanger.getFeedback() - 0.1f));
            flanger.setRate(Math.max(0.05f, flanger.getRate() - 0.2f));
            status = "Flanger: rate=" + flanger.getRate() + "Hz feedback=" + String.format("%.1f", flanger.getFeedback());
        } else if (phaser != null) {
            phaser.setStages(Math.max(2, phaser.getStages() - 2));
            phaser.setFeedback(Math.max(0, phaser.getFeedback() - 0.15f));
            status = "Phaser: stages=" + phaser.getStages() + " feedback=" + String.format("%.2f", phaser.getFeedback());
        } else if (reverb != null) {
            reverb.setRoomSize(Math.max(0.1f, reverb.getRoomSize() - 0.1f));
            reverb.setWet(Math.max(0, reverb.getWet() - 0.1f));
            status = "Reverb: room=" + String.format("%.1f", reverb.getRoomSize()) + " wet=" + String.format("%.1f", reverb.getWet());
        } else if (limiter != null) {
            limiter.setInputGain(Math.max(0, limiter.getInputGain() - 3));
            status = "Limiter: ceiling=" + limiter.getCeiling() + "dB gain=" + limiter.getInputGain() + "dB";
        }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Composite Effects: Chorus / Flanger / Phaser / Reverb / Limiter", 20,
                Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, "Active: " + activeEffect, 20, Gdx.graphics.getHeight() - 85);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
                "Chorus (Osc)", "Flanger (Osc)", "Phaser (Osc)",
                "Reverb (Osc)", "Limiter (Osc)", "Chorus (File)",
                "Flanger (File)", "Phaser (File)", "Reverb (File)",
                "Limiter (File)", "Tweak +", "Tweak -",
                "Stop");

        try {
            switch (btn) {
                case 0: playWithChorus(false); break;
                case 1: playWithFlanger(false); break;
                case 2: playWithPhaser(false); break;
                case 3: playWithReverb(false); break;
                case 4: playWithLimiter(false); break;
                case 5: playWithChorus(true); break;
                case 6: playWithFlanger(true); break;
                case 7: playWithPhaser(true); break;
                case 8: playWithReverb(true); break;
                case 9: playWithLimiter(true); break;
                case 10: tweakUp(); break;
                case 11: tweakDown(); break;
                case 12:
                    stopSource();
                    activeEffect = "none";
                    status = "Stopped";
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("Effects", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopSource();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
