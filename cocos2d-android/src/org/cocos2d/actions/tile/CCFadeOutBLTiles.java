package org.cocos2d.actions.tile;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCFadeOutBLTiles action.
 Fades out the tiles in a Bottom-Left direction
 */
public class CCFadeOutBLTiles extends CCFadeOutTRTiles {

	public static CCFadeOutBLTiles action(ccGridSize gSize, float d) {
		return new CCFadeOutBLTiles(gSize, d);
	}
	
	protected CCFadeOutBLTiles(ccGridSize gSize, float d) {
		super(gSize, d);
	}
	
	@Override
    public float testFunc(ccGridSize pos, float time) {
        CGPoint	n = CGPoint.ccpMult(CGPoint.ccp(gridSize.x, gridSize.y), (1.0f-time));

        if ( (pos.x+pos.y) == 0 )
            return 1.0f;
        return (float)Math.pow( (n.x+n.y) / (pos.x+pos.y), 6 );
    }

}

