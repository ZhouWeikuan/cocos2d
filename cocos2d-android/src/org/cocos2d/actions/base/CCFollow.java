package org.cocos2d.actions.base;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

/** CCFollow is an action that "follows" a node.
 
 Eg:
	[layer runAction: [CCFollow actionWithTarget:hero]];
 
 Instead of using CCCamera as a "follower", use this action instead.
 @since v0.99.2
 */
public class CCFollow extends CCAction {
	/* node to follow */
	CCNode	followedNode_;
	
	/* whether camera should be limited to certain area */
	/** alter behavior - turn on/off boundary */
	boolean boundarySet;
	public void setBoundarySet(boolean flag) {
		boundarySet = flag;
	}
	public boolean getBoundarySet() {
		return boundarySet;
	}
	
	/* if screensize is bigger than the boundary - update not needed */
	boolean boundaryFullyCovered;
	
	/* fast access to the screen dimensions */
	CGPoint halfScreenSize;
	CGPoint fullScreenSize;
	
	/* world boundaries */
	float leftBoundary;
	float rightBoundary;
	float topBoundary;
	float bottomBoundary;
	

	/** creates the action with no boundary set */
	public static CCFollow action(CCNode followedNode) {
		return new CCFollow(followedNode);
	}

	/** creates the action with a set boundary */
	public static CCFollow action(CCNode followedNode, CGRect rect) {
		return new CCFollow(followedNode, rect);
	}

	/** initializes the action */
	protected CCFollow(CCNode followedNode) {
		super();

		followedNode_ = followedNode;
		boundarySet = false;
		boundaryFullyCovered = false;

		CGSize winSize = CCDirector.sharedDirector().winSize();
		fullScreenSize = CGPoint.make(winSize.width, winSize.height);
		halfScreenSize = CGPoint.ccpMult(fullScreenSize, .5f);
	}

	/** initializes the action with a set boundary */
	protected CCFollow(CCNode followedNode, CGRect rect) {
		super();
		
		followedNode_ = followedNode;
		boundarySet = true;
		boundaryFullyCovered = false;
		
		CGSize winSize = CCDirector.sharedDirector().winSize();
		fullScreenSize = CGPoint.make(winSize.width, winSize.height);
		halfScreenSize = CGPoint.ccpMult(fullScreenSize, .5f);
		
		leftBoundary = -((rect.origin.x+rect.size.width) - fullScreenSize.x);
		rightBoundary = -rect.origin.x ;
		topBoundary = -rect.origin.y;
		bottomBoundary = -((rect.origin.y+rect.size.height) - fullScreenSize.y);
		
		if(rightBoundary < leftBoundary) {
			// screen width is larger than world's boundary width
			//set both in the middle of the world
			rightBoundary = leftBoundary = (leftBoundary + rightBoundary) / 2;
		}
		
		if(topBoundary < bottomBoundary) {
			// screen width is larger than world's boundary width
			//set both in the middle of the world
			topBoundary = bottomBoundary = (topBoundary + bottomBoundary) / 2;
		}
		
		if( (topBoundary == bottomBoundary) && (leftBoundary == rightBoundary) )
			boundaryFullyCovered = true;
	}

    @Override
    public CCFollow copy() {
    	CCFollow f = new CCFollow(this.followedNode_);
    	f.setTag(this.getTag());
    	return f;
    }
    

	@Override
	public boolean isDone()	{
		return ( ! followedNode_.isRunning());
	}
	
	@Override
	public void stop() {
		target = null;
		super.stop();
	}

	private static float CLAMP(float x, float y, float z) {
		return Math.max(Math.min(x, y), z);
	}
	
	@Override
	public void step(float dt) {
		if(boundarySet) {
			// whole map fits inside a single screen, no need to modify the position - unless map boundaries are increased
			if(boundaryFullyCovered)
				return;
			
			CGPoint tempPos = CGPoint.ccpSub( halfScreenSize, followedNode_.getPosition());
			target.setPosition(CGPoint.ccp(CLAMP(tempPos.x,leftBoundary,rightBoundary), 
					CLAMP(tempPos.y,bottomBoundary,topBoundary)));
		}
		else {
			target.setPosition(CGPoint.ccpSub(halfScreenSize, followedNode_.getPosition()));
		}
	}
	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
		
	}
}
