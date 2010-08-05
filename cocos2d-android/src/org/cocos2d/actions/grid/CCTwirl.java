package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

/** CCTwirl action */
public class CCTwirl extends CCGrid3DAction {
	/** twirl center */
	CGPoint	position;

	int		twirls;
	/** amplitude */
	float	amplitude;
	/** amplitude rate */
	float	amplitudeRate;
	

	/** creates the action with center position, number of twirls, amplitude, a grid size and duration */
	public static CCTwirl action(CGPoint pos, int t, float amp, ccGridSize gridSize, float d) {
		return new CCTwirl(pos, t, amp, gridSize, d);
	}

	/** initializes the action with center position, number of twirls, amplitude, a grid size and duration */
	public CCTwirl(CGPoint pos, int t, float amp, ccGridSize gSize, float d) {
		super(gSize, d);
			position = CGPoint.make(pos.x, pos.y);
			twirls = t;
			amplitude = amp;
			amplitudeRate = 1.0f;
	}

	@Override
	public void update (float time) {
		int i, j;
		CGPoint		c = position;

		CGPoint	d = new CGPoint();
		
		for( i = 0; i < (gridSize.x+1); i++ ) {
			for( j = 0; j < (gridSize.y+1); j++ ) {
				CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
				
				CGPoint	avg = CGPoint.ccp(i-(gridSize.x/2.0f), j-(gridSize.y/2.0f));
				float r = CGPoint.ccpLength( avg );
				
				float amp = 0.1f * amplitude * amplitudeRate;
				float a = (float)(r * Math.cos(Math.PI/2.0f + time * Math.PI * twirls * 2 ) * amp);
				
				
				d.x = (float)(Math.sin(a) * (v.y-c.y) + Math.cos(a) * (v.x-c.x));
				d.y = (float)(Math.cos(a) * (v.y-c.y) - Math.sin(a) * (v.x-c.x));
				
				v.x = c.x + d.x;
				v.y = c.y + d.y;
				
				setVertex(ccGridSize.ccg(i,j), v);
			}
		}
	}

	@Override
	public CCTwirl copy() {
		CCTwirl copy = new CCTwirl(position, twirls, amplitude, gridSize, duration);
		return copy;
	}

}
