package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCLens3D action */
public class CCLens3D extends CCGrid3DAction {
	CGPoint	position;
	float	radius;
	
	/** lens effect. Defaults to 0.7 - 0 means no effect, 1 is very strong effect */
	float	lensEffect;

	/** lens center position */	
	CGPoint	lastPosition;
	
	/** creates the action with center position, radius, a grid size and duration */
	public static CCLens3D action(CGPoint pos, float r, ccGridSize gridSize, float d) {
		return new CCLens3D(pos, r, gridSize, d);
	}

	/** initializes the action with center position, radius, a grid size and duration */
	public CCLens3D(CGPoint pos, float r, ccGridSize gridSize, float d) {
		super(gridSize, d);

		position = pos;
		radius = r;
		lensEffect = 0.7f;
		lastPosition = CGPoint.ccp(-1,-1);
	}
	
	@Override
	public CCLens3D copy() {
		CCLens3D copy = new CCLens3D(position, radius, gridSize, duration);
		return copy;
	}

	@Override
	public void update(float time) {
		if ( position.x != lastPosition.x || position.y != lastPosition.y ) {
			int i, j;
			
			for( i = 0; i < gridSize.x+1; i++ ) {
				for( j = 0; j < gridSize.y+1; j++ ) {
					CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
					CGPoint vect = CGPoint.ccpSub(position, CGPoint.ccp(v.x,v.y));
					float r = CGPoint.ccpLength(vect);
					
					if ( r < radius ) {
						r = radius - r;
						float pre_log = r / radius;
						if ( pre_log == 0 ) pre_log = 0.001f;
						float l = (float)Math.log(pre_log) * lensEffect;
						float new_r = (float)Math.exp( l ) * radius;
						
						if ( CGPoint.ccpLength(vect) > 0 ) {
							vect = CGPoint.ccpNormalize(vect);
							CGPoint new_vect = CGPoint.ccpMult(vect, new_r);
							v.z += CGPoint.ccpLength(new_vect) * lensEffect;
						}
					}
					
					setVertex(ccGridSize.ccg(i,j), v);
				}
			}
			
			lastPosition = position;
		}
	}

}
