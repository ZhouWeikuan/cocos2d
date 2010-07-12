package org.cocos2d.transitions;

import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.*;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

/**
 * RotoZoom Transition.
 * Rotate and zoom out the outgoing scene, and then rotate and zoom in the incoming
 */
public class RotoZoomTransition extends TransitionScene {

    public static RotoZoomTransition transition(float t, Scene s) {
        return new RotoZoomTransition(t, s);
    }

    public RotoZoomTransition(float t, Scene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        // CCSize s = Director.sharedDirector().winSize();

        inScene.setScale(0.001f);
        outScene.setScale(1.0f);

        inScene.setAnchorPoint(CGPoint.make(0.5f, 0.5f));
        outScene.setAnchorPoint(CGPoint.make(0.5f, 0.5f));

        IntervalAction rotozoom = Sequence.actions(Spawn.actions(ScaleBy.action(duration / 2, 0.001f),
                RotateBy.action(duration / 2, 360 * 2)),
                DelayTime.action(duration / 2));


        outScene.runAction(rotozoom);
        inScene.runAction(Sequence.actions(rotozoom.reverse(),
                CallFunc.action(this, "finish")));
    }
}
