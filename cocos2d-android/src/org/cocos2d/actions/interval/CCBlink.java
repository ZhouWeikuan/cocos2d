package org.cocos2d.actions.interval;

/** Blinks a CCNode object by modifying it's visible attribute
*/
public class CCBlink extends CCIntervalAction {
    private int times;

    /** creates the action */
    public static CCBlink action(float t, int b) {
        return new CCBlink(t, b);
    }

    /** initilizes the action */
    protected CCBlink(float t, int b) {
        super(t);
        times = b;
    }

    @Override
    public CCBlink copy() {
        return new CCBlink(duration, times);
    }

    @Override
    public void update(float t) {
        float slice = 1.0f / times;
        float m = t % slice;
        target.setVisible(m > slice / 2 ? true : false);
    }

    @Override
    public CCBlink reverse() {
        return new CCBlink(duration, times);
    }
}

