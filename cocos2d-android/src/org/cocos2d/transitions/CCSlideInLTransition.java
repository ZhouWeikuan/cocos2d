package org.cocos2d.transitions;

import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * SlideInL Transition.
 * Slide in the incoming scene from the left border.
 */
public class CCSlideInLTransition extends CCTransitionScene implements CCTransitionEaseScene {
	// The adjust factor is needed to prevent issue #442
	// One solution is to use DONT_RENDER_IN_SUBPIXELS images, but NO
	// The other issue is that in some transitions (and I don't know why)
	// the order should be reversed (In in top of Out or vice-versa).
    protected static final float ADJUST_FACTOR = 0.5f;

    public static CCSlideInLTransition transition(float t, CCScene s) {
        return new CCSlideInLTransition(t, s);
    }

    public CCSlideInLTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        initScenes();

        CCIntervalAction in = action();
        CCIntervalAction out = action();

        inScene.runAction(easeAction(in));
        outScene.runAction(CCSequence.actions(
                easeAction(out),
                CCCallFunc.action(this, "finish")));
    }

    public void sceneOrder() {
        inSceneOnTop = false;
    }

    /**
     * initializes the scenes
     */
    protected void initScenes() {
        CGSize s = CCDirector.sharedDirector().winSize();
        inScene.setPosition(-(s.width-ADJUST_FACTOR), 0);
    }

	/**
     * returns the action that will be performed
     */
    protected CCIntervalAction action() {
        CGSize s = CCDirector.sharedDirector().winSize();
        return CCMoveBy.action(duration, CGPoint.make(s.width-ADJUST_FACTOR,0));
    }


    @Override
    public CCIntervalAction easeAction(CCIntervalAction action) {
        return CCEaseOut.action(action, 2.0f);
    }

}
