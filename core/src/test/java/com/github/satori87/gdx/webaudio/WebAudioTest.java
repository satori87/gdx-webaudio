package com.github.satori87.gdx.webaudio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WebAudioTest {

    @AfterEach
    void resetPlatform() {
        WebAudio.setPlatform(null);
    }

    @Test void createContextWithoutPlatformThrows() {
        WebAudio.setPlatform(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, WebAudio::createContext);
        assertTrue(ex.getMessage().contains("not initialized"));
    }

    @Test void createOfflineContextWithoutPlatformThrows() {
        WebAudio.setPlatform(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> WebAudio.createOfflineContext(2, 44100, 44100));
        assertTrue(ex.getMessage().contains("not initialized"));
    }

    @Test void setPlatformAndCreateContext() {
        WebAudioContext[] holder = new WebAudioContext[1];
        WebAudio.setPlatform(new WebAudio.WebAudioPlatform() {
            @Override public WebAudioContext createContext() { return null; }
            @Override public OfflineAudioContext createOfflineContext(int c, int l, float sr) { return null; }
        });
        // Should not throw
        assertDoesNotThrow(WebAudio::createContext);
    }

    @Test void setPlatformAndCreateOfflineContext() {
        WebAudio.setPlatform(new WebAudio.WebAudioPlatform() {
            @Override public WebAudioContext createContext() { return null; }
            @Override public OfflineAudioContext createOfflineContext(int c, int l, float sr) { return null; }
        });
        assertDoesNotThrow(() -> WebAudio.createOfflineContext(2, 44100, 44100));
    }
}
