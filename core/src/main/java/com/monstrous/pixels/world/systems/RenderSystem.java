package com.monstrous.pixels.world.systems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.pixels.world.ECS.*;
import com.monstrous.pixels.world.ECS.EntitySystem;
import com.monstrous.pixels.world.components.DynamicsComponent;
import com.monstrous.pixels.world.components.RenderComponent;
import com.monstrous.pixels.world.components.SpinComponent;

public class RenderSystem extends EntitySystem {
    private final ComponentMapper<RenderComponent> renderMap;
    private final ComponentMapper<DynamicsComponent> dynMap;
    private final ComponentMapper<SpinComponent> spinMap;

    public RenderSystem(Engine engine) {
        super(engine);
        renderMap = engine.componentManager.getComponentMapper(RenderComponent.class);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
        spinMap = engine.componentManager.getComponentMapper(SpinComponent.class);

        ComponentType componentType = engine.componentManager.getType(RenderComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
        label = "RenderSystem";
    }


    private final Vector3 pos = new Vector3();
    private final Vector3 dir = new Vector3();

    public void update(Camera camera, Array<ModelInstance> instances) {
        for(int index = 0; index < entities.size; index++){
            int eid = entities.get(index);
            RenderComponent renderComponent = renderMap.get(eid);
            if(renderComponent == null) {
                boolean live = engine.entityManager.isAlive(eid);
                throw new RuntimeException("Mandatory component missing");
            }

            // update model instance transform if there is a dynamics component
            if(dynMap.has(eid)){
                DynamicsComponent dynamics = dynMap.get(eid);

                renderComponent.modelInstance.transform.idt();
                renderComponent.modelInstance.transform.trn(dynamics.position);
                // point the model in the direction of travel (can be overruled by spin later)
                dir.set(dynamics.velocity).nor();
                renderComponent.modelInstance.transform.rotate(Vector3.Z, dir);
                //renderComponent.modelInstance.transform.rotateTowardDirection(dir, Vector3.Y);    // faster?
            }

            // add any spin to model instance transform
            if(spinMap.has(eid)){
                SpinComponent spin = spinMap.get(eid);

                renderComponent.modelInstance.transform.getTranslation(pos);
                renderComponent.modelInstance.transform.setToTranslation(pos);
                renderComponent.modelInstance.transform.rotate(Vector3.Z, spin.forward);
                //renderComponent.modelInstance.transform.rotateTowardDirection(spin.forward, Vector3.Y);
            }
            // frustum culling
            // (increases frame rate about 4 times)
            renderComponent.modelInstance.transform.getTranslation(pos);
            if(camera.frustum.sphereInFrustum(pos, renderComponent.radius))
                instances.add(renderComponent.modelInstance);
        }
    }

}
