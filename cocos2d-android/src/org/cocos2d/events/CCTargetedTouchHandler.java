package org.cocos2d.events;

import java.util.HashSet;

import org.cocos2d.protocols.CCTargetedTouchDelegateProtocol;

/**
    CCTargetedTouchHandler
    Object than contains the claimed touches and if it swallows touches.
    Used internally by TouchDispatcher
*/

/*
 * This file contains the delegates of the touches
 * There are 2 possible delegates:
 *   - CCStandardTouchHandler: propagates all the events at once
 *   - CCTargetedTouchHandler: propagates 1 event at the time
*/

public class CCTargetedTouchHandler extends CCTouchHandler {
    /** whether or not the touches are swallowed */
	private boolean swallowsTouches_;

    /** MutableSet that contains the claimed touches */
	private HashSet<?> claimedTouches_;

    public void setSwallowsTouches(boolean st) {
        swallowsTouches_ = st;
    }

    public boolean getSwallowsTouches() {
        return swallowsTouches_;
    }

    public HashSet<?> getClaimedTouches() {
        return claimedTouches_;
    }

    /** allocates a TargetedTouchHandler with a delegate, a priority and whether or not 
            it swallows touches or not 
    */
    public CCTargetedTouchHandler
    makeHandler(CCTargetedTouchDelegateProtocol aDelegate, int priority, boolean isSwallowsTouches) {
        return new CCTargetedTouchHandler(aDelegate, priority, isSwallowsTouches);
    }

    /** initializes a TargetedTouchHandler with a delegate, 
     *      a priority and whether or not it swallows touches or not 
    */
    public CCTargetedTouchHandler(CCTargetedTouchDelegateProtocol aDelegate,
                    int priority, boolean isSwallowsTouches) {
        super(aDelegate, priority);
        swallowsTouches_ = isSwallowsTouches;

        /*
		if( [aDelegate respondsToSelector:@selector(ccTouchBegan:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorBeganBit;
		if( [aDelegate respondsToSelector:@selector(ccTouchMoved:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorMovedBit;
		if( [aDelegate respondsToSelector:@selector(ccTouchEnded:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorEndedBit;
		if( [aDelegate respondsToSelector:@selector(ccTouchCancelled:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorCancelledBit;
        */
    }

    // private void updateKnownTouches(NSMutableSet *touches, UIEvent * event, SEL selector, BOOL doUnclaim);
}

