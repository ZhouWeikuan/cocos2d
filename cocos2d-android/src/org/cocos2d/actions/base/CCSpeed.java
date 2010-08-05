package org.cocos2d.actions.base;

import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.nodes.CCNode;


/** Changes the speed of an action, making it take longer (speed>1)
 or less (speed<1) time.
 Useful to simulate 'slow motion' or 'fast forward' effect.
 @warning This action can't be Sequenceable because it is not an IntervalAction
 */
public class CCSpeed extends CCAction {

	protected CCIntervalAction other;
    /** alter the speed of the inner function in runtime */
    protected float speed;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /** creates the action */
    public static CCSpeed action(CCIntervalAction action, float r) {
        return new CCSpeed(action, r);
    }

    /** initializes the action */
    protected CCSpeed(CCIntervalAction action, float r) {
        other = action;
        speed = r;
    }

    @Override
    public CCSpeed copy() {
        return new CCSpeed(other.copy(), speed);
    }


    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        other.start(target);
    }

    @Override
    public void stop() {
        other.stop();
        super.stop();
    }

    @Override
    public void step(float dt) {
        other.step(dt * speed);
    }

    @Override
    public boolean isDone() {
        return other.isDone();
    }

    public CCSpeed reverse() {
        return CCSpeed.action(other.reverse(), speed);
    }

	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
	}

}
