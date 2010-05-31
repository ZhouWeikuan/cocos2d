package org.cocos2d.nodes;

import android.util.Log;
import org.cocos2d.opengl.TextureAtlas;
import org.cocos2d.types.*;
import org.cocos2d.utils.CCFormatter;

import java.util.HashMap;

/** AtlasSprite is a CocosNode object that implements the CocosNodeFrames and CocosNodeRGBA protocols.
 *
 * AtlasSprite can be used as a replacement of Sprite.
 *
 * AtlasSprite has all the features from CocosNode with the following additions and limitations:
 *	- New features
 *		- It is MUCH faster than Sprite
 *		- supports flipX, flipY
 *
 *	- Limitations
 *		- Their parent can only be an AtlasSpriteManager
 *		- They can't have children
 *		- Camera is not supported yet (eg: OrbitCamera action doesn't work)
 *		- GridBase actions are not supported (eg: Lens, Ripple, Twirl)
 *		- The Alias/Antialias property belongs to AtlasSpriteManager, so you can't individually set the aliased property.
 *		- The Blending function property belongs to AtlasSpriteManager, so you can't individually set the blending function property.
 *		- Parallax scroller is not supported, but can be simulated with a "proxy" sprite.
 *
 */

public class AtlasSprite extends CocosNode implements CocosNode.CocosNodeSize, CocosNode.CocosNodeFrames, CocosNode.CocosNodeRGBA {
    public static final int kIndexNotInitialized = -1;

    TextureAtlas textureAtlas_;
    int atlasIndex_;

    /**
     * returns the altas index of the AtlasSprite
     */
    public int atlasIndex() {
        return atlasIndex_;
    }

    public void setIndex(int index) {
        atlasIndex_ = index;
    }

    // texture pixels
    private CCRect rect_;

    // texture coords
    // stored as floats in the range [0..1]
    private CCQuad2 texCoords_ = new CCQuad2();

    // vertex coordinates
    // stored as pixel locations
    private CCQuad3 vertexCoords_ = new CCQuad3();

    // opacity and RGB protocol
    private int opacity_;
    private CCColor3B color_;
    
    private boolean dirty_;

    // Animations that belong to the sprite
    HashMap<String, AtlasAnimation> animations;

    // image is flipped
    private boolean flipX_;
    private	boolean flipY_;

    /**
     * whether or not the Sprite's color needs to be updated in the Atlas
     */
    public boolean isDirty() {
        return dirty_;
    }

    /**
     * returns the rect of the AtlasSprite
     */
    public CCRect getTextureRect() {
        return rect_;
    }


    public static AtlasSprite sprite(CCRect rect, AtlasSpriteManager manager) {
        return new AtlasSprite(rect, manager);
    }

    protected AtlasSprite(CCRect rect, AtlasSpriteManager manager) {
        textureAtlas_ = manager.atlas();

        atlasIndex_ = kIndexNotInitialized;

        // default transform anchor: center
        setAnchorPoint(0.5f, 0.5f);

        // RGB and opacity
        opacity_ = (byte) 255;
        color_ = new CCColor3B(255, 255, 255);

        animations = null;        // lazy alloc

        setTextureRect(rect);
    }

    @Override
    public String toString() {
        return new CCFormatter().format("<%s = %08X | Rect = (%.2f,%.2f,%.2f,%.2f) | tag = %i>", AtlasSprite.class.getSimpleName(),
                this, rect_.origin.x, rect_.origin.y, rect_.size.width, rect_.size.height, getTag());
    }

    private void initAnimationDictionary() {
        animations = new HashMap<String, AtlasAnimation>(2);
    }

    public void setTextureRect(CCRect rect) {
        rect_ = rect;

        updateTextureCoords();

        // Don't update Atlas if index == -1. issue #283
        if (atlasIndex_ == kIndexNotInitialized)
            dirty_ = true;
        else
            updateAtlas();

        // add these lines
        if (!((rect.size.width == getWidth()) && ((rect.size.getHeight()) == getHeight()))) {
            setContentSize(rect.size.width, rect.size.height);
            dirty_ = true;
        }
    }

