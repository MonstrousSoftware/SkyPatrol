package com.monstrous.pixels.world.components;


import com.monstrous.pixels.world.ECS.Component;
import com.monstrous.pixels.world.GameObjectType;

public class FiringComponent extends Component {
    public float timeToFire;
    public GameObjectType type;     // to distinguish tank & jet

    public FiringComponent(GameObjectType type) {
        this.timeToFire = (float)Math.random() * 10f;
        this.type = type;
    }
}
