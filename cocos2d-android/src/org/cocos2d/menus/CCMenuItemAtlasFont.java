package org.cocos2d.menus;

import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCNode;

/** A CCMenuItemAtlasFont
 Helper class that creates a MenuItemLabel class with a LabelAtlas
 */
public class CCMenuItemAtlasFont extends CCMenuItemLabel {

    /** creates a menu item from a string and atlas with a target/selector */
    public static CCMenuItemAtlasFont item(CharSequence value, String charMapFile, int itemWidth, int itemHeight, char startCharMap) {
        assert value.length() != 0 :"value length must be greater than 0";

        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new CCMenuItemAtlasFont(label, null, null);
    }

    /** creates a menu item from a string and atlas. Use it with MenuItemToggle */
    public static CCMenuItemAtlasFont item(CharSequence value, String charMapFile, int itemWidth, int itemHeight, char startCharMap, CCNode rec, String cb) {
        assert value.length() != 0 :"value length must be greater than 0";

        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new CCMenuItemAtlasFont(label, rec, cb);
    }

    /** initializes a menu item from a string and atlas with a target/selector */
    protected CCMenuItemAtlasFont(CCLabelAtlas label, CCNode rec, String cb) {
        super(label, rec, cb);
    }
}

