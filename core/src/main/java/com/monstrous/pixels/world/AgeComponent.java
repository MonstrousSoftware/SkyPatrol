package com.monstrous.pixels.world;


public class AgeComponent {
    public final int id;
    public float timeToLive;
    public boolean isDead;
    public AgeComponent partner;

    public AgeComponent(int id, float timeToLive) {
        this.id = id;
        this.timeToLive = timeToLive;
        this.isDead = false;
    }
}
