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

public class StartScreen  extends ScreenAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    //public PixelPerfectViewport viewport;
    public FrameBuffer fbo;
    public SpriteBatch batch;
    public SpriteBatch batch2;
    public PostProcessor postProcessor;
    public BitmapFont font;
    public Color background;
    private int savedWidth, savedHeight;
    //private int mulW, mulH;
    private SceneManager sceneManager;
    private Beep beep;
    private Main game;

    private boolean noCRT = true;
    private boolean enableMusic = false;


    public StartScreen(Main game) {
        this.game = game;

    }

    @Override
    public void show() {
        modelBatch = new ModelBatch();


        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 150f;
        cam.update();


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/jet.gltf"));

        // turn model into a wireframe model
        Model model = WireFramerBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"));


//        ModelBuilder modelBuilder = new ModelBuilder();
//
//        model = modelBuilder.createBox(5f, 5f, 5f, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
//            VertexAttributes.Usage.Position );
        instance = new ModelInstance(model);

        inputController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, LOWRES_WIDTH, LOWRES_HEIGHT, true);
        batch = new SpriteBatch();
        batch2 = new SpriteBatch();
        postProcessor = new PostProcessor();

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);

        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);

        //viewport = new PixelPerfectViewport(LOWRES_WIDTH, LOWRES_HEIGHT);


        beep = new Beep();
        beep.beep();

        if(enableMusic)
            beep.startMusic();

    }

    @Override
    public void render(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.setScreen(new GameScreen(game));
            return;
        }
        // F11 to toggle full screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            boolean fullScreen = Gdx.graphics.isFullscreen();
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
            if (fullScreen)
                Gdx.graphics.setWindowedMode(savedWidth, savedHeight);
            else {
                savedWidth = Gdx.graphics.getWidth();
                savedHeight = Gdx.graphics.getHeight();
                Gdx.graphics.setFullscreenMode(currentMode);
            }
        }
        inputController.update();
        float delta = Gdx.graphics.getDeltaTime();
        instance.transform.rotate(Vector3.Y, 20f*delta);


        fbo.begin();
        ScreenUtils.clear(background, true);

        modelBatch.begin(cam);
        modelBatch.render(instance);
        //modelBatch.render(world.getInstances());
        modelBatch.end();

        batch.begin();
        font.draw(batch, "SKY PATROL", 100, 300);
        font.draw(batch, "PRESS 1 TO START", 32, 32);
        batch.end();
        fbo.end();


        if(noCRT) {
            batch.begin();
            Sprite s = new Sprite(fbo.getColorBufferTexture());
            s.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            s.flip(false, true); // coordinate system in buffer differs from screen
            batch.draw(s, 0, 0, 4 * s.getWidth(), 4 * s.getHeight()); //, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.end();
        } else {

            postProcessor.render(fbo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());    // retro effect
        }

//        viewport.apply();
//        batch2.begin();
//        Sprite s = new Sprite(fbo.getColorBufferTexture());
//        s.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
//        s.flip(false, true); // coordinate system in buffer differs from screen
//        batch2.draw(s, 0, 0, 4*s.getWidth(), 4*s.getHeight()); //, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        //font.draw(batch2, "3D CUBE", 200, 32);
//        batch2.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
        postProcessor.resize(width, height);
        //viewport.update(width, height);
    }


}
