package com.monstrous.pixels.world;

import com.monstrous.pixels.world.ECS.*;
import com.monstrous.pixels.world.ECS.EntitySystem;

public class M_SpinSystem extends EntitySystem {

    private final ComponentMapper<SpinComponent> spinMap;

    public M_SpinSystem(Engine engine) {
        super(engine);
        spinMap = engine.componentManager.getComponentMapper(SpinComponent.class);
        ComponentType componentType = engine.componentManager.getType(SpinComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    @Override
    public void update(int entityId, float delta){
            SpinComponent spinComponent = spinMap.get(entityId);
            spinComponent.forward.rotate(spinComponent.spinAxis, delta * spinComponent.spinSpeed);

    }
}
