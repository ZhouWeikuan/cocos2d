package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCFlipY3D action */
public class CCFlipY3D extends CCFlipX3D {
	public static CCIntervalAction action(float duration) {
		return new CCFlipY3D(duration);
	}
	
	public static CCFlipY3D action(ccGridSize gridSize, float duration) {
		return new CCFlipY3D(gridSize, duration);
	}
	
	protected CCFlipY3D(float duration) {
		this(ccGridSize.ccg(1, 1), duration);
	}

	protected CCFlipY3D(ccGridSize gridSize, float duration) {
		super(gridSize, duration);
	}
	
	@Override
	public void update(float time) {
		float angle = (float)Math.PI * time; // 180 degrees
		float mz = (float)Math.sin( angle );
		angle = angle / 2.0f;     // x calculates degrees from 0 to 90
		float my = (float)Math.cos( angle );
		
		CCVertex3D	v0, v1, v;
		CCVertex3D diff = new CCVertex3D(0, 0, 0);
		
		v0 = originalVertex(ccGridSize.ccg(1,1));
		v1 = originalVertex(ccGridSize.ccg(0,0));
		
		float	y0 = v0.y;
		float	y1 = v1.y;
		float y;
		ccGridSize	a, b, c, d;
		
		if ( y0 > y1 ) {
			// Normal Grid
			a = ccGridSize.ccg(0,0);
			b = ccGridSize.ccg(0,1);
			c = ccGridSize.ccg(1,0);
			d = ccGridSize.ccg(1,1);
			y = y0;
		} else {
			// Reversed Grid
			b = ccGridSize.ccg(0,0);
			a = ccGridSize.ccg(0,1);
			d = ccGridSize.ccg(1,0);
			c = ccGridSize.ccg(1,1);
			y = y1;
		}
		
		diff.y = y - y * my;
		diff.z = (float)Math.abs( Math.floor( (y * mz) / 4.0f ) );
		
		// bottom-left
		v = originalVertex(a);
		v.y = diff.y;
		v.z += diff.z;
		setVertex(a, v);
		
		// upper-left
		v = originalVertex(b);
		v.y -= diff.y;
		v.z -= diff.z;
		setVertex(b, v);
		
		// bottom-right
		v = originalVertex(c);
		v.y = diff.y;
		v.z += diff.z;
		setVertex(c, v);
		
		// upper-right
		v = originalVertex(d);
		v.y -= diff.y;
		v.z -= diff.z;
		setVertex(d, v);
	}
}


