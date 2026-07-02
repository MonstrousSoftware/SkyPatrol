package com.monstrous.pixels.world.ECS;


import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;

public class EntityManager {

    private final Bag<Entity> entities;     // entities are never deleted from this (do we even need it?)
    private final IntArray pool;            // entity id's for reuse
    private final Bits alive;               // dead/alive bit map

    public EntityManager(int initialCapacity) {
        entities = new Bag<>(initialCapacity);
        pool = new IntArray();
        alive = new Bits(initialCapacity);
    }

    public int createEntity(){
        Entity e;
        if(pool.notEmpty()){
            int id = pool.pop();
            e = entities.get(id);
            System.out.println("Obtained from pool, pool size "+pool.size);
        } else {
            e = new Entity(entities.getSize());
            entities.add(e);

        }
        alive.set(e.id);
        return e.id;
    }

    void removeEntity(int entityId ){
        if(alive.get(entityId)) {
            alive.clear(entityId);
            pool.add(entityId);
            System.out.println("Removed entity, pool size " + pool.size);
        }
    }


    public boolean isAlive(int entityId ){
        return alive.get(entityId);
    }

    // beware: entity could be dead, use isAlive() to check
//    public Entity get(int entityId ){
//        return entities.get(entityId);
//    }

    void clear(){
        for(Entity e : entities) {
            pool.add(e.id);
        }
        alive.clear();
    }
}
