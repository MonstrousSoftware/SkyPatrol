package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class ProjectileComponent {
    public final int id;
    public final Vector3 position;  // overlapping with DynamicsComponent
    public DynamicsComponent target;
    public final boolean friendly;
    public boolean isMakingSound;   // set to true when close to player


    public ProjectileComponent(int id, Vector3 position, boolean friendly) {
        this.id = id;
        this.position = new Vector3(position);
        this.friendly = friendly;
        this.isMakingSound = false;
    }
}
