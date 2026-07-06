package com.monstrous.pixels.world.components;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class ProjectileComponent extends Component {
    public Vector3 position;  // overlapping with DynamicsComponent
    public DynamicsComponent target;
    public boolean friendly;
    public boolean isMakingSound;   // set to true when close to player

    public ProjectileComponent(){

    }

    // target may be null
    public ProjectileComponent(Vector3 position, boolean friendly, DynamicsComponent target) {
        this.position = new Vector3(position);
        this.friendly = friendly;
        this.isMakingSound = false;
        this.target = target;
    }

    public void set(Vector3 position, boolean friendly, DynamicsComponent target) {
        this.position = new Vector3(position);
        this.friendly = friendly;
        this.isMakingSound = false;
        this.target = target;
    }
}
