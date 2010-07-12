package org.cocos2d.nodes;

import org.cocos2d.types.CGRect;
import org.cocos2d.utils.CCFormatter;

public class AtlasSpriteFrame {
    public CGRect rect;

    public AtlasSpriteFrame(CGRect frame) {
        rect = frame;
    }

    public String toString() {
        return new CCFormatter().format("<%s = %08X | Rect = (%.2f,%.2f,%.2f,%.2f)>",
                AtlasSpriteFrame.class.getSimpleName(), this,
                rect.origin.x,
                rect.origin.y,
                rect.size.width,
                rect.size.height);
    }

}
