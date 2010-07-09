package org.cocos2d.nodes;

import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.TextureAtlas;
import org.cocos2d.opengl.Primitives;
import org.cocos2d.types.ccMacros;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.CCRect;
import org.cocos2d.types.CCPoint;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class AtlasSpriteManager extends CocosNode {

    // private static final String LOG_TAG = AtlasSpriteManager.class.getSimpleName();

    public static final int defaultCapacity = 29;

    private static boolean DEBUG_DRAW = false;
    int totalSprites_;
    TextureAtlas textureAtlas_;

    public TextureAtlas atlas() {
        return textureAtlas_;
    }

    private ccBlendFunc blendFunc_;

    /*
    * init with Texture2D
    */
    public AtlasSpriteManager(CCTexture2D tex) {
        this(tex, defaultCapacity);
    }

    public AtlasSpriteManager(CCTexture2D tex, int capacity) {

        blendFunc_ = new ccBlendFunc(ccMacros.CC_BLEND_SRC, ccMacros.CC_BLEND_DST);

        textureAtlas_ = new TextureAtlas(tex, capacity);

        updateBlendFunc();
        
        // no lazy alloc in this node
        children = new ArrayList<CocosNode>(capacity);
    }

    /*
     * init with FileImage
     */
    public AtlasSpriteManager(String imageFile) {
        this(imageFile, defaultCapacity);
    }

    public AtlasSpriteManager(String fileImage, int capacity) {

        blendFunc_ = new ccBlendFunc(ccMacros.CC_BLEND_SRC, ccMacros.CC_BLEND_DST);

        textureAtlas_ = new TextureAtlas(fileImage, capacity);

        updateBlendFunc();

        // no lazy alloc in this node
        children = new ArrayList<CocosNode>(capacity);
    }


    // TODO: CAREFUL:
    // This visit is almost identical to CocosNode#visit
    // with the exception that it doesn't call visit on its children
    //
    // The alternative is to have a void AtlasSprite#visit, but this
    // although is less mantainable, is faster
    //

    @Override
    public void visit(GL10 gl) {

        if (!visible_)
            return;

        gl.glPushMatrix();

        if (getGrid() != null && getGrid().isActive())
            getGrid().beforeDraw(gl);

        transform(gl);

        draw(gl);

        if (getGrid() != null && getGrid().isActive())
            getGrid().afterDraw(gl, getCamera());

        gl.glPopMatrix();
    }

    private int indexForNewChild(int z) {
        int index = 0;

        for (int i = 0; i < children.size(); i++) {
            CocosNode sprite = children.get(i);
            if (sprite.getZOrder() > z) {
                break;
            }
            index++;
        }

        return index;
    }

    @Override
    public CocosNode addChild(CocosNode node, int z, int aTag) {
        assert node != null : "Argument must be non-null";
        assert node instanceof AtlasSprite : "AtlasSpriteManager only supports AtlasSprites as children";

        AtlasSprite child = (AtlasSprite) node;

        if (totalSprites_ == textureAtlas_.capacity())
            resizeAtlas();

        int index = indexForNewChild(z);
        child.insertInAtlas(index);

        if (textureAtlas_.withColorArray())
            child.updateColor();

        totalSprites_++;
        super.addChild(child, z, aTag);

        int count = children.size();
        index++;
        for (; index < count; index++) {
            AtlasSprite sprite = (AtlasSprite) children.get(index);
            assert sprite.atlasIndex() == index - 1 : "AtlasSpriteManager: index failed";
            sprite.setIndex(index);
        }

        return this;
    }

    @Override
    public void removeChild(CocosNode node, boolean doCleanup) {
        AtlasSprite child = (AtlasSprite) node;

        // explicit null handling
        if (child == null)
            return;
        // ignore non-children
        if (!children.contains(child))
            return;

        int index = child.atlasIndex();
        super.removeChild(child, doCleanup);

        textureAtlas_.removeQuad(index);

        // update all sprites beyond this one
        int count = children.size();
        for (; index < count; index++) {
            AtlasSprite other = (AtlasSprite) children.get(index);
            assert other.atlasIndex() == index + 1 : "AtlasSpriteManager: index failed";
            other.setIndex(index);
        }
        totalSprites_--;
    }

    @Override
    public void reorderChild(CocosNode node, int z) {
        AtlasSprite child = (AtlasSprite) node;

        // reorder child in the children array
        super.reorderChild(child, z);


        // What's the new atlas index ?
        int newAtlasIndex = 0;
        for (int i = 0; i < children.size(); i++) {
            CocosNode sprite = children.get(i);
            if (sprite == child)
                break;
            newAtlasIndex++;
        }

        if (newAtlasIndex != child.atlasIndex()) {

            textureAtlas_.insertQuad(child.atlasIndex(), newAtlasIndex);

            // update atlas index
            int count = Math.max(newAtlasIndex, child.atlasIndex());
            int index = Math.min(newAtlasIndex, child.atlasIndex());
            for (; index < count + 1; index++) {
                AtlasSprite sprite = (AtlasSprite) children.get(index);
                sprite.setIndex(index);
            }
        }
    }

    @Override
    public void removeChild(int index, boolean doCleanup) {
        super.removeChild(children.get(index), doCleanup);
    }

    @Override
    public void removeAllChildren(boolean doCleanup) {
        super.removeAllChildren(doCleanup);

        totalSprites_ = 0;
        textureAtlas_.removeAllQuads();
    }

    @Override
    public void draw(GL10 gl) {
        if(textureAtlas_.getTotalQuads() == 0)
            return;

        
        for (int i = 0; i < children.size(); i++) {
            AtlasSprite sprite = (AtlasSprite) children.get(i);
            if (sprite.isDirty())
                sprite.updatePosition();
                sprite.updateColor();

            if (DEBUG_DRAW) {
		        CCRect rect = sprite.getBoundingBox(); //Inssue 528
                CCPoint[] vertices= {
                    CCPoint.ccp(rect.origin.x, rect.origin.y),
                    CCPoint.ccp(rect.origin.x+rect.size.width, rect.origin.y),
                    CCPoint.ccp(rect.origin.x+rect.size.width, rect.origin.y+rect.size.height),
                    CCPoint.ccp(rect.origin.x, rect.origin.y+rect.size.height),
                };
		        Primitives.drawPoly(gl, vertices, 4, true);
            }
        }

        if (totalSprites_ > 0) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            if (textureAtlas_.withColorArray())
                gl.glEnableClientState(GL10.GL_COLOR_ARRAY);


            gl.glEnable(GL10.GL_TEXTURE_2D);

            boolean newBlend = false;
            if( blendFunc_.src != ccMacros.CC_BLEND_SRC || blendFunc_.dst != ccMacros.CC_BLEND_DST ) {
                newBlend = true;
                gl.glBlendFunc( blendFunc_.src, blendFunc_.dst );
            }

            textureAtlas_.drawQuads(gl);

            if( newBlend )
                gl.glBlendFunc(ccMacros.CC_BLEND_SRC, ccMacros.CC_BLEND_DST);

            gl.glDisable(GL10.GL_TEXTURE_2D);

            if (textureAtlas_.withColorArray())
                gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
    }

    private void resizeAtlas() {
        // if we're going beyond the current TextureAtlas's capacity,
        // all the previously initialized sprites will need to redo their texture coords
        // this is likely computationally expensive
        int quantity = (textureAtlas_.getTotalQuads() + 1) * 4 / 3;

//        Log.w(LOG_TAG, "Resizing TextureAtlas capacity, from [%d] to [%d].", textureAtlas_.getTotalQuads(), quantity);


        textureAtlas_.resizeCapacity(quantity);

        for (int i = 0; i < children.size(); i++) {
            AtlasSprite sprite = (AtlasSprite) children.get(i);
            sprite.updateAtlas();
        }
    }

    private void updateBlendFunc()
    {
        if( ! textureAtlas_.getTexture().hasPremultipliedAlpha() ) {
            blendFunc_.src = GL10.GL_SRC_ALPHA;
            blendFunc_.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
        }
    }

}