package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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

    private final Array<Tank> tanks;
    private final Array<Jet> jets;
    private final Array<Building> buildings;
    private final Array<Rocket> rockets;
    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Model tankBody;
    private final Model tankTurret;
    private final Model building;
//    private final Model aaBody;
//    private final Model aaTurret;
    private final Model jet;
    private final Model rocket;
    private final Vector3 tmpVec = new Vector3();
    private float coolDown = 0;


    public World(Camera cam) {
        tanks = new Array<>();
        tanks.add(new Tank(new Vector3(0,0,0), new Vector3(1,0,0)));
        tanks.add(new Tank(new Vector3(28,0,0), new Vector3(0, 0, 1)));

        jets = new Array<>();
        jets.add(new Jet(new Vector3(0,18,60), new Vector3(1,0,0)));
        jets.add(new Jet(new Vector3(68,22,0), new Vector3(0, 0, 1)));

        buildings = new Array<>();
        buildings.add(new Building(new Vector3(10,0,0), new Vector3(1,0,1)));
        buildings.add(new Building(new Vector3(38,0,0), new Vector3(-1, 0, -1)));

        rockets = new Array<>();


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        tankBody = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"));
        tankTurret = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"));
        building = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Building"));
        rocket = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Rocket"));
        jet = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Jet"));


        terrain = new Terrain();

        instances = new Array<>();

        populate(terrain, tanks, jets, buildings);
    }

    private void populate(Terrain terrain, Array<Tank> tanks, Array<Jet> jets, Array<Building> buildings){
        ModelInstance instance;

        instances.clear();
        instances.add(terrain.instance);

        for(Building b : buildings) {
            instance = new ModelInstance(building, b.position);
            instance.transform.rotate(Vector3.Z, b.direction);
            instances.add(instance);
        }

        for(Tank tank : tanks) {
            instance = new ModelInstance(tankBody, tank.position);
            instance.transform.rotate(Vector3.Z, tank.direction);
            instances.add(instance);
            instance = new ModelInstance(tankTurret, tank.position);
            instance.transform.rotate(Vector3.Z, tank.direction);
            instances.add(instance);
        }

        for(Jet j : jets) {
            instance = new ModelInstance(jet, j.position);
            instance.transform.rotate(Vector3.Z, j.direction);
            instances.add(instance);
        }

        for(Rocket r : rockets) {
            instance = new ModelInstance(rocket, r.position);
            instance.transform.rotate(Vector3.Z, r.direction);
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
        for(Tank tank : tanks) {
            tank.forward(deltaTime);
            tank.rotate(deltaTime, 1f);
            // todo let turrets rotate towards player
        }
        for(Jet jet : jets) {
            jet.forward(deltaTime);
            jet.rotate(deltaTime, 1f);
        }
        Array<Rocket> toDelete = new Array<>();
        for(Rocket r : rockets) {
            r.forward(deltaTime);
            if(r.timeToLive <= 0)
                toDelete.add(r);
        }
        rockets.removeAll(toDelete, true);
        populate(terrain, tanks, jets, buildings);
    }

    private final Vector3 intersection = new Vector3();

    public boolean weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        float tankRadius = tankBody.nodes.get(0).parts.get(0).meshPart.radius;
        float jetRadius = jet.nodes.get(0).parts.get(0).meshPart.radius;
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

    public boolean rocketHits(){
        float tankRadius = tankBody.nodes.get(0).parts.get(0).meshPart.radius;
        float jetRadius = jet.nodes.get(0).parts.get(0).meshPart.radius;
        for(Rocket r : rockets) {
            for (Tank tank : tanks) {
                if (r.position.dst(tank.position) < tankRadius) {
                    r.timeToLive = 0;
                    return true;
                }
            }
            for (Jet jet : jets) {
                if (r.position.dst(jet.position) < jetRadius) {
                    r.timeToLive = 0;
                    return true;
                }
            }
        }
        return false;
    }

    public Array<ModelInstance> getInstances(){
        return instances;
    }

    @Override
    public void dispose() {
        tankBody.dispose();
        tankTurret.dispose();
    }
}
