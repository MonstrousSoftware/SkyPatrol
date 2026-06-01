package com.monstrous.pixels.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

public class PostProcessor implements Disposable {

    private FrameBuffer fboBlur;
    private BrightFilter brightFilter;
    private BlurFilter blurFilter;
    private CombineFilter combineFilter;
    private TVFilter TVFilter;
    private Filter filter;
    private int width;
    private int height;

    public PostProcessor() {
        brightFilter = new BrightFilter();
        blurFilter = new BlurFilter( 2);
        combineFilter = new CombineFilter();
        TVFilter = new TVFilter();
        filter = new Filter();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        // the buffers used for blurring have a lower resolution
        // this saves processing and gives a more blurry effect
        fboBlur = new FrameBuffer(Pixmap.Format.RGBA8888, width/8, height/8, false);

        brightFilter.resize(width, height);
        blurFilter.resize(width, height);
        combineFilter.resize(width, height);
        TVFilter.resize(width, height);
        filter.resize(width, height);
    }

    public void render ( FrameBuffer fbo ) {
        render(fbo, 0, 0, width, height);
    }


    // todo Note we're upscaling a low rez fbo, but for a good bloom we need to do this at high res
    public void render ( FrameBuffer fbo, int x, int y, int w, int h ) {

        // brightness filter fbo -> fboBlur
        // brightness filter works best with white pixels
//       brightFilter.renderToBuffer(fboBlur, fbo);
//
//        blurFilter.renderToBuffer(fboBlur, fboBlur);
//        // latest blurred image is now in fboBlur
//
//        // combine original render with blurred highlights
//        combineFilter.setHighlightTexture(fboBlur);
//        combineFilter.render(fbo,  x, y, w, h);

        TVFilter.render(fbo,  x, y, w, h);
    }

    @Override
    public void dispose () {
        fboBlur.dispose();
        brightFilter.dispose();
        blurFilter.dispose();
        combineFilter.dispose();
        TVFilter.dispose();
    }
}
