package org.cocos2d.menus;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCLabel;

public class MenuItemFont extends MenuItemLabel {
    private CCLabel label_;

    static int _fontSize = kItemSize;
    static String _fontName = "DroidSans";

    public static void setFontSize(int s) {
        _fontSize = s;
    }

    public static int fontSize() {
        return _fontSize;
    }

    public static void setFontName(String n) {
        _fontName = n;
    }

    public static String fontName() {
        return _fontName;
    }

    public static MenuItemFont item(String value) {
        return new MenuItemFont(CCLabel.makeLabel(value, _fontName, _fontSize), null, null);
    }

    public static MenuItemFont item(String value, CCNode rec, String cb) {
        CCLabel lbl = CCLabel.makeLabel(value, _fontName, _fontSize);
        return new MenuItemFont(lbl, rec, cb);
    }

    protected MenuItemFont(CCLabel label, CCNode rec, String cb) {
        super(label, rec, cb);
    }

}

