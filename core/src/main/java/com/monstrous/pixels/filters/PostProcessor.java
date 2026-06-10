package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

public class PostProcessor implements Disposable {

    private final TVFilter TVFilter;
    private int width;
    private int height;

    public PostProcessor() {
        TVFilter = new TVFilter();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        TVFilter.resize(width, height);
    }

    public void render ( FrameBuffer fbo ) {
        render(fbo, 0, 0, width, height);
    }


    // todo Note we're upscaling a low rez fbo, but for a good bloom we need to do this at high res
    public void render ( FrameBuffer fbo, int x, int y, int w, int h ) {
        TVFilter.render(fbo,  x, y, w, h);
    }

    @Override
    public void dispose () {
        TVFilter.dispose();
    }
}
