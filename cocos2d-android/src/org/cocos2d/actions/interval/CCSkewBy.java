package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

public class CCSkewBy extends CCSkewTo {

	private float skewX;
	private float skewY;

	public static CCSkewBy action(float t, float dx, float dy) {
		return new CCSkewBy(t, dx, dy);
	}
	
	protected CCSkewBy(float t, float dx, float dy) {
		super(t, dx, dy);
		skewX = dx;
		skewY = dy;
	}
	
	@Override
	public CCSkewBy copy() {
		return new CCSkewBy(duration, skewX, skewY);
	}

	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);
		
		deltaX = skewX;
		deltaY = skewY;
		
		endSkewX = startSkewX + deltaX;
		endSkewY = startSkewY + deltaY;
	}
}
