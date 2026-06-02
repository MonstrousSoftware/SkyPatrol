package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.pixels.WireFramerBuilder;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<Tank> tanks;
    private final Array<ModelInstance> instances;
    private final Model tankBody;
    private final Model tankTurret;


    public World() {
        tanks = new Array<>();
        tanks.add(new Tank(new Vector3(0,0,0), new Vector3(1,0,0)));
        tanks.add(new Tank(new Vector3(28,0,0), new Vector3(0, 0, 1)));


        // load a gltf file
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/tank.gltf"));

        // turn model into a wireframe model
        tankBody = WireFramerBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankBody"));
        tankTurret = WireFramerBuilder.makeWireFrame(sceneAsset.scene.model.getNode("TankTurret"));

        instances = new Array<>();

        populate(tanks);
    }

    private void populate(Array<Tank> tanks){
        ModelInstance instance;

        instances.clear();
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
        populate(tanks);
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
