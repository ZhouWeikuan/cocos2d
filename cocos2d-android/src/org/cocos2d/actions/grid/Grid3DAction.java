package org.cocos2d.actions.grid;

import org.cocos2d.grid.Grid3D;
import org.cocos2d.grid.GridBase;
import org.cocos2d.types.CCGridSize;
import org.cocos2d.types.CCVertex3D;

public class Grid3DAction extends GridAction {

    protected Grid3DAction(CCGridSize gSize, float d) {
        super(gSize, d);
    }


    public GridBase grid() {
        return new Grid3D(gridSize);
    }

    public CCVertex3D vertex(CCGridSize pos) {
        Grid3D g = (Grid3D) target.getGrid();
        return g.vertex(pos);
    }

    public CCVertex3D originalVertex(CCGridSize pos) {
        Grid3D g = (Grid3D) target.getGrid();
        return g.originalVertex(pos);
    }

    public void setVertex(CCGridSize pos, CCVertex3D vertex) {
        Grid3D g = (Grid3D) target.getGrid();
        g.setVertex(pos, vertex);
    }

}
