package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.monstrous.pixels.world.ECS.*;

public class M_ColliderSystem extends EntitySystem {

    private final ComponentMapper<ColliderComponent> colliderMap;
    private final ComponentMapper<DynamicsComponent> dynMap;

    public M_ColliderSystem(Engine engine) {
        super(engine);
        colliderMap = engine.componentManager.getComponentMapper(ColliderComponent.class);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);

        ComponentType componentType = engine.componentManager.getType(ColliderComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
    }

    @Override
    public void update(int entityId, float delta){

        DynamicsComponent dynamics = dynMap.get(entityId);
        if(dynamics != null) {
            ColliderComponent colliderComponent = colliderMap.get(entityId);
            colliderComponent.position.set(dynamics.position);
        }
    }

    private final Vector3 intersection = new Vector3();

    public ColliderComponent intersect(Ray ray){
        for(int index = 0; index < entities.size; index++){
            int eid = entities.get(index);
            ColliderComponent colliderComponent = colliderMap.get(eid);
            if (Intersector.intersectRaySphere(ray, colliderComponent.position, colliderComponent.radius, intersection)) {
                return colliderComponent;
            }
        }
        return null;
    }

    public ColliderComponent intersectPoint(Vector3 centre){
        for(int index = 0; index < entities.size; index++){
            int eid = entities.get(index);
            ColliderComponent colliderComponent = colliderMap.get(eid);
            if (centre.dst(colliderComponent.position) < colliderComponent.radius) {
                return colliderComponent;
            }
        }
        return null;
    }

}
