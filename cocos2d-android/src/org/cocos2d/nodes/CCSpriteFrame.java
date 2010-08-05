package org.cocos2d.nodes;

import org.cocos2d.types.CGRect;
import org.cocos2d.utils.CCFormatter;

public class CCSpriteFrame {
    public CGRect rect;

    public CCSpriteFrame(CGRect frame) {
        rect = frame;
    }

    public String toString() {
        return new CCFormatter().format("<%s = %08X | Rect = (%.2f,%.2f,%.2f,%.2f)>",
                CCSpriteFrame.class.getSimpleName(), this,
                rect.origin.x,
                rect.origin.y,
                rect.size.width,
                rect.size.height);
    }

}
