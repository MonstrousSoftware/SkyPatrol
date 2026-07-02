package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.IntArray;

public abstract class EntitySystem {

    protected Engine engine;
    protected final IntArray entities; // set of entities relevant for this system
    protected long requiredComponentsBitFlag = 0L;

    public EntitySystem(Engine engine) {
        this.engine = engine;
        entities = new IntArray();
    }

    public long requiredComponentsBitFlag(){
        return requiredComponentsBitFlag;
    }


    public void addEntity(int entityId){
        entities.add(entityId);
    }

    public void removeEntity(int entityId){
        entities.removeValue(entityId);
    }

    public void clear(){
        entities.clear();
    }

    public void update(float deltaTime){
        for(int i = 0; i < entities.size; i++){
            update(entities.get(i), deltaTime);
        }
    }

    public void update(int entityId, float deltaTime){

    }

    // don't modify this array!
    public IntArray getEntities(){
        return entities;
    }

    public int numEntities(){
        return entities.size;
    }


}
