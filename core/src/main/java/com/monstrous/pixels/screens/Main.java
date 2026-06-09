package com.monstrous.pixels.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Comparator;

public class Main extends Game {

    public Score[] scores;

    @Override
    public void create() {
        scores = new Score[10];
        for(int i = 0; i < 10; i++){
            scores[i] =  new Score((10-i)*100, "BOB");
        }


        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            setScreen(new InitScreen(this));
        else
            setScreen(new InitScreen(this));

    }

    public int lowestScore(){
        return scores[9].points;
    }

    public void addScore(String name, int points){
        if(points < lowestScore())
            return;
        scores[9].points = points;
        scores[9].name = name;
        Arrays.sort(scores);
    }
}
