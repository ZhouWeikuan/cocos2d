package org.cocos2d.nodes;

import org.cocos2d.opengl.Texture2D;
import org.cocos2d.types.CCBlendFunc;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCMacros;
import org.cocos2d.types.CCPoint;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;


public class TextureNode extends CocosNode implements CocosNode.CocosNodeRGBA, CocosNode.CocosNodeSize {

    /**
     * The texture that is rendered
     */
    private Texture2D texture_;

    // blend func
    private CCBlendFunc blendFunc_;

    // texture RGBA
    private int opacity_;
    private CCColor3B color_;

    boolean opacityModifyRGB_;


    public Texture2D getTexture() {
        return texture_;
    }

    public void setTexture(Texture2D texture) {
        texture_ = texture;
        setContentSize(texture.getWidth(), texture.getHeight());
//        if( ! texture.hasPremultipliedAlpha() ) {
//            blendFunc_.src = GL_SRC_ALPHA;
//            blendFunc_.dst = GL_ONE_MINUS_SRC_ALPHA;
//        }
//        opacityModifyRGB_ = texture.hasPremultipliedAlpha();

    }

    /**
     * conforms to CocosNodeOpacity and CocosNodeRGB protocol
     */
    public int getOpacity() {
        return opacity_;
    }

    public void setOpacity(int opacity) {
        opacity_ = opacity;
    }

    public void setColor(CCColor3B color) {
        color_.r = color.r;
        color_.g = color.g;
        color_.b = color.b;
    }

    public CCColor3B getColor() {
        return new CCColor3B(color_.r, color_.g, color_.b);
    }

    public TextureNode() {
        opacity_ = 255;
        color_ = new CCColor3B(255, 255, 255);
        setAnchorPoint(0.5f, 0.5f);
        blendFunc_ = new CCBlendFunc(CCMacros.CC_BLEND_SRC, CCMacros.CC_BLEND_DST);

    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glEnable(GL_TEXTURE_2D);

        gl.glColor4f(color_.r / 255f, color_.g / 255f, color_.b / 255f, opacity_ / 255f);

        boolean newBlend = false;
        if (blendFunc_.src != CCMacros.CC_BLEND_SRC || blendFunc_.dst != CCMacros.CC_BLEND_DST) {
            newBlend = true;
            gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
        }

        if (texture_ != null)
            texture_.drawAtPoint(gl, CCPoint.zero());

        if (newBlend)
            gl.glBlendFunc(CCMacros.CC_BLEND_SRC, CCMacros.CC_BLEND_DST);

        // is this chepear than saving/restoring color state ?
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glDisable(GL_TEXTURE_2D);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    }

    @Override
    public float getWidth() {
        return texture_.getWidth();
    }

    @Override
    public float getHeight() {
        return texture_.getHeight();
    }
}