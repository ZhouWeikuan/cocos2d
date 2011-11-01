package org.cocos2d.nodes;

import java.util.Arrays;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.protocols.CCTextureProtocol;
import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.FastFloatBuffer;

import android.graphics.Bitmap;

/** CCSprite is a 2d image ( http://en.wikipedia.org/wiki/Sprite_(computer_graphics) )
 *
 * CCSprite can be created with an image, or with a sub-rectangle of an image.
 *
 * If the parent or any of its ancestors is a CCSpriteSheet then the following features/limitations are valid
 *	- Features when the parent is a CCSpriteSheet:
 *		- MUCH faster rendering, specially if the CCSpriteSheet has many children. All the children will be drawn in a single batch.
 *
 *	- Limitations
 *		- Camera is not supported yet (eg: CCOrbitCamera action doesn't work)
 *		- GridBase actions are not supported (eg: CCLens, CCRipple, CCTwirl)
 *		- The Alias/Antialias property belongs to CCSpriteSheet, so you can't individually set the aliased property.
 *		- The Blending function property belongs to CCSpriteSheet, so you can't individually set the blending function property.
 *		- Parallax scroller is not supported, but can be simulated with a "proxy" sprite.
 *
 *  If the parent is an standard CCNode, then CCSprite behaves like any other CCNode:
 *    - It supports blending functions
 *    - It supports aliasing / antialiasing
 *    - But the rendering will be slower: 1 draw per children.
 *
 */
public class CCSprite extends CCNode implements CCRGBAProtocol, CCTextureProtocol {

    // XXX: Optmization
    class TransformValues {
        CGPoint pos;		// position x and y
        CGPoint	scale;		// scale x and y
        float	rotation;
        CGPoint ap;			// anchor point in pixels
    }
	
	/// CCSprite invalid index on the CCSpriteSheet
	public static final int CCSpriteIndexNotInitialized = 0xffffffff;

    /** 
     * Whether or not an CCSprite will rotate, scale or translate with it's parent.
     * Useful in health bars, when you want that the health bar translates with it's parent
     * but you don't want it to rotate with its parent.
     * @since v0.99.0
     */
	//! Translate with it's parent
	public static final int CC_HONOR_PARENT_TRANSFORM_TRANSLATE =  1 << 0;
	//! Rotate with it's parent
	public static final int CC_HONOR_PARENT_TRANSFORM_ROTATE	=  1 << 1;
	//! Scale with it's parent
	public static final int CC_HONOR_PARENT_TRANSFORM_SCALE		=  1 << 2;

	//! All possible transformation enabled. Default value.
	public static final int CC_HONOR_PARENT_TRANSFORM_ALL		=  CC_HONOR_PARENT_TRANSFORM_TRANSLATE
            | CC_HONOR_PARENT_TRANSFORM_ROTATE | CC_HONOR_PARENT_TRANSFORM_SCALE;

   
	// Animations that belong to the sprite
    private HashMap<String, CCAnimation> animations_;

	// image is flipped
    /** whether or not the sprite is flipped vertically.
     * It only flips the texture of the sprite, and not the texture of the sprite's children.
     * Also, flipping the texture doesn't alter the anchorPoint.
     * If you want to flip the anchorPoint too, and/or to flip the children too use: 
     * sprite.scaleY *= -1;
    */
	public boolean flipY_;

	/** whether or not the sprite is flipped horizontally. 
     * It only flips the texture of the sprite, and not the texture of the sprite's children.
     * Also, flipping the texture doesn't alter the anchorPoint.
     * If you want to flip the anchorPoint too, and/or to flip the children too use:
     * sprite.scaleX *= -1;
    */
	public boolean flipX_;

	// opacity and RGB protocol
    /** opacity: conforms to CCRGBAProtocol protocol */
    int		opacity_;

    public int getOpacity() {
        return opacity_;
    }

    public void setOpacity(int anOpacity) {
        opacity_			= anOpacity;

        // special opacity for premultiplied textures
        if( opacityModifyRGB_ )
            setColor(colorUnmodified_);
        updateColor();
    }

    /** RGB colors: conforms to CCRGBAProtocol protocol */
	ccColor3B	color_;
	ccColor3B	colorUnmodified_;
	boolean		opacityModifyRGB_;

    public void setOpacityModifyRGB(boolean modify) {
        if (opacityModifyRGB_ != modify) {
            ccColor3B oldColor	= this.color_;
            opacityModifyRGB_	= modify;
            setColor(oldColor);
        }
    }

    public ccColor3B getColor() {
        if(opacityModifyRGB_){
            return new ccColor3B(colorUnmodified_);
        }
        return new ccColor3B(color_);
    }

