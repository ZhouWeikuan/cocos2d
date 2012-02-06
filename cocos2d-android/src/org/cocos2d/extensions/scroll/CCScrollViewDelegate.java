/*
 *	Port from SWScrollView and SWTableView for iphone 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll;

public interface CCScrollViewDelegate {
	public void scrollViewDidScroll(CCScrollView view);
	public void scrollViewDidZoom(CCScrollView view);
}
