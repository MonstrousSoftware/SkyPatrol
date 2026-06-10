package com.monstrous.pixels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;


public class WireFrameShader extends DefaultShader {

    public WireFrameShader(Renderable renderable) {
        super(renderable, new Config(
            Gdx.files.internal("shaders/wireframe.vertex.glsl").readString(),
            Gdx.files.internal("shaders/wireframe.fragment.glsl").readString() ) );
    }


     // assumes the shader is only ever called for wireframe renderables
    @Override
    public boolean canRender(Renderable renderable) {
        return true;
    }

}
