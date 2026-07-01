package com.monstrous.pixels.world.ECS;


public class EntityManager {

    private final Bag<Entity> entities;
    private final Bag<Entity> pool;

    public EntityManager(int initialCapacity) {
        entities = new Bag<>(initialCapacity);
        pool = new Bag<>();
    }

    public Entity createEntity(){
        Entity e;
        if(pool.notEmpty()){
            e = pool.get(0);
            pool.remove(0);
            e.id = entities.getSize();
            System.out.println("Obtained from pool, pool size "+pool.size);
        } else {
            e = new Entity(entities.getSize());
        }
        entities.add(e);
        return e;
    }

    void removeEntity(int entityId ){
        Entity e = entities.get(entityId);
        if(e != null) {
            entities.remove(entityId);
            pool.add(e);
            System.out.println("Removed entity, pool size " + pool.size);
        }
    }

    public Entity get(int entityId ){
        return entities.get(entityId);
    }

    void clear(){
        for(Entity e : entities)
            pool.add(e);
        entities.clear();
    }
}
