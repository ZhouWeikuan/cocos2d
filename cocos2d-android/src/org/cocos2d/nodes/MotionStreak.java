package org.cocos2d.nodes;

import org.cocos2d.opengl.Texture2D;
import org.cocos2d.types.CCBlendFunc;
import org.cocos2d.types.CCColor4B;
import org.cocos2d.types.CCPoint;

/**
 * Motion Streak manages a Ribbon based on it's motion in absolute space.
 * You construct it with a fadeTime, minimum segment size, texture path, texture
 * length and color. The fadeTime controls how long it takes each vertex in
 * the streak to fade out, the minimum segment size it how many pixels the
 * streak will move before adding a new ribbon segement, and the texture
 * length is the how many pixels the texture is stretched across. The texture
 * is vertically aligned along the streak segemnts.
 * <p/>
 * Limitations:
 * MotionStreak, by default, will use the GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA blending function.
 * This blending function might not be the correct one for certain textures.
 * But you can change it by using:
 * [obj setBlendFunc: (ccBlendfunc) {new_src_blend_func, new_dst_blend_func}];
 *
 * @since v0.8.1
 */

public class MotionStreak extends CocosNode /*implements CocosNodeTexture*/ {
    Ribbon ribbon_;
    float mSegThreshold;
    float mWidth;
    CCPoint mLastLocation;

    /**
     * Ribbon used by MotionStreak (weak reference)
     */
    public Ribbon getRibbon() {
        return ribbon_;
    }

    /**
     * creates the a MotionStreak. The image will be loaded using the TextureMgr.
     */
    public MotionStreak(float fade, float seg, String path, float width, float length, CCColor4B color) {
        mSegThreshold = seg;
        mWidth = width;
        mLastLocation = CCPoint.make(0, 0);
        ribbon_ = new Ribbon(mWidth, path, length, color, fade);
        addChild(ribbon_);

        // update ribbon position
        schedule("update", 0);
    }

    /**
     * polling function
     */
    public void update(float delta) {
        CCPoint location = convertToWorldSpace(0, 0);
        ribbon_.setPosition(-1 * location.x, -1 * location.y);
        float len = (float)Math.sqrt((float) Math.pow(mLastLocation.x - location.x, 2) + (float) Math.pow(mLastLocation.y - location.y, 2));
        if (len > mSegThreshold) {
            ribbon_.addPoint(location, mWidth);
            mLastLocation = location;
        }
        ribbon_.update(delta);
    }

    // CocosNodeTexture protocol

    public void setTexture(Texture2D texture) {
        ribbon_.setTexture(texture);
    }

    public Texture2D texture() {
        return ribbon_.texture();
    }

    public CCBlendFunc blendFunc() {
        return ribbon_.blendFunc();
    }

    public void setBlendFunc(CCBlendFunc blendFunc) {
        ribbon_.setBlendFunc(blendFunc);
    }

}
