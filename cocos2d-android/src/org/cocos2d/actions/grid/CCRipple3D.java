package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCRipple3D action */
public class CCRipple3D extends CCGrid3DAction {
	/** center position */
	CGPoint	position;
	
	float	radius;
	int		waves;

	/** amplitude */
	float	amplitude;
	/** amplitude rate */
	float	amplitudeRate;
	
	/** creates the action with radius, number of waves, amplitude, a grid size and duration */
	public static CCRipple3D action(CGPoint pos, float radius, 
			int wav,float amp, ccGridSize gridSize, float d)	{
		return new CCRipple3D(pos, radius, wav, amp, gridSize, d);
	}

	/** initializes the action with radius, number of waves, amplitude, a grid size and duration */
	public CCRipple3D(CGPoint pos, float r, int wav, float amp, 
			ccGridSize gSize, float d) {
		super(gSize, d);
		position = pos;
		radius = r;
		waves = wav;
		amplitude = amp;
		amplitudeRate = 1.0f;		
	}

	@Override
	public CCRipple3D copy() {
		CCRipple3D copy = new CCRipple3D(position, radius, waves, 
				amplitude, gridSize, duration);
		return copy;
	}


	@Override
	public void update(float time) {
		int i, j;
		
		for( i = 0; i < (gridSize.x+1); i++ ) {
			for( j = 0; j < (gridSize.y+1); j++ ) {
				CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
				CGPoint vect = CGPoint.ccpSub(position, CGPoint.ccp(v.x,v.y));
				float r = CGPoint.ccpLength(vect);
				
				if ( r < radius ) {
					r = radius - r;
					float rate = (float)Math.pow( r / radius, 2);
					v.z += (Math.sin( time*Math.PI*waves*2 + r * 0.1f) * amplitude * amplitudeRate * rate );
				}
				
				setVertex(ccGridSize.ccg(i,j), v);
			}
		}
	}

}
