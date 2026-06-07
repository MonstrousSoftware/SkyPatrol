package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.pixels.WireFrameBuilder;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    public final Array<GameObject> gameObjects;
    public final Array<GameObject> enemies;
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
    public GameObjectType debrisType;


    public World() {

        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        Model tankModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"), Color.GREEN);
        Model tankTurretModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"), Color.GREEN);
        Model buildingModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Building"), Color.BROWN);
        //Model towerModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Tower"), Color.BROWN);
        Model rocketModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Rocket"), Color.WHITE);
        Model jetModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"), Color.CYAN);
        Model debrisModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Debris"), Color.BLACK);

        tankType = new GameObjectType("TANK", tankModel, tankTurretModel);
        tankType.speed = 5f;
        tankType.turnSpeed = 1f;
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
        debrisType = new GameObjectType("DEBRIS", debrisModel);
        debrisType.speed = 30f;
        debrisType.timeToLive = 8f;
        debrisType.spinSpeed = 150f;
        debrisType.gravity = 1f;


        terrain = new Terrain();

        gameObjects = new Array<>();
        enemies = new Array<>();  // subset

        populate2();

        instances = new Array<>();

        generateInstances();
    }

    private void populate(){
        addTank(new Vector3(0,0,-150), new Vector3(1,0,0) );
        addTank(new Vector3(28,0,0), new Vector3(0,0,1) );

        addBuilding(new Vector3(0,0,-20), new Vector3(1,0,0) );

        addBuilding(new Vector3(10,0,0), new Vector3(1,0,1) );
        addBuilding(new Vector3(38,0,0), new Vector3(-1,0,-1) );

        addJet( new Vector3(0,18,60), new Vector3(1,0,0));
        addJet( new Vector3(68,22,0), new Vector3(0, 0, 1));
    }

    private void populate2(){
        // todo fix seed
        int numTanks = 10;
        int numJets = 10;
        int numBuildings = 30;

        for(int i = 0; i < numTanks; i++) {
            float x = (float)Math.random() * 500f - 250f;
            float z = (float)Math.random() * 500f - 250f;
            addTank(new Vector3(x, 0, z), new Vector3(1, 0, 0));
        }

        for(int i = 0; i < numJets; i++) {
            float x = (float)Math.random() * 500f - 250f;
            float z = (float)Math.random() * 500f - 250f;
            float h = 10f + 20f * (float)Math.random();
            addJet(new Vector3(x, h, z), new Vector3(1, 0, 0));
        }

        for(int i = 0; i < numBuildings; i++) {
            float x = (float)Math.random() * 500f - 250f;
            float z = (float)Math.random() * 500f - 250f;
            addBuilding(new Vector3(x, 0, z), new Vector3(1, 0, 0));
        }
    }

    public void addBuilding(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(buildingType, position, direction));
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

    public void addRocket(boolean isEnemy, Vector3 position, Vector3 direction){
        GameObject go;
        if(isEnemy) {
            go = new GameObject(enemyRocketType, position, direction);
            go.isEnemy = isEnemy;
        } else {
            go = new GameObject(rocketType, position, direction);
        }
        gameObjects.add(go);
    }

    public void addEnemyRocket(boolean isEnemy, Vector3 position, Vector3 direction){
        GameObject go = new GameObject(rocketType, position, direction);
        go.isEnemy = isEnemy;
        gameObjects.add(go);
    }

    public GameObject addDebris(Vector3 position, Vector3 direction){
        GameObject go = new GameObject(debrisType, position, direction);
        gameObjects.add(go);
        return go;
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

    public boolean fireRocket(Camera cam){
        if(coolDown <= 0) {
            addRocket(false, tmpVec.set(cam.position).add(new Vector3(0, -0.3f, 0)), cam.direction);
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

        for(GameObject tank : enemies){
            if(tank.type != tankType)
                continue;
            // make turret point towards camera
            tank.forward2.set(cameraPosition).sub(tank.position).scl(1,0,1).nor();

            tank.timeToFire -= deltaTime;
            if(tank.timeToFire < 0){
                tank.timeToFire = (float)Math.random() * 10f;
                tmpVec2.set(tank.forward2);
                tmpVec2.y += 0.2f;
                tmpVec2.nor();
                addRocket(true, tmpVec.set(tank.position).add(new Vector3(0, 1.5f, 0)), tmpVec2);
            }
        }


        generateInstances();
    }


    private void blowUp(GameObject target){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        for(int i = 0; i < 12; i++){
            float az = (float)Math.random()*360f;
            axis.setToRandomDirection();

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

    public boolean weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        for(GameObject go : enemies ){
            if(go.type == tankType || go.type == jetType) {
                if (Intersector.intersectRaySphere(ray, go.position, go.type.radius, intersection))
                    return true;
            }
        }
        return false;
    }

    /** does a rocket hit any enemy? If so return the number of points earned, otherwise zero */
    public GameObject rocketHits(Vector3 cameraPosition){
        for(int i = 0; i < gameObjects.size; i++ ){
            GameObject r = gameObjects.get(i);
            if(r.type != rocketType  && r.type != enemyRocketType)
                continue;
            if(!r.isEnemy) {
                // player rockets
                for (GameObject t : enemies) {
                    if (t.type == tankType || t.type == jetType) {
                        if (r.position.dst(t.position) < t.type.radius) {
                            r.isDead = true;
                            t.isDead = true;
                            blowUp(t);
                            return t; //.type == jetType? 500 : 100;
                        }
                    }
                }
            } else {
                // enemy rockets
                if(r.position.dst(cameraPosition) < 5f){
                    Gdx.app.log("PLAYER DEAD", "");
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
