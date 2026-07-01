package com.monstrous.pixels.world;


import com.monstrous.pixels.world.ECS.Component;

public class FiringComponent extends Component {
    public final int id;
    public float timeToFire;
    public GameObjectType type;

    public FiringComponent(int id, GameObjectType type) {
        this.id = id;
        this.timeToFire = (float)Math.random() * 10f;
        this.type = type;
    }
}
