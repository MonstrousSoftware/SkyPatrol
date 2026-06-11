package com.monstrous.pixels.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixels.CamController;
import com.monstrous.pixels.WireFrameBuilder;
import com.monstrous.pixels.WireFrameShader;
import com.monstrous.pixels.sound.Beep;
import com.monstrous.pixels.world.GameObject;
import com.monstrous.pixels.world.World;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;


public class GameScreen extends RetroScreen {
    public PerspectiveCamera cam;
    public CamController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    //public Color background;
    private Beep beep;
    private Sound soundLock;
    private Sound soundBoom;
    private Sound soundFire;
    private World world;
    private float time;
    private float targetDistance;
    private boolean enableMusic = true;
    private int score = 0;
    private WireFrameShader wireFrameShader;
    private String message = "";
    private float messageTimer = 0;
    private int lives;
    private StringBuilder livesString;
    private int level;
    private float levelUpTimer;
    private float startupTimer;
    private final Vector3 intersection = new Vector3();
    private GameObject target;


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

        //inputController = new CameraInputController(cam);
        inputController = new CamController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputController));

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        //background = new Color(0.0f, 0.2f, 0.1f, 1.0f); // greenish

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);



        soundLock = Gdx.audio.newSound(Gdx.files.internal("sound/lock.wav"));
        soundBoom = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.wav"));
        soundFire = Gdx.audio.newSound(Gdx.files.internal("sound/fire.wav"));

        beep = new Beep();
        //beep.beep();

        if(enableMusic)
            beep.startMusic();

        Renderable renderable = new Renderable();
        ModelInstance instance = world.getInstances().get(1);
        instance.getRenderable(renderable);
        wireFrameShader = new WireFrameShader(renderable);
        //modelBatch = new ModelBatch();
        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(final Renderable renderable) {
                return wireFrameShader;
            }
        });

        livesString = new StringBuilder();

        levelUp();
    }

    private void update(float deltaTime){
        time += deltaTime;
        inputController.update();

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(world.fireRocket(cam, target))   // target may be null
                soundFire.play();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            if(beep.isMusicPlaying())
                beep.stopMusic();
            else
                beep.startMusic();
        }

        world.update(0.016f, cam.position);//;deltaTime);
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

        GameObject killed = world.rocketHits(cam.position);
        if(killed != null){
            soundBoom.play();
            if(killed.type == world.helicopterType){
                lives--;
                livesString.setLength(0);
                livesString.append("HEALTH: ");
                livesString.append(lives * 20);
                livesString.append("%");
                if(lives > 0) {
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
            //System.out.println("levelUpTimer "+levelUpTimer+" "+deltaTime);
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
            game.setScreen(new NewScoreScreen(game, score));
            return;
        }
        // do updates
        if(lives > 0)
            update(deltaTime);

        // render frame
        ScreenUtils.clear(world.getColor(), true);

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
        font.draw(batch, livesString.toString(), 8, LOWRES_HEIGHT-18);
        font.draw(batch, message, 100, 10);

        if(startupTimer > 0) { // level up sequence
            font.draw(batch, String.format("LEVEL: %d", level), 100, LOWRES_HEIGHT/2f);
        }
        batch.end();
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
    public void dispose() {
        world.dispose();
        modelBatch.dispose();
        model.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }


}
