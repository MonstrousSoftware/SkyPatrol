package com.monstrous.pixels.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Terrain {

    public ModelInstance instance;
    public int rows = 250;
    public int rowSize = 15;
    public int worldSize = rows * rowSize;

    public Terrain() {
        ModelBuilder builder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        int vattr = VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal;
        Model model = builder.createLineGrid(rows, rows, rowSize, rowSize, mat, vattr);

        // set every vertex normal to (0,1,0)
        Mesh mesh = model.meshes.get(0);
        int nv = mesh.getNumVertices();
        int stride = mesh.getVertexSize()/Float.BYTES;
        float[] verts = new float[nv*stride];
        mesh.getVertices(verts);
        for(int v = 0; v < nv; v++){
            verts[v*stride + 3] = 0f;
            verts[v*stride + 4] = 1f;
            verts[v*stride + 5] = 0f;
        }
        mesh.setVertices(verts);
        instance = new ModelInstance(model);
    }
}
