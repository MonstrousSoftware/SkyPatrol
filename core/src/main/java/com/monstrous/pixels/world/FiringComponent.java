package com.monstrous.pixels.world;


public class FiringComponent {
    public final int id;
    public float timeToFire;
    public GameObjectType type;

    public FiringComponent(int id, GameObjectType type) {
        this.id = id;
        this.timeToFire = (float)Math.random() * 10f;
        this.type = type;
    }
}
