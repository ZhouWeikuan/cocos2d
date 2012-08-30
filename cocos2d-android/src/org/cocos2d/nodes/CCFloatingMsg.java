package org.cocos2d.nodes;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

public class CCFloatingMsg extends CCNode {
	private CCLabel lbl;
	private CCSequence fader;
	
	public static float SHOW_SHORT = 1.5f;
	public static float SHOW_LONG  = 2.5f;
	
	protected CCFloatingMsg(String msg, float duration, int size, ccColor3B color) {
		// TODO Auto-generated constructor stub
	  CCScene scene = CCDirector.sharedDirector().getRunningScene();
	  
	  CGSize wSize = CCDirector.sharedDirector().winSize();
	  
	  lbl = CCLabel.makeLabel(msg, "Arial", size);
	  lbl.setColor(color);
	  lbl.setPosition(CGPoint.ccp(wSize.width * 0.5f, lbl.getContentSize().height * 0.5f));
	  lbl.setOpacity(0);
	  
	  scene.addChild(lbl);
	  
	  CCFadeTo showIn  = CCFadeTo.action(0.5f, 255);
	  CCDelayTime wait = CCDelayTime.action(duration);
	  CCFadeTo showOut = CCFadeTo.action(0.5f, 0);
	  CCCallFunc func  = CCCallFunc.action(this, "removeChild");
	  
	  fader = CCSequence.actions(showIn, wait, showOut, func);
	}
	
	public static CCFloatingMsg makeText(String msg) {
		return new CCFloatingMsg(msg, SHOW_SHORT, 18, ccColor3B.ccWHITE);
	}
	
	public static CCFloatingMsg makeText(String msg, float duration) {
		return new CCFloatingMsg(msg, duration, 18, ccColor3B.ccWHITE);
	}
	
	public static CCFloatingMsg makeText(String msg, float duration, int size) {
		return new CCFloatingMsg(msg, duration, size, ccColor3B.ccWHITE);
	}
	
	public static CCFloatingMsg makeText(String msg, float duration, int size, ccColor3B color) {
		return new CCFloatingMsg(msg, duration, size, color);
	}
	
	public void show() {
	  lbl.runAction(fader);
	}
	
	public void show(CGPoint pt) {
	  lbl.setPosition(pt);
	  lbl.runAction(fader);
	}
	
	@SuppressWarnings("unused")
	private void removeChild() {
	  lbl.removeSelf();
	  lbl = null;
	}
}
