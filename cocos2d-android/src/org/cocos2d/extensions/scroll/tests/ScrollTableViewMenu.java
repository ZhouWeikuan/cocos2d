/*
 *	Sample code for CCTableView 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll.tests;

import java.util.ArrayList;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.extensions.scroll.CCTableView;
import org.cocos2d.extensions.scroll.CCTableViewCell;
import org.cocos2d.extensions.scroll.CCTableViewDataSource;
import org.cocos2d.extensions.scroll.CCTableViewDelegate;
import org.cocos2d.extensions.scroll.CCTableViewSpriteCell;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class ScrollTableViewMenu extends CCLayer implements CCTableViewDelegate, CCTableViewDataSource{

    private CGSize cellSize_;

	private CCTableView tableView_;
	private ArrayList<String> elements_;
	

	// You need to send an array of sprite names, check on commented code in ScrollView example
	public ScrollTableViewMenu(ArrayList<String> array) {
	    cellSize_ = CGSize.make(77, 78);
	    setIsTouchEnabled(true);
	    elements_ = array;
	    
//		CCLayerColor *clipping = CCLayerColor::layerWithColor(ccc4(255, 255, 255, 255));
//	    clipping.setPosition(ccp(50, 100));
//	    clipping.setContentSize(CGSizeMake(100, 300));
	//    
//	    addChild(clipping);
	    
	    CGSize winSize = CCDirector.sharedDirector().winSize();
	    tableView_ = CCTableView.view(this, CGSize.make(77, 300));//winSize.width, 57));
	    tableView_.tDelegate = this;
	    tableView_.dataSource = this;
//	    tableView_.setClipsToBounds(true);
//	    tableView_.setViewSize(CGSizeMake(100, 300));
	    tableView_.setPosition(CGPoint.ccp(50, 100));

	    //tableView_.setDirection(SWScrollViewDirectionHorizontal);
	    tableView_.setVerticalFillOrder(CCTableView.CCTableViewFillTopDown);

	    addChild(tableView_);
	    
	    //CCSprite *image = CCSprite::spriteWithFile("Icon.png");
	    //tableView_.addChild(image);
	    tableView_.reloadData();
	}

	public void setPosition(CGPoint position)
	{
	    tableView_.setPosition(position);
	}

	public CGPoint getPosition()
	{
	    return tableView_.getPosition();
	}

	public static ScrollTableViewMenu menu(ArrayList<String> array)
	{
		return new ScrollTableViewMenu(array);
	}

	public void registerWithTouchDispatcher()
	{
	    CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
	}

	public boolean containsTouchLocation(MotionEvent event)
	{
	    CGSize s = tableView_.viewSize;
	    CGRect r = CGRect.make(getPosition().x, getPosition().y, s.width, s.height);
	    return CGRect.containsPoint(r, convertTouchToNodeSpace(event));
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
	    if (!containsTouchLocation(event)) return false;
	    return true;
	}

	//SWTableViewDelegate Protocol
	public void tableCellTouched(CCTableView table, CCTableViewCell cell)
	{
	    //setPosition(ccp(getPosition().x, getPosition().y + cellSize_.height));
	}

	//SWTableViewDataSource Protocol
	public CGSize cellSizeForTable(CCTableView table)
	{
	    return cellSize_;
	}

	public CCTableViewCell tableCellAtIndex(CCTableView table, int idx)
	{
	    CCTableViewSpriteCell cell = (CCTableViewSpriteCell) table.dequeueCell();
	    if (cell == null) {
	        cell = new CCTableViewSpriteCell();
	    }

	    String name = (String) elements_.get(idx);
	    CCSprite image = CCSprite.sprite(name);
	    image.setColor(ccColor3B.ccc3(255/(idx + 1), 255/(idx + 1), 255));
	    cell.setSprite(image);
	    cell.getSprite().setPosition(CGPoint.ccp((cellSize_.width - image.getContentSize().width) /2, (cellSize_.height - image.getContentSize().height) / 2));
	    return cell;
	}

	public int numberOfCellsInTableView(CCTableView table)
	{
	    return elements_.size();
	}
}
