package org.cocos2d.actions.interval;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cocos2d.nodes.CCNode;


/** CCPropertyAction
 
 CCPropertyAction is an action that lets you update any property of an object.
 For example, if you want to modify the "width" property of a target from 200 to 300 in 2 senconds, then:
 
	CCIntervalAction modifyWidth = CCPropertyAction.action(2.0f, "setWidth", 200.0f, 300.0f);
	target.runAction(modifyWidth);
 

 Another example: CCScaleTo action could be rewriten using CCPropertyAction:
 
	// scaleA and scaleB are equivalents
	id scaleA = [CCScaleTo actionWithDuration:2 scale:3];
	id scaleB = [CCPropertyAction actionWithDuration:2 key:@"scale" from:1 to:3];

 
 @since v0.99.2
 */
public class CCPropertyAction extends CCIntervalAction {

    Method          setMethod_;   // setScale, like this
    String          key_;
    float			from_, to_;
    float			delta_;

    /** creates an initializes the action with the property name (key), and the from and to parameters. */
    public static CCPropertyAction action(float aDuration, String key, float from, float to) {
        return new CCPropertyAction(aDuration, key, from, to);
    }

    /** initializes the action with the property name (key), and the from and to parameters. */
    protected CCPropertyAction(float aDuration, String key, float from, float to) {
        super(aDuration);
        key_    = key;

        setMethod_ = null;
		
		
        to_		= to;
        from_	= from;
    }

	@Override
	public CCPropertyAction copy() {
		return new CCPropertyAction(duration, key_, from_, to_);
	}

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        delta_ = to_ - from_;
    }

    @Override
    public void update(float dt) {
        try {
        	if (setMethod_ == null) {        		
        		setMethod_    = target.getClass().getMethod(key_, new Class[] {Float.TYPE});        		
        	}
        	setMethod_.invoke(target, new Object[] {
        			to_  - delta_ * (1 - dt)
        	});
    	} catch (NoSuchMethodException e) {
    		e.printStackTrace();
    	} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }

    @Override
    public CCPropertyAction reverse() {
        return new CCPropertyAction(duration, key_, to_, from_);
    }

}

