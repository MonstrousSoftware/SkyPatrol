package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.CamController;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.World;


public class GameScreen extends RetroScreen {
    public PerspectiveCamera cam;
    public CamController inputController;
    //public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Color background;
    private Beep beep;
    private Sound soundLock;
    private Sound soundBoom;
    private Sound soundFire;
    private World world;
    private float time;
    private boolean enableMusic = false;
    private int score = 0;


    public GameScreen(Main game) {
        super(game);
    }


    @Override
    public void show() {
        super.show();
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, LOWRES_WIDTH, LOWRES_HEIGHT);
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0, 10, 0);
        cam.near = 0.1f;
        cam.far = 1000f;
        cam.update();

        world = new World();

        //inputController = new CameraInputController(cam);
        inputController = new CamController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f); // greenish

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);



        soundLock = Gdx.audio.newSound(Gdx.files.internal("sound/lock.wav"));
        soundBoom = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.wav"));
        soundFire = Gdx.audio.newSound(Gdx.files.internal("sound/fire.wav"));

        beep = new Beep();
        //beep.beep();

        if(enableMusic)
            beep.startMusic();

    }

    @Override
    protected void renderFrame(float deltaTime) {
        // do updates
        time += deltaTime;
        inputController.update();

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(world.fireRocket(cam))
                soundFire.play();
        }

        world.update(0.016f);//;deltaTime);

        int points = world.rocketHits();
        if(points > 0){
            soundBoom.play();
            score += points;
        }

        // render frame
        ScreenUtils.clear(background, true);

        modelBatch.begin(cam);
        modelBatch.render(world.getInstances());
        modelBatch.end();

        boolean locked = world.weaponLocked(cam);
        if(locked)
            soundLock.play();
        drawReticule(locked);
        //drawRadar();

        int mm = (int)time / 60;
        int ss = (int)time - 60*mm;

        batch.begin();
        font.draw(batch, "SCORE: ", 8, LOWRES_HEIGHT-8);
        font.draw(batch, String.format("%05d", score), 64, LOWRES_HEIGHT-8);

        font.draw(batch, String.format("%02d:%02d", mm, ss), 270, LOWRES_HEIGHT-8);
        font.draw(batch, "SKY PATROL", 32, 32);
        batch.end();
    }

    private void drawReticule(boolean locked){
        int dx = 30;
        int sdx = 15;
        int adx = locked ? 15 : 30;
        int dy = 10;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(LOWRES_WIDTH/2-adx, LOWRES_HEIGHT/2-dy, LOWRES_WIDTH/2-dx, LOWRES_HEIGHT/2-2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2+adx, LOWRES_HEIGHT/2-dy, LOWRES_WIDTH/2+dx, LOWRES_HEIGHT/2-2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2-adx, LOWRES_HEIGHT/2+dy, LOWRES_WIDTH/2-dx, LOWRES_HEIGHT/2+2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2+adx, LOWRES_HEIGHT/2+dy, LOWRES_WIDTH/2+dx, LOWRES_HEIGHT/2+2*dy);

        shapeRenderer.line(LOWRES_WIDTH/2-dx, LOWRES_HEIGHT/2-2*dy, LOWRES_WIDTH/2-sdx, LOWRES_HEIGHT/2-2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2+dx, LOWRES_HEIGHT/2-2*dy, LOWRES_WIDTH/2+sdx, LOWRES_HEIGHT/2-2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2-dx, LOWRES_HEIGHT/2+2*dy, LOWRES_WIDTH/2-sdx, LOWRES_HEIGHT/2+2*dy);
        shapeRenderer.line(LOWRES_WIDTH/2+dx, LOWRES_HEIGHT/2+2*dy, LOWRES_WIDTH/2+sdx, LOWRES_HEIGHT/2+2*dy);

        shapeRenderer.end();
    }

//    private final Vector3 tmpV = new Vector3();
//    private final Quaternion quat = new Quaternion();

//    private void drawRadar(){
//        float scale = 0.05f;
//        float cx = 3*LOWRES_WIDTH/4;
//        float cy = 3*LOWRES_HEIGHT/4;
//
//        cam.view.getRotation(quat);
//        float degrees = 180f + quat.getAngleAround(Vector3.Y);
//
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
//        shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.point(cx, cy, 0);
//        shapeRenderer.setColor(Color.BROWN);
//        for(Building t : world.buildings) {
//            tmpV.set(t.position).sub(cam.position).scl(scale).rotate(Vector3.Y, -degrees).add(cx, 0, cy);
//            shapeRenderer.point(tmpV.x, tmpV.z, 0);
//        }
//        shapeRenderer.setColor(Color.GREEN);
//        for(Tank t : world.tanks) {
//            tmpV.set(t.position).sub(cam.position).scl(scale).rotate(Vector3.Y, degrees).add(cx, 0, cy);
//            shapeRenderer.point(tmpV.x, tmpV.z, 0);
//        }
//        shapeRenderer.setColor(Color.BLUE);
//        for(Jet t : world.jets) {
//            tmpV.set(t.position).sub(cam.position).scl(scale).rotate(Vector3.Y, degrees).add(cx, 0, cy);
//            shapeRenderer.point(tmpV.x, tmpV.z, 0);
//        }
//        shapeRenderer.end();
//    }


    @Override
    public void dispose() {
        world.dispose();
        modelBatch.dispose();
        model.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }


}
