package com.monstrous.pixels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {

    public Camera camera;
    public float rotateSpeed = 90f;
    public float forwardSpeed = 10f;
    public int forwardKey = Input.Keys.UP;
    public int backwardKey = Input.Keys.DOWN;
    public int rotateRightKey = Input.Keys.RIGHT;
    public int rotateLeftKey = Input.Keys.LEFT;
    public boolean autoUpdate = true;

    protected boolean forwardPressed;
    protected boolean backwardPressed;
    protected boolean rotateRightPressed;
    protected boolean rotateLeftPressed;
    protected boolean controlsInverted;
    private float rollAngle = 0;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public CamController(Camera camera) {
        this.camera = camera;
    }

    public void update(){
        final float delta = Gdx.graphics.getDeltaTime();
        tmpV1.set(camera.direction).scl(delta * forwardSpeed);
        tmpV1.y = 0;
        camera.translate(tmpV1);


        if(!rotateRightPressed &&  !rotateLeftPressed) {
            camera.rotate(camera.direction, -20f*delta*Math.signum(rollAngle));
            rollAngle -= 20f* delta * Math.signum(rollAngle);
        }
        // pressing right and left together forces roll to zero (may be needed after a looping)
        if(rotateRightPressed &&  rotateLeftPressed) {
            camera.up.set(Vector3.Y);
            rollAngle = 0;
        }
        //Gdx.app.log("roll angle", ""+rollAngle);

        if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {


            if (rotateRightPressed && rollAngle < 40f) {
                rollAngle += 40f*delta;
                camera.rotate(camera.direction, delta*40f);
            }
            if (rotateLeftPressed && rollAngle > -40f) {
                rollAngle -= 40f*delta;
                camera.rotate(camera.direction, -delta*40f);
            }





            //camera.up.set(Vector3.Y);
            // yaw
            if (rotateRightPressed) camera.rotate(Vector3.Y, -delta * rollAngle);
            if (rotateLeftPressed ) camera.rotate(Vector3.Y, -delta * rollAngle);

            // tilt camera up/down (pitch)
            if (forwardPressed) {
                tmpV1.set(camera.up).crs(camera.direction);
                camera.rotate(tmpV1, -delta * rotateSpeed);
            }
            if (backwardPressed) {
                tmpV1.set(camera.up).crs(camera.direction);
                camera.rotate(tmpV1, delta * rotateSpeed);
            }

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
        return false;
    }
}
