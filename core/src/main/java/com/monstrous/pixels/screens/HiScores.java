package com.monstrous.pixels.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Arrays;

public class HiScores {
    public final int NUM_SCORES = 10;

    private final Preferences prefs;
    private final Score[] scores;

    public HiScores() {
        prefs = Gdx.app.getPreferences("skypatrol.txt");
        scores = new Score[NUM_SCORES];
        load();
    }

    public int lowestScore(){
        return scores[NUM_SCORES-1].points;
    }

    public Score getScore(int i){
        return scores[i];
    }

    public void addScore(String name, int points){
        if(points < lowestScore())
            return;
        scores[NUM_SCORES-1].points = points;
        scores[NUM_SCORES-1].name = name;
        Arrays.sort(scores);
        save();
    }


    public void load(){
        for(int i = 0; i < NUM_SCORES; i++){
            String name = prefs.getString("name"+i, "BOB");
            int points = prefs.getInteger("points"+i, (NUM_SCORES-i)*100);
            scores[i] =  new Score(points, name);
        }
    }

    public void save(){
        for(int i = 0; i < NUM_SCORES; i++){
            prefs.putString("name"+i, scores[i].name);
            prefs.putInteger("points"+i, scores[i].points);
        }
        prefs.flush();
    }
}
