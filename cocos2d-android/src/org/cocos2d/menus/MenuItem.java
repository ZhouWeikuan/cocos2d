package org.cocos2d.menus;

import org.cocos2d.nodes.CocosNode;
import org.cocos2d.types.CCRect;

import java.lang.reflect.Method;

public abstract class MenuItem extends CocosNode {
    public static final int kItemSize = 32;

    static int _fontSize = kItemSize;
    static String fontName = "DroidSans";

    public static final int kCurrentItem = 0xc0c05001;

    public static final int kZoomActionTag = 0xc0c05002;

    protected boolean isEnabled_;
    protected boolean isSelected_;

    protected Object targetCallback;
    protected String selector;

    private Method invocation;

    /**
     * Initializes a menu item with a target/selector
     */
    protected MenuItem(Object rec, String cb) {
        targetCallback = rec;
        selector = cb;

        setAnchorPoint(0.5f, 0.5f);

        invocation = null;
        if (rec != null && cb != null)
            try {
                Class<?> cls = rec.getClass();
                invocation = cls.getMethod(cb);
            } catch (Exception e) {
                // Do nothing
            }

        isEnabled_ = true;
        isSelected_ = false;
    }

    /**
     * Activate the item
     */
    public void activate() {
        if (isEnabled_) {
            if (targetCallback != null & invocation != null) {
                try {
                    invocation.invoke(targetCallback);
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * The item was selected (not activated), similar to "mouse-over"
     */
    public void selected() {
        isSelected_ = true;
    }

    /**
     * The item was unselected
     */
    public void unselected() {
        isSelected_ = false;
    }

    /**
     * Enable or disabled the MenuItem
     */
    public void setIsEnabled(boolean enabled) {
        isEnabled_ = enabled;
    }


    /**
     * Returns whether or not the MenuItem is enabled
     */
    public boolean isEnabled() {
        return isEnabled_;
    }

    /**
     * Returns the outside box
     */
    public CCRect rect() {
        return CCRect.make(getPositionX() - getWidth() * getAnchorPointX(), getPositionY() -
                getHeight() * getAnchorPointY(),
                getWidth(), getHeight());
    }


}