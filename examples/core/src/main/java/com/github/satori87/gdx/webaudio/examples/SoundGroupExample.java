package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.SoundGroup;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates SoundGroup mixing buses with shared volume/pan/fade controls,
 * and the master volume feature on WebAudioContext.
 * Two groups (Music + SFX) route through their own SoundGroup to the destination.
 */
public class SoundGroupExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private AudioBuffer musicBuf, sfxBuf;
    private boolean loaded = false;
    private int loadCount = 0;
    private String status = "Loading audio...";

    private SoundGroup musicGroup;
    private SoundGroup sfxGroup;
    private AudioBufferSourceNode musicSrc;
    private OscillatorNode oscSrc;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Create two sound groups routed to destination
        musicGroup = ctx.createSoundGroup();
        musicGroup.getOutput().connect(ctx.getDestination());

        sfxGroup = ctx.createSoundGroup();
        sfxGroup.getOutput().connect(ctx.getDestination());

        byte[] d1 = Gdx.files.internal("music-calm.wav").readBytes();
        ctx.decodeAudioData(d1, buf -> { musicBuf = buf; checkLoaded(); },
                () -> status = "Failed to decode music");

        byte[] d2 = Gdx.files.internal("explosion.wav").readBytes();
        ctx.decodeAudioData(d2, buf -> { sfxBuf = buf; checkLoaded(); },
                () -> status = "Failed to decode SFX");
    }

    private void checkLoaded() {
        if (++loadCount >= 2) {
            loaded = true;
            status = "Ready! Music Group + SFX Group -> Destination";
        }
    }

    private void playMusic() {
        stopMusic();
        if (musicBuf == null) return;
        musicSrc = ctx.createBufferSource();
        musicSrc.setBuffer(musicBuf);
        musicSrc.setLoop(true);
        musicSrc.connect(musicGroup.getInput());
        musicSrc.start();
        status = "Music playing through Music Group";
    }

    private void stopMusic() {
        try { if (musicSrc != null) { musicSrc.stop(); musicSrc = null; } } catch (Exception ignored) {}
    }

    private void playSfx() {
        if (sfxBuf == null) return;
        AudioBufferSourceNode sfx = ctx.createBufferSource();
        sfx.setBuffer(sfxBuf);
        sfx.connect(sfxGroup.getInput());
        sfx.start();
        status = "SFX fired through SFX Group";
    }

    private void playOsc() {
        stopOsc();
        oscSrc = ctx.createOscillator();
        oscSrc.setType(OscillatorType.SQUARE);
        oscSrc.getFrequency().setValue(330);
        oscSrc.connect(sfxGroup.getInput());
        oscSrc.start();
        status = "Oscillator playing through SFX Group";
    }

    private void stopOsc() {
        try { if (oscSrc != null) { oscSrc.stop(); oscSrc = null; } } catch (Exception ignored) {}
    }

    private String groupInfo(String name, SoundGroup g) {
        return name + ": vol=" + (int)(g.getVolume() * 100) + "% pan=" + String.format("%.1f", g.getPan());
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "SoundGroup + Master Volume", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 65);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, groupInfo("Music", musicGroup) + "  |  " + groupInfo("SFX", sfxGroup),
                20, Gdx.graphics.getHeight() - 85);
        font.draw(batch, "Master Volume: " + (int)(ctx.getMasterVolume() * 100) + "%",
                20, Gdx.graphics.getHeight() - 105);
        batch.end();

        int btn = drawButtons(shapes, batch, font,
                "Play Music", "Play SFX", "Play Osc->SFX",
                "Music Vol -", "Music Vol +", "Music Pan L -0.5",
                "Music Pan R +0.5", "SFX Vol -", "SFX Vol +",
                "Music Fade In 2s", "Music Fade Out 2s", "Master Vol -",
                "Master Vol +", "Stop Music", "Stop All");

        if (!loaded && btn >= 0 && btn != 14) return;

        try {
            switch (btn) {
                case 0: playMusic(); break;
                case 1: playSfx(); break;
                case 2: playOsc(); break;
                case 3: musicGroup.setVolume(Math.max(0, musicGroup.getVolume() - 0.1f)); break;
                case 4: musicGroup.setVolume(Math.min(2, musicGroup.getVolume() + 0.1f)); break;
                case 5:
                    musicGroup.setPan(Math.max(-1, musicGroup.getPan() - 0.5f));
                    status = "Music pan: " + String.format("%.1f", musicGroup.getPan()) + " (use headphones)";
                    break;
                case 6:
                    musicGroup.setPan(Math.min(1, musicGroup.getPan() + 0.5f));
                    status = "Music pan: " + String.format("%.1f", musicGroup.getPan()) + " (use headphones)";
                    break;
                case 7: sfxGroup.setVolume(Math.max(0, sfxGroup.getVolume() - 0.1f)); break;
                case 8: sfxGroup.setVolume(Math.min(2, sfxGroup.getVolume() + 0.1f)); break;
                case 9:
                    musicGroup.fadeIn(2000);
                    status = "Music Group fading in over 2s";
                    break;
                case 10:
                    musicGroup.fadeOut(2000);
                    status = "Music Group fading out over 2s";
                    break;
                case 11:
                    ctx.setMasterVolume(Math.max(0, ctx.getMasterVolume() - 0.1f));
                    break;
                case 12:
                    ctx.setMasterVolume(Math.min(2, ctx.getMasterVolume() + 0.1f));
                    break;
                case 13: stopMusic(); status = "Music stopped"; break;
                case 14:
                    stopMusic();
                    stopOsc();
                    status = "All stopped";
                    break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("SoundGroup", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopMusic();
        stopOsc();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
