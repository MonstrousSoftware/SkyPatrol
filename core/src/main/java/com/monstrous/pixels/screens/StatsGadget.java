package com.monstrous.pixels.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.monstrous.pixels.world.ECS.Engine;
import com.monstrous.pixels.world.ECS.EntitySystem;

/** Show a real-time histogram of frame times */
public class StatsGadget {
    private Engine engine;
    private float updateTimer;
    private final SpriteBatch batch;
    private int x,y;        // where to show graph
    private final BitmapFont font;
    private final StringBuilder sb;

    public StatsGadget(Engine engine) {
        this.engine = engine;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        updateTimer = 1f;
        sb = new StringBuilder();
    }

    public void update(float delta){
        // take average every 1 second
        updateTimer -= delta;
        if(updateTimer < 0) {
            sb.setLength(0);
            sb.append("Entities: ");
            sb.append(engine.entityManager.count());
            sb.append(" peak: ");
            sb.append(engine.entityManager.maxCount());
            sb.append("\n");
            Array<EntitySystem> systems = engine.getSystems();
            for(EntitySystem sys : systems){
                sb.append(" ");
                sb.append(sys.getLabel());
                sb.append(": ");
                sb.append(sys.numEntities());
            }
            updateTimer = 1.0f;
        }
    }

    public void render(){
        batch.begin();
        font.draw(batch, sb.toString(), x, y+20);
        batch.end();
    }

    public void resize(int width, int height){
        batch.getProjectionMatrix().setToOrtho2D(0,0, width, height);
        x = 0;
        y = 300;
    }
}
