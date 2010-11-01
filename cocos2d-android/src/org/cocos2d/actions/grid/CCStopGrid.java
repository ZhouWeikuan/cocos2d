package org.cocos2d.actions.grid;

import org.cocos2d.actions.instant.CCInstantAction;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.nodes.CCNode;

////////////////////////////////////////////////////////////

/** CCStopGrid action.
 Don't call this action if another grid action is active.
 Call if you want to remove the the grid effect. Example:
 [Sequence actions:[Lens ...], [StopGrid action], nil];
 */
public class CCStopGrid extends CCInstantAction {
	
	public static CCStopGrid action() {
		return new CCStopGrid();
	}

	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);

		CCGridBase gb = this.target.getGrid();
		if ( gb != null && gb.isActive() ) {
			gb.setActive(false);
		}
	}

}
