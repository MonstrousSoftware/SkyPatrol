package com.monstrous.pixels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class WireFrameBuilder {

    // could be improved by merging triangles into quads

    public static Model makeWireFrame( Node node, Color color ){


        Mesh mesh = node.parts.get(0).meshPart.mesh;
        short[] indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);
        float[] vertices = new float[mesh.getMaxVertices() * mesh.getVertexSize()];
        mesh.getVertices(vertices);
        int posOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / Float.BYTES;
        int norOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / Float.BYTES;


        Material mat = new Material(ColorAttribute.createDiffuse(color));
        int vattr = VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_LINES, vattr, mat);
        //meshBuilder.cone(5, 5, 5, 10);

        MeshPartBuilder.VertexInfo vert = new MeshPartBuilder.VertexInfo();
        vert.hasPosition = true;
        vert.hasColor = true;
        vert.hasNormal = true;
        vert.hasUV = false;

        int stride = mesh.getVertexSize()/Float.BYTES;
        for(int i = 0; i < vertices.length; i += stride) {
            vert.position.set(vertices[i + posOffset], vertices[i + posOffset + 1], vertices[i + posOffset + 2]);
            vert.normal.set(vertices[i + norOffset], vertices[i + norOffset + 1], vertices[i + norOffset + 2]);
            vert.color.set(color);
            meshBuilder.vertex(vert);
        }

        for(int i = 0; i < indices.length; i += 3)
            meshBuilder.triangle(indices[i], indices[i+1], indices[i+2]);


        Model model = modelBuilder.end();
        return model;
    }
}
