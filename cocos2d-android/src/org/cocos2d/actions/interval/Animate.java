package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CCNode;


//
// Animate
//

public class Animate extends IntervalAction {

    private CCNode.CocosAnimation animation;
    private Object origFrame;
    private boolean restoreOriginalFrame;

    public static Animate action(CCNode.CocosAnimation anim) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new Animate(anim, true);
    }

    public static Animate action(CCNode.CocosAnimation anim, boolean restore) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new Animate(anim, restore);
    }

    protected Animate(CCNode.CocosAnimation anim, boolean restore) {
        super(anim.frames().size() * anim.delay());

        restoreOriginalFrame = restore;
        animation = anim;
        origFrame = null;
    }

    @Override
    public IntervalAction copy() {
        return new Animate(animation, true);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        CCNode.CocosNodeFrames sprite = (CCNode.CocosNodeFrames) target;

        origFrame = sprite.displayFrame();
    }

    @Override
    public void stop() {
        if (restoreOriginalFrame) {
            CCNode.CocosNodeFrames sprite = (CCNode.CocosNodeFrames) target;
            sprite.setDisplayFrame(origFrame);
        }

        super.stop();
    }

    @Override
    public void update(float t) {
        int idx = 0;

        float slice = 1.0f / animation.frames().size();

        if (t != 0)
            idx = (int) (t / slice);

        if (idx >= animation.frames().size()) {
            idx = animation.frames().size() - 1;
        }

        CCNode.CocosNodeFrames sprite = (CCNode.CocosNodeFrames) target;
        if (!sprite.isFrameDisplayed(animation.frames().get(idx))) {
            sprite.setDisplayFrame(animation.frames().get(idx));
        }
    }
}
