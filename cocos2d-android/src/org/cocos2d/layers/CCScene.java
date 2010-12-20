package org.cocos2d.layers;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;

/** CCScene is a subclass of CCNode that is used only as an abstract concept.
 
 CCScene an CCNode are almost identical with the difference that CCScene has it's
 anchor point (by default) at the center of the screen.

 For the moment CCScene has no other logic than that, but in future releases it might have
 additional logic.

 It is a good practice to use and CCScene as the parent of all your nodes.
*/
public class CCScene extends CCNode {

    public static CCScene node() {
        return new CCScene();
    }

    protected CCScene() {
    	super();
    	
        CGSize s = CCDirector.sharedDirector().winSize();
        
        setRelativeAnchorPoint(false);

        setAnchorPoint(0.5f, 0.5f);
        setContentSize(s);	
    }
}

