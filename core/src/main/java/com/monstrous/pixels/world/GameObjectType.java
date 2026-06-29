package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class GameObjectType {
    public String typeName;
    public int scorePoints;
    public final Model model;
    public final float radius;
    public float speed;
    public float turnSpeed;
    public float spinSpeed;
    public final Vector3 spinAxis;
    public float timeToLive;
    public float gravity;

    public GameObjectType(String typeName, Model model ) {

        this.typeName = typeName;
        this.scorePoints = 0;
        this.model = model;
        this.radius = model.nodes.get(0).parts.get(0).meshPart.radius;
        speed = 0;
        turnSpeed = 0;
        spinSpeed = 0;
        timeToLive = 99999999f;
        spinAxis = new Vector3(Vector3.Y);
        gravity = 0;
    }
}
