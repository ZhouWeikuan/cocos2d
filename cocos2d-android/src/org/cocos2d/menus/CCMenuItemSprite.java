package org.cocos2d.menus;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

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
    public void setNormalImage(CCNode image) {
        assert(image!=null):"Cann't set normalImage_ to be null!";
        if (image != normalImage_ ) {
            image.setAnchorPoint(0,0);
            image.setVisible(true);

            this.removeChild(normalImage_,  true);
            this.addChild(image);

            normalImage_ = image;
        }

    }
    public CCNode getNormalImage() {
        return normalImage_;
    }

    /** the image used when the item is selected */
    protected CCNode selectedImage_;
    public void setSelectedImage(CCNode image) {
        assert(image!=null):"Cann't set selectedImage_ to be null!";
        if( image != selectedImage_ ) {
            image.setAnchorPoint(0,0);
            image.setVisible(false);

            removeChild(selectedImage_, true);
            addChild(image);

            selectedImage_ = image;
        }

    }
    public CCNode getSelectedImage() {
        return selectedImage_;
    }

    /** the image used when the item is disabled */
    protected CCNode disabledImage_;
    public void setDisabledImage(CCNode image) {
        if( image != disabledImage_ ) {
            if (disabledImage_ != null) {
                removeChild(disabledImage_, true);
            }
            if (image != null) {
                image.setAnchorPoint(0,0);
                image.setVisible(false);
                addChild(image);
            }
            disabledImage_ = image;
        }

    }
    public CCNode getDisabledImage(){
        return disabledImage_;
    }

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
        setNormalImage(normalSprite);
        setSelectedImage(selectedSprite);
        setDisabledImage(disabledSprite);
        CGSize size = normalImage_.getContentSize();
        setContentSize(size);
    }

    @Override
    public void draw(GL10 gl) {
        if (isEnabled_) {
            if (isSelected_) {
                selectedImage_.draw(gl);
            } else {
                normalImage_.draw(gl);
            }

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
    
    @Override
    public void selected() {
        super.selected();

        if (selectedImage_ != null) {
            normalImage_.setVisible(false);
            selectedImage_.setVisible(true);
            if (disabledImage_ != null)
                disabledImage_.setVisible(false);
        } else { // there is not selected image
            normalImage_.setVisible(true);
            selectedImage_.setVisible(false);
            if (disabledImage_ != null)
                disabledImage_.setVisible(false);
        }
    }

    @Override
    public void unselected() {
        super.unselected();
        normalImage_.setVisible(true);
        selectedImage_.setVisible(false);
        if (disabledImage_ != null)
            disabledImage_.setVisible(false);
    }

    @Override
    public void setIsEnabled(boolean enabled) {
        super.setIsEnabled(enabled);

        if (enabled) {
            normalImage_.setVisible(true);
            selectedImage_.setVisible(false);
            if( disabledImage_ != null ) {
            	disabledImage_.setVisible(false);
            }
        } else {
            if( disabledImage_ != null) {
                normalImage_.setVisible(false);
                selectedImage_.setVisible(false);
                disabledImage_.setVisible(true);
            } else {
                normalImage_.setVisible(true);
                selectedImage_.setVisible(false);
//                if (disabledImage_ != null)
//                    disabledImage_.setVisible(false);
            }
        }
    }

}

