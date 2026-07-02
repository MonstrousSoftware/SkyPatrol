package com.monstrous.pixels.world;

import com.monstrous.pixels.world.ECS.*;

public class M_ProjectileSystem extends EntitySystem {

    private final ComponentMapper<ProjectileComponent> projMap;
    private final ComponentMapper<DynamicsComponent> dynMap;

    public M_ProjectileSystem(Engine engine) {
        super(engine);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
        projMap = engine.componentManager.getComponentMapper(ProjectileComponent.class);
        ComponentType componentType = engine.componentManager.getType(DynamicsComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
        componentType = engine.componentManager.getType(ProjectileComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    @Override
    public void update(int entityId, float delta){
        ProjectileComponent projectileComponent = projMap.get(entityId);
        DynamicsComponent dynamics = dynMap.get(entityId);

        projectileComponent.position.set(dynamics.position);        // perhaps we don't need a copy in projectile Component?
                                                                    // but we use it for collision detection

        // rocket with a target are "heat seeking". They will follow their target.
        if(projectileComponent.target != null){
            // make rocket point towards target (instantly)
            float speed = dynamics.velocity.len();
            dynamics.velocity.set(projectileComponent.target.position).sub(projectileComponent.position).nor().scl(speed);
        }

    }
}
