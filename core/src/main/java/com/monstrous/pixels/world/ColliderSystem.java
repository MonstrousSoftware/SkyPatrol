package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ColliderSystem {

    public static void update(ColliderComponent component, DynamicsComponent dynamics) {
        component.position.set(dynamics.position);
    }
}
