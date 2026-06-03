package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class Building {
    public final Vector3 position;
    public final Vector3 direction;

    public Building(Vector3 position, Vector3 direction) {
        this.position = new Vector3(position);
        this.direction = new Vector3(direction).nor();
    }
}
