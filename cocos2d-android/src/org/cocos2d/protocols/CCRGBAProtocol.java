package org.cocos2d.protocols;

import org.cocos2d.types.ccColor3B;


/// CC RGBA protocol
public interface CCRGBAProtocol {
    /** sets Color
     @since v0.8
     */
    public void setColor(ccColor3B color);

    /** returns the color
     @since v0.8
     */
    public ccColor3B getColor();

    /// returns the opacity
    public int getOpacity();

    /** sets the opacity.
     @warning If the the texture has premultiplied alpha then, the R, G and B channels will be modifed.
     Values goes from 0 to 255, where 255 means fully opaque.
     */
    public void setOpacity(int opacity);

    /** sets the premultipliedAlphaOpacity property.
     If set to NO then opacity will be applied as: glColor(R,G,B,opacity);
     If set to YES then oapcity will be applied as: glColor(opacity, opacity, opacity, opacity );
     Textures with premultiplied alpha will have this property by default on YES.
        Otherwise the default value is NO
     @since v0.8
     */
    public void setOpacityModifyRGB(boolean b);

    /** returns whether or not the opacity will be applied using glColor(R,G,B,opacity)
        or glColor(opacity, opacity, opacity, opacity);
     @since v0.8
     */
    public boolean doesOpacityModifyRGB();
}

