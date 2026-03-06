package com.github.satori87.gdx.webaudio.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.satori87.gdx.webaudio.teavm.TeaVMWebAudio;
import com.github.satori87.gdx.webaudio.examples.ExampleLauncher;
import org.teavm.jso.JSBody;

/** Launches the TeaVM/HTML application. */
public class TeaVMLauncher {

    @JSBody(script = "return window.innerWidth;")
    private static native int getWindowWidth();

    @JSBody(script = "return window.innerHeight;")
    private static native int getWindowHeight();

    @JSBody(script =
        "var canvas = document.getElementById('canvas');" +
        "window.addEventListener('resize', function() {" +
        "  canvas.width = window.innerWidth;" +
        "  canvas.height = window.innerHeight;" +
        "});")
    private static native void setupResizeListener();

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = getWindowWidth();
        config.height = getWindowHeight();
        TeaVMWebAudio.initialize();
        setupResizeListener();
        new TeaApplication(new ExampleLauncher(), config);
    }
}
