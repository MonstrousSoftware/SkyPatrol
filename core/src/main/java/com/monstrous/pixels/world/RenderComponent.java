package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class RenderComponent {
    public final int id;
    public final ModelInstance modelInstance;

    public RenderComponent(int id, ModelInstance modelInstance) {
        this.id = id;
        this.modelInstance = modelInstance;
    }
}
