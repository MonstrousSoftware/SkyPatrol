package com.monstrous.pixels.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.monstrous.pixels.WireFrameBuilder;
import com.monstrous.pixels.world.ECS.ComponentMapper;
import com.monstrous.pixels.world.ECS.ComponentType;
import com.monstrous.pixels.world.ECS.Engine;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<ModelInstance> instances;
    private final Terrain terrain;
    private final Vector3 tmpVec = new Vector3();
    private final Vector3 tmpVec2 = new Vector3();
    private float rocketCoolDown = 0;
    public float rocketFireDelay = 2f;  // repeat rate
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
    private final ColliderComponent helicopterCollider;
    private final Sound soundRocketFlyBy;
    private final Color background = new Color();
    public final Engine engine;
    private final RenderSystem renderSystem;
    private final ColliderSystem colliderSystem;
    private final ProjectileSystem projectileSystem;
    private final FiringSystem firingSystem;
    private final ComponentMapper<ProjectileComponent> projMap;
    private final ComponentMapper<AgeComponent> ageMap;
    private final ComponentMapper<DynamicsComponent> dynMap;
    public Vector3 cameraPosition;

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

        instances = new Array<>();

        //  used to signify the player was hit
        helicopterCollider = new ColliderComponent(-1, Vector3.Zero, 1, Color.GREEN, helicopterType);

        soundRocketFlyBy = Gdx.audio.newSound(Gdx.files.internal("sound/rocket-flyby.wav"));


        engine = new Engine();

        engine.addSystem(renderSystem = new RenderSystem(engine), false);
        engine.addSystem(new DynamicsSystem(engine), true);
        engine.addSystem(new SpinSystem(engine), true);
        engine.addSystem(new AgeSystem(engine), true);
        firingSystem = new FiringSystem(engine, this);
        engine.addSystem(firingSystem, true);
        engine.addSystem(projectileSystem = new ProjectileSystem(engine), true);
        engine.addSystem(colliderSystem = new ColliderSystem(engine), true);

        projMap = engine.componentManager.getComponentMapper(ProjectileComponent.class);
        ageMap = engine.componentManager.getComponentMapper(AgeComponent.class);
        dynMap = engine.componentManager.getComponentMapper(DynamicsComponent.class);
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

        engine.clear();

        if(level == 1)
            background.set(0.0f, 0.2f, 0.1f, 1.0f); // greenish
        else
            background.set(MathUtils.random()*0.3f, MathUtils.random()*0.3f, MathUtils.random()*0.3f, 1.0f);

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
            addWatermelon(new Vector3(x, 8, z));   // add height
        }
        System.out.println("Number of entities: "+engine.entityManager.count());
        //generateInstances(camera);
    }


    public void addBuilding(Vector3 position, Vector3 direction){
        int entityId = engine.createEntity();
        engine.addComponent(entityId, new RenderComponent(new ModelInstance(buildingType.model, position)));
        engine.commit(entityId);
        // e.id in RenderComponent is no longer needed
        //System.out.println("Building "+entityId);
    }

    public void addTower(Vector3 position, Vector3 direction){
        int entityId = engine.createEntity();
        engine.addComponent(entityId, new RenderComponent(new ModelInstance(towerType.model, position)));
        engine.commit(entityId);
    }

    public void addTank(Vector3 position, Vector3 velocity){
        int entityId = engine.createEntity();
        int entityId2 = engine.createEntity();
        engine.addComponent(entityId, new RenderComponent(new ModelInstance(tankType.model, position)));
        engine.addComponent(entityId, new DynamicsComponent(position, velocity, tankType.turnSpeed, 0));
        engine.addComponent(entityId, new AgeComponent(Float.MAX_VALUE, entityId2));
        engine.addComponent(entityId, new ColliderComponent( entityId, position, tankType.radius, Color.GREEN, tankType));
        engine.commit(entityId);

        engine.addComponent(entityId2, new RenderComponent(new ModelInstance(tankTurretType.model, position)));
        engine.addComponent(entityId2, new DynamicsComponent( position, velocity, tankType.turnSpeed, 0));
        engine.addComponent(entityId2, new SpinComponent(Vector3.Z, Vector3.Y, 0));
        engine.addComponent(entityId2, new AgeComponent(Float.MAX_VALUE, entityId));
        engine.addComponent(entityId2, new ColliderComponent(entityId2, position, tankType.radius, Color.GREEN, tankType));
        engine.addComponent(entityId2, new FiringComponent(tankTurretType));
        engine.commit(entityId2);


    }

    public void addJet(Vector3 position, Vector3 velocity){
        int entityId = engine.createEntity();
        engine.addComponent(entityId, new RenderComponent(new ModelInstance(jetType.model, position)));
        engine.addComponent(entityId, new DynamicsComponent(position, velocity, jetType.turnSpeed, 0));
        engine.addComponent(entityId, new AgeComponent(Float.MAX_VALUE));
        engine.addComponent(entityId, new ColliderComponent(entityId, position, jetType.radius, Color.CYAN, jetType));
        engine.addComponent(entityId, new FiringComponent(jetType));
        engine.commit(entityId);
    }

    public void addFriendlyRocket(Vector3 position, Vector3 velocity, ColliderComponent targetCollider){

        DynamicsComponent target = null;
        if(targetCollider != null)
            target = engine.componentManager.getComponentMapper(DynamicsComponent.class).get(targetCollider.id);

        int entityId = engine.createEntity();
        engine.createComponent(entityId, RenderComponent.class).set(new ModelInstance(rocketType.model, position));
        engine.createComponent(entityId, DynamicsComponent.class).set(position, velocity, 0, rocketType.gravity);
        engine.createComponent(entityId, AgeComponent.class).set(rocketType.timeToLive);
        engine.createComponent(entityId, ProjectileComponent.class).set(position, true, target);
        engine.commit(entityId);
        System.out.println("Rocket "+entityId);
    }

    public void addEnemyRocket(Vector3 position, Vector3 velocity){

        int entityId = engine.createEntity();
        engine.createComponent(entityId, RenderComponent.class).set(new ModelInstance(enemyRocketType.model, position));
        engine.createComponent(entityId, DynamicsComponent.class).set(position, velocity, 0, enemyRocketType.gravity);
        engine.createComponent(entityId, AgeComponent.class).set(enemyRocketType.timeToLive);
        engine.createComponent(entityId, ProjectileComponent.class).set(position, false, null);
//
//        engine.addComponent(entityId, new DynamicsComponent(position, velocity, 0, enemyRocketType.gravity));
//        engine.addComponent(entityId, new AgeComponent(enemyRocketType.timeToLive));
//        engine.addComponent(entityId, new ProjectileComponent(position, false, null));
        engine.commit(entityId);
    }

    public void addDebris(Vector3 position, Vector3 velocity, Vector3 spinAxis){
        int entityId = engine.createEntity();
        engine.createComponent(entityId, RenderComponent.class).set(new ModelInstance(debrisType.model, position));
        engine.createComponent(entityId, DynamicsComponent.class).set(position, velocity, 0, debrisType.gravity);
        engine.createComponent(entityId, AgeComponent.class).set(debrisType.timeToLive);
        engine.createComponent(entityId, SpinComponent.class).set(Vector3.Z, spinAxis, debrisType.spinSpeed);

//        engine.addComponent(entityId, new RenderComponent(new ModelInstance(debrisType.model, position)));
//        engine.addComponent(entityId, new DynamicsComponent(position, velocity, 0, debrisType.gravity));
//        engine.addComponent(entityId, new SpinComponent(Vector3.Z, spinAxis, debrisType.spinSpeed));
//        engine.addComponent(entityId, new AgeComponent(debrisType.timeToLive));
        engine.commit(entityId);
    }

    public void addWatermelon(Vector3 position){
        int entityId = engine.createEntity();
        engine.addComponent(entityId, new RenderComponent(new ModelInstance(watermelonType.model, position)));
        engine.addComponent(entityId, new SpinComponent(Vector3.Z, Vector3.Y, watermelonType.spinSpeed));
        engine.addComponent(entityId, new ColliderComponent(entityId, position, watermelonType.radius, Color.GREEN, watermelonType));
        engine.addComponent(entityId, new AgeComponent(Float.MAX_VALUE));
        engine.commit(entityId);
        System.out.println("Water melon "+entityId);
    }

    public int enemyCount(){
        return firingSystem.numEntities();   // number of shooters
    }

    private void generateInstances(Camera camera){
        instances.clear();
        instances.add(terrain.instance);
        renderSystem.update(camera, instances);
    }

    /**
     * player fires rocket in camera's forward direction. Target may be null.
     */
    public boolean fireRocket(Camera cam, ColliderComponent target){
        if(rocketCoolDown <= 0) {
            tmpVec.set(cam.position).add(new Vector3(0, -0.3f, 0)); // initial position is just below camera position
            tmpVec2.set(cam.direction).scl(rocketType.speed);    // velocity vector
            addFriendlyRocket( tmpVec, tmpVec2, target);
            rocketCoolDown = rocketFireDelay;
            return true;
        }
        return false;
    }


    public void update( float deltaTime, Camera camera ){
        this.cameraPosition = camera.position;       // used by firing system

        rocketCoolDown -= deltaTime;
        engine.update(deltaTime);
        generateInstances(camera);
    }

    Vector3 vel = new Vector3();
    Vector3 axis = new Vector3();
    Vector3 position = new Vector3();

    private void blowUp(ColliderComponent colliderComponent){
        System.out.println("Blow up "+colliderComponent.id);
        if(!engine.entityManager.isAlive(colliderComponent.id))
            return;

        ageMap.get(colliderComponent.id).isDead = true;
        position.set(colliderComponent.position);

        // make debris model match colour of destroyed object
        debrisType.model.materials.get(0).set(ColorAttribute.createDiffuse(colliderComponent.color));

        for(int i = 0; i < 12; i++){    // pieces of debris
            float az = (float)Math.random()*360f; // XZ direction
            axis.setToRandomDirection();    // random spin axis

            vel.set((float)Math.sin(az), 2f + (float)Math.random(), (float)Math.cos(az)).nor().scl(debrisType.speed);
            addDebris(position, vel, axis);
        }
    }

    /** Use ray casting to check if there is an enemy directly in front. Returns null if not */
    public ColliderComponent weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);

        return colliderSystem.intersect(ray);
    }

    /** does a rocket hit any enemy? If so return the object that was hit, otherwise null.
     * If the player was hit, we return the helicopter object. */
    public ColliderComponent rocketHits(Camera camera) {

        IntArray projectiles = projectileSystem.getEntities();
        for(int i = 0; i < projectiles.size; i++) {
            int entityId = projectiles.get(i);
            ProjectileComponent projectileComponent = projMap.get(entityId);

            if (projectileComponent.friendly) {
                ColliderComponent colliderComponent = colliderSystem.intersectPoint(projectileComponent.position);
                if(colliderComponent != null) {
                    ageMap.get(entityId).isDead = true;
                    System.out.println("Rocket hits, mark rocket as dead "+entityId);
                    blowUp(colliderComponent);          // blow up enemy
                    return colliderComponent;
              }
            } else { // enemy rocket
                // if the enemy rocket is close enough, play the rocket sound
                if (!projectileComponent.isMakingSound) {

                    float dist = camera.position.dst(projectileComponent.position);
                    if (dist < 100f) {
                        projectileComponent.isMakingSound = true;
                        soundRocketFlyBy.play();
                    }
                }
                // if the enemy rocket is very close, take damage or die
                if (projectileComponent.position.dst(camera.position) < 1.0f) {    // is size of hitbox
                    Vector3 vel = dynMap.get(entityId).velocity;
                    float dot = vel.dot(camera.direction);
                    //System.out.println("rocket angle: "+dot);
                    if (dot < -0.5f) {   // rockets from behind will always miss
                        ageMap.get(entityId).isDead = true;
                        return helicopterCollider;  // signifies the player was hit
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
