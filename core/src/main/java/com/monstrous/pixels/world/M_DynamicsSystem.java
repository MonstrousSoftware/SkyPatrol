package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.*;

import java.util.Collection;
import java.util.Map;

public class M_DynamicsSystem extends EntitySystem {

    private final ComponentMapper<DynamicsComponent> dynMap;
    private final ComponentMapper<AgeComponent> ageMap;

    public M_DynamicsSystem(Engine engine) {
        super(engine);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
        ageMap = engine.componentManager.getComponentMapper(AgeComponent.class);

        ComponentType componentType = engine.componentManager.getType(DynamicsComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    public void update(float delta){
        for(Entity e : entities){
            DynamicsComponent dynComponent = dynMap.get(e.id);
            if (dynComponent.velocity.len2() > 0f) {
                Vector3 tmpVec = new Vector3();
                tmpVec.set(dynComponent.velocity).scl(delta);
                dynComponent.position.add(tmpVec);
            }
            if (dynComponent.turnSpeed > 0)
                dynComponent.velocity.rotate(Vector3.Y, delta * dynComponent.turnSpeed);

            if (dynComponent.gravity > 0)
                dynComponent.velocity.y -= delta * dynComponent.gravity;

            // kill any entities that go below ground level (e.g. debris or enemy rockets)
            if(dynComponent.position.y < 0) {
                AgeComponent ageComponent = ageMap.get(e.id);
                if(ageComponent != null)
                    ageComponent.isDead = true;
            }
        }
    }
}
