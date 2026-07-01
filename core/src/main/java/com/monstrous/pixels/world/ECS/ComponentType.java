package com.monstrous.pixels.world.ECS;

public class ComponentType {

    private final Class<? extends Component> type;
    private final int index;

    public ComponentType(Class<? extends Component> type, int index) {
        this.type = type;
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public Class<? extends Component> getType(){
        return type;
    }
}
