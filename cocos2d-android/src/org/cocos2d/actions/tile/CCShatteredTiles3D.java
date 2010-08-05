package org.cocos2d.actions.tile;

import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCShatteredTiles3D action */
public class CCShatteredTiles3D extends CCTiledGrid3DAction {
	int		randrange;
	boolean	once;
	boolean	shatterZ;

    /** creates the action with a range, whether of not to shatter Z vertices, a grid size and duration */
    public static CCShatteredTiles3D action(int range, boolean sz, ccGridSize gridSize, float d) {
        return new CCShatteredTiles3D(range, sz, gridSize, d);
    }

    /** initializes the action with a range, whether or not to shatter Z vertices, a grid size and duration */
    public CCShatteredTiles3D(int range, boolean sz, ccGridSize gridSize, float d) {
        super(gridSize, d);
        once = false;
        randrange = range;
        shatterZ = sz;
    }

    @Override
    public CCShatteredTiles3D copy() {
        return new CCShatteredTiles3D(randrange, shatterZ, gridSize, duration);
    }

    @Override
    public void update(float time) {
        int i, j;

        if ( once == false ) {
            for( i = 0; i < gridSize.x; i++ ) {
                for( j = 0; j < gridSize.y; j++ ) {
                    ccQuad3 coords = originalTile(ccGridSize.ccg(i,j));

                    // X
                    coords.bl_x += ( Math.random() * (randrange*2) ) - randrange;
                    coords.br_x += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tl_x += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tr_x += ( Math.random() * (randrange*2) ) - randrange;

                    // Y
                    coords.bl_y += ( Math.random() * (randrange*2) ) - randrange;
                    coords.br_y += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tl_y += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tr_y += ( Math.random() * (randrange*2) ) - randrange;

                    if( shatterZ ) {
                        coords.bl_z += ( Math.random() * (randrange*2) ) - randrange;
                        coords.br_z += ( Math.random() * (randrange*2) ) - randrange;				
                        coords.tl_z += ( Math.random() * (randrange*2) ) - randrange;
                        coords.tr_z += ( Math.random() * (randrange*2) ) - randrange;
                    }

                    setTile(ccGridSize.ccg(i,j), coords);
                }
            }

            once = true;
        }
    }

}

