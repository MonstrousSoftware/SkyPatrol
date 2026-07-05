package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class ProjectileComponent extends Component {
    public final Vector3 position;  // overlapping with DynamicsComponent
    public DynamicsComponent target;
    public final boolean friendly;
    public boolean isMakingSound;   // set to true when close to player


    // target may be null
    public ProjectileComponent(Vector3 position, boolean friendly, DynamicsComponent target) {
        this.position = new Vector3(position);
        this.friendly = friendly;
        this.isMakingSound = false;
        this.target = target;
    }
}
