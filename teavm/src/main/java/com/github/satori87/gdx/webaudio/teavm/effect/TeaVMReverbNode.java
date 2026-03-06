package com.github.satori87.gdx.webaudio.teavm.effect;

import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.effect.ReverbNode;
import com.github.satori87.gdx.webaudio.teavm.TeaVMCompositeAudioNode;
import com.github.satori87.gdx.webaudio.teavm.jso.*;

/**
 * TeaVM/browser implementation of {@link ReverbNode}.
 *
 * <p>Generates a synthetic impulse response based on room size and damping parameters,
 * using a ConvolverNode internally. The IR is regenerated when parameters change.</p>
 */
public class TeaVMReverbNode extends TeaVMCompositeAudioNode implements ReverbNode {
    private final JSAudioContext jsCtx;
    private final JSConvolverNode convolver;
    private final JSGainNode wetGain;
    private final JSGainNode dryGain;
    private float roomSize = 0.5f;
    private float damping = 0.5f;
    private float wet = 0.5f;
    private float dry = 0.5f;

    public TeaVMReverbNode(JSAudioContext jsCtx, WebAudioContext context) {
        this(jsCtx, context, jsCtx.createGain(), jsCtx.createGain());
    }

    private TeaVMReverbNode(JSAudioContext jsCtx, WebAudioContext context,
                            JSGainNode inputGain, JSGainNode outputGain) {
        super(inputGain, outputGain, context);
        this.jsCtx = jsCtx;

        this.convolver = jsCtx.createConvolver();
        this.wetGain = jsCtx.createGain();
        this.dryGain = jsCtx.createGain();

        wetGain.getGain().setValue(wet);
        dryGain.getGain().setValue(dry);

        // Dry path
        inputGain.connect(dryGain);
        dryGain.connect(outputGain);

        // Wet path
        inputGain.connect(convolver);
        convolver.connect(wetGain);
        wetGain.connect(outputGain);

        regenerateIR();
    }

    private void regenerateIR() {
        float sampleRate = jsCtx.getSampleRate();
        // Room size maps to reverb tail length: 0.1s to 5s
        float reverbTime = 0.1f + roomSize * 4.9f;
        int length = (int)(reverbTime * sampleRate);
        if (length < 1) length = 1;

        JSAudioBuffer buffer = jsCtx.createBuffer(2, length, sampleRate);
        for (int ch = 0; ch < 2; ch++) {
            JSFloat32Array data = buffer.getChannelData(ch);
            float lastSample = 0;
            for (int i = 0; i < length; i++) {
                float noise = (float)(Math.random() * 2 - 1);
                float decay = (float)Math.exp(-3.0 * i / length);
                float sample = noise * decay;
                // Low-pass filtering for damping
                sample = sample * (1 - damping) + lastSample * damping;
                lastSample = sample;
                data.set(i, sample);
            }
        }
        convolver.setBuffer(buffer);
    }

    @Override public float getRoomSize() { return roomSize; }
    @Override public void setRoomSize(float roomSize) {
        this.roomSize = Math.max(0, Math.min(1, roomSize));
        regenerateIR();
    }
    @Override public float getDamping() { return damping; }
    @Override public void setDamping(float damping) {
        this.damping = Math.max(0, Math.min(1, damping));
        regenerateIR();
    }
    @Override public float getWet() { return wet; }
    @Override public void setWet(float wet) { this.wet = wet; wetGain.getGain().setValue(wet); }
    @Override public float getDry() { return dry; }
    @Override public void setDry(float dry) { this.dry = dry; dryGain.getGain().setValue(dry); }
}
