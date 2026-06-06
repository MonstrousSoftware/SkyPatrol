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
import com.badlogic.gdx.utils.Array;

public class WireFrameBuilder {

    // could be improved by merging triangles into quads

    public static class Line {
        short v1, v2;

        public Line(short v1, short v2) {
            // always place smallest first, to have equivalence regardless of line direction
            if(v1 < v2) {
                this.v1 = v1;
                this.v2 = v2;
            } else {
                this.v1 = v2;
                this.v2 = v1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line other = (Line)o;
            return other.v1 == v1 && other.v2 == v2;
        }
    }

    public static Model makeWireFrame( Node node, Color color ){

        Array<Line> lines = new Array<>();

        Mesh mesh = node.parts.get(0).meshPart.mesh;
        short[] indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);
        float[] vertices = new float[mesh.getMaxVertices() * mesh.getVertexSize()];
        mesh.getVertices(vertices);
        int posOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / Float.BYTES;
        int norOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / Float.BYTES;


        Material mat = new Material(ColorAttribute.createDiffuse(color));
        // add normals to each vertex in order to let the shader do back face culling per line
        int vattr = VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_LINES, vattr, mat);

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

        for(int i = 0; i < indices.length; i += 3) {
            //meshBuilder.triangle(indices[i], indices[i + 1], indices[i + 2]);
            addLine(lines, indices[i], indices[i + 1]);
            addLine(lines, indices[i+1], indices[i + 2]);
            addLine(lines, indices[i+2], indices[i]);
        }

        for(int i = 0; i < lines.size; i++)
            meshBuilder.line(lines.get(i).v1, lines.get(i).v2);


        return modelBuilder.end();
    }

    private static void addLine(Array<Line> lines, short v1, short v2){
        Line line = new Line(v1, v2);
        // if a line is already in the list, don't add it and remove the existing one.
        // because if the same line appears twice in the mesh it is a polygon internal line.
        // (when we compare lines the order of the vertices is not relevant,i.e. [v1,v2] will match
        // with [v2,v1])
        if(lines.contains(line, false)){
            lines.removeValue(line, false);
        } else {
            lines.add(line);
        }
    }
}
