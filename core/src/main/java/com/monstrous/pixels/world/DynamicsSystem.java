package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

import java.util.Collection;
import java.util.Map;

public class DynamicsSystem {


    public static void update(Collection<DynamicsComponent> components, Map<Integer, AgeComponent> ageComponentMap, float delta){
        for(DynamicsComponent component : components){
            update(component, ageComponentMap, delta);
        }
    }
    public static void update(DynamicsComponent component, Map<Integer, AgeComponent> ageComponentMap, float delta) {
        if (component.velocity.len2() > 0f) {
            Vector3 tmpVec = new Vector3();
            tmpVec.set(component.velocity).scl(delta);
            component.position.add(tmpVec);
        }
        if (component.turnSpeed > 0)
            component.velocity.rotate(Vector3.Y, delta * component.turnSpeed);

        if (component.gravity > 0)
            component.velocity.y -= delta * component.gravity;

        if(component.position.y < 0) {
            ageComponentMap.get(component.id).isDead = true;
        }
    }
}
