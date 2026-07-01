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

    public <C extends Component> ComponentMapper<C> getComponentMapper( Class<C> clazz){
        ComponentType type = getType(clazz);    // get component type

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

    // we need a class parameter in order to select the right component mapper.
    // perhaps it can be determined from the component object...
    public <C extends Component> void addComponent(int entityId, Class<C> clazz, C component){
        ComponentMapper<C> mapper = getComponentMapper(clazz);

        // add component for the entity
        mapper.set(entityId, component);

        ComponentType type = getType(clazz);    // get component type
        Long flagObj = flags.get(entityId);
        long flag = 0L;
        if(flagObj != null)
            flag = flagObj;
        flag |= 1L << type.getIndex();
        flags.set(entityId, flag);
    }

    public void clear(){
         for(ComponentMapper mapper : mappers)
            mapper.clear();
         flags.clear();
    }

    public void remove(int entityId){
        for(ComponentMapper mapper : mappers)
            mapper.remove(entityId);
    }

}
