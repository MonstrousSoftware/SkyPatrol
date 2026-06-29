package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.CamController;
import com.monstrous.pixels.WireFrameShader;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.GameObject;
import com.monstrous.pixels.world.World;



public class GameScreen extends RetroScreen {
    public PerspectiveCamera cam;
    public CamController inputController;
    public ModelBatch modelBatch;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    private Beep beep;
    private Sound soundBoom;
    private Sound soundFire;
    private World world;
    private float time;
    private float targetDistance;
    private int score = 0;
    private WireFrameShader wireFrameShader;
    private String message = "";
    private float messageTimer = 0;
    private int lives;
    private boolean gettingHit;
    private StringBuilder livesString;
    private int level;
    private float levelUpTimer;
    private float startupTimer;
    private GameObject target;
    private boolean hardCore;
    private FrameRateGadget fpsGadget;
    private boolean showFPS = true;
    private final boolean invincible = true;


    public GameScreen(Main game) {
        super(game);
    }


    @Override
    public void show() {
        super.show();


        cam = new PerspectiveCamera(67, LOWRES_WIDTH, LOWRES_HEIGHT);
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0, 10, 0);
        cam.near = 0.1f;
        cam.far = 1000f;
        cam.update();

        world = new World();
        level = 0;
        world.populate(level);

        inputController = new CamController(cam);
        setUpDownControls();
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        soundBoom = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.wav"));
        soundFire = Gdx.audio.newSound(Gdx.files.internal("sound/fire.wav"));

        beep = new Beep();

        if(game.enableMusic)
            beep.startMusic();

        Renderable renderable = new Renderable();
        ModelInstance instance = world.getInstances().get(1);
        instance.getRenderable(renderable);
        wireFrameShader = new WireFrameShader(renderable);

        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(final Renderable renderable) {
                return wireFrameShader;
            }
        });

        livesString = new StringBuilder();

        levelUp();

        hardCore = game.oneLife;

        fpsGadget = new FrameRateGadget();
    }

    private void setUpDownControls(){
        if(game.invertedControls) {
            inputController.forwardKey = Input.Keys.DOWN;
            inputController.backwardKey = Input.Keys.UP;
        } else {
            inputController.forwardKey = Input.Keys.UP;
            inputController.backwardKey = Input.Keys.DOWN;
        }
    }

    private void update(float deltaTime){
        time += deltaTime;
        inputController.update();

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(world.fireRocket(cam, target))   // target may be null
                soundFire.play();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            message = enableCRTeffect ? "TV OUTPUT" : "MONITOR OUTPUT";
            messageTimer = 1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {

            if (beep.isMusicPlaying()) {
                beep.stopMusic();
                game.enableMusic = false;
            } else {
                game.enableMusic = true;
                beep.startMusic();
            }
            message = game.enableMusic ? "MUSIC ON" : "MUSIC OFF";
            messageTimer = 1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            game.invertedControls = !game.invertedControls;
            message = game.invertedControls ? "INVERTED PITCH CONTROL" : "REGULAR PITCH CONTROL";
            messageTimer = 1f;
            setUpDownControls();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            game.oneLife = !game.oneLife;
            message = game.oneLife ? "ONE LIFE ONLY" : "HEALTH PERCENTAGE";
            messageTimer = 1f;
            if(!game.oneLife)       // using health status even a little bit invalidated hard core status for the score.
                hardCore = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            showFPS = !showFPS;
            message = showFPS ? "FPS ON" : "FPS OFF";
            messageTimer = 1f;
        }


        world.update(deltaTime, cam.position);
        if(messageTimer > 0){
            messageTimer -= deltaTime;
            if(messageTimer <= 0)
                message = "";
        }

        target = world.weaponLocked(cam);   // null means no lock
        if(target != null) {
            //soundLock.play();
            targetDistance = target.position.dst(cam.position);
            message = "DISTANCE: "+(int)targetDistance;
            messageTimer = 1f;
        }

        gettingHit = false;
        GameObject killed = world.rocketHits(cam);
        if(killed != null){
            soundBoom.play();

            if(!invincible && killed.type == world.helicopterType){
                gettingHit = true;
                lives-= game.oneLife ? 5 : 1;
                livesString.setLength(0);
                livesString.append("HEALTH: ");
                livesString.append(lives * 20);
                livesString.append("%");
                if(!game.oneLife && lives > 0) {
                    message = "TAKING DAMAGE!";
                    messageTimer = 1f;
                } else {
                    message = "GAME OVER! (PRESS 1)";
                    messageTimer = 10f;
                }
            } else {
                score += killed.type.scorePoints;
                message = "DESTROYED " + killed.type.typeName;
                messageTimer = 1f;
            }
        }
        // all enemies defeated? go to next level (after a few seconds)
        if(world.enemyCount() == 0){
            // start next level with a little delay so we can enjoy the explosion
            if(levelUpTimer < 0)    // timer not started yet?
                levelUpTimer = 2f;  // start timer before level up
        }
        if(levelUpTimer > 0) {
            levelUpTimer -= deltaTime;
            if(levelUpTimer <= 0)
                levelUp();
        }
        startupTimer -= deltaTime;
    }

    private void levelUp(){
        level++;
        lives = 5;
        livesString.setLength(0);
        livesString.append("HEALTH: ");
        livesString.append(lives * 20);
        livesString.append("%");
        message = "GET READY!";
        messageTimer = 2f;
        world.populate(level);
        cam.position.set(0f, 10f, 10f);
        cam.up.set(Vector3.Y);
        cam.lookAt(0, 10, 0);
        inputController.reset();
        startupTimer = 2f;
        levelUpTimer = -1;
    }


    @Override
    protected void renderFrame(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.setScreen(new NewScoreScreen(game, score, hardCore));
            return;
        }
        // do updates
        if(lives > 0)
            update(deltaTime);

        // render frame
        ScreenUtils.clear(gettingHit ? Color.WHITE: world.getColor(), true);

        if(startupTimer < 0) { // hide during start up sequence
            modelBatch.begin(cam);
            modelBatch.render(world.getInstances());
            modelBatch.end();

            drawReticule(target);
        }

        int mm = (int)time / 60;
        int ss = (int)time - 60*mm;

        batch.begin();
        font.draw(batch, String.format("SCORE: %05d", score), 8, LOWRES_HEIGHT-8);
        font.draw(batch, String.format("LEVEL: %d", level), 150, LOWRES_HEIGHT-8);
        font.draw(batch, String.format("%02d:%02d", mm, ss), 270, LOWRES_HEIGHT-8);
        if(!game.oneLife)
            font.draw(batch, livesString.toString(), 8, LOWRES_HEIGHT-18);
        font.draw(batch, message, 10, 10);

        if(startupTimer > 0) { // level up sequence
            font.draw(batch, String.format("LEVEL: %d", level), 100, LOWRES_HEIGHT/2f);
        }
        batch.end();


    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);
        fpsGadget.update(deltaTime);
        if(showFPS)
            fpsGadget.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        fpsGadget.resize(width, height);
    }

    private void drawReticule(GameObject target){
        int dx = 30;
        int sdx = 15;
        int adx = target != null ? 15 : 30;
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
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if(beep.isMusicPlaying())
            beep.stopMusic();
        world.dispose();
        modelBatch.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }


}
