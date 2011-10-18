package org.cocos2d.particlesystem;

import java.lang.ref.WeakReference;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLResourceHelper.Resource;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.utils.BufferProvider;
import org.cocos2d.utils.FastFloatBuffer;

/** CCQuadParticleSystem is a subclass of CCParticleSystem

 It includes all the features of ParticleSystem.

 Special features and Limitations:	
  - Particle size can be any float number.
  - The system can be scaled
  - The particles can be rotated
  - On 1st and 2nd gen iPhones: It is only a bit slower that CCPointParticleSystem
  - On 3rd gen iPhone and iPads: It is MUCH faster than CCPointParticleSystem
  - It consumes more RAM and more GPU memory than CCPointParticleSystem
  - It supports subrects
 @since v0.8
 */
public class CCQuadParticleSystem extends CCParticleSystem implements Resource {
	// ccV2F_C4F_T2F_Quad	quads;		// quads to be rendered

	FastFloatBuffer         texCoords;
	FastFloatBuffer         vertices;
	FastFloatBuffer         colors;

	ShortBuffer			indices;	// indices
	int					quadsIDs[];	// VBO id
	public static final int QuadSize = 3;
	
//	private GLResourceHelper.GLResourceLoader  mLoader;

	private static class QuadParticleLoader implements GLResourceHelper.GLResourceLoader {

		private WeakReference<CCQuadParticleSystem> weakRef;
    	
    	public QuadParticleLoader(CCQuadParticleSystem holder) {
    		weakRef = new WeakReference<CCQuadParticleSystem>(holder);
		}
		
		@Override
		public void load(Resource res) {
			CCQuadParticleSystem thisp = weakRef.get();
    		if(thisp == null)
    			return;
			
			GL11 gl = (GL11)CCDirector.gl;
			// create the VBO buffer
			thisp.quadsIDs = new int[QuadSize];
			gl.glGenBuffers(QuadSize, thisp.quadsIDs, 0);
			
			// initial binding
			// for texCoords
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, thisp.quadsIDs[0]);
			thisp.texCoords.position(0);
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.texCoords.capacity() * 4, thisp.texCoords.bytes, GL11.GL_DYNAMIC_DRAW);	
			
