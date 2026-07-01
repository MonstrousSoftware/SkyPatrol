package com.monstrous.pixels.world;


import com.monstrous.pixels.world.ECS.Component;

public class AgeComponent extends Component {
    public final int id;
    public float timeToLive;
    public boolean isDead;
    public AgeComponent partner;

    public AgeComponent(int id, float timeToLive) {
        this.id = id;
        this.timeToLive = timeToLive;
        this.isDead = false;
    }
}
