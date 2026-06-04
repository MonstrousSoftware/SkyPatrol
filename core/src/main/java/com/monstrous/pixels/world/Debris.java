package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class Debris {
    public final Vector3 position;
    public final Vector3 direction;
    public final Vector3 forward;
    public final Vector3 axis;
    public float speed;
    public float timeToLive;
    private final Vector3 tmpVec = new Vector3();


    public Debris(Vector3 position, Vector3 direction, Vector3 axis) {
        this.position = new Vector3(position);
        this.direction = new Vector3(direction).nor();
        this.forward = new Vector3(direction).nor();
        this.axis = new Vector3(axis).nor();
        this.speed = 30f;
        this.timeToLive = 8f;
    }

    public void forward(float delta){
        tmpVec.set(forward).scl(speed*delta);
        position.add(tmpVec);
        forward.y -= 0.5f* delta;
        direction.rotate(axis, delta * 50f);    // spin
        timeToLive -= delta;
        if(position.y <= -5)
            timeToLive = 0;
    }

}
