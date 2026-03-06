package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.effect.BiquadFilterNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

public class SynthesizerExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private static final String[] KEY_LABELS = {"C", "D", "E", "F", "G", "A", "B", "C"};
    private static final float[] KEY_FREQS = {
        261.63f, 293.66f, 329.63f, 349.23f, 392.00f, 440.00f, 493.88f, 523.25f
    };

    private static final int KEY_WIDTH = 80;
    private static final int KEY_HEIGHT = 160;
    private static final int KEY_GAP = 4;
    private static final int KEYS_Y = 40;

    private int lastPlayedKey = -1;
    private String status = "Click a key to play";

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);
    }

    private int getKeysStartX() {
        int totalWidth = KEY_FREQS.length * (KEY_WIDTH + KEY_GAP) - KEY_GAP;
        return (Gdx.graphics.getWidth() - totalWidth) / 2;
    }

    private int getKeyAt(int x, int y) {
        if (y < KEYS_Y || y > KEYS_Y + KEY_HEIGHT) return -1;
        int startX = getKeysStartX();
        for (int i = 0; i < KEY_FREQS.length; i++) {
            int kx = startX + i * (KEY_WIDTH + KEY_GAP);
            if (x >= kx && x < kx + KEY_WIDTH) return i;
        }
        return -1;
    }

    private void playNoteWithADSR(float frequency) {
        double now = ctx.getCurrentTime();
        float attack = 0.02f, decay = 0.1f, sustain = 0.6f, release = 0.4f;
        float duration = attack + decay + 0.2f + release;

        // Two detuned sawtooth oscillators for a thick sound
        OscillatorNode osc1 = ctx.createOscillator();
        osc1.setType(OscillatorType.SAWTOOTH);
        osc1.getFrequency().setValueAtTime(frequency, now);

        OscillatorNode osc2 = ctx.createOscillator();
        osc2.setType(OscillatorType.SAWTOOTH);
        osc2.getFrequency().setValueAtTime(frequency, now);
        osc2.getDetune().setValueAtTime(7, now); // 7 cents sharp

        // Mix the two oscillators
        GainNode mix = ctx.createGain();
        mix.getGain().setValueAtTime(0.5f, now);
        osc1.connect(mix);
        osc2.connect(mix);

        // Low-pass filter with envelope for brightness sweep
        BiquadFilterNode filter = ctx.createBiquadFilter();
        filter.setType(BiquadFilterType.LOWPASS);
        filter.getQ().setValueAtTime(5, now);
        filter.getFrequency().setValueAtTime(200, now);
        filter.getFrequency().linearRampToValueAtTime(4000, now + attack);
        filter.getFrequency().linearRampToValueAtTime(1500, now + attack + decay);
        filter.getFrequency().linearRampToValueAtTime(400, now + duration);

        // LFO for subtle vibrato
        OscillatorNode lfo = ctx.createOscillator();
        lfo.setType(OscillatorType.SINE);
        lfo.getFrequency().setValueAtTime(5, now); // 5 Hz vibrato
        GainNode lfoDepth = ctx.createGain();
        lfoDepth.getGain().setValueAtTime(6, now); // +/- 6 Hz
        lfo.connect(lfoDepth);
        lfoDepth.connectParam(osc1.getFrequency());
        lfoDepth.connectParam(osc2.getFrequency());

        // ADSR envelope on gain
        GainNode envelope = ctx.createGain();
        envelope.getGain().setValueAtTime(0, now);
        envelope.getGain().linearRampToValueAtTime(0.4f, now + attack);
        envelope.getGain().linearRampToValueAtTime(sustain * 0.4f, now + attack + decay);
        envelope.getGain().setValueAtTime(sustain * 0.4f, now + duration - release);
        envelope.getGain().linearRampToValueAtTime(0, now + duration);

        // Signal chain: oscillators -> mix -> filter -> envelope -> destination
        mix.connect(filter);
        filter.connect(envelope);
        envelope.connect(ctx.getDestination());

        osc1.start(now);
        osc2.start(now);
        lfo.start(now);
        osc1.stop(now + duration);
        osc2.stop(now + duration);
        lfo.stop(now + duration);
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        int startX = getKeysStartX();

        // ---- DRAW FIRST (before any audio calls) ----

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < KEY_FREQS.length; i++) {
            int kx = startX + i * (KEY_WIDTH + KEY_GAP);
            if (i == lastPlayedKey) {
                shapes.setColor(0.5f, 0.7f, 1f, 1f);
            } else {
                shapes.setColor(0.9f, 0.9f, 0.9f, 1f);
            }
            shapes.rect(kx, KEYS_Y, KEY_WIDTH, KEY_HEIGHT);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.3f, 0.3f, 0.3f, 1f);
        for (int i = 0; i < KEY_FREQS.length; i++) {
            int kx = startX + i * (KEY_WIDTH + KEY_GAP);
            shapes.rect(kx, KEYS_Y, KEY_WIDTH, KEY_HEIGHT);
        }
        shapes.end();

        batch.begin();
        font.setColor(0.2f, 0.2f, 0.2f, 1f);
        for (int i = 0; i < KEY_LABELS.length; i++) {
            int kx = startX + i * (KEY_WIDTH + KEY_GAP);
            font.draw(batch, KEY_LABELS[i], kx + KEY_WIDTH / 2f - 4, KEYS_Y + 25);
        }
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Synthesizer Example", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Click the piano keys to play notes", 20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 90);
        batch.end();

        // ---- THEN handle input (after all drawing is done) ----

        lastPlayedKey = -1;
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            int clickedKey = getKeyAt(touchX, touchY);
            if (clickedKey >= 0) {
                lastPlayedKey = clickedKey;
                try {
                    playNoteWithADSR(KEY_FREQS[clickedKey]);
                    status = "Playing: " + KEY_LABELS[clickedKey] + " ("
                            + (int) KEY_FREQS[clickedKey] + " Hz)";
                } catch (Exception e) {
                    status = "Error: " + e.getMessage();
                    Gdx.app.error("Synth", "Failed to play note", e);
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
