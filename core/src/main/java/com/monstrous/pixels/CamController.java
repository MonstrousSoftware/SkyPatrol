package com.monstrous.pixels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {

    public Camera camera;
    public float rotateSpeed = 90f;
    public float tiltSpeed = 30f;
    public float forwardSpeed = 10f;
    public float tiltMax = 30f;
    public final float maxRoll = 30f;
    public final float rollSpeed = 80f;
    public int forwardKey = Input.Keys.UP;
    public int backwardKey = Input.Keys.DOWN;
    public int rotateRightKey = Input.Keys.RIGHT;
    public int rotateLeftKey = Input.Keys.LEFT;
    public boolean autoUpdate = true;

    protected boolean forwardPressed;
    protected boolean backwardPressed;
    protected boolean rotateRightPressed;
    protected boolean rotateLeftPressed;
    protected boolean shiftPressed;
    private float rollAngle = 0;
    private float tiltAngle = 0;
    private final Vector3 tmpV1 = new Vector3();


    public CamController(Camera camera) {
        this.camera = camera;
    }

    public void update(){
        final float delta = Gdx.graphics.getDeltaTime();
        // get speed vector from horizontal components of camera direction
        tmpV1.set(camera.direction).scl(1,0,1).nor().scl(delta * forwardSpeed);
        camera.translate(tmpV1);    // move forward

        // roll (banking)
        float roll = 0;
        if (rotateRightPressed && rollAngle < maxRoll)
            roll = rollSpeed*delta;
        if (rotateLeftPressed && rollAngle > -maxRoll)
            roll = -rollSpeed*delta;
        if(!rotateRightPressed &&  !rotateLeftPressed)
            roll = -0.5f*rollSpeed*delta*Math.signum(rollAngle);
        rollAngle += roll;
        camera.rotate(camera.direction, roll);



        if(shiftPressed){
            // strafe
            // use a cross product of up and forward to get a left vector.  Then remove the vertical component, normalize and scale
            if (rotateRightPressed) camera.translate( tmpV1.set(camera.up).crs(camera.direction).scl(1,0,1).nor().scl(-delta*20));
            if (rotateLeftPressed)  camera.translate( tmpV1.set(camera.up).crs(camera.direction).scl(1,0,1).nor().scl(delta*20));

        } else {
            // yaw
            if (rotateRightPressed) camera.rotate(Vector3.Y, -delta * rollAngle);
            if (rotateLeftPressed) camera.rotate(Vector3.Y, -delta * rollAngle);
        }

        // tilt camera up/down (pitch) up to a max of 45 degrees
        float tilt = 0;
        if (forwardPressed && tiltAngle > -tiltMax)
            tilt = -delta * tiltSpeed;
        if (backwardPressed && tiltAngle < tiltMax)
            tilt = delta * tiltSpeed;
        if(Math.abs(tilt) > 0.1f) {
            tiltAngle += tilt;
            tmpV1.set(camera.up).crs(camera.direction);
            camera.rotate(tmpV1, tilt);
        }
        if (autoUpdate) camera.update();
    }


    @Override
    public boolean keyDown (int keycode) {
        if (keycode == forwardKey)
            forwardPressed = true;
        else if (keycode == backwardKey)
            backwardPressed = true;
        else if (keycode == rotateRightKey)
            rotateRightPressed = true;
        else if (keycode == rotateLeftKey)
            rotateLeftPressed = true;
        else if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            shiftPressed = true;
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (keycode == forwardKey)
            forwardPressed = false;
        else if (keycode == backwardKey)
            backwardPressed = false;
        else if (keycode == rotateRightKey)
            rotateRightPressed = false;
        else if (keycode == rotateLeftKey)
            rotateLeftPressed = false;
        else if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            shiftPressed = false;
        return false;
    }
}
