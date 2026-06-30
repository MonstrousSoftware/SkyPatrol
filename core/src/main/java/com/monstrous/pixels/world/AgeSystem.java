package com.monstrous.pixels.world;

import java.util.Collection;

public class AgeSystem {

    public static void update(Collection<AgeComponent> components, float delta){
        for(AgeComponent component : components){
            component.timeToLive -= delta;
            if(component.timeToLive < 0)
                component.isDead = true;
            if(component.isDead && component.partner != null)
                component.partner.isDead = true;
        }
    }

}
