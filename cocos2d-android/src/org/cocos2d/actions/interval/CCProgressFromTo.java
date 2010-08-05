package org.cocos2d.actions.interval;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.nodes.CCNode;

/**
 Progress from a percentage to another percentage
 @since v0.99.1
 */
public class CCProgressFromTo extends CCIntervalAction {
	float to_;
	float from_;

    /** Creates and initializes the action with a duration, a "from" percentage and a "to" percentage */
    public static CCProgressFromTo action(float t, float fromPercentage, float toPercentage) {
        return new CCProgressFromTo(t, fromPercentage, toPercentage);
    }

    /** Initializes the action with a duration, a "from" percentage and a "to" percentage */
    protected CCProgressFromTo(float t, float fromPercentage, float toPercentage) {
        super(t);
        to_ = toPercentage;
        from_ = fromPercentage;
    }

    @Override
    public CCProgressFromTo copy() {
        return new CCProgressFromTo(duration, from_, to_);
    }

    @Override
    public CCProgressFromTo reverse() {
        return new CCProgressFromTo(duration, to_, from_);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
    }

    @Override
    public void update(float t) {
        ((CCProgressTimer)target).setPercentage(from_ + ( to_ - from_ ) * t);
    }
}

