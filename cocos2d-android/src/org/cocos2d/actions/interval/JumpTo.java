package org.cocos2d.actions.interval;

import org.cocos2d.nodes.CocosNode;

//
// JumpTo
//

public class JumpTo extends JumpBy {

    public static JumpTo action(float time, float x, float y, float height, int jumps) {
        return new JumpTo(time, x, y, height, jumps);
    }

    protected JumpTo(float time, float x, float y, float height, int jumps) {
        super(time, x, y, height, jumps);
    }

    @Override
    public IntervalAction copy() {
        return new JumpTo(duration, delta.x, delta.y, height, jumps);
    }

    @Override
    public void start(CocosNode aTarget) {
        super.start(aTarget);
        delta.x -= startPosition.x;
        delta.y -= startPosition.y;
    }
}
