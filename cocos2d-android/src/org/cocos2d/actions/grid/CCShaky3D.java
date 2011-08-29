package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.utils.GameRand;

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

	GameRand rand = new GameRand();
	CCVertex3D	v;
	ccGridSize gs = ccGridSize.ccg(0, 0);
	int i, j;
	float r;
	@Override
	public void update(float time) {
		for( i = 0; i < (gridSize.x+1); i++ ) {
			gs.x = i;
			for( j = 0; j < (gridSize.y+1); j++ ) {
				gs.y = j;
				v = originalVertex(gs);
				r = rand.nextFloat() * (randrange*2);
				v.x += r - randrange;
				v.y += r - randrange;
				if( shakeZ )
					v.z += r - randrange;
				
				setVertex(gs, v);
			}
		}
	}

}

