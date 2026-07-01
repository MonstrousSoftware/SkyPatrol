package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Engine {

    public EntityManager entityManager;
    public ComponentManager componentManager;
    public Array<System> systems;

    public Engine() {
        entityManager = new EntityManager( 1024);
        componentManager = new ComponentManager();
        systems = new Array<>();
    }

    public void addSystem(System system){
        systems.add(system);
    }

    public void removeSystem(System system){
        systems.removeValue(system, true);
    }


    public Entity createEntity(){
        return entityManager.createEntity();    // at this point components are not defined yet

    }

    /** push entity to the systems, always call this after createEntity and related addComponents
     * (would be nice to avoid this) */
    public void commit(int entityId){
        long flags = componentManager.flags.get(entityId);
        Entity e = entityManager.get(entityId);
        for(System system : systems){
            if((system.requiredComponentsBitFlag & flags) == system.requiredComponentsBitFlag) {
                system.addEntity(e);
                Gdx.app.log("", "add entity "+e.id+ " to system "+system.toString());
            }
        }
    }

    public void removeEntity(int entityId ){
        entityManager.removeEntity(entityId);
        for(System system : systems){
            system.removeEntity(entityId);
        }
    }

    public void clear(){
        Gdx.app.log("", "clear ");
        entityManager.clear();
        for(System system : systems){
            system.clear();
        }
        componentManager.clear();
    }

    public <C extends Component> void addComponent(int entityId, Class<C> clazz, C component){
        componentManager.addComponent(entityId, clazz, component);
    }


}
