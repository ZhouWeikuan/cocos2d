package org.cocos2d.nodes;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4B;

/**
 * CCMotionStreak manages a Ribbon based on it's motion in absolute space.
 * You construct it with a fadeTime, minimum segment size, texture path, texture
 * length and color. The fadeTime controls how long it takes each vertex in
 * the streak to fade out, the minimum segment size it how many pixels the
 * streak will move before adding a new ribbon segement, and the texture
 * length is the how many pixels the texture is stretched across. The texture
 * is vertically aligned along the streak segemnts. 
 *
 * Limitations:
 *   CCMotionStreak, by default, will use the GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA blending function.
 *   This blending function might not be the correct one for certain textures.
 *   But you can change it by using:
 *     [obj setBlendFunc: (ccBlendfunc) {new_src_blend_func, new_dst_blend_func}];
 *
 * @since v0.8.1
 */
public class CCMotionStreak extends CCNode implements UpdateCallback/*implements CocosNodeTexture*/ {
    CCRibbon ribbon_;
    float segThreshold_;
    float width_;
    CGPoint lastLocation_;

    /**
     * Ribbon used by MotionStreak (weak reference)
     */
    public CCRibbon getRibbon() {
        return ribbon_;
    }

    /**
     * creates the a MotionStreak. The image will be loaded using the TextureMgr.
     */
    public CCMotionStreak streak(float fade, float seg,
    	String path, float width, float length, ccColor4B color) {
    	return new CCMotionStreak(fade, seg, path, width, length, color);
    }    

    /** initializes a MotionStreak. The file will be loaded using the TextureMgr. */
    public CCMotionStreak(float fade, float seg, String path, float width, float length, ccColor4B color) {
        segThreshold_ = seg;
        width_ = width;
        lastLocation_ = CGPoint.make(0, 0);
        ribbon_ = new CCRibbon(width_, path, length, color, fade);
        addChild(ribbon_);

        // update ribbon position
//        schedule("update", 0);
        scheduleUpdate();
    }

    /**
     * polling function
     */
    public void update(float delta) {
        CGPoint location = convertToWorldSpace(0, 0);
        ribbon_.setPosition(CGPoint.make(-1 * location.x, -1 * location.y));
        float len = (float)Math.sqrt((float) Math.pow(lastLocation_.x - location.x, 2) + (float) Math.pow(lastLocation_.y - location.y, 2));
        if (len > segThreshold_) {
            ribbon_.addPoint(location, width_);
            lastLocation_ = location;
        }
        ribbon_.update(delta);
    }

    // CocosNodeTexture protocol
    public void setTexture(CCTexture2D texture) {
        ribbon_.setTexture(texture);
    }

    public CCTexture2D texture() {
        return ribbon_.texture();
    }

    public ccBlendFunc blendFunc() {
        return ribbon_.blendFunc();
    }

    public void setBlendFunc(ccBlendFunc blendFunc) {
        ribbon_.setBlendFunc(blendFunc);
    }

}
