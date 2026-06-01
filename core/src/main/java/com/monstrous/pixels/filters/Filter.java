package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

// Filter class for post-processing shader effects

public class Filter implements Disposable {

    SpriteBatch batch;
    OrthographicCamera bufferCam;
    ShaderProgram shader;

    public Filter() {
        batch = new SpriteBatch();
        bufferCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader = null;      // set a shader in derived classes
    }

    public void resize(int width, int height ) {

        bufferCam.viewportWidth = width;
        bufferCam.viewportHeight = height;
        bufferCam.position.set(width/2, height/2, 0);
        bufferCam.update();
        batch.setProjectionMatrix(bufferCam.combined);
    }

    public void render(FrameBuffer fbo, int x, int y, int w, int h ) {
        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false, true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(shader);
        batch.draw(s, x, y, w, h);    // draw frame buffer to screen window
        batch.setShader(null);
        batch.end();
    }

    public void render(FrameBuffer fbo ) {      // full screen
        render(fbo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void renderToBuffer(FrameBuffer fboOut, FrameBuffer fboIn ) {
        Sprite s = new Sprite(fboIn.getColorBufferTexture());
        s.flip(false, true); // coordinate system in buffer differs from screen

        int width = fboOut.getWidth();
        int height = fboOut.getHeight();

        // to adapt for fboOut resolution
        bufferCam.viewportWidth = width;
        bufferCam.viewportHeight = height;
        bufferCam.position.set(width/2, height/2, 0);
        bufferCam.update();
        batch.setProjectionMatrix(bufferCam.combined);

        fboOut.begin();
        render(fboIn, 0, 0, width, height);
        fboOut.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if(shader!=null)
            shader.dispose();
    }
}
