package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

import java.util.Collection;

public class SpinSystem {

    public static void update(Collection<SpinComponent> components, float delta){
        for(SpinComponent component : components){
            component.forward.rotate(component.spinAxis, delta * component.spinSpeed);
        }
    }

    // todo we need instance.transform and position
    public void update(SpinComponent component, float delta) {
        if(component.spinSpeed > 0)
            component.forward.rotate(component.spinAxis, delta * component.spinSpeed);

        // todo
//        else
//            component.forward.set(velocity).nor();
    }
}
