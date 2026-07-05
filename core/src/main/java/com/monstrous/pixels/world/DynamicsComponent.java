package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class DynamicsComponent extends Component {
    public Vector3 position;
    public Vector3 velocity;     // velocity vector
    public float turnSpeed;
    public float gravity;

    public DynamicsComponent(){

    }

    public DynamicsComponent(Vector3 position, Vector3 velocity, float turnSpeed, float gravity) {
        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.turnSpeed = turnSpeed;
        this.gravity = gravity;
    }

    public void set(Vector3 position, Vector3 velocity, float turnSpeed, float gravity) {
        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.turnSpeed = turnSpeed;
        this.gravity = gravity;
    }
}
