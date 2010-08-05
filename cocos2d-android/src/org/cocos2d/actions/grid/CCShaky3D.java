package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCShaky3D action */
public class CCShaky3D extends CCGrid3DAction {
	int randrange;
	boolean	shakeZ;


	/** creates the action with a range, shake Z vertices, a grid and duration */
	public static CCShaky3D action(int range, boolean sz, ccGridSize gridSize, float d) {
		return new CCShaky3D(range, sz, gridSize, d);
	}

	/** initializes the action with a range, shake Z vertices, a grid and duration */
	public CCShaky3D(int range, boolean sz, ccGridSize gSize, float d) {
		super(gSize, d);
		randrange = range;
		shakeZ = sz;
	}

	@Override
	public CCShaky3D copy()	{
		CCShaky3D copy = new CCShaky3D(randrange, shakeZ, gridSize, duration);
		return copy;
	}

	@Override
	public void update(float time) {
		int i, j;
		
		for( i = 0; i < (gridSize.x+1); i++ ) {
			for( j = 0; j < (gridSize.y+1); j++ ) {
				CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
				float r = (float)Math.random() * (randrange*2);
				v.x += r - randrange;
				v.y += r - randrange;
				if( shakeZ )
					v.z += r - randrange;
				
				setVertex(ccGridSize.ccg(i,j), v);
			}
		}
	}

}

