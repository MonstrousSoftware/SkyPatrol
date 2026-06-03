package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.World;


public class GameScreen extends RetroScreen {
    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Color background;
    private Beep beep;
    private World world;
    private float time;
    private boolean enableMusic = false;

    public GameScreen(Main game) {
        super(game);
    }


    @Override
    public void show() {
        super.show();
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, LOWRES_WIDTH, LOWRES_HEIGHT);
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 150f;
        cam.update();

        world = new World();

        inputController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f); // greenish

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

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

        world.update(0.1f);

        // render frame
        ScreenUtils.clear(background, true);

        modelBatch.begin(cam);
        modelBatch.render(world.getInstances());
        modelBatch.end();

        drawReticule(time > 2);

        batch.begin();
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

    @Override
    public void dispose() {
        world.dispose();
        modelBatch.dispose();
        model.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }


}
