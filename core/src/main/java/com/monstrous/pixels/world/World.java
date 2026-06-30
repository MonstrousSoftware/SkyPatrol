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

import java.util.HashMap;
import java.util.Map;

public class World implements Disposable {

//    private final Array<GameObject> gameObjects;
//    private final Array<GameObject> enemies;            // subset of gameObjects: all enemies
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

//        gameObjects = new Array<>();
//        enemies = new Array<>();  // subset

        instances = new Array<>();

        //  used to signify the player was hit
        helicopterCollider = new ColliderComponent(0, Vector3.Zero, 1, Color.GREEN, helicopterType);

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

        removeAllEntities();

        if(level == 1)
            background.set(0.0f, 0.2f, 0.1f, 1.0f); // greenish
        else
            background.set(MathUtils.random()*0.3f, MathUtils.random()*0.3f, MathUtils.random()*0.3f, 1.0f);

        //gameObjects.clear();
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
        generateInstances();
    }

    public Map<Integer, RenderComponent> renderComponentMap = new HashMap<>();
    public Map<Integer, DynamicsComponent> dynamicsComponentMap = new HashMap<>();
    public Map<Integer, SpinComponent> spinComponentMap = new HashMap<>();
    public Map<Integer, AgeComponent> ageComponentMap = new HashMap<>();
    public Map<Integer, ColliderComponent> colliderComponentMap = new HashMap<>();
    public Map<Integer, ProjectileComponent> projectileComponentMap = new HashMap<>();
    public Map<Integer, FiringComponent> firingComponentMap = new HashMap<>();
    private int nextId = 1;

    public int getEntityId(){
        return nextId++;
    }

    private void removeEntity(int id){
        // all components apart from ageComponent
        renderComponentMap.remove(id);
        dynamicsComponentMap.remove(id);
        spinComponentMap.remove(id);
        colliderComponentMap.remove(id);
        projectileComponentMap.remove(id);
        firingComponentMap.remove(id);
    }

    private void removeAllEntities(){
        renderComponentMap.clear();
        dynamicsComponentMap.clear();
        spinComponentMap.clear();
        colliderComponentMap.clear();
        projectileComponentMap.clear();
        firingComponentMap.clear();
        ageComponentMap.clear();
    }

    public void addBuilding(Vector3 position, Vector3 direction){
        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(buildingType.model, position));
        renderComponentMap.put(id, renderComponent);
    }

    public void addTower(Vector3 position, Vector3 direction){

        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(towerType.model, position));
        renderComponentMap.put(id, renderComponent);
    }

    public void addTank(Vector3 position, Vector3 velocity){

        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(tankType.model, position));
        renderComponentMap.put(id, renderComponent);
        DynamicsComponent dynamicsComponent = new DynamicsComponent(id, position, velocity, tankType.turnSpeed, 0);
        dynamicsComponentMap.put(id, dynamicsComponent);
        AgeComponent ageComponent = new AgeComponent(id, Float.MAX_VALUE);
        ageComponentMap.put(id, ageComponent);
        Material mat = tankType.model.materials.get(0);
        Color diffuse = ((ColorAttribute)(mat.get(ColorAttribute.Diffuse))).color;
        ColliderComponent colliderComponent = new ColliderComponent(id, position, tankType.radius, diffuse, tankType);
        colliderComponentMap.put(id, colliderComponent);

        int id2 = getEntityId();
        renderComponent = new RenderComponent(id2, new ModelInstance(tankTurretType.model, position));
        renderComponentMap.put(id2, renderComponent);
        dynamicsComponent = new DynamicsComponent(id2, position, velocity, tankType.turnSpeed, 0);
        dynamicsComponentMap.put(id2, dynamicsComponent);
        SpinComponent spinComponent = new SpinComponent(id2, Vector3.Z, Vector3.Y, 0);
        spinComponentMap.put(id2, spinComponent);
        AgeComponent ageComponent2 = new AgeComponent(id2, Float.MAX_VALUE);
        ageComponent.partner = ageComponent2;   // link the lifetime of tank & turret so they get destroyed together
        ageComponent2.partner = ageComponent;
        ageComponentMap.put(id2, ageComponent2);
        FiringComponent firingComponent = new FiringComponent(id2, tankTurretType);
        firingComponentMap.put(id2, firingComponent);

    }

    public void addJet(Vector3 position, Vector3 velocity){
        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(jetType.model, position));
        renderComponentMap.put(id, renderComponent);
        DynamicsComponent dynamicsComponent = new DynamicsComponent(id, position, velocity, jetType.turnSpeed, 0);
        dynamicsComponentMap.put(id, dynamicsComponent);
        AgeComponent ageComponent = new AgeComponent(id, Float.MAX_VALUE);
        ageComponentMap.put(id, ageComponent);
        Material mat = jetType.model.materials.get(0);
        Color diffuse = ((ColorAttribute)(mat.get(ColorAttribute.Diffuse))).color;
        ColliderComponent colliderComponent = new ColliderComponent(id, position, jetType.radius, diffuse, jetType);
        colliderComponentMap.put(id, colliderComponent);
        FiringComponent firingComponent = new FiringComponent(id, jetType);
        firingComponentMap.put(id, firingComponent);
    }

    public void addFriendlyRocket(Vector3 position, Vector3 velocity, ColliderComponent targetCollider){
        DynamicsComponent target = null;
        if(targetCollider != null)
            target = dynamicsComponentMap.get(targetCollider.id);

        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(rocketType.model, position));
        renderComponentMap.put(id, renderComponent);
        DynamicsComponent dynamicsComponent = new DynamicsComponent(id, position, velocity, 0, rocketType.gravity);
        dynamicsComponentMap.put(id, dynamicsComponent);
        AgeComponent ageComponent = new AgeComponent(id, rocketType.timeToLive);
        ageComponentMap.put(id, ageComponent);
        ProjectileComponent projectileComponent = new ProjectileComponent(id, position, true);
        projectileComponent.target = target;
        projectileComponentMap.put(id, projectileComponent);
    }

    public void addEnemyRocket(Vector3 position, Vector3 velocity){
        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(enemyRocketType.model, position));
        renderComponentMap.put(id, renderComponent);
        DynamicsComponent dynamicsComponent = new DynamicsComponent(id, position, velocity, 0, enemyRocketType.gravity);
        dynamicsComponentMap.put(id, dynamicsComponent);
        AgeComponent ageComponent = new AgeComponent(id, enemyRocketType.timeToLive);
        ageComponentMap.put(id, ageComponent);
        ProjectileComponent projectileComponent = new ProjectileComponent(id, position, false);
        projectileComponentMap.put(id, projectileComponent);
    }

    public void addDebris(Vector3 position, Vector3 velocity, Vector3 spinAxis){
        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(debrisType.model, position));
        renderComponentMap.put(id, renderComponent);
        DynamicsComponent dynamicsComponent = new DynamicsComponent(id, position, velocity, 0, debrisType.gravity);
        dynamicsComponentMap.put(id, dynamicsComponent);
        SpinComponent spinComponent = new SpinComponent(id, Vector3.Z, spinAxis, debrisType.spinSpeed);
        spinComponentMap.put(id, spinComponent);
        AgeComponent ageComponent = new AgeComponent(id, debrisType.timeToLive);
        ageComponentMap.put(id, ageComponent);
    }

    public void addWatermelon(Vector3 position){
        int id = getEntityId();
        RenderComponent renderComponent = new RenderComponent(id, new ModelInstance(watermelonType.model, position));
        renderComponentMap.put(id, renderComponent);
        SpinComponent spinComponent = new SpinComponent(id, Vector3.Z, Vector3.Y, watermelonType.spinSpeed);
        spinComponentMap.put(id, spinComponent);
        Material mat = watermelonType.model.materials.get(0);
        Color diffuse = ((ColorAttribute)(mat.get(ColorAttribute.Diffuse))).color;
        ColliderComponent colliderComponent = new ColliderComponent(id, position, watermelonType.radius, diffuse, watermelonType);
        colliderComponentMap.put(id, colliderComponent);
        AgeComponent ageComponent = new AgeComponent(id, Float.MAX_VALUE);
        ageComponentMap.put(id, ageComponent);
    }

    public int enemyCount(){
        return firingComponentMap.size();
    }

    private void generateInstances(){
        instances.clear();
        instances.add(terrain.instance);
        for(Integer id : renderComponentMap.keySet()) {
            RenderSystem.update(renderComponentMap.get(id), dynamicsComponentMap.get(id), spinComponentMap.get(id), instances);
        }
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


    public void update( float deltaTime, Vector3 cameraPosition ){
        rocketCoolDown -= deltaTime;

        DynamicsSystem.update(dynamicsComponentMap.values(), ageComponentMap, deltaTime);
        SpinSystem.update(spinComponentMap.values(), deltaTime);
        AgeSystem.update(ageComponentMap.values(), deltaTime);  // todo reaping

        for(Integer id : colliderComponentMap.keySet()) {
            DynamicsComponent dyn = dynamicsComponentMap.get(id);
            if(dyn != null) // static objects could also have collider
                ColliderSystem.update(colliderComponentMap.get(id), dyn);
        }
        for(Integer id : projectileComponentMap.keySet()) {
            DynamicsComponent dyn = dynamicsComponentMap.get(id);
            ProjectileSystem.update(projectileComponentMap.get(id), dyn);
        }

        grimReaper();

        enemyFire(cameraPosition, deltaTime);

        generateInstances();
    }

    private void enemyFire(Vector3 cameraPosition, float deltaTime){
        for(FiringComponent firingComponent : firingComponentMap.values()){

            if(firingComponent.type == tankTurretType) {
                // make turret point towards camera (instantly)
                SpinComponent spin = spinComponentMap.get(firingComponent.id);
                DynamicsComponent dyn = dynamicsComponentMap.get(firingComponent.id);
                spin.forward.set(cameraPosition).sub(dyn.position).scl(1, 0, 1).nor();

                firingComponent.timeToFire -= deltaTime;
                if (firingComponent.timeToFire < 0) {
                    firingComponent.timeToFire = (float) Math.random() * 10f;

                    tmpVec2.set(spin.forward);
                    tmpVec2.y += 0.2f;  // shoot slightly up
                    tmpVec2.nor();
                    tmpVec2.scl(rocketType.speed);

                    addEnemyRocket(tmpVec.set(dyn.position).add(new Vector3(0, 1.5f, 0)), tmpVec2);
                }
            }

            if(firingComponent.type == jetType) {
                // if jet is heading more or less towards the player (dot product is close to 1)
                // and cool down period is expired, then fire a rocket
                firingComponent.timeToFire -= deltaTime;
                if (firingComponent.timeToFire < 0) {
                    DynamicsComponent dyn = dynamicsComponentMap.get(firingComponent.id);
                    // work out unit vector to player in the horizontal plane
                    tmpVec2.set(cameraPosition).sub(dyn.position).scl(1, 0, 1).nor();
                    tmpVec.set(dyn.velocity).nor();  // normalized velocity
                    if( tmpVec.dot(tmpVec2) > 0.9f) {     // jet is heading more or less towards player
                        firingComponent.timeToFire = (float) Math.random() * 3f;
                        tmpVec2.scl(rocketType.speed);
                        addEnemyRocket(tmpVec.set(dyn.position).add(new Vector3(0, -1.5f, 0)), tmpVec2);
                    }
                }
            }
        }
    }

    private final Array<AgeComponent> removalList = new Array<>();

    private void grimReaper(){
        removalList.clear();
        for(AgeComponent ageComponent : ageComponentMap.values()){
            if(ageComponent.isDead){
                removeEntity(ageComponent.id);
                removalList.add(ageComponent);
            }
        }
        for(AgeComponent ageComponent : removalList){
            ageComponentMap.remove(ageComponent.id);
        }
    }




    private void blowUp(ColliderComponent colliderComponent){
        Vector3 vel = new Vector3();
        Vector3 axis = new Vector3();

        ageComponentMap.get((colliderComponent.id)).isDead = true;

        for(int i = 0; i < 12; i++){    // pieces of debris
            float az = (float)Math.random()*360f; // XZ direction
            axis.setToRandomDirection();    // random spin axis

            vel.set((float)Math.sin(az), 2f + (float)Math.random(), (float)Math.cos(az)).nor().scl(debrisType.speed);
            addDebris(colliderComponent.position, vel, axis);
        }
        // make debris model match colour of destroyed object
        debrisType.model.materials.get(0).set(ColorAttribute.createDiffuse(colliderComponent.color));

    }

    private final Vector3 intersection = new Vector3();

    /** Use ray casting to check if there is an enemy directly in front. Returns null if not */
    public ColliderComponent weaponLocked(Camera cam){
        Ray ray = new Ray(cam.position, cam.direction);
        for(ColliderComponent colliderComponent: colliderComponentMap.values()){
            if (Intersector.intersectRaySphere(ray, colliderComponent.position, colliderComponent.radius, intersection)) {
                return colliderComponent;
            }
        }
        return null;
    }

    /** does a rocket hit any enemy? If so return the object that was hit, otherwise null.
     * If the player was hit, we return the helicopter object. */
    public ColliderComponent rocketHits(Camera camera) {
        for (ProjectileComponent projectileComponent : projectileComponentMap.values()) {
            if (projectileComponent.friendly) {
                for (ColliderComponent colliderComponent : colliderComponentMap.values()) {
                    if (projectileComponent.position.dst(colliderComponent.position) < colliderComponent.radius) {
                        ageComponentMap.get((projectileComponent.id)).isDead = true;
                        //projectileComponent.isDead = true;    // delete rocket
                        // if you hit a turret, return the parent tank
//                        if(t.parent != null)
//                            t = t.parent;
                        blowUp(colliderComponent);          // blow up enemy
                        return colliderComponent;
                    }
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
                    Vector3 vel = dynamicsComponentMap.get(projectileComponent.id).velocity;
                    float dot = vel.dot(camera.direction);
                    //System.out.println("rocket angle: "+dot);
                    if (dot < -0.5f) {   // rockets from behind will always miss
                        ageComponentMap.get((projectileComponent.id)).isDead = true;
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
