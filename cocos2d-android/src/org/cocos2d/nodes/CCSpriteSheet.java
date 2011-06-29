package org.cocos2d.nodes;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.protocols.CCTextureProtocol;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.utils.FastFloatBuffer;

/** CCSpriteSheet is like a batch node: if it contains children, it will draw them in 1 single OpenGL call
 * (often known as "batch draw").
 *
 * A CCSpriteSheet can reference one and only one texture (one image file, one texture atlas).
 * Only the CCSprites that are contained in that texture can be added to the CCSpriteSheet.
 * All CCSprites added to a CCSpriteSheet are drawn in one OpenGL ES draw call.
 * If the CCSprites are not added to a CCSpriteSheet then an OpenGL ES draw call will be needed for each one, which is less efficient.
 *
 *
 * Limitations:
 *  - The only object that is accepted as child (or grandchild, grand-grandchild, etc...) is CCSprite or any subclass of CCSprite. eg: particles, labels and layer can't be added to a CCSpriteSheet.
 *  - Either all its children are Aliased or Antialiased. It can't be a mix. This is because "alias" is a property of the texture, and all the sprites share the same texture.
 * 
 * @since v0.7.1
 */
public class CCSpriteSheet extends CCNode implements CCTextureProtocol {
    public static final int defaultCapacity = 29;

    /** returns the TextureAtlas that is used */
    protected CCTextureAtlas	textureAtlas_;
    public CCTextureAtlas getTextureAtlas() {
        return textureAtlas_;
    }

    /** conforms to CCTextureProtocol protocol */
    ccBlendFunc		blendFunc_;
    public ccBlendFunc getBlendFunc() {
        return blendFunc_;
    }

    /** descendants (children, gran children, etc) */
    ArrayList<CCSprite>	descendants_;

    /** creates a CCSpriteSheet with a texture2d and a default capacity of 29 children.
      The capacity will be increased in 33% in runtime if it run out of space.
      */
    public static CCSpriteSheet spriteSheet(CCTexture2D tex) {
        return new CCSpriteSheet(tex, defaultCapacity);
    }

    /** creates a CCSpriteSheet with a texture2d and capacity of children.
      The capacity will be increased in 33% in runtime if it run out of space.
      */
    public static CCSpriteSheet spriteSheet(CCTexture2D tex, int capacity) {
        return new CCSpriteSheet(tex, capacity);
    }

    /** creates a CCSpriteSheet with a file image (.png, .jpeg, .pvr, etc) with a default capacity of 29 children.
      The capacity will be increased in 33% in runtime if it run out of space.
      The file will be loaded using the TextureMgr.
      */
    public static CCSpriteSheet spriteSheet(String fileImage) {
        return new CCSpriteSheet(fileImage, defaultCapacity);
    }

    /** creates a CCSpriteSheet with a file image (.png, .jpeg, .pvr, etc) and capacity of children.
      The capacity will be increased in 33% in runtime if it run out of space.
      The file will be loaded using the TextureMgr.
      */
    public static CCSpriteSheet spriteSheet(String fileImage, int capacity) {
        return new CCSpriteSheet(fileImage, capacity);
    }

    /** initializes a CCSpriteSheet with a texture2d and capacity of children.
      The capacity will be increased in 33% in runtime if it run out of space.
      */
    protected CCSpriteSheet(CCTexture2D tex, int capacity) {
    	blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
        textureAtlas_ = new CCTextureAtlas(tex, capacity);

        updateBlendFunc();

        // no lazy alloc in this node
        children_ = new ArrayList<CCNode>();
        descendants_ = new ArrayList<CCSprite>();
    }

    /** initializes a CCSpriteSheet with a file image (.png, .jpeg, .pvr, etc) and a capacity of children.
      The capacity will be increased in 33% in runtime if it run out of space.
      The file will be loaded using the TextureMgr.
      */
    protected CCSpriteSheet(String fileImage, int capacity) {
        this(CCTextureCache.sharedTextureCache().addImage(fileImage), capacity);
    }

    /** creates an sprite with a rect in the CCSpriteSheet.
      It's the same as:
      - create an standard CCSsprite
      - set the usingSpriteSheet = YES
      - set the textureAtlas to the same texture Atlas as the CCSpriteSheet
      @deprecated Use [CCSprite spriteWithSpriteSheet:rect] instead;
      */
    public CCSprite createSprite(CGRect rect) {
        CCSprite sprite = CCSprite.sprite(textureAtlas_.getTexture(), rect);
        sprite.useSpriteSheetRender(this);

        return sprite;
    }

