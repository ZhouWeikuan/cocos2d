package org.cocos2d.actions.tile;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCFadeOutUpTiles action.
 Fades out the tiles in upwards direction
 */
public class CCFadeOutUpTiles extends CCFadeOutTRTiles {
	public static CCFadeOutUpTiles action(ccGridSize gSize, float d) {
		return new CCFadeOutUpTiles(gSize, d);
	}
	
	protected CCFadeOutUpTiles(ccGridSize gSize, float d) {
		super(gSize, d);
	}
	
	
	@Override
    public float testFunc(ccGridSize pos, float time) {
        CGPoint	n = CGPoint.ccpMult(CGPoint.ccp(gridSize.x, gridSize.y), time);
        if ( n.y == 0 )
            return 1.0f;
        return (float)Math.pow( pos.y / n.y, 6 );
    }

	@Override
    public void transformTile(ccGridSize pos, float distance) {
        ccQuad3	coords = originalTile(pos);
        CGPoint step = target.getGrid().getStep();

        coords.bl_y += (step.y / 2) * (1.0f - distance);
        coords.br_y += (step.y / 2) * (1.0f - distance);
        coords.tl_y -= (step.y / 2) * (1.0f - distance);
        coords.tr_y -= (step.y / 2) * (1.0f - distance);

        setTile(pos, coords);
    }

}

