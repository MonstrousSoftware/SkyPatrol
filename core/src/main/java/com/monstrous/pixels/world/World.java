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

    public final Array<Tank> tanks;
    public final Array<Jet> jets;
    public final Array<Building> buildings;
    public final Array<Rocket> rockets;
    public final Array<Debris> debris;
    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Model tankModel;
    private final Model tankTurretModel;
    private final Model buildingModel;
//    private final Model aaBody;
//    private final Model aaTurret;
    private final Model jetModel;
    private final Model rocketModel;
    private final Model debrisModel;
    private final Vector3 tmpVec = new Vector3();
    private float coolDown = 0;


    public World(Camera cam) {
        tanks = new Array<>();
        tanks.add(new Tank(new Vector3(0,0,-150), new Vector3(1,0,0)));
        tanks.add(new Tank(new Vector3(28,0,0), new Vector3(0, 0, 1)));

        jets = new Array<>();
        jets.add(new Jet(new Vector3(0,18,60), new Vector3(1,0,0)));
        jets.add(new Jet(new Vector3(68,22,0), new Vector3(0, 0, 1)));

        buildings = new Array<>();
        buildings.add(new Building(new Vector3(10,0,0), new Vector3(1,0,1)));
        buildings.add(new Building(new Vector3(38,0,0), new Vector3(-1, 0, -1)));

        rockets = new Array<>();

        debris = new Array<>();


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        tankModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"), Color.GREEN);
        tankTurretModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"), Color.GREEN);
        buildingModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Building"), Color.BROWN);
        rocketModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Rocket"), Color.WHITE);
        jetModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"), Color.CYAN);
        debrisModel = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Debris"), Color.LIGHT_GRAY);


        terrain = new Terrain();

        instances = new Array<>();

        populate(terrain, tanks, jets, buildings, debris);
    }

    private void populate(Terrain terrain, Array<Tank> tanks, Array<Jet> jets, Array<Building> buildings, Array<Debris> debris){
        ModelInstance instance;

        instances.clear();
        instances.add(terrain.instance);

        for(Building b : buildings) {
            instance = new ModelInstance(buildingModel, b.position);
            instance.transform.rotate(Vector3.Z, b.direction);
            instances.add(instance);
        }

        for(Tank tank : tanks) {
            instance = new ModelInstance(tankModel, tank.position);
            instance.transform.rotate(Vector3.Z, tank.direction);
            instances.add(instance);
            instance = new ModelInstance(tankTurretModel, tank.position);
            instance.transform.rotate(Vector3.Z, tank.direction);
            instances.add(instance);
        }

        for(Jet j : jets) {
            instance = new ModelInstance(jetModel, j.position);
            instance.transform.rotate(Vector3.Z, j.direction);
            instances.add(instance);
        }

        for(Rocket r : rockets) {
            instance = new ModelInstance(rocketModel, r.position);
            instance.transform.rotate(Vector3.Z, r.direction);
            instances.add(instance);
        }

        for(Debris d : debris) {
            instance = new ModelInstance(debrisModel, d.position);
            instance.transform.rotate(Vector3.Z, d.direction);
            instances.add(instance);
        }
    }

    public boolean fireRocket(Camera cam){
        if(coolDown <= 0) {
            rockets.add(new Rocket(tmpVec.set(cam.position).add(new Vector3(0, -1, 0)), cam.direction));
            coolDown = 0.5f;
            return true;
        }
        return false;
    }

    public void update( float deltaTime ){
        coolDown -= deltaTime;
        Array<Tank> tanksToDelete = new Array<>();
        for(Tank tank : tanks) {
            tank.forward(deltaTime);
            tank.rotate(deltaTime, 1f);
            if(tank.timeToLive <= 0)
                tanksToDelete.add(tank);
            // todo let turrets rotate towards player
        }
        tanks.removeAll(tanksToDelete, true);
        Array<Jet> jetsToDelete = new Array<>();
        for(Jet jet : jets) {
            jet.forward(deltaTime);
            jet.rotate(deltaTime, 10f);
            if(jet.timeToLive <= 0)
                jetsToDelete.add(jet);
        }
        jets.removeAll(jetsToDelete, true);
        Array<Rocket> rocketsToDelete = new Array<>();
        for(Rocket r : rockets) {
            r.forward(deltaTime);
            if(r.timeToLive <= 0)
                rocketsToDelete.add(r);
        }
        rockets.removeAll(rocketsToDelete, true);

        Array<Debris> debrisToDelete = new Array<>();
        for(Debris r : debris) {
            r.forward(deltaTime);
            if(r.timeToLive <= 0)
                debrisToDelete.add(r);
        }
        debris.removeAll(debrisToDelete, true);
        populate(terrain, tanks, jets, buildings, debris);
    }

    private void blowUp(Vector3 position){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        for(int i = 0; i < 12; i++){
            float az = (float)Math.random()*360f;
            axis.setToRandomDirection();

            vel.set((float)Math.sin(az), 1f + (float)Math.random(), (float)Math.cos(az)).nor();
            debris.add(new Debris(position, vel, axis));
        }

    }

    private final Vector3 intersection = new Vector3();

    public boolean weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        float tankRadius = tankModel.nodes.get(0).parts.get(0).meshPart.radius;
        float jetRadius = jetModel.nodes.get(0).parts.get(0).meshPart.radius;
        for(Tank tank : tanks) {
            if(Intersector.intersectRaySphere(ray, tank.position, tankRadius, intersection))
                return true;
        }
        for(Jet jet : jets) {
            if(Intersector.intersectRaySphere(ray, jet.position, jetRadius, intersection))
                return true;
        }
        return false;
    }

    /** does a rocket hit any enemy? If so return the number of points earned, otherwise zero */
    public int rocketHits(){
        float tankRadius = tankModel.nodes.get(0).parts.get(0).meshPart.radius;
        float jetRadius = 2f* jetModel.nodes.get(0).parts.get(0).meshPart.radius;
        for(Rocket r : rockets) {
            for (Tank tank : tanks) {
                if (r.position.dst(tank.position) < tankRadius) {
                    r.timeToLive = 0;
                    tank.timeToLive = 0;
                    blowUp(tank.position);
                    return 100;
                }
            }
            for (Jet jet : jets) {
                if (r.position.dst(jet.position) < jetRadius) {
                    r.timeToLive = 0;
                    jet.timeToLive = 0;
                    blowUp(jet.position);
                    return 500;
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
        tankModel.dispose();
        tankTurretModel.dispose();
    }
}
