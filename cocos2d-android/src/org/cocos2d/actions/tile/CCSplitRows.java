package org.cocos2d.actions.tile;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCSplitRows action */
public class CCSplitRows extends CCTiledGrid3DAction {
	int		rows;
	CGSize	winSize;

    /** creates the action with the number of rows to split and the duration */
    public static CCSplitRows action(int r, float d) {
        return new CCSplitRows(r, d);
    }

    /** initializes the action with the number of rows to split and the duration */
    protected CCSplitRows(int r, float d) {
        super(ccGridSize.ccg(1, r), d);
        rows = r;
    }

    @Override
    public CCSplitRows copy() {
        return new CCSplitRows(rows, duration);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        winSize = CCDirector.sharedDirector().winSize();
    }

    @Override
    public void update(float time) {
        int j;

        for( j = 0; j < gridSize.y; j++ ) {
            ccQuad3 coords = originalTile(ccGridSize.ccg(0,j));
            float	direction = 1;

            if ( (j % 2 ) == 0 )
                direction = -1;

            coords.bl_x += direction * winSize.width * time;
            coords.br_x += direction * winSize.width * time;
            coords.tl_x += direction * winSize.width * time;
            coords.tr_x += direction * winSize.width * time;

            setTile(ccGridSize.ccg(0,j), coords);
        }
    }

}

