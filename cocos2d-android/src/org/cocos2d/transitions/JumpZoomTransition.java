package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.*;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;

/**
 * JumpZoom Transition.
 * Zoom out and jump the outgoing scene, and then jump and zoom in the incoming
 */
public class JumpZoomTransition extends TransitionScene {

    public static JumpZoomTransition transition(float t, Scene s) {
        return new JumpZoomTransition(t, s);
    }

    public JumpZoomTransition(float t, Scene s) {
        super(t, s);
    }

    public void onEnter() {
        super.onEnter();
        CCSize size = Director.sharedDirector().winSize();
        
        float width = size.getWidth();
        float height = size.getHeight();

        inScene.scale(0.5f);
        inScene.setPosition(width, 0);

        inScene.setAnchorPoint(0.5f, 0.5f);
        outScene.setAnchorPoint(0.5f, 0.5f);

        IntervalAction jump = JumpBy.action(duration / 4, -width, 0, width / 4, 2);
        IntervalAction scaleIn = ScaleTo.action(duration / 4, 1.0f);
        IntervalAction scaleOut = ScaleTo.action(duration / 4, 0.5f);

        IntervalAction jumpZoomOut = Sequence.actions(scaleOut, jump);
        IntervalAction jumpZoomIn = Sequence.actions(jump, scaleIn);

        IntervalAction delay = DelayTime.action(duration / 2);

        outScene.runAction(jumpZoomOut);
        inScene.runAction(Sequence.actions(delay, jumpZoomIn,
                CallFunc.action(this, "finish")));
    }
}
