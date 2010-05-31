package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;


//
// Animate
//

public class Animate extends IntervalAction {

    private CocosNode.CocosAnimation animation;
    private Object origFrame;
    private boolean restoreOriginalFrame;

    public static Animate action(CocosNode.CocosAnimation anim) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new Animate(anim, true);
    }

    public static Animate action(CocosNode.CocosAnimation anim, boolean restore) {
        assert anim != null : "Animate: argument Animation must be non-null";
        return new Animate(anim, restore);
    }

    protected Animate(CocosNode.CocosAnimation anim, boolean restore) {
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
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        CocosNode.CocosNodeFrames sprite = (CocosNode.CocosNodeFrames) target;

        origFrame = sprite.displayFrame();
    }

    @Override
    public void stop() {
        if (restoreOriginalFrame) {
            CocosNode.CocosNodeFrames sprite = (CocosNode.CocosNodeFrames) target;
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

        CocosNode.CocosNodeFrames sprite = (CocosNode.CocosNodeFrames) target;
        if (!sprite.isFrameDisplayed(animation.frames().get(idx))) {
            sprite.setDisplayFrame(animation.frames().get(idx));
        }
    }
}
