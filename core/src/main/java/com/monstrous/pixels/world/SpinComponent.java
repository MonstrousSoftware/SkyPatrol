package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class SpinComponent extends Component {
    public Vector3 forward;       // which way the model is pointing
    public Vector3 spinAxis;
    public float spinSpeed;

    public SpinComponent(){

    }

    public SpinComponent(Vector3 forward, Vector3 spinAxis, float spinSpeed) {
        this.forward = new Vector3(forward);
        this.spinAxis = new Vector3(spinAxis);
        this.spinSpeed = spinSpeed;
    }

    public void set(Vector3 forward, Vector3 spinAxis, float spinSpeed) {
        this.forward = new Vector3(forward);
        this.spinAxis = new Vector3(spinAxis);
        this.spinSpeed = spinSpeed;
    }
}
