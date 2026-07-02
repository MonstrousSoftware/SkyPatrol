package com.monstrous.pixels.world.ECS;

import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    Map<Class<? extends Component>, ComponentType> componentTypes;
    Bag<ComponentMapper<? extends Component>> mappers;
    Bag<Long> flags;

    public ComponentManager() {
        componentTypes = new HashMap<>();
        mappers = new Bag<>();
        flags = new Bag<>();
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

        Long flagObj = flags.get(entityId);
        long flag = 0L;
        if(flagObj != null)
            flag = flagObj;
        flag |= 1L << type.getIndex();
        flags.set(entityId, flag);
    }

    public void clear(){
         for(ComponentMapper<? extends Component> mapper : mappers)
            mapper.clear();
         flags.clear();
    }

    public void remove(int entityId){
        for(ComponentMapper<? extends Component> mapper : mappers)
            mapper.remove(entityId);
    }

}
