package org.cocos2d.actions.tile;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCFadeOutDownTiles action.
 Fades out the tiles in downwards direction
 */
public class CCFadeOutDownTiles extends CCFadeOutUpTiles {
	public static CCFadeOutDownTiles action(ccGridSize gSize, float d) {
		return new CCFadeOutDownTiles(gSize, d);
	}
	
	protected CCFadeOutDownTiles(ccGridSize gSize, float d) {
		super(gSize, d);
	}
	
	@Override
    public float testFunc(ccGridSize pos, float time) {
        CGPoint	n = CGPoint.ccpMult(CGPoint.ccp(gridSize.x,gridSize.y), (1.0f - time));
        if ( pos.y == 0 )
            return 1.0f;
        return (float)Math.pow( n.y / pos.y, 6 );
    }

}

