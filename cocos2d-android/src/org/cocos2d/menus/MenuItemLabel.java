package org.cocos2d.menus;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.interval.ScaleTo;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Label;
import org.cocos2d.types.CCColor3B;

public class MenuItemLabel extends MenuItem implements CocosNode.CocosNodeRGBA {
    private CocosNodeLabel label_;
    private CCColor3B colorBackup;
    private CCColor3B disabledColor_;

    public static MenuItemLabel item(CocosNodeLabel label, CocosNode target, String selector) {
        return new MenuItemLabel(label, target, selector);
    }
    
    public static MenuItemLabel item(String text, CocosNode target, String selector) {
    	Label lbl = Label.label(text, "DroidSansMono", 30);
        return new MenuItemLabel(lbl, target, selector);
    }

    protected MenuItemLabel(CocosNodeLabel label, CocosNode target, String selector) {
        super(target, selector);
        setLabel(label);
        colorBackup = new CCColor3B(255, 255, 255);
        disabledColor_ = new CCColor3B(126, 126, 126);
    }

    public void setOpacity(int opacity) {
        ((CocosNodeRGBA) label_).setOpacity(opacity);
    }

    public int getOpacity() {
        return ((CocosNodeRGBA) label_).getOpacity();
    }

    public void setColor(CCColor3B color) {
        ((CocosNodeRGBA) label_).setColor(color);
    }

    public CCColor3B getColor() {
        return ((CocosNodeRGBA) label_).getColor();
    }

    public CCColor3B getDisabledColor() {
        return new CCColor3B(disabledColor_.r, disabledColor_.g, disabledColor_.b);
    }

    public void setDisabledColor(CCColor3B color) {
        disabledColor_.r = color.r;
        disabledColor_.g = color.g;
        disabledColor_.b = color.b;
    }

    public CocosNodeLabel getLabel() {
        return label_;
    }

    public void setLabel(CocosNodeLabel label) {
        label_ = label;
        setContentSize(((CocosNodeSize) label_).getWidth(), ((CocosNodeSize) label_).getHeight());
    }

    public void setString(String string) {
        label_.setString(string);
        setContentSize(((CocosNodeSize) label_).getWidth(), ((CocosNodeSize) label_).getHeight());
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
        ((CocosNode)label_).draw(gl);
    }


}