    public void setColor(ccColor3B color3) {
        color_.set(color3);
        colorUnmodified_.set(color3);

        if( opacityModifyRGB_ ){
            color_.r = color3.r * opacity_/255;
            color_.g = color3.g * opacity_/255;
            color_.b = color3.b * opacity_/255;
        }

        updateColor();
    }

	//
	// Data used when the sprite is self-rendered
	//
	CCTexture2D				texture_;				// Texture used to render the sprite

    /** conforms to CCTextureProtocol protocol */
	protected ccBlendFunc blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
	
	// texture pixels
	CGRect rect_ = CGRect.zero();
	Boolean rectRotated_ = false;
	
    /** offset position of the sprite. Calculated automatically by editors like Zwoptex.
      @since v0.99.0
    */
	CGPoint	offsetPosition_;	// absolute
	CGPoint unflippedOffsetPositionFromCenter_;

	//
	// Data used when the sprite is rendered using a CCSpriteSheet
	//
    /** weak reference of the CCTextureAtlas used when the sprite is rendered using a CCSpriteSheet */
    CCTextureAtlas			textureAtlas_;

    /** The index used on the TextureATlas.
     * Don't modify this value unless you know what you are doing */
	public		int			atlasIndex;	// Absolute (real) Index on the SpriteSheet

    /** weak reference to the CCSpriteSheet that renders the CCSprite */
	CCSpriteSheet			spriteSheet_;

	// whether or not to transform according to its parent transformations
    /** whether or not to transform according to its parent transfomrations.
     * Useful for health bars. eg: Don't rotate the health bar, even if the parent rotates.
     * IMPORTANT: Only valid if it is rendered using an CCSpriteSheet.
     * @since v0.99.0
    */
    int                     honorParentTransform_;

    /** whether or not the Sprite needs to be updated in the Atlas */
	boolean					dirty_;					// Sprite needs to be updated
	boolean					recursiveDirty_;		// Subchildren needs to be updated
	boolean					hasChildren_;			// optimization to check if it contain children
	
	// vertex coords, texture coords and color info
    /** buffers that are going to be rendered */
    /** the quad (tex coords, vertex coords and color) information */
    private FastFloatBuffer texCoords;
    public float[] getTexCoordsArray() {
    	float ret[] = new float[texCoords.limit()];
    	texCoords.get(ret, 0, texCoords.limit());
    	return ret;
    }
    
    private FastFloatBuffer vertexes;
    public float[] getVertexArray() {
    	float ret[] = new float[vertexes.limit()];
    	vertexes.get(ret, 0, vertexes.limit());
    	return ret;
    }
    
    public FastFloatBuffer getTexCoords() {
    	texCoords.position(0);
    	return texCoords;
    }
    
    public FastFloatBuffer getVertices() {
    	vertexes.position(0);
    	return vertexes;
    }
    
    private FastFloatBuffer colors;

	// whether or not it's parent is a CCSpriteSheet
    /** whether or not the Sprite is rendered using a CCSpriteSheet */
    boolean             usesSpriteSheet_;

    public CGRect getTextureRect() {
        return rect_;
    }
    
    public Boolean getTextureRectRotated()
    {
    	return rectRotated_;
    }

    /** Creates an sprite with a texture.
      The rect used will be the size of the texture.
      The offset will be (0,0).
      */
    public static CCSprite sprite(CCTexture2D texture) {
        return new CCSprite(texture);
    }

    /** Creates an sprite with a texture and a rect.
      The offset will be (0,0).
      */
    public static CCSprite sprite(CCTexture2D texture, CGRect rect) {
        return new CCSprite(texture, rect);
    }

    /** Creates an sprite with an sprite frame.
    */
    public static CCSprite sprite(CCSpriteFrame spriteFrame) {
        return new CCSprite(spriteFrame);
    }

    /** Creates an sprite with an sprite frame name.
      An CCSpriteFrame will be fetched from the CCSpriteFrameCache by name.
      If the CCSpriteFrame doesn't exist it will raise an exception.
      @since v0.9
      */
    public static CCSprite sprite(String spriteFrameName, boolean isFrame) {
        return new CCSprite(spriteFrameName, isFrame);
    }

    /** Creates an sprite with an image filepath.
      The rect used will be the size of the image.
      The offset will be (0,0).
      */
    public static CCSprite sprite(String filepath) {
        return new CCSprite(filepath);
    }

    /** Creates an sprite with an image filepath and a rect.
      The offset will be (0,0).
      */
    public static CCSprite sprite(String filepath, CGRect rect) {
        return new CCSprite(filepath, rect);
    }
    
