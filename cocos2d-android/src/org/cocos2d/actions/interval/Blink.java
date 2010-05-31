package org.cocos2d.actions.interval;

//
// Blink
//

public class Blink extends IntervalAction {
    private int times;

    public static Blink action(float t, int b) {
        return new Blink(t, b);
    }

    protected Blink(float t, int b) {
        super(t);
        times = b;
    }

    @Override
    public IntervalAction copy() {
        return new Blink(duration, times);
    }

    @Override
    public void update(float t) {
        float slice = 1.0f / times;
        float m = t % slice;
        target.setVisible(m > slice / 2 ? true : false);
    }

    @Override
    public IntervalAction reverse() {
        return new Blink(duration, times);
    }
}
