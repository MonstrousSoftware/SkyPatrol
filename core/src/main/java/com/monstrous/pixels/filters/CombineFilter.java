package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CombineFilter extends Filter {

    public CombineFilter() {
        super();
        shader = new ShaderProgram(
                Gdx.files.internal("shaders\\combine.vertex.glsl"),
                Gdx.files.internal("shaders\\combine.fragment.glsl"));
        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());
    }

    // call this before calling one of the render() functions
    public void setHighlightTexture( FrameBuffer fbo2 ) {

        shader.bind();
        shader.setUniformi("u_highlightTexture", 1);
        Gdx.gl.glActiveTexture(Gdx.gl20.GL_TEXTURE1);		// bind fbo2 texture to texture unit 1
        fbo2.getColorBufferTexture().bind();
        Gdx.gl.glActiveTexture(Gdx.gl20.GL_TEXTURE0);
    }
}
