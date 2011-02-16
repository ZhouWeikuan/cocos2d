package org.cocos2d.events;

import org.cocos2d.protocols.CCTouchDelegateProtocol;

import android.view.MotionEvent;

public class CCTargetedTouchHandler extends CCTouchHandler {

	boolean swallowsTouches;
	
	private boolean claimed = false;
	
	public CCTargetedTouchHandler(CCTouchDelegateProtocol delegate, int priority, boolean swallow) {
		super(delegate, priority);
		swallowsTouches = swallow;
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		claimed = super.ccTouchesBegan(event);
		return claimed;
	}

	// this return shouldn't meen anything
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		if(claimed) {
			return super.ccTouchesMoved(event);
		}
		return false;
	}
	
	// this return shouldn't meen anything
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		if(claimed) {
			claimed = false;
			return super.ccTouchesEnded(event);
		}
		return false;
	}
	
	@Override
	public boolean ccTouchesCancelled(MotionEvent event) {
		if(claimed) {
			claimed = false;
			return super.ccTouchesCancelled(event);
		}
		return false;
	}
	
}
