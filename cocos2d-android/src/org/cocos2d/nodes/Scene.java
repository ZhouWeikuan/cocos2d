package org.cocos2d.nodes;

import org.cocos2d.types.CCSize;

public class Scene extends CocosNode {

    public static Scene node() {
        return new Scene();
    }

    protected Scene() {
        CCSize s = Director.sharedDirector().winSize();
        
        setRelativeAnchorPoint(false);

        setAnchorPoint(0.5f, 0.5f);
        setContentSize(s.width, s.height);	
    }
}