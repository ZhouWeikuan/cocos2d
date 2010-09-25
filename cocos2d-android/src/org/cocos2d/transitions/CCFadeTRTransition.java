package org.cocos2d.transitions;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

/**
 * FadeTRTransition.
 * Fade the tiles of the outgoing scene from the left-bottom corner to the top-right corner.
 */
public class CCFadeTRTransition extends CCTransitionScene {

    public static CCFadeTRTransition transition(float t, CCScene s) {
        return new CCFadeTRTransition(t, s);
    }

    public CCFadeTRTransition(float t, CCScene s) {
        super(t, s);
    }

    // override addScenes, and change the order
    public void sceneOrder()
    {
        inSceneOnTop = false;
    }

    public void onEnter() {
        super.onEnter();

        CGSize s = CCDirector.sharedDirector().winSize();
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
