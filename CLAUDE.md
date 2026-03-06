# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

gdx-webaudio is a Java library that provides a platform-agnostic abstraction over the Web Audio API for libGDX projects targeting TeaVM. Licensed under Apache 2.0 (matching libGDX). Distributed via JitPack.

## Build Commands

```bash
# Library (Gradle 8.5, requires Java 11)
JAVA_HOME="/c/Users/sator/.jdks/azul-11.0.30" ./gradlew build
./gradlew :core:build    # Core API only
./gradlew :teavm:build   # TeaVM impl only

# Examples (Gradle 9.3.1, auto-downloads JDK 17 via foojay)
cd examples
./gradlew build                    # Compile + transpile to JS
./gradlew :teavm:buildJavaScript   # Transpile only
./gradlew :teavm:run               # Run via local Jetty at http://localhost:8080/
```

## Architecture

Two-module Gradle library following the libGDX extension pattern:

- **`:core`** — Platform-agnostic Java interfaces and enums. Package: `com.github.satori87.gdx.webaudio`. Depends only on `com.badlogicgames.gdx:gdx`. Contains:
  - Root package: `WebAudio` (factory), `WebAudioContext`, `AudioNode`, `AudioParam`, `AudioBuffer`, `AudioListener`, `AudioDestinationNode`, `PeriodicWave`, `OfflineAudioContext`
  - `source/`: `OscillatorNode`, `AudioBufferSourceNode`, `ConstantSourceNode`, `AudioScheduledSourceNode`, media nodes
  - `effect/`: `GainNode`, `BiquadFilterNode`, `DelayNode`, `ConvolverNode`, `WaveShaperNode`, `DynamicsCompressorNode`, `StereoPannerNode`, `IIRFilterNode`
  - `spatial/`: `PannerNode`, `SpatialAudioSource`, `SpatialAudioScene2D`, `SpatialAudioScene3D`
  - `analysis/`: `AnalyserNode`
  - `channel/`: `ChannelSplitterNode`, `ChannelMergerNode`
  - `worklet/`: `AudioWorkletNode`, `AudioParamMap`
  - `types/`: All enums (`OscillatorType`, `BiquadFilterType`, `DistanceModel`, etc.)

- **`:teavm`** — TeaVM implementation. Package: `com.github.satori87.gdx.webaudio.teavm`. Depends on `:core` + TeaVM JSO 0.10.2.
  - `jso/`: Raw JavaScript bindings using `@JSClass(name=...)`, `@JSProperty`, `@JSMethod`, `@JSBody`, `@JSFunctor`
  - Root + subpackages: `TeaVM*` implementation classes wrapping JSO objects
  - Entry point: `TeaVMWebAudio.initialize()` registers the platform

## Examples

The `examples/` directory is a **standalone Gradle project** (not a subproject of the library), modeled after `gdx-webrtc/examples/webrtc-chat`. Uses Gradle composite builds (`includeBuild('..')`) to reference the library source.

- **`:core`** — Shared example code (10 examples: playback, synth, effects, 2D/3D spatial, visualizer, dynamic music, rhythm, offline render, worklet)
- **`:teavm`** — TeaVM launcher using `gdx-teavm` (`com.github.xpenatan.gdx-teavm:backend-teavm:1.4.0`)
  - `TeaVMBuilder` — Configures and runs TeaVM transpilation
  - `TeaVMLauncher` — Entry point creating `TeaApplication`
  - Gretty plugin provides `run` task (local Jetty server)
- **`assets/`** — Shared assets (uiskin, white-noise-processor.js)

## Code Style

- Always use `import` statements instead of fully qualified class references in code. Never inline FQ paths like `com.github.satori87.gdx.webaudio.effect.GainNode` in method signatures or bodies — add an import and use the simple name.

## Key Patterns

- All Web Audio API string enums map to Java enums with `toJsValue()`/`fromJsValue()` methods
- Core interfaces are implemented by `TeaVM*` classes that wrap `JS*` JSO bindings
- `TeaVMAudioNode.unwrap()` extracts the underlying `JSAudioNode` from any `AudioNode`
- `AudioParam` scheduling methods return `this` for chaining
- Typed arrays (`Float32Array`, `Uint8Array`) bridged via `JSFloat32Array`/`JSUint8Array`
- Promises handled via `@JSFunctor` callback interfaces
- TeaVM `@JSClass` requires `name` parameter: `@JSClass(name = "AudioContext")`, not `@JSClass("AudioContext")`
- Fields accessed from subpackages (e.g., `TeaVMAudioBuffer.jsBuffer`) must be `public`
- `JSOfflineAudioContext` is an `abstract class` extending `JSAudioContext`, not an interface
