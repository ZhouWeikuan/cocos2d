package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.opengl.Texture2D;
import org.cocos2d.types.CCColorF;
import static org.cocos2d.types.CCMacros.*;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCPointSprite;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class ParticleSystem extends CocosNode {

    public static class Particle {
        CCPoint pos = new CCPoint();
        CCPoint startPos = new CCPoint();
        CCPoint dir = new CCPoint();
        float radialAccel;
        float tangentialAccel;
        CCColorF color = new CCColorF();
        CCColorF deltaColor = new CCColorF();
        float size;
        float life;
    }

    protected int id;

    // is the particle system active ?
    protected boolean active;

    // duration in seconds of the system. -1 is infinity
    protected float duration;

    // time elapsed since the start of the system (in seconds)
    protected float elapsed;

    /// Gravity of the particles
    protected CCPoint gravity = CCPoint.zero();

    // position is from "superclass" CocosNode
    // Emitter source position
    protected CCPoint source = CCPoint.zero();

    // Position variance
    protected CCPoint posVar = CCPoint.zero();

    // The angle (direction) of the particles measured in degrees
    protected float angle;
    // Angle variance measured in degrees;
    protected float angleVar;

    // The speed the particles will have.
    protected float speed;
    // The speed variance
    protected float speedVar;

    // Tangential acceleration
    protected float tangentialAccel;

    // Tangential acceleration variance
    protected float tangentialAccelVar;

    // Radial acceleration
    protected float radialAccel;

    // Radial acceleration variance
    protected float radialAccelVar;

    // Size of the particles
    protected float size;

    // Size variance
    protected float sizeVar;

    // How many seconds will the particle live
    protected float life;
    // Life variance
    protected float lifeVar;

    // Start color of the particles
    protected CCColorF startColor = new CCColorF();

    // Start color variance
    protected CCColorF startColorVar = new CCColorF();

    // End color of the particles
    protected CCColorF endColor = new CCColorF();

    // End color variance
    protected CCColorF endColorVar = new CCColorF();

    // Array of particles
    protected Particle particles[];

    // Maximum particles
    protected int totalParticles;

    // Count of active particles
    protected int particleCount;

    // additive color or blend
    protected boolean blendAdditive;
    // color modulate
    protected boolean colorModulate;

    // How many particles can be emitted per second
    protected float emissionRate;
    protected float emitCounter;

    // Texture of the particles
    protected Texture2D texture;

    // Array of (x,y,size,color)
    CCPointSprite vertices[];

    // Array of colors
    //CCColorF	colors[];

    // Array of pointsizes
    //float pointsizes[];

    // vertices buffer id
    protected int verticesID = -1;

    // colors buffer id
    protected int colorsID;

    //  particle idx
    protected int particleIdx;

    /**
     * Is the emitter active
     */

    public boolean getActive() {
        return active;
    }

    /**
     * Quantity of particles that are being simulated at the moment
     */

    public int getParticleCount() {
        return particleCount;
    }

    /**
     * Gravity value
     */

    public CCPoint getGravity() {
        return gravity;
    }

    public void setGravity(CCPoint gravity) {
        this.gravity = gravity;
    }

    /**
     * How many seconds the emitter wil run. -1 means 'forever'
     */

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Source location of particles respective to emitter location
     */

    public CCPoint getSource() {
        return source;
    }

    public void setSource(CCPoint source) {
        this.source = source;
    }

    /**
     * Position variance of the emitter
     */
    public CCPoint getPosVar() {
        return posVar;
    }


    /**
     * life, and life variation of each particle
     */
    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

//    /** life variance of each particle */
//    protected float lifeVar;
//    /** angle and angle variation of each particle */
//    protected float angle;
//    /** angle variance of each particle */
//    protected float angleVar;
//    /** speed of each particle */
//    protected float speed;
//    /** speed variance of each particle */
//    protected float speedVar;
//    /** tangential acceleration of each particle */
//    protected float tangentialAccel;
//    /** tangential acceleration variance of each particle */
//    protected float tangentialAccelVar;
//    /** radial acceleration of each particle */
//    protected float radialAccel;
//    /** radial acceleration variance of each particle */
//    protected float radialAccelVar;
//    /** size in pixels of each particle */
//    protected float size;
//    /** size variance in pixels of each particle */
//    protected float sizeVar;
//    /** start color of each particle */
//    protected CCColorF startColor;
//    /** start color variance of each particle */
//    protected CCColorF startColorVar;
//    /** end color and end color variation of each particle */
//    protected CCColorF endColor;
//    /** end color variance of each particle */
//    protected CCColorF endColorVar;
//    /** emission rate of the particles */
//    protected float emissionRate;
//    /** maximum particles of the system */
//    protected int totalParticles;

    public static final int kPositionTypeFree = 1;
    public static final int kPositionTypeGrouped = 2;
    
    // movement type: free or grouped
    private	int positionType_;

    public int getPositionType() {
        return positionType_;
    }

    public void setPositionType(int type) {
        positionType_ = type;
    }

    
    /**
     * texture used to render the particles
     */

    public Texture2D getTexture() {
        return texture;
    }

    public void setTexture(Texture2D tex) {
        this.texture = tex;
    }


    private FloatBuffer mVertices;
    private FloatBuffer mPointSizes;
    private FloatBuffer mColors;


    //! Initializes a system with a fixed number of particles
    protected ParticleSystem(int numberOfParticles) {
        totalParticles = numberOfParticles;

        particles = new Particle[totalParticles];
        vertices = new CCPointSprite[totalParticles];


        for (int i = 0; i < totalParticles; i++) {
            particles[i] = new Particle();
            vertices[i]  = new CCPointSprite();
        }

        // default, active
        active = true;

        // default: additive
        blendAdditive = false;

        // default: modulate
        //colorModulate = true;

        ByteBuffer vfb = ByteBuffer.allocateDirect(4 * 2 * totalParticles);
        vfb.order(ByteOrder.nativeOrder());
        mVertices = vfb.asFloatBuffer();

        ByteBuffer sfb = ByteBuffer.allocateDirect(4 * 1 * totalParticles);
        sfb.order(ByteOrder.nativeOrder());
        mPointSizes = sfb.asFloatBuffer();

        ByteBuffer cfb = ByteBuffer.allocateDirect(4 * 4 * totalParticles);
        cfb.order(ByteOrder.nativeOrder());
        mColors = cfb.asFloatBuffer();

        // default movement type;
        positionType_ = kPositionTypeFree;

        schedule("step");

    }

    //! Add a particle to the emitter
    public boolean addParticle() {
        if (isFull())
            return false;

        Particle particle = particles[particleCount];

        initParticle(particle);
        particleCount++;

        return true;

    }

    private void initParticle(Particle particle) {
        CCPoint v = CCPoint.zero();

        // position
        particle.pos.x = (int) (source.x + posVar.x * CCRANDOM_MINUS1_1());
        particle.pos.y = (int) (source.y + posVar.y * CCRANDOM_MINUS1_1());

        // direction
        float a = CC_DEGREES_TO_RADIANS(angle + angleVar * CCRANDOM_MINUS1_1());
        v.y = (float)Math.sin(a);
        v.x = (float)Math.cos(a);
        float s = speed + speedVar * CCRANDOM_MINUS1_1();
        particle.dir = CCPoint.ccpMult(v, s);

        // radial accel
        particle.radialAccel = radialAccel + radialAccelVar * CCRANDOM_MINUS1_1();

        // tangential accel
        particle.tangentialAccel = tangentialAccel + tangentialAccelVar * CCRANDOM_MINUS1_1();

        // life
        particle.life = life + lifeVar * CCRANDOM_MINUS1_1();

        // Color
        CCColorF start = new CCColorF();
        start.r = startColor.r + startColorVar.r * CCRANDOM_MINUS1_1();
        start.g = startColor.g + startColorVar.g * CCRANDOM_MINUS1_1();
        start.b = startColor.b + startColorVar.b * CCRANDOM_MINUS1_1();
        start.a = startColor.a + startColorVar.a * CCRANDOM_MINUS1_1();

        CCColorF end = new CCColorF();
        end.r = endColor.r + endColorVar.r * CCRANDOM_MINUS1_1();
        end.g = endColor.g + endColorVar.g * CCRANDOM_MINUS1_1();
        end.b = endColor.b + endColorVar.b * CCRANDOM_MINUS1_1();
        end.a = endColor.a + endColorVar.a * CCRANDOM_MINUS1_1();

        particle.color = start;
        particle.deltaColor.r = (end.r - start.r) / particle.life;
        particle.deltaColor.g = (end.g - start.g) / particle.life;
        particle.deltaColor.b = (end.b - start.b) / particle.life;
        particle.deltaColor.a = (end.a - start.a) / particle.life;

        // size
        particle.size = size + sizeVar * CCRANDOM_MINUS1_1();

        // position
        if( positionType_ == kPositionTypeFree ) {
            particle.startPos = convertToWorldSpace(0, 0);
        } else {
            particle.startPos = CCPoint.make(getPositionX(), getPositionY());
        }

    }


    //! Initializes a particle
//    public ParticleSystem(Particle particle)
//    {
//    }

    public void step(float dt) {
        if (active && emissionRate != 0) {
            float rate = 1.0f / emissionRate;
            emitCounter += dt;
            while (particleCount < totalParticles && emitCounter > rate) {
                addParticle();
                emitCounter -= rate;
            }

            elapsed += dt;
            if (duration != -1 && duration < elapsed)
                stopSystem();
        }

        particleIdx = 0;

        while (particleIdx < particleCount) {
            Particle p = particles[particleIdx];

            if (p.life > 0) {

                CCPoint tmp, radial, tangential;

                radial = CCPoint.zero();
                // radial acceleration
                if (p.pos.x != 9 || p.pos.y != 0)
                    radial = CCPoint.ccpNormalize(p.pos);
                tangential = radial;
                radial = CCPoint.ccpMult(radial, p.radialAccel);

                // tangential acceleration
                float newy = tangential.x;
                tangential.x = -tangential.y;
                tangential.y = newy;
                tangential = CCPoint.ccpMult(tangential, p.tangentialAccel);

                // (gravity + radial + tangential) * dt
                tmp = CCPoint.ccpAdd(CCPoint.ccpAdd(radial, tangential), gravity);
                tmp = CCPoint.ccpMult(tmp, dt);
                p.dir = CCPoint.ccpAdd(p.dir, tmp);
                tmp = CCPoint.ccpMult(p.dir, dt);
                p.pos = CCPoint.ccpAdd(p.pos, tmp);

                p.color.r += (p.deltaColor.r * dt);
                p.color.g += (p.deltaColor.g * dt);
                p.color.b += (p.deltaColor.b * dt);
                p.color.a += (p.deltaColor.a * dt);

                p.life -= dt;

                // place vertices and colos in array
                vertices[particleIdx].x = p.pos.x;
                vertices[particleIdx].y = p.pos.y;

                // TODO: Remove when glPointSizePointerOES is fixed
                vertices[particleIdx].size = p.size;
                vertices[particleIdx].colors = new CCColorF(p.color);

                // update particle counter
                particleIdx++;

            } else {
                // life < 0
                if (particleIdx != particleCount - 1)
                    particles[particleIdx] = particles[particleCount - 1];
                particleCount--;
            }
        }
    }


    //! stop emitting particles. Running particles will continue to run until they die
    public void stopSystem() {
        active = false;
        elapsed = duration;
        emitCounter = 0;
    }

    //! Kill all living particles.
    public void resetSystem() {
        active = true;
        elapsed = 0;
        for (particleIdx = 0; particleIdx < particleCount; ++particleIdx) {
            Particle p = particles[particleIdx];
            p.life = 0;
        }
    }

    public void draw(GL10 gl) {
        if (verticesID <= 0) {
            int name[] = new int[1];
            ((GL11) gl).glGenBuffers(1, name, 0);
            ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID);
            // TODO: Remove when glPointSizePointerOES is fixed
//            gl.glBufferData(GL_ARRAY_BUFFER, 4*7*totalParticles, vertices, GL_DYNAMIC_DRAW);
            ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        }

        gl.glEnable(GL_TEXTURE_2D);
        gl.glBindTexture(GL_TEXTURE_2D, texture.name());

        gl.glEnable(GL11.GL_POINT_SPRITE_OES);
        ((GL11) gl).glTexEnvi(GL11.GL_POINT_SPRITE_OES, GL11.GL_COORD_REPLACE_OES, GL10.GL_TRUE);

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID);

        for (int i = 0; i < totalParticles; i++) {
            mVertices.put(vertices[i].x);
            mVertices.put(vertices[i].y);
            mPointSizes.put(vertices[i].size);
            mColors.put(vertices[i].colors.r);
            mColors.put(vertices[i].colors.g);
            mColors.put(vertices[i].colors.b);
            mColors.put(vertices[i].colors.a);
        }
        mVertices.position(0);
        mPointSizes.position(0);
        mColors.position(0);

        // TODO: Remove when glPointSizePointerOES is fixed
        //gl.glVertexPointer(2, GL_FLOAT, 7*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, 0, mVertices);

        gl.glEnableClientState(GL11.GL_POINT_SIZE_ARRAY_OES);
        // TODO: Remove when glPointSizePointerOES is fixed
        //gl.glPointSizePointerOES(GL_FLOAT, 7*4, 4*2);
        ((GL11) gl).glPointSizePointerOES(GL_FLOAT, 0, mPointSizes);

        gl.glEnableClientState(GL_COLOR_ARRAY);
        // TODO: Remove when glPointSizePointerOES is fixed
        //gl.glColorPointer(4, GL_FLOAT, 7*4, 4*3);
        gl.glColorPointer(4, GL_FLOAT, 0, mColors);

        if (blendAdditive)
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        else
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // save color mode
        //glGetTexEnviv(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, &colorMode);

        gl.glDrawArrays(GL_POINTS, 0, particleIdx);

        // restore blend state
        gl.glBlendFunc(CC_BLEND_SRC, CC_BLEND_DST);

        // restore color mode
        //glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, colorMode);

        // unbind VBO buffer
        ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_POINT_SIZE_ARRAY_OES);
        gl.glDisableClientState(GL_COLOR_ARRAY);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisable(GL11.GL_POINT_SPRITE_OES);
    }

    //! whether or not the system is full
    public boolean isFull() {
        return particleCount == totalParticles;
    }

}
