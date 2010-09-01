package org.cocos2d.menus;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.ccColor3B;


/** An abstract class for "label" CCMenuItemLabel items 
 Any CCNode that supports the CCLabelProtocol protocol can be added.
 Supported nodes:
   - CCBitmapFontAtlas
   - CCLabelAtlas
   - CCLabel
 */

public class CCMenuItemLabel extends CCMenuItem implements CCRGBAProtocol {
    /** Label that is rendered. It can be any CCNode that implements the CCLabelProtocol */
    private CCLabelProtocol label_;
    private ccColor3B colorBackup;

    /** the color that will be used to disable the item */
    private ccColor3B disabledColor_;
	private float originalScale_;

    /** creates a CCMenuItemLabel with a Label, target and selector */
    public static CCMenuItemLabel item(CCLabelProtocol label, CCNode target, String selector) {
        return new CCMenuItemLabel(label, target, selector);
    }
    
    public static CCMenuItemLabel item(String text, CCNode target, String selector) {
    	CCLabel lbl = CCLabel.makeLabel(text, "DroidSansMono", 30);
        return new CCMenuItemLabel(lbl, target, selector);
    }

    /** initializes a CCMenuItemLabel with a Label, target and selector */
    protected CCMenuItemLabel(CCLabelProtocol label, CCNode target, String selector) {
        super(target, selector);
        originalScale_ = 1.0f;
        setLabel(label);
        colorBackup = new ccColor3B(255, 255, 255);
        disabledColor_ = new ccColor3B(126, 126, 126);
    }

    public void setOpacity(int opacity) {
        ((CCRGBAProtocol) label_).setOpacity(opacity);
    }

    public int getOpacity() {
        return ((CCRGBAProtocol) label_).getOpacity();
    }

    public void setColor(ccColor3B color) {
        ((CCRGBAProtocol) label_).setColor(color);
    }

    public ccColor3B getColor() {
        return ((CCRGBAProtocol) label_).getColor();
    }

    public ccColor3B getDisabledColor() {
        return new ccColor3B(disabledColor_.r, disabledColor_.g, disabledColor_.b);
    }

    public void setDisabledColor(ccColor3B color) {
        disabledColor_.r = color.r;
        disabledColor_.g = color.g;
        disabledColor_.b = color.b;
    }

    public CCLabelProtocol getLabel() {
        return label_;
    }

    public void setLabel(CCLabelProtocol label) {
        label_ = label;
        setContentSize(((CCNode)label_).getContentSize());
    }

    /** sets a new string to the inner label */
    public void setString(String string) {
        label_.setString(string);
        setContentSize(((CCNode)label_).getContentSize());
    }

    public void activate() {
        if (isEnabled_) {
            stopAllActions();

            setScale(originalScale_);

            super.activate();
        }
    }

    public void selected() {
        // subclass to change the default action
        if (isEnabled_) {
            super.selected();

            stopAction(kZoomActionTag);
            originalScale_ = getScale();
            CCAction zoomAction = CCScaleTo.action(0.1f, 1.2f * originalScale_);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    public void unselected() {
        // subclass to change the default action
        if (isEnabled_) {
            super.unselected();

            stopAction(kZoomActionTag);
            CCAction zoomAction = CCScaleTo.action(0.1f, originalScale_);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    /** Enable or disabled the CCMenuItemFont
     @warning setIsEnabled changes the RGB color of the font
    */
    public void setIsEnabled(boolean enabled) {
        if (isEnabled_ != enabled) {
            if (!enabled) {
                colorBackup = ((CCRGBAProtocol) label_).getColor();
                ((CCRGBAProtocol) label_).setColor(disabledColor_);
            } else {
                ((CCRGBAProtocol) label_).setColor(colorBackup);
            }
        }

        super.setIsEnabled(enabled);
    }

    public void draw(GL10 gl) {
        ((CCNode)label_).draw(gl);
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

