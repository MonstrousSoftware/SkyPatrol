package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    Map<Class<? extends Component>, ComponentType> componentTypes;  // map type to ComponentType
    Bag<ComponentMapper<? extends Component>> mappers;      // mapper per type, indexed by ComponentType.index
    Array<Long> flags;    // component bit flag per entity, indexed by entityId, using a Long caps the max nr of components

    public ComponentManager() {
        componentTypes = new HashMap<>();
        mappers = new Bag<>();
        flags = new Array<>(1024);
    }


    public <C extends Component> ComponentType getType(Class<C> type){
        ComponentType componentType = componentTypes.get(type);
        if(componentType == null){
            componentType = new ComponentType(type, componentTypes.size());
            componentTypes.put(type, componentType);
        }
        return componentType;
    }

    public <C extends Component> ComponentMapper<C> getComponentMapper( Class<C> clazz ){
        ComponentType type = getType(clazz);    // get component type
        return getComponentMapper(type);
    }

    public <C extends Component> ComponentMapper<C> getComponentMapper( ComponentType type ){
        // look for the relevant component mapper
        @SuppressWarnings("unchecked")
        ComponentMapper<C> mapper = (ComponentMapper<C>) mappers.get(type.getIndex());
        if(mapper == null){
            // create mapper if needed
            mapper = new ComponentMapper<C>();
            mappers.set(type.getIndex(), mapper);
        }
        return mapper;
    }

    // this is not pooling friendly as the component is allocated by the caller
    // alternative: createComponent and then fill it.
    public void addComponent(int entityId, Component component){
        ComponentType type = getType(component.getClass());
        ComponentMapper mapper = getComponentMapper(type);

        // add component for the entity
        mapper.set(entityId, component);


        //    flags.ensureCapacity(entityId+100);
        while(entityId >= flags.size)    // hmm...inefficient
            flags.add(0L);
        long flag = flags.get(entityId);
        flag |= 1L << type.getIndex();
        flags.set(entityId, flag);
    }

    public <C extends Component> C createComponent(int entityId, Class<C> clazz) {
        ComponentType type = getType(clazz);
        ComponentMapper mapper = getComponentMapper(type);

        // add component for the entity
        C c = (C) mapper.create(entityId, clazz);

        //    flags.ensureCapacity(entityId+100);
        while(entityId >= flags.size)    // hmm...inefficient
            flags.add(0L);
        long flag = flags.get(entityId);
        flag |= 1L << type.getIndex();
        flags.set(entityId, flag);
        return c;
    }

//    public <C extends Component> C createComponent(int entityId, ComponentType type) {
//        ComponentMapper mapper = getComponentMapper(type);
//
//        // add component for the entity
//        C c = (C) mapper.create(entityId);
//
//        //    flags.ensureCapacity(entityId+100);
//        while(entityId >= flags.size)    // hmm...inefficient
//            flags.add(0L);
//        long flag = flags.get(entityId);
//        flag |= 1L << type.getIndex();
//        flags.set(entityId, flag);
//        return c;
//    }

    public void clear(){
         for(ComponentMapper<? extends Component> mapper : mappers)
            mapper.clear();
         for(int i = 0; i < flags.size; i++)
             flags.set(i, 0L);
    }

    public void remove(int entityId){
        for(ComponentMapper<? extends Component> mapper : mappers) {
            if(mapper.has(entityId))
                mapper.remove(entityId);
        }
        flags.set(entityId, 0L);
    }

}
