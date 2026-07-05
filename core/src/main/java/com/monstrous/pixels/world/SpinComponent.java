package com.monstrous.pixels.world;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixels.world.ECS.Component;

public class SpinComponent extends Component {
    public final Vector3 forward;       // which way the model is pointing
    public final Vector3 spinAxis;
    public final float spinSpeed;

    public SpinComponent(Vector3 forward, Vector3 spinAxis, float spinSpeed) {
        this.forward = new Vector3(forward);
        this.spinAxis = new Vector3(spinAxis);
        this.spinSpeed = spinSpeed;
    }
}
