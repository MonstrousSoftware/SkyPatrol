package com.monstrous.pixels.world;

import com.monstrous.pixels.world.ECS.*;


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
        if(ageComponent.isDead && ageComponent.partner >= 0) {
            if(engine.entityManager.isAlive(ageComponent.partner)) {
                ageMap.get(ageComponent.partner).isDead = true;
                System.out.println("Mark partner for removal" + ageComponent.partner);
            }
        }
        if(ageComponent.isDead){
            System.out.println("AgeSystem: Entity that is marked dead is removed " + entityId);
            engine.removeEntity(entityId);

        }

    }

}
