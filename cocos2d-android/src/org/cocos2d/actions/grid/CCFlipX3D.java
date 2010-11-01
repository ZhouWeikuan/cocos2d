package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCFlipX3D action */
public class CCFlipX3D extends CCGrid3DAction {
	/** creates the action with duration */
	public static CCIntervalAction action(float duration) {
		return new CCFlipX3D(ccGridSize.ccg(1, 1), duration);
	}
	
	/** initizlies the action with duration */
	public CCFlipX3D(float duration) {
		this(ccGridSize.ccg(1, 1), duration);
	}
	
	public CCFlipX3D(ccGridSize gridSize, float d) {
		super(gridSize, d);
		assert (gridSize.x==1&&gridSize.y==1):"Grid size must be (1,1)";		
	}
	
	@Override
	public CCFlipX3D copy() {
		return new CCFlipX3D(this.getGridSize(), this.getDuration());
	}

	@Override
	public void update(float time) {
		float angle = (float)Math.PI * time; // 180 degrees
		float mz = (float)Math.sin( angle );
		angle = angle / 2.0f;     // x calculates degrees from 0 to 90
		float mx = (float)Math.cos( angle );

		CCVertex3D	v0, v1, v;

		v0 = this.originalVertex(ccGridSize.ccg(1, 1)); 
		v1 = this.originalVertex(ccGridSize.ccg(0, 0));

		float	x0 = v0.x;
		float	x1 = v1.x;
		float x;
		ccGridSize	a, b, c, d;

		if ( x0 > x1 ) {
			// Normal Grid
			a = ccGridSize.ccg(0,0);
			b = ccGridSize.ccg(0,1);
			c = ccGridSize.ccg(1,0);
			d = ccGridSize.ccg(1,1);
			x = x0;
		} else {
			// Reversed Grid
			c = ccGridSize.ccg(0,0);
			d = ccGridSize.ccg(0,1);
			a = ccGridSize.ccg(1,0);
			b = ccGridSize.ccg(1,1);
			x = x1;
		}

		CCVertex3D diff = new CCVertex3D(0, 0, 0);
		diff.x = ( x - x * mx );
		diff.z = (float)Math.abs( Math.floor( (x * mz) / 4.0f ) );

		// bottom-left
		v = originalVertex(a);
		v.x = diff.x;
		v.z += diff.z;
		setVertex(a, v);

		// upper-left
		v = originalVertex(b);
		v.x = diff.x;
		v.z += diff.z;
		setVertex(b, v);

		// bottom-right
		v = originalVertex(c);
		v.x -= diff.x;
		v.z -= diff.z;
		setVertex(c, v);

		// upper-right
		v = originalVertex(d);
		v.x -= diff.x;
		v.z -= diff.z;
		setVertex(d, v);
	}

}

