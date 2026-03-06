package com.github.satori87.gdx.webaudio.teavm.source;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.source.NoiseNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.*;
import com.github.satori87.gdx.webaudio.types.NoiseType;

/**
 * TeaVM/browser implementation of {@link NoiseNode}.
 *
 * <p>Generates a looping noise buffer and plays it through an internal
 * {@link JSAudioBufferSourceNode}. The noise type is fixed at creation time.</p>
 */
public class TeaVMNoiseNode extends TeaVMAudioNode implements NoiseNode {
    private final JSAudioBufferSourceNode jsSource;
    private final NoiseType noiseType;

    public TeaVMNoiseNode(JSAudioContext jsCtx, WebAudioContext context, NoiseType type) {
        this(context, type, jsCtx.createBufferSource(), jsCtx);
    }

    private TeaVMNoiseNode(WebAudioContext context, NoiseType type,
                           JSAudioBufferSourceNode source, JSAudioContext jsCtx) {
        super(source, context);
        this.jsSource = source;
        this.noiseType = type;

        JSAudioBuffer noiseBuffer = generateNoise(jsCtx, type);
        source.setBuffer(noiseBuffer);
        source.setLoop(true);
    }

    private static JSAudioBuffer generateNoise(JSAudioContext jsCtx, NoiseType type) {
        float sampleRate = jsCtx.getSampleRate();
        int length = (int)(sampleRate * 2); // 2 seconds, looped
        JSAudioBuffer buffer = jsCtx.createBuffer(1, length, sampleRate);
        JSFloat32Array data = buffer.getChannelData(0);

        switch (type) {
            case WHITE:
                for (int i = 0; i < length; i++) {
                    data.set(i, (float)(Math.random() * 2 - 1));
                }
                break;
            case PINK: {
                // Paul Kellet's refined pink noise method
                float b0 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
                for (int i = 0; i < length; i++) {
                    float white = (float)(Math.random() * 2 - 1);
                    b0 = 0.99886f * b0 + white * 0.0555179f;
                    b1 = 0.99332f * b1 + white * 0.0750759f;
                    b2 = 0.96900f * b2 + white * 0.1538520f;
                    b3 = 0.86650f * b3 + white * 0.3104856f;
                    b4 = 0.55000f * b4 + white * 0.5329522f;
                    b5 = -0.7616f * b5 - white * 0.0168980f;
                    float pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f;
                    b6 = white * 0.115926f;
                    data.set(i, pink * 0.11f);
                }
                break;
            }
            case BROWNIAN: {
                float lastOut = 0;
                for (int i = 0; i < length; i++) {
                    float white = (float)(Math.random() * 2 - 1);
                    lastOut = (lastOut + 0.02f * white) / 1.02f;
                    data.set(i, lastOut * 3.5f);
                }
                break;
            }
        }
        return buffer;
    }

    @Override public NoiseType getType() { return noiseType; }
    @Override public void start() { jsSource.start(); }
    @Override public void start(double when) { jsSource.start(when); }
    @Override public void stop() { jsSource.stop(); }
    @Override public void stop(double when) { jsSource.stop(when); }
    @Override public void setOnEnded(Runnable listener) {
        jsSource.setOnended(() -> listener.run());
    }
}