    // override visit.
    // Don't call visit on it's children
    @Override
    public void visit(GL10 gl) {

        // CAREFUL:
        // This visit is almost identical to CocosNode#visit
        // with the exception that it doesn't call visit on it's children
        //
        // The alternative is to have a void CCSprite#visit, but
        // although this is less mantainable, is faster
        //
        if (!visible_)
            return;

        gl.glPushMatrix();

        if ( grid_!=null && grid_.isActive()) {
            grid_.beforeDraw(gl);
            transformAncestors(gl);
        }

        transform(gl);

        draw(gl);

        if ( grid_!=null && grid_.isActive()) {
            grid_.afterDraw(gl, this);
        }

        gl.glPopMatrix();
    }

    /** initializes a previously created sprite with a rect. This sprite will have the same texture as the CCSpriteSheet.
      It's the same as:
      - initialize an standard CCSsprite
      - set the usingSpriteSheet = YES
      - set the textureAtlas to the same texture Atlas as the CCSpriteSheet
      @since v0.99.0
      @deprecated Use [CCSprite initWithSpriteSheet:rect] instead;
      */ 
    public CCSprite initSprite(CGRect rect) {
    	CCSprite sprite = new CCSprite(textureAtlas_.getTexture(), rect);
        sprite.useSpriteSheetRender(this);
        return sprite;
    }

    // override addChild:
    @Override
    public CCNode addChild(CCNode child, int z, int aTag) {
        // NSAssert( child != nil, @"Argument must be non-nil");
        // NSAssert( [child isKindOfClass:[CCSprite class]], @"CCSpriteSheet only supports CCSprites as children");
        // NSAssert( child.texture.name == textureAtlas_.texture.name, @"CCSprite is not using the same texture id");

    	super.addChild(child, z, aTag);
    	
    	CCSprite sprite = (CCSprite)child;

        int index = atlasIndex(sprite, z);
        insertChild(sprite, index);
        
        sprite.updateColor();

        return child;
    }

    // override reorderChild
    public void reorderChild(CCNode child, int z) {
        // NSAssert( child != nil, @"Child must be non-nil");
        // NSAssert( [children_ containsObject:child], @"Child doesn't belong to Sprite" );

        if( z == child.getZOrder())
            return;

        // XXX: Instead of removing/adding, it is more efficient to reorder manually
        removeChild(child, false);
        addChild(child, z);
    }

    /** removes a child given a reference. It will also cleanup the running actions depending on the cleanup parameter.
      @warning Removing a child from a CCSpriteSheet is very slow
      */
    public void removeChild(CCNode child, boolean doCleanup) {
    	CCSprite sprite = (CCSprite)child;
        // explicit nil handling
        if (sprite == null)
            return;

        // NSAssert([children_ containsObject:sprite], @"CCSpriteSheet doesn't contain the sprite. Can't remove it");

        // cleanup before removing
        removeSpriteFromAtlas(sprite);
        super.removeChild(sprite, doCleanup);
    }

    /** removes a child given a certain index. It will also cleanup the running actions depending on the cleanup parameter.
      @warning Removing a child from a CCSpriteSheet is very slow
      */
    public void removeChildAtIndex(int index, boolean doCleanup) {
    	CCSprite sprite = (CCSprite)children_.get(index);
        removeChild(sprite, doCleanup);
    }

    public void removeAllChildren(boolean doCleanup) {
        // Invalidate atlas index. issue #569
        for (CCNode node: children_) {
        	CCSprite sprite = (CCSprite)node;
        	sprite.useSelfRender();
        }

        super.removeAllChildren(doCleanup);

        descendants_.clear();
        textureAtlas_.removeAllQuads();
    }

