package org.cocos2d.transitions;

import org.cocos2d.actions.ease.CCEaseInOut;
import org.cocos2d.actions.grid.CCStopGrid;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.tile.CCSplitCols;
import org.cocos2d.layers.CCScene;

/**
 * SplitCols Transition.
 * The odd columns goes upwards while the even columns goes downwards.
 */
public class CCSplitColsTransition extends CCTransitionScene implements CCTransitionEaseScene {

	public static CCSplitColsTransition transition(float t, CCScene s) {
		return new CCSplitColsTransition(t, s);
	}

    public CCSplitColsTransition(float t, CCScene s) {
        super(t, s);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        inScene.setVisible(false);

        CCIntervalAction split = action();
        CCIntervalAction seq = CCSequence.actions(
                    split,
                    CCCallFunc.action(this, "hideOutShowIn"),
                    split.reverse());
        runAction(CCSequence.actions(
                   easeAction(seq),
                   CCCallFunc.action(this, "finish"),
                   CCStopGrid.action()));
    }


    @Override
    public CCIntervalAction easeAction(CCIntervalAction action) {
	return CCEaseInOut.action(action, 3.0f);
    }

    protected CCIntervalAction action() {
        return CCSplitCols.action(3, duration/2.0f);
    }
}
