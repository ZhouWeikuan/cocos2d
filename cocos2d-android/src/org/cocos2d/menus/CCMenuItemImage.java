package org.cocos2d.menus;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;


/** CCMenuItemImage accepts images as items.
 The images has 3 different states:
 - unselected image
 - selected image
 - disabled image
 
 For best results try that all images are of the same size
 */
public class CCMenuItemImage extends CCMenuItemSprite {

    /** creates a menu item with a normal and selected image*/
    public static CCMenuItemImage item(String value, String value2) {
        return item(value, value2, null, null, null);
    }

    /** creates a menu item with a normal and selected image with target/selector */
    public static CCMenuItemImage item(String value, String value2, CCNode t, String s) {
        return item(value, value2, null, t, s);
    }

    public static CCMenuItemImage item(String value, String value2, String value3) {
        return item(value, value2, value3, null, null);
    }

    /** creates a menu item with a normal,selected  and disabled image with target/selector */
    public static CCMenuItemImage item(String normalI, String selectedI, String disabledI,
            CCNode t, String sel) {
        return new CCMenuItemImage(CCSprite.sprite(normalI), CCSprite.sprite(selectedI),
                         (disabledI == null) ? null : CCSprite.sprite(disabledI), t, sel);
    }

    /** initializes a menu item with a normal, selected  and disabled image with target/selector */
    protected CCMenuItemImage(CCSprite normal, CCSprite selected, CCSprite disabled, CCNode t, String sel) {
        super(normal, selected, disabled, t, sel);
    }
}