    /** Creates an sprite with a CGImageRef.
     * BE AWARE OF the fact that copy of image is stored in memory,
     * use assets method if you can.
     * 
     * @deprecated Use spriteWithCGImage:key: instead. Will be removed in v1.0 final
     */
    public static CCSprite sprite(Bitmap image) {
        return new CCSprite(image);
    }

    /** Creates an sprite with a CGImageRef and a key.
      The key is used by the CCTextureCache to know if a texture was already created with this CGImage.
      For example, a valid key is: @"sprite_frame_01".
      If key is nil, then a new texture will be created each time by the CCTextureCache. 

    * BE AWARE OF the fact that copy of image is stored in memory,
    * use assets method if you can.
      
      @since v0.99.0
      */
    public static CCSprite sprite(Bitmap image, String key) {
        return new CCSprite(image, key);
    }

    /** Creates an sprite with an CCSpriteSheet and a rect
    */
    public static CCSprite sprite(CCSpriteSheet spritesheet, CGRect rect) {
        return new CCSprite(spritesheet, rect);
    }


    /** Initializes an sprite with a texture.
      The rect used will be the size of the texture.
      The offset will be (0,0).
      */
    public CCSprite(CCTexture2D texture) {
        CGSize size = texture.getContentSize();
        CGRect rect = CGRect.make(0, 0, size.width, size.height);
	    init(texture, rect);
    }
    
    public CCSprite(CCTexture2D texture, CGRect rect) {
    	init(texture, rect);
    }
    
    /** Initializes an sprite with a texture and a rect.
      The offset will be (0,0).
      */
    protected void init(CCTexture2D texture, CGRect rect) {
        assert texture!=null:"Invalid texture for sprite";
        // IMPORTANT: [self init] and not [super init];
        init();
        setTexture(texture);
        setTextureRect(rect);
    }

    /** Initializes an sprite with an sprite frame.
    */
    public CCSprite(CCSpriteFrame spriteFrame) {
    	init(spriteFrame);
    }
    
    protected void init(CCSpriteFrame spriteFrame) {
        assert spriteFrame!=null:"Invalid spriteFrame for sprite";
        
        rectRotated_ = spriteFrame.rotated_;
        init(spriteFrame.getTexture(), spriteFrame.getRect());
        setDisplayFrame(spriteFrame);    	
    }

    /** Initializes an sprite with an sprite frame name.
      An CCSpriteFrame will be fetched from the CCSpriteFrameCache by name.
      If the CCSpriteFrame doesn't exist it will raise an exception.
      @since v0.9
      */
    public CCSprite(String spriteFrameName, boolean isFrame) {
        assert spriteFrameName!=null:"Invalid spriteFrameName for sprite";
        CCSpriteFrame frame = CCSpriteFrameCache.sharedSpriteFrameCache()
            .getSpriteFrame(spriteFrameName);
        init(frame);
    }

    /** Initializes an sprite with an image filepath.
      The rect used will be the size of the image.
      The offset will be (0,0).
      */
    public CCSprite(String filepath) {
        assert filepath!=null:"Invalid filename for sprite";

        CCTexture2D texture = CCTextureCache.sharedTextureCache().addImage(filepath);
        if( texture != null) {
            CGRect rect = CGRect.make(0, 0, 0, 0);
            rect.size = texture.getContentSize();
            init(texture, rect);
        } else {
		ccMacros.CCLOGERROR("CCSprite", "Unable to load texture from file: " + filepath);
        }
    }

    public CCSprite() {
    	init();
    }
    
    /** Initializes an sprite with an image filepath, and a rect.
      The offset will be (0,0).
      */
    public CCSprite(String filepath, CGRect rect) {
        assert filepath!=null:"Invalid filename for sprite";

        CCTexture2D texture = CCTextureCache.sharedTextureCache().addImage(filepath);
        if( texture != null) {
            init(texture, rect);
        }
    }

    /** Initializes an sprite with a CGImageRef
      @deprecated Use spriteWithCGImage:key: instead. Will be removed in v1.0 final
      */
    public CCSprite(Bitmap image) {
        assert image!=null:"Invalid CGImageRef for sprite";

        // XXX: possible bug. See issue #349. New API should be added
        String key = image.toString();
        CCTexture2D texture = CCTextureCache.sharedTextureCache().addImage(image, key);

        CGSize size = texture.getContentSize();
        CGRect rect = CGRect.make(0, 0, size.width, size.height );

        init(texture, rect);
    }

    /** Initializes an sprite with a CGImageRef and a key
      The key is used by the CCTextureCache to know if a texture was already created with this CGImage.
      For example, a valid key is: @"sprite_frame_01".
      If key is nil, then a new texture will be created each time by the CCTextureCache. 
      @since v0.99.0
      */
    public CCSprite(Bitmap image, String key) {
        assert image!=null:"Invalid CGImageRef for sprite";

        // XXX: possible bug. See issue #349. New API should be added
        CCTexture2D texture = CCTextureCache.sharedTextureCache().addImage(image, key);

        CGSize size = texture.getContentSize();
        CGRect rect = CGRect.make(0, 0, size.width, size.height );

        init(texture, rect);
    }

