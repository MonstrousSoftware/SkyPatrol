package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.filters.PostProcessor;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.World;


/** Shared screen adapter that will expand a low resolution frame to the full screen and apply visual effects.
 * Takes care of the frame buffers, upscaling etc.
 * Provides font.
 * Subclasses need to implement renderFrame()
 */


public abstract class RetroScreen extends ScreenAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;

    private FrameBuffer fboSmall;
    private FrameBuffer fbo;
    public SpriteBatch batch;
    private PostProcessor postProcessor;
    public BitmapFont font;
    public Color borderColor;
    private int pixelScale;
    private int savedWidth, savedHeight;
    protected final Main game;
    protected boolean enableCRTeffect = false;


    public RetroScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        fboSmall = new FrameBuffer(Pixmap.Format.RGBA4444, LOWRES_WIDTH, LOWRES_HEIGHT, true);

        batch = new SpriteBatch();
        postProcessor = new PostProcessor();

        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);

        borderColor = new Color(Color.BLUE);
    }

    @Override
    public void render(float deltaTime) {
        // F11 to toggle full screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            boolean fullScreen = Gdx.graphics.isFullscreen();
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
            if (fullScreen)
                Gdx.graphics.setWindowedMode(savedWidth, savedHeight);
            else {
                savedWidth = Gdx.graphics.getWidth();
                savedHeight = Gdx.graphics.getHeight();
                Gdx.graphics.setFullscreenMode(currentMode);
            }
        }

        // render a low resolution frame to the frame buffer
        fboSmall.begin();
            renderFrame(deltaTime);
        fboSmall.end();


        // place the low resolution frame centrally on the screen, surrounded by a border
        fbo.begin();
            ScreenUtils.clear(borderColor, true);    // border
            batch.getProjectionMatrix().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();
            Sprite s = new Sprite(fboSmall.getColorBufferTexture());
            s.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            s.flip(false, true); // coordinate system in buffer differs from screen
            int w = pixelScale * (int)s.getWidth();
            int h = pixelScale * (int)s.getHeight();
            int x = (Gdx.graphics.getWidth() - w)/2;
            int y = (Gdx.graphics.getHeight() - h)/2;
            batch.draw(s, x, y, w, h);
            batch.end();
        fbo.end();

        // post-processing for visual effects
        if(!enableCRTeffect) {
            Sprite s2 = new Sprite(fbo.getColorBufferTexture());
            s2.flip(false, true);   // flip Y
            batch.begin();
            batch.draw(s2, 0, 0);
            batch.end();
        } else {
            postProcessor.render(fbo);    // retro effect
        }
    }

    abstract void renderFrame(float deltaTime);

    @Override
    public void dispose() {
        fboSmall.dispose();
        fbo.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        postProcessor.resize(width, height);
        pixelScale = Math.min(width/LOWRES_WIDTH, height/LOWRES_HEIGHT);
    }


}
