package org.cocos2d.menus;

import org.cocos2d.nodes.AtlasSprite;
import org.cocos2d.nodes.CocosNode;

import javax.microedition.khronos.opengles.GL10;

public class MenuItemAtlasSprite extends MenuItemSprite {


    public static MenuItemAtlasSprite item(AtlasSprite normalSprite, AtlasSprite selectedSprite, AtlasSprite disabledSprite, CocosNode target, String selector) {
        return new MenuItemAtlasSprite(normalSprite, selectedSprite, disabledSprite, target, selector);
    }

    protected MenuItemAtlasSprite(AtlasSprite normalSprite, AtlasSprite selectedSprite, AtlasSprite disabledSprite,
                               CocosNode target, String selector) {
        super(normalSprite, selectedSprite, disabledSprite, target, selector);

        normalImage_.setVisible(true);
        selectedImage_.setVisible(false);
        disabledImage_.setVisible(false);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        normalImage_.setPosition(x, y);
        selectedImage_.setPosition(x, y);
        disabledImage_.setPosition(x, y);
    }

    @Override
    public void setRotation(float angle) {
        super.setRotation(angle);
        normalImage_.setRotation(angle);
        selectedImage_.setRotation(angle);
        disabledImage_.setRotation(angle);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        normalImage_.setScale(scale);
        selectedImage_.setScale(scale);
        disabledImage_.setScale(scale);
    }

    @Override
    public void selected() {
        if (isEnabled_) {
            super.selected();
            normalImage_.setVisible(false);
            selectedImage_.setVisible(true);
            disabledImage_.setVisible(false);
        }
    }

    @Override
    public void unselected() {
        if (isEnabled_) {
            super.unselected();
            normalImage_.setVisible(true);
            selectedImage_.setVisible(false);
            disabledImage_.setVisible(false);
        }
    }

    @Override
    public void setIsEnabled(boolean enabled) {
        super.setIsEnabled(enabled);
        if (enabled) {
            normalImage_.setVisible(true);
            selectedImage_.setVisible(false);
            disabledImage_.setVisible(false);

        } else {
            normalImage_.setVisible(false);
            selectedImage_.setVisible(false);
            if (disabledImage_ != null)
                disabledImage_.setVisible(true);
            else
                normalImage_.setVisible(true);
        }
    }

    @Override
    public void draw(GL10 gl) {
        // override parent draw
        // since AtlasSpriteManager is the one that draws all the AtlasSprite objects
    }

}
