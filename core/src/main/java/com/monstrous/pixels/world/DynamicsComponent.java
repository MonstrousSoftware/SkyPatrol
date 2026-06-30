package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class DynamicsComponent {
    public final int id;
    public final Vector3 position;
    public final Vector3 velocity;     // velocity vector
    public float turnSpeed;
    public float gravity;


    public DynamicsComponent(int id, Vector3 position, Vector3 velocity, float turnSpeed, float gravity) {
        this.id = id;
        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.turnSpeed = turnSpeed;
        this.gravity = gravity;
    }
}