    @Override
    public void draw(GL10 gl) {
        if( textureAtlas_.getTotalQuads() == 0 )
            return;

        final int descendants_Num = descendants_.size();
        for (int i = 0; i < descendants_Num; i++) {
        	CCSprite child = descendants_.get(i);
            // fast dispatch
            // if( dirtyMethod(child, selDirty) )
            //    updateMethod(child, selUpdate);
            if (child.dirty_) {
                child.updateTransform();
            }

            if(ccConfig.CC_SPRITESHEET_DEBUG_DRAW) {
                CGRect rect = child.getBoundingBox();
                CGPoint vertices[]={
                    CGPoint.ccp(rect.origin.x,rect.origin.y),
                    CGPoint.ccp(rect.origin.x+rect.size.width,rect.origin.y),
                    CGPoint.ccp(rect.origin.x+rect.size.width,rect.origin.y+rect.size.height),
                    CGPoint.ccp(rect.origin.x,rect.origin.y+rect.size.height),
                };
                CCDrawingPrimitives.ccDrawPoly(gl, vertices, 4, true);
            } // CC_SPRITESHEET_DEBUG_DRAW
        }

        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Unneeded states: -

        boolean newBlend = false;
        if( blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST ) {
            newBlend = true;
            gl.glBlendFunc( blendFunc_.src, blendFunc_.dst );
        }

        textureAtlas_.drawQuads(gl);
        if( newBlend )
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
    }

    private void increaseAtlasCapacity() {
        // if we're going beyond the current TextureAtlas's capacity,
        // all the previously initialized sprites will need to redo their texture coords
        // this is likely computationally expensive
        int quantity = (textureAtlas_.capacity() + 1) * 4 / 3;

        ccMacros.CCLOG("CCSpriteSheet", "resizing TextureAtlas capacity from [" 
        		+ String.valueOf(textureAtlas_.capacity())  +"] to [" 
        		+ String.valueOf(quantity) + "].");


        textureAtlas_.resizeCapacity(quantity);
    }


    public int rebuildIndexInOrder(CCSprite node, int index) {
        for (CCNode n: node.getChildren()) {
        	CCSprite sprite = (CCSprite)n;
            if( sprite.getZOrder() < 0 ) {
                index = rebuildIndexInOrder(sprite, index);
            }
        }

        // ignore self (spritesheet)
        if( (CCNode)node != (CCNode)this) {
            node.atlasIndex = index;
            index++;
        }

        for (CCNode o: node.getChildren()) {
        	CCSprite sprite = (CCSprite)o;
            if( sprite.getZOrder() >= 0 )
                index = rebuildIndexInOrder(sprite, index);
        }

        return index;
    }

    public int highestAtlasIndexInChild(CCSprite sprite) {
        List<CCNode> array = sprite.getChildren();
        if (array == null)
		return sprite.atlasIndex;
        
        int count = array.size();
        if( count == 0 )
            return sprite.atlasIndex;
        else
            return highestAtlasIndexInChild((CCSprite)array.get(count - 1));
    }

    public int lowestAtlasIndexInChild(CCSprite sprite) {
        List<CCNode> array = sprite.getChildren();
        int count = array.size();
        if( count == 0 )
            return sprite.atlasIndex;
        else
            return lowestAtlasIndexInChild((CCSprite)array.get(0));
    }


    public int atlasIndex(CCSprite sprite, int z) {
        List<CCNode> brothers = sprite.getParent().getChildren();
        int childIndex = brothers.indexOf(sprite);

        // ignore parent Z if parent is spriteSheet
        boolean ignoreParent = ( sprite.getParent() == this );
        CCSprite previous = null;
        if( childIndex > 0 )
            previous = (CCSprite) brothers.get(childIndex-1);

        // first child of the sprite sheet
        if( ignoreParent ) {
            if( childIndex == 0 )
                return 0;
            // else
            return highestAtlasIndexInChild(previous) + 1;
        }

        // parent is a CCSprite, so, it must be taken into account
        // first child of an CCSprite ?
        if( childIndex == 0 ) {
            CCSprite p = (CCSprite) sprite.getParent();

            // less than parent and brothers
            if( z < 0 )
                return p.atlasIndex;
            else
                return p.atlasIndex+1;
        } else {
            // previous & sprite belong to the same branch
            if( ( previous.getZOrder() < 0 && z < 0 )|| (previous.getZOrder() >= 0 && z >= 0) ) {
                return highestAtlasIndexInChild(previous) + 1;
            }
            // else (previous < 0 and sprite >= 0 )
            CCSprite p = (CCSprite) sprite.getParent();
            return p.atlasIndex + 1;
        }
    }

