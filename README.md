# gdx-webaudio

[![](https://jitpack.io/v/satori87/gdx-webaudio.svg)](https://jitpack.io/#satori87/gdx-webaudio)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)]()

A comprehensive Java abstraction over the [Web Audio API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API) for [libGDX](https://libgdx.com/) projects targeting the web via [TeaVM](https://teavm.org/). Provides a full-featured, type-safe audio engine with oscillators, filters, effects, spatial audio, analysis, and more.

## Features

- **Complete Web Audio API coverage** — Oscillators, buffer sources, gain, filters, delay, reverb, compression, wave shaping, stereo panning, and more
- **3D spatial audio** — PannerNode with HRTF/equalpower models, distance attenuation, and directional cones
- **High-level spatial scenes** — `SpatialAudioScene2D` and `SpatialAudioScene3D` integrate directly with libGDX cameras
- **Audio parameter automation** — Schedule precise value changes with ramps, curves, and targets
- **Real-time analysis** — FFT spectrum and waveform data via AnalyserNode
- **Offline rendering** — Render audio graphs to buffers without real-time playback
- **AudioWorklet support** — Load custom JavaScript DSP processors
- **Custom waveforms** — Create PeriodicWave with arbitrary harmonic content
- **Type-safe enums** — All Web Audio string constants mapped to Java enums
- **libGDX integration** — Works with OrthographicCamera, PerspectiveCamera, Vector2, Vector3

## Installation

Add the JitPack repository and dependencies to your Gradle build:

```groovy
// In your root build.gradle or settings.gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    // Core API (platform-agnostic interfaces) — add to your core module
    implementation 'com.github.satori87.gdx-webaudio:core:TAG'

    // TeaVM implementation (browser backend) — add to your teavm/html module
    implementation 'com.github.satori87.gdx-webaudio:teavm:TAG'
}
```

Replace `TAG` with a release tag or commit hash (e.g., `1.0.0` or `main-SNAPSHOT`).

**Requirements:** Java 11+, libGDX 1.14.0+, TeaVM 0.10.2+

## Quick Start

### Initialization

Initialize the TeaVM platform before creating any audio contexts. This is typically done in your TeaVM launcher:

```java
import com.github.satori87.gdx.webaudio.WebAudio;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import com.github.satori87.gdx.webaudio.teavm.TeaVMWebAudio;

// In your TeaVM launcher's main():
TeaVMWebAudio.initialize();

// Then in your game code, create a context:
WebAudioContext ctx = WebAudio.createContext();

// Resume the context (required due to browser autoplay policies):
ctx.resume(null);
```

### Playing a Sound File

```java
// Load and decode audio data
byte[] wavData = Gdx.files.internal("coin.wav").readBytes();
ctx.decodeAudioData(wavData, buffer -> {
    // Create a source, connect to output, and play
    AudioBufferSourceNode source = ctx.createBufferSource();
    source.setBuffer(buffer);
    source.connect(ctx.getDestination());
    source.start();
}, () -> {
    Gdx.app.error("Audio", "Failed to decode audio");
});
```

### Playing an Oscillator Tone

```java
import com.github.satori87.gdx.webaudio.source.OscillatorNode;
import com.github.satori87.gdx.webaudio.effect.GainNode;
import com.github.satori87.gdx.webaudio.types.OscillatorType;

OscillatorNode osc = ctx.createOscillator();
osc.setType(OscillatorType.SINE);
osc.getFrequency().setValueAtTime(440, ctx.getCurrentTime()); // A4 = 440Hz

GainNode gain = ctx.createGain();
gain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());

osc.connect(gain);
gain.connect(ctx.getDestination());
osc.start();

// Stop after 2 seconds
osc.stop(ctx.getCurrentTime() + 2.0);
```

---

## Simple Examples

### Volume Control

```java
GainNode volume = ctx.createGain();
volume.getGain().setValueAtTime(0.5f, ctx.getCurrentTime()); // 50% volume

// Connect any source through the gain node
source.connect(volume);
volume.connect(ctx.getDestination());

// Change volume over time (fade out over 1 second)
double now = ctx.getCurrentTime();
volume.getGain().setValueAtTime(0.5f, now);
volume.getGain().linearRampToValueAtTime(0.0f, now + 1.0);
```

### Looping Music

```java
AudioBufferSourceNode music = ctx.createBufferSource();
music.setBuffer(musicBuffer);
music.setLoop(true);
music.setLoopStart(0.0);    // Loop from the beginning
music.setLoopEnd(0.0);      // 0 = loop to the end of the buffer
music.connect(ctx.getDestination());
music.start();

// To stop:
music.stop();
```

### Stereo Panning

```java
import com.github.satori87.gdx.webaudio.effect.StereoPannerNode;

StereoPannerNode panner = ctx.createStereoPanner();
// Pan value: -1.0 = full left, 0.0 = center, 1.0 = full right
panner.getPan().setValueAtTime(-0.7f, ctx.getCurrentTime()); // Mostly left

source.connect(panner);
panner.connect(ctx.getDestination());

// Animate pan from left to right over 3 seconds
double now = ctx.getCurrentTime();
panner.getPan().setValueAtTime(-1.0f, now);
panner.getPan().linearRampToValueAtTime(1.0f, now + 3.0);
```

### Low-Pass Filter

```java
import com.github.satori87.gdx.webaudio.effect.BiquadFilterNode;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;

BiquadFilterNode filter = ctx.createBiquadFilter();
filter.setType(BiquadFilterType.LOWPASS);
filter.getFrequency().setValueAtTime(800, ctx.getCurrentTime());  // Cutoff at 800Hz
filter.getQ().setValueAtTime(5, ctx.getCurrentTime());            // Resonance

source.connect(filter);
filter.connect(ctx.getDestination());
```

### Playback Rate and Detune

```java
AudioBufferSourceNode source = ctx.createBufferSource();
source.setBuffer(buffer);

// Play at 1.5x speed (also raises pitch)
source.getPlaybackRate().setValueAtTime(1.5f, ctx.getCurrentTime());

// Or detune without changing speed (in cents: 100 cents = 1 semitone)
source.getDetune().setValueAtTime(700, ctx.getCurrentTime()); // Up a perfect 5th

source.connect(ctx.getDestination());
source.start();
```

### Playing a Slice of Audio

```java
AudioBufferSourceNode source = ctx.createBufferSource();
source.setBuffer(buffer);
source.connect(ctx.getDestination());

// Play starting at 2-second offset
source.start(ctx.getCurrentTime(), 2.0);

// Or play a specific duration: start at 1s offset, play for 1.5s
source.start(ctx.getCurrentTime(), 1.0, 1.5);
```

### OnEnded Callback

```java
OscillatorNode osc = ctx.createOscillator();
osc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());
osc.connect(ctx.getDestination());

osc.setOnEnded(() -> {
    Gdx.app.log("Audio", "Oscillator finished playing!");
});

osc.start();
osc.stop(ctx.getCurrentTime() + 1.0); // Plays for 1 second, then onEnded fires
```

---

## Advanced Examples

### Synthesizer with ADSR Envelope

```java
public void playNote(float frequency, float duration) {
    OscillatorNode osc = ctx.createOscillator();
    osc.setType(OscillatorType.SAWTOOTH);
    osc.getFrequency().setValueAtTime(frequency, ctx.getCurrentTime());

    GainNode envelope = ctx.createGain();
    double now = ctx.getCurrentTime();

    // ADSR envelope
    float attack = 0.02f, decay = 0.1f, sustain = 0.7f, release = 0.3f;
    envelope.getGain().setValueAtTime(0, now);
    envelope.getGain().linearRampToValueAtTime(1.0f, now + attack);
    envelope.getGain().linearRampToValueAtTime(sustain, now + attack + decay);
    envelope.getGain().setValueAtTime(sustain, now + duration - release);
    envelope.getGain().linearRampToValueAtTime(0, now + duration);

    // Low-pass filter for warmth
    BiquadFilterNode filter = ctx.createBiquadFilter();
    filter.setType(BiquadFilterType.LOWPASS);
    filter.getFrequency().setValueAtTime(2000, ctx.getCurrentTime());
    filter.getQ().setValueAtTime(5, ctx.getCurrentTime());

    osc.connect(filter);
    filter.connect(envelope);
    envelope.connect(ctx.getDestination());

    osc.start(now);
    osc.stop(now + duration);
}
```

### Vibrato with LFO (Low Frequency Oscillator)

```java
OscillatorNode osc = ctx.createOscillator();
osc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

// LFO modulates the main oscillator's frequency
OscillatorNode lfo = ctx.createOscillator();
lfo.setType(OscillatorType.SINE);
lfo.getFrequency().setValueAtTime(5, ctx.getCurrentTime()); // 5Hz vibrato rate

GainNode lfoDepth = ctx.createGain();
lfoDepth.getGain().setValueAtTime(10, ctx.getCurrentTime()); // ±10Hz depth

lfo.connect(lfoDepth);
lfoDepth.connectParam(osc.getFrequency()); // Connect LFO to frequency param

osc.connect(ctx.getDestination());
osc.start();
lfo.start();
```

### Tremolo with ConstantSourceNode

```java
import com.github.satori87.gdx.webaudio.source.ConstantSourceNode;

OscillatorNode osc = ctx.createOscillator();
osc.getFrequency().setValueAtTime(440, ctx.getCurrentTime());

// ConstantSourceNode provides a DC offset for gain modulation
ConstantSourceNode constant = ctx.createConstantSource();
constant.getOffset().setValueAtTime(0.5f, ctx.getCurrentTime());

// LFO modulates the constant source's offset
OscillatorNode lfo = ctx.createOscillator();
lfo.getFrequency().setValueAtTime(4, ctx.getCurrentTime()); // 4Hz tremolo
GainNode lfoDepth = ctx.createGain();
lfoDepth.getGain().setValueAtTime(0.4f, ctx.getCurrentTime());
lfo.connect(lfoDepth);
lfoDepth.connectParam(constant.getOffset());

// Use the constant source to modulate a gain node
GainNode tremolo = ctx.createGain();
constant.connectParam(tremolo.getGain());

osc.connect(tremolo);
tremolo.connect(ctx.getDestination());

osc.start();
constant.start();
lfo.start();
```

### Effect Chain (Filter + Delay + Compressor)

```java
import com.github.satori87.gdx.webaudio.effect.*;
import com.github.satori87.gdx.webaudio.types.BiquadFilterType;

// EQ filter
BiquadFilterNode eq = ctx.createBiquadFilter();
eq.setType(BiquadFilterType.PEAKING);
eq.getFrequency().setValueAtTime(1000, ctx.getCurrentTime());
eq.getGain().setValueAtTime(3, ctx.getCurrentTime()); // +3dB at 1kHz
eq.getQ().setValueAtTime(1, ctx.getCurrentTime());

// Feedback delay (echo effect)
DelayNode delay = ctx.createDelay(1.0f);
delay.getDelayTime().setValueAtTime(0.25f, ctx.getCurrentTime());
GainNode feedback = ctx.createGain();
feedback.getGain().setValueAtTime(0.4f, ctx.getCurrentTime());
delay.connect(feedback);
feedback.connect(delay); // Feedback loop

// Dry/wet mix
GainNode dryGain = ctx.createGain();
dryGain.getGain().setValueAtTime(0.7f, ctx.getCurrentTime());
GainNode wetGain = ctx.createGain();
wetGain.getGain().setValueAtTime(0.3f, ctx.getCurrentTime());

// Compressor to prevent clipping
DynamicsCompressorNode compressor = ctx.createDynamicsCompressor();
compressor.getThreshold().setValueAtTime(-24, ctx.getCurrentTime());
compressor.getKnee().setValueAtTime(30, ctx.getCurrentTime());
compressor.getRatio().setValueAtTime(12, ctx.getCurrentTime());
compressor.getAttack().setValueAtTime(0.003f, ctx.getCurrentTime());
compressor.getRelease().setValueAtTime(0.25f, ctx.getCurrentTime());

// Wire it up: source → EQ → dry/wet split → compressor → output
source.connect(eq);
eq.connect(dryGain);
eq.connect(delay);
delay.connect(wetGain);
dryGain.connect(compressor);
wetGain.connect(compressor);
compressor.connect(ctx.getDestination());
```

### Convolution Reverb

```java
import com.github.satori87.gdx.webaudio.effect.ConvolverNode;

// Create a convolver with a loaded impulse response
ConvolverNode reverb = ctx.createConvolver();

byte[] irData = Gdx.files.internal("impulse-response.wav").readBytes();
ctx.decodeAudioData(irData, irBuffer -> {
    reverb.setBuffer(irBuffer);
    reverb.setNormalize(true);
}, () -> Gdx.app.error("Audio", "Failed to decode IR"));

// Dry/wet routing
GainNode dryGain = ctx.createGain();
dryGain.getGain().setValueAtTime(0.6f, ctx.getCurrentTime());
GainNode wetGain = ctx.createGain();
wetGain.getGain().setValueAtTime(0.4f, ctx.getCurrentTime());

source.connect(dryGain);
source.connect(reverb);
reverb.connect(wetGain);
dryGain.connect(ctx.getDestination());
wetGain.connect(ctx.getDestination());
```

You can also synthesize an impulse response procedurally:

```java
// Generate a synthetic reverb IR (exponential decay noise)
float sr = ctx.getSampleRate();
int irLen = (int)(sr * 2); // 2-second reverb tail
AudioBuffer synthIR = ctx.createBuffer(2, irLen, sr);
for (int ch = 0; ch < 2; ch++) {
    float[] data = synthIR.getChannelData(ch);
    for (int i = 0; i < irLen; i++) {
        float t = (float)i / sr;
        float decay = (float)Math.exp(-t * 3.0);
        data[i] = ((float)(Math.random() * 2 - 1)) * decay;
    }
    synthIR.copyToChannel(data, ch);
}
reverb.setBuffer(synthIR);
```

### Wave Shaping (Distortion)

```java
import com.github.satori87.gdx.webaudio.effect.WaveShaperNode;
import com.github.satori87.gdx.webaudio.types.OverSampleType;

WaveShaperNode distortion = ctx.createWaveShaper();

// Generate a distortion curve
float amount = 50;
int samples = 44100;
float[] curve = new float[samples];
for (int i = 0; i < samples; i++) {
    float x = i * 2f / samples - 1;
    curve[i] = (float)((3 + amount) * x * 20 * Math.PI / 180
        / (Math.PI + amount * Math.abs(x)));
}
distortion.setCurve(curve);
distortion.setOversample(OverSampleType.FOUR_X); // Reduce aliasing

source.connect(distortion);
distortion.connect(ctx.getDestination());
```

### Dynamic Music Crossfading

```java
// Start two music layers simultaneously
GainNode calmGain = ctx.createGain();
calmGain.getGain().setValueAtTime(1.0f, ctx.getCurrentTime());
calmGain.connect(ctx.getDestination());

GainNode combatGain = ctx.createGain();
combatGain.getGain().setValueAtTime(0.0f, ctx.getCurrentTime());
combatGain.connect(ctx.getDestination());

AudioBufferSourceNode calmLayer = ctx.createBufferSource();
calmLayer.setBuffer(calmBuffer);
calmLayer.setLoop(true);
calmLayer.connect(calmGain);

AudioBufferSourceNode combatLayer = ctx.createBufferSource();
combatLayer.setBuffer(combatBuffer);
combatLayer.setLoop(true);
combatLayer.connect(combatGain);

// Start both at the same time
double startTime = ctx.getCurrentTime() + 0.1;
calmLayer.start(startTime);
combatLayer.start(startTime);

// Crossfade to combat music over 2 seconds
public void enterCombat() {
    double now = ctx.getCurrentTime();
    calmGain.getGain().setValueAtTime(1.0f, now);
    calmGain.getGain().linearRampToValueAtTime(0.0f, now + 2.0);
    combatGain.getGain().setValueAtTime(0.0f, now);
    combatGain.getGain().linearRampToValueAtTime(1.0f, now + 2.0);
}
```

### Custom Waveforms with PeriodicWave

```java
import com.github.satori87.gdx.webaudio.PeriodicWave;

OscillatorNode osc = ctx.createOscillator();

// Define harmonics: fundamental + 3rd + 5th (organ-like tone)
float[] real = {0, 0, 0, 0, 0, 0};       // Cosine components
float[] imag = {0, 1, 0, 0.5f, 0, 0.25f}; // Sine components

PeriodicWave wave = ctx.createPeriodicWave(real, imag);
osc.setPeriodicWave(wave);
osc.getFrequency().setValueAtTime(220, ctx.getCurrentTime());

// With disableNormalization (preserves original amplitudes)
PeriodicWave rawWave = ctx.createPeriodicWave(real, imag, true);
```

### Audio Parameter Automation

All `AudioParam` scheduling methods return `this` for chaining:

```java
AudioParam freq = osc.getFrequency();
double now = ctx.getCurrentTime();

// Immediate value
freq.setValueAtTime(440, now);

// Linear ramp to target
freq.linearRampToValueAtTime(880, now + 1.0);

// Exponential ramp (value must be > 0)
freq.exponentialRampToValueAtTime(220, now + 2.0);

// Asymptotic approach (good for envelopes)
freq.setTargetAtTime(440, now + 3.0, 0.1); // timeConstant = 0.1s

// Arbitrary value curve
float[] curve = {440, 550, 660, 880, 440};
freq.setValueCurveAtTime(curve, now + 4.0, 1.0); // Over 1 second

// Cancel all scheduled changes
freq.cancelScheduledValues(now + 5.0);

// Cancel but hold at current computed value
freq.cancelAndHoldAtTime(now + 5.0);
```

### All BiquadFilter Types

```java
// All 8 filter types:
filter.setType(BiquadFilterType.LOWPASS);    // Passes frequencies below cutoff
filter.setType(BiquadFilterType.HIGHPASS);   // Passes frequencies above cutoff
filter.setType(BiquadFilterType.BANDPASS);   // Passes a range around frequency
filter.setType(BiquadFilterType.LOWSHELF);   // Boosts/cuts below frequency (uses gain param)
filter.setType(BiquadFilterType.HIGHSHELF);  // Boosts/cuts above frequency (uses gain param)
filter.setType(BiquadFilterType.PEAKING);    // Boosts/cuts around frequency (uses gain + Q)
filter.setType(BiquadFilterType.NOTCH);      // Cuts a narrow band at frequency
filter.setType(BiquadFilterType.ALLPASS);    // Passes all frequencies, changes phase

// Get frequency response data for visualization
float[] frequencies = new float[128];
float[] magResponse = new float[128];
float[] phaseResponse = new float[128];
for (int i = 0; i < 128; i++) {
    frequencies[i] = 20 * (float)Math.pow(1000, (double)i / 127); // 20Hz–20kHz log
}
filter.getFrequencyResponse(frequencies, magResponse, phaseResponse);
```

### IIR Filter

```java
import com.github.satori87.gdx.webaudio.effect.IIRFilterNode;

// Custom IIR filter from feedforward/feedback coefficients
float[] feedforward = {0.0675f, 0.1349f, 0.0675f};
float[] feedback = {1.0f, -1.1430f, 0.4128f};
IIRFilterNode iir = ctx.createIIRFilter(feedforward, feedback);

source.connect(iir);
iir.connect(ctx.getDestination());

// Get frequency response (same as BiquadFilterNode)
iir.getFrequencyResponse(frequencies, magResponse, phaseResponse);
```

### Sample-Accurate Scheduling (Rhythm Game)

```java
public void playDrumPattern(float bpm) {
    double beatDuration = 60.0 / bpm;
    double startTime = ctx.getCurrentTime() + 0.1;

    for (int bar = 0; bar < 4; bar++) {
        double barStart = startTime + bar * 4 * beatDuration;

        // Kick on beats 1 and 3
        playBufferAt(kickBuffer, barStart);
        playBufferAt(kickBuffer, barStart + 2 * beatDuration);

        // Snare on beats 2 and 4
        playBufferAt(snareBuffer, barStart + beatDuration);
        playBufferAt(snareBuffer, barStart + 3 * beatDuration);

        // Hi-hat on every 8th note
        for (int i = 0; i < 8; i++) {
            AudioBufferSourceNode hh = ctx.createBufferSource();
            hh.setBuffer(hihatBuffer);
            GainNode hhGain = ctx.createGain();
            hhGain.getGain().setValueAtTime(i % 2 == 0 ? 0.8f : 0.4f, barStart);
            hh.connect(hhGain);
            hhGain.connect(ctx.getDestination());
            hh.start(barStart + i * beatDuration * 0.5);
        }
    }
}

private void playBufferAt(AudioBuffer buffer, double time) {
    AudioBufferSourceNode src = ctx.createBufferSource();
    src.setBuffer(buffer);
    src.connect(ctx.getDestination());
    src.start(time);
}
```

### Mixing Bus Pattern

```java
// Create a bus hierarchy: sources → submix → master → destination
GainNode masterBus = ctx.createGain();
masterBus.getGain().setValueAtTime(0.8f, ctx.getCurrentTime());
masterBus.connect(ctx.getDestination());

GainNode sfxBus = ctx.createGain();
sfxBus.getGain().setValueAtTime(1.0f, ctx.getCurrentTime());
sfxBus.connect(masterBus);

GainNode musicBus = ctx.createGain();
musicBus.getGain().setValueAtTime(0.6f, ctx.getCurrentTime());
musicBus.connect(masterBus);

// Route sounds to appropriate buses
sfxSource.connect(sfxBus);
musicSource.connect(musicBus);

// Mute SFX without affecting music
sfxBus.getGain().setValueAtTime(0, ctx.getCurrentTime());
```

---

## Spatial Audio

### 2D Spatial Audio

Use `SpatialAudioScene2D` for top-down or side-scrolling games:

```java
import com.github.satori87.gdx.webaudio.spatial.*;
import com.github.satori87.gdx.webaudio.types.DistanceModel;

SpatialAudioScene2D scene = /* create via TeaVM factory */;
scene.setWorldScale(1.0f / 32f); // 32 pixels per meter

// Create a positioned sound source
SpatialAudioSource enemy = scene.createSource(200, 150);
enemy.setDistanceModel(DistanceModel.INVERSE);
enemy.setRefDistance(2);    // Full volume within 2 meters
enemy.setMaxDistance(50);   // Inaudible beyond 50 meters
enemy.setRolloffFactor(1);

// Connect audio to the spatial source
AudioBufferSourceNode shot = ctx.createBufferSource();
shot.setBuffer(gunshotBuffer);
shot.connect(enemy.getInput());
shot.start();

// In your render loop, update the listener position
scene.updateListenerFromCamera(camera); // OrthographicCamera

// Move the source as the enemy moves
enemy.setPosition(enemyX, enemyY);
```

### 3D Spatial Audio

Use `SpatialAudioScene3D` for 3D games:

```java
import com.github.satori87.gdx.webaudio.spatial.*;
import com.github.satori87.gdx.webaudio.types.*;

SpatialAudioScene3D scene = /* create via TeaVM factory */;
scene.setWorldScale(1.0f);  // 1 unit = 1 meter
scene.setDefaultPanningModel(PanningModel.HRTF);
scene.setDefaultDistanceModel(DistanceModel.INVERSE);

// Helicopter circling overhead
SpatialAudioSource heli = scene.createSource(0, 50, 0);
heli.setRefDistance(5);
heli.setMaxDistance(200);
heli.setRolloffFactor(0.8f);

AudioBufferSourceNode rotor = ctx.createBufferSource();
rotor.setBuffer(helicopterLoopBuffer);
rotor.setLoop(true);
rotor.connect(heli.getInput());
rotor.start();

// Update listener from camera each frame
scene.updateListenerFromCamera(perspectiveCamera);

// Animate sound position
float t = Gdx.graphics.getDeltaTime();
heli.setPosition(
    (float)Math.cos(t * 0.5) * 30,
    50,
    (float)Math.sin(t * 0.5) * 30
);
```

### Using PannerNode Directly

For more control, use PannerNode without the high-level scene:

```java
import com.github.satori87.gdx.webaudio.spatial.PannerNode;
import com.github.satori87.gdx.webaudio.types.*;

// Configure the listener
AudioListener listener = ctx.getListener();
listener.setPosition(0, 0, 0);
listener.setOrientation(0, 0, -1, 0, 1, 0); // Forward: -Z, Up: +Y

// Create a panner node
PannerNode panner = ctx.createPanner();
panner.setDistanceModel(DistanceModel.INVERSE);
panner.setPanningModel(PanningModel.HRTF);
panner.setRefDistance(1);
panner.setMaxDistance(50);
panner.setRolloffFactor(1);
panner.setPosition(10, 0, -5);

source.connect(panner);
panner.connect(ctx.getDestination());
```

### Distance Models

```java
// INVERSE (default): volume = refDist / (refDist + rolloff * (dist - refDist))
// Most natural sounding, used for realistic environments
panner.setDistanceModel(DistanceModel.INVERSE);

// LINEAR: volume = 1 - rolloff * (dist - refDist) / (maxDist - refDist)
// Predictable falloff, good for gameplay audio
panner.setDistanceModel(DistanceModel.LINEAR);

// EXPONENTIAL: volume = (dist / refDist) ^ (-rolloff)
// Dramatic falloff, good for explosions or alerts
panner.setDistanceModel(DistanceModel.EXPONENTIAL);
```

### Directional Audio Cones

```java
// Source is loud in front, quiet behind
panner.setConeInnerAngle(90);    // Full volume within this cone (degrees)
panner.setConeOuterAngle(270);   // Reduced volume outside inner, within outer
panner.setConeOuterGain(0.2f);   // Volume multiplier outside the outer cone

// Point the source in a direction
panner.setOrientation(0, 0, -1); // Facing forward (-Z)
```

---

## Analysis and Visualization

### FFT Spectrum Analysis

```java
import com.github.satori87.gdx.webaudio.analysis.AnalyserNode;

AnalyserNode analyser = ctx.createAnalyser();
analyser.setFftSize(2048);
analyser.setSmoothingTimeConstant(0.85f);
analyser.setMinDecibels(-90);
analyser.setMaxDecibels(-10);

// Insert in signal path
source.connect(analyser);
analyser.connect(ctx.getDestination());

// Read spectrum data each frame (values 0–255)
byte[] frequencyData = new byte[analyser.getFrequencyBinCount()];
analyser.getByteFrequencyData(frequencyData);

// Or as float dB values
float[] floatData = new float[analyser.getFrequencyBinCount()];
analyser.getFloatFrequencyData(floatData);
```

### Waveform Display

```java
// Get time-domain data for oscilloscope-style display
float[] waveform = new float[analyser.getFftSize()];
analyser.getFloatTimeDomainData(waveform);
// Values are -1.0 to 1.0

// Or as bytes (0–255, 128 = silence)
byte[] byteWaveform = new byte[analyser.getFftSize()];
analyser.getByteTimeDomainData(byteWaveform);
```

---

## Advanced Topics

### Offline Rendering

Render audio to a buffer without real-time playback:

```java
import com.github.satori87.gdx.webaudio.OfflineAudioContext;

float sampleRate = 44100;
int lengthInSamples = (int)(sampleRate * 2); // 2 seconds

OfflineAudioContext offline = WebAudio.createOfflineContext(2, lengthInSamples, sampleRate);

// Build audio graph on the offline context
OscillatorNode osc = offline.createOscillator();
osc.getFrequency().setValueAtTime(440, 0);

GainNode fade = offline.createGain();
fade.getGain().setValueAtTime(1.0f, 0);
fade.getGain().linearRampToValueAtTime(0.0f, 2.0);

osc.connect(fade);
fade.connect(offline.getDestination());
osc.start();
osc.stop(2.0);

// Render
offline.startRendering(renderedBuffer -> {
    // renderedBuffer is a ready-to-use AudioBuffer
    Gdx.app.log("Audio", "Rendered " + renderedBuffer.getLength() + " samples");

    // Play the rendered buffer on the main context
    AudioBufferSourceNode player = ctx.createBufferSource();
    player.setBuffer(renderedBuffer);
    player.connect(ctx.getDestination());
    player.start();
});
```

### AudioWorklet (Custom DSP)

Load and use custom JavaScript audio processors:

```java
// Load a worklet module (the .js file must be served alongside your app)
ctx.addWorkletModule("white-noise-processor.js", () -> {
    // Module loaded — create a node
    AudioWorkletNode noise = ctx.createWorkletNode("white-noise-processor");
    GainNode volume = ctx.createGain();
    volume.getGain().setValueAtTime(0.1f, ctx.getCurrentTime());
    noise.connect(volume);
    volume.connect(ctx.getDestination());
}, () -> {
    Gdx.app.error("Audio", "Failed to load worklet module");
});
```

Example processor JavaScript (`white-noise-processor.js`):

```javascript
class WhiteNoiseProcessor extends AudioWorkletProcessor {
    process(inputs, outputs, parameters) {
        const output = outputs[0];
        for (let channel = 0; channel < output.length; ++channel) {
            const data = output[channel];
            for (let i = 0; i < data.length; ++i) {
                data[i] = Math.random() * 2 - 1;
            }
        }
        return true;
    }
}
registerProcessor('white-noise-processor', WhiteNoiseProcessor);
```

> **Note:** AudioWorklet requires HTTPS in production.

### Channel Splitting and Merging

```java
import com.github.satori87.gdx.webaudio.channel.*;

// Split stereo into two mono channels
ChannelSplitterNode splitter = ctx.createChannelSplitter(2);
source.connect(splitter);

// Process left and right channels independently
GainNode leftGain = ctx.createGain();
GainNode rightGain = ctx.createGain();
splitter.connect(leftGain, 0, 0);  // Left channel
splitter.connect(rightGain, 1, 0); // Right channel

// Merge back to stereo
ChannelMergerNode merger = ctx.createChannelMerger(2);
leftGain.connect(merger, 0, 0);
rightGain.connect(merger, 0, 1);
merger.connect(ctx.getDestination());
```

### Context State Management

```java
// Check current state
AudioContextState state = ctx.getState(); // SUSPENDED, RUNNING, or CLOSED

// Listen for state changes
ctx.setOnStateChange(() -> {
    Gdx.app.log("Audio", "State changed to: " + ctx.getState().toJsValue());
});

// Suspend (pause audio processing)
ctx.suspend(() -> Gdx.app.log("Audio", "Suspended"));

// Resume (required after user gesture due to browser autoplay policy)
ctx.resume(() -> Gdx.app.log("Audio", "Resumed"));

// Close (release all resources — cannot be resumed)
ctx.close(() -> Gdx.app.log("Audio", "Closed"));

// Latency info
double baseLatency = ctx.getBaseLatency();     // Minimum latency
double outputLatency = ctx.getOutputLatency(); // Actual output latency
double currentTime = ctx.getCurrentTime();     // High-resolution audio clock
float sampleRate = ctx.getSampleRate();        // e.g., 44100 or 48000
```

### Node Properties

All `AudioNode` instances expose channel configuration:

```java
GainNode gain = ctx.createGain();

// Channel configuration
gain.setChannelCount(2);
gain.setChannelCountMode(ChannelCountMode.EXPLICIT);
gain.setChannelInterpretation(ChannelInterpretation.SPEAKERS);

// Read back
int channels = gain.getChannelCount();
ChannelCountMode mode = gain.getChannelCountMode();
ChannelInterpretation interp = gain.getChannelInterpretation();

// I/O info
int inputs = gain.getNumberOfInputs();
int outputs = gain.getNumberOfOutputs();

// Destination info
int maxChannels = ctx.getDestination().getMaxChannelCount();
```

### Targeted Disconnect

```java
// Disconnect a specific connection (rather than all)
gain.disconnect(ctx.getDestination()); // Only removes the connection to destination

// Disconnect a specific output index
gain.disconnect(0);

// Disconnect from a specific param
gain.disconnectParam(osc.getFrequency());
```

### AudioBuffer Operations

```java
// Create an empty buffer
float sampleRate = ctx.getSampleRate();
AudioBuffer buffer = ctx.createBuffer(2, (int)(sampleRate * 1), sampleRate); // 2ch, 1sec

// Write data to a channel
float[] data = new float[(int)sampleRate];
for (int i = 0; i < data.length; i++) {
    data[i] = (float)Math.sin(2 * Math.PI * 440 * i / sampleRate); // 440Hz sine
}
buffer.copyToChannel(data, 0);       // Write to channel 0
buffer.copyToChannel(data, 1);       // Write to channel 1

// Read data from a channel
float[] readBack = new float[(int)sampleRate];
buffer.copyFromChannel(readBack, 0); // Read from channel 0

// With offset
buffer.copyToChannel(data, 0, 1000);   // Start writing at sample 1000
buffer.copyFromChannel(readBack, 0, 1000);

// Buffer properties
float duration = buffer.getDuration();
int length = buffer.getLength();
int channels = buffer.getNumberOfChannels();
float sr = buffer.getSampleRate();
```

---

## API Reference

### Source Nodes

| Node | Description |
|------|-------------|
| `OscillatorNode` | Generates periodic waveforms (sine, square, sawtooth, triangle, custom) |
| `AudioBufferSourceNode` | Plays audio from an in-memory AudioBuffer |
| `ConstantSourceNode` | Outputs a constant DC value, useful for modulation |
| `MediaElementAudioSourceNode` | Wraps an HTML `<audio>` or `<video>` element |
| `MediaStreamAudioSourceNode` | Wraps a MediaStream (e.g., microphone input) |
| `MediaStreamAudioDestinationNode` | Creates a MediaStream from audio graph output |

### Effect Nodes

| Node | Description |
|------|-------------|
| `GainNode` | Controls signal amplitude (volume) |
| `BiquadFilterNode` | Configurable second-order filter (lowpass, highpass, bandpass, etc.) |
| `IIRFilterNode` | Custom IIR filter from feedforward/feedback coefficients |
| `DelayNode` | Delays the audio signal by a specified time |
| `ConvolverNode` | Applies convolution reverb using an impulse response buffer |
| `WaveShaperNode` | Applies non-linear distortion via a shaping curve |
| `DynamicsCompressorNode` | Dynamic range compression |
| `StereoPannerNode` | Simple left/right stereo panning |

### Spatial Nodes

| Node | Description |
|------|-------------|
| `PannerNode` | Full 3D positional audio with distance models and cones |
| `SpatialAudioSource` | High-level wrapper for positioned audio sources |
| `SpatialAudioScene2D` | 2D scene manager with OrthographicCamera integration |
| `SpatialAudioScene3D` | 3D scene manager with Camera integration |

### Analysis & Channel Nodes

| Node | Description |
|------|-------------|
| `AnalyserNode` | Real-time FFT spectrum and waveform analysis |
| `ChannelSplitterNode` | Splits multi-channel audio into separate outputs |
| `ChannelMergerNode` | Merges multiple inputs into a multi-channel output |
| `AudioWorkletNode` | Runs custom JavaScript audio processors |

### Enum Types

| Enum | Values |
|------|--------|
| `OscillatorType` | `SINE`, `SQUARE`, `SAWTOOTH`, `TRIANGLE`, `CUSTOM` |
| `BiquadFilterType` | `LOWPASS`, `HIGHPASS`, `BANDPASS`, `LOWSHELF`, `HIGHSHELF`, `PEAKING`, `NOTCH`, `ALLPASS` |
| `DistanceModel` | `LINEAR`, `INVERSE`, `EXPONENTIAL` |
| `PanningModel` | `EQUALPOWER`, `HRTF` |
| `ChannelCountMode` | `MAX`, `CLAMPED_MAX`, `EXPLICIT` |
| `ChannelInterpretation` | `SPEAKERS`, `DISCRETE` |
| `OverSampleType` | `NONE`, `TWO_X`, `FOUR_X` |
| `AudioContextState` | `SUSPENDED`, `RUNNING`, `CLOSED` |
| `AutomationRate` | `A_RATE`, `K_RATE` |

---

## Browser Requirements

- **Web Audio API** — Supported in all modern browsers (Chrome, Firefox, Safari, Edge)
- **Autoplay Policy** — Most browsers require a user gesture (click/tap) before audio can play. Call `ctx.resume()` in response to user interaction
- **HTTPS** — Required for AudioWorklet in production
- **Sample Rates** — Typically 44100 Hz or 48000 Hz depending on the browser/OS

## License

[Apache License 2.0](LICENSE)
