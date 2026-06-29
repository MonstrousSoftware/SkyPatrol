package com.monstrous.pixels.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Show a real-time histogram of frame times */
public class FrameRateGadget {
    private final static float GRAPH_HEIGHT = 50f;   // scale in pixels for average frame time
    private final static int NUM_SAMPLES = 500;      // equals graph width

    private final float[] samples;
    private int writeIndex;
    private float sampleSum;
    private int sampleCount;
    private float updateTimer;
    private String fps;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private int x,y;        // where to show graph
    private float scale;    // height scale (dynamic)
    private final BitmapFont font;

    public FrameRateGadget() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        samples = new float[NUM_SAMPLES];
        writeIndex = 0;
        scale = 2000f;
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        fps = "-";
        updateTimer = 1f;
    }

    public void update(float delta){
        samples[writeIndex] = delta;
        sampleSum += delta;
        sampleCount++;
        writeIndex++;
        if(writeIndex == NUM_SAMPLES) {
            writeIndex = 0;
        }
        // take average every 1 second
        updateTimer -= delta;
        if(updateTimer < 0) {
            float avgFPS = sampleCount/sampleSum;
            fps = "FPS: "+ Math.round(avgFPS);
            scale = GRAPH_HEIGHT / (sampleSum/sampleCount);
            sampleSum = 0;
            sampleCount = 0;
            updateTimer = 1.0f;
        }
    }

    public void render(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for(int i = 0; i < NUM_SAMPLES; i++)
            if(i != writeIndex)
                shapeRenderer.line(x+i, y, x+i, y+scale*samples[i]);
        shapeRenderer.end();
        batch.begin();
        font.draw(batch, fps, x, y+20);
        batch.end();
    }

    public void resize(int width, int height){
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0,0, width, height);
        batch.getProjectionMatrix().setToOrtho2D(0,0, width, height);
        x = 0;
        y = 0;
    }
}
