package org.cocos2d.actions.tile;

import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;


////////////////////////////////////////////////////////////

/** CCJumpTiles3D action.
 A sin function is executed to move the tiles across the Z axis
 */
public class CCJumpTiles3D extends CCTiledGrid3DAction {
	int jumps;
/** amplitude of the sin*/
	float amplitude;
/** amplitude rate */
	float amplitudeRate;


    /** creates the action with the number of jumps, the sin amplitude, the grid size and the duration */
    public static CCJumpTiles3D action(int j, float amp, ccGridSize gridSize, float d) {
        return new CCJumpTiles3D(j, amp, gridSize, d);
    }

    /** initializes the action with the number of jumps, the sin amplitude, the grid size and the duration */
    protected CCJumpTiles3D(int j, float amp, ccGridSize gridSize, float d) {
        super(gridSize, d);
        jumps = j;
        amplitude = amp;
        amplitudeRate = 1.0f;
    }

    @Override
    public CCJumpTiles3D copy() {
        return new CCJumpTiles3D(jumps, amplitude, gridSize, duration);
    }


    @Override
    public void update(float time) {
        int i, j;

        float sinz =  (float)(Math.sin(Math.PI*time*jumps*2) * amplitude * amplitudeRate );
        float sinz2 = (float)(Math.sin(Math.PI*(time*jumps*2 + 1)) * amplitude * amplitudeRate );

        for( i = 0; i < gridSize.x; i++ ) {
            for( j = 0; j < gridSize.y; j++ ) {
                ccQuad3 coords = originalTile(ccGridSize.ccg(i,j));

                if ( ((i+j) % 2) == 0 ) {
                    coords.br_z += sinz;
                    coords.br_z += sinz;
                    coords.tl_z += sinz;
                    coords.tr_z += sinz;
                } else {
                    coords.bl_z += sinz2;
                    coords.br_z += sinz2;
                    coords.tl_z += sinz2;
                    coords.tr_z += sinz2;
                }

                setTile(ccGridSize.ccg(i,j), coords);
            }
        }
    }
}
