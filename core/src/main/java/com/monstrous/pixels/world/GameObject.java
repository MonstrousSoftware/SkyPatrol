package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class GameObject {
    public final GameObjectType type;
    public final Vector3 position;
    public final Vector3 direction;
    public final Vector3 forward;
    public final Vector3 spinAxis;
    public float speed;
    public float turnSpeed;
    public float timeToLive;
    public boolean isDead;

    private final Vector3 tmpVec = new Vector3();


    public GameObject(GameObjectType type, Vector3 position, Vector3 direction) {
        this.type = type;
        this.position = new Vector3(position);
        this.direction = new Vector3(direction).nor();
        this.forward = new Vector3(direction).nor();
        this.speed = type.speed;
        this.turnSpeed = type.turnSpeed;
        this.timeToLive = type.timeToLive;
        this.spinAxis = new Vector3(type.spinAxis);
        this.isDead = false;
    }

    public void update(float delta){
        if(speed > 0) {
            tmpVec.set(direction).scl(speed * delta);
            position.add(tmpVec);
        }
        if(turnSpeed > 0)
            direction.rotate(Vector3.Y, delta * turnSpeed);
//        if(type.spinSpeed > 0)
//            forward.rotate(spinAxis, delta * type.spinSpeed);
//        else
//            forward.set(direction);

        if(type.gravity > 0)
            direction.y -= delta * type.gravity;

        if(timeToLive >= 0){
            timeToLive -= delta;
            if(timeToLive < 0)
                isDead = true;
        }
        if(position.y < 0)
            isDead = true;
    }

}
