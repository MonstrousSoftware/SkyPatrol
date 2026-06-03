package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Terrain {

    public ModelInstance instance;

    public Terrain() {
        ModelBuilder builder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        int vattr = VertexAttributes.Usage.Position;
        Model model = builder.createLineGrid(250, 250, 15, 15, mat, vattr);
        instance = new ModelInstance(model);
    }
}
