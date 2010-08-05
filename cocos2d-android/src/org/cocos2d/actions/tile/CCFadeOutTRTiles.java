package org.cocos2d.actions.tile;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCFadeOutTRTiles action
 Fades out the tiles in a Top-Right direction
 */
public class CCFadeOutTRTiles extends CCTiledGrid3DAction {

	public static CCFadeOutTRTiles action(ccGridSize gSize, float d) {
		return new CCFadeOutTRTiles(gSize, d);
	}
	
	protected CCFadeOutTRTiles(ccGridSize gSize, float d) {
		super(gSize, d);
	}
	
    public float testFunc(ccGridSize pos, float time) {
        CGPoint	n = CGPoint.ccpMult(CGPoint.ccp(gridSize.x,gridSize.y), time);
        if ( (n.x+n.y) == 0.0f )
            return 1.0f;
        return (float)Math.pow( (pos.x+pos.y) / (n.x+n.y), 6 );
    }

    public void turnOnTile(ccGridSize pos) {
        setTile(pos, originalTile(pos));
    }

    public void turnOffTile(ccGridSize pos) {
        ccQuad3	coords = new ccQuad3();	
        setTile(pos, coords);
    }

    public void transformTile(ccGridSize pos, float distance) {
        ccQuad3	coords = originalTile(pos);
        CGPoint	step = target.getGrid().getStep();

        coords.bl_x += (step.x / 2) * (1.0f - distance);
        coords.bl_y += (step.y / 2) * (1.0f - distance);

        coords.br_x -= (step.x / 2) * (1.0f - distance);
        coords.br_y += (step.y / 2) * (1.0f - distance);

        coords.tl_x += (step.x / 2) * (1.0f - distance);
        coords.tl_y -= (step.y / 2) * (1.0f - distance);

        coords.tr_x -= (step.x / 2) * (1.0f - distance);
        coords.tr_y -= (step.y / 2) * (1.0f - distance);

        setTile(pos, coords);
    }

    @Override
    public void update(float time) {
        int i, j;

        for( i = 0; i < gridSize.x; i++ ) {
            for( j = 0; j < gridSize.y; j++ ) {
                float distance = testFunc(ccGridSize.ccg(i,j), time);
                if ( distance == 0 )
                    turnOffTile(ccGridSize.ccg(i,j));
                else if ( distance < 1 )
                    transformTile(ccGridSize.ccg(i,j), distance);
                else
                    turnOnTile(ccGridSize.ccg(i,j));
            }
        }
    }

}

