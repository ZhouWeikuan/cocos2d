package org.cocos2d.actions.instant;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

/** Flips the sprite horizontally
 @since v0.99.0
 */
public class CCFlipX extends CCInstantAction {
	boolean  flipX;

    public static CCFlipX action(boolean fx) {
        return new CCFlipX(fx);
    }

    public CCFlipX(boolean fx) {
        super();
        flipX = fx;
    }

    @Override
    public CCFlipX copy() {
        CCFlipX copy = new CCFlipX(flipX);
        return copy;
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        CCSprite sprite = (CCSprite)aTarget;
        sprite.setFlipX(flipX);
    }

    @Override
    public CCFlipX reverse() {
        return CCFlipX.action(!flipX);
    }
}

