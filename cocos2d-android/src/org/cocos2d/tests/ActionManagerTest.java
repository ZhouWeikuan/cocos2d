package org.cocos2d.tests;

import org.cocos2d.actions.CCActionManager;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// There is a downloadable demo on this test
// http://code.google.com/p/cocos2d-android-1/downloads/detail?name=ActionManagerTest.3gp&can=2&q=#makechanges
//
public class ActionManagerTest extends Activity {
	// private static final String LOG_TAG = ActionManagerTest.class.getSimpleName();

	public static final int kTagNode	 	= 0;
	public static final int kTagGrossini 	= 1;
	public static final int kTagSister		= 2;
	public static final int kTagSlider		= 3;
	public static final int kTagSequence	= 4;

	private CCGLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);
		CCDirector director = CCDirector.sharedDirector();
		director.attachInView(mGLSurfaceView);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		setContentView(mGLSurfaceView);
		

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 30);

		CCScene scene = CCScene.node();
		scene.addChild(nextAction());

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);

	}

	static int sceneIdx = -1;
	static Class<?> transitions[] = {
		CrashTest.class,
		LogicTest.class,
		PauseTest.class,
		RemoveTest.class,
		Issue835.class,
	};

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onPause() {
		super.onPause();

		CCDirector.sharedDirector().onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		CCDirector.sharedDirector().onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		CCDirector.sharedDirector().end();
		// CCTextureCache.sharedTextureCache().removeAllTextures();
	}

	// 
	// Actions here
	// 
	static CCLayer nextAction() {

		sceneIdx++;
		sceneIdx = sceneIdx % transitions.length;

		return restartAction();
	}

	static CCLayer backAction() {
		sceneIdx--;
		int total = transitions.length;
		if (sceneIdx < 0)
			sceneIdx += total;
		return restartAction();
	}

	static CCLayer restartAction() {
		try {
			Class<?> c = transitions[sceneIdx];
			return (CCLayer) c.newInstance();
		} catch (Exception e) {
			return null;
		}
	}


	static abstract class ActionManagerDemo extends CCLayer {
		CCTextureAtlas atlas;

		public ActionManagerDemo() {

			CGSize s = CCDirector.sharedDirector().winSize();

			CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 32);
			addChild(label, 1);
			label.setPosition(CGPoint.make(s.width / 2, s.height / 2 - 50));

			String subtitle = subtitle();
			if( subtitle != null ) {
				CCLabel l = CCLabel.makeLabel(subtitle, "DroidSerif", 16);
				addChild(l, 1);
				l.setPosition(CGPoint.ccp(s.width/2, s.height - 80));
			}	
			
			
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);

			menu.setPosition(CGPoint.make(0, 0));
			item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
			item2.setPosition(CGPoint.make(s.width / 2, 30));
			item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
			addChild(menu, 1);
		}

		public void restartCallback(Object sender) {
			CCScene s = CCScene.node();
			s.addChild(restartAction());
			CCDirector.sharedDirector().replaceScene(s);
		}

		public void nextCallback(Object sender) {
			CCScene s = CCScene.node();
			s.addChild(nextAction());
			CCDirector.sharedDirector().replaceScene(s);
		}

		public void backCallback(Object sender) {
			CCScene s = CCScene.node();
			s.addChild(backAction());
			CCDirector.sharedDirector().replaceScene(s);
		}

		public String title() {
			return "No Title!";
		}

		public String subtitle() {
			return null;
		}
	}

	static class CrashTest extends ActionManagerDemo {
		public CrashTest() {
			super();

			CCSprite child = CCSprite.sprite("grossini.png");
			child.setPosition(CGPoint.ccp(200,200));
			addChild(child, 1);

			//Sum of all action's duration is 1.5 second.
			child.runAction(CCRotateBy.action(1.5f, 90));
			child.runAction(CCSequence.actions(
					CCDelayTime.action(1.4f),
					CCFadeOut.action(1.1f) ));			

			//After 1.5 second, self will be removed.
			this.runAction(CCSequence.actions(
				CCDelayTime.action(1.4f),
				CCCallFunc.action(this, "removeThis")
				));

		}

		public void removeThis() {
			this.getParent().removeChild(this, true);
			this.nextCallback(null);
		}

		@Override
		public String title(){
			return "Test 1. Should not crash";
		}
	}


	static class LogicTest extends ActionManagerDemo {
		public LogicTest() {
			super();

			CCSprite grossini = CCSprite.sprite("grossini.png");
			addChild(grossini);
			grossini.setPosition(CGPoint.ccp(200,200));

			grossini.runAction(CCSequence.actions( 
				CCMoveBy.action(1.0f, CGPoint.ccp(150,0)),
				CCCallFuncN.action(this, "bugMe")));
		}

		public void bugMe(CCNode node) {
			node.stopAllActions(); //After this stop next action not working, if remove this stop everything is working
			node.runAction(CCScaleTo.action(2.0f, 2.0f));
		}

		public String title() {
			return "Logic test";
		}
	}

	static class PauseTest extends ActionManagerDemo {
		public PauseTest() {
			super();
		}
		
		public void onEnter() {
			//
			// This test MUST be done in 'onEnter' and not on 'init'
			// otherwise the paused action will be resumed at 'onEnter' time
			//
			super.onEnter();

			//
			// Also, this test MUST be done, after [super onEnter]
			//
			CCSprite grossini = CCSprite.sprite("grossini.png");
			addChild(grossini, 0, kTagGrossini);
			grossini.setPosition(CGPoint.ccp(200,200));

			CCAction action = CCMoveBy.action(1.0f, CGPoint.ccp(150,0));

			CCActionManager.sharedManager().addAction(action, grossini, true);

			this.schedule("unpause", 3);
		}

		public void unpause(float dt) {
			unschedule("unpause");
			CCNode node = getChildByTag(kTagGrossini);
			CCActionManager.sharedManager().resume(node);
		}

		@Override
		public String title() {
			return "Pause Test";
		}

		public String subtitle() {
			return "After 3 seconds grossini should move";
		}
	}

	static class RemoveTest extends ActionManagerDemo {
		public RemoveTest() {
			super();

			CCMoveBy move = CCMoveBy.action(2,CGPoint.ccp(200,0));

			CCCallFunc callback = CCCallFunc.action(this, "stopAction");

			CCSequence sequence = CCSequence.actions(move, callback);
			sequence.setTag(kTagSequence);

			CCSprite child = CCSprite.sprite("grossini.png");
			child.setPosition(CGPoint.ccp(200,200));
			this.addChild(child, 1, kTagGrossini);

			child.runAction(sequence);
		}

		public void stopAction(Object sender) {
			CCNode sprite = getChildByTag(kTagGrossini);
			sprite.stopAction(kTagSequence);
		}

		public String title() {
			return "Remove Test";
		}

		public String subtitle() {
			return "Should not crash. Testing issue #841";
		}
	}

	static class Issue835 extends ActionManagerDemo {
		public Issue835() {
			super();
		}
		
		public void onEnter() {
			super.onEnter();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCSprite grossini = CCSprite.sprite("grossini.png");
			addChild(grossini, 0, kTagGrossini);

			grossini.setPosition(CGPoint.ccp(s.width/2, s.height/2));

			// An action should be scheduled before calling pause, otherwise pause won't pause a non-existang target
			grossini.runAction(CCScaleBy.action(2.0f, 2.0f));

			CCActionManager.sharedManager().pause(grossini);
			grossini.runAction(CCRotateBy.action(2.0f, 360));

			this.schedule("resumeGrossini", 3);
		}

		public String title() {
			return "Issue 835";
		}

		public String subtitle() {
			return "Grossini only rotate/scale in 3 seconds";
		}

		public void resumeGrossini(float dt) {
			this.unschedule("resumeGrossini");

			CCNode grossini = this.getChildByTag(kTagGrossini); 
			CCActionManager.sharedManager().resume(grossini);
		}
	}
}
