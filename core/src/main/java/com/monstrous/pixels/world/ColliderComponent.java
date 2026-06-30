package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class ColliderComponent {
    public final int id;
    public final Vector3 position;  // overlapping with DynamicsComponent
    public float radius;
    public Color color;         // color to use for debris
    public GameObjectType type;


    public ColliderComponent(int id, Vector3 position, float radius, Color color, GameObjectType type) {
        this.id = id;
        this.position = new Vector3(position);
        this.radius = radius;
        this.color = new Color(color);
        this.type = type;
    }
}
