package com.monstrous.pixels.world;

import com.monstrous.pixels.world.ECS.*;

import java.util.Collection;

public class M_AgeSystem extends EntitySystem {

    private final ComponentMapper<AgeComponent> ageMap;

    public M_AgeSystem(Engine engine) {
        super(engine);
        ageMap = engine.componentManager.getComponentMapper(AgeComponent.class);
        ComponentType componentType = engine.componentManager.getType(AgeComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    @Override
    public void update(int entityId, float delta){
        AgeComponent ageComponent = ageMap.get(entityId);
        ageComponent.timeToLive -= delta;
        if(ageComponent.timeToLive < 0) {
            ageComponent.isDead = true;
            System.out.println("Entity dies of old age " + entityId);
        }
        if(ageComponent.isDead && ageComponent.partner != null) {
            ageComponent.partner.isDead = true;
        }
        if(ageComponent.isDead){
            engine.removeEntity(entityId);
        }

    }

}
