package com.github.satori87.gdx.webaudio.examples.teavm;

import com.github.satori87.gdx.webaudio.teavm.TeaVMWebAudio;
import com.github.satori87.gdx.webaudio.examples.ExampleLauncher;

/**
 * TeaVM entry point for the examples application.
 * Initializes the Web Audio platform before launching the libGDX app.
 */
public class TeaVMExampleLauncher {
    public static void main(String[] args) {
        TeaVMWebAudio.initialize();
        // The actual TeaVM/libGDX application bootstrap would go here.
        // With gdx-teavm, this would be:
        // new TeaVMApplication(new ExampleLauncher(), TeaVMApplicationConfiguration.newConfiguration());
    }
}
