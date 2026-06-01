package com.monstrous.pixels;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixels.filters.PostProcessor;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public PixelPerfectViewport viewport;
    public FrameBuffer fbo;
    public SpriteBatch batch;
    public SpriteBatch batch2;
    public PostProcessor postProcessor;
    public BitmapFont font;
    public Color background;
    private int savedWidth, savedHeight;
    private int mulW, mulH;

    @Override
    public void create() {
        modelBatch = new ModelBatch();


        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 150f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();

        model = modelBuilder.createBox(5f, 5f, 5f, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
            VertexAttributes.Usage.Position );
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

        viewport = new PixelPerfectViewport(LOWRES_WIDTH, LOWRES_HEIGHT);

        mulW = 3;
        mulH = 2;


    }

    @Override
    public void render() {
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
        modelBatch.end();
        batch.begin();
        font.draw(batch, "3D CUBE", 0, 32);
        batch.end();
        fbo.end();


        postProcessor.render(fbo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());    // retro effect


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
        viewport.update(width, height);
    }


}
