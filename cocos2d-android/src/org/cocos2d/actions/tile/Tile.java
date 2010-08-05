package org.cocos2d.actions.tile;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

public class Tile {
	CGPoint	position;
	CGPoint	startPosition;
	ccGridSize	delta;

    public static Tile make(CGPoint pos, CGPoint startPos, ccGridSize gs) {
        return new Tile(pos, startPos, gs);
    }

    public static Tile make() {
        return new Tile();
    }

    protected Tile(CGPoint pos, CGPoint startPos, ccGridSize gs) {
        position = CGPoint.make(pos.x, pos.y);
        startPosition = CGPoint.make(startPos.x, startPos.y);
        delta = ccGridSize.ccg(gs.x, gs.y);
    }

    protected Tile() {
        // warning: all members are not initialized
    }
}

