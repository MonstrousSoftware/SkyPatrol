package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.WireFrameBuilder;
import com.monstrous.pixels.WireFrameShader;
import com.monstrous.pixels.sound.Beep;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;


public class StartScreen  extends RetroScreen {

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public SpriteBatch batch;
    public Color background;
    private Beep beep;

    public StartScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        cam = new PerspectiveCamera(67, LOWRES_WIDTH, LOWRES_HEIGHT);
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 150f;
        cam.update();


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        Model model = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Helicopter"), Color.CYAN);


        instance = new ModelInstance(model);

        Renderable renderable = new Renderable();
        instance.getRenderable(renderable);
        WireFrameShader wireFrameShader = new WireFrameShader(renderable);
        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(final Renderable renderable) {
                return wireFrameShader;
            }
        });

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            game.setScreen(new InstructionsScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            game.setScreen(new HiScoreScreen(game));
            return;
        }

        inputController.update();
        float delta = Gdx.graphics.getDeltaTime();
        instance.transform.rotate(Vector3.Y, 20f*delta);

        ScreenUtils.clear(Color.BLACK, true);

        modelBatch.begin(cam);
        modelBatch.render(instance);
        modelBatch.end();

        batch.begin();
        font.draw(batch, "SKY PATROL", 100, 220);
        font.draw(batch, "1 TO START", 100, 50);
        font.draw(batch, "2 FOR INSTRUCTIONS", 100, 40);
        font.draw(batch, "3 FOR HI SCORES", 100, 30);
        font.draw(batch, "MONSTROUS SOFTWARE (C) 1980", 40, 10);

        batch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
}
