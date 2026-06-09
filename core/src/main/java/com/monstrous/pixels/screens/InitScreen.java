package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class InitScreen extends RetroScreen {

    private SpriteBatch batch;
    private Texture texture;


    public InitScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        texture = new Texture(Gdx.files.internal("images/title.png"));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
    }

    @Override
    public void renderFrame(float deltaTime) {
        ScreenUtils.clear(Color.BLACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            game.setScreen( new StartScreen(game) );

        batch.begin();
        batch.draw(texture, 0, 0);
        font.setColor(Color.WHITE);
        font.draw(batch, "PRESS [SPACE]", 200, 32);
        batch.end();

    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();
        texture.dispose();
    }
}
