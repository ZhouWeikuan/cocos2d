package org.cocos2d.menus;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import javax.microedition.khronos.opengles.GL10;

/**
 * MenuItemSprite accepts CocosNode<CocosNodeRGBA> objects as items.
 * The images has 3 different states:
 * - unselected image
 * - selected image
 * - disabled image
 *
 * @since v0.8.0
 */

public class CCMenuItemSprite extends CCMenuItem implements CCRGBAProtocol {
    /** the image used when the item is not selected */
    protected CCNode normalImage_;
    /** the image used when the item is selected */
    protected CCNode selectedImage_;
    /** the image used when the item is disabled */
    protected CCNode disabledImage_;

    /** creates a menu item with a normal and selected image*/
    public static CCMenuItemSprite item(CCNode normalSprite, CCNode selectedSprite) {
        return new CCMenuItemSprite(normalSprite, selectedSprite, null, null, null);
    }

    /** creates a menu item with a normal and selected image with target/selector */
    public static CCMenuItemSprite item(CCNode normalSprite, CCNode selectedSprite, CCNode target, String selector) {
        return new CCMenuItemSprite(normalSprite, selectedSprite, null, target, selector);
    }

    /** creates a menu item with a normal,selected  and disabled image with target/selector */
    public static CCMenuItemSprite item(CCNode normalSprite, CCNode selectedSprite, CCNode disabledSprite, CCNode target, String selector) {
        return new CCMenuItemSprite(normalSprite, selectedSprite, disabledSprite, target, selector);
    }

    /** initializes a menu item with a normal, selected  and disabled image with target/selector */
    protected CCMenuItemSprite(CCNode normalSprite, CCNode selectedSprite, CCNode disabledSprite, CCNode target, String selector) {
        super(target, selector);
        normalImage_ = normalSprite;
        selectedImage_ = selectedSprite;
        disabledImage_ = disabledSprite;
        CGSize size = normalImage_.getContentSize();
        setContentSize(size);
    }

    @Override
    public void draw(GL10 gl) {
        if (isEnabled_) {
            if (isSelected_)
                selectedImage_.draw(gl);
            else
                normalImage_.draw(gl);

        } else {
            if (disabledImage_ != null)
                disabledImage_.draw(gl);

                // disabled image was not provided
            else
                normalImage_.draw(gl);
        }
    }

    // CocosNodeRGBA protocol
    public void setOpacity(int opacity) {
        ((CCRGBAProtocol) normalImage_).setOpacity(opacity);
        ((CCRGBAProtocol) selectedImage_).setOpacity(opacity);
        if (disabledImage_ != null)
            ((CCRGBAProtocol) disabledImage_).setOpacity(opacity);
    }

    public void setColor(ccColor3B color) {
        ((CCRGBAProtocol) normalImage_).setColor(color);
        ((CCRGBAProtocol) selectedImage_).setColor(color);
        if (disabledImage_ != null)
            ((CCRGBAProtocol) disabledImage_).setColor(color);
    }

    public ccColor3B getColor() {
        return ((CCRGBAProtocol) normalImage_).getColor();
    }

    public int getOpacity() {
        return ((CCRGBAProtocol) normalImage_).getOpacity();
    }

	@Override
	public boolean doesOpacityModifyRGB() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOpacityModifyRGB(boolean b) {
		// TODO Auto-generated method stub
		
	}
}

