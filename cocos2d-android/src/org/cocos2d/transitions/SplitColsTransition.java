package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;

/**
 * SplitCols Transition.
 * The odd columns goes upwards while the even columns goes downwards.
 */
public class SplitColsTransition extends TransitionScene {

    public SplitColsTransition(float t, CCScene s) {
        super(t, s);
    }

//    public void onEnter()
//    {
//        super.onEnter();
//
//        inScene.setVisible(false);
//
//        Action split = action();
//        Action seq = Sequence.actions(
//                    split,
//                    new CallFunc(this, new CCSelector(this, "hideOutShowIn")),
//                    split.reverse());
//        runAction(Sequence.actions(
//                   EaseInOut.action(seq, 3.0f),
//                   new CallFunc(this, new CCSelector(this, "finish")),
//                   StopGrid.action());
//    }
//
//    protected IntervalAction action(CCGridSize v)
//    {
//        return new SplitCols(3, duration/2.0f);
//    }
}
