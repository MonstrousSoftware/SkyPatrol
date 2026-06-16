package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;


public class NewScoreScreen extends RetroScreen {

    private final int points;
    private SpriteBatch batch;
    private Color background;
    private StringBuffer name;
    private String pointsString;
    private final boolean hardCore;
    private int charIndex;



    public NewScoreScreen(Main game, int points, boolean hardCore) {
        super(game);
        this.points = points;
        this.hardCore = hardCore;
    }

    @Override
    public void show() {
        super.show();

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);
        name = new StringBuffer();
        charIndex = 0;
        pointsString = ""+points;
    }

    @Override
    public void renderFrame(float deltaTime) {
        if(points < game.hiScores.lowestScore()){    // not a new hi score, go straight to next screen without adding this score
            game.setScreen(new HiScoreScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.hiScores.addScore(name.toString(), points, hardCore);
            game.setScreen(new HiScoreScreen(game));
            return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            game.initials[charIndex]++;
            if(game.initials[charIndex] > 'Z')
                game.initials[charIndex] = 'A';
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            game.initials[charIndex]--;
            if(game.initials[charIndex] < 'A')
                game.initials[charIndex] = 'Z';
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && charIndex > 0){
            charIndex--;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && charIndex < 2){
            charIndex++;
        }

        name.setLength(0);
        for(int i = 0;i < 3; i++)
            name.append(game.initials[i]);

        ScreenUtils.clear(background, false);

        batch.begin();
        font.draw(batch, "--== NEW HI SCORE ==--", 80, 200);
        font.draw(batch, pointsString, 150, 160);
        font.draw(batch, name, 150, 100);
        font.draw(batch, "USE CURSOR KEYS TO EDIT NAME", 40, 60);
        font.draw(batch, "PRESS 1 TO CONTINUE", 90, 10);

        font.draw(batch, "_", 150+9*charIndex, 96);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
