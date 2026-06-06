package com.monstrous.pixels.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

    @Override
    public void create() {
        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            setScreen(new InitScreen(this));
        else
            setScreen(new StartScreen(this));

    }
}
