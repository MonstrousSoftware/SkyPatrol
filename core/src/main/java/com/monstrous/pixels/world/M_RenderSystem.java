package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.pixels.world.ECS.*;
import com.monstrous.pixels.world.ECS.System;

public class M_RenderSystem extends System {

    private final ComponentMapper<RenderComponent> renderMap;
    private final ComponentMapper<DynamicsComponent> dynMap;
    private final ComponentMapper<SpinComponent> spinMap;

    public M_RenderSystem(Engine engine) {
        super(engine);
        renderMap = engine.componentManager.getComponentMapper(RenderComponent.class);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
        spinMap = engine.componentManager.getComponentMapper(SpinComponent.class);

        ComponentType componentType = engine.componentManager.getType(RenderComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    private final Vector3 pos = new Vector3();
    private final Vector3 dir = new Vector3();

    public void update(Array<ModelInstance> instances) {
        for(Entity e : entities){
            RenderComponent renderComponent = renderMap.get(e.id);

            // update model instance transform if there is a dynamics component
            DynamicsComponent dynamics = dynMap.get(e.id);
            if(dynamics != null) {

                renderComponent.modelInstance.transform.idt();
                renderComponent.modelInstance.transform.trn(dynamics.position);
                // point the model in the direction of travel (can be overruled by spin later)
                dir.set(dynamics.velocity).nor();
                renderComponent.modelInstance.transform.rotate(Vector3.Z, dir);
            }

            // add any spin to model instance transform
            SpinComponent spin = spinMap.get(e.id);
            if(spin != null) {
                renderComponent.modelInstance.transform.getTranslation(pos);
                renderComponent.modelInstance.transform.setToTranslation(pos);
                renderComponent.modelInstance.transform.rotate(Vector3.Z, spin.forward);
            }

            instances.add(renderComponent.modelInstance);
        }
    }

}
