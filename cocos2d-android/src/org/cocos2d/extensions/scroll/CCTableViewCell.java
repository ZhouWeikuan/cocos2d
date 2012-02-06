/*
 *	Port from SWScrollView and SWTableView for iphone 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll;

import org.cocos2d.nodes.CCNode;

public class CCTableViewCell extends CCNode {
	private int m_idx;
		
	public void reset() {
	    m_idx = Integer.MAX_VALUE;
	}
	    
	public void setObjectID(int i) {
	    m_idx = i;
	}
	    
	public int getObjectID() {
	    return m_idx;
	}

}
