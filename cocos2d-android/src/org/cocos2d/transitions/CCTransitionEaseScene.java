package org.cocos2d.transitions;

import org.cocos2d.actions.interval.CCIntervalAction;

/** CCTransitionEaseScene can ease the actions of the scene protocol.
  @since v0.8.2
  */
public interface CCTransitionEaseScene {
    /** returns the Ease action that will be performed on a linear action.
      @since v0.8.2
      */
    public CCIntervalAction easeAction(CCIntervalAction action);
}
