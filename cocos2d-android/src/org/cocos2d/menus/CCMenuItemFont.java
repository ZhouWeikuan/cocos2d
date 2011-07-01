package org.cocos2d.menus;

import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;

/** A CCMenuItemFont
 Helper class that creates a CCMenuItemLabel class with a Label
 */
public class CCMenuItemFont extends CCMenuItemLabel {
//    private CCLabel label_;

    static int _fontSize = kItemSize;
    static String _fontName = "DroidSans";

    /** set font size */
    public static void setFontSize(int s) {
        _fontSize = s;
    }

    /** get font size */
    public static int fontSize() {
        return _fontSize;
    }

    /** set the font name */
    public static void setFontName(String n) {
        _fontName = n;
    }

    /** get the font name */
    public static String fontName() {
        return _fontName;
    }

    /** creates a menu item from a string without target/selector.
     * To be used with CCMenuItemToggle */
    public static CCMenuItemFont item(String value) {
        return new CCMenuItemFont(CCLabel.makeLabel(value, _fontName, _fontSize), null, null);
    }

    /** creates a menu item from a string with a target/selector */
    public static CCMenuItemFont item(String value, CCNode rec, String cb) {
        CCLabel lbl = CCLabel.makeLabel(value, _fontName, _fontSize);
        return new CCMenuItemFont(lbl, rec, cb);
    }

    /** initializes a menu item from a string with a target/selector */
    protected CCMenuItemFont(CCLabel label, CCNode rec, String cb) {
        super(label, rec, cb);
    }
}

