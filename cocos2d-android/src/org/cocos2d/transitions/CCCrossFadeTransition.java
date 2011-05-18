package org.cocos2d.transitions;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCFadeTo;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCRenderTexture;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4B;

/**
 CCCrossFadeTransition:
 Cross fades two scenes using the CCRenderTexture object.
 */

public class CCCrossFadeTransition extends CCTransitionScene {

    public static CCCrossFadeTransition transition(float t, CCScene s) {
        return new CCCrossFadeTransition(t, s);
    }

	protected CCCrossFadeTransition(float t, CCScene s) {
		super(t, s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(GL10 gl) {
		// override draw since both scenes (textures) are rendered in 1 scene
		super.draw(gl);
	}

	@Override
	public void onEnter() {
		super.onEnter();

		// create a transparent color layer
		// in which we are going to add our rendertextures
		ccColor4B  color = new ccColor4B(0, 0, 0, 0);
		CGSize size = CCDirector.sharedDirector().winSize();
		CCColorLayer layer = CCColorLayer.node(color);

		// create the first render texture for inScene
		CCRenderTexture inTexture = CCRenderTexture.renderTexture((int)size.width, (int)size.height);
		inTexture.getSprite().setAnchorPoint(0.5f,0.5f);
		inTexture.setPosition(size.width/2, size.height/2);
		inTexture.setAnchorPoint(0.5f,0.5f);

		// render inScene to its texturebuffer
		inTexture.begin();
		inScene.visit(CCDirector.gl);
		inTexture.end();

		// create the second render texture for outScene
		CCRenderTexture outTexture = CCRenderTexture.renderTexture((int)size.width, (int)size.height);
		outTexture.getSprite().setAnchorPoint(0.5f,0.5f);
		outTexture.setPosition(size.width/2, size.height/2);
		outTexture.setAnchorPoint(0.5f,0.5f);

		// render outScene to its texturebuffer
		outTexture.begin();
		outScene.visit(CCDirector.gl);
		outTexture.end();

		// create blend functions

		ccBlendFunc blend1 = new ccBlendFunc(GL10.GL_ONE, GL10.GL_ONE); // inScene will lay on background and will not be used with alpha
		ccBlendFunc blend2 = new ccBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); // we are going to blend outScene via alpha

		// set blendfunctions
		inTexture.getSprite().setBlendFunc(blend1);
		outTexture.getSprite().setBlendFunc(blend2);

		// add render textures to the layer
		layer.addChild(inTexture);
		layer.addChild(outTexture);

		// initial opacity:
		inTexture.getSprite().setOpacity(255);
		outTexture.getSprite().setOpacity(255);

		// create the blend action
		CCIntervalAction layerAction = CCSequence.actions(
				CCFadeTo.action(duration, 0),
				CCCallFunc.action(this, "hideOutShowIn"),
				CCCallFunc.action(this, "finish")
			);

		// run the blend action
		outTexture.getSprite().runAction(layerAction);

		// add the layer (which contains our two rendertextures) to the scene
		addChild(layer, 2, kSceneFade);
	}

	// clean up on exit
	public void onExit() {
		// remove our layer and release all containing objects
		removeChildByTag(kSceneFade, false);

		super.onExit();
	}

}
