package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;


public class HiScoreScreen extends RetroScreen {

    private SpriteBatch batch;
    private Color background;

    public HiScoreScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);
    }

    @Override
    public void renderFrame(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.setScreen(new StartScreen(game));
            return;
        }


        ScreenUtils.clear(background, false);

        batch.begin();
        font.draw(batch, "--== TOP GUNS ==--", 80, 200);
        for(int i = 0; i < 10; i++){
            Score score = game.hiScores.getScore(i);
            String line = String.format("%8d...%s %s", score.points, score.name, score.hardCore ? "*": "");
            font.draw(batch, line, 80, 180-i*10);
        }
        font.draw(batch, "*: ONE LIFE ONLY", 90, 60);
        font.draw(batch, "PRESS 1 TO CONTINUE", 90, 10);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
