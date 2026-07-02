package com.monstrous.pixels.world.ECS;

public abstract class EntitySystem {

    protected Engine engine;
    protected final Bag<Entity> entities; // set of entities relevant for this system
    protected long requiredComponentsBitFlag = 0L;

    public EntitySystem(Engine engine) {
        this.engine = engine;
        entities = new Bag<>();
    }

    public long requiredComponentsBitFlag(){
        return requiredComponentsBitFlag;
    }


    public void addEntity(Entity e){
        entities.add(e);
    }

    // note: the bag of entities is not indexed by e.id but only contains the relevant entities so we can iterate quickly
    // this means removal is a bit slower.
    public void removeEntity(Entity e){
        entities.remove(e);
    }

    public void clear(){
        entities.clear();
    }

    public void update(float deltaTime){
        for(Entity e : entities){
            update(e.id, deltaTime);
        }
    }

    public void update(int entityId, float deltaTime){

    }


}
