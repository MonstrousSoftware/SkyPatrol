package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class GameObject {
    // note: this is a fat game object carrying member fields for all types of game objects
    // but it avoids a class hierarchy
    public final GameObjectType type;
    public final Vector3 position;
    public final Vector3 velocity;     // velocity vector
    public final Vector3 forward;       // which way the model is pointing
    public final Vector3 spinAxis;
    public GameObject target;       // for rockets
    public GameObject parent;   // for turret
    public GameObject child;    // turret is child of tank
    //public float speed;         // could be combined with direction to be velocity vector
    public float turnSpeed;
    public float timeToLive;
    public float timeToFire;
    public boolean isDead;
    public boolean isMakingSound;

    private final Vector3 tmpVec = new Vector3();

    public GameObject(GameObjectType type, Vector3 position, Vector3 velocity) {
        this(type, position, velocity, velocity);
    }

    public GameObject(GameObjectType type, Vector3 position, Vector3 velocity, Vector3 forward) {
        this.type = type;
        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.forward = new Vector3(forward).nor();
        //this.speed = type.speed;
        this.turnSpeed = type.turnSpeed;
        this.timeToLive = type.timeToLive;
        this.timeToFire = (float)Math.random() * 10f;
        this.spinAxis = new Vector3(type.spinAxis);
        this.target = null;
        this.isDead = false;
        this.isMakingSound = false;
    }

    public void update(float delta){
        if(parent != null){
            position.set(parent.position);
        }
        if(velocity.len2() > 0f) {
            tmpVec.set(velocity).scl(delta);
            position.add(tmpVec);
        }
        if(turnSpeed > 0)
            velocity.rotate(Vector3.Y, delta * turnSpeed);

        if(type.gravity > 0)
            velocity.y -= delta * type.gravity;

        if(type.spinSpeed > 0)
            forward.rotate(spinAxis, delta * type.spinSpeed);
        else
            forward.set(velocity).nor();

        if(timeToLive >= 0){
            timeToLive -= delta;
            if(timeToLive < 0)
                isDead = true;
        }
        if(position.y < 0)
            isDead = true;
    }

}
