package org.cocos2d.extensions.scroll;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public class CCClipNode extends CCLayer {
	public static final int RECT_ORIGIN_INVALID = 99999;
	CGRect _clippedRect;
	
	public CCClipNode(){
		_clippedRect = CGRect.make(RECT_ORIGIN_INVALID + 1, 0, 0, 0);
	}
	
	static CGPoint pos = CGPoint.zero();
	static CGPoint pos2 = CGPoint.zero();
	static CGRect rect = CGRect.zero();
	
	private /*CGRect*/ void clipRectFromRect(){
		CGSize size = CCDirector.sharedDirector().winSize();
		
//		CGPoint pos = _clippedRect.origin;
		pos.set(_clippedRect.origin);
		//pos = getParent().convertToWorldSpace(pos.x, pos.y);
		getParent().convertToWorldSpace(pos.x, pos.y, pos);
		pos.y = size.height - pos.y;
		pos = CCDirector.sharedDirector().convertToUI(pos);
		
//		CGPoint pos2 = _clippedRect.origin;
		pos2.set(_clippedRect.origin);
		pos2 = CGPoint.ccpAdd(pos2, CGPoint.ccp(_clippedRect.size.width, _clippedRect.size.height));
		getParent().convertToWorldSpace(pos2.x, pos2.y, pos2);
		pos2.y = size.height - pos2.y;
		pos2 = CCDirector.sharedDirector().convertToUI(pos2);
		
		float x1 = Math.min(pos.x, pos2.x);
		float y1 = Math.min(pos.y, pos2.y);
		float x2 = Math.max(pos.x, pos2.x);
		float y2 = Math.max(pos.y, pos2.y);
		
		rect.set(x1, y1, x2 - x1, y2 - y1);
		
		//return CGRect.make(x1, y1, x2 - x1, y2 - y1);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void visit(GL10 gl) {
		if(_clippedRect.origin.x < RECT_ORIGIN_INVALID){
			/*CGRect globalRect =*/ clipRectFromRect();
			gl.glEnable(gl.GL_SCISSOR_TEST);
			gl.glScissor((int)rect.origin.x, (int)rect.origin.y, (int)rect.size.width, (int)rect.size.height);/*(int)globalRect.origin.x, (int)globalRect.origin.y, 
					(int)globalRect.size.width, (int)globalRect.size.height);*/
		}
		super.visit(gl);
		if(_clippedRect.origin.x < RECT_ORIGIN_INVALID)
			gl.glDisable(gl.GL_SCISSOR_TEST);
	}
	
	public void setClipRect(CGRect clippedRect) {
		_clippedRect = clippedRect;
	}
	
}
