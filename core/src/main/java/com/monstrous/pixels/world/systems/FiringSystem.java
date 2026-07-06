package com.monstrous.pixels.world.systems;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.ComponentMapper;
import com.monstrous.pixels.world.ECS.ComponentType;
import com.monstrous.pixels.world.ECS.Engine;
import com.monstrous.pixels.world.ECS.EntitySystem;
import com.monstrous.pixels.world.World;
import com.monstrous.pixels.world.components.DynamicsComponent;
import com.monstrous.pixels.world.components.FiringComponent;
import com.monstrous.pixels.world.components.SpinComponent;

// Let enemies fire rockets
public class FiringSystem extends EntitySystem {

    private final ComponentMapper<FiringComponent> firingMap;
    private final ComponentMapper<DynamicsComponent> dynMap;
    private final ComponentMapper<SpinComponent> spinMap;
    private final Vector3 tmpVec, tmpVec2;
    private final World world;

    public FiringSystem(Engine engine, World world) {
        super(engine);
        this.world = world;
        firingMap = engine.componentManager.getComponentMapper(FiringComponent.class);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
        spinMap = engine.componentManager.getComponentMapper(SpinComponent.class);

        ComponentType componentType = engine.componentManager.getType(FiringComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();
        componentType = engine.componentManager.getType(DynamicsComponent.class);
        requiredComponentsBitFlag |= 1L << componentType.getIndex();

        tmpVec = new Vector3();
        tmpVec2 = new Vector3();
        label = "FiringSystem";
    }

    @Override
    public void update(int entityId, float delta){

        FiringComponent firingComponent = firingMap.get(entityId);
        if(firingComponent == null) {
            boolean live = engine.entityManager.isAlive(entityId);
            throw new RuntimeException("Mandatory component missing");
        }

        firingComponent.timeToFire -= delta;    // countdown to next shot

        if(firingComponent.type == world.tankTurretType) {
            // make turret point towards camera (instantly)
            SpinComponent spin = spinMap.get(entityId);
            DynamicsComponent dynComponent = dynMap.get(entityId);
            spin.forward.set(world.cameraPosition).sub(dynComponent.position).scl(1, 0, 1).nor();

            if (firingComponent.timeToFire < 0) {
                firingComponent.timeToFire = (float) Math.random() * 10f;

                dynComponent = dynMap.get(entityId);
                tmpVec2.set(spin.forward);
                tmpVec2.y += 0.2f;  // shoot slightly up
                tmpVec2.nor();
                tmpVec2.scl(world.rocketType.speed);

                world.addEnemyRocket(tmpVec.set(dynComponent.position).add(new Vector3(0, 1.5f, 0)), tmpVec2);
            }
        } else { // jet
            if (firingComponent.timeToFire < 0) {

                // if jet is heading more or less towards the player (dot product is close to 1)
                // and cool down period is expired, then fire a rocket
                DynamicsComponent dyn = dynMap.get(entityId);

                // work out unit vector to player in the horizontal plane
                tmpVec2.set(world.cameraPosition).sub(dyn.position).scl(1, 0, 1).nor();
                tmpVec.set(dyn.velocity).nor();  // normalized velocity
                if( tmpVec.dot(tmpVec2) > 0.9f) {     // jet is heading more or less towards player
                    firingComponent.timeToFire = (float) Math.random() * 3f;
                    tmpVec2.scl(world.rocketType.speed);
                    world.addEnemyRocket(tmpVec.set(dyn.position).add(new Vector3(0, -1.5f, 0)), tmpVec2);
                }
            }
        }
    }
}
