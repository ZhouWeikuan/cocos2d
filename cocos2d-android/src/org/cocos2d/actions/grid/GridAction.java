package org.cocos2d.actions.grid;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.ReverseTime;
import org.cocos2d.grid.GridBase;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.types.CCGridSize;

public abstract class GridAction extends IntervalAction {
    CCGridSize gridSize;


    protected GridAction(CCGridSize gSize, float d) {
        super(d);
        gridSize = gSize;
    }

    public void start(CocosNode aTarget) {
        super.start(aTarget);

        GridBase newgrid = grid();

        // Class<?> clazz = newgrid.getClass();
        if (target.getGrid() != null && target.getGrid().reuseGrid()) {
            if (target.getGrid().isActive() &&
                    target.getGrid().getGridWidth() == gridSize.x &&
                    target.getGrid().getGridHeight() == gridSize.y &&
                    target.getGrid().getClass() == newgrid.getClass()) {
                target.getGrid().reuse();
            } else {
                throw new RuntimeException("Cannot reuse grid_");
            }
        } else {
            if (target.getGrid() != null && target.getGrid().isActive())
                target.getGrid().setActive(false);
            target.setGrid(newgrid);
            target.getGrid().setActive(true);
        }
    }

    public abstract GridBase grid();

    public IntervalAction reverse() {
        return ReverseTime.action(this);
    }

}