    private void updateTextureCoords() {
        float atlasWidth = textureAtlas_.getTexture().pixelsWide();
        float atlasHeight = textureAtlas_.getTexture().pixelsHigh();

        float left = rect_.origin.x / atlasWidth;
        float right = (rect_.origin.x + rect_.size.width) / atlasWidth;
        float top = rect_.origin.y / atlasHeight;
        float bottom = (rect_.origin.y + rect_.size.height) / atlasHeight;

        if( flipX_)
            CCMacros.CC_SWAP(left,right);
        if( flipY_)
            CCMacros.CC_SWAP(top,bottom);

        CCQuad2 newCoords = new CCQuad2(
                left, bottom,
                right, bottom,
                left, top,
                right, top);

        texCoords_ = newCoords;
    }

    public void updateColor() {
        CCColor4B colorQuad = new CCColor4B(color_.r, color_.g, color_.b, opacity_);
        textureAtlas_.updateColor(colorQuad, atlasIndex_);
        dirty_ = false;
    }

    // algorithm from pyglet ( http://www.pyglet.org )
    public void updatePosition() {

        // if not visible_ then everything is 0
        if (!visible_) {
            CCQuad3 newVertices = new CCQuad3(
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0);

            vertexCoords_ = newVertices;
        }

        // rotation ? -> update: rotation, scale, position
        else if (getRotation() != 0) {
            float x1 = -getTransformAnchorX() * getScaleX();
            float y1 = -getTransformAnchorY() * getScaleY();

            float x2 = x1 + rect_.size.width * getScaleX();
            float y2 = y1 + rect_.size.height * getScaleY();
            float x = getPositionX();
            float y = getPositionY();

            float r = -CCMacros.CC_DEGREES_TO_RADIANS(getRotation());
            float cr = (float)Math.cos(r);
            float sr = (float)Math.sin(r);
            float ax = x1 * cr - y1 * sr + x;
            float ay = x1 * sr + y1 * cr + y;
            float bx = x2 * cr - y1 * sr + x;
            float by = x2 * sr + y1 * cr + y;
            float cx = x2 * cr - y2 * sr + x;
            float cy = x2 * sr + y2 * cr + y;
            float dx = x1 * cr - y2 * sr + x;
            float dy = x1 * sr + y2 * cr + y;

            CCQuad3 newVertices = new CCQuad3(
                    (int) ax, (int) ay, 0,
                    (int) bx, (int) by, 0,
                    (int) dx, (int) dy, 0,
                    (int) cx, (int) cy, 0);

            vertexCoords_ = newVertices;
        }

        // scale ? -> update: scale, position
        else if (getScaleX() != 1 || getScaleY() != 1) {
            float x = getPositionX();
            float y = getPositionY();

            float x1 = (x - getTransformAnchorX() * getScaleX());
            float y1 = (y - getTransformAnchorY() * getScaleY());
            float x2 = (x1 + rect_.size.width * getScaleX());
            float y2 = (y1 + rect_.size.height * getScaleY());

            CCQuad3 newVertices = new CCQuad3(
                    (int) x1, (int) y1, 0,
                    (int) x2, (int) y1, 0,
                    (int) x1, (int) y2, 0,
                    (int) x2, (int) y2, 0);

            vertexCoords_ = newVertices;
        }

        // update position
        else {
            float x = getPositionX();
            float y = getPositionY();

            float x1 = (x - getTransformAnchorX());
            float y1 = (y - getTransformAnchorY());
            float x2 = (x1 + rect_.size.width);
            float y2 = (y1 + rect_.size.height);

            CCQuad3 newVertices = new CCQuad3(
                    (int) x1, (int) y1, 0,
                    (int) x2, (int) y1, 0,
                    (int) x1, (int) y2, 0,
                    (int) x2, (int) y2, 0);

            vertexCoords_ = newVertices;
        }

        textureAtlas_.updateQuad(texCoords_, vertexCoords_, atlasIndex_);
        dirty_ = false;
    }

