package org.cocos2d.nodes;

import org.cocos2d.opengl.Texture2D;
import org.cocos2d.types.*;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * A ribbon is a dynamically generated list of polygons drawn as a single or series
 * of triangle strips. The primary use of Ribbon is as the drawing class of Motion Streak,
 * but it is quite useful on it's own. When manually drawing a ribbon, you can call addPointAt
 * and pass in the parameters for the next location in the ribbon. The system will automatically
 * generate new polygons, texture them accourding to your texture width, etc, etc.
 * <p/>
 * Ribbon data is stored in a RibbonSegment class. This class statically allocates enough verticies and
 * texture coordinates for 50 locations (100 verts or 48 triangles). The ribbon class will allocate
 * new segments when they are needed, and reuse old ones if available. The idea is to avoid constantly
 * allocating new memory and prefer a more static method. However, since there is no way to determine
 * the maximum size of some ribbons (motion streaks), a truely static allocation is not possible.
 *
 * @since v0.8.1
 */
public class Ribbon extends CocosNode {

    /**
     * object to hold ribbon segment data
     */
    public static class RibbonSegment {

        private static final int COUNT = 50;

        float[] verts = new float[COUNT * 3 * 2];
        float[] coords = new float[COUNT * 2 * 2];
        byte[] colors = new byte[COUNT * 4 * 2];
        float[] creationTime = new float[COUNT];
        boolean finished;
        int end;
        int begin;


        FloatBuffer mVertices;
        FloatBuffer mCoordinates;
        ByteBuffer mColors;


        public RibbonSegment() {
            reset();

            ByteBuffer vfb = ByteBuffer.allocateDirect(COUNT * 3 * 2 * 4);
            vfb.order(ByteOrder.nativeOrder());
            mVertices = vfb.asFloatBuffer();

            ByteBuffer tfb = ByteBuffer.allocateDirect(COUNT * 2 * 2 * 4);
            tfb.order(ByteOrder.nativeOrder());
            mCoordinates = tfb.asFloatBuffer();

            ByteBuffer cbb = ByteBuffer.allocateDirect(COUNT * 4 * 2 * 1);
            mColors = cbb;

        }

        public void reset() {
            end = 0;
            begin = 0;
            finished = false;
        }

        public void draw(GL10 gl, float curTime, float fadeTime, CCColor4B color) {
            int r = color.r;
            int g = color.g;
            int b = color.b;
            int a = color.a;

            if (begin < 50) {
                // the motion streak class will call update and cause time to change, thus, if mCurTime != 0
                // we have to generate alpha for the ribbon each frame.
                if (curTime == 0) {
                    // no alpha over time, so just set the color
                    gl.glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
                } else {
                    // generate alpha/color for each point
                    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                    int i = begin;
                    for (; i < end; ++i) {
                        int idx = i * 8;
                        colors[idx + 0] = (byte) r;
                        colors[idx + 1] = (byte) g;
                        colors[idx + 2] = (byte) b;
                        colors[idx + 4] = (byte) r;
                        colors[idx + 5] = (byte) g;
                        colors[idx + 6] = (byte) b;
                        float alive = ((curTime - creationTime[i]) / fadeTime);
                        if (alive > 1) {
                            begin++;
                            colors[idx + 3] = 0;
                            colors[idx + 7] = 0;
                        } else {
                            byte o = (byte) (255.f - (alive * 255.f));
                            colors[idx + 3] = o;
                            colors[idx + 7] = o;
                        }
                    }
                    mColors.put(colors, begin * 4 * 2, (end - begin) * 4 * 2);
                    mColors.position(0);

                    gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColors);
                }

                mVertices.put(verts, begin * 3 * 2, (end - begin) * 3 * 2);
                mVertices.position(0);

                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices);

