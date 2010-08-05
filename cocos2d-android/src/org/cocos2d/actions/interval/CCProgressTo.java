package org.cocos2d.actions.interval;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.nodes.CCNode;

/**
 Progress to percentage
@since v0.99.1
*/
public class CCProgressTo extends CCIntervalAction 
{
	float to_;
	float from_;

    /** Creates and initializes with a duration and a percent */
    public static CCProgressTo action(float duration, float percent) {
        return new CCProgressTo(duration, percent);
    }

    /** Initializes with a duration and a percent */
    protected CCProgressTo(float duration, float percent) {
        super(duration);
        to_ = percent;
    }

    @Override
    public CCProgressTo copy() {
        return new CCProgressTo(duration, to_);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        from_ = ((CCProgressTimer)target).getPercentage();

        // XXX: Is this correct ?
        // Adding it to support CCRepeat
        if( from_ == 100)
            from_ = 0;
    }

    @Override
    public void update(float t) {
        ((CCProgressTimer)target).setPercentage(from_ + ( to_ - from_ ) * t);
    }

}