    public void updateAtlas() {
        textureAtlas_.updateQuad(texCoords_, vertexCoords_, atlasIndex_);
    }

    public void insertInAtlas(int index) {
        atlasIndex_ = index;
        textureAtlas_.insertQuad(texCoords_, vertexCoords_, atlasIndex_);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        dirty_ = true;
    }

    @Override
    public void setRotation(float rot) {
        super.setRotation(rot);
        dirty_ = true;
    }

    @Override
    public void setScaleX(float sx) {
        super.setScaleX(sx);
        dirty_ = true;
    }

    @Override
    public void setScaleY(float sy) {
        super.setScaleY(sy);
        dirty_ = true;
    }

    @Override
    public void setScale(float s) {
        super.scale(s);
        dirty_ = true;
    }

    @Override
    public void setVertexZ(float z) {
        super.setVertexZ(z);
        dirty_ = true;
    }

    @Override
    public void setAnchorPoint(float x, float y) {
        super.setAnchorPoint(x, y);
        dirty_ = true;
    }

    @Override
    public void setRelativeAnchorPoint(boolean relative) {
        Log.w("AtlasSprite", "relativeTransformAnchor_ is ignored in AtlasSprite");
    }

    @Override
    public void setVisible(boolean v) {
        super.setVisible(v);
        dirty_ = true;
    }

    public void setFlipX(boolean b)
    {
        if( flipX_ != b ) {
            flipX_ = b;
            setTextureRect(rect_);
        }
    }

    public boolean getFlipX()
    {
        return flipX_;
    }

    public void setFlipY(boolean b)
    {
        if( flipY_ != b ) {
            flipY_ = b;
            setTextureRect(rect_);
        }
    }

    public boolean getFlipY()
    {
        return flipY_;
    }
    
    @Override
    public CocosNode addChild(CocosNode child, int z, int aTag) {
        assert false : "AtlasSprite can't have children";
        return null;
    }

    public void setOpacity(int opacity) {
        opacity_ = opacity;
        dirty_ = true;
    }

    public int getOpacity() {
        return opacity_;
    }

    public void setColor(CCColor3B color) {
        color_.r = color.r;
        color_.g = color.g;
        color_.b = color.b;
        dirty_ = true;
    }

    public CCColor3B getColor() {
        return new CCColor3B(color_.r, color_.g, color_.b);
    }

    // TODO Remove cast
    public void setDisplayFrame(Object newFrame) {
        if (newFrame instanceof AtlasSpriteFrame) {
            AtlasSpriteFrame frame = (AtlasSpriteFrame) newFrame;
            CCRect rect = frame.rect;

            setTextureRect(rect);
        }
    }

    // TODO Remove cast
    public void setDisplayFrame(String animationName, int frameIndex) {
        if (animations == null)
            initAnimationDictionary();

        AtlasAnimation a = animations.get(animationName);
        AtlasSpriteFrame frame = (AtlasSpriteFrame) a.frames.get(frameIndex);

        assert frame != null : "AtlasSprite#setDisplayFrame. Invalid frame";
        CCRect rect = frame.rect;

        setTextureRect(rect);

    }

    // TODO Remove cast
    public boolean isFrameDisplayed(Object frame) {
        if (frame instanceof AtlasSpriteFrame) {
            AtlasSpriteFrame spr = (AtlasSpriteFrame) frame;
            CCRect r = spr.rect;
            return CCRect.equalToRect(r, rect_);
        }
        return false;
    }

    public AtlasSpriteFrame displayFrame() {
        return new AtlasSpriteFrame(rect_);
    }

    public void addAnimation(CocosAnimation anim) {
        // lazy alloc
        if (animations == null)
            initAnimationDictionary();

        animations.put(anim.name(), (AtlasAnimation) anim);
    }

    public CocosAnimation animationByName(String animationName) {
        assert animationName != null : "animationName parameter must be non null";
        return animations.get(animationName);
    }
}
