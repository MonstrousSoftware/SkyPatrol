package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.pixels.WireFrameBuilder;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<Tank> tanks;
    private final Array<Building> buildings;
    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Model tankBody;
    private final Model tankTurret;
    private final Model building;
//    private final Model aaBody;
//    private final Model aaTurret;
//    private final Model jet;
//    private final Model rocket;


    public World() {
        tanks = new Array<>();
        tanks.add(new Tank(new Vector3(0,0,0), new Vector3(1,0,0)));
        tanks.add(new Tank(new Vector3(28,0,0), new Vector3(0, 0, 1)));

        buildings = new Array<>();
        buildings.add(new Building(new Vector3(10,0,0), new Vector3(1,0,1)));
        buildings.add(new Building(new Vector3(38,0,0), new Vector3(-1, 0, -1)));


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/skypatrol.gltf"));

        // turn model into a wireframe model
        tankBody = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"));
        tankTurret = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"));
        building = WireFrameBuilder.makeWireFrame(sceneAsset.scene.model.getNode("Building"));


        terrain = new Terrain();

        instances = new Array<>();

        populate(terrain, tanks, buildings);
    }

    private void populate(Terrain terrain, Array<Tank> tanks, Array<Building> buildings){
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
    }

    public void update( float deltaTime ){
        for(Tank tank : tanks) {
            tank.forward(deltaTime);
            tank.rotate(deltaTime, 1f);
        }
        populate(terrain, tanks, buildings);
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
