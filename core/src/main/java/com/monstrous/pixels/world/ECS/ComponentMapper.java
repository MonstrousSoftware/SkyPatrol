package com.monstrous.pixels.world.ECS;

public class ComponentMapper<A extends Component> {
    public Bag<A> components;       // sparse, match up with entity id

    public ComponentMapper() {
        components = new Bag<>();
    }

    public A get(int entityId){
        return components.get(entityId);
    }

    public boolean has(int entityId){
        return components.get(entityId) != null;
    }

    public void set(int entityId, A t){
        components.set(entityId, t);
    }

    public void remove(int entityId){
        components.remove(entityId);    // todo should use pool
    }

    public void clear(){
        components.clear();
    }
}
