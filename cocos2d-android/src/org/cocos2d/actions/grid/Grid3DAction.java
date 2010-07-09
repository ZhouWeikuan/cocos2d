package org.cocos2d.actions.grid;

import org.cocos2d.grid.Grid3D;
import org.cocos2d.grid.GridBase;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.CCVertex3D;

public class Grid3DAction extends GridAction {

    protected Grid3DAction(ccGridSize gSize, float d) {
        super(gSize, d);
    }


    public GridBase grid() {
        return new Grid3D(gridSize);
    }

    public CCVertex3D vertex(ccGridSize pos) {
        Grid3D g = (Grid3D) target.getGrid();
        return g.vertex(pos);
    }

    public CCVertex3D originalVertex(ccGridSize pos) {
        Grid3D g = (Grid3D) target.getGrid();
        return g.originalVertex(pos);
    }

    public void setVertex(ccGridSize pos, CCVertex3D vertex) {
        Grid3D g = (Grid3D) target.getGrid();
        g.setVertex(pos, vertex);
    }

}
