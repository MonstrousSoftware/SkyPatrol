package com.monstrous.pixels.world;

public class ProjectileSystem {

    public static void update(ProjectileComponent component, DynamicsComponent dynamics) {
        component.position.set(dynamics.position);

        // rocket with a target are "heat seeking". They will follow their target.
        if(component.target != null){
            // make rocket point towards target (instantly)
            float speed = dynamics.velocity.len();
            dynamics.velocity.set(component.target.position).sub(component.position).nor().scl(speed);
        }
    }
}
