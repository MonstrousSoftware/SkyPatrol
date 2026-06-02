package com.monstrous.pixels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class StartScreen extends ScreenAdapter {

    private Main game;
    private BitmapFont font;
    private SpriteBatch batch;


    public StartScreen(Main game) {
        this.game = game;

    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);

    }

    @Override
    public void render(float deltaTime) {
        ScreenUtils.clear(Color.BLACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            game.setScreen( new GameScreen(game) );

        batch.begin();
        font.draw(batch, "PRESS [SPACE]", 200, 32);
        batch.end();

    }

//    @Override
//    public void resize(int width, int height) {
//    }
//
//
//    @Override
//    public void hide() {
//        // This method is called when another screen replaces this one.
//    }
//
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        font.dispose();
        batch.dispose();

    }
}
