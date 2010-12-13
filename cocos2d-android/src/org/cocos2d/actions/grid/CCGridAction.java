package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCReverseTime;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.ccGridSize;


/** Base class for Grid actions */
public abstract class CCGridAction extends CCIntervalAction {
    /** size of the grid */
    protected ccGridSize gridSize;
    
    public void setGridSize(ccGridSize gs) {
    	gridSize = new ccGridSize(gs);
    }
    
    public ccGridSize getGridSize() {
    	return new ccGridSize(gridSize);
    }

    /** initializes the action with size and duration */
    protected CCGridAction(ccGridSize gSize, float d) {
        super(d);
        gridSize = new ccGridSize(gSize);
    }
    
    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        CCGridBase newgrid = grid();
        CCGridBase targetGrid = target.getGrid();

        // Class<?> clazz = newgrid.getClass();
        if (targetGrid != null && targetGrid.reuseGrid() > 0) {
            if (targetGrid.isActive() &&
            		targetGrid.getGridWidth() == gridSize.x &&
            		targetGrid.getGridHeight() == gridSize.y &&
            		targetGrid.getClass() == newgrid.getClass()) {
            	targetGrid.reuse(CCDirector.gl);
            } else {
                throw new RuntimeException("Cannot reuse grid_");
            }
        } else {
            if (targetGrid != null && targetGrid.isActive())
                targetGrid.setActive(false);
            
            target.setGrid(newgrid);
            
            if (targetGrid != null)
            	target.getGrid().setActive(true);
        }
    }
    
    /** returns the grid */
    public abstract CCGridBase grid();

    @Override
    public abstract CCGridAction copy();

    @Override
    public CCIntervalAction reverse() {
        return CCReverseTime.action(this);
    }

}
