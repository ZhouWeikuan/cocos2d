package org.cocos2d.nodes;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class Scene extends CCNode {

    public static Scene node() {
        return new Scene();
    }

    protected Scene() {
        CGSize s = CCDirector.sharedDirector().winSize();
        
        setRelativeAnchorPoint(false);

        setAnchorPoint(CGPoint.make(0.5f, 0.5f));
        setContentSize(s);	
    }
}