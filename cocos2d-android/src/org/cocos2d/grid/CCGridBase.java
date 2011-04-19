package org.cocos2d.grid;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLResourceHelper.Resource;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.utils.CCFormatter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLU;

/** Base class for other
 */
public abstract class CCGridBase {
	/** wheter or not the grid is active */
    protected boolean active_;

	/** number of times that the grid will be reused */
    protected int reuseGrid_;
    
	/** size of the grid */
    protected ccGridSize gridSize_;
    
	/** pixels between the grids */
    protected CCTexture2D texture_;

	/** texture used */
    protected CGPoint step_;
    
    public CGPoint getStep() {
    	return CGPoint.make(step_.x, step_.y);
    }

	/** grabber used */
    protected CCGrabber grabber_;

	/** is texture flipped */
    protected boolean isTextureFlipped_;
    
    protected GL10 gl;

    public boolean isTextureFlipped() {
    	return isTextureFlipped_;
    }

    public void setIsTextureFlipped(GL10 gl, boolean flipped){
    	if( isTextureFlipped_ != flipped ) {
    		isTextureFlipped_ = flipped;
    		this.calculateVertexPoints();
    	}
    }

    public static final int kTextureSize = 512;

    public int reuseGrid() {
        return reuseGrid_ ;
    }
    
    public void setReuseGrid(int g) {
    	reuseGrid_ = g;
    }

    public boolean isActive() {
        return active_;
    }

    public void setActive(boolean active) {
        active_ = active;
     	if( ! active ) {
     		CCDirector director = CCDirector.sharedDirector();
     		int proj = director.getProjection();
     		director.setProjection(proj);
     	}
    }


    public int getGridWidth() {
        return gridSize_.x;
    }

    public int getGridHeight() {
        return gridSize_.y;
    }

    public CCGridBase(GL10 gl, ccGridSize gridSize, CCTexture2D texture, boolean flipped) {
    	init(gridSize, texture, flipped);
    }
    
    private void init(ccGridSize gridSize, CCTexture2D texture, boolean flipped) {
    	active_ = false;
    	reuseGrid_ = 0;
    	gridSize_ = gridSize;
    	texture_ = texture;
    	isTextureFlipped_ = flipped;

    	CGSize texSize = texture_.getContentSize();
    	step_ = CGPoint.ccp(texSize.width / gridSize_.x, texSize.height / gridSize_.y);

    	grabber_ = new CCGrabber();
    	grabber_.grab(texture_);
    	calculateVertexPoints();
    }
    
    public CCGridBase(final ccGridSize gSize) {
    	CCTexture2D texture = new CCTexture2D();
    	
    	texture.setLoader(new GLResourceHelper.GLResourceLoader() {
			
			@Override
			public void load(Resource res) {
		    	CGSize s = CCDirector.sharedDirector().winSize();

		    	int textureSize = 8;
		    	while (textureSize < s.width || textureSize < s.height)
		    		textureSize *= 2;

		    	if (textureSize > 1024) {
		    		textureSize = 1024;
		    	}
		    	Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Config.ARGB_8888);
		        Canvas canvas = new Canvas(bitmap);
		        canvas.drawBitmap(bitmap, 0, 0, new Paint());
		        
		        ((CCTexture2D)res).initWithImage(bitmap, CGSize.make(textureSize, textureSize));
				
				init(gSize, ((CCTexture2D)res), false);
			}
		});
    }

    public String toString() {
        return new CCFormatter().format("<%s : Dimensions = %dx%d>",
        			CCGridBase.class, gridSize_.x, gridSize_.y);
    }

    // This routine can be merged with Director
    public void applyLandscape(GL10 gl) {
    	CCDirector director = CCDirector.sharedDirector();
        boolean landscape = director.getLandscape();
        CGSize winSize = director.winSize();
        float w = winSize.width / 2;
     	float h = winSize.height / 2;
     	
        if (landscape) {
        	gl.glTranslatef(w,h,0);
 			gl.glRotatef(-90,0,0,1);
 			gl.glTranslatef(-h,-w,0);
        }
    }

    public void set2DProjection(GL10 gl) {
        CGSize winSize = CCDirector.sharedDirector().winSize();

        gl.glLoadIdentity();
        gl.glViewport(0, 0, (int) winSize.width, (int) winSize.height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, winSize.width, 0, winSize.height, -100, 100);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    // This routine can be merged with Director
    public void set3DProjection(GL10 gl) {
        CGSize winSize = CCDirector.sharedDirector().displaySize();

        gl.glViewport(0, 0, (int) winSize.width, (int) winSize.height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 60, winSize.width / winSize.height, 0.5f, 1500.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, winSize.width / 2, winSize.height / 2, CCDirector.sharedDirector().getZEye(),
                winSize.width / 2, winSize.height / 2, 0,
                0.0f, 1.0f, 0.0f
        );
    }

    public void beforeDraw(GL10 gl) {
        set2DProjection(gl);
        grabber_.beforeRender(texture_);
    }

    public void afterDraw(GL10 gl, CCNode target) {
    	grabber_.afterRender(texture_);
        set3DProjection(gl);
        applyLandscape(gl);

        if (target.getCamera().getDirty()) {
    		CGPoint offset = target.getAnchorPointInPixels();

    		//
    		// XXX: Camera should be applied in the AnchorPoint
    		//
    		gl.glTranslatef(offset.x, offset.y, 0);
    		target.getCamera().locate(gl);
    		gl.glTranslatef(-offset.x, -offset.y, 0);
        }
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

        blit(gl);
    }


    @Override
    public void finalize()  throws Throwable {
    	ccMacros.CCLOGINFO("cocos2d: deallocing %s", this.toString());

    	setActive(false);

        super.finalize();
    }
    
    public abstract void blit(GL10 gl);

    public abstract void reuse(GL10 gl);

    public abstract void calculateVertexPoints();

}
