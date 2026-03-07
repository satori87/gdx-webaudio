package com.github.satori87.gdx.webaudio.teavm;

import com.github.satori87.gdx.webaudio.OfflineAudioContext;
import com.github.satori87.gdx.webaudio.WebAudio;
import com.github.satori87.gdx.webaudio.WebAudioContext;
import org.teavm.jso.JSBody;

/**
 * TeaVM/browser implementation of {@link WebAudio.WebAudioPlatform}.
 *
 * <p>Serves as the platform factory that creates audio contexts backed by the browser's
 * Web Audio API. Call {@link #initialize()} once at application startup to register
 * this platform with the core {@link WebAudio} facade.</p>
 *
 * <p>Initialization also installs a user-gesture handler that automatically resumes
 * any AudioContext created through this platform when the user first interacts with
 * the page (click, touch, or keypress), satisfying browser autoplay policies.</p>
 */
public class TeaVMWebAudio implements WebAudio.WebAudioPlatform {

    /**
     * Registers this TeaVM platform implementation with the core {@link WebAudio} facade
     * and installs a browser gesture handler to auto-resume suspended AudioContexts.
     */
    public static void initialize() {
        installAudioResumeHandler();
        WebAudio.setPlatform(new TeaVMWebAudio());
    }

    @Override
    public WebAudioContext createContext() {
        return new TeaVMWebAudioContext();
    }

    @Override
    public OfflineAudioContext createOfflineContext(int channels, int length, float sampleRate) {
        return new TeaVMOfflineAudioContext(channels, length, sampleRate);
    }

    /**
     * Intercepts AudioContext creation to auto-resume all contexts on first user gesture.
     * Browsers require a user interaction (click, touch, keypress) before audio can play.
     */
    @JSBody(script =
        "var Orig = window.AudioContext || window.webkitAudioContext;" +
        "if (!Orig) return;" +
        "var contexts = [];" +
        "function resumeAll() {" +
        "  for (var i = 0; i < contexts.length; i++) {" +
        "    if (contexts[i].state === 'suspended') contexts[i].resume();" +
        "  }" +
        "}" +
        "document.addEventListener('click', resumeAll);" +
        "document.addEventListener('touchstart', resumeAll);" +
        "document.addEventListener('keydown', resumeAll);" +
        "window.AudioContext = function(o) {" +
        "  var c = o ? new Orig(o) : new Orig();" +
        "  contexts.push(c);" +
        "  return c;" +
        "};" +
        "window.AudioContext.prototype = Orig.prototype;")
    private static native void installAudioResumeHandler();
}
