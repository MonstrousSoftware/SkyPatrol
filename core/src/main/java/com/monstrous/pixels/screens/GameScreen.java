package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.filters.PostProcessor;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.World;
import net.mgsx.gltf.scene3d.scene.SceneManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen extends ScreenAdapter {
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
    public ShapeRenderer shapeRenderer;
    public PostProcessor postProcessor;
    public BitmapFont font;
    public Color background;
    private int savedWidth, savedHeight;
    private Beep beep;
    private final Main game;
    private World world;
    private float time;
    private boolean noCRT = false;
    private boolean enableMusic = false;


    public GameScreen(Main game) {
        this.game = game;

    }

    @Override
    public void show() {
        modelBatch = new ModelBatch();


        cam = new PerspectiveCamera(67, LOWRES_WIDTH, LOWRES_HEIGHT); //Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 150f;
        cam.update();

        world = new World();



        inputController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, LOWRES_WIDTH, LOWRES_HEIGHT, true);
        batch = new SpriteBatch();
        batch2 = new SpriteBatch();
        postProcessor = new PostProcessor();

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);

        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        beep = new Beep();
        beep.beep();

        if(enableMusic)
            beep.startMusic();

    }

    @Override
    public void render(float deltaTime) {
        time += deltaTime;

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
//        float delta = Gdx.graphics.getDeltaTime();
//        instance.transform.rotate(Vector3.Y, 20f*delta);

        world.update(0.1f);

        fbo.begin();
            ScreenUtils.clear(background, true);

            modelBatch.begin(cam);
            //modelBatch.render(instance);
            modelBatch.render(world.getInstances());
            modelBatch.end();

            drawReticule(time > 2);

            batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
            batch.begin();
            font.draw(batch, "SKY PATROL", 32, 32);
            batch.end();
        fbo.end();


        if(noCRT) {
            batch.getProjectionMatrix().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
        postProcessor.resize(width, height);
        //viewport.update(width, height);
    }


}