			// for vertices
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, thisp.quadsIDs[1]);
			thisp.vertices.position(0);
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.vertices.capacity() * 4, thisp.vertices.bytes, GL11.GL_DYNAMIC_DRAW);	
			
			// for colors
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, thisp.quadsIDs[2]);
			thisp.colors.position(0);
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.colors.capacity() * 4, thisp.colors.bytes, GL11.GL_DYNAMIC_DRAW);	
			
			// restore the elements, arrays
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		}
		
	}
	
	// overriding the init method
	public CCQuadParticleSystem(int numberOfParticles) {
		super(numberOfParticles);

		// allocating data space
		// quads = malloc( sizeof(quads[0]) * totalParticles );
		texCoords	= new FastFloatBuffer(4 * 2 * totalParticles);
		vertices 	= new FastFloatBuffer(4 * 2 * totalParticles);
		colors  	= new FastFloatBuffer(4 * 4 * totalParticles);
		
		indices = BufferProvider.createShortBuffer(totalParticles * 6 );

		if( texCoords == null || vertices == null || colors == null || indices == null) {
			ccMacros.CCLOG("cocos2d", "Particle system: not enough memory");
			return ;
		}

		// initialize only once the texCoords and the indices
		initTexCoordsWithRect(0, 0, 10, 10);
		initIndices();

		GLResourceHelper.GLResourceLoader mLoader = new QuadParticleLoader(this);

		GLResourceHelper.sharedHelper().addLoader(this, mLoader, true);
	}

	@Override
	public void finalize() throws Throwable {
		if(quadsIDs != null) {
			GLResourceHelper.sharedHelper().perform(new GLResourceHelper.GLResorceTask() {
				
				@Override
				public void perform(GL10 gl) {
					GL11 gl11 = (GL11)gl;
					
					gl11.glDeleteBuffers(QuadSize, quadsIDs, 0);
				}
				
			});
		}
    	
		super.finalize();
	}

	// initilizes the text coords
	// rect should be in Texture coordinates, not pixel coordinates
	public void initTexCoordsWithRect(CGRect rect) {
		initTexCoordsWithRect(rect.origin.x, rect.origin.y, rect.size.width, rect.size.height);
	}
	
	public void initTexCoordsWithRect(float rectX, float rectY, float rectW, float rectH) {
		float bottomLeftX = rectX;
		float bottomLeftY = rectY;

		float bottomRightX = bottomLeftX + rectW;
		float bottomRightY = bottomLeftY;

		float topLeftX = bottomLeftX;
		float topLeftY = bottomLeftY + rectH;

		float topRightX = bottomRightX;
		float topRightY = topLeftY;

		// Important. Texture in cocos2d are inverted, so the Y component should be inverted
		float tmp = topRightY;
		topRightY = bottomRightY;
		bottomRightY = tmp;
		
		tmp = topLeftY;
		topLeftY = bottomLeftY;
		bottomLeftY = tmp;

		for(int i=0; i<totalParticles; i++) {
			final int base = i * 8;
			// bottom-left vertex:
			texCoords.put(base + 0, bottomLeftX);
			texCoords.put(base + 1, bottomLeftY);
			
			// bottom-right vertex:
			texCoords.put(base + 2, bottomRightX);
			texCoords.put(base + 3, bottomRightY);
			
			// top-left vertex:
			texCoords.put(base + 4, topLeftX);
			texCoords.put(base + 5, topLeftY);
			
			// top-right vertex:
			texCoords.put(base + 6, topRightX);
			texCoords.put(base + 7, topRightY);
		}
	}


	/** Sets a new texture with a rect. The rect is in pixels.
		@since v0.99.4
	 */
	public void setTexture(CCTexture2D tex, CGRect rect) {
		// Only update the texture if is different from the current one
		if (tex != texture)
			super.setTexture(tex);

		// convert to Tex coords
		float wide = tex.pixelsWide();
		float high = tex.pixelsHigh();
		
		float rectX = rect.origin.x / wide;
		float rectY = rect.origin.y / high;
		float rectW = rect.size.width / wide;
		float rectH = rect.size.height / high;
		initTexCoordsWithRect(rectX, rectY, rectW, rectH);
	}
	
	public void setTexture(CCTexture2D tex) {
		this.setTexture(tex, CGRect.make(0, 0, tex.pixelsWide(), tex.pixelsHigh()));
	}


	/** Sets a new CCSpriteFrame as particle.
		WARNING: this method is experimental. Use setTexture:withRect instead.
		@since v0.99.4
	 */
	public void setDisplayFrame(CCSpriteFrame spriteFrame) {
		assert CGPoint.equalToPoint( spriteFrame.getOffsetRef() , CGPoint.getZero() ):"QuadParticle only supports SpriteFrames with no offsets";

		// update texture before updating texture rect
		if ( spriteFrame.getTexture() != texture )
			setTexture(spriteFrame.getTexture());
	}

	// initialices the indices for the vertices
	public void initIndices() {
		for( int i=0;i< totalParticles;i++) {
			final short base4 = (short) (i * 4);
			final int base6 = i * 6;
			indices.put(base6+0, (short) (base4 + 0));
			indices.put(base6+1, (short) (base4 + 1));
			indices.put(base6+2, (short) (base4 + 2));

			indices.put(base6+3, (short) (base4 + 1));
			indices.put(base6+4, (short) (base4 + 2));
			indices.put(base6+5, (short) (base4 + 3));
		}
	}

	@Override
	public void updateQuad(CCParticle p, CGPoint newPos) {
		// colors
		for (int i=0; i<4; ++i) {
			colors.put(particleIdx * 16 + i*4 + 0, p.color.r);
			colors.put(particleIdx * 16 + i*4 + 1, p.color.g);
			colors.put(particleIdx * 16 + i*4 + 2, p.color.b);
			colors.put(particleIdx * 16 + i*4 + 3, p.color.a);
		}

		// vertices
		float size_2 = p.size/2;
		if( p.rotation != 0) {
			float x1 = -size_2;
			float y1 = -size_2;

			float x2 = size_2;
			float y2 = size_2;
			float x = newPos.x;
			float y = newPos.y;

			float r = (float)- ccMacros.CC_DEGREES_TO_RADIANS(p.rotation);
			float cr = (float) Math.cos(r);
			float sr = (float) Math.sin(r);
			float ax = x1 * cr - y1 * sr + x;
			float ay = x1 * sr + y1 * cr + y;
			float bx = x2 * cr - y1 * sr + x;
			float by = x2 * sr + y1 * cr + y;
			float cx = x2 * cr - y2 * sr + x;
			float cy = x2 * sr + y2 * cr + y;
			float dx = x1 * cr - y2 * sr + x;
			float dy = x1 * sr + y2 * cr + y;
			
			final int base = particleIdx * 8;
			// bottom-left vertex:
			vertices.put(base + 0, ax);
			vertices.put(base + 1, ay);
			
			// bottom-right vertex:
			vertices.put(base + 2, bx);
			vertices.put(base + 3, by);
			
			// top-left vertex:
			vertices.put(base + 4, dx);
			vertices.put(base + 5, dy);
			
			// top-right vertex:
			vertices.put(base + 6, cx);
			vertices.put(base + 7, cy);
		} else {
			final int base = particleIdx * 8;
			// bottom-left vertex:
			vertices.put(base + 0, newPos.x - size_2);
			vertices.put(base + 1, newPos.y - size_2);
			
			// bottom-right vertex:
			vertices.put(base + 2, newPos.x + size_2);
			vertices.put(base + 3, newPos.y - size_2);
			
			// top-left vertex:
			vertices.put(base + 4, newPos.x - size_2);
			vertices.put(base + 5, newPos.y + size_2);
			
			// top-right vertex:
			vertices.put(base + 6, newPos.x + size_2);
			vertices.put(base + 7, newPos.y + size_2);
		}
	}

	@Override
	public void postStep(){
		if(quadsIDs == null)
			return;
		
		GL11 gl = (GL11)CCDirector.gl;

		// for texCoords
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[0]);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, texCoords.capacity() * 4, texCoords.bytes);	
		
		// for vertices
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[1]);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, vertices.capacity() * 4, vertices.bytes);	
		
		// for colors
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[2]);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, colors.capacity() * 4, colors.bytes);	
		
		// restore the elements, arrays
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
	}

	// overriding draw method
	@Override
	public void draw(GL10 gle)
	{
		if(quadsIDs == null)
			return;
		
		// Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
		// Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
		// Unneeded states: -
		GL11 gl = (GL11)gle;

		gl.glBindTexture(GL11.GL_TEXTURE_2D, texture.name());
		// for texCoords
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[0]);
		gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		// gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, texCoords);
		
		// for vertices
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[1]);			
		gl.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		// gl.glVertexPointer(2, GL11.GL_FLOAT, 0, vertices);
		
		// for colors
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[2]);		
		gl.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
		// gl.glColorPointer(4, GL11.GL_FLOAT, 0, colors);
		
		boolean newBlend = false;
		if( blendFunc.src != ccConfig.CC_BLEND_SRC || blendFunc.dst != ccConfig.CC_BLEND_DST ) {
			newBlend = true;
			gl.glBlendFunc( blendFunc.src, blendFunc.dst );
		}

		/*
		if( particleIdx != particleCount ) {
			String str = String.format("pd:%d, pc:%d", particleIdx, particleCount);
			ccMacros.CCLOG("CCQuadParticleSystem", str);
		}*/

		// Log.e("ParticleSystem", "particleIdx is " + String.valueOf(particleIdx));
		
		// gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, quadsIDs[3]);
		gl.glDrawElements(GL11.GL_TRIANGLES, particleIdx*6, GL11.GL_UNSIGNED_SHORT, indices);
		
		// restore blend state
		if( newBlend )
			gl.glBlendFunc( ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST );

		// restore the elements, arrays
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		// restore GL default state
		// -
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

