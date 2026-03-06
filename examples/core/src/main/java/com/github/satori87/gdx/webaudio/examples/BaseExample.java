package com.github.satori87.gdx.webaudio.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.satori87.gdx.webaudio.WebAudio;
import com.github.satori87.gdx.webaudio.WebAudioContext;

public abstract class BaseExample {
    protected WebAudioContext ctx;
    protected ScreenViewport viewport;

    protected static final int BTN_W = 200;
    protected static final int BTN_H = 36;
    protected static final int BTN_GAP = 8;
    protected static final int BTN_START_X = 20;
    protected static final int BTN_START_Y = 20;
    protected static final int BTN_COLS = 3;

    public void create() {
        ctx = WebAudio.createContext();
        viewport = new ScreenViewport();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /** Update projection matrices to match current screen size. Call at start of render(). */
    protected void applyViewport(SpriteBatch batch, ShapeRenderer shapes) {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewport.apply();
        if (batch != null) batch.setProjectionMatrix(viewport.getCamera().combined);
        if (shapes != null) shapes.setProjectionMatrix(viewport.getCamera().combined);
    }

    public abstract void render();

    public void dispose() {
        if (ctx != null) {
            ctx.close(null);
            ctx = null;
        }
    }

    /** Draw a grid of buttons at the bottom. Returns the index of the clicked button, or -1. */
    protected int drawButtons(ShapeRenderer shapes, SpriteBatch batch, BitmapFont font, String... labels) {
        int clicked = -1;
        int touchX = -1, touchY = -1;
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        }

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < labels.length; i++) {
            int col = i % BTN_COLS;
            int row = i / BTN_COLS;
            int bx = BTN_START_X + col * (BTN_W + BTN_GAP);
            int by = BTN_START_Y + row * (BTN_H + BTN_GAP);
            boolean hover = touchX >= bx && touchX < bx + BTN_W && touchY >= by && touchY < by + BTN_H;
            if (hover) {
                clicked = i;
                shapes.setColor(0.3f, 0.6f, 0.9f, 1f);
            } else {
                shapes.setColor(0.25f, 0.4f, 0.65f, 1f);
            }
            shapes.rect(bx, by, BTN_W, BTN_H);
        }
        shapes.end();

        batch.begin();
        font.setColor(1, 1, 1, 1);
        for (int i = 0; i < labels.length; i++) {
            int col = i % BTN_COLS;
            int row = i / BTN_COLS;
            int bx = BTN_START_X + col * (BTN_W + BTN_GAP);
            int by = BTN_START_Y + row * (BTN_H + BTN_GAP);
            font.draw(batch, labels[i], bx + 10, by + 24);
        }
        batch.end();

        return clicked;
    }
}
