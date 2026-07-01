package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class RenderComponent extends Component {
    public final int id;
    public final ModelInstance modelInstance;

    public RenderComponent(int id, ModelInstance modelInstance) {
        this.id = id;
        this.modelInstance = modelInstance;
    }
}
