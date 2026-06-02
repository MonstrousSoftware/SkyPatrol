package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Tank {
    public final Vector3 position;
    public final Vector3 direction;
    public float speed;
    private final Vector3 tmpVec = new Vector3();


    public Tank(Vector3 position, Vector3 direction) {
        this.position = new Vector3(position);
        this.direction = new Vector3(direction);
        this.speed = 1f;
    }

    public void forward(float delta){
        tmpVec.set(direction).scl(speed*delta);
        position.add(tmpVec);
        //Gdx.app.log("pos", position.toString());
    }

    public void rotate(float delta, float turnSpeed){
        direction.rotate(Vector3.Y, delta * turnSpeed);
    }
}
