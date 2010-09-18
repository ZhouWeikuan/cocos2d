package org.cocos2d.nodes;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.protocols.CCTextureProtocol;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;

/**
 * AtlasNode is a subclass of CocosNode that implements CocosNodeOpacity, CocosNodeRGB and
 * CocosNodeSize protocols.
 * <p/>
 * It knows how to render a TextureAtlas object.
 * <p/>
 * All features from CocosNode are valid, plus the following features:
 * - opacity
 * - color (setRGB:::)
 * - contentSize
 */

/** CCAtlasNode is a subclass of CCNode that implements the CCRGBAProtocol and
 CCTextureProtocol protocol
 
 It knows how to render a TextureAtlas object.
 If you are going to render a TextureAtlas consider subclassing CCAtlasNode (or a subclass of CCAtlasNode)
 
 All features from CCNode are valid, plus the following features:
 - opacity and RGB colors
 */
public abstract class CCAtlasNode extends CCNode
        implements CCRGBAProtocol, CCTextureProtocol {
    /// texture atlas
    protected CCTextureAtlas textureAtlas_;
    /// chars per row
    protected int itemsPerRow;
    /// chars per column
    protected int itemsPerColumn;

    /// texture coordinate x increment
    protected float texStepX;
    /// texture coordinate y increment
    protected float texStepY;

    /// width of each char
    protected int itemWidth;
    /// height of each char
    protected int itemHeight;

    // blend function
    ccBlendFunc blendFunc_;

    // texture RGBA
    int opacity_;
    ccColor3B color_;
	ccColor3B	colorUnmodified_;
    boolean opacityModifyRGB_;

    /** initializes an CCAtlasNode  with an Atlas file the width and height of each item and the quantity of items to render*/
    protected CCAtlasNode(String tile, int w, int h, int c) {
    	super();
    	
        itemWidth = w;
        itemHeight = h;
        opacity_ = 255;
        color_ = ccColor3B.ccWHITE;
        colorUnmodified_ = ccColor3B.ccWHITE;
        opacityModifyRGB_ = true;

        blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
        textureAtlas_ = new CCTextureAtlas(tile, c);

        updateBlendFunc();
        updateOpacityModifyRGB();
        
        calculateMaxItems();
        calculateTexCoordsSteps();
    }

    private void calculateMaxItems() {
        CGSize s = textureAtlas_.getTexture().getContentSize();
        itemsPerColumn = (int) (s.height / itemHeight);
        itemsPerRow = (int) (s.width / itemWidth);
    }

    private void calculateTexCoordsSteps() {
        CCTexture2D tex = textureAtlas_.getTexture();
        texStepX = itemWidth / (float) tex.pixelsWide();
        texStepY = itemHeight / (float) tex.pixelsHigh();
    }

    /** updates the Atlas (indexed vertex array).
     * Shall be overriden in subclasses
     */
    public abstract void updateAtlasValues();

    @Override
    public void draw(GL10 gl) {

    	// Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
    	// Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
    	// Unneeded states: GL_COLOR_ARRAY
    	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    	
        gl.glColor4f(color_.r / 255f, color_.g / 255f, color_.b / 255f, opacity_ / 255f);

        boolean newBlend = false;
        if (blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST) {
            newBlend = true;
            gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
        }

        textureAtlas_.drawQuads(gl);

        if (newBlend)
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

       	
    	// is this chepear than saving/restoring color state ?
    	// XXX: There is no need to restore the color to (255,255,255,255). Objects should use the color
    	// XXX: that they need
        //    	glColor4ub( 255, 255, 255, 255);

    	// restore default GL state
    	gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }
    
    /** conforms to CCRGBAProtocol protocol */
    public void setOpacity(int opacity) {
        opacity_ = opacity;

	    // special opacity for premultiplied textures
        if (opacityModifyRGB_) {
            setColor(opacityModifyRGB_? colorUnmodified_ : color_);
        }
    }

    public int getOpacity() {
        return opacity_;
    }

    /** conforms to CCRGBAProtocol protocol */
    public void setColor(ccColor3B color) {
        color_ = new ccColor3B(color);
        colorUnmodified_ = new ccColor3B(color);
        if( opacityModifyRGB_ ){
            color_.r = color.r * opacity_/255;
            color_.g = color.g * opacity_/255;
            color_.b = color.b * opacity_/255;
        }	
    }

    public ccColor3B getColor() {
        if (opacityModifyRGB_) {
            return new ccColor3B(colorUnmodified_);
        }

        return new ccColor3B(color_);
    }

    // CocosNodeTexture protocol
    /** conforms to CCTextureProtocol protocol */
    public void updateBlendFunc() {
    	if( ! (textureAtlas_.getTexture().hasPremultipliedAlpha() )) {
    		blendFunc_.src = GL10.GL_SRC_ALPHA;
    		blendFunc_.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
    	}
    }

    /** conforms to CCTextureProtocol protocol */
    public void setTexture(CCTexture2D texture) {
        textureAtlas_.setTexture(texture);
        updateBlendFunc();
        updateOpacityModifyRGB();
    }

    public CCTexture2D getTexture() {
        return textureAtlas_.getTexture();
    }
    
    public void setOpacityModifyRGB(boolean modify) {
    	opacityModifyRGB_ = modify;
    }

    public boolean doesOpacityModifyRGB() {
    	return opacityModifyRGB_;
    }

    public void updateOpacityModifyRGB() {
    	opacityModifyRGB_ = textureAtlas_.getTexture().hasPremultipliedAlpha();
    }
}

