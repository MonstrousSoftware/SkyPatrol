package com.monstrous.pixels.world.ECS;

public abstract class System {

    protected Engine engine;
    protected final Bag<Entity> entities; // set of entities relevant for this system
    protected long requiredComponentsBitFlag = 0L;

    public System(Engine engine) {
        this.engine = engine;
        entities = new Bag<>();
    }

    public long requiredComponentsBitFlag(){
        return requiredComponentsBitFlag;
    }


    public void addEntity(Entity e){
        entities.add(e);
    }

    public void removeEntity(int id){
        entities.remove(id);
    }

    public void clear(){
        entities.clear();
    }

//    public void process();
//        for(Entity e : entities){
//            do something
//        }


}
