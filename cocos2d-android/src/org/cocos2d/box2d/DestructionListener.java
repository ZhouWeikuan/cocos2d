package org.cocos2d.box2d;

import org.box2d.dynamics.BBFixture;
import org.box2d.dynamics.joints.BBJoint;
import static org.box2d.dynamics.BBWorldCallbacks.*;

public class DestructionListener implements BBDestructionListener {
    
    public void SayGoodbye(BBFixture fixture) {}
    public void SayGoodbye(BBJoint joint) {}

    Test test;

}
