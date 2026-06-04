package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class Jet {
    public final Vector3 position;
    public final Vector3 direction;
    public float speed;
    public float timeToLive;
    private final Vector3 tmpVec = new Vector3();


    public Jet(Vector3 position, Vector3 direction) {
        this.position = new Vector3(position);
        this.direction = new Vector3(direction).nor();
        this.speed = 30f;
        this.timeToLive = 1f;
    }

    public void forward(float delta){
        tmpVec.set(direction).scl(speed*delta);
        position.add(tmpVec);
    }

    public void rotate(float delta, float turnSpeed){
        direction.rotate(Vector3.Y, delta * turnSpeed);
    }
}
