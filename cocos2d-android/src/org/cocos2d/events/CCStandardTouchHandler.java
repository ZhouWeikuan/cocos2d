package org.cocos2d.events;

import org.cocos2d.protocols.CCTouchDelegateProtocol;

/** CCStandardTouchHandler
 It forwardes each event to the delegate.
 */

/*
 * This file contains the delegates of the touches
 * There are 2 possible delegates:
 *   - CCStandardTouchHandler: propagates all the events at once
 *   - CCTargetedTouchHandler: propagates 1 event at the time
 */

public class CCStandardTouchHandler extends CCTouchHandler {
    /** allocates a TouchHandler with a delegate and a priority */
    public static CCStandardTouchHandler makeHandler(CCTouchDelegateProtocol delegate, int priority) {
        return new CCStandardTouchHandler(delegate, priority);
    }

    /** initializes a TouchHandler with a delegate and a priority */
    public CCStandardTouchHandler(CCTouchDelegateProtocol delegate, int priority) {
        super(delegate, priority);
        /* TODO: add me?
		if( [del respondsToSelector:@selector(ccTouchesBegan:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorBeganBit;
		if( [del respondsToSelector:@selector(ccTouchesMoved:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorMovedBit;
		if( [del respondsToSelector:@selector(ccTouchesEnded:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorEndedBit;
		if( [del respondsToSelector:@selector(ccTouchesCancelled:withEvent:)] )
			enabledSelectors_ |= ccTouchSelectorCancelledBit;
        */
    }
}

