package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.monstrous.pixels.cassette.Cassette;
import com.monstrous.pixels.cassette.WavIO;
import com.monstrous.pixels.filters.Border;

import java.nio.ByteBuffer;


public class LoadScreen extends RetroScreen {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;
    public final int ZX_WIDTH = 256;
    public final int ZX_HEIGHT = 192;

    final static String FILE_NAME = "tapes/input.wav";

    private Color background;
    private SpriteBatch batch;
    private Texture sourceTexture;
    private Pixmap sourcePixmap;
    private Texture destTexture;
    private Pixmap destPixmap;
    private Sound sound;
    private Cassette cassette;
    private Border filter;
    private boolean more = true;
    private boolean onPause = true;
    private int x, y;
    private Texture paperBackground;

    public LoadScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        font.setColor(Color.WHITE);
        background = new Color(0.15f, 0.15f, 0.2f, 1f);

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
        filter = new Border();
        sound = Gdx.audio.newSound(Gdx.files.internal(FILE_NAME));

        sourceTexture = new Texture(Gdx.files.internal("images/choppa-zx2.png"));
        if (!sourceTexture.getTextureData().isPrepared()) {   // need to do this to extract the pixmap
            sourceTexture.getTextureData().prepare();
        }
        sourcePixmap = sourceTexture.getTextureData().consumePixmap();

//        ByteBuffer bb = sourcePixmap.getPixels();
//        int numBytes = bb.limit();
//        byte[] data = new byte[numBytes];
//        bb.get(data);
//        cassette = new Cassette(filter);
//        byte[] newSamples = cassette.convertToSamples(data);
//        WavIO wavIO = new WavIO();
//        FileHandle file = Gdx.files.local("choppa.wav");
//        wavIO.write(file, newSamples);


        // pixmap and texture to read the image data into, this si the texture that will be put on screen as it is read line by line
        destPixmap = new Pixmap(sourceTexture.getWidth(), sourceTexture.getHeight(), Pixmap.Format.RGB565);
        destPixmap.setColor(background);
        destPixmap.fill();
        destTexture = new Texture(destPixmap);
        destTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);


        // one pixel texture to use for background fill
        Pixmap paper = new Pixmap(1,1, Pixmap.Format.RGB565);
        paper.setColor(background);
        paper.fill();
        paperBackground = new Texture(paper);

        more = true;
    }

    @Override
    public void renderFrame(float delta) {
        if(!more && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new StartScreen(game));
            return;
        }

        batch.begin();
        batch.draw(filter.getBorderTexture(), 0, 0, LOWRES_WIDTH, LOWRES_HEIGHT);
        batch.draw(paperBackground, (LOWRES_WIDTH-ZX_WIDTH)/2f, (LOWRES_HEIGHT-ZX_HEIGHT)/2f, ZX_WIDTH, ZX_HEIGHT);

        if ( onPause ){

            font.draw(batch, "> LOAD \"SKYPATROL\"", 40, 40);

            if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                onPause = false;
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
            // convert a few ms of samples
            more = cassette.updateConversion(delta);

            // read any converted bytes (and ignore)
            // this should be the image data but that would take far too long so we just read some dummy data
            while (cassette.isDataReady()) {
                byte b = cassette.getByte();
                if (b != 0) {   // for ascii this makes sense
                    System.out.print((char) b);
                }
            }

            // visual feedback
            // this should take as long as the audio effect
            for (int i = 0; i < 512; i++) {
                int col = sourcePixmap.getPixel(x, y);
                destPixmap.drawPixel(x, y, col);
                x++;
                if (x >= LOWRES_WIDTH) {
                    y++;
                    x = 0;
                }
            }
            destTexture.draw(destPixmap, 0, 0);


            //batch.begin();
            batch.draw(destTexture, (LOWRES_WIDTH-ZX_WIDTH)/2f, (LOWRES_HEIGHT-ZX_HEIGHT)/2f);
            if (!more) {
                batch.draw(paperBackground, (LOWRES_WIDTH-ZX_WIDTH)/2f, (LOWRES_HEIGHT-ZX_HEIGHT)/2f, ZX_WIDTH, 10);
                font.setColor(Color.WHITE);
                font.draw(batch, "PRESS [SPACE]", 100, 30);
            }
        }
        batch.end();
    }


    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        filter.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        filter.dispose();
    }
}
