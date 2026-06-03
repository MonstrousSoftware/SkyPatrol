package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.WireFramerBuilder;
import com.monstrous.pixels.filters.PostProcessor;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.World;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class StartScreen  extends RetroScreen {

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    //public PixelPerfectViewport viewport;
    //public FrameBuffer fbo;
    public SpriteBatch batch;
    //public SpriteBatch batch2;
    //public PostProcessor postProcessor;
    //public BitmapFont font;
    public Color background;
    //private int savedWidth, savedHeight;
    //private int mulW, mulH;
    //private SceneManager sceneManager;
    private Beep beep;
    //private Main game;

    private boolean enableMusic = false;


    public StartScreen(Main game) {
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


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/jet.gltf"));

        // turn model into a wireframe model
        Model model = WireFramerBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"));


        instance = new ModelInstance(model);

        inputController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);

        beep = new Beep();
        beep.beep();
    }

    @Override
    public void renderFrame(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.setScreen(new GameScreen(game));
            return;
        }

        inputController.update();
        float delta = Gdx.graphics.getDeltaTime();
        instance.transform.rotate(Vector3.Y, 20f*delta);

        ScreenUtils.clear(background, true);

        modelBatch.begin(cam);
        modelBatch.render(instance);
        modelBatch.end();

        batch.begin();
        font.draw(batch, "SKY PATROL", 10, 30);
        font.draw(batch, "MONSTROUS SOFTWARE (C) 1980", 10, 20);
        font.draw(batch, "PRESS 1 TO START", 32, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
}
