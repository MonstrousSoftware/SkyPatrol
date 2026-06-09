package com.monstrous.pixels.screens;

public class Score implements Comparable<Score> {
    int points;
    String name;

    public Score(int points, String name) {
        this.points = points;
        this.name = name;
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
