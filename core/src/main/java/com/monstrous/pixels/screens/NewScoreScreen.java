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
    private char[] initials = new char[3];
    private int charIndex;



    public NewScoreScreen(Main game, int points) {
        super(game);
        this.points = points;
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
        for(int i = 0;i < 3; i++)
            initials[i] = 'A';
    }

    @Override
    public void renderFrame(float deltaTime) {
        if(points < game.hiScores.lowestScore()){    // not a new hi score, go straight to next screen without adding this score
            game.setScreen(new HiScoreScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.hiScores.addScore(name.toString(), points);
            game.setScreen(new HiScoreScreen(game));
            return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            initials[charIndex]++;
            if(initials[charIndex] > 'Z')
                initials[charIndex] = 'A';
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            initials[charIndex]--;
            if(initials[charIndex] < 'A')
                initials[charIndex] = 'Z';
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && charIndex > 0){
            charIndex--;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && charIndex < 2){
            charIndex++;
        }

        name.setLength(0);
        for(int i = 0;i < 3; i++)
            name.append(initials[i]);

        ScreenUtils.clear(background, false);

        batch.begin();
        font.draw(batch, "--== NEW HI SCORE ==--", 80, 200);
        font.draw(batch, pointsString, 150, 160);
        font.draw(batch, name, 150, 100);
        font.draw(batch, "PRESS 1 TO CONTINUE", 90, 10);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
