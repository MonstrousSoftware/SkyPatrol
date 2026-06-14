package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.cassette.Cassette;
import com.monstrous.pixels.cassette.WavIO;
import com.monstrous.pixels.filters.BorderFilter;

import javax.swing.*;


public class LoadScreen extends ScreenAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;

    final static String FILE_NAME = "tapes/input.wav";

    private SpriteBatch batch;
    private Texture texture;
    private Pixmap oriPixmap;
    private Texture newTexture;
    private Pixmap pixmap;
    private Sound sound;
    private Cassette cassette;
    private BitmapFont font;
    private StringBuilder sb;
    private final Main game;
    private FrameBuffer fbo;
    private BorderFilter filter;
    private boolean more = true;
    private boolean onPause = true;
    private int x, y;

    public LoadScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);
        font.getLineHeight();
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
        filter = new BorderFilter();
        sound = Gdx.audio.newSound(Gdx.files.internal(FILE_NAME));

        texture = new Texture(Gdx.files.internal("images/title.png"));
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        oriPixmap = texture.getTextureData().consumePixmap();
        pixmap = new Pixmap(texture.getWidth(), texture.getHeight(), Pixmap.Format.RGBA8888);
        newTexture = new Texture(pixmap);
        newTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);


        sb = new StringBuilder();
        sb.setLength(0);
        more = true;
    }

    @Override
    public void render(float delta) {
        if(!more && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new StartScreen(game));
            return;
        }
        fbo.begin();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (onPause ){
            batch.begin();
            font.draw(batch, "> LOAD \"SKYPATROL\"", 0, 160);
            batch.end();

            if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                onPause = false;
                sb.append("\n");
                x = 0;
                y = 0;

                WavIO wavIO = new WavIO();
                byte[] samples = wavIO.read(Gdx.files.internal(FILE_NAME));
                if(samples == null)
                    System.out.println("READ ERROR");
                else {

                    sound.play();
                    cassette = new Cassette(filter);
                    cassette.startConversion(samples);
                }
            }
        } else {
            more = cassette.updateConversion(delta);

            while (cassette.isDataReady()) {
                byte b = cassette.getByte();
                if (b != 0) {   // for ascii this makes sense
                    sb.append((char) b);
                    System.out.print((char) b);
                }
            }

            for (int i = 0; i < 512; i++) {
                int col = oriPixmap.getPixel(x, y);
                pixmap.drawPixel(x, y, col);
                x++;
                if (x >= LOWRES_WIDTH) {
                    y++;
                    x = 0;
                }
            }
            newTexture.draw(pixmap, 0, 0);


            batch.begin();
            batch.draw(newTexture, 0, 0);
            if (!more) {
                font.setColor(Color.BLACK);
                font.draw(batch, "PRESS [SPACE]", 120, 10);
            }
            batch.end();

        }

        fbo.end();
        filter.render(fbo);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        // todo
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        filter.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        filter.dispose();
        fbo.dispose();
    }

//    private void writeMessage(){
//        String msg = "Now is the time for all good men\nto come to the aid of the party.\nDrink your Ovaltine.";
//        WavIO wavIO = new WavIO();
//        byte[] newSamples = cassette.convertToSamples(msg.getBytes());
//        if (!wavIO.write(Gdx.files.local("output.wav"), newSamples))
//            System.out.println("WRITE ERROR");
//    }
}
