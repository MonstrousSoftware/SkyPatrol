package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.IntArray;

public abstract class EntitySystem {

    protected Engine engine;
    protected final IntArray entities; // set of entities relevant for this system
    protected long requiredComponentsBitFlag = 0L;
    protected String label;     // for debug info

    public EntitySystem(Engine engine) {
        this.engine = engine;
        entities = new IntArray(false, 64);
        label = "EntitySystem";
    }

    public long requiredComponentsBitFlag(){
        return requiredComponentsBitFlag;
    }

    public String getLabel(){
        return label;
    }

    public void addEntity(int entityId){
//        System.out.println("System: add entity "+entityId);
//        System.out.println("before: "+entities.contains(entityId));
        if(entities.contains(entityId))
            throw new RuntimeException("Entity already in this system");
        entities.add(entityId);
    }

    public void removeEntity(int entityId){
//        System.out.println("System: remove entity "+entityId);
//        System.out.println("before: "+entities.contains(entityId));
        boolean done = entities.removeValue(entityId);
        if(!done)
            System.out.println("removeEntity: failed for "+entityId);
//        System.out.println("after: "+done+" "+entities.contains(entityId));
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
