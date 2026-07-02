package com.monstrous.pixels.world;


import com.monstrous.pixels.world.ECS.Component;

public class AgeComponent extends Component {
    public final int id;
    public float timeToLive;
    public boolean isDead;
    public int partner; // entity id of a partner that needs to die at the same time

    public AgeComponent(int id, float timeToLive) {
        this(id, timeToLive, -1);
    }

    public AgeComponent(int id, float timeToLive, int partner) {
        this.id = id;
        this.timeToLive = timeToLive;
        this.isDead = false;
        this.partner = partner;
    }
}
