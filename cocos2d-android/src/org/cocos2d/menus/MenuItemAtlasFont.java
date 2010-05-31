package org.cocos2d.menus;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.LabelAtlas;

public class MenuItemAtlasFont extends MenuItemLabel {

    public static MenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap) {
        assert value.length() != 0 :"value length must be greater than 0";

        LabelAtlas label = LabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new MenuItemAtlasFont(label, null, null);
    }

    public static MenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap, CocosNode rec, String cb) {
        assert value.length() != 0 :"value length must be greater than 0";

        LabelAtlas label = LabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
    	return new MenuItemAtlasFont(label, rec, cb);
    }

    protected MenuItemAtlasFont(LabelAtlas label, CocosNode rec, String cb) {
        super(label, rec, cb);
    }

}
