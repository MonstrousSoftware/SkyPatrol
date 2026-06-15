package com.monstrous.pixels.filters;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;


public class Border implements Disposable {

    private static final int BORDER_SIZE = 50;
    private static final int NUM_STRIPES = 128;

    public static final int BLACK = 0;
    public static final int YELLOW = 1;
    public static final int CYAN = 2;

    private final SpriteBatch batch;
    private final int[] pattern = new int[NUM_STRIPES];
    private final Texture patternTexture;
    private final Pixmap patternPixmap;
    private int width, height;


    public Border() {

        // texture for the border pattern, we only need one pixel of width which will be stretched for the full screen width
        // NUM_STRIPES defines the vertical resolution
        // we don't need an alpha channel
        patternTexture = new Texture(1, NUM_STRIPES, Pixmap.Format.RGB565);
        patternPixmap = new Pixmap(1, NUM_STRIPES, Pixmap.Format.RGB565);

        batch = new SpriteBatch();
        setBorderColor(BLACK);
    }

    /** add a horizontal color bar */
    public void changeColor(int colorCode){
        for(int y = NUM_STRIPES-1; y > 0; y--)     // scroll up
            pattern[y] = pattern[y-1];
        pattern[0] = colorCode;
        updateTexture();
    }

    /** change the complete border to one color */
    public void setBorderColor(int colorCode){
        for (int y = NUM_STRIPES-1; y >= 0; y--)
            pattern[y] = colorCode;
        updateTexture();
    }

    /** update the pattern texture to match the pattern array */
    private void updateTexture(){
        for(int y = 0; y < NUM_STRIPES; y++){
            switch(pattern[y]){
                case BLACK:     patternPixmap.setColor(Color.BLACK); break;
                case YELLOW:    patternPixmap.setColor(Color.YELLOW); break;
                case CYAN:      patternPixmap.setColor(Color.BLUE); break;
            }
            patternPixmap.drawPixel(0, y);
        }
        patternTexture.draw(patternPixmap, 0, 0);
    }

    public void resize (int width, int height) {
        this.width = width;
        this.height = height;
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    public Texture getBorderTexture(){
        return patternTexture;
    }

    public void render( FrameBuffer fbo ) {
        render(fbo, 0, 0, width, height);    // draw frame buffer as screen filling texture
    }

    public void render( FrameBuffer fbo, float x, float y, float w, float h ) {
        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false,  true); // coordinate system in buffer differs from screen
        batch.begin();
        batch.draw(patternTexture, 0, 0, width, height);
        batch.draw(s, BORDER_SIZE, BORDER_SIZE, width-2*BORDER_SIZE, height-2*BORDER_SIZE);
        batch.end();
    }


    @Override
    public void dispose() {
        batch.dispose();
    }
}
