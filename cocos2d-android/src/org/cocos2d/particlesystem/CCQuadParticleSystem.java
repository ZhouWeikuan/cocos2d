package org.cocos2d.particlesystem;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.HashMap;

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
import org.cocos2d.utils.PlistParser;

import com.badlogic.gdx.utils.BufferUtils;

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

	ByteBuffer         texCoords;
	ByteBuffer         vertices;
	ByteBuffer         colors;

	int					quadsIDs[];	// VBO id
	public static final int QuadSize = 4;
	
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
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.texCoords.capacity(), thisp.texCoords, GL11.GL_DYNAMIC_DRAW);	
			
			// for vertices
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, thisp.quadsIDs[1]);
			thisp.vertices.position(0);
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.vertices.capacity(), thisp.vertices, GL11.GL_DYNAMIC_DRAW);	
			
			// for colors
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, thisp.quadsIDs[2]);
			thisp.colors.position(0);
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, thisp.colors.capacity(), thisp.colors, GL11.GL_DYNAMIC_DRAW);	
			
			// for indices
			ByteBuffer indices = BufferUtils.newUnsafeByteBuffer(thisp.totalParticles * 6 * 2);
			thisp.initIndices(indices);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, thisp.quadsIDs[3]);
			gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indices.capacity(), indices, GL11.GL_STATIC_DRAW);
			BufferUtils.disposeUnsafeByteBuffer(indices);
			
			// restore the elements, arrays
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		}
		
	}
	
	// overriding the init method
	public CCQuadParticleSystem(int numberOfParticles) {
		super(numberOfParticles);
		init();
	}
	
	public CCQuadParticleSystem(String plistFile) {
		HashMap<String,Object> dictionary = PlistParser.parse(plistFile);
    	int numParticles = ((Number)dictionary.get("maxParticles")).intValue();
    	initWithNumberOfParticles(numParticles);
    	
		init();
		
		loadParticleFile(dictionary);
	}

	protected void init() {
		// allocating data space
		texCoords = BufferUtils.newUnsafeByteBuffer(4 * 2 * totalParticles * 4); 
		vertices = BufferUtils.newUnsafeByteBuffer(4 * 2 * totalParticles * 4);
		colors = BufferUtils.newUnsafeByteBuffer(4 * 4 * totalParticles * 4);

		if( texCoords == null || vertices == null || colors == null) {
			ccMacros.CCLOG("cocos2d", "Particle system: not enough memory");
			return ;
		}

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
		
		BufferUtils.disposeUnsafeByteBuffer(texCoords);
		BufferUtils.disposeUnsafeByteBuffer(vertices);
		BufferUtils.disposeUnsafeByteBuffer(colors);
    	
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
			final int base = i * 8 * 4;
			// bottom-left vertex:
			tmpArray[0] = bottomLeftX;
			tmpArray[1] = bottomLeftY;
			
			// bottom-right vertex:
			tmpArray[2] = bottomRightX;
			tmpArray[3] = bottomRightY;
			
			// top-left vertex:
			tmpArray[4] = topLeftX;
			tmpArray[5] = topLeftY;
			
			// top-right vertex:
			tmpArray[6] = topRightX;
			tmpArray[7] = topRightY;
			
			texCoords.position(base);
			BufferUtils.copy(tmpArray, 0, texCoords, 8);
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
	public void initIndices(ByteBuffer indices) {
		for( int i=0;i< totalParticles;i++) {
			final short base4 = (short) (i * 4);
			final int base6 = i * 6 * 2;
			indices.putShort(base6+0, (short) (base4 + 0));
			indices.putShort(base6+2, (short) (base4 + 1));
			indices.putShort(base6+4, (short) (base4 + 2));

			indices.putShort(base6+6, (short) (base4 + 1));
			indices.putShort(base6+8, (short) (base4 + 2));
			indices.putShort(base6+10, (short) (base4 + 3));
		}
	}

	private static float[] tmpArray = new float[16];
	
	@Override
	public void updateQuad(CCParticle p, CGPoint newPos) {
		// colors
		for (int i = 0; i < 4; ++i) {
			int baseIndex = i*4;
			tmpArray[baseIndex + 0] = p.color.r;
			tmpArray[baseIndex + 1] = p.color.g;
			tmpArray[baseIndex + 2] = p.color.b;
			tmpArray[baseIndex + 3] = p.color.a;
		}
		colors.position(particleIdx * 16 * 4);
		BufferUtils.copy(tmpArray, 0, colors, 16);

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
			
			// bottom-left vertex:
			tmpArray[0] = ax;
			tmpArray[1] = ay;
			
			// bottom-right vertex:
			tmpArray[2] = bx;
			tmpArray[3] = by;
			
			// top-left vertex:
			tmpArray[4] = dx;
			tmpArray[5] = dy;
			
			// top-right vertex:
			tmpArray[6] = cx;
			tmpArray[7] = cy;
			
			vertices.position(particleIdx * 8 * 4);
			BufferUtils.copy(tmpArray, 0, vertices, 8);
		} else {
			// bottom-left vertex:
			tmpArray[0] = newPos.x - size_2;
			tmpArray[1] = newPos.y - size_2;
			
			// bottom-right vertex:
			tmpArray[2] = newPos.x + size_2;
			tmpArray[3] = newPos.y - size_2;
			
			// top-left vertex:
			tmpArray[4] = newPos.x - size_2;
			tmpArray[5] = newPos.y + size_2;
			
			// top-right vertex:
			tmpArray[6] = newPos.x + size_2;
			tmpArray[7] = newPos.y + size_2;
			
			vertices.position(particleIdx * 8 * 4);
			BufferUtils.copy(tmpArray, 0, vertices, 8);
		}
	}

	@Override
	public void postStep(){
		if(quadsIDs == null)
			return;
		
		GL11 gl = (GL11)CCDirector.gl;

		// for texCoords
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[0]);
		texCoords.position(0);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, texCoords.capacity(), texCoords);	
		
		// for vertices
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[1]);
		vertices.position(0);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, vertices.capacity(), vertices);	
		
		// for colors
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[2]);
		colors.position(0);
		gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, colors.capacity(), colors);	
		
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
		
		// for vertices
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[1]);			
		gl.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		// for colors
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, quadsIDs[2]);		
		gl.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
		
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
		
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, quadsIDs[3]);
		gl.glDrawElements(GL11.GL_TRIANGLES, particleIdx*6, GL11.GL_UNSIGNED_SHORT, 0);
		
		// restore blend state
		if( newBlend )
			gl.glBlendFunc( ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST );

		// restore the elements, arrays
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
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