    // add child helper
    protected void insertChild(CCSprite sprite, int index) {
        sprite.useSpriteSheetRender(this);
        sprite.atlasIndex = index;
        sprite.dirty_ = true;

        if(textureAtlas_.getTotalQuads() == textureAtlas_.capacity()) {
            increaseAtlasCapacity();
        }

        textureAtlas_.insertQuad(sprite.getTexCoords(), sprite.getVertices(), index);
        descendants_.add(index, sprite);
        
        // update indices
        int i = index + 1;
        CCSprite child;
        for(; i<descendants_.size(); i++){
            child = descendants_.get(i);
            child.atlasIndex = child.atlasIndex + 1;
        }

        // add children recursively
        if (sprite.getChildren() != null) {
        	for (CCNode o: sprite.getChildren()) {
        		child = (CCSprite)o;
        		int idx = atlasIndex(child, child.getZOrder());
        		insertChild(child, idx);
        	}
        }
    }

    // remove child helper
    public void removeSpriteFromAtlas(CCSprite sprite) {
        // remove from TextureAtlas
        textureAtlas_.removeQuad(sprite.atlasIndex);

        // Cleanup sprite. It might be reused (issue #569)
        sprite.useSelfRender();

        int index = descendants_.indexOf(sprite);
        if( index != -1 ) {
        	descendants_.remove(index);

            // update all sprites beyond this one
            int count = descendants_.size();

            for(; index < count; index++) {
                CCSprite s = descendants_.get(index);
                s.atlasIndex = s.atlasIndex - 1;
            }
        }

        // remove children recursively
        if (sprite.getChildren() != null) {
            for(CCNode o : sprite.getChildren()) {
                removeSpriteFromAtlas((CCSprite)o);
            }
        }
    }

    public void updateBlendFunc() {
        if( ! textureAtlas_.getTexture().hasPremultipliedAlpha()) {
            blendFunc_.src = GL10.GL_SRC_ALPHA;
            blendFunc_.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
        }
    }

    public void setTexture(CCTexture2D texture) {
        textureAtlas_.setTexture(texture);
        updateBlendFunc();
    }

    public CCTexture2D getTexture() {
        return textureAtlas_.getTexture();
    }

	public void setBlendFunc(ccBlendFunc blendFunc) {
		// TODO Auto-generated method stub
		blendFunc_ = blendFunc;
	}



	/* IMPORTANT XXX IMPORTNAT:
	 * These 2 methods can't be part of CCTMXLayer since they call [super add...], and CCSpriteSheet#add SHALL not be called
	 */
	/* Adds a quad into the texture atlas but it won't be added into the children array.
	 This method should be called only when you are dealing with very big AtlasSrite and when most of the CCSprite won't be updated.
	 For example: a tile map (CCTMXMap) or a label with lots of characgers (BitmapFontAtlas)
	 */
	protected void addQuadFromSprite(CCSprite sprite, int index) {
		assert(sprite != null): "Argument must be non-nil";
		assert(sprite.getClass().equals(CCSprite.class)): "CCSpriteSheet only supports CCSprites as children";

		while(index >= textureAtlas_.capacity() || textureAtlas_.capacity() == textureAtlas_.getTotalQuads() )
			this.increaseAtlasCapacity();

		//
		// update the quad directly. Don't add the sprite to the scene graph
		//

		sprite.useSpriteSheetRender(this);
		sprite.atlasIndex	= index;

		FastFloatBuffer texCordBuffer = sprite.getTexCoords();
		FastFloatBuffer vertexBuffer  = sprite.getVertices();
		textureAtlas_.insertQuad(texCordBuffer, vertexBuffer, index);

		// XXX: updateTransform will update the textureAtlas too using updateQuad.
		// XXX: so, it should be AFTER the insertQuad
		sprite.updateTransform();
	}

	/* This is the opposite of "addQuadFromSprite.
	 It add the sprite to the children and descendants array, but it doesn't update add it to the texture atlas
	 */
	protected CCSpriteSheet addSpriteWithoutQuad(CCSprite child, int z, int aTag) {
		assert(child != null): "Argument must be non-nil";
		assert(child.getClass().equals(CCSprite.class)):"CCSpriteSheet only supports CCSprites as children";

		// quad index is Z
		child.atlasIndex = z;

		// XXX: optimize with a binary search
		int i = 0;
		for (CCSprite c : descendants_ ) {
			if( c.atlasIndex >= z )
				break;
			i++;
		}
		descendants_.add(i, child);

		// IMPORTANT: Call super, and not self. Avoid adding it to the texture atlas array
		super.addChild(child, z, aTag);
		return this;
	}

}

