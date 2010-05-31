package org.cocos2d.box2d;

import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBFixture;

public class ContactPoint {
    BBFixture fixtureA;
    BBFixture fixtureB;
    BBVec2 normal;
    BBVec2 position;
//    BBPointState state;
}
