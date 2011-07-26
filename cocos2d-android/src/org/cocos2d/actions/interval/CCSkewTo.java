package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;

public class CCSkewTo extends CCIntervalAction {

	protected float endSkewX;
	protected float endSkewY;
	protected float startSkewX;
	protected float deltaX;
	protected float startSkewY;
	protected float deltaY;

	public static CCSkewTo action(float t, float sx, float sy) {
		return new CCSkewTo(t, sx, sy);
	}
	
	protected CCSkewTo(float t, float sx, float sy) {
		super(t);
		endSkewX = sx;
		endSkewY = sy;
	}

	@Override
	public CCSkewTo copy() {
		return new CCSkewTo(duration, endSkewX, endSkewY);
	}
	
	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);
		
		/** Calculate Skew X */
		startSkewX = target.getSkewX();		
		
		if (startSkewX > 0)
			startSkewX = startSkewX % 180f;
		else
			startSkewX = startSkewX % -180f;
		
		deltaX = endSkewX - startSkewX;
		
		if (deltaX > 180)
			deltaX -= 360;
		else if (deltaX < -180)
			deltaX += 360;
		
		/** Calculate Skew Y */
		startSkewY = target.getSkewY();
		
		if (startSkewY > 0)
			startSkewY = startSkewY % 180f;
		else
			startSkewY = startSkewY % -180f;
		
		deltaY = endSkewY - startSkewY;
			
		if (deltaY > 180)
			deltaY -= 360f;
		else if (deltaY < -180)
			deltaY += 360f;
	}
	
	@Override
	public void update(float t) {
		target.setSkewX(startSkewX + deltaX * t);
		target.setSkewY(startSkewY + deltaY * t);
	}
}
