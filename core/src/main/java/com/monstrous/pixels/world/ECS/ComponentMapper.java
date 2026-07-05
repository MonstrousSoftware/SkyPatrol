package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import static com.badlogic.gdx.utils.reflect.ClassReflection.getConstructor;
import static com.badlogic.gdx.utils.reflect.ClassReflection.newInstance;

public class ComponentMapper<A extends Component> {
    public Array<A> components;       // sparse, match up with entity id
    public Array<A> pool;       // for reuse

    public ComponentMapper() {

        components = new Array<>();
        pool = new Array<>(false, 16);
    }

    public A get(int entityId){
        if(entityId >= components.size)
            return null;
        return components.get(entityId);
    }

    // obtain component for entity may be a recycled object,
    //  be sure to set all the members explicitly.
    public A create(int entityId, Class<A> type){
        if(entityId >= components.size)
            components.setSize(entityId+100);
        A component = components.get(entityId);
        if(component == null){
            if(pool.size > 0) {
                component = pool.pop();
            }
            else {
                try {
                    component = ClassReflection.newInstance(type);
                    System.out.println("New component "+type);
                } catch (ReflectionException e) {
                    throw new RuntimeException("Unable to instantiate component.", e);
                }
            }
            components.set(entityId, component);
        }
        return component;
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
        A component = components.get(entityId);
        pool.add(component);
        components.set(entityId, null);
    }

    public void clear(){
        components.clear();
    }
}
