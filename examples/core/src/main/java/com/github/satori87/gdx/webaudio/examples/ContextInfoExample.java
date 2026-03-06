package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.source.AudioBufferSourceNode;
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.types.AudioContextState;
import com.github.satori87.gdx.webaudio.types.ChannelCountMode;
import com.github.satori87.gdx.webaudio.types.ChannelInterpretation;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

/**
 * Demonstrates AudioContext state management (suspend/resume/close),
 * latency info, node properties (channelCount, channelCountMode,
 * channelInterpretation, numberOfInputs/outputs), copyFromChannel,
 * and targeted disconnect.
 */
public class ContextInfoExample extends BaseExample {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private String status = "Ready";
    private String stateInfo = "";
    private String latencyInfo = "";
    private String nodeInfo = "";
    private String bufferInfo = "";
    private String stateChangeLog = "";

    private OscillatorNode testOsc;
    private GainNode testGain;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        ctx.resume(null);

        // Set up state change listener
        ctx.setOnStateChange(() -> stateChangeLog = "onstatechange fired! State: " + ctx.getState().toJsValue());

        updateStateInfo();
        updateLatencyInfo();
    }

    private void updateStateInfo() {
        AudioContextState state = ctx.getState();
        stateInfo = "State: " + state.toJsValue() + "  |  currentTime: "
            + String.format("%.3f", ctx.getCurrentTime())
            + "  |  sampleRate: " + (int) ctx.getSampleRate() + " Hz";
    }

    private void updateLatencyInfo() {
        try {
            latencyInfo = "baseLatency: " + String.format("%.4f", ctx.getBaseLatency()) + "s"
                + "  |  outputLatency: " + String.format("%.4f", ctx.getOutputLatency()) + "s";
        } catch (Exception e) {
            latencyInfo = "Latency info: " + e.getMessage();
        }
    }

    private void doSuspend() {
        ctx.suspend(() -> {
            updateStateInfo();
            status = "Context suspended";
        });
    }

    private void doResume() {
        ctx.resume(() -> {
            updateStateInfo();
            status = "Context resumed";
        });
    }

    private void showNodeProperties() {
        GainNode gain = ctx.createGain();
        OscillatorNode osc = ctx.createOscillator();
        AudioNode dest = ctx.getDestination();

        StringBuilder sb = new StringBuilder();
        sb.append("GainNode — inputs:").append(gain.getNumberOfInputs())
          .append(" outputs:").append(gain.getNumberOfOutputs())
          .append(" channels:").append(gain.getChannelCount())
          .append(" mode:").append(gain.getChannelCountMode().toJsValue())
          .append(" interp:").append(gain.getChannelInterpretation().toJsValue());
        sb.append("\nOscillatorNode — inputs:").append(osc.getNumberOfInputs())
          .append(" outputs:").append(osc.getNumberOfOutputs())
          .append(" channels:").append(osc.getChannelCount());
        sb.append("\nDestination — inputs:").append(dest.getNumberOfInputs())
          .append(" outputs:").append(dest.getNumberOfOutputs())
          .append(" maxChannels:").append(ctx.getDestination().getMaxChannelCount());
        nodeInfo = sb.toString();
        status = "Node properties displayed";
    }

    private void demoCopyFromChannel() {
        // Create a buffer with known data, then copy it out
        float sr = ctx.getSampleRate();
        AudioBuffer buf = ctx.createBuffer(2, 100, sr);
        float[] source = new float[100];
        for (int i = 0; i < 100; i++) {
            source[i] = (float) Math.sin(2 * Math.PI * i / 100);
        }
        buf.copyToChannel(source, 0);

        // Now copy it back out using copyFromChannel
        float[] dest = new float[100];
        buf.copyFromChannel(dest, 0);

        // Verify
        boolean match = true;
        for (int i = 0; i < 100; i++) {
            if (Math.abs(source[i] - dest[i]) > 0.0001f) { match = false; break; }
        }
        bufferInfo = "copyToChannel -> copyFromChannel: " + (match ? "MATCH (100 samples verified)" : "MISMATCH!")
            + "\nBuffer: channels=" + buf.getNumberOfChannels()
            + " length=" + buf.getLength()
            + " sampleRate=" + (int) buf.getSampleRate()
            + " duration=" + String.format("%.6f", buf.getDuration()) + "s";
        status = "copyFromChannel test " + (match ? "passed" : "FAILED");
    }

    private void demoTargetedDisconnect() {
        stopTest();
        testOsc = ctx.createOscillator();
        testOsc.setType(OscillatorType.SINE);
        testOsc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

        testGain = ctx.createGain();
        testGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());

        testOsc.connect(testGain);
        testGain.connect(ctx.getDestination());
        testOsc.start();
        status = "Oscillator playing through GainNode — click 'Disconnect Gain' to remove it";
    }

    private void disconnectGain() {
        if (testGain != null) {
            testGain.disconnect(ctx.getDestination());
            status = "GainNode disconnected from destination — sound should stop";
        }
    }

    private void demoChannelProps() {
        GainNode gain = ctx.createGain();
        // Change channel properties
        gain.setChannelCount(1);
        gain.setChannelCountMode(ChannelCountMode.EXPLICIT);
        gain.setChannelInterpretation(ChannelInterpretation.DISCRETE);

        nodeInfo = "After modification:"
            + "\nchannelCount: " + gain.getChannelCount()
            + " (set to 1)"
            + "\nchannelCountMode: " + gain.getChannelCountMode().toJsValue()
            + " (set to explicit)"
            + "\nchannelInterpretation: " + gain.getChannelInterpretation().toJsValue()
            + " (set to discrete)";
        status = "Channel properties modified on GainNode";
    }

    private void stopTest() {
        try { if (testOsc != null) { testOsc.stop(); testOsc = null; } } catch (Exception ignored) {}
        if (testGain != null) { testGain.disconnect(); testGain = null; }
    }

    @Override
    public void render() {
        applyViewport(batch, shapes);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        updateStateInfo();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Context & Node Info", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, status, 20, Gdx.graphics.getHeight() - 60);
        font.setColor(0.7f, 0.85f, 1, 1);
        font.draw(batch, stateInfo, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, latencyInfo, 20, Gdx.graphics.getHeight() - 96);
        if (!stateChangeLog.isEmpty()) {
            font.setColor(1, 1, 0.3f, 1);
            font.draw(batch, stateChangeLog, 20, Gdx.graphics.getHeight() - 112);
        }
        if (!nodeInfo.isEmpty()) {
            font.setColor(0.8f, 1, 0.8f, 1);
            String[] lines = nodeInfo.split("\n");
            for (int i = 0; i < lines.length; i++) {
                font.draw(batch, lines[i], 20, Gdx.graphics.getHeight() - 132 - i * 16);
            }
        }
        if (!bufferInfo.isEmpty()) {
            font.setColor(0.8f, 0.8f, 1, 1);
            String[] lines = bufferInfo.split("\n");
            for (int i = 0; i < lines.length; i++) {
                font.draw(batch, lines[i], 20, Gdx.graphics.getHeight() - 200 - i * 16);
            }
        }
        batch.end();

        int btn = drawButtons(shapes, batch, font,
            "Suspend Context", "Resume Context", "Refresh Info",
            "Node Properties", "Set Channel Props", "copyFromChannel Test",
            "Play (test disconnect)", "Disconnect Gain", "Stop"
        );

        try {
            switch (btn) {
                case 0: doSuspend(); break;
                case 1: doResume(); break;
                case 2:
                    updateStateInfo();
                    updateLatencyInfo();
                    status = "Info refreshed";
                    break;
                case 3: showNodeProperties(); break;
                case 4: demoChannelProps(); break;
                case 5: demoCopyFromChannel(); break;
                case 6: demoTargetedDisconnect(); break;
                case 7: disconnectGain(); break;
                case 8: stopTest(); status = "Stopped"; break;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            Gdx.app.error("CtxInfo", "Error", e);
        }
    }

    @Override
    public void dispose() {
        stopTest();
        batch.dispose();
        font.dispose();
        shapes.dispose();
        super.dispose();
    }
}
