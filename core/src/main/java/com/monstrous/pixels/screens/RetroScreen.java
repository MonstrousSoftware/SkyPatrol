package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.filters.PostProcessor;


/** Shared screen adapter that will expand a low resolution frame to the full screen and apply visual effects.
 * Takes care of the frame buffers, upscaling etc.
 * Provides font.
 * Subclasses need to implement renderFrame() and call super methods in constructor and show().
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
    protected final Main game;
    protected boolean enableCRTeffect = true;


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
                Gdx.graphics.setWindowedMode(game.savedWidth, game.savedHeight);
            else {
                // save window size in the Main class so it persists over different screens
                game.savedWidth = Gdx.graphics.getWidth();
                game.savedHeight = Gdx.graphics.getHeight();
                Gdx.graphics.setFullscreenMode(currentMode);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            enableCRTeffect = !enableCRTeffect;
        }

        // render a low resolution frame to the frame buffer
        fboSmall.begin();
            renderFrame(deltaTime);
        fboSmall.end();


        // place the low resolution frame centrally on the screen, surrounded by a border
        fbo.begin();
            Sprite frameSprite = new Sprite(fboSmall.getColorBufferTexture());  // mem alloc
            frameSprite.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            frameSprite.flip(false, true); // coordinate system in buffer differs from screen

            int w = pixelScale * (int) frameSprite.getWidth();
            int h = pixelScale * (int) frameSprite.getHeight();
            int x = (Gdx.graphics.getWidth() - w)/2;
            int y = (Gdx.graphics.getHeight() - h)/2;

            ScreenUtils.clear(borderColor, true);    // border
            batch.getProjectionMatrix().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();


            batch.draw(frameSprite, x, y, w, h);
            batch.end();
        fbo.end();

        // post-processing for visual effects
        if(!enableCRTeffect) {
            Sprite s2 = new Sprite(fbo.getColorBufferTexture());
            s2.flip(false, true);
            batch.begin();
            batch.draw(s2, 0, 0);
            //batch.draw(fbo.getColorBufferTexture(), 0, 0, fbo.getWidth(), fbo.getHeight(), 0,0, 1, 1);
            batch.end();
        } else {
            postProcessor.render(fbo);    // retro effect
        }
    }

    abstract void renderFrame(float deltaTime);

    @Override
    public void dispose() {
        //System.out.println("RetroScreen.dispose()");
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
