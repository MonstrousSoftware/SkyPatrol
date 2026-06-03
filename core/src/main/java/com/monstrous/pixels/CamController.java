package com.monstrous.pixels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {

    public Camera camera;
    public float rotateAngle = 90f;
    public float translateUnits = 10f;
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
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public CamController(Camera camera) {
        this.camera = camera;
    }

    public void update(){
        final float delta = Gdx.graphics.getDeltaTime();
        tmpV1.set(camera.direction).scl(delta * translateUnits);
        tmpV1.y = 0;
        camera.translate(tmpV1);
        if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {

            //camera.up.set(Vector3.Y);
            if (rotateRightPressed) camera.rotate(camera.up, -delta * rotateAngle);
            if (rotateLeftPressed) camera.rotate(camera.up, delta * rotateAngle);
            if (forwardPressed) {
                tmpV1.set(camera.up).crs(camera.direction);
                camera.rotate(tmpV1, -delta * rotateAngle);
            }
            if (backwardPressed) {
                tmpV1.set(camera.up).crs(camera.direction);
                camera.rotate(tmpV1, delta * rotateAngle);
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
