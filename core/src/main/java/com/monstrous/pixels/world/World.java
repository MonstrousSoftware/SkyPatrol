package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
    private float coolDown = 0;
    public GameObjectType tankType;
    public GameObjectType jetType;
    public GameObjectType buildingType;
    public GameObjectType rocketType;
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
        Model debrisModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Debris"), Color.LIGHT_GRAY);

        tankType = new GameObjectType("TANK", tankModel, tankTurretModel);
        tankType.speed = 1f;
        tankType.turnSpeed = 1f;
        jetType = new GameObjectType("JET", jetModel);
        jetType.speed = 30f;
        jetType.turnSpeed = 10f;
        rocketType = new GameObjectType("ROCKET", rocketModel);
        rocketType.speed = 60f;
        rocketType.timeToLive = 5f;
        buildingType = new GameObjectType("BUILDING", buildingModel);
        debrisType = new GameObjectType("DEBRIS", debrisModel);
        debrisType.speed = 30f;
        debrisType.timeToLive = 18f;
        debrisType.spinSpeed = 50f;
        debrisType.gravity = 1f;


        terrain = new Terrain();

        gameObjects = new Array<>();
        enemies = new Array<>();  // subset

        populate();

        //blowUp(new Vector3(0,0,-100));

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

    public void addRocket(Vector3 position, Vector3 direction){
        gameObjects.add(new GameObject(rocketType, position, direction));
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
                instance.transform.rotate(Vector3.Z, go.forward); // todo
                instances.add(instance);
            }
        }
    }

    public boolean fireRocket(Camera cam){
        if(coolDown <= 0) {
            addRocket(tmpVec.set(cam.position).add(new Vector3(0, -0.3f, 0)), cam.direction);
            coolDown = 0.5f;
            return true;
        }
        return false;
    }

    public void update( float deltaTime ){
        coolDown -= deltaTime;

        Array<GameObject> toDelete = new Array<>();
        for(GameObject go : gameObjects){
            go.update(deltaTime);
            if( go.isDead)
                toDelete.add(go);

        }
        gameObjects.removeAll(toDelete, true);
        enemies.removeAll(toDelete, true);

        generateInstances();
    }

    private void blowUp(Vector3 position){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        for(int i = 0; i < 12; i++){
            float az = (float)Math.random()*360f;
            axis.setToRandomDirection();

            vel.set((float)Math.sin(az), 2f + (float)Math.random(), (float)Math.cos(az)).nor();
            GameObject go = addDebris(position, vel);
            go.spinAxis.setToRandomDirection();
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
    public int rocketHits(){
        for(int i = 0; i < gameObjects.size; i++ ){
            GameObject r = gameObjects.get(i);
            if(r.type != rocketType)
                continue;

            for(GameObject t : enemies ){
                if(t.type == tankType || t.type == jetType){
                    if (r.position.dst(t.position) < t.type.radius) {
                        r.isDead = true;
                        t.isDead= true;
                        blowUp(t.position);
                        return t.type == jetType? 500 : 100;
                    }
                }
            }
        }
        return 0;
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
