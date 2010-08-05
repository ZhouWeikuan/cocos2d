package org.cocos2d.actions.grid;

import org.cocos2d.actions.instant.CCInstantAction;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.nodes.CCNode;

////////////////////////////////////////////////////////////

/** CCReuseGrid action */
public class CCReuseGrid extends CCInstantAction {
	int t;
	
	/** creates an action with the number of times that the current grid will be reused */
	public static CCReuseGrid action(int times) {
		return new CCReuseGrid(times);		
	}
	
	/** initializes an action with the number of times that the current grid will be reused */
	public CCReuseGrid(int times) {
		super();
		t = times;		
	}

	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);

		CCGridBase gb = target.getGrid();
		if ( gb != null && gb.isActive() )
			gb.setReuseGrid(gb.reuseGrid() + t);
	}

}
