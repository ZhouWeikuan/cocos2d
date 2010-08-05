package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.Sprite;

/** Animates a sprite given the name of an Animation */
public class CCAnimate extends CCIntervalAction {

    private CCNode.CCAnimation animation;
    private Object origFrame;
    private boolean restoreOriginalFrame;

    /** animation used for the animage */
    public CCNode.CCAnimation getAnimation() {
    	return animation;
    }
    
    public void setAnimation(CCNode.CCAnimation anim) {
    	animation = anim;
    }

    /** creates an action with a duration, animation and depending of the restoreOriginalFrame, it will restore the original frame or not.
     The 'delay' parameter of the animation will be overrided by the duration parameter.
     @since v0.99.0
     */
    public static CCAnimate action(float duration, CCNode.CCAnimation anim, boolean restore) {
    	assert anim!=null:"Animate: argument anim must be non-null";
    	return new CCAnimate(duration, anim, restore);
    }

    /** creates the action with an Animation and will restore the original frame when the animation is over */
    public static CCAnimate action(CCNode.CCAnimation anim) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new CCAnimate(anim, true);
    }

    /** creates the action with an Animation */
    public static CCAnimate action(CCNode.CCAnimation anim, boolean restore) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new CCAnimate(anim, restore);
    }
    
    /** initializes the action with an Animation 
     * 	and indicate whether restore the original frame
     * 	 	when the animtion is over */
    protected CCAnimate(CCNode.CCAnimation anim, boolean restore) {
        super(anim.frames().size() * anim.delay());

        restoreOriginalFrame = restore;
        animation = anim;
        origFrame = null;
    }

    /** initializes an action with a duration, animation and depending of the restoreOriginalFrame, it will restore the original frame or not.
     The 'delay' parameter of the animation will be overrided by the duration parameter.
     @since v0.99.0
     */
    protected CCAnimate(float duration, CCNode.CCAnimation anim, boolean restore) {
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
        Sprite sprite = (Sprite) target;

        origFrame = null;
        if (restoreOriginalFrame)
        	origFrame = sprite.displayFrame();
    }

    @Override
    public void stop() {
        if (restoreOriginalFrame) {
            Sprite sprite = (Sprite) target;
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

        Sprite sprite = (Sprite) target;
        if (!sprite.isFrameDisplayed(animation.frames().get(idx))) {
            sprite.setDisplayFrame(animation.frames().get(idx));
        }
    }
    
    /*
    @Override
    public CCAnimate reverse() {
    	ArrayList<Object> ao = new ArrayList<Object>();
    	for (Object o: animation.frames()) {
    		ao.add(0, o);
    	}
    	
    	CCAnimation *newAnim = [CCAnimation animationWithName:animation_.name delay:animation_.delay frames:newArray];
    	return [[self class] actionWithDuration:duration animation:newAnim restoreOriginalFrame:restoreOriginalFrame];
    }*/

}