    /** Initializes an sprite with an CCSpriteSheet and a rect
    */
    public CCSprite(CCSpriteSheet spritesheet, CGRect rect) {
        init(spritesheet.getTexture(), rect);
        useSpriteSheetRender(spritesheet);
    }

    /** updates the texture rect of the CCSprite.
    */

    
	public void setTextureRect(float x, float y, float w, float h, Boolean rotated) {
    	setTextureRect(x, y, w, h, w, h, rotated);
    }
	
	public void setTextureRect(CGRect rect, Boolean rotated) {
	    setTextureRect(rect, rect.size, rotated);
    }
	
    public void setTextureRect(CGRect rect) {
	    setTextureRect(rect, rectRotated_);
    }

    /** tell the sprite to use self-render.
      @since v0.99.0
      */
    public void useSelfRender() {
        atlasIndex = CCSpriteIndexNotInitialized;
        usesSpriteSheet_ = false;
        textureAtlas_ = null;
        spriteSheet_ = null;
        dirty_ = recursiveDirty_ = false;

        float x1 = 0 + offsetPosition_.x;
        float y1 = 0 + offsetPosition_.y;
        float x2 = x1 + rect_.size.width;
        float y2 = y1 + rect_.size.height;

        vertexes.position(0);
        vertexes.put(x1);
        vertexes.put(y2);
        vertexes.put(0);
        vertexes.put(x1);
        vertexes.put(y1);
        vertexes.put(0);
        vertexes.put(x2);
        vertexes.put(y2);
        vertexes.put(0);
        vertexes.put(x2);
        vertexes.put(y1);
        vertexes.put(0);
        vertexes.position(0);
    }

    /** tell the sprite to use sprite sheet render.
      @since v0.99.0
      */
    public void useSpriteSheetRender(CCSpriteSheet spriteSheet) {
        usesSpriteSheet_ = true;
        textureAtlas_ = spriteSheet.getTextureAtlas(); // weak ref
        spriteSheet_ = spriteSheet; // weak ref
    }

    protected void init() {
        texCoords = new FastFloatBuffer(4 * 2);
        vertexes  = new FastFloatBuffer(4 * 3);
        colors    = new FastFloatBuffer(4 * 4);
    	
		dirty_ = false;
        recursiveDirty_ = false;
		
		// zwoptex default values
		offsetPosition_ = CGPoint.zero();
		unflippedOffsetPositionFromCenter_ = new CGPoint();
        rect_ = CGRect.make(0, 0, 1, 1);
		
		// by default use "Self Render".
		// if the sprite is added to an SpriteSheet,
        // then it will automatically switch to "SpriteSheet Render"
		useSelfRender();
		
		opacityModifyRGB_			= true;
		opacity_					= 255;
		color_                      = new ccColor3B(ccColor3B.ccWHITE);
        colorUnmodified_	        = new ccColor3B(ccColor3B.ccWHITE);
				
		// update texture (calls updateBlendFunc)
		setTexture(null);
		
		flipY_ = flipX_ = false;
		
		// lazy alloc
		animations_ = null;
		
		// default transform anchor: center
		anchorPoint_.set(0.5f, 0.5f);
		
		
		honorParentTransform_ = CC_HONOR_PARENT_TRANSFORM_ALL;
		hasChildren_ = false;
		
		// Atlas: Color
		colors.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
		colors.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
		colors.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
		colors.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
		colors.position(0);
		
		// Atlas: Vertex		
		// updated in "useSelfRender"		
		// Atlas: TexCoords
		setTextureRect(0, 0, 0, 0, rectRotated_);
    }

    /** sets a new display frame to the CCSprite. */
    public void setDisplayFrame(CCSpriteFrame frame) {
        unflippedOffsetPositionFromCenter_.set(frame.offset_);

        CCTexture2D newTexture = frame.getTexture();
        // update texture before updating texture rect
        if ( texture_ == null || newTexture.name() != texture_.name())
            setTexture(newTexture);

        // update rect
        setTextureRect(frame.rect_, frame.originalSize_, frame.rotated_);
    }


    /** changes the display frame based on an animation and an index. */
    public void setDisplayFrame(String animationName, int frameIndex) {
        if (animations_ == null)
            initAnimationDictionary();

        CCAnimation anim = animations_.get(animationName);
        CCSpriteFrame frame = (CCSpriteFrame) anim.frames().get(frameIndex);
        setDisplayFrame(frame);
    }

