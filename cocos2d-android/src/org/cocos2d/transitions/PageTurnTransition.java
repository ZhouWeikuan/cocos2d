package org.cocos2d.transitions;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;

public class PageTurnTransition extends TransitionScene {
    private boolean back_;

    /** creates a base transition with duration and incoming scene */
    public static PageTurnTransition transition(float t, Scene s, boolean back) {
        return new PageTurnTransition(t, s, back);
    }

    /** initializes a transition with duration and incoming scene */
    public PageTurnTransition(float t, Scene s, boolean back) {
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

        CCSize s = Director.sharedDirector().winSize();
        int x,y;
        if( s.width > s.height)
        {
            x=16;y=12;
        }
        else
        {
            x=12;y=16;
        }

        IntervalAction action  = action(x,y);

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

    public IntervalAction action(float x, float y)
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
