package com.monstrous.pixels.world.ECS;


import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;

public class EntityManager {

    private int nextId;                     // id for next new entity
    private final IntArray pool;            // entity id's for reuse
    private final Bits alive;               // dead/alive bit map

    public EntityManager(int initialCapacity) {
        nextId = 0;
        //entities = new IntArray(initialCapacity);
        pool = new IntArray(false, 64);
        alive = new Bits(initialCapacity);
    }

    public int createEntity(){
        int eid;
        if(pool.notEmpty()){
            eid = pool.pop();
        } else {
            eid = nextId++;//entities.size;
            //ntities.add(eid);
        }
        alive.set(eid);
        return eid;
    }

    void removeEntity(int entityId ){
        if(alive.get(entityId)) {
            alive.clear(entityId);
            pool.add(entityId);
            //System.out.println("Removed entity, pool size " + pool.size);
        }
    }


    public boolean isAlive(int entityId ){
        return alive.get(entityId);
    }

    public int count(){
        return nextId - pool.size;
    }

    public int maxCount(){
        return nextId;
    }

    void clear(){
        // add live entities to the reuse pool (dead entities are already there)
        for(int id = 0; id < nextId; id++) {
            if(isAlive(id))
                pool.add(id);
        }
        alive.clear();
    }
}
