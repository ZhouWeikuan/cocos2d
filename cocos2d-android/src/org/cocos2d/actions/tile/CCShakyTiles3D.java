package org.cocos2d.actions.tile;

import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

/** CCShakyTiles3D action */
public class CCShakyTiles3D extends CCTiledGrid3DAction {
	int		randrange;
	boolean	shakeZ;


    /** creates the action with a range, whether or not to shake Z vertices, a grid size, and duration */
    public static CCShakyTiles3D action(int range, boolean shakeZ, ccGridSize gridSize, float d) {
        return new CCShakyTiles3D(range, shakeZ, gridSize, d);
    }

    /** initializes the action with a range, whether or not to shake Z vertices, a grid size, and duration */
    protected CCShakyTiles3D (int range, boolean sz, ccGridSize gridSize, float d) {
        super(gridSize, d);
        randrange = range;
        shakeZ = sz;
    }

    @Override
    public CCShakyTiles3D copy() {
        return new CCShakyTiles3D(randrange, shakeZ, gridSize, duration);
    }

    @Override
    public void update(float time) {
        int i, j;

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

                if( shakeZ ) {
                    coords.bl_z += ( Math.random() * (randrange*2) ) - randrange;
                    coords.br_z += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tl_z += ( Math.random() * (randrange*2) ) - randrange;
                    coords.tr_z += ( Math.random() * (randrange*2) ) - randrange;
                }

                setTile(ccGridSize.ccg(i,j), coords);
            }
        }
    }
}

