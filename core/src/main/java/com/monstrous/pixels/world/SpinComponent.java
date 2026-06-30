package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;

public class SpinComponent {
    public final int id;
    public final Vector3 forward;       // which way the model is pointing
    public final Vector3 spinAxis;
    public final float spinSpeed;

    public SpinComponent(int id, Vector3 forward, Vector3 spinAxis, float spinSpeed) {
        this.id = id;
        this.forward = new Vector3(forward);
        this.spinAxis = new Vector3(spinAxis);
        this.spinSpeed = spinSpeed;
    }
}
