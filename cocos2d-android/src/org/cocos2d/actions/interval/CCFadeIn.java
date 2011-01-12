package org.cocos2d.actions.interval;

import org.cocos2d.protocols.CCRGBAProtocol;

/** Fades In an object that implements the CCRGBAProtocol protocol.
 * It modifies the opacity from 0 to 255.
 *  The "reverse" of this action is FadeOut
 */
public class CCFadeIn extends CCIntervalAction {

    public static CCFadeIn action(float t) {
        return new CCFadeIn(t);
    }

    protected CCFadeIn(float t) {
        super(t);
    }

	@Override
	public CCFadeIn copy() {
		return new CCFadeIn(duration);
	}

    @Override
    public void update(float t) {
        ((CCRGBAProtocol) target).setOpacity((int) (255.0f * t));
    }

    @Override
    public CCFadeOut reverse() {
        return new CCFadeOut(duration);
    }
}

