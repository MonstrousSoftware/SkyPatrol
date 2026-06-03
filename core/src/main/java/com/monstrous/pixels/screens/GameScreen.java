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


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen extends ScreenAdapter {
    public final int LOWRES_WIDTH = 320;
    public final int LOWRES_HEIGHT = 240;

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public FrameBuffer fboSmall;
    public FrameBuffer fbo;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public PostProcessor postProcessor;
    public BitmapFont font;
    public Color background;
    public int pixelScale;
    private int savedWidth, savedHeight;
    private Beep beep;
    private final Main game;
    private World world;
    private float time;
    private boolean enableCRTeffect = true;
    private boolean enableMusic = false;


    public GameScreen(Main game) {
        this.game = game;

    }

    @Override
    public void show() {
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

        fboSmall = new FrameBuffer(Pixmap.Format.RGBA4444, LOWRES_WIDTH, LOWRES_HEIGHT, true);

        batch = new SpriteBatch();
        postProcessor = new PostProcessor();

        background = new Color(0.0f, 0.2f, 0.1f, 1.0f);

        font = new BitmapFont(Gdx.files.internal("font/zx-spectrum.fnt"));
        font.setColor(Color.GREEN);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        beep = new Beep();
        //beep.beep();

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

        world.update(0.1f);

        // render a low resolution frame to the frame buffer
        fboSmall.begin();
            ScreenUtils.clear(background, true);

            modelBatch.begin(cam);
            modelBatch.render(world.getInstances());
            modelBatch.end();

            drawReticule(time > 2);

            batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);
            batch.begin();
            font.draw(batch, "SKY PATROL", 32, 32);
            batch.end();
        fboSmall.end();


        // place the low resolution frame centrally on the screen, surrounded by a border
        fbo.begin();
            ScreenUtils.clear(Color.BLUE, true);    // border
            batch.getProjectionMatrix().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();
            Sprite s = new Sprite(fboSmall.getColorBufferTexture());
            s.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            s.flip(false, true); // coordinate system in buffer differs from screen
            int w = pixelScale * (int)s.getWidth();
            int h = pixelScale * (int)s.getHeight();
            int x = (Gdx.graphics.getWidth() - w)/2;
            int y = (Gdx.graphics.getHeight() - h)/2;
            batch.draw(s, x, y, w, h);
            batch.end();
        fbo.end();

        // post-processing for visual effects
        if(!enableCRTeffect) {
            Sprite s2 = new Sprite(fbo.getColorBufferTexture());
            s2.flip(false, true);   // flip Y
            batch.begin();
            batch.draw(s2, 0, 0);
            batch.end();
        } else {
            postProcessor.render(fbo);    // retro effect
        }
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
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        postProcessor.resize(width, height);

        pixelScale = Math.min(width/LOWRES_WIDTH, height/LOWRES_HEIGHT);
    }


}
