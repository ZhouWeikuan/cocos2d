package org.cocos2d.menus;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.interval.ScaleTo;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

public class MenuItemLabel extends MenuItem implements CCNode.CocosNodeRGBA {
    private CCLabelProtocol label_;
    private ccColor3B colorBackup;
    private ccColor3B disabledColor_;

    public static MenuItemLabel item(CCLabelProtocol label, CCNode target, String selector) {
        return new MenuItemLabel(label, target, selector);
    }
    
    public static MenuItemLabel item(String text, CCNode target, String selector) {
    	CCLabel lbl = CCLabel.makeLabel(text, "DroidSansMono", 30);
        return new MenuItemLabel(lbl, target, selector);
    }

    protected MenuItemLabel(CCLabelProtocol label, CCNode target, String selector) {
        super(target, selector);
        setLabel(label);
        colorBackup = new ccColor3B(255, 255, 255);
        disabledColor_ = new ccColor3B(126, 126, 126);
    }

    public void setOpacity(int opacity) {
        ((CocosNodeRGBA) label_).setOpacity(opacity);
    }

    public int getOpacity() {
        return ((CocosNodeRGBA) label_).getOpacity();
    }

    public void setColor(ccColor3B color) {
        ((CocosNodeRGBA) label_).setColor(color);
    }

    public ccColor3B getColor() {
        return ((CocosNodeRGBA) label_).getColor();
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
        setContentSize(CGSize.make(((CocosNodeSize) label_).getWidth(), ((CocosNodeSize) label_).getHeight()));
    }

    public void setString(String string) {
        label_.setString(string);
        setContentSize(CGSize.make(((CocosNodeSize) label_).getWidth(), ((CocosNodeSize) label_).getHeight()));
    }

    public void activate() {
        if (isEnabled_) {
            stopAllActions();

            setScale(1.0f);

            super.activate();
        }
    }

    public void selected() {
        // subclass to change the default action
        if (isEnabled_) {
            super.selected();

            stopAction(kZoomActionTag);
            Action zoomAction = ScaleTo.action(0.1f, 1.2f);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    public void unselected() {
        // subclass to change the default action
        if (isEnabled_) {
            super.unselected();

            stopAction(kZoomActionTag);
            Action zoomAction = ScaleTo.action(0.1f, 1.0f);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    public void setIsEnabled(boolean enabled) {
        if (isEnabled_ != enabled) {
            if (!enabled) {
                colorBackup = ((CocosNodeRGBA) label_).getColor();
                ((CocosNodeRGBA) label_).setColor(disabledColor_);
            } else
                ((CocosNodeRGBA) label_).setColor(colorBackup);
        }

        super.setIsEnabled(enabled);
    }

    public void draw(GL10 gl) {
        ((CCNode)label_).draw(gl);
    }


}
