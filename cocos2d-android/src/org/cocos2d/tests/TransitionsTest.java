package org.cocos2d.tests;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.CCCrossFadeTransition;
import org.cocos2d.transitions.CCFadeBLTransition;
import org.cocos2d.transitions.CCFadeDownTransition;
import org.cocos2d.transitions.CCFadeTRTransition;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.transitions.CCFadeUpTransition;
import org.cocos2d.transitions.CCFlipAngularTransition;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.transitions.CCFlipYTransition;
import org.cocos2d.transitions.CCJumpZoomTransition;
import org.cocos2d.transitions.CCMoveInBTransition;
import org.cocos2d.transitions.CCMoveInLTransition;
import org.cocos2d.transitions.CCMoveInRTransition;
import org.cocos2d.transitions.CCMoveInTTransition;
import org.cocos2d.transitions.CCPageTurnTransition;
import org.cocos2d.transitions.CCRadialCCWTransition;
import org.cocos2d.transitions.CCRadialCWTransition;
import org.cocos2d.transitions.CCRotoZoomTransition;
import org.cocos2d.transitions.CCShrinkGrowTransition;
import org.cocos2d.transitions.CCSlideInBTransition;
import org.cocos2d.transitions.CCSlideInLTransition;
import org.cocos2d.transitions.CCSlideInRTransition;
import org.cocos2d.transitions.CCSlideInTTransition;
import org.cocos2d.transitions.CCSplitColsTransition;
import org.cocos2d.transitions.CCSplitRowsTransition;
import org.cocos2d.transitions.CCTurnOffTilesTransition;
import org.cocos2d.transitions.CCZoomFlipAngularTransition;
import org.cocos2d.transitions.CCZoomFlipXTransition;
import org.cocos2d.transitions.CCZoomFlipYTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class TransitionsTest extends Activity {
	public static final String LOG_TAG = "TransitionTest";
	// private static final String LOG_TAG = TransitionsTest.class.getSimpleName();
	private CCGLSurfaceView mGLSurfaceView;

	private static final float TRANSITION_DURATION = 1.2f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mGLSurfaceView = new CCGLSurfaceView(this);
		setContentView(mGLSurfaceView);
	}

	@Override
	public void onStart() {
		super.onStart();

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// set landscape mode
		CCDirector.sharedDirector().setLandscape(true);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = TestLayer.scene();

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
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

	static class FadeWhiteTransition extends CCFadeTransition {
		public FadeWhiteTransition(float t, CCScene s) {
			super(t, s, ccColor3B.ccWHITE);
		}
	}

	static class FlipXLeftOver extends CCFlipXTransition {
		public FlipXLeftOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationLeftOver);
		}
	}

	static class FlipXRightOver extends CCFlipXTransition {
		public FlipXRightOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationRightOver);
		}
	}

	static class FlipYUpOver extends CCFlipYTransition {
		public FlipYUpOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationUpOver);
		}
	}

	static class FlipYDownOver extends CCFlipYTransition {
		public FlipYDownOver (float t, CCScene s) {
			super(t, s, tOrientation.kOrientationDownOver);
		}
	}

	static class FlipAngularLeftOver extends CCFlipAngularTransition {
		public FlipAngularLeftOver (float t, CCScene s) {
			super(t, s, tOrientation.kOrientationLeftOver);
		}
	}

	static class FlipAngularRightOver extends CCFlipAngularTransition {
		public FlipAngularRightOver (float t, CCScene s) {
			super(t, s, tOrientation.kOrientationRightOver);
		}
	}

	static class ZoomFlipXLeftOver extends CCZoomFlipXTransition {
		public ZoomFlipXLeftOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationLeftOver);
		}
	}

	static class ZoomFlipXRightOver extends CCZoomFlipXTransition {
		public ZoomFlipXRightOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationRightOver);
		}
	}

	static class ZoomFlipYUpOver extends CCZoomFlipYTransition {
		public ZoomFlipYUpOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationUpOver);
		}
	}

	static class ZoomFlipYDownOver extends CCZoomFlipYTransition {
		public ZoomFlipYDownOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationDownOver);
		}
	}

	static class ZoomFlipAngularLeftOver extends CCZoomFlipAngularTransition {
		public ZoomFlipAngularLeftOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationLeftOver);
		}
	}

	static class ZoomFlipAngularRightOver extends CCZoomFlipAngularTransition {
		public ZoomFlipAngularRightOver(float t, CCScene s) {
			super(t, s, tOrientation.kOrientationRightOver);
		}
	}

	static class PageTransitionForward extends CCPageTurnTransition {
		public PageTransitionForward(float t, CCScene s) {
			super(t, s, false);
		}
	}

	static class PageTransitionBackward extends CCPageTurnTransition {
		public PageTransitionBackward(float t, CCScene s) {
			super(t, s, true);
		}
	}

	static int sceneIdx = 0;
	static Class<?> transitions[] = {
		CCJumpZoomTransition.class,
		CCCrossFadeTransition.class,
		CCRadialCCWTransition.class,
		CCRadialCWTransition.class,
		PageTransitionForward.class,
		PageTransitionBackward.class,
		CCFadeTRTransition.class,
		CCFadeBLTransition.class,
		CCFadeUpTransition.class,
		CCFadeDownTransition.class,
		CCTurnOffTilesTransition.class,
		CCSplitRowsTransition.class,
		CCSplitColsTransition.class,
		CCFadeTransition.class,
		FadeWhiteTransition.class,
		FlipXLeftOver.class,
		FlipXRightOver.class,
		FlipYUpOver.class,
		FlipYDownOver.class,
		FlipAngularLeftOver.class,
		FlipAngularRightOver.class,
		ZoomFlipXLeftOver.class,
		ZoomFlipXRightOver.class,
		ZoomFlipYUpOver.class,
		ZoomFlipYDownOver.class,
		ZoomFlipAngularLeftOver.class,
		ZoomFlipAngularRightOver.class,
		CCShrinkGrowTransition.class,
		CCRotoZoomTransition.class,
		CCMoveInLTransition.class,
		CCMoveInRTransition.class,
		CCMoveInTTransition.class,
		CCMoveInBTransition.class,
		CCSlideInLTransition.class,
		CCSlideInRTransition.class,
		CCSlideInTTransition.class,
		CCSlideInBTransition.class,
	};

	static Class<?> nextTransition() {
		sceneIdx++;
		sceneIdx = sceneIdx % transitions.length;

		return transitions[sceneIdx];
	}

	static Class<?> backTransition() {
		sceneIdx--;
		int total = transitions.length;
		if (sceneIdx < 0)
			sceneIdx += total;

		return transitions[sceneIdx];
	}

	static Class<?> restartTransition() {
		return transitions[sceneIdx];
	}

	static class TestLayer extends CCLayer {
		public static CCScene scene() {
			CCScene s = CCScene.node();
			TestLayer l = new TestLayer();
			s.addChild(l);

			return s;
		}

		public TestLayer() {
			CGSize size = CCDirector.sharedDirector().winSize();
			float x = size.width;
			float y = size.height;

			CCSprite bg1 = CCSprite.sprite("background1.jpg");
			bg1.setPosition(size.width/2, size.height/2);
			addChild(bg1, -1);

			CCLabel title = CCLabel.makeLabel(transitions[sceneIdx].toString(), "DroidSans", 24);
			addChild(title);
			title.setColor(new ccColor3B(255, 32, 32));
			title.setPosition(CGPoint.make(x / 2, y-100));

			CCLabel label = CCLabel.makeLabel("SCENE 1", "DroidSans", 32);
			label.setColor(new ccColor3B(16,16,255));
			label.setPosition(x/2,y/2);
			addChild(label);

			// menu
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(0, 0);
			item1.setPosition(size.width / 2 - 100, 30);
			item2.setPosition(size.width / 2, 30);
			item3.setPosition(size.width / 2 + 100, 30);
			addChild(menu, 1);

			schedule("step", 1.0f);
		}

		public void step(float dt) {
			ccMacros.CCLOG(LOG_TAG, "Scene1#step called");
		}

		public void onEnter() {
			super.onEnter();

			ccMacros.CCLOG(LOG_TAG, "Scene 1 onEnter");
		}

		public void onEnterTransitionDidFinish() {
			super.onEnterTransitionDidFinish();

			ccMacros.CCLOG(LOG_TAG, "Scene 1: transition did finish");
		}

		public void onExit() {
			ccMacros.CCLOG(LOG_TAG, "Scene 1 onExit");

			super.onExit();
		}

		@Override
		public void finalize() throws Throwable {
			ccMacros.CCLOG(LOG_TAG, "------> Scene#1 dealloc!");
			super.finalize();
		}

		public void nextCallback(Object sender) {
			Class<?> transition = nextTransition();
			restartCallback(transition);
		}

		public void backCallback(Object sender) {
			Class<?> transition = backTransition();
			restartCallback(transition);
		}

		public void restartCallback(Object sender) {
			Class<?> transition = restartTransition();
			CCScene s2 = TestLayer2.scene();

			try {
				Constructor<?> c = transition.getConstructor(Float.TYPE, CCScene.class);
				CCScene s = (CCScene) c.newInstance(TRANSITION_DURATION, s2);
				CCDirector.sharedDirector().replaceScene(s);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class TestLayer2 extends CCLayer {
		public static CCScene scene() {
			CCScene s = CCScene.node();
			TestLayer2 l = new TestLayer2();
			s.addChild(l);

			return s;
		}

		public TestLayer2() {
			CGSize size = CCDirector.sharedDirector().winSize();
			float x = size.width;
			float y = size.height;

			CCSprite bg2 = CCSprite.sprite("background2.jpg");
			bg2.setPosition(size.width/2, size.height/2);
			addChild(bg2, -1);

			CCLabel title = CCLabel.makeLabel(transitions[sceneIdx].toString(), "DroidSans", 24);
			addChild(title);
			title.setColor(new ccColor3B(255,32,32));
			title.setPosition(x/2, y-100);

			CCLabel label = CCLabel.makeLabel("SCENE 2", "DroidSans", 32);
			label.setColor(new ccColor3B(16,16,255));
			label.setPosition(x / 2, y / 2);
			addChild(label);

			// menu
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");
			// menu
			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(0, 0);
			item1.setPosition(size.width / 2 - 100, 30);
			item2.setPosition(size.width / 2, 30);
			item3.setPosition(size.width / 2 + 100, 30);
			addChild(menu, 1);

			this.schedule("step", 1.0f);
		}

		public void nextCallback(Object sender) {
			Class<?> transition = nextTransition();
			restartCallback(transition);
		}

		public void backCallback(Object sender) {
			Class<?> transition = backTransition();
			restartCallback(transition);
		}

		public void restartCallback(Object sender) {
			Class<?> transition = restartTransition();
			CCScene s2 = TestLayer.scene();
			try {
				Constructor<?> c = transition.getConstructor(Float.TYPE, CCScene.class);
				CCScene s = (CCScene)c.newInstance(TRANSITION_DURATION, s2);
				CCDirector.sharedDirector().replaceScene(s);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void step(float dt) {
			ccMacros.CCLOG("TransitionTest", "Scene2#step called");
		}

		/// callbacks
		public void onEnter() {
			super.onEnter();
			ccMacros.CCLOG("TransitionTest", "Scene 2 onEnter");
		}

		public void onEnterTransitionDidFinish() {
			super.onEnterTransitionDidFinish();
			ccMacros.CCLOG("TransitionTest", "Scene 2: transition did finish");
		}

		public void onExit() {
			super.onExit();
			ccMacros.CCLOG("TransitionTest", "Scene 2 onExit");
		}
	}
}

