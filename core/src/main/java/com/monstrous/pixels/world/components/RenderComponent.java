package com.monstrous.pixels.world.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.monstrous.pixels.world.ECS.Component;

public class RenderComponent extends Component {
    public ModelInstance modelInstance;
    public float radius;

    public RenderComponent() {
        modelInstance = null;
    }

    public RenderComponent(ModelInstance modelInstance) {
        set(modelInstance);
    }

    public void set(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        this.radius = modelInstance.model.nodes.get(0).parts.get(0).meshPart.radius;
    }
}
