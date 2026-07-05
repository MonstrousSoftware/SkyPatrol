package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.utils.reflect.ClassReflection.newInstance;

public class ComponentMapper<A extends Component> {
    public Array<A> components;       // sparse, match up with entity id

    public ComponentMapper() {
        components = new Array<>();
    }

    public A get(int entityId){
        if(entityId >= components.size)
            return null;
        return components.get(entityId);
    }

//    public A create(int entityId){
//        A c = components.get(entityId);
//        if(c != null)
//            return c;
//        c = newInstance(c);
//    }

    public boolean has(int entityId){
        if(entityId >= components.size)
            return false;
        return components.get(entityId) != null;
    }

    public void set(int entityId, A t){
        if(entityId >= components.size)
            components.setSize(entityId+100);
        components.set(entityId, t);
    }

//    public void set(int entityId, Component t){
//        components.set(entityId, (A)t);
//    }

    public void remove(int entityId){
        if(entityId >= components.size)
            return;
        components.set(entityId, null);    // todo should use pool
    }

    public void clear(){
        components.clear();
    }
}
