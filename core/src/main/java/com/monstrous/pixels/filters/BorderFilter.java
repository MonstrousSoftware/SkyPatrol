package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;


// post-processing effect to render an FBO to screen

public class BorderFilter implements Disposable {

    private final static String SHADER = "border";

    private static final int NUM_STRIPES = 128;

    private final SpriteBatch batch;
    private final ShaderProgram program;
    private float time;
    private final float[] resolution = { 640, 480 };  // modified by resize()
    private final float[] colors = { 0f,0f,0f,  1f,1f,0f,   0f,0f,1f }; // black, yellow, blue
    private final int[] pattern = new int[NUM_STRIPES];
    private final int u_time; // shader uniform id
    private final int u_resolution; // shader uniform id
    private final int u_pattern;
    private final int u_colors;


    public BorderFilter() {
        // full screen post processing shader
        program = new ShaderProgram(
            Gdx.files.internal("shaders\\" + SHADER + ".vertex.glsl"),
            Gdx.files.internal("shaders\\" + SHADER + ".fragment.glsl"));
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        ShaderProgram.pedantic = true;

        u_time = program.getUniformLocation("u_time");
        u_resolution = program.getUniformLocation("u_resolution");
        u_pattern = program.getUniformLocation("u_pattern[0]"); // beware: name has to include "[0]"!
        u_colors = program.getUniformLocation("u_colors[0]");

        program.setUniform3fv(u_colors, colors, 0, 9);

        batch = new SpriteBatch();
        setBorderColor(0);
    }

    /** add a horizontal color bar */
    public void changeColor(int colorCode){
        for(int y = NUM_STRIPES-1; y > 0; y--)     // scroll up
            pattern[y] = pattern[y-1];
        pattern[0] = colorCode;
    }

    /** change the complete border to one color */
    public void setBorderColor(int colorCode){
        for (int y = NUM_STRIPES-1; y >= 0; y--)
            pattern[y] = colorCode;
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
        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false,  true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(program);                        // post-processing shader
        program.setUniformf(u_time, time);
        program.setUniform2fv(u_resolution, resolution, 0, 2);
        program.setUniform1iv(u_pattern, pattern, 0, NUM_STRIPES);
        program.setUniform3fv(u_colors, colors, 0, 9);
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
