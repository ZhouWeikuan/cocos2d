package org.cocos2d.transitions;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.types.CCSize;

/**
 * FadeTRTransition.
 * Fade the tiles of the outgoing scene from the left-bottom corner to the top-right corner.
 */
public class FadeTRTransition extends TransitionScene {

    public static FadeTRTransition transition(float t, Scene s) {
        return new FadeTRTransition(t, s);
    }

    public FadeTRTransition(float t, Scene s) {
        super(t, s);
    }

    // override addScenes, and change the order
    public void sceneOrder()
    {
        inSceneOnTop = false;
    }

    public void onEnter() {
        super.onEnter();

        CCSize s = Director.sharedDirector().winSize();
        float aspect = s.width / s.height;
        // int x = (int) (12 * aspect);
        // int y = 12;

//			Action action  = action(CCGridSize.ccg(x,y));
//
//			outScene.runAction(IntervalAction.Sequence.actions(
//							action,
//						    InstantAction.new CallFunc(this, new CCSelector(this, "finish")),
//						    StopGrid.action()));
    }

//	   	protected IntervalAction action(CCGridSize v)
//		{
//			return FadeOutTRTiles.action(v, duration);
//		}
}
