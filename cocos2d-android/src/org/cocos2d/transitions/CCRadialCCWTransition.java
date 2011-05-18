package org.cocos2d.transitions;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCProgressFromTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCRenderTexture;
import org.cocos2d.types.CGSize;


///
//	A counter colock-wise radial transition to the next scene
///
public class CCRadialCCWTransition extends CCTransitionScene {

	public static CCRadialCCWTransition transition(float t, CCScene s) {
		return new CCRadialCCWTransition(t, s);
	}

	protected CCRadialCCWTransition(float t, CCScene s) {
		super(t, s);
		// TODO Auto-generated constructor stub
	}

	protected static final int kSceneRadial = 0xc001;


	public void sceneOrder() {
		inSceneOnTop = false;
	}

	public int radialType() {
		return CCProgressTimer.kCCProgressTimerTypeRadialCCW;
	}

	public void onEnter() {
		super.onEnter();
		// create a transparent color layer
		// in which we are going to add our rendertextures
		CGSize size = CCDirector.sharedDirector().winSize();

		// create the second render texture for outScene
		CCRenderTexture outTexture = CCRenderTexture.renderTexture((int)size.width, (int)size.height);
		outTexture.getSprite().setAnchorPoint(0.5f,0.5f);
		outTexture.setPosition(size.width/2, size.height/2);
		outTexture.setAnchorPoint(0.5f,0.5f);

		// render outScene to its texturebuffer
		outTexture.clear(0, 0, 0, 1);
		outTexture.begin();
		outScene.visit(CCDirector.gl);
		outTexture.end();

		//	Since we've passed the outScene to the texture we don't need it.
		hideOutShowIn();

		//	We need the texture in RenderTexture.
		CCProgressTimer outNode = CCProgressTimer.progress(outTexture.getSprite().getTexture());
		// but it's flipped upside down so we flip the sprite
		outNode.getSprite().setFlipY(true);
		//	Return the radial type that we want to use
		outNode.setType(this.radialType());
		outNode.setPercentage(100.f);
		outNode.setPosition(size.width/2, size.height/2);
		outNode.setAnchorPoint(0.5f,0.5f);

		// create the blend action
		CCIntervalAction layerAction = CCSequence.actions(
					CCProgressFromTo.action(duration, 100.f, 0.f),
					CCCallFunc.action(this, "finish"));

		// run the blend action
		outNode.runAction(layerAction);

		// add the layer (which contains our two rendertextures) to the scene
		addChild(outNode, 2, kSceneRadial);
	}

	// clean up on exit
	public void onExit () {
		// remove our layer and release all containing objects
		removeChildByTag(kSceneRadial, false);
		super.onExit();
	}
}