    @Override
    public void setVisible(boolean v) {
        if( v != visible_ ) {
        	super.setVisible(v);
            if( usesSpriteSheet_ && ! recursiveDirty_ ) {
                dirty_ = recursiveDirty_ = true;
                if(children_ != null)
	                for (CCNode child:children_) {
	                    child.setVisible(v);
	                }
            }
        }
    }


    /** adds an Animation to the Sprite. */
    public void addAnimation(CCAnimation anim) {
        // lazy alloc
        if (animations_ == null)
            initAnimationDictionary();

        animations_.put(anim.name(), anim);
    }

    /** returns an Animation given it's name. */
    public CCAnimation animationByName(String animationName) {
        assert animationName != null : "animationName parameter must be non null";
        return animations_.get(animationName);
    }
    
    private static final ccColor4B tmpColor4B = ccColor4B.ccc4(0, 0, 0, 0);
    private static final ccColor4B[] tmpColors = new ccColor4B[] { tmpColor4B, tmpColor4B, tmpColor4B, tmpColor4B };
    public void updateColor() {		
        float tmpR = color_.r/255.f;
        float tmpG = color_.g/255.f;
        float tmpB = color_.b/255.f;
        float tmpA = opacity_/255.f;
        
        colors.put(tmpR).put(tmpG).put(tmpB).put(tmpA)
        	  .put(tmpR).put(tmpG).put(tmpB).put(tmpA)
        	  .put(tmpR).put(tmpG).put(tmpB).put(tmpA)
        	  .put(tmpR).put(tmpG).put(tmpB).put(tmpA);
        colors.position(0);
        
        // renders using Sprite Manager
        if( usesSpriteSheet_ ) {
            if( atlasIndex != CCSpriteIndexNotInitialized) {
            	tmpColor4B.r = color_.r; tmpColor4B.g = color_.g; tmpColor4B.b = color_.b; tmpColor4B.a = opacity_;
		textureAtlas_.updateColor(tmpColors, atlasIndex);
            	
            } else {
                // no need to set it recursively
                // update dirty_, don't update recursiveDirty_
                dirty_ = true;
            }
        }
        // self render
        // do nothing
    }
    
    public void setFlipX(boolean b) {
        if( flipX_ != b ) {
            flipX_ = b;
            setTextureRect(rect_);
        }
    }

    public boolean getFlipX() {
        return flipX_;
    }

    public void setFlipY(boolean b) {
        if( flipY_ != b ) {
            flipY_ = b;	
            setTextureRect(rect_);
        }	
    }

    public boolean getFlipY() {
        return flipY_;
    }

    public void setTexture(CCTexture2D texture) {
        assert ! usesSpriteSheet_: "CCSprite: setTexture doesn't work when the sprite is rendered using a CCSpriteSheet";

        // accept texture==nil as argument
        assert (texture==null || texture instanceof CCTexture2D) 
        	: "setTexture expects a CCTexture2D. Invalid argument";
        texture_ = texture;
        updateBlendFunc();
    }

    public CCTexture2D getTexture() {
        return texture_;
    }

    /** returns whether or not a CCSpriteFrame is being displayed */
    public boolean isFrameDisplayed(CCSpriteFrame frame) {
        CGRect r = frame.rect_;
        CGPoint p = frame.offset_;
        return (CGRect.equalToRect(r, rect_) &&
                frame.getTexture().name() == this.getTexture().name() &&
                CGPoint.equalToPoint(p, offsetPosition_));
    }

    /** returns the current displayed frame. */
    public CCSpriteFrame displayedFrame() {
	    return CCSpriteFrame.frame(getTexture(), rect_, CGPoint.zero());
    }

    // XXX HACK: optimization
    private void SET_DIRTY_RECURSIVELY() {						
        if( usesSpriteSheet_ && ! recursiveDirty_ ) {
            dirty_ = recursiveDirty_ = true;			
            if( hasChildren_)					
                setDirtyRecursively(true);
        }								
    }

    private void updateBlendFunc() {
        assert ! usesSpriteSheet_ :
            "CCSprite: updateBlendFunc doesn't work when the sprite is rendered using a CCSpriteSheet";

        // it's possible to have an untextured sprite
        if( texture_==null || !texture_.hasPremultipliedAlpha()) {
            blendFunc_.src = GL10.GL_SRC_ALPHA;
            blendFunc_.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
            setOpacityModifyRGB(false);
        } else {
            blendFunc_.src = ccConfig.CC_BLEND_SRC;
            blendFunc_.dst = ccConfig.CC_BLEND_DST;
            setOpacityModifyRGB(true);
        }
    }

