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
    private final Array<GameObject> enemies;            // subset of gameObjects: all enemies
    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Vector3 tmpVec = new Vector3();
    private final Vector3 tmpVec2 = new Vector3();
    private float rocketCoolDown = 0;
    private float rocketFireRate = 2f;
    public GameObjectType tankType;
    public GameObjectType tankTurretType;
    public GameObjectType jetType;
    public GameObjectType buildingType;
    public GameObjectType rocketType;
    public GameObjectType enemyRocketType;
    public GameObjectType towerType;
    public GameObjectType debrisType;
    public GameObjectType helicopterType;
    public GameObjectType watermelonType;
    private GameObject watermelon;
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
        Model watermelonModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Watermelon"), Color.GREEN);

        tankType = new GameObjectType("TANK", tankModel);
        tankType.speed = 3f;
        tankType.turnSpeed = 2f;
        tankType.scorePoints = 100;
        tankTurretType = new GameObjectType("TANKTURRET", tankTurretModel);
        tankTurretType.scorePoints = 0;
        jetType = new GameObjectType("JET", jetModel);
        jetType.speed = 30f;
        jetType.turnSpeed = 10f;
        jetType.scorePoints = 500;
        rocketType = new GameObjectType("ROCKET", rocketModel);
        rocketType.speed = 60f;
        rocketType.timeToLive = 7f;
        enemyRocketType = new GameObjectType("ROCKET", rocketModel);
        enemyRocketType.speed = 60f;
        enemyRocketType.timeToLive = 5f;
        enemyRocketType.gravity = 6f;
        buildingType = new GameObjectType("BUILDING", buildingModel);
        towerType = new GameObjectType("TOWER", towerModel);
        debrisType = new GameObjectType("DEBRIS", debrisModel);
        debrisType.speed = 30f;
        debrisType.timeToLive = 8f;
        debrisType.spinSpeed = 150f;
        debrisType.gravity = 30f;
        watermelonType = new GameObjectType("LEVITATING WATERMELON", watermelonModel);
        watermelonType.speed = 0f;
        watermelonType.spinSpeed = 150f;
        helicopterType = new GameObjectType("HELICOPTER", helicopterModel);


        terrain = new Terrain();

        gameObjects = new Array<>();
        enemies = new Array<>();  // subset

        instances = new Array<>();

        // this game object is not placed in the gameObjects array, is never visible
        // but used to signify the player
        helicopter = new GameObject(helicopterType, Vector3.Zero, Vector3.Zero);

        soundRocketFlyBy = Gdx.audio.newSound(Gdx.files.internal("sound/rocket-flyby.wav"));
    }


    public Color getColor(){
        return background;
    }

    public void populate(int level){
        int seed = level * 1234;        // random but reproducible
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
            addTank(new Vector3(x, 0, z), new Vector3( MathUtils.sin(deg), 0,  MathUtils.cos(deg)).scl(tankType.speed));
        }

        for(int i = 0; i < numJets; i++) {
            float x =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float h = 10f + 30f * MathUtils.random();
            float deg = MathUtils.random(0, 360);
            addJet(new Vector3(x, h, z), new Vector3(MathUtils.sin(deg), 0, MathUtils.cos(deg)).scl(jetType.speed));
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

        for(int i = 0; i < 1; i++) {
            float x =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            float z =  (MathUtils.random() - 0.5f) * spawnAreaSize;
            watermelon = addWatermelon(new Vector3(x, 8, z));   // add height
        }
        generateInstances();
    }

    public void addBuilding(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(buildingType, position, Vector3.Zero, direction));
    }

    public void addTower(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(towerType, position, Vector3.Zero, direction));
    }

    public void addTank(Vector3 position, Vector3 velocity){
        GameObject tank = new GameObject(tankType, position, velocity);
        gameObjects.add(tank);
        enemies.add(tank);
        GameObject turret = new GameObject(tankTurretType, position, Vector3.Zero);
        turret.parent = tank;     // attach turret to tank, i.e. follow position
        tank.child = turret;
        gameObjects.add(turret);
        enemies.add(turret);
    }

    public void addJet(Vector3 position, Vector3 velocity){
        GameObject go = new GameObject(jetType, position, velocity);
        gameObjects.add(go);
        enemies.add(go);
    }

    public void addFriendlyRocket(Vector3 position, Vector3 velocity, GameObject target){
        GameObject go = new GameObject(rocketType, position, velocity);
        go.target = target;
        gameObjects.add(go);
    }

    public void addEnemyRocket(Vector3 position, Vector3 velocity){
        GameObject go = new GameObject(enemyRocketType, position, velocity);
        gameObjects.add(go);
    }

    public GameObject addDebris(Vector3 position, Vector3 velocity){
        GameObject go = new GameObject(debrisType, position, velocity);
        gameObjects.add(go);
        return go;
    }

    public GameObject addWatermelon(Vector3 position){
        GameObject go = new GameObject(watermelonType, position, Vector3.Z);
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
        }
    }

    /** player fires rocket in camera's forward direction. Target may be null.
     */
    public boolean fireRocket(Camera cam, GameObject target){
        if(rocketCoolDown <= 0) {
            tmpVec.set(cam.position).add(new Vector3(0, -0.3f, 0)); // initial position is just below camera position
            tmpVec2.set(cam.direction).scl(rocketType.speed);    // velocity vector
            addFriendlyRocket( tmpVec, tmpVec2, target);
            rocketCoolDown = rocketFireRate;
            return true;
        }
        return false;
    }


    public void update( float deltaTime, Vector3 cameraPosition ){
        rocketCoolDown -= deltaTime;

        Array<GameObject> toDelete = new Array<>();
        for(GameObject go : gameObjects){
            go.update(deltaTime);
            if( go.isDead)
                toDelete.add(go);

        }
        gameObjects.removeAll(toDelete, true);
        enemies.removeAll(toDelete, true);

        for(GameObject go : enemies){

            if(go.type == tankTurretType) {
                // make turret point towards camera (instantly)
                go.forward.set(cameraPosition).sub(go.position).scl(1, 0, 1).nor();

                go.timeToFire -= deltaTime;
                if (go.timeToFire < 0) {
                    go.timeToFire = (float) Math.random() * 10f;
                    tmpVec2.set(go.forward);
                    tmpVec2.y += 0.2f;  // shoot slightly up
                    tmpVec2.nor();
                    tmpVec2.scl(rocketType.speed);
                    addEnemyRocket(tmpVec.set(go.position).add(new Vector3(0, 1.5f, 0)), tmpVec2);
                }
            }

            if(go.type == jetType) {
                // if jet is heading more or less towards the player (dot product is close to 1)
                // and cool down period is expired, then fire a rocket
                go.timeToFire -= deltaTime;
                if (go.timeToFire < 0) {
                    // work out unit vector to player in the horizontal plane
                    tmpVec2.set(cameraPosition).sub(go.position).scl(1, 0, 1).nor();
                    tmpVec.set(go.velocity).nor();  // normalized velocity
                    if( tmpVec.dot(tmpVec2) > 0.9f) {     // jet is heading more or less towards player
                        go.timeToFire = (float) Math.random() * 3f;
                        tmpVec2.scl(rocketType.speed);
                        addEnemyRocket(tmpVec.set(go.position).add(new Vector3(0, -1.5f, 0)), tmpVec2);
                    }
                }
            }
        }
        generateInstances();
    }



    private void blowUp(GameObject target){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        target.isDead = true;
        // compound objects, e.g. tank and turret
        if(target.child != null)
            target.child.isDead = true;
        if(target.parent != null)
            target.parent.isDead = true;

        for(int i = 0; i < 12; i++){    // pieces of debris
            float az = (float)Math.random()*360f; // XZ direction
            axis.setToRandomDirection();    // random spin axis

            vel.set((float)Math.sin(az), 2f + (float)Math.random(), (float)Math.cos(az)).nor().scl(debrisType.speed);
            GameObject go = addDebris(target.position, vel);
            go.spinAxis.setToRandomDirection();
            // make debris model match colour of destroyed object
            Material mat = target.type.model.materials.get(0);
            Color diffuse = ((ColorAttribute)(mat.get(ColorAttribute.Diffuse))).color;
            go.type.model.materials.get(0).set(ColorAttribute.createDiffuse(diffuse));
        }

    }

    private final Vector3 intersection = new Vector3();

    /** Use ray casting to check if there is an enemy directly in front. Returns null if not */
    public GameObject weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        for(GameObject go : enemies ){
            if (Intersector.intersectRaySphere(ray, go.position, go.type.radius, intersection)) {
                return go;
            }
        }
        if (!watermelon.isDead && Intersector.intersectRaySphere(ray, watermelon.position, watermelon.type.radius, intersection)) {
            return watermelon;
        }
        return null;
    }

    /** does a rocket hit any enemy? If so return the object that was hit, otherwise null.
     * If the player was hit, we return the helicopter object. */
    public GameObject rocketHits(Camera camera){
        for(int i = 0; i < gameObjects.size; i++ ){
            GameObject r = gameObjects.get(i);
            if(r.type == rocketType) { // player rockets
                // rocket with a target are "heat seeking". They will follow their target.
                if(r.target != null){
                    // make rocket point towards target (instantly)
                    r.velocity.set(r.target.position).sub(r.position).nor().scl(rocketType.speed);
                }
                // have we hit an enemy?
                for (GameObject t : enemies) {
                    if (r.position.dst(t.position) <  t.type.radius) {
                        r.isDead = true;    // delete rocket
                        // if you hit a turret, return the parent tank
                        if(t.parent != null)
                            t = t.parent;
                        blowUp(t);          // blow up enemy
                        return t;
                    }
                }
                if (!watermelon.isDead && r.position.dst(watermelon.position) <  5) { //watermelon.type.radius) {
                    r.isDead = true;    // delete rocket
                    rocketFireRate *= 0.8f; // increase firing rate by some %
                    blowUp(watermelon);          // blow up enemy
                    return watermelon;
                }
            } else if(r.type == enemyRocketType){ // enemy rockets

                // if the enemy rocket is close enough, play the rocket sound
                if(!r.isMakingSound){

                    float dist = camera.position.dst(r.position);
                    if(dist < 100f){
                        r.isMakingSound = true;
                        soundRocketFlyBy.play();
                    }
                }
                // if the enemy rocket is very close, take damage or die
                if(r.position.dst(camera.position) < 1.0f){    // is size of hitbox
                    float dot = r.velocity.dot(camera.direction);
                    //System.out.println("rocket angle: "+dot);
                    if(dot < -0.5f) {   // rockets from behind will always miss
                        r.isDead = true;    // delete rocket
                        return helicopter;  // signifies the player was hit
                    }
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
        // todo
//        tankModel.dispose();
//        tankTurretModel.dispose();
    }
}
