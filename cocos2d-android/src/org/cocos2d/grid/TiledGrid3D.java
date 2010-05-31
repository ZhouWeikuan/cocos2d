package org.cocos2d.grid;

import org.cocos2d.types.CCGridSize;
import org.cocos2d.types.CCQuad2;
import org.cocos2d.types.CCQuad3;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class TiledGrid3D extends GridBase {
    FloatBuffer texCoordinates;
    FloatBuffer vertices;
    FloatBuffer originalVertices;
    ShortBuffer indices;

    public TiledGrid3D(CCGridSize gSize) {
        super(gSize);
        calculateVertexPoints();

    }

    public void blit(GL10 gl) {
        int n = gridSize.x * gridSize.y;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates);
        gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void calculateVertexPoints() {
        float width = (float) texture.pixelsWide();
        float height = (float) texture.pixelsHigh();

        int numQuads = gridSize.x * gridSize.y;

        ByteBuffer vfb = ByteBuffer.allocateDirect(CCQuad3.size * numQuads * 4);
        vfb.order(ByteOrder.nativeOrder());
        vertices = vfb.asFloatBuffer();

        ByteBuffer ofb = ByteBuffer.allocateDirect(CCQuad3.size * numQuads * 4);
        ofb.order(ByteOrder.nativeOrder());
        originalVertices = ofb.asFloatBuffer();

        ByteBuffer tfb = ByteBuffer.allocateDirect(CCQuad2.size * numQuads * 4);
        tfb.order(ByteOrder.nativeOrder());
        texCoordinates = tfb.asFloatBuffer();

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * numQuads * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();

        for (int x = 0; x < gridSize.x; x++) {
            for (int y = 0; y < gridSize.y; y++) {
                float x1 = x * step.x;
                float x2 = x1 + step.x;
                float y1 = y * step.y;
                float y2 = y1 + step.y;

                vertices.put(x1);
                vertices.put(y1);
                vertices.put(0);
                vertices.put(x2);
                vertices.put(y1);
                vertices.put(0);
                vertices.put(x1);
                vertices.put(y2);
                vertices.put(0);
                vertices.put(x2);
                vertices.put(y2);
                vertices.put(0);

                texCoordinates.put(x1 / width);
                texCoordinates.put(y1 / height);
                texCoordinates.put(x2 / width);
                texCoordinates.put(y1 / height);
                texCoordinates.put(x1 / width);
                texCoordinates.put(y2 / height);
                texCoordinates.put(x2 / width);
                texCoordinates.put(y2 / height);
            }
        }
        vertices.position(0);
        texCoordinates.position(0);

        for (int x = 0; x < numQuads; x++) {
            indices.put(x * 6 + 0, (short) (x * 4 + 0));
            indices.put(x * 6 + 1, (short) (x * 4 + 1));
            indices.put(x * 6 + 2, (short) (x * 4 + 2));

            indices.put(x * 6 + 3, (short) (x * 4 + 1));
            indices.put(x * 6 + 4, (short) (x * 4 + 2));
            indices.put(x * 6 + 5, (short) (x * 4 + 3));
        }

        originalVertices.put(vertices);
        originalVertices.position(0);
    }

    public void setTile(CCGridSize pos, CCQuad3 coords) {
        int idx = (gridSize.y * pos.x + pos.y) * CCQuad3.size;
        float[] vertArray = coords.ccQuad3();
        for (int i = 0; i < CCQuad3.size; i++) {
            vertices.put(idx + i, vertArray[i]);
        }
        vertices.position(0);
    }

    public CCQuad3 originalTile(CCGridSize pos) {
        int idx = (gridSize.y * pos.x + pos.y) * CCQuad3.size;

        float[] vertArray = new float[CCQuad3.size];
        for (int i = 0; i < CCQuad3.size; i++) {
            vertArray[i] = originalVertices.get(idx + i);
        }

        return new CCQuad3(vertArray);
    }

    public CCQuad3 tile(CCGridSize pos) {
        int idx = (gridSize.y * pos.x + pos.y) * CCQuad3.size;

        float[] vertArray = new float[CCQuad3.size];
        for (int i = 0; i < CCQuad3.size; i++) {
            vertArray[i] = vertices.get(idx + i);
        }

        return new CCQuad3(vertArray);
    }

    public void reuse() {
        if (reuseGrid > 0) {
            int numQuads = gridSize.x * gridSize.y;

//            memcpy(originalVertices, vertices, numQuads*12*sizeof(float));
            reuseGrid--;
        }
    }

}
