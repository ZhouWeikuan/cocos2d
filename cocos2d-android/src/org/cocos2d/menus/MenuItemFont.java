package org.cocos2d.menus;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Label;

public class MenuItemFont extends MenuItemLabel {
    private Label label_;

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
        return new MenuItemFont(Label.label(value, _fontName, _fontSize), null, null);
    }

    public static MenuItemFont item(String value, CocosNode rec, String cb) {
        Label lbl = Label.label(value, _fontName, _fontSize);
        return new MenuItemFont(lbl, rec, cb);
    }

    protected MenuItemFont(Label label, CocosNode rec, String cb) {
        super(label, rec, cb);
    }

}

