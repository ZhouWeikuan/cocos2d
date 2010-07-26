package org.cocos2d.menus;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCLabelAtlas;

public class MenuItemAtlasFont extends MenuItemLabel {

    public static MenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap) {
        assert value.length() != 0 :"value length must be greater than 0";

        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new MenuItemAtlasFont(label, null, null);
    }

    public static MenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap, CCNode rec, String cb) {
        assert value.length() != 0 :"value length must be greater than 0";

        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new MenuItemAtlasFont(label, rec, cb);
    }

    protected MenuItemAtlasFont(CCLabelAtlas label, CCNode rec, String cb) {
        super(label, rec, cb);
    }

}
