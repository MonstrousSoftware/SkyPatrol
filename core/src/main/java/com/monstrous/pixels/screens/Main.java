package com.monstrous.pixels.screens;

import com.badlogic.gdx.Game;


public class Main extends Game {

    public HiScores hiScores;
    // settings we want to keep between games
    public int savedWidth, savedHeight; // used for F11 full screen toggle
    public boolean enableMusic = true;
    public boolean invertedControls = false;
    public boolean oneLife = true;
    public char[] initials = new char[3];

    @Override
    public void create() {
        for(int i = 0;i < 3; i++)
            initials[i] = 'A';
        hiScores = new HiScores();
        setScreen(new LoadScreen(this));
    }

}
