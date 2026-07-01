package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class RenderSystem {



    public static void update(RenderComponent component, Array<ModelInstance> instances) {
        instances.add(component.modelInstance);
    }

    public static void update(RenderComponent component, DynamicsComponent dynamics, SpinComponent spin, Array<ModelInstance> instances) {
        Vector3 pos = new Vector3();
        Vector3 dir = new Vector3();

        // update model instance transform if there is a dynamics component
        if(dynamics != null) {
            component.modelInstance.transform.idt();
            component.modelInstance.transform.trn(dynamics.position);
            // point the model in the direction of travel (can be overruled by spin later)
            dir.set(dynamics.velocity).nor();
            component.modelInstance.transform.rotate(Vector3.Z, dir);
        }

        // add any spin to model instance transform
        if(spin != null) {
            component.modelInstance.transform.getTranslation(pos);
            component.modelInstance.transform.setToTranslation(pos);
            component.modelInstance.transform.rotate(Vector3.Z, spin.forward);
        }
        instances.add(component.modelInstance);
    }
}
