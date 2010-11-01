package org.cocos2d.actions.tile;

import java.util.Random;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

/** CCShuffleTiles action
  Shuffle the tiles in random order
  */
public class CCShuffleTiles extends CCTiledGrid3DAction {
    Random rand;
    int	seed;
    int tilesCount;
    int tilesOrder[];
    Tile tiles[];

    /** creates the action with a random seed, the grid size and the duration */
    public static CCShuffleTiles action(int s, ccGridSize gridSize, float d) {
        return new CCShuffleTiles(s, gridSize, d);
    }

    /** initializes the action with a random seed, the grid size and the duration */
    public CCShuffleTiles(int s, ccGridSize gridSize, float d) {
        super(gridSize, d);
        seed = s;
        rand = new Random();
        tilesOrder = null;
        tiles = null;
    }

    @Override
    public CCShuffleTiles copy() {
        return new CCShuffleTiles(seed, gridSize, duration);
    }

    public void shuffle(int[] array, int len) {
        int i;
        for( i = len - 1; i >= 0; i-- ) {
            int j = (int)(Math.random() * (i+1));
            int v = array[i];
            array[i] = array[j];
            array[j] = v;
        }
    }

    public ccGridSize getDelta(ccGridSize pos) {
        CGPoint	pos2 = CGPoint.make(0, 0);
        int idx = pos.x * gridSize.y + pos.y;

        pos2.x = tilesOrder[idx] / (int)gridSize.y;
        pos2.y = tilesOrder[idx] % (int)gridSize.y;

        return ccGridSize.ccg((int)(pos2.x - pos.x), (int)(pos2.y - pos.y));
    }

    public void placeTile(ccGridSize pos, Tile t) {
        ccQuad3	coords = originalTile(pos);

        CGPoint step = target.getGrid().getStep();
        coords.bl_x += (int)(t.position.x * step.x);
        coords.bl_y += (int)(t.position.y * step.y);

        coords.br_x += (int)(t.position.x * step.x);
        coords.br_y += (int)(t.position.y * step.y);

        coords.tl_x += (int)(t.position.x * step.x);
        coords.tl_y += (int)(t.position.y * step.y);

        coords.tr_x += (int)(t.position.x * step.x);
        coords.tr_y += (int)(t.position.y * step.y);

        setTile(pos, coords);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        if ( seed != -1 ) {
            rand.setSeed(seed);
        }

        tilesCount = gridSize.x * gridSize.y;
        tilesOrder = new int[tilesCount]; // (int*)malloc(tilesCount*sizeof(int));
        int i, j;

        for( i = 0; i < tilesCount; i++ )
            tilesOrder[i] = i;

        shuffle(tilesOrder, tilesCount);

        tiles = new Tile[tilesCount]; // malloc(tilesCount*sizeof(Tile));
        Tile tileArray[] = tiles;

        int t = 0;
        for( i = 0; i < gridSize.x; i++ ) {
            for( j = 0; j < gridSize.y; j++ ) {
                tileArray[t] = Tile.make(CGPoint.ccp(i,j),
                					CGPoint.ccp(i,j),
                					getDelta(ccGridSize.ccg(i,j)));
                ++t;
            }
        }
    }

    @Override
    public void update(float time) {
        int i, j;

        Tile tileArray[] = tiles;
        int idx = 0;

        for( i = 0; i < gridSize.x; i++ ) {
            for( j = 0; j < gridSize.y; j++ ) {
                tileArray[idx].position = CGPoint.ccpMult(
                            CGPoint.ccp(tileArray[idx].delta.x, tileArray[idx].delta.y), time);
                placeTile(ccGridSize.ccg(i,j), tileArray[idx]);
                idx ++;
            }
        }
    }
}


