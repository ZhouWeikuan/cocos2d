package org.cocos2d.nodes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CCTexParams;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.FastFloatBuffer;

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
public class CCRibbon extends CCNode {
    /** object to hold ribbon segment data */
    public static class CCRibbonSegment {

        private static final int COUNT = 50;

        float[] verts = new float[COUNT * 3 * 2];
        float[] coords = new float[COUNT * 2 * 2];
        float[] colors = new float[COUNT * 4 * 2];
        float[] creationTime = new float[COUNT];
        boolean finished;
        int end;
        int begin;

        FastFloatBuffer mVertices;
        FastFloatBuffer mCoordinates;
        FastFloatBuffer mColors;

        public CCRibbonSegment() {
            ByteBuffer vfb = ByteBuffer.allocateDirect(COUNT * 3 * 2 * 4);
            vfb.order(ByteOrder.nativeOrder());
            mVertices = FastFloatBuffer.createBuffer(vfb);

            ByteBuffer tfb = ByteBuffer.allocateDirect(COUNT * 2 * 2 * 4);
            tfb.order(ByteOrder.nativeOrder());
            mCoordinates = FastFloatBuffer.createBuffer(tfb);

            ByteBuffer cbb = ByteBuffer.allocateDirect(COUNT * 4 * 2 * 4);
            cbb.order(ByteOrder.nativeOrder());
            mColors = FastFloatBuffer.createBuffer(cbb);

            reset();
        }

        public void reset() {
            end = 0;
            begin = 0;
            finished = false;
        }

        public void draw(GL10 gl, float curTime, float fadeTime, ccColor4B color) {
            int r = color.r;
            int g = color.g;
            int b = color.b;
            int a = color.a;

            if (begin < COUNT) {
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
                        colors[idx + 0] = r / 255f;
                        colors[idx + 1] = g / 255f;
                        colors[idx + 2] = b / 255f;
                        colors[idx + 4] = r / 255f;
                        colors[idx + 5] = g / 255f;
                        colors[idx + 6] = b / 255f;
                        float alive = ((curTime - creationTime[i]) / fadeTime);
                        if (alive > 1) {
                            begin++;
                            colors[idx + 3] = 0;
                            colors[idx + 7] = 0;
                        } else {
                            float o = 1.0f - alive;
                            colors[idx + 3] = o;
                            colors[idx + 7] = o;
                        }
                    }
                    mColors.put(colors, begin * 4 * 2, (end - begin) * 4 * 2);
                    mColors.position(0);

                    gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColors.bytes);
                }

