package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.pixels.WireFrameBuilder;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<GameObject> gameObjects;
    private final Array<GameObject> enemies;
    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Vector3 tmpVec = new Vector3();
    private final Vector3 tmpVec2 = new Vector3();
    private float coolDown = 0;
    public GameObjectType tankType;
    public GameObjectType jetType;
    public GameObjectType buildingType;
    public GameObjectType rocketType;
    public GameObjectType enemyRocketType;
    public GameObjectType towerType;
    public GameObjectType debrisType;
    public GameObjectType helicopterType;
    private final GameObject helicopter;
    private final Sound soundRocketFlyBy;
    private final Color background = new Color();



    public World() {

        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        Model tankModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"), Color.GREEN);
        Model tankTurretModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"), Color.GREEN);
        Model buildingModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Building"), Color.BROWN);
        Model towerModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("ControlTower"), Color.BROWN);
        Model rocketModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Rocket"), Color.WHITE);
        Model jetModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"), Color.CYAN);
        Model debrisModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Debris"), Color.BLACK);
        Model helicopterModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Helicopter"), Color.WHITE);

        tankType = new GameObjectType("TANK", tankModel, tankTurretModel);
        tankType.speed = 3f;
        tankType.turnSpeed = 2f;
        tankType.scorePoints = 100;
        tankType.isEnemy = true;
        jetType = new GameObjectType("JET", jetModel);
        jetType.speed = 30f;
        jetType.turnSpeed = 10f;
        jetType.scorePoints = 500;
        jetType.isEnemy = true;
        rocketType = new GameObjectType("ROCKET", rocketModel);
        rocketType.speed = 60f;
        rocketType.timeToLive = 5f;
        enemyRocketType = new GameObjectType("ROCKET", rocketModel);
        enemyRocketType.speed = 60f;
        enemyRocketType.timeToLive = 5f;
        enemyRocketType.gravity = 0.1f;
        buildingType = new GameObjectType("BUILDING", buildingModel);
        towerType = new GameObjectType("TOWER", towerModel);
        debrisType = new GameObjectType("DEBRIS", debrisModel);
        debrisType.speed = 30f;
        debrisType.timeToLive = 8f;
        debrisType.spinSpeed = 150f;
        debrisType.gravity = 1f;
        helicopterType = new GameObjectType("HELICOPTER", helicopterModel);


        terrain = new Terrain();

        gameObjects = new Array<>();
        enemies = new Array<>();  // subset

        instances = new Array<>();

        //generateInstances();
        helicopter = new GameObject(helicopterType, Vector3.Zero, Vector3.Zero);

        soundRocketFlyBy = Gdx.audio.newSound(Gdx.files.internal("sound/rocket-flyby.wav"));
    }

    private void populateOld(){
        addTank(new Vector3(0,0,-150), new Vector3(1,0,0) );
        addTank(new Vector3(28,0,0), new Vector3(0,0,1) );

        addBuilding(new Vector3(0,0,-20), new Vector3(1,0,0) );

        addBuilding(new Vector3(10,0,0), new Vector3(1,0,1) );
        addBuilding(new Vector3(38,0,0), new Vector3(-1,0,-1) );

        addJet( new Vector3(0,18,60), new Vector3(1,0,0));
        addJet( new Vector3(68,22,0), new Vector3(0, 0, 1));
    }

    public Color getColor(){
        return background;
    }

    public void populate(int level){
        int seed = level * 1234;
        MathUtils.random.setSeed(seed);
        int numTanks = 1 + 3 * (level-1);
        int numJets =  2 * (level-1);
        int numBuildings = 5 * level;
        int numTowers = 1;
        float spawnAreaSize = 250f+10*level;

        if(level == 1)
            background.set(0.0f, 0.2f, 0.1f, 1.0f); // greenish
        else
            background.set(MathUtils.random()*0.3f, MathUtils.random()*0.3f, MathUtils.random()*0.3f, 1.0f);

        gameObjects.clear();
        for(int i = 0; i < numTanks; i++) {
            float x = (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z = (MathUtils.random() - 0.5f) * spawnAreaSize;
            float deg = MathUtils.random(0, 360);
            addTank(new Vector3(x, 0, z), new Vector3(MathUtils.sin(deg), 0, MathUtils.cos(deg)));
        }

        for(int i = 0; i < numJets; i++) {
            float x =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float h = 10f + 30f * MathUtils.random();
            float deg = MathUtils.random(0, 360);
            addJet(new Vector3(x, h, z), new Vector3(MathUtils.sin(deg), 0, MathUtils.cos(deg)));
        }

        for(int i = 0; i < numBuildings; i++) {
            float x =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float deg = MathUtils.random(0, 360);
            addBuilding(new Vector3(x, 0, z), new Vector3(MathUtils.sin(deg), 0, MathUtils.cos(deg)));
        }

        for(int i = 0; i < numTowers; i++) {
            float x =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            addTower(new Vector3(x, 0, z), new Vector3(1,0,1));
        }
        generateInstances();
    }

    public void addBuilding(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(buildingType, position, direction));
    }

    public void addTower(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(towerType, position, direction));
    }

    public void addTank(Vector3 position, Vector3 direction){
        GameObject go = new GameObject(tankType, position, direction);
        gameObjects.add(go);
        enemies.add(go);
    }

    public void addJet(Vector3 position, Vector3 direction){
        GameObject go = new GameObject(jetType, position, direction);
        gameObjects.add(go);
        enemies.add(go);
    }

    public void addFriendlyRocket(Vector3 position, Vector3 direction, GameObject target){
            GameObject go = new GameObject(rocketType, position, direction);
            go.isEnemy = false;
            go.target = target;
            gameObjects.add(go);
    }

    public void addEnemyRocket(Vector3 position, Vector3 direction){
        GameObject go = new GameObject(enemyRocketType, position, direction);
        go.isEnemy = true;
        gameObjects.add(go);
        //rocketFlyBy.play();
    }

    public GameObject addDebris(Vector3 position, Vector3 direction){
        GameObject go = new GameObject(debrisType, position, direction);
        gameObjects.add(go);
        return go;
    }

    public int enemyCount(){
        return enemies.size;
    }

    private void generateInstances(){
        ModelInstance instance;

        instances.clear();
        instances.add(terrain.instance);

        for(GameObject go : gameObjects){
            instance = new ModelInstance(go.type.model, go.position);
            instance.transform.rotate(Vector3.Z, go.forward);
            instances.add(instance);
            // for tank turret etc.
            if(go.type.model2 != null){
                instance = new ModelInstance(go.type.model2, go.position);
                instance.transform.rotate(Vector3.Z, go.forward2);
                instances.add(instance);
            }
        }
    }

    public boolean fireRocket(Camera cam, GameObject target){
        if(coolDown <= 0) {
            addFriendlyRocket( tmpVec.set(cam.position).add(new Vector3(0, -0.3f, 0)), cam.direction, target);
            coolDown = 0.5f;
            return true;
        }
        return false;
    }

    public void update( float deltaTime, Vector3 cameraPosition ){
        coolDown -= deltaTime;

        Array<GameObject> toDelete = new Array<>();
        for(GameObject go : gameObjects){
            go.update(deltaTime);
            if( go.isDead)
                toDelete.add(go);

        }
        gameObjects.removeAll(toDelete, true);
        enemies.removeAll(toDelete, true);

        for(GameObject go : enemies){

            if(go.type == tankType) {
                // make turret point towards camera (instantly)
                go.forward2.set(cameraPosition).sub(go.position).scl(1, 0, 1).nor();

                go.timeToFire -= deltaTime;
                if (go.timeToFire < 0) {
                    go.timeToFire = (float) Math.random() * 10f;
                    tmpVec2.set(go.forward2);
                    tmpVec2.y += 0.2f;
                    tmpVec2.nor();
                    addEnemyRocket(tmpVec.set(go.position).add(new Vector3(0, 1.5f, 0)), tmpVec2);
                }
            }
            if(go.type == jetType) {
                // work out vector to player in the horizontal plane
                go.forward2.set(cameraPosition).sub(go.position).scl(1, 0, 1).nor();

                // if jet is heading more or less towards the player (dot product is close to 1)
                // and cool down period is expired, then fire a rocket
                go.timeToFire -= deltaTime;
                if (go.timeToFire < 0 && go.direction.dot(go.forward2) > 0.9f) {
                    go.timeToFire = (float) Math.random() * 10f;
                    tmpVec2.set(go.forward2);
                    addEnemyRocket(tmpVec.set(go.position).add(new Vector3(0, -1.5f, 0)), tmpVec2);
                }
            }
        }


        generateInstances();
    }


    private void blowUp(GameObject target){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        target.isDead = true;

        for(int i = 0; i < 12; i++){    // pieces of debris
            float az = (float)Math.random()*360f; // XZ direction
            axis.setToRandomDirection();    // spin axis

            vel.set((float)Math.sin(az), 2f + (float)Math.random(), (float)Math.cos(az)).nor();
            GameObject go = addDebris(target.position, vel);
            go.spinAxis.setToRandomDirection();
            // make debris model match colour of destroyed object
            Material mat = target.type.model.materials.get(0);
            Color diffuse = ((ColorAttribute)(mat.get(ColorAttribute.Diffuse))).color;
            go.type.model.materials.get(0).set(ColorAttribute.createDiffuse(diffuse));
        }

    }

    private final Vector3 intersection = new Vector3();

    public GameObject weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        for(GameObject go : enemies ){
            if(go.type == tankType || go.type == jetType) {
                if (Intersector.intersectRaySphere(ray, go.position, go.type.radius, intersection)) {
                    return go;
                }
            }
        }
        return null;
    }

    /** does a rocket hit any enemy? If so return the object that was hit, otherwise null.
     * If the player was hit, we return the helicopter object. */
    public GameObject rocketHits(Vector3 cameraPosition){
        for(int i = 0; i < gameObjects.size; i++ ){
            GameObject r = gameObjects.get(i);
            if(r.type == rocketType) { // player rockets
                // rockets are "heat seeking". They will follow their target.
                if(r.target != null){
                    // make rocket point towards target (instantly)
                    r.direction.set(r.target.position).sub(r.position).nor();
                }
                // have we hit an enemy
                for (GameObject t : enemies) {
                    if (t.type == tankType || t.type == jetType) {
                        if (r.position.dst(t.position) <  t.type.radius) {
                            r.isDead = true;

                            blowUp(t);
                            return t;
                        }
                    }
                }
            } else if(r.type == enemyRocketType){
                // enemy rockets
                if(!r.isMakingSound){
                    // if the enemy rocket is close enough, play the rocket sound
                    float dist = cameraPosition.dst(r.position);
                    if(dist < 100f){
                        r.isMakingSound = true;
                        soundRocketFlyBy.play();
                    }
                }
                if(r.position.dst(cameraPosition) < 2f){    // is size of hitbox
                    r.isDead = true;
                    Gdx.app.log("PLAYER HIT", "");
                    return helicopter;
                }
            }
        }
        return null;
    }

    public Array<ModelInstance> getInstances(){
        return instances;
    }

    @Override
    public void dispose() {
//        tankModel.dispose();
//        tankTurretModel.dispose();
    }
}
