package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Real-time audio visualization using AnalyserNode.
 * Can visualize both oscillator and loaded music file.
 */
public class AudioVisualizerExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private AnalyserNode analyser;
    private byte[] frequencyData;
    private float[] frequencyFloatData;
    private float[] waveformData;
    private byte[] waveformByteData;
    private OscillatorNode osc;
    private AudioBufferSourceNode musicSrc;
    private AudioBuffer musicBuffer;
    private boolean playing;
    private String source = "";
    private boolean showFloatFreq = false;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        analyser = ctx.createAnalyser();
        analyser.setFftSize(256);
        analyser.setSmoothingTimeConstant(0.8f);
        analyser.setMinDecibels(-90);
        analyser.setMaxDecibels(-10);

        frequencyData = new byte[analyser.getFrequencyBinCount()];
        frequencyFloatData = new float[analyser.getFrequencyBinCount()];
        waveformData = new float[analyser.getFftSize()];
        waveformByteData = new byte[analyser.getFftSize()];

        byte[] data = Gdx.files.internal("music-chiptune.wav").readBytes();
        ctx.decodeAudioData(data, buf -> musicBuffer = buf,
                () -> Gdx.app.error("Visualizer", "Failed to load music"));
    }

    private void stopAll() {
        if (osc != null) { osc.stop(); osc = null; }
        if (musicSrc != null) {
            try { musicSrc.stop(); } catch (Exception ignored) {}
            musicSrc = null;
        }
        playing = false;
    }

    private void startOscillator() {
        stopAll();
        osc = ctx.createOscillator();
        osc.setType(OscillatorType.SAWTOOTH);
        osc.getFrequency().setValue(220);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(0.3f);
        osc.connect(gain);
        gain.connect(analyser);
        analyser.connect(ctx.getDestination());
        osc.start();
        playing = true;
        source = "Oscillator (sawtooth 220Hz)";
    }

    private void startMusic() {
        if (musicBuffer == null) return;
        stopAll();
        musicSrc = ctx.createBufferSource();
        musicSrc.setBuffer(musicBuffer);
        musicSrc.setLoop(true);
        GainNode gain = ctx.createGain();
        gain.getGain().setValue(0.5f);
        musicSrc.connect(gain);
        gain.connect(analyser);
        analyser.connect(ctx.getDestination());
        musicSrc.start();
        playing = true;
        source = "Music (chiptune)";
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        if (playing) {
            analyser.getByteFrequencyData(frequencyData);
            analyser.getFloatFrequencyData(frequencyFloatData);
            analyser.getFloatTimeDomainData(waveformData);
            analyser.getByteTimeDomainData(waveformByteData);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            float barWidth = (float) w / frequencyData.length;
            int rows = (6 + BTN_COLS - 1) / BTN_COLS;  // account for buttons
            int barBase = BTN_START_Y + rows * (BTN_H + BTN_GAP) + 10;
            if (!showFloatFreq) {
                // Byte frequency data (0-255)
                for (int i = 0; i < frequencyData.length; i++) {
                    float val = (frequencyData[i] & 0xFF) / 255f;
                    float barHeight = val * h * 0.3f;
                    shapes.setColor(val, 0.3f, 1 - val, 0.8f);
                    shapes.rect(i * barWidth, barBase, barWidth - 1, barHeight);
                }
            } else {
                // Float frequency data (dB scale, typically -100 to 0)
                for (int i = 0; i < frequencyFloatData.length; i++) {
                    float db = frequencyFloatData[i];
                    float val = Math.max(0, (db + 100) / 100f); // normalize -100..0 to 0..1
                    float barHeight = val * h * 0.3f;
                    shapes.setColor(0.3f, val, 1 - val, 0.8f);
                    shapes.rect(i * barWidth, barBase, barWidth - 1, barHeight);
                }
            }
            shapes.end();

            shapes.begin(ShapeRenderer.ShapeType.Line);
            // Float time-domain (green) — upper waveform
            float greenY = h * 0.78f;
            shapes.setColor(0, 1, 0, 1);
            for (int i = 0; i < waveformData.length - 1; i++) {
                float x1 = (float) i / waveformData.length * w;
                float x2 = (float) (i + 1) / waveformData.length * w;
                float y1 = greenY + waveformData[i] * h * 0.08f;
                float y2 = greenY + waveformData[i + 1] * h * 0.08f;
                shapes.line(x1, y1, x2, y2);
            }
            // Byte time-domain (yellow) — lower waveform
            float yellowY = h * 0.62f;
            shapes.setColor(1, 1, 0, 1);
            for (int i = 0; i < waveformByteData.length - 1; i++) {
                float x1 = (float) i / waveformByteData.length * w;
                float x2 = (float) (i + 1) / waveformByteData.length * w;
                float val1 = ((waveformByteData[i] & 0xFF) - 128) / 128f;
                float val2 = ((waveformByteData[i + 1] & 0xFF) - 128) / 128f;
                shapes.line(x1, yellowY + val1 * h * 0.08f, x2, yellowY + val2 * h * 0.08f);
            }
            shapes.end();
        }

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Audio Visualizer (AnalyserNode — all 4 data methods)", 150, h - 10);
        if (playing) {
            font.draw(batch, "Source: " + source, 150, h - 28);
            font.setColor(0.7f, 0.85f, 1, 1);
            font.draw(batch, "Bars: " + (showFloatFreq ? "getFloatFrequencyData (dB)" : "getByteFrequencyData (0-255)"), 150, h - 46);
            font.setColor(0, 1, 0, 1);
            font.draw(batch, "Green line: getFloatTimeDomainData", 150, h - 64);
            font.setColor(1, 1, 0, 1);
            font.draw(batch, "Yellow line: getByteTimeDomainData", 150, h - 82);
        }
        batch.end();

        int clicked = drawButtons(shapes, batch, font, "Start Oscillator", "Start Music", "Stop",
            "Toggle Float/Byte Freq");
        if (clicked == 0) startOscillator();
        else if (clicked == 1) startMusic();
        else if (clicked == 2) stopAll();
        else if (clicked == 3) showFloatFreq = !showFloatFreq;
    }

    @Override
    public void dispose() {
        stopAll();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
