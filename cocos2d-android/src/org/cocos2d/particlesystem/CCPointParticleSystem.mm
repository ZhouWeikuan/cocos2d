package org.cocos2d.particlesystem;

/** CCPointParticleSystem is a subclass of CCParticleSystem
 Attributes of a Particle System:
 * All the attributes of Particle System

 Features:
  * consumes small memory: uses 1 vertex (x,y) per particle, no need to assign tex coordinates
  * size can't be bigger than 64
  * the system can't be scaled since the particles are rendered using GL_POINT_SPRITE
 
 Limitations:
  * On 3rd gen iPhone devices and iPads, this node performs MUCH slower than CCQuadParticleSystem.
 */
public class CCPointParticleSystem extends CCParticleSystem {
	// Array of (x,y,size) 
	ccPointSprite vertices[];

	// vertices buffer id
	int	verticesID;

    GL10 gl;

    public CCPointParticleSystem(int numberOfParticles) {
        super(numberOfParticles);

        gl = CCDirector.gl;

        vertices = new ccPointSprite[totalParticles];

        gl.glGenBuffers(1, &verticesID);

        // initial binding
        gl.glBindBuffer(GL_ARRAY_BUFFER, verticesID);
        gl.glBufferData(GL_ARRAY_BUFFER, sizeof(ccPointSprite)*totalParticles, vertices,GL_DYNAMIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void finalize() {
        vertices = null;
        gl.glDeleteBuffers(1, &verticesID);

        super.finalize();
    }

    @Override
    public void updateQuad(CCParticle p, CGPoint newPos) {
        // place vertices and colos in array
        vertices[particleIdx].x = newPos.x;
        vertices[particleIdx].y = newPos.y;
        vertices[particleIdx].size = p.size;
        vertices[particleIdx].colors = p.color;
    }

    public void postStep() {
        gl.glBindBuffer(GL_ARRAY_BUFFER, verticesID);
        gl.glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(ccPointSprite)*particleCount, vertices);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        if (particleIdx==0)
            return;

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY
        // Unneeded states: GL_TEXTURE_COORD_ARRAY
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        glBindTexture(GL_TEXTURE_2D, texture_.name);

        glEnable(GL_POINT_SPRITE_OES);
        glTexEnvi( GL_POINT_SPRITE_OES, GL_COORD_REPLACE_OES, GL_TRUE );

        glBindBuffer(GL_ARRAY_BUFFER, verticesID);

        glVertexPointer(2,GL_FLOAT,sizeof(vertices[0]),0);

        glColorPointer(4, GL_FLOAT, sizeof(vertices[0]),(GLvoid*) offsetof(ccPointSprite,colors) );

        glEnableClientState(GL_POINT_SIZE_ARRAY_OES);
        glPointSizePointerOES(GL_FLOAT,sizeof(vertices[0]),(GLvoid*) offsetof(ccPointSprite,size) );


        BOOL newBlend = NO;
        if( blendFunc_.src != CC_BLEND_SRC || blendFunc_.dst != CC_BLEND_DST ) {
            newBlend = YES;
            glBlendFunc( blendFunc_.src, blendFunc_.dst );
        }

        glDrawArrays(GL_POINTS, 0, particleIdx);

        // restore blend state
        if( newBlend )
            glBlendFunc( CC_BLEND_SRC, CC_BLEND_DST);

        // unbind VBO buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableClientState(GL_POINT_SIZE_ARRAY_OES);
        glDisable(GL_POINT_SPRITE_OES);

        // restore GL default state
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }

    //
    // SPIN IS NOT SUPPORTED
    //
    public void setStartSpin(float a) {
        assert(a == 0): "PointParticleSystem doesn't support spinning";
        super.setStartSpin(a);
    }

    public void setStartSpinVar(float a) {
        assert(a == 0): "PointParticleSystem doesn't support spinning";
        super.setStartSpin(a);
    }

    public void setEndSpin(float a) {
        assert(a == 0): "PointParticleSystem doesn't support spinning";
        super.setStartSpin(a);
    }

    public void setEndSpinVar(float a) {
        assert(a == 0): "PointParticleSystem doesn't support spinning";
        super.setStartSpin(a);
    }

    //
    // SIZE > 64 IS NOT SUPPORTED
    //
    public void setStartSize(float size) {
        assert(size >= 0 && size <= CC_MAX_PARTICLE_SIZE)
            :"PointParticleSystem only supports 0 <= size <= 64";
        super.setStartSize(size);
    }

    public void setEndSize(float size) {
        assert( (size == kCCParticleStartSizeEqualToEndSize) || ( size >= 0 && size <= CC_MAX_PARTICLE_SIZE))
                : "PointParticleSystem only supports 0 <= size <= 64";
        super.setEndSize(size);
    }
}

