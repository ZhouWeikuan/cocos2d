package org.cocos2d.actions.tile;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCSplitCols action */
public class CCSplitCols extends CCTiledGrid3DAction {
	int		cols;
	CGSize	winSize;

    /** creates the action with the number of columns to split and the duration */
    public static CCSplitCols action(int c, float d) {
        return new CCSplitCols(c, d);
    }

    /** initializes the action with the number of columns to split and the duration */
    protected CCSplitCols(int c, float d) {
        super(ccGridSize.ccg(c, 1), d);
        cols = c;
    }

    @Override
    public CCSplitCols copy() {
        return new CCSplitCols(cols, duration);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        winSize = CCDirector.sharedDirector().winSize();
    }

    @Override
    public void update(float time) {
        int i;

        for( i = 0; i < gridSize.x; i++ ) {
            ccQuad3 coords = originalTile(ccGridSize.ccg(i,0));
            float	direction = 1;

            if ( (i % 2 ) == 0 )
                direction = -1;

            coords.bl_y += direction * winSize.height * time;
            coords.br_y += direction * winSize.height * time;
            coords.tl_y += direction * winSize.height * time;
            coords.tr_y += direction * winSize.height * time;

            setTile(ccGridSize.ccg(i,0), coords);
        }
    }

}

