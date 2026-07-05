package com.monstrous.pixels.world;


import com.monstrous.pixels.world.ECS.Component;

public class AgeComponent extends Component {
    public float timeToLive;
    public boolean isDead;
    public int partner; // entity id of a partner that needs to die at the same time

    public AgeComponent(float timeToLive) {
        this( timeToLive, -1);
    }

    public AgeComponent(float timeToLive, int partner) {
        this.timeToLive = timeToLive;
        this.isDead = false;
        this.partner = partner;
    }
}
