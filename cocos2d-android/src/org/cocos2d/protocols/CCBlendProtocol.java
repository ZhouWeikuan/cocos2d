package org.cocos2d.protocols;

import org.cocos2d.types.ccBlendFunc;

/**
 You can specify the blending fuction.
 @since v0.99.0
 */
public interface CCBlendProtocol {
    /** set the source blending function for the texture */
    public void setBlendFunc(ccBlendFunc blendFunc);

    /** returns the blending function used for the texture */
    public ccBlendFunc getBlendFunc();
}
