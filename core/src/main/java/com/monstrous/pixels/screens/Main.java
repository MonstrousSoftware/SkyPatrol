package com.monstrous.pixels.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

    public HiScores hiScores;
    public int savedWidth, savedHeight;
    public boolean enableMusic = true;

    @Override
    public void create() {
        hiScores = new HiScores();

        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            setScreen(new InitScreen(this));
        else
            setScreen(new LoadScreen(this));

    }

}
