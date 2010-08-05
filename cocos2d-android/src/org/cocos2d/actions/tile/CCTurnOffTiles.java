package org.cocos2d.actions.tile;

import java.util.Random;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

/** CCTurnOffTiles action.
 Turn off the files in random order
 */
public class CCTurnOffTiles extends CCTiledGrid3DAction {
	Random rand;
	int	seed;
	int tilesCount;
	int tilesOrder[];


    /** creates the action with a random seed, the grid size and the duration */
    public static CCTurnOffTiles action(int s, ccGridSize gridSize, float d) {
    	return new CCTurnOffTiles(s, gridSize, d);
    }

    /** initializes the action with a random seed, the grid size and the duration */
    protected CCTurnOffTiles(int s, ccGridSize gridSize, float d) {
        super(gridSize, d);
        seed = s;
        rand = new Random();
        tilesOrder = null;
    }

    @Override
    public CCTurnOffTiles copy() {
        return new CCTurnOffTiles(seed, gridSize, duration);
    }

    public void shuffle(int []array, int len) {
        for(int i = len - 1; i>=0; i-- ) {
            int j = (int)(rand.nextFloat() * (i+1));
            int v = array[i];
            array[i] = array[j];
            array[j] = v;
        }
    }

    public void turnOnTile(ccGridSize pos) {
        setTile(pos, originalTile(pos));
    }

    public void turnOffTile(ccGridSize pos) {
        ccQuad3	coords = new ccQuad3();
        setTile(pos, coords);
    }

    public void start(CCNode aTarget) {
        super.start(aTarget);

        if ( seed != -1 ) {
        	rand.setSeed(seed);
        }

        tilesCount = gridSize.x * gridSize.y;
        tilesOrder = new int[tilesCount];

        for(int i = 0; i < tilesCount; i++ )
            tilesOrder[i] = i;

        shuffle(tilesOrder, tilesCount);
    }

    @Override
    public void update(float time) {
        int i, l, t;

        l = (int)(time * (float)tilesCount);

        for( i = 0; i < tilesCount; i++ ) {
            t = tilesOrder[i];
            ccGridSize tilePos = ccGridSize.ccg( t / gridSize.y, t % gridSize.y );

            if ( i < l )
                turnOffTile(tilePos);
            else
                turnOnTile(tilePos);
        }
    }

}

