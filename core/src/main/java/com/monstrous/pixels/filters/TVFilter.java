package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;


// post-processing effect to render an FBO to screen applying underwater shader effects

public class TVFilter implements Disposable {

    private static String SHADER = "tv";

    private SpriteBatch batch;
    private ShaderProgram program;
    private float time;
    private float[] resolution = { 640, 480 };  // modified by resize()
    private int u_time; // shader uniform id
    private int u_resolution; // shader uniform id

    public TVFilter() {
        // full screen post processing shader
        program = new ShaderProgram(
            Gdx.files.internal("shaders\\" + SHADER + ".vertex.glsl"),
            Gdx.files.internal("shaders\\" + SHADER + ".fragment.glsl"));
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        ShaderProgram.pedantic = false;

        u_time = program.getUniformLocation("u_time");
        u_resolution = program.getUniformLocation("u_resolution");

        batch = new SpriteBatch();
    }

    public void resize (int width, int height) {
        resolution[0] = width;
        resolution[1] = height;
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);  // to ensure the fbo is rendered to the full window after a resize

    }

    public void render( FrameBuffer fbo ) {
        render(fbo, 0, 0, resolution[0], resolution[1]);    // draw frame buffer as screen filling texture
    }

    public void render( FrameBuffer fbo, float x, float y, float w, float h ) {
        time += Gdx.graphics.getDeltaTime();

        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false,  true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(program);                        // post-processing shader
        program.setUniformf(u_time, time);
        program.setUniform2fv(u_resolution, resolution, 0, 2);
        batch.draw(s, x, y, w, h);    // draw frame buffer as screen filling texture
        batch.end();
        batch.setShader(null);
    }


    @Override
    public void dispose() {
        batch.dispose();
        program.dispose();
    }
}
