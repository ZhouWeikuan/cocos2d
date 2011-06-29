package org.cocos2d.menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;


/** A CCMenuItemToggle
 A simple container class that "toggles" it's inner items
 The inner itmes can be any MenuItem
 */

public class CCMenuItemToggle extends CCMenuItem {
    /** returns the selected item */
    private int selectedIndex_;
    /** NSMutableArray that contains the subitems. You can add/remove items in runtime, and you can replace the array with a new one.
      @since v0.7.2
    */
    private ArrayList<CCMenuItem> subItems_;
    public ArrayList<CCMenuItem> getSubItemsRef() {
    	if (subItems_ == null) {
    		subItems_ = new ArrayList<CCMenuItem>();
    	}
    	return subItems_;	
    }
    
    /** conforms with CCRGBAProtocol protocol */
    private byte opacity_;
    /** conforms with CCRGBAProtocol protocol */
    ccColor3B color_;

    /** creates a menu item from a list of items with a target/selector */
    public static CCMenuItemToggle item(CCNode target, String selector, CCMenuItem... items) {
        return new CCMenuItemToggle(target, selector, items);
    }

    /** initializes a menu item from a list of items with a target selector */
    protected CCMenuItemToggle(CCNode t, String sel, CCMenuItem... items) {
        super(t, sel);

        subItems_ = new ArrayList<CCMenuItem>(items.length);

        subItems_.addAll(Arrays.asList(items));

        selectedIndex_ = Integer.MAX_VALUE;
        setSelectedIndex(0);
    }

    public void setSelectedIndex(int index) {
        if (index != selectedIndex_) {
            selectedIndex_ = index;
            removeChildByTag(kCurrentItem, false);

            CCMenuItem item = subItems_.get(selectedIndex_);
            addChild(item, 0, kCurrentItem);

            CGSize s = item.getContentSize();
            setContentSize(s);
            item.setPosition(CGPoint.make(s.width / 2, s.height / 2));
        }
    }

    public int selectedIndex() {
        return selectedIndex_;
    }

    @Override
    public void selected() {
        super.selected();
        subItems_.get(selectedIndex_).selected();
    }

    @Override
    public void unselected() {
        super.unselected();
        subItems_.get(selectedIndex_).unselected();
    }

    @Override
    public void activate() {
        // update index

        if (isEnabled_) {
            int newIndex = (selectedIndex_ + 1) % subItems_.size();
            setSelectedIndex(newIndex);

        }
        super.activate();
    }

    @Override
    public void setIsEnabled(boolean enabled) {
        super.setIsEnabled(enabled);
        for (CCMenuItem item : subItems_)
            item.setIsEnabled(enabled);
    }

    /** return the selected item */
    public CCMenuItem selectedItem() {
        return subItems_.get(selectedIndex_);
    }

    public void setOpacity(byte opacity) {
        opacity_ = opacity;
        for (CCMenuItem item : subItems_)
            ((CCRGBAProtocol) item).setOpacity(opacity);
    }
    
    public byte getOpacity() {
    	return opacity_;
    }

    public void setColor(ccColor3B color) {
        color_ = color;
        for (CCMenuItem item : subItems_)
            ((CCRGBAProtocol) item).setColor(color);
    }
}