    private void initAnimationDictionary() {
        animations_ = new HashMap<String, CCAnimation>();
    }

    private void setTextureRect(CGRect rect, CGSize size, Boolean rotated) {
    	setTextureRect(rect.origin.x, rect.origin.y, rect.size.width, rect.size.height, size.width, size.height, rotated);
    }
	private void setTextureRect(float x, float y, float w, float h, float sw, float sh, boolean rotated) {
        rect_.set(x, y, w, h);
        rectRotated_ = rotated;

        setContentSize(sw, sh);
        updateTextureCoords(rect_);

        float relativeOffsetX = unflippedOffsetPositionFromCenter_.x;
        float relativeOffsetY = unflippedOffsetPositionFromCenter_.y;
        
        // issue #732
        if( flipX_ )
        	relativeOffsetX = - relativeOffsetX;
        if( flipY_ )
        	relativeOffsetY = - relativeOffsetY;

        offsetPosition_.x = relativeOffsetX + (contentSize_.width - rect_.size.width) / 2;
        offsetPosition_.y = relativeOffsetY + (contentSize_.height - rect_.size.height) / 2;

        // rendering using SpriteSheet
        if( usesSpriteSheet_ ) {
            // update dirty_, don't update recursiveDirty_
            dirty_ = true;
        } else { // self rendering
            // Atlas: Vertex
            float x1 = 0 + offsetPosition_.x;
            float y1 = 0 + offsetPosition_.y;
            float x2 = x1 + w;
            float y2 = y1 + h;

            // Don't update Z.
            vertexes.position(0);
            vertexes.put(x1);
            vertexes.put(y2);
            vertexes.put(0);
            vertexes.put(x1);
            vertexes.put(y1);
            vertexes.put(0);
            vertexes.put(x2);
            vertexes.put(y2);
            vertexes.put(0);
            vertexes.put(x2);
            vertexes.put(y1);
            vertexes.put(0);
            vertexes.position(0);
        }
    }
	
    // XXX: Optimization: instead of calling 5 times the parent sprite to obtain: position, scale.x, scale.y, anchorpoint and rotation,
    // this fuction return the 5 values in 1 single call
    protected TransformValues getTransformValues() {
        TransformValues tv = new TransformValues();
        tv.pos = position_;
        tv.scale = CGPoint.ccp(scaleX_, scaleY_);
        tv.rotation = rotation_;
        tv.ap = anchorPointInPixels_;

        return tv;
    }

    public boolean doesOpacityModifyRGB() {
        return opacityModifyRGB_;
    }

    public void setDirtyRecursively(boolean b) {
        dirty_ = recursiveDirty_ = b;
        // recursively set dirty
        if( hasChildren_ ) {
        	for (CCNode child: children_) {
        		CCSprite sprite = (CCSprite)child;
        		sprite.setDirtyRecursively(true);
        	}
        }
    }

