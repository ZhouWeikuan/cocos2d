package org.cocos2d.protocols;

import org.cocos2d.opengl.CCTexture2D;

/** CCNode objects that uses a Texture2D to render the images.
 The texture can have a blending function.
 If the texture has alpha premultiplied the default blending function is:
    src=GL_ONE dst= GL_ONE_MINUS_SRC_ALPHA
 else
	src=GL_SRC_ALPHA dst= GL_ONE_MINUS_SRC_ALPHA
 But you can change the blending funtion at any time.
 @since v0.8.0
 */

public interface CCTextureProtocol extends CCBlendProtocol {
    /** returns the used texture */
    public CCTexture2D getTexture();

    /** sets a new texture. it will be retained */
    public void setTexture(CCTexture2D texture);

}

