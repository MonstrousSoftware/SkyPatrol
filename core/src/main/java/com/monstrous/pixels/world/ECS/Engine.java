package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Engine {

    public EntityManager entityManager;
    public ComponentManager componentManager;
    public Array<EntitySystem> systems;
    public Array<EntitySystem> updateSystems;   // systems which are automatically updated

    public Engine() {
        entityManager = new EntityManager( 1024);
        componentManager = new ComponentManager();
        systems = new Array<>();
        updateSystems = new Array<>();
    }

    public void addSystem(EntitySystem system, boolean autoUpdate){
        systems.add(system);
        if(autoUpdate)
            updateSystems.add(system);
    }

    public void removeSystem(EntitySystem system){
        systems.removeValue(system, true);
        updateSystems.removeValue(system, true);
    }

    public void update(float deltaTime){
        for(EntitySystem system : updateSystems) {
            system.update(deltaTime);
        }
    }


    public Entity createEntity(){
        return entityManager.createEntity();    // at this point components are not defined yet

    }

    /** push entity to the systems, always call this after createEntity and related addComponents
     * (would be nice to avoid this) */
    public void commit(int entityId){
        long flags = componentManager.flags.get(entityId);
        Entity e = entityManager.get(entityId);
        for(EntitySystem system : systems){
            if((system.requiredComponentsBitFlag & flags) == system.requiredComponentsBitFlag) {
                system.addEntity(e);
                Gdx.app.log("", "add entity "+e.id+ " to system "+system.toString());
            }
        }
    }

    public void removeEntity(int entityId ){
        Entity e = entityManager.get(entityId);
        entityManager.removeEntity(entityId);
        for(EntitySystem system : systems){
            system.removeEntity(e);
        }
        //componentManager.remove(entityId);
    }

    public void clear(){
        Gdx.app.log("", "clear ");
        entityManager.clear();
        for(EntitySystem system : systems){
            system.clear();
        }
        componentManager.clear();
    }

//    public <C extends Component> void addComponent(int entityId, Class<C> clazz, C component){
//        componentManager.addComponent(entityId, clazz, component);
//    }

    public <C extends Component> void addComponent(int entityId, C component){
        componentManager.addComponent(entityId, component);
    }


}
