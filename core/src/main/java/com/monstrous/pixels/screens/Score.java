package com.monstrous.pixels.screens;

public class Score implements Comparable<Score> {
    public int points;
    public String name;
    public boolean hardCore;

    public Score(int points, String name, boolean hardCore) {
        this.points = points;
        this.name = name;
        this.hardCore = hardCore;
    }


    public int getPoints() {
        return points;
    }

    // to sort in descending order
    @Override
    public int compareTo(Score score) {
        if(this.points < score.getPoints()) return 1;
        if(this.points == score.getPoints()) return 0;
        else return -1;
    }
}
