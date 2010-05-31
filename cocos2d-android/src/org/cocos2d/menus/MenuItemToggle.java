package org.cocos2d.menus;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.types.CCColor3B;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuItemToggle extends MenuItem {
    private int selectedIndex_;
    private ArrayList<MenuItem> subItems_;
    private byte opacity_;
    CCColor3B color_;

    public static MenuItemToggle item(CocosNode target, String selector, MenuItem... items) {
        return new MenuItemToggle(target, selector, items);
    }

    protected MenuItemToggle(CocosNode t, String sel, MenuItem... items) {
        super(t, sel);

        subItems_ = new ArrayList<MenuItem>(items.length);

        subItems_.addAll(Arrays.asList(items));


        selectedIndex_ = Integer.MAX_VALUE;
        setSelectedIndex(0);

    }

    public void setSelectedIndex(int index) {
        if (index != selectedIndex_) {
            selectedIndex_ = index;
            removeChild(kCurrentItem, false);

            MenuItem item = subItems_.get(selectedIndex_);
            addChild(item, 0, kCurrentItem);

            float width = item.getWidth();
            float height = item.getHeight();

            setContentSize(width, height);
            item.setPosition(width / 2, height / 2);
        }
    }

    public int selectedIndex() {
        return selectedIndex_;
    }

    @Override
    public void selected() {
        subItems_.get(selectedIndex_).selected();
    }

    @Override
    public void unselected() {
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
        for (MenuItem item : subItems_)
            item.setIsEnabled(enabled);
    }

    public MenuItem selectedItem() {
        return subItems_.get(selectedIndex_);
    }

    public void setOpacity(byte opacity) {
        opacity_ = opacity;
        for (MenuItem item : subItems_)
            ((CocosNodeRGBA) item).setOpacity(opacity);
    }

    public void setColor(CCColor3B color) {
        color_ = color;
        for (MenuItem item : subItems_)
            ((CocosNodeRGBA) item).setColor(color);
    }

}