                mVertices.put(verts, begin * 3 * 2, (end - begin) * 3 * 2);
                mVertices.position(0);

                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices.bytes);

                mCoordinates.put(coords, begin * 2 * 2, (end - begin) * 2 * 2);
                mCoordinates.position(0);

                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCoordinates.bytes);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (end - begin) * 2);
            } else
                finished = true;
        }

    }

    ArrayList<CCRibbonSegment> segments_;
    ArrayList<CCRibbonSegment> deletedSegments_;

    CGPoint lastPoint1_;
    CGPoint lastPoint2_;
    CGPoint lastLocation_;
    int vertCount_;
    float texVPos_;
    float curTime_;
    float fadeTime_;
    float delta_;
    float lastWidth_;
    float lastSign_;
    boolean pastFirstPoint_;

    /** Texture used by the ribbon. Conforms to CCTextureProtocol protocol */
    CCTexture2D texture_;

    /** Texture lenghts in pixels */
    float textureLength_;

    /** color used by the Ribbon (RGBA) */
    ccColor4B color_;

    /** GL blendind function */
    ccBlendFunc blendFunc_;

    /** creates the ribbon */
    public static CCRibbon node(float w, String path, float l, ccColor4B color, float fade) {
        return new CCRibbon(w, path, l, color, fade);
    }

    /** init the ribbon */
    protected CCRibbon(float w, String path, float l, ccColor4B color, float fade) {

        segments_ = new ArrayList<CCRibbonSegment>();
        deletedSegments_ = new ArrayList<CCRibbonSegment>();

        /* 1 initial segment */
        CCRibbonSegment seg = new CCRibbonSegment();
        segments_.add(seg);

        textureLength_ = l;

        color_ = color;
        fadeTime_ = fade;
        lastLocation_ = CGPoint.make(0, 0);
        lastWidth_ = w / 2;
        texVPos_ = 0.0f;

        curTime_ = 0;
        pastFirstPoint_ = false;

        /* XXX:
         Ribbon, by default uses this blend function, which might not be correct
         if you are using premultiplied alpha images,
         but 99% you might want to use this blending function regarding of the texture
         */
        blendFunc_ = new ccBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        texture_ = CCTextureCache.sharedTextureCache().addImage(path);

        /* default texture parameter */
        CCTexParams params = new CCTexParams(GL10.GL_LINEAR, GL10.GL_LINEAR, GL10.GL_REPEAT, GL10.GL_REPEAT);
        texture_.setTexParameters(params);
    }

    // rotates a point around 0, 0
    private CGPoint rotatePoint(CGPoint vec, float a) {
        float xtemp = (vec.x * (float)Math.cos(a)) - (vec.y * (float)Math.sin(a));
        vec.y = (vec.x * (float)Math.sin(a)) + (vec.y * (float)Math.cos(a));
        vec.x = xtemp;
        return vec;
    }

    /**
     * add a point to the ribbon
     */
    public void addPoint(CGPoint location, float w) {
        w = w * 0.5f;
        // if this is the first point added, cache it and return
        if (!pastFirstPoint_) {
            lastWidth_ = w;
            lastLocation_ = location;
            pastFirstPoint_ = true;
            return;
        }

        CGPoint sub = CGPoint.ccpSub(lastLocation_, location);
        float r = CGPoint.ccpToAngle(sub) + (float) Math.PI * 2;
        CGPoint p1 = CGPoint.ccpAdd(rotatePoint(CGPoint.ccp(-w, 0), r), location);
        CGPoint p2 = CGPoint.ccpAdd(rotatePoint(CGPoint.ccp(w, 0), r), location);
        float len = (float) Math.sqrt((float) Math.pow(lastLocation_.x - location.x, 2) + (float) Math.pow(lastLocation_.y - location.y, 2));
        float tend = texVPos_ + len / textureLength_;
        CCRibbonSegment seg;
        // grab last segment
        seg = segments_.get(segments_.size() - 1);
        // lets kill old segments
        for (CCRibbonSegment seg2 : segments_) {
            if (seg2 != seg && seg2.finished) {
                deletedSegments_.add(seg2);
            }
        }
        segments_.removeAll(deletedSegments_);

        // is the segment full?
        if (seg.end >= 50)
            segments_.removeAll(deletedSegments_);
        // grab last segment and appent to it if it's not full
        seg = segments_.get(segments_.size() - 1);

        // is the segment full?
        if (seg.end >= 50) {
            CCRibbonSegment newSeg;
            // grab it from the cache if we can
            if (deletedSegments_.size() > 0) {
                newSeg = deletedSegments_.get(0);
                deletedSegments_.remove(newSeg);
                newSeg.reset();
            } else {
                newSeg = new CCRibbonSegment();
            }

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
            segments_.add(seg);
        }

        if (seg.end == 0) {
            // first edge has to get rotation from the first real polygon
            CGPoint lp1 = CGPoint.ccpAdd(rotatePoint(CGPoint.ccp(-lastWidth_, 0), r), lastLocation_);
            CGPoint lp2 = CGPoint.ccpAdd(rotatePoint(CGPoint.ccp(+lastWidth_, 0), r), lastLocation_);
            seg.creationTime[0] = curTime_ - delta_;
            seg.verts[0] = lp1.x;
            seg.verts[1] = lp1.y;
            seg.verts[2] = 0.0f;
            seg.verts[3] = lp2.x;
            seg.verts[4] = lp2.y;
            seg.verts[5] = 0.0f;
            seg.coords[0] = 0.0f;
            seg.coords[1] = texVPos_;
            seg.coords[2] = 1.0f;
            seg.coords[3] = texVPos_;
            seg.end++;
        }

        int v = seg.end * 6;
        int c = seg.end * 4;
        // add new vertex
        seg.creationTime[seg.end] = curTime_;
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

        texVPos_ = tend;
        lastLocation_ = location;
        lastPoint1_ = p1;
        lastPoint2_ = p2;
        lastWidth_ = w;
        seg.end++;
    }

    /** polling function */
    public void update(float delta) {
        curTime_ += delta;
        delta_ = delta;
    }

    /** determine side of line */
    public float sideOfLine(CGPoint p, CGPoint l1, CGPoint l2) {
        CGPoint vp = CGPoint.ccpPerp(CGPoint.ccpSub(l1, l2));
        CGPoint vx = CGPoint.ccpSub(p, l1);
        return CGPoint.ccpDot(vx, vp);
    }

    @Override
    public void draw(GL10 gl) {
        if (segments_.size() > 0) {
            // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
            // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
            // Unneeded states: GL_COLOR_ARRAY
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

            boolean newBlend = false;
            if (blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST) {
                newBlend = true;
                gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
            }

            for (CCRibbonSegment seg : segments_)
                seg.draw(gl, curTime_, fadeTime_, color_);

            if (newBlend)
                gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        }
    }

    public ccBlendFunc blendFunc() {
        return blendFunc_;
    }

    public void setBlendFunc(ccBlendFunc blendFunc) {
        blendFunc_ = blendFunc;
    }

    // CocosNodeTexture protocol

    public void setTexture(CCTexture2D texture) {
        texture_ = texture;
        setContentSize(texture.getContentSize());
        /* XXX Don't update blending function in Ribbons */
    }

    public CCTexture2D texture() {
        return texture_;
    }

}

