package org.cocos2d.particlesystem;

import javax.microedition.khronos.opengles.GL11;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccPointSprite;
import org.cocos2d.utils.FastFloatBuffer;

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
	// Array of (x,y, ccColor4F) 
	FastFloatBuffer vertices;
	// Array of (size)
	FastFloatBuffer sizeBuffer;

	// vertices buffer id
	int	verticesID[];    

    public CCPointParticleSystem(int numberOfParticles) {
        super(numberOfParticles);

        GL11 gl = (GL11) CCDirector.gl;

        vertices = new FastFloatBuffer(numberOfParticles * ccPointSprite.spriteSize);
        sizeBuffer = new FastFloatBuffer(numberOfParticles);
        
        verticesID = new int[2];
        gl.glGenBuffers(2, verticesID, 0);

        // initial binding
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, ccPointSprite.spriteSize*4*totalParticles, vertices.bytes, GL11.GL_DYNAMIC_DRAW);
        
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[1]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, 4*totalParticles, sizeBuffer.bytes, GL11.GL_DYNAMIC_DRAW);
        
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void finalize() throws Throwable {
        vertices = null;
        GL11 gl = (GL11) CCDirector.gl;
        gl.glDeleteBuffers(2, verticesID, 0);

        super.finalize();
    }

    @Override
    public void updateQuad(CCParticle p, CGPoint newPos) {
        // place vertices and colos in array
    	final int base = particleIdx * ccPointSprite.spriteSize;
        vertices.put(base + 0, newPos.x);
        vertices.put(base + 1, newPos.y);        
        vertices.put(base + 2, p.color.r);
        vertices.put(base + 3, p.color.g);
        vertices.put(base + 4, p.color.b);
        vertices.put(base + 5, p.color.a);
        
        sizeBuffer.put(particleIdx, p.size);
    }

    public void postStep() {
    	GL11 gl = (GL11) CCDirector.gl;
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[0]);
        gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, ccPointSprite.spriteSize*4*particleCount, vertices.bytes);
        
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[1]);
        gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, 4*particleCount, sizeBuffer.bytes);
        
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        if (particleIdx==0)
            return;

        GL11 gl = (GL11) CCDirector.gl;
        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY
        // Unneeded states: GL_TEXTURE_COORD_ARRAY
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        gl.glBindTexture(GL11.GL_TEXTURE_2D, texture.name());

        gl.glEnable(GL11.GL_POINT_SPRITE_OES);
        gl.glTexEnvi(GL11.GL_POINT_SPRITE_OES, GL11.GL_COORD_REPLACE_OES, GL11.GL_TRUE );

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[0]);

        gl.glVertexPointer(2, GL11.GL_FLOAT, ccPointSprite.spriteSize*4, 0);

        gl.glColorPointer(4, GL11.GL_FLOAT, ccPointSprite.spriteSize*4, 2*4); // ccPointSprite.color

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, verticesID[1]);
        gl.glEnableClientState(GL11.GL_POINT_SIZE_ARRAY_OES);
        gl.glPointSizePointerOES(GL11.GL_FLOAT, 0, sizeBuffer.bytes); // ccPointSprite.size


        boolean newBlend = false;
        if( blendFunc.src != ccConfig.CC_BLEND_SRC || blendFunc.dst != ccConfig.CC_BLEND_DST ) {
            newBlend = true;
            gl.glBlendFunc(blendFunc.src, blendFunc.dst);
        }

        gl.glDrawArrays(GL11.GL_POINTS, 0, particleIdx);

        // restore blend state
        if( newBlend )
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        // unbind VBO buffer
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        gl.glDisableClientState(GL11.GL_POINT_SIZE_ARRAY_OES);
        gl.glDisable(GL11.GL_POINT_SPRITE_OES);

        // restore GL default state
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
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
        assert(size >= 0 && size <= ccMacros.CC_MAX_PARTICLE_SIZE)
            :"PointParticleSystem only supports 0 <= size <= 64";
        super.setStartSize(size);
    }

    public void setEndSize(float size) {
        assert( (size == kCCParticleStartSizeEqualToEndSize) || ( size >= 0 && size <= ccMacros.CC_MAX_PARTICLE_SIZE))
                : "PointParticleSystem only supports 0 <= size <= 64";
        super.setEndSize(size);
    }

	@Override
	public void setBlendFunc(ccBlendFunc blendFunc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ccBlendFunc getBlendFunc() {
		// TODO Auto-generated method stub
		return null;
	}
}

