package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCReverseTime;
import org.cocos2d.grid.CCGrid3D;
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
    
    CCGridBase targetGrid;
    Class<? extends CCGridBase> lastclass = null;
    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        
        //CCGridBase newgrid = grid();
        targetGrid = target.getGrid();


        // Class<?> clazz = newgrid.getClass();
        if (targetGrid != null && targetGrid.reuseGrid() > 0) {
        	if (lastclass == null)
        		lastclass = targetGrid.getClass();
        	
            if (targetGrid.isActive() &&
            		targetGrid.getGridWidth() == gridSize.x &&
            		targetGrid.getGridHeight() == gridSize.y &&
            		targetGrid.getClass() == lastclass) {
            	targetGrid.reuse(CCDirector.gl);
            } else {
                throw new RuntimeException("Cannot reuse grid_");
            }
        } else {
            if (targetGrid != null && targetGrid.isActive())
                targetGrid.setActive(false);
            
            targetGrid = grid();
            target.setGrid(targetGrid);
            lastclass = targetGrid.getClass();
            
            if (targetGrid != null)
            	target.getGrid().setActive(true);
        }
    }
    
    /** returns the grid */
    public abstract CCGridBase grid();
    
    public abstract Class gridClass();

    @Override
    public abstract CCGridAction copy();

    @Override
    public CCIntervalAction reverse() {
        return CCReverseTime.action(this);
    }

}
