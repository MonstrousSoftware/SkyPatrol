package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BlurFilter extends Filter {

    ShaderProgram blurHorizontal;
    ShaderProgram blurVertical;
    int bufWidth, bufHeight;
    int iterations;
    FrameBuffer fbo2;

    // w,h : size of fbo buffer to use for blurring, assumed to be same size as for render call
    public  BlurFilter( int iterations ) {
        super();
        this.iterations = iterations;
        bufWidth = 0;
        bufHeight = 0;
        fbo2 = null;
        blurHorizontal = new ShaderProgram(
                Gdx.files.internal("shaders\\blurhoriz.vertex.glsl"),
                Gdx.files.internal("shaders\\blurhoriz.fragment.glsl"));
        if (!blurHorizontal.isCompiled())
            throw new GdxRuntimeException(blurHorizontal.getLog());

        blurVertical = new ShaderProgram(
                Gdx.files.internal("shaders\\blurvertical.vertex.glsl"),
                Gdx.files.internal("shaders\\blurvertical.fragment.glsl"));
        if (!blurVertical.isCompiled())
            throw new GdxRuntimeException(blurVertical.getLog());
    }

    public void resize(int width, int height ) {
        // create extra frame buffer, same size as fbo
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        bufWidth = width;
        bufHeight = height;

        blurHorizontal.bind();
        blurHorizontal.setUniformf("u_resolution", new Vector2(width, height));

        blurVertical.bind();
        blurVertical.setUniformf("u_resolution", new Vector2(width, height));

        bufferCam.viewportWidth = width;
        bufferCam.viewportHeight = height;
        bufferCam.position.set(width/2, height/2, 0);
        bufferCam.update();
        batch.setProjectionMatrix(bufferCam.combined);
    }

    // use local version because calling Filter.render (super.render) would cause infinite loop
    private void localRender(FrameBuffer fbo, int x, int y, int w, int h ) {
        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false, true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(shader);
        batch.draw(s, x, y, w, h);    // draw frame buffer to screen window
        batch.setShader(null);
        batch.end();
    }

    private void renderToBuf(FrameBuffer fboOut, FrameBuffer fboIn ) {
        Sprite s = new Sprite(fboIn.getColorBufferTexture());
        s.flip(false, true); // coordinate system in buffer differs from screen

        fboOut.begin();
        localRender(fboIn, 0, 0, bufWidth, bufHeight);
        fboOut.end();
    }

    @Override
    public void render(FrameBuffer fbo, int x, int y, int w, int h) {

        int width = fbo.getWidth();
        int height = fbo.getHeight();
        if(fbo2 == null || width != bufWidth || height != bufHeight) {
            resize(width, height);
        }

        // ping pong between two buffers blurring horizontally then vertically
        for(int i = 0; i < iterations; i++) {
            shader = blurHorizontal;
            renderToBuf(fbo2, fbo);
            shader = blurVertical;
            if(i < iterations-1)            // last iteration not to buffer
                renderToBuf(fbo, fbo2);
        }
        localRender(fbo2, x, y, w, h);      // but to screen
    }

    @Override
    public void dispose() {
        super.dispose();
        blurVertical.dispose();
        blurHorizontal.dispose();
    }
}
