package org.cocos2d.actions.grid;

import org.cocos2d.grid.CCGrid3D;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

/** Base class for CCGrid3D actions.
Grid3D actions can modify a non-tiled grid.
*/
public class CCGrid3DAction extends CCGridAction {

	public static CCGrid3DAction action(ccGridSize gSize, float d) {
		return new CCGrid3DAction(gSize, d);
	}
	
    protected CCGrid3DAction(ccGridSize gSize, float d) {
        super(gSize, d);
    }

    public CCGridBase grid() {
        return new CCGrid3D(gridSize);
    }

    /** returns the vertex than belongs to certain position
     * 	 in the grid */
    public CCVertex3D vertex(ccGridSize pos) {
        return ((CCGrid3D) target.getGrid()).vertex(pos);
    }

    /** returns the non-transformed vertex than belongs to 
     * certain position in the grid */
    public CCVertex3D originalVertex(ccGridSize pos) {
        return ((CCGrid3D) target.getGrid()).originalVertex(pos);
    }

    /** sets a new vertex to a certain position of the grid */
    public void setVertex(ccGridSize pos, CCVertex3D vertex) {
        ((CCGrid3D) target.getGrid()).setVertex(pos, vertex);
    }

    @Override
    public CCGrid3DAction copy() {
    	return new CCGrid3DAction(getGridSize(), getDuration());
    }

	@Override
	public Class<CCGrid3D> gridClass() {
		return CCGrid3D.class;
	}

}
