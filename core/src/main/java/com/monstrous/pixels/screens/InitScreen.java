package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class InitScreen extends RetroScreen {

    private SpriteBatch batch;


    public InitScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
    }

    @Override
    public void renderFrame(float deltaTime) {
        ScreenUtils.clear(Color.BLACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            game.setScreen( new StartScreen(game) );

        batch.begin();
        font.draw(batch, "PRESS [SPACE]", 70, 32);
        batch.end();

    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();
    }
}
