package com.monstrous.pixels.screens;

import com.badlogic.gdx.Game;


public class Main extends Game {

    public HiScores hiScores;
    public int savedWidth, savedHeight; // used for F11 full screen toggle
    public boolean enableMusic = true;
    public boolean invertedControls = false;

    @Override
    public void create() {
        hiScores = new HiScores();

//        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
//            setScreen(new LoadScreen(this));
//        else
        setScreen(new LoadScreen(this));

    }

}
