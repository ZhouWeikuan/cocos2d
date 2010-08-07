package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

public class PageTurnTransition extends TransitionScene {
    private boolean back_;

    /** creates a base transition with duration and incoming scene */
    public static PageTurnTransition transition(float t, CCScene s, boolean back) {
        return new PageTurnTransition(t, s, back);
    }

    /** initializes a transition with duration and incoming scene */
    public PageTurnTransition(float t, CCScene s, boolean back) {
        super(t, s);
        back_ = back;
    }


    public void sceneOrder()
    {
        inSceneOnTop = back_;
    }

//
    public void onEnter()
    {
        super.onEnter();

        CGSize s = CCDirector.sharedDirector().winSize();
        int x,y;
        if( s.width > s.height)
        {
            x=16;y=12;
        }
        else
        {
            x=12;y=16;
        }

        CCIntervalAction action  = action(x,y);

        if(! back_ )
        {
//            outScene.runAction(Sequence.actions(
//                                 action,
//                                 CallFunc.action(this, "finish")),
//                                 StopGrid.action())
        }
        else
        {
            // to prevent initial flicker
            inScene.setVisible(false);
//            inScene.runAction(Sequence.actions(
//                                 Show.action()),
//                                 action,
//                                 CallFunc.action(this, "finish")),
//                                 StopGrid.action())
        }

    }

    public CCIntervalAction action(float x, float y)
    {
        if( back_ )
        {
            // Get hold of the PageTurn3DAction
//            return ReverseTime.action(PageTurn3D.action(x, y, duration));
        }
        else
        {
            // Get hold of the PageTurn3DAction
//            return PageTurn3D.action(x, y, duration);
        }
        return null;
    }

}
