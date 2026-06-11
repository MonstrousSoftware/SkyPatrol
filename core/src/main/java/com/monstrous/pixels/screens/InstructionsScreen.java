package com.monstrous.pixels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class InstructionsScreen extends RetroScreen {

    private SpriteBatch batch;
    private Color background;
    private List<String> lines;
    String text;

    public InstructionsScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0,0, LOWRES_WIDTH, LOWRES_HEIGHT);

        background = Color.BLUE;

        FileHandle file = Gdx.files.internal("instructions.txt");
        text = file.readString();

    }

    @Override
    public void renderFrame(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            game.setScreen(new StartScreen(game));
            return;
        }


        ScreenUtils.clear(background, false);

        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, text, 10, 200);
        font.draw(batch, "PRESS 1 TO CONTINUE", 90, 10);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
