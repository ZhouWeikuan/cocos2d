/*
 *	Port from SWScrollView and SWTableView for iphone 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

public class CCTableViewSpriteCell extends CCTableViewCell {
	private CCSprite m_sprite;

	public void setSprite(CCSprite s) {
	    if (m_sprite != null) {
	        removeChild(m_sprite, false);
	    }
	    
	    s.setAnchorPoint(CGPoint.zero());
	    s.setPosition(CGPoint.zero());
	    m_sprite = s;
	    addChild(m_sprite);
	}
	
	public CCSprite getSprite() {
	    return m_sprite;
	}
	
	@Override
	public void reset() {
		super.reset();
	
	    if (m_sprite != null) {
	        removeChild(m_sprite, false);
	    }
	    m_sprite = null;
	}
}
