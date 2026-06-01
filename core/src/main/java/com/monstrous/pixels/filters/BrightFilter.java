package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BrightFilter extends Filter {

    public BrightFilter() {
        super();
        shader = new ShaderProgram(
                Gdx.files.internal("shaders\\brightfilter.vertex.glsl"),
                Gdx.files.internal("shaders\\brightfilter.fragment.glsl"));
        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());
    }
}
