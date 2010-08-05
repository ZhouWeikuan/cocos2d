package org.cocos2d.actions.tile;

import org.cocos2d.actions.grid.CCGridAction;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.grid.CCTiledGrid3D;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** Base class for CCTiledGrid3D actions */
public class CCTiledGrid3DAction extends CCGridAction {
	public static CCTiledGrid3DAction action(ccGridSize gSize, float d) {
		return new CCTiledGrid3DAction(gSize, d);
	}
	
	protected CCTiledGrid3DAction(ccGridSize gSize, float d) {
		super(gSize, d);
	}

	public CCGridBase grid() {
		return CCTiledGrid3D.make(gridSize);
	}

	/** returns the tile that belongs to a certain position of the grid */
	public ccQuad3 tile(ccGridSize pos) {
		CCTiledGrid3D g = (CCTiledGrid3D)target.getGrid();
		return g.tile(pos);
	}

	/** returns the non-transformed tile that belongs to a certain position of the grid */
	public ccQuad3 originalTile(ccGridSize pos) {
		CCTiledGrid3D g = (CCTiledGrid3D)target.getGrid();
		return g.originalTile(pos);
	}

	/** sets a new tile to a certain position of the grid */
	public void setTile(ccGridSize pos, ccQuad3 coords) {
		CCTiledGrid3D g = (CCTiledGrid3D)target.getGrid();
		g.setTile(pos, coords);
	}

	@Override
	public CCTiledGrid3DAction copy() {		
		return new CCTiledGrid3DAction(getGridSize(), getDuration());
	}
}
