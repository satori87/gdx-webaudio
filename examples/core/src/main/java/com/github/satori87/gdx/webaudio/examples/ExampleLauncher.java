package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ExampleLauncher extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;
    private GlyphLayout layout;
    private ScreenViewport viewport;
    private BaseExample currentExample;

    private static final int BACK_BTN_W = 120;
    private static final int BACK_BTN_H = 36;
    private static final int BACK_BTN_X = 20;
    private static final int ITEM_X = 30;
    private static final int ITEM_H = 26;
    private static final int HEADER_H = 35;
    private static final int SCROLLBAR_W = 14;
    private static final int SCROLLBAR_MARGIN = 6;

    private float scrollOffset = 0;
    private boolean draggingScrollbar = false;
    private float dragStartY;
    private float dragStartOffset;
    private float[] textWidths;
    private int hoveredIndex = -1;
    private int lastMouseX = -1;
    private int lastMouseY = -1;

    private final InputAdapter menuInput = new InputAdapter() {
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            lastMouseX = screenX;
            lastMouseY = screenY;
            return false;
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            lastMouseX = screenX;
            lastMouseY = screenY;
            return false;
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            lastMouseX = screenX;
            lastMouseY = screenY;
            return false;
        }
    };

    private final String[] exampleNames = {
        "1. Simple Playback (WebSound & WebMusic)",
        "2. Low-Level Playback (Using Low-Level API)",
        "3. Basic Playback (Load & Play Files)",
        "4. Sound Board (12 Sound Effects)",
        "5. Format Showcase (WAV/MP3/OGG/FLAC/M4A/WebM)",
        "6. Synthesizer (Oscillators + ADSR)",
        "7. Waveforms & Noise (Sine/Sq/Saw/Tri/Noise)",
        "8. Playback Control (Pitch/Rate/Fade/Loop)",
        "9. Mixing Bus / Ducking / Channels",
        "10. Effect Chain (Filter/Delay/Distortion)",
        "11. 2D Spatial Audio",
        "12. 3D Spatial Audio",
        "13. Audio Visualizer (FFT — all 4 data methods)",
        "14. Dynamic Music (Crossfade Layers)",
        "15. Rhythm Game (Precise Scheduling)",
        "16. Offline Rendering",
        "17. AudioWorklet (Custom DSP)",
        "18. AudioParam Automation (all methods)",
        "19. Filter Showcase (8 Biquad types + IIR)",
        "20. Convolution Reverb (ConvolverNode)",
        "21. Advanced Sources (Const/Periodic/Offset/Detune)",
        "22. Context & Node Info (State/Latency/Props)",
        "23. Distance Models & Panning Compare",
        "24. Noise Generator (NoiseNode)",
        "25. Composite Effects (Chorus/Flanger/Phaser/Reverb/Limiter)",
        "26. Sound Groups (Mixing Bus + Master Volume)",
        "27. Sound Pool (Fire-and-Forget Playback)",
        "28. Doppler Effect (2D Spatial)"
    };

    @SuppressWarnings("unchecked")
    private final java.util.function.Supplier<BaseExample>[] exampleFactories = new java.util.function.Supplier[] {
        SimplePlaybackExample::new,
        LowLevelPlaybackExample::new,
        BasicPlaybackExample::new,
        SoundBoardExample::new,
        FormatShowcaseExample::new,
        SynthesizerExample::new,
        WaveformNoiseExample::new,
        PlaybackControlExample::new,
        MixingBusExample::new,
        EffectChainExample::new,
        SpatialAudio2DExample::new,
        SpatialAudio3DExample::new,
        AudioVisualizerExample::new,
        DynamicMusicExample::new,
        RhythmGameExample::new,
        OfflineRenderExample::new,
        AudioWorkletExample::new,
        AudioParamAutomationExample::new,
        FilterShowcaseExample::new,
        ConvolutionReverbExample::new,
        AdvancedSourcesExample::new,
        ContextInfoExample::new,
        DistanceModelCompareExample::new,
        NoiseGeneratorExample::new,
        CompositeEffectsExample::new,
        SoundGroupExample::new,
        SoundPoolExample::new,
        DopplerEffectExample::new
    };

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();
        viewport = new ScreenViewport();

        // Precompute text widths for hit testing
        textWidths = new float[exampleNames.length];
        for (int i = 0; i < exampleNames.length; i++) {
            layout.setText(font, exampleNames[i]);
            textWidths[i] = layout.width;
        }

        Gdx.input.setInputProcessor(menuInput);
    }

    private int getBackBtnY() {
        return Gdx.graphics.getHeight() - BACK_BTN_H - 4;
    }

    private boolean isBackBtnHit(int screenX, int screenY) {
        int y = Gdx.graphics.getHeight() - screenY;
        int btnY = getBackBtnY();
        return screenX >= BACK_BTN_X && screenX <= BACK_BTN_X + BACK_BTN_W
            && y >= btnY && y <= btnY + BACK_BTN_H;
    }

    private void launchExample(int index) {
        try {
            if (currentExample != null) {
                currentExample.dispose();
            }
            Gdx.input.setInputProcessor(null);
            currentExample = exampleFactories[index].get();
            currentExample.create();
            Gdx.app.log("ExampleLauncher", "Launched: " + exampleNames[index]);
        } catch (Exception e) {
            Gdx.app.error("ExampleLauncher", "Failed to launch example", e);
            currentExample = null;
        }
    }

    private void returnToMenu() {
        if (currentExample != null) {
            currentExample.dispose();
            currentExample = null;
        }
        Gdx.input.setInputProcessor(menuInput);
    }

    private int getContentH() {
        return exampleNames.length * ITEM_H;
    }

    private int getViewH() {
        return Gdx.graphics.getHeight() - HEADER_H;
    }

    private float getMaxScroll() {
        return Math.max(0, getContentH() - getViewH());
    }

    private boolean needsScroll() {
        return getMaxScroll() > 0;
    }

    // Scrollbar track occupies the right edge of the view area
    private int getTrackX() {
        return Gdx.graphics.getWidth() - SCROLLBAR_W - SCROLLBAR_MARGIN;
    }

    private int getTrackTop() {
        return Gdx.graphics.getHeight() - HEADER_H;
    }

    private int getTrackH() {
        return getViewH();
    }

    // Thumb size proportional to visible fraction
    private float getThumbH() {
        float viewH = getViewH();
        float contentH = getContentH();
        if (contentH <= 0) return viewH;
        return Math.max(30, viewH * viewH / contentH);
    }

    // Thumb Y position (in screen coords, bottom-up)
    private float getThumbY() {
        float maxScroll = getMaxScroll();
        if (maxScroll <= 0) return 0;
        float trackH = getTrackH();
        float thumbH = getThumbH();
        float scrollFraction = scrollOffset / maxScroll;
        // thumbY goes from (trackTop - thumbH) at scroll=0 down to 0 at scroll=max
        return (trackH - thumbH) * (1 - scrollFraction);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (currentExample != null) {
            currentExample.resize(width, height);
        }
    }

    private void applyViewport() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);
    }

    @Override
    public void render() {
        applyViewport();
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (currentExample != null) {
            try {
                currentExample.render();
            } catch (Exception e) {
                Gdx.app.error("ExampleLauncher", "Example render error", e);
            }

            // Draw back button
            int btnY = getBackBtnY();
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.8f, 0.2f, 0.2f, 0.9f);
            shapes.rect(BACK_BTN_X, btnY, BACK_BTN_W, BACK_BTN_H);
            shapes.end();

            batch.begin();
            font.setColor(1, 1, 1, 1);
            font.draw(batch, "<  Back", BACK_BTN_X + 20, btnY + 24);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                returnToMenu();
            } else if (Gdx.input.justTouched()) {
                if (isBackBtnHit(Gdx.input.getX(), Gdx.input.getY())) {
                    returnToMenu();
                }
            }
            return;
        }

        // --- Menu screen ---
        int h = Gdx.graphics.getHeight();
        int w = Gdx.graphics.getWidth();
        float maxScroll = getMaxScroll();
        boolean scrollable = needsScroll();
        int trackX = getTrackX();
        int menuTop = h - HEADER_H;

        // Draw scrollbar
        if (scrollable) {
            float thumbH = getThumbH();
            float thumbY = getThumbY();

            // Track background
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.22f, 0.22f, 0.22f, 1);
            shapes.rect(trackX, 0, SCROLLBAR_W, menuTop);

            // Thumb
            shapes.setColor(draggingScrollbar ? 0.65f : 0.45f, 0.45f, 0.55f, 1);
            shapes.rect(trackX + 2, thumbY, SCROLLBAR_W - 4, thumbH);
            shapes.end();
        }

        // Determine hovered item from tracked mouse position
        hoveredIndex = -1;
        if (lastMouseX >= 0 && !draggingScrollbar) {
            float mouseY = h - lastMouseY;
            if (mouseY < menuTop && mouseY >= 0) {
                int index = (int) ((menuTop - mouseY + scrollOffset) / ITEM_H);
                if (index >= 0 && index < exampleNames.length) {
                    if (lastMouseX >= ITEM_X && lastMouseX <= ITEM_X + textWidths[index]) {
                        hoveredIndex = index;
                    }
                }
            }
        }

        // Draw header
        batch.begin();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "gdx-webaudio Examples (" + exampleNames.length + ") — Click to launch:", 20, h - 10);

        // Draw menu items with hover highlight
        for (int i = 0; i < exampleNames.length; i++) {
            float itemY = menuTop - i * ITEM_H + scrollOffset;
            if (itemY > menuTop + 10 || itemY < -10) continue;
            if (i == hoveredIndex) {
                font.setColor(1f, 1f, 0.4f, 1);
            } else {
                font.setColor(0.8f, 0.9f, 1, 1);
            }
            font.draw(batch, exampleNames[i], ITEM_X, itemY);
        }
        batch.end();

        // --- Handle input ---

        // Scrollbar drag
        if (scrollable && Gdx.input.isTouched()) {
            int touchX = Gdx.input.getX();
            float touchY = h - Gdx.input.getY();

            if (Gdx.input.justTouched()) {
                // Check if touch is on scrollbar track
                if (touchX >= trackX && touchX <= trackX + SCROLLBAR_W && touchY < menuTop) {
                    float thumbH = getThumbH();
                    float thumbY = getThumbY();

                    if (touchY >= thumbY && touchY <= thumbY + thumbH) {
                        // Grabbed the thumb
                        draggingScrollbar = true;
                        dragStartY = touchY;
                        dragStartOffset = scrollOffset;
                    } else {
                        // Clicked track above or below thumb — jump
                        float trackH = getTrackH();
                        float clickFraction = 1 - (touchY / trackH);
                        scrollOffset = Math.max(0, Math.min(maxScroll, clickFraction * maxScroll));
                    }
                    return;
                }
            }

            if (draggingScrollbar) {
                float trackH = getTrackH();
                float thumbH = getThumbH();
                float usableTrack = trackH - thumbH;
                if (usableTrack > 0) {
                    float dy = dragStartY - touchY; // positive = dragged down = scroll down
                    float scrollDelta = dy / usableTrack * maxScroll;
                    scrollOffset = Math.max(0, Math.min(maxScroll, dragStartOffset + scrollDelta));
                }
                return;
            }
        } else {
            draggingScrollbar = false;
        }

        // Click on menu item — use touch position directly with text-width constraint
        if (Gdx.input.justTouched() && !draggingScrollbar) {
            int touchX = Gdx.input.getX();
            float touchY = h - Gdx.input.getY();
            if (touchY < menuTop && touchY >= 0) {
                int index = (int) ((menuTop - touchY + scrollOffset) / ITEM_H);
                if (index >= 0 && index < exampleNames.length) {
                    if (touchX >= ITEM_X && touchX <= ITEM_X + textWidths[index]) {
                        launchExample(index);
                    }
                }
            }
        }

        // Arrow keys for scrolling
        if (scrollable) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                scrollOffset = Math.max(0, scrollOffset - ITEM_H * 2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                scrollOffset = Math.min(maxScroll, scrollOffset + ITEM_H * 2);
            }
        }
    }

    @Override
    public void dispose() {
        if (currentExample != null) {
            currentExample.dispose();
        }
        batch.dispose();
        font.dispose();
        shapes.dispose();
    }
}
