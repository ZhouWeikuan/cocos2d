package org.cocos2d.menus;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Sprite;

public class MenuItemImage extends MenuItemSprite {

    public static MenuItemImage item(String value, String value2) {
        return item(value, value2, null, null, null);
    }

    public static MenuItemImage item(String value, String value2, CocosNode t, String s) {
        return item(value, value2, null, t, s);
    }

    public static MenuItemImage item(String value, String value2, String value3) {
        return item(value, value2, value3, null, null);
    }

    public static MenuItemImage item(String normalI, String selectedI, String disabledI, CocosNode t, String sel) {
        return new MenuItemImage(Sprite.sprite(normalI), Sprite.sprite(selectedI), (disabledI == null) ? null : Sprite.sprite(disabledI), t, sel);
    }

    protected MenuItemImage(Sprite normal, Sprite selected, Sprite disabled, CocosNode t, String sel) {
        super(normal, selected, disabled, t, sel);
    }

}