    public void setPosition(CGPoint pos) {
        super.setPosition(pos);
        SET_DIRTY_RECURSIVELY();
    }
    
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        SET_DIRTY_RECURSIVELY();
    }
    
    public void setRotation(float rot) {
        super.setRotation(rot);
        SET_DIRTY_RECURSIVELY();
    }

    public void setScaleX(float sx) {
        super.setScaleX(sx);
        SET_DIRTY_RECURSIVELY();
    }

    public void setScaleY(float sy) {
        super.setScaleY(sy);
        SET_DIRTY_RECURSIVELY();
    }

    public void setScale(float s) {
        super.setScale(s);
        SET_DIRTY_RECURSIVELY();
    }

    public void setVertexZ(float z) {
        super.setVertexZ(z);
        SET_DIRTY_RECURSIVELY();
    }

    public void setAnchorPoint(CGPoint anchor) {
        super.setAnchorPoint(anchor);
        SET_DIRTY_RECURSIVELY();
    }

    public void setRelativeAnchorPoint(boolean relative) {
        assert !usesSpriteSheet_:"relativeTransformAnchor is invalid in CCSprite";
        super.setRelativeAnchorPoint(relative);
    }

    public void reorderChild(CCNode child, int z) {
        // assert child != null: "Child must be non-nil";
        // assert children_.has(child): "Child doesn't belong to Sprite";

        if( z == child.getZOrder() )
            return;

        if( usesSpriteSheet_ ) {
            // XXX: Instead of removing/adding, it is more efficient to reorder manually
            removeChild(child, false);
            addChild(child, z);
        } else {
            super.reorderChild(child, z);
        }
    }

    @Override
    public CCNode addChild(CCNode child, int z, int aTag) {
        super.addChild(child, z, aTag);
        
        if(child instanceof CCSprite && usesSpriteSheet_) {
        	CCSprite sprite = (CCSprite)child;
            int index = spriteSheet_.atlasIndex(sprite, z);
            spriteSheet_.insertChild(sprite, index);
        }

        hasChildren_ = true;

        return this;
    }

    public void removeChild(CCNode node, boolean doCleanup) {
        if( usesSpriteSheet_ ) {
        	CCSprite sprite = (CCSprite) node;
            spriteSheet_.removeSpriteFromAtlas(sprite);
        }

        super.removeChild(node, doCleanup);

        hasChildren_ = (children_.size() > 0);
    }

    public void removeAllChildren(boolean doCleanup) {
        if( usesSpriteSheet_ ) {
            for( CCNode child : children_ ) {
            	CCSprite sprite = (CCSprite)child;
            	spriteSheet_.removeSpriteFromAtlas(sprite);
            }
        }

        super.removeAllChildren(doCleanup);
        hasChildren_ = false;
    }

    public void draw(GL10 gl) {	
        assert !usesSpriteSheet_:"If CCSprite is being rendered by CCSpriteSheet, CCSprite#draw SHOULD NOT be called";

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Unneeded states: -

        boolean newBlend = false;
        if( blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST ) {
            newBlend = true;
            gl.glBlendFunc( blendFunc_.src, blendFunc_.dst );
        }

//        ((EGL10) gl).eglWaitNative(EGL10.EGL_NATIVE_RENDERABLE, null);
//        // bug fix in case texture name = 0
//        texture_.checkName();
        // #define kQuadSize sizeof(quad_.bl)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_.name());

        // int offset = (int)&quad_;

        // vertex
        // int diff = offsetof( ccV3F_C4B_T2F, vertices);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0 , vertexes.bytes);

        // color
        // diff = offsetof( ccV3F_C4B_T2F, colors);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors.bytes);

        // tex coords
        // diff = offsetof( ccV3F_C4B_T2F, texCoords);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords.bytes);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if( newBlend )
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        /*
        if (ccConfig.CC_SPRITE_DEBUG_DRAW) {
            CGSize s = this.contentSize();
            CGPoint vertices[]= new CGPoint [] {
                CGPoint.ccp(0,0),   CGPoint.ccp(s.width,0),
                CGPoint.ccp(s.width,s.height),  CGPoint.ccp(0,s.height)
            };
            ccDrawingPrimitives.ccDrawPoly(vertices, 4, true);
        } // CC_TEXTURENODE_DEBUG_DRAW
        */
    }

    private void updateTextureCoords(CGRect rect) {    	
        float atlasWidth = 1;
        float atlasHeight = 1;

        if (texture_ != null) {
        	 atlasWidth = texture_.pixelsWide();
        	 atlasHeight = texture_.pixelsHigh();
        }
        
        if (rectRotated_)
        {
        	float left	= (2*rect.origin.x+1)/(2*atlasWidth);
        	float right	= left+(rect.size.height*2-2)/(2*atlasWidth);
        	float top	= (2*rect.origin.y+1)/(2*atlasHeight);
        	float bottom= top+(rect.size.width*2-2)/(2*atlasHeight);
	
	        if( flipX_) {
	        	float tmp = top;
	            top = bottom;
	            bottom = tmp;
	        }
	
	        if( flipY_) {
	            float tmp = left;
	            left = right;
	            right = tmp;
	        }
	
	        texCoords.put(0, right); // tl u
	        texCoords.put(1, top); // tl v
	        texCoords.put(2, left); // bl u
	        texCoords.put(3, top); // bl v   
	        texCoords.put(4, right); // tr u
	        texCoords.put(5, bottom); // tr v
	        texCoords.put(6, left); // br u
	        texCoords.put(7, bottom); // br v
        }else
        {
        	float left	= (2*rect.origin.x+1)/(2*atlasWidth);
        	float right	= left + (rect.size.width*2-2)/(2*atlasWidth);
        	float top	= (2*rect.origin.y+1)/(2*atlasHeight);
        	float bottom= top + (rect.size.height*2-2)/(2*atlasHeight);
	
	        if( flipX_) {
	            float tmp = left;
	            left = right;
	            right = tmp;
	        }
	
	        if( flipY_) {
	            float tmp = top;
	            top = bottom;
	            bottom = tmp;
	        }
	
	        texCoords.put(0, left); // tl u
	        texCoords.put(1, top); // tl v
	        texCoords.put(2, left); // bl u
	        texCoords.put(3, bottom); // bl v   
	        texCoords.put(4, right); // tr u
	        texCoords.put(5, top); // tr v
	        texCoords.put(6, right); // br u
	        texCoords.put(7, bottom); // br v
        }
        
        texCoords.position(0);
        
        if(usesSpriteSheet_)
		textureAtlas_.putTexCoords( texCoords, atlasIndex);
    }

    private final static CGAffineTransform tmpMatrix = CGAffineTransform.identity();
    private final static CGAffineTransform tmpNewMatrix = CGAffineTransform.identity();
    private final static float tmpV[] = new float[] { 
        	0, 0, 0 , 	0, 0, 0,
        	0, 0, 0,  	0, 0, 0
        };  
    /** updates the quad according the the rotation, position, scale values.
    */
    public void updateTransform() {
    	tmpMatrix.setToIdentity();

        // Optimization: if it is not visible, then do nothing
        if( ! visible_ ) {
        	Arrays.fill(tmpV, 0);
		textureAtlas_.putVertex(textureAtlas_.getVertexBuffer(), tmpV, atlasIndex);
            dirty_ = recursiveDirty_ = false;
            return ;
        }

        // Optimization: If parent is spritesheet, or parent is nil
        // build Affine transform manually
        if( parent_==null || parent_ == spriteSheet_ ) {
            float radians = -ccMacros.CC_DEGREES_TO_RADIANS(rotation_);
            float c = (float)Math.cos(radians);
            float s = (float)Math.sin(radians);
            
            tmpMatrix.set(c * scaleX_,  s * scaleX_,
                    -s * scaleY_, c * scaleY_,
                    position_.x, position_.y);

            tmpMatrix.translate(-anchorPointInPixels_.x, -anchorPointInPixels_.y);
        } 

        // else do affine transformation according to the HonorParentTransform
        else if( parent_ != spriteSheet_ ) {

            int prevHonor = CC_HONOR_PARENT_TRANSFORM_ALL;

            for (CCNode p = this; p != null && p != spriteSheet_; p = p.getParent()) {
            	CCSprite sprP = (CCSprite)p;
                
                tmpNewMatrix.setToIdentity();
                // 2nd: Translate, Rotate, Scale
                if( (prevHonor & CC_HONOR_PARENT_TRANSFORM_TRANSLATE) !=0 )
                	tmpNewMatrix.translate(sprP.position_.x, sprP.position_.y);
                if( (prevHonor & CC_HONOR_PARENT_TRANSFORM_ROTATE) != 0 )
                	tmpNewMatrix.rotate(-ccMacros.CC_DEGREES_TO_RADIANS(sprP.rotation_));
                if( (prevHonor & CC_HONOR_PARENT_TRANSFORM_SCALE) != 0 ) {
                	tmpNewMatrix.scale(sprP.scaleX_, sprP.scaleY_);
                }

                // 3rd: Translate anchor point
                tmpNewMatrix.translate(-sprP.anchorPointInPixels_.x, -sprP.anchorPointInPixels_.y);
                // 4th: Matrix multiplication
                tmpMatrix.multiply(tmpNewMatrix);
                prevHonor = sprP.honorParentTransform_;
            }		
        }

        //
        // calculate the Quad based on the Affine Matrix
        //	

        CGSize size = rect_.size;

        float x1 = offsetPosition_.x;
        float y1 = offsetPosition_.y;

        float x2 = x1 + size.width;
        float y2 = y1 + size.height;
        float x = (float) tmpMatrix.m02;
        float y = (float) tmpMatrix.m12;

        float cr = (float) tmpMatrix.m00;
        float sr = (float) tmpMatrix.m10;
        float cr2 = (float) tmpMatrix.m11;
        float sr2 = (float) -tmpMatrix.m01;

        float ax = x1 * cr - y1 * sr2 + x;
        float ay = x1 * sr + y1 * cr2 + y;

        float bx = x2 * cr - y1 * sr2 + x;
        float by = x2 * sr + y1 * cr2 + y;

        float cx = x2 * cr - y2 * sr2 + x;
        float cy = x2 * sr + y2 * cr2 + y;

        float dx = x1 * cr - y2 * sr2 + x;
        float dy = x1 * sr + y2 * cr2 + y;

        tmpV[0] = dx; tmpV[1] = dy; tmpV[2] = vertexZ_;   
        tmpV[3] = ax; tmpV[4] = ay; tmpV[5] = vertexZ_;   
        tmpV[6] = cx; tmpV[7] = cy; tmpV[8] = vertexZ_;   
        tmpV[9] = bx; tmpV[10] = by; tmpV[11] = vertexZ_;   

        textureAtlas_.putVertex(textureAtlas_.getVertexBuffer(), tmpV, atlasIndex);
        dirty_ = recursiveDirty_ = false;
    }

	public ccBlendFunc getBlendFunc() {
		return blendFunc_;
	}

	public void setBlendFunc(ccBlendFunc blendFunc) {
		blendFunc_ = blendFunc;
	}

}