                mCoordinates.put(coords, begin * 2 * 2, (end - begin) * 2 * 2);
                mCoordinates.position(0);

                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCoordinates);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (end - begin) * 2);
            } else
                finished = true;
        }

    }

    ArrayList<RibbonSegment> mSegments;
    ArrayList<RibbonSegment> dSegments;

    CCPoint mLastPoint1;
    CCPoint mLastPoint2;
    CCPoint mLastLocation;
    int mVertCount;
    float mTexVPos;
    float mCurTime;
    float mFadeTime;
    float mDelta;
    float mLastWidth;
    float mLastSign;
    boolean mPastFirstPoint;

    // Texture used
    Texture2D texture_;

    // texture lenght
    float textureLength_;

    // RGBA protocol
    CCColor4B color_;

    // blend func
    CCBlendFunc blendFunc_;

    /**
     * creates the ribbon
     */
    public Ribbon(float w, String path, float l, CCColor4B color, float fade) {

        mSegments = new ArrayList<RibbonSegment>();
        dSegments = new ArrayList<RibbonSegment>();

        /* 1 initial segment */
        RibbonSegment seg = new RibbonSegment();
        mSegments.add(seg);

        textureLength_ = l;

        color_ = color;
        mFadeTime = fade;
        mLastLocation = CCPoint.make(0, 0);
        mLastWidth = w / 2;
        mTexVPos = 0.0f;

        mCurTime = 0;
        mPastFirstPoint = false;

        /* XXX:
         Ribbon, by default uses this blend function, which might not be correct
         if you are using premultiplied alpha images,
         but 99% you might want to use this blending function regarding of the texture
         */
        blendFunc_ = new CCBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        texture_ = TextureManager.sharedTextureManager().addImage(path);

        /* default texture parameter */
        CCTexParams params = new CCTexParams(GL10.GL_LINEAR, GL10.GL_LINEAR, GL10.GL_REPEAT, GL10.GL_REPEAT);
        texture_.setTexParameters(params);
    }

    // rotates a point around 0, 0
    private CCPoint rotatePoint(CCPoint vec, float a) {
        float xtemp = (vec.x * (float)Math.cos(a)) - (vec.y * (float)Math.sin(a));
        vec.y = (vec.x * (float)Math.sin(a)) + (vec.y * (float)Math.cos(a));
        vec.x = xtemp;
        return vec;
    }

    /**
     * add a point to the ribbon
     */
    public void addPoint(CCPoint location, float w) {
        w = w * 0.5f;
        // if this is the first point added, cache it and return
        if (!mPastFirstPoint) {
            mLastWidth = w;
            mLastLocation = location;
            mPastFirstPoint = true;
            return;
        }

        CCPoint sub = CCPoint.ccpSub(mLastLocation, location);
        float r = CCPoint.ccpToAngle(sub) + (float) Math.PI * 2;
        CCPoint p1 = CCPoint.ccpAdd(rotatePoint(CCPoint.ccp(-w, 0), r), location);
        CCPoint p2 = CCPoint.ccpAdd(rotatePoint(CCPoint.ccp(w, 0), r), location);
        float len = (float) Math.sqrt((float) Math.pow(mLastLocation.x - location.x, 2) + (float) Math.pow(mLastLocation.y - location.y, 2));
        float tend = mTexVPos + len / textureLength_;
        RibbonSegment seg;
        // grab last segment
        seg = mSegments.get(mSegments.size() - 1);
        // lets kill old segments
        for (RibbonSegment seg2 : mSegments) {
            if (seg2 != seg && seg2.finished) {
                dSegments.add(seg2);
            }
        }
        mSegments.removeAll(dSegments);
        // is the segment full?
        if (seg.end >= 50)
            mSegments.removeAll(dSegments);
        // grab last segment and appent to it if it's not full
        seg = mSegments.get(mSegments.size() - 1);
        // is the segment full?
        if (seg.end >= 50) {
            RibbonSegment newSeg;
            // grab it from the cache if we can
            if (dSegments.size() > 0) {
                newSeg = dSegments.get(0);
                dSegments.remove(newSeg);
                newSeg.reset();
            } else {
                newSeg = new RibbonSegment();
            }

            newSeg.creationTime[0] = seg.creationTime[seg.end - 1];
            int v = (seg.end - 1) * 6;
            int c = (seg.end - 1) * 4;
            newSeg.verts[0] = seg.verts[v];
            newSeg.verts[1] = seg.verts[v + 1];
            newSeg.verts[2] = seg.verts[v + 2];
            newSeg.verts[3] = seg.verts[v + 3];
            newSeg.verts[4] = seg.verts[v + 4];
            newSeg.verts[5] = seg.verts[v + 5];

            newSeg.coords[0] = seg.coords[c];
            newSeg.coords[1] = seg.coords[c + 1];
            newSeg.coords[2] = seg.coords[c + 2];
            newSeg.coords[3] = seg.coords[c + 3];
            newSeg.end++;
            seg = newSeg;
            mSegments.add(seg);
        }
        if (seg.end == 0) {
            // first edge has to get rotation from the first real polygon
            CCPoint lp1 = CCPoint.ccpAdd(rotatePoint(CCPoint.ccp(-mLastWidth, 0), r), mLastLocation);
            CCPoint lp2 = CCPoint.ccpAdd(rotatePoint(CCPoint.ccp(+mLastWidth, 0), r), mLastLocation);
            seg.creationTime[0] = mCurTime - mDelta;
            seg.verts[0] = lp1.x;
            seg.verts[1] = lp1.y;
            seg.verts[2] = 0.0f;
            seg.verts[3] = lp2.x;
            seg.verts[4] = lp2.y;
            seg.verts[5] = 0.0f;
            seg.coords[0] = 0.0f;
            seg.coords[1] = mTexVPos;
            seg.coords[2] = 1.0f;
            seg.coords[3] = mTexVPos;
            seg.end++;
        }

        int v = seg.end * 6;
        int c = seg.end * 4;
        // add new vertex
        seg.creationTime[seg.end] = mCurTime;
        seg.verts[v] = p1.x;
        seg.verts[v + 1] = p1.y;
        seg.verts[v + 2] = 0.0f;
        seg.verts[v + 3] = p2.x;
        seg.verts[v + 4] = p2.y;
        seg.verts[v + 5] = 0.0f;


        seg.coords[c] = 0.0f;
        seg.coords[c + 1] = tend;
        seg.coords[c + 2] = 1.0f;
        seg.coords[c + 3] = tend;

        mTexVPos = tend;
        mLastLocation = location;
        mLastPoint1 = p1;
        mLastPoint2 = p2;
        mLastWidth = w;
        seg.end++;
    }

    /**
     * polling function
     */
    public void update(float delta) {
        mCurTime += delta;
        mDelta = delta;
    }

    /**
     * determine side of line
     */
    public float sideOfLine(CCPoint p, CCPoint l1, CCPoint l2) {
        CCPoint vp = CCPoint.ccpPerp(CCPoint.ccpSub(l1, l2));
        CCPoint vx = CCPoint.ccpSub(p, l1);
        return CCPoint.ccpDot(vx, vp);
    }

    @Override
    public void draw(GL10 gl) {
        if (mSegments.size() > 0) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

            boolean newBlend = false;
            if (blendFunc_.src != CCMacros.CC_BLEND_SRC || blendFunc_.dst != CCMacros.CC_BLEND_DST) {
                newBlend = true;
                gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
            }

            for (RibbonSegment seg : mSegments)
                seg.draw(gl, mCurTime, mFadeTime, color_);

            if (newBlend)
                gl.glBlendFunc(CCMacros.CC_BLEND_SRC, CCMacros.CC_BLEND_DST);

            gl.glDisable(GL10.GL_TEXTURE_2D);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }

    public CCBlendFunc blendFunc() {
        return blendFunc_;
    }

    public void setBlendFunc(CCBlendFunc blendFunc) {
        blendFunc_ = blendFunc;
    }


    // CocosNodeTexture protocol

    public void setTexture(Texture2D texture) {
        setContentSize(texture.getWidth(), texture.getHeight());
        /* XXX Don't update blending function in Ribbons */
    }

    public Texture2D texture() {
        return texture_;
    }


}
