package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class ColliderComponent extends Component {
    public final int id;
    public final Vector3 position;  // overlapping with DynamicsComponent
    public float radius;
    public Color color;         // color to use for debris
    public GameObjectType type;


    public ColliderComponent(int entityId, Vector3 position, float radius, Color color, GameObjectType type) {
        this.id = entityId;
        this.position = new Vector3(position);
        this.radius = radius;
        this.color = new Color(color);
        this.type = type;
    }
}
