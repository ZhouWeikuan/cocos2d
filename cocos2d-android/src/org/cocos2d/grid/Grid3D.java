package org.cocos2d.grid;

import org.cocos2d.types.CCGridSize;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCQuad3;
import org.cocos2d.types.CCVertex3D;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Grid3D extends GridBase {
    FloatBuffer texCoordinates;
    FloatBuffer vertices;
    FloatBuffer originalVertices;
    ShortBuffer indices;

    public Grid3D(CCGridSize gSize) {
        super(gSize);
        calculateVertexPoints();
    }

    public void blit(GL10 gl) {
        int n = getGridWidth() * getGridHeight();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates);
        gl.glDrawElements(GL10.GL_TRIANGLES, n * 6, GL10.GL_UNSIGNED_SHORT, indices);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private void calculateVertexPoints() {
        float width = (float) texture.pixelsWide();
        float height = (float) texture.pixelsHigh();

        int x, y, i;

        ByteBuffer vfb = ByteBuffer.allocateDirect(CCQuad3.size * (getGridWidth() + 1) * (getGridHeight() + 1) * 4);
        vfb.order(ByteOrder.nativeOrder());
        vertices = vfb.asFloatBuffer();

        ByteBuffer ofb = ByteBuffer.allocateDirect(CCQuad3.size * (getGridWidth() + 1) * (getGridHeight() + 1) * 4);
        ofb.order(ByteOrder.nativeOrder());
        originalVertices = ofb.asFloatBuffer();

        ByteBuffer tfb = ByteBuffer.allocateDirect(2 * (getGridWidth() + 1) * (getGridHeight() + 1) * 4);
        tfb.order(ByteOrder.nativeOrder());
        texCoordinates = tfb.asFloatBuffer();

        ByteBuffer isb = ByteBuffer.allocateDirect(6 * (getGridWidth() + 1) * (getGridHeight() + 1) * 2);
        isb.order(ByteOrder.nativeOrder());
        indices = isb.asShortBuffer();

        for (y = 0; y < (getGridHeight() + 1); y++) {
            for (x = 0; x < (getGridWidth() + 1); x++) {
                int idx = (y * (getGridWidth() + 1)) + x;

                vertices.put(idx * 3 + 0, -1);
                vertices.put(idx * 3 + 1, -1);
                vertices.put(idx * 3 + 2, -1);
                vertices.put(idx * 2 + 0, -1);
                vertices.put(idx * 2 + 1, -1);
            }
        }
        vertices.position(0);

        for (x = 0; x < getGridWidth(); x++) {
            for (y = 0; y < getGridHeight(); y++) {
                int idx = (y * getGridWidth()) + x;

                float x1 = x * step.x;
                float x2 = x1 + step.x;
                float y1 = y * step.y;
                float y2 = y1 + step.y;

                short a = (short) (x * (getGridHeight() + 1) + y);
                short b = (short) ((x + 1) * (getGridHeight() + 1) + y);
                short c = (short) ((x + 1) * (getGridHeight() + 1) + (y + 1));
                short d = (short) (x * (getGridHeight() + 1) + (y + 1));

                short[] tempidx = {a, b, d, b, c, d};

                indices.put(tempidx, 6 * idx, 6);

                int[] l1 = {a * 3, b * 3, c * 3, d * 3};
                CCVertex3D e = new CCVertex3D(x1, y1, 0);
                CCVertex3D f = new CCVertex3D(x2, y1, 0);
                CCVertex3D g = new CCVertex3D(x2, y2, 0);
                CCVertex3D h = new CCVertex3D(x1, y2, 0);

                CCVertex3D[] l2 = {e, f, g, h};

                int[] tex1 = {a * 2, b * 2, c * 2, d * 2};
                CCPoint[] tex2 = {CCPoint.ccp(x1, y1), CCPoint.ccp(x2, y1), CCPoint.ccp(x2, y2), CCPoint.ccp(x1, y2)};

                for (i = 0; i < 4; i++) {
                    vertices.put(l1[i] + 0, l2[i].x);
                    vertices.put(l1[i] + 1, l2[i].y);
                    vertices.put(l1[i] + 2, l2[i].z);

                    texCoordinates.put(tex1[i] + 0, tex2[i].x / width);
                    texCoordinates.put(tex1[i] + 1, tex2[i].y / height);
                }
            }
        }
        vertices.position(0);
        texCoordinates.position(0);

        originalVertices.put(vertices);
        originalVertices.position(0);
    }

    public CCVertex3D vertex(CCGridSize pos) {
        int index = (pos.x * (getGridHeight() + 1) + pos.y) * 3;
        CCVertex3D vert = new CCVertex3D(vertices.get(index + 0), vertices.get(index + 1), vertices.get(index + 2));

        return vert;
    }

    public CCVertex3D originalVertex(CCGridSize pos) {
        int index = (pos.x * (getGridHeight() + 1) + pos.y) * 3;
        CCVertex3D vert = new CCVertex3D(originalVertices.get(index + 0), originalVertices.get(index + 1), originalVertices.get(index + 2));

        return vert;
    }

    public void setVertex(CCGridSize pos, CCVertex3D vertex) {
        int index = (pos.x * (getGridHeight() + 1) + pos.y) * 3;
        vertices.put(index + 0, vertex.x);
        vertices.put(index + 1, vertex.y);
        vertices.put(index + 2, vertex.z);
    }

    @Override
    public void reuse() {
        if (reuseGrid > 0) {
//            memcpy(originalVertices, vertices, (getGridWidth()+1)*(getGridHeight()+1)*sizeof(CCVertex3D));
            reuseGrid--;
        }
    }

}
