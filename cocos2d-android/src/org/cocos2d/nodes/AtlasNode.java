package org.cocos2d.nodes;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.TextureAtlas;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;

import javax.microedition.khronos.opengles.GL10;

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
public abstract class AtlasNode extends CCNode implements CCNode.CocosNodeRGBA, CCNode.CocosNodeTexture {

    /// texture atlas
    protected TextureAtlas textureAtlas_;
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
    boolean opacityModifyRGB_;

    protected AtlasNode(String tile, int w, int h, int c) {

        itemWidth = w;
        itemHeight = h;

        opacity_ = (byte) 255;
        color_ = new ccColor3B((byte) 255, (byte) 255, (byte) 255);
        opacityModifyRGB_ = false;

        blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
        textureAtlas_ = new TextureAtlas(tile, c);

        calculateMaxItems();
        calculateTexCoordsSteps();
    }

    private void calculateMaxItems() {
        itemsPerColumn = (int) (textureAtlas_.getTexture().getHeight() / itemHeight);
        itemsPerRow = (int) (textureAtlas_.getTexture().getWidth() / itemWidth);
    }

    private void calculateTexCoordsSteps() {
        texStepX = itemWidth / (float) textureAtlas_.getTexture().pixelsWide();
        texStepY = itemHeight / (float) textureAtlas_.getTexture().pixelsHigh();
    }

    public abstract void updateAtlasValues();

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glEnable(GL10.GL_TEXTURE_2D);


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
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glDisable(GL10.GL_TEXTURE_2D);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void setOpacity(int opacity) {
        opacity_ = opacity;
    }

    public int getOpacity() {
        return opacity_;
    }


    public void setColor(ccColor3B color) {
        color_.r = color.r;
        color_.g = color.g;
        color_.b = color.b;
    }

    public ccColor3B getColor() {
        return new ccColor3B(color_.r, color_.g, color_.b);
    }


    // CocosNodeTexture protocol

    public void updateBlendFunc() {
    }

    public void setTexture(CCTexture2D texture) {
        textureAtlas_.setTexture(texture);
        updateBlendFunc();
//        updateOpacityModifyRGB();
    }

    public CCTexture2D getTexture() {
        return textureAtlas_.getTexture();
    }
}
