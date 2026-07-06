package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    Map<Class<? extends Component>, ComponentType> componentTypes;  // map type to ComponentType
    Array<ComponentMapper<? extends Component>> mappers;      // mapper per type, indexed by ComponentType.index
    Array<Long> flags;    // component bit flag per entity, indexed by entityId, using a Long caps the max nr of components

    public ComponentManager() {
        componentTypes = new HashMap<>();
        mappers = new Array<>(64);
        flags = new Array<>(1024);
    }


    public <C extends Component> ComponentType getType(Class<C> type){
        ComponentType componentType = componentTypes.get(type);
        if(componentType == null){
            // add a new type
            componentType = new ComponentType(type, componentTypes.size());
            componentTypes.put(type, componentType);
            // create mapper for the new type
            ComponentMapper<C> mapper = new ComponentMapper<C>();
            mappers.add(mapper);
        }
        return componentType;
    }

    public <C extends Component> ComponentMapper<C> getComponentMapper( Class<C> clazz ){
        ComponentType type = getType(clazz);    // get component type
        return getComponentMapper(type);
    }

    public <C extends Component> ComponentMapper<C> getComponentMapper( ComponentType type ){
        return (ComponentMapper<C>) mappers.get(type.getIndex());
    }

    // this is not pooling friendly as the component is allocated by the caller
    // alternative: createComponent and then fill it.
    public void addComponent(int entityId, Component component){
        ComponentType type = getType(component.getClass());
        ComponentMapper mapper = getComponentMapper(type);

        // add component for the entity
        mapper.set(entityId, component);

        setFlag(entityId, type);
    }

    /** to save on allocations, use createComponent instead of addComponent.
     * It returns a component which may be a reused one or a new one.
     * @param entityId
     * @param clazz
     * @return
     * @param <C>
     */
    public <C extends Component> C createComponent(int entityId, Class<C> clazz) {
        ComponentType type = getType(clazz);
        ComponentMapper mapper = getComponentMapper(type);

        // create component for the entity
        C c = (C) mapper.create(entityId, clazz);

        setFlag(entityId, type);
        return c;
    }

    public <C extends Component> void removeComponent(int entityId, Class<C> clazz){
        ComponentType type = getType(clazz);
        ComponentMapper mapper = getComponentMapper(type);

        // add component for the entity
        mapper.remove(entityId);

        long flag = flags.get(entityId);
        flag ^= (1L << type.getIndex());
        flags.set(entityId, flag);
    }

    private void setFlag(int entityId, ComponentType type){
        if(entityId >= flags.size)
            flags.setSize(entityId + 100);
        if(flags.get(entityId) == null)
            flags.set(entityId, 0L);
        long flag = flags.get(entityId);
        flag |= 1L << type.getIndex();
        flags.set(entityId, flag);
    }


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
