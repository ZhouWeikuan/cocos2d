package org.cocos2d.actions.interval;

import java.util.ArrayList;

import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;

/** Animates a sprite given the name of an Animation */
public class CCAnimate extends CCIntervalAction {

    private CCAnimation animation;
    private CCSpriteFrame origFrame;
    private boolean restoreOriginalFrame;

    /** animation used for the animage */
    public CCAnimation getAnimation() {
    	return animation;
    }
    
    public void setAnimation(CCAnimation anim) {
    	animation = anim;
    }

    /** creates an action with a duration, animation and depending of the restoreOriginalFrame, it will restore the original frame or not.
     The 'delay' parameter of the animation will be overrided by the duration parameter.
     @since v0.99.0
     */
    public static CCAnimate action(float duration, CCAnimation anim, boolean restore) {
    	assert anim!=null:"Animate: argument anim must be non-null";
    	return new CCAnimate(duration, anim, restore);
    }

    /** creates the action with an Animation and will restore the original frame when the animation is over */
    public static CCAnimate action(CCAnimation anim) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new CCAnimate(anim, true);
    }

    /** creates the action with an Animation */
    public static CCAnimate action(CCAnimation anim, boolean restore) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new CCAnimate(anim, restore);
    }
    
    /** initializes the action with an Animation 
     * 	and indicate whether restore the original frame
     * 	 	when the animtion is over */
    protected CCAnimate(CCAnimation anim, boolean restore) {
        super(anim.frames().size() * anim.delay());

        restoreOriginalFrame = restore;
        animation = anim;
        origFrame = null;
    }

    /** initializes an action with a duration, animation and depending of the restoreOriginalFrame, it will restore the original frame or not.
     The 'delay' parameter of the animation will be overrided by the duration parameter.
     @since v0.99.0
     */
    protected CCAnimate(float duration, CCAnimation anim, boolean restore) {
    	super(duration);
    	assert anim != null : "Animate: argument Animation must be non-null";
    	restoreOriginalFrame = restore;
    	animation = anim;
    	origFrame = null;
    }

    @Override
    public CCAnimate copy() {
        return new CCAnimate(duration, animation, restoreOriginalFrame);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        CCSprite sprite = (CCSprite) target;

        origFrame = null;
        if (restoreOriginalFrame)
        	origFrame = sprite.displayedFrame();
    }

    @Override
    public void stop() {
        if (restoreOriginalFrame) {
            CCSprite sprite = (CCSprite) target;
            sprite.setDisplayFrame(origFrame);
        }

        super.stop();
    }

    @Override
    public void update(float t) {
    	int numberOfFrames = animation.frames().size();
        int idx = (int) (t * numberOfFrames);

        if (idx >= numberOfFrames) {
            idx = numberOfFrames - 1;
        }

        CCSprite sprite = (CCSprite) target;
        CCSpriteFrame frame = animation.frames().get(idx); 
        if (!sprite.isFrameDisplayed(frame)) {
            sprite.setDisplayFrame(frame);
        }
    }
    
    @Override
    public CCAnimate reverse() {
    	ArrayList<CCSpriteFrame> ao = new ArrayList<CCSpriteFrame>();
    	for (CCSpriteFrame o: animation.frames()) {
    		ao.add(0, o);
    	}
    	
    	CCAnimation newAnim = CCAnimation.animation(animation.name(), animation.delay(), ao);
    	return CCAnimate.action(duration, newAnim, restoreOriginalFrame);
    }

}
