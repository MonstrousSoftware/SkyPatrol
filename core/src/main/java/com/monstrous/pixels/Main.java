package com.monstrous.pixels;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 320;

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public FrameBuffer fbo;
    public SpriteBatch batch;
    private int savedWidth, savedHeight;

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

        model = modelBuilder.createBox(5f, 5f, 5f, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            VertexAttributes.Usage.Position );
        instance = new ModelInstance(model);

        inputController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        fbo = new FrameBuffer(Pixmap.Format.RGBA4444, LOWRES_WIDTH, LOWRES_HEIGHT, true);
        batch = new SpriteBatch();
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
        ScreenUtils.clear(Color.BLACK, true);

        modelBatch.begin(cam);
        modelBatch.render(instance);
        modelBatch.end();
        fbo.end();

        batch.begin();
        batch.draw(fbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        batch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0,0, width, height);
    }


}
