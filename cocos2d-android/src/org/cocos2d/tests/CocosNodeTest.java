package org.cocos2d.tests;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.camera.CCOrbitCamera;
import org.cocos2d.actions.ease.CCEaseInOut;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCCamera;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.particlesystem.CCParticleFire;
import org.cocos2d.particlesystem.CCParticleSun;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

//
// cocos node tests
// a cocos2d example
// http://www.cocos2d-iphone.org
//
public class CocosNodeTest extends Activity {
	// private static final String LOG_TAG = CocosNodeTest.class.getSimpleName();
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
		setContentView(mGLSurfaceView);
		

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// set landscape mode
		CCDirector.sharedDirector().setLandscape(false);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 30);

		CCScene scene = CCScene.node();
		scene.addChild(nextAction());

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

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
	}

	public static final int kTagSprite1 = 1;
	public static final int kTagSprite2 = 2;
	public static final int kTagSprite3 = 3;
	public static final int kTagSlider  = 4;

	static int sceneIdx = -1;
	static Class<?> transitions[] = {
		Test2.class,
		Test4.class,
		Test5.class,
		Test6.class,
		StressTest1.class,
		StressTest2.class,
		NodeToWorld.class,
		SchedulerTest1.class,
		CameraOrbitTest.class,
		CameraZoomTest.class,
		CameraCenterTest.class,
	};

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
		Class<?> c = transitions[sceneIdx];
		try {
			return (CCLayer) c.newInstance();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	static abstract class TestDemo extends CCLayer {
		public TestDemo() {
			super();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 32);
			addChild(label);
			label.setPosition(CGPoint.ccp(s.width/2, s.height-50));

			String subtitle = subtitle();
			if( subtitle != null ) {
				CCLabel l = CCLabel.makeLabel(subtitle, "DroidSerif", 16);
				addChild(l, 1);
				l.setPosition(CGPoint.ccp(s.width/2, s.height-80));
			}

			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CGPoint.zero());
			item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
			item2.setPosition(CGPoint.make(s.width / 2, 30));
			item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
			addChild(menu, -1);
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
			return "No title";
		}

		public String subtitle() {
			return null;
		}
	}

	static class Test2 extends TestDemo {

		public void onEnter() {
			super.onEnter();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
			CCSprite sp3 = CCSprite.sprite("grossinis_sister1.png");
			CCSprite sp4 = CCSprite.sprite("grossinis_sister2.png");

			sp1.setPosition(CGPoint.make(100, s.height / 2));
			sp2.setPosition(CGPoint.make(380, s.height / 2));
			addChild(sp1);
			addChild(sp2);

			sp3.setScale(0.25f);
			sp4.setScale(0.25f);

			sp1.addChild(sp3);
			sp2.addChild(sp4);

			CCIntervalAction a1 = CCRotateBy.action(2, 360);
			CCIntervalAction a2 = CCScaleBy.action(2, 2);

			CCAction action1 = CCRepeatForever.action(CCSequence.actions(a1, a2, a2.reverse()));
			CCAction action2 = CCRepeatForever.action(CCSequence.actions(a1.copy(), a2.copy(), a2.reverse()));

			sp2.setAnchorPoint(CGPoint.zero());

			sp1.runAction(action1);
			sp2.runAction(action2);
		}

		public String title() {
			return "anchorPoint and children";
		}
	}

	static class Test4 extends TestDemo {

		public Test4() {
			super();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");

			sp1.setPosition(CGPoint.make(100, 160));
			sp2.setPosition(CGPoint.make(380, 160));

			addChild(sp1, 0, 2);
			addChild(sp2, 0, 3);

			schedule("delay2", 2.0f);
			schedule("delay4", 4.0f);
		}

		public void delay2(float dt) {
			CCNode node = getChildByTag(2);
			CCIntervalAction action1 = CCRotateBy.action(1, 360);
			node.runAction(action1);
		}

		public void delay4(float dt) {
			unschedule("delay4");
			removeChildByTag(3, false);
		}

		public String title() {
			return "Tags";
		}
	}

	static class Test5 extends TestDemo {

		public Test5() {
			super();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");

			sp1.setPosition(CGPoint.make(100, 160));
			sp2.setPosition(CGPoint.make(380, 160));

			CCIntervalAction rot = CCRotateBy.action(2, 360);
			CCIntervalAction rot_back = rot.reverse();
			CCAction forever = CCRepeatForever.action(
					CCSequence.actions(rot, rot_back));
			CCAction forever2 = forever.copy();

			forever.setTag(101);
			forever2.setTag(102);

			addChild(sp1, 0, kTagSprite1);
			addChild(sp2, 0, kTagSprite2);

			sp1.runAction(forever);
			sp2.runAction(forever2);

			schedule("addAndRemove", 2.0f);
		}

		public void addAndRemove(float dt) {
			CCNode sp1 = getChildByTag(kTagSprite1);
			CCNode sp2 = getChildByTag(kTagSprite2);

			removeChild(sp1, false);
			removeChild(sp2, true);

			addChild(sp1, 0, kTagSprite1);
			addChild(sp2, 0, kTagSprite2);
		}

		public String title() {
			return "remove and cleanup";
		}
	}

	static class Test6 extends TestDemo {

		public Test6() {
			super();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			CCSprite sp11 = CCSprite.sprite("grossinis_sister1.png");

			CCSprite sp2 = CCSprite.sprite("grossinis_sister2.png");
			CCSprite sp21 = CCSprite.sprite("grossinis_sister2.png");

			sp1.setPosition(CGPoint.make(100, 160));
			sp2.setPosition(CGPoint.make(380, 160));


			CCIntervalAction rot = CCRotateBy.action(2, 360);
			CCIntervalAction rot_back = rot.reverse();
			CCAction forever1 = CCRepeatForever.action(CCSequence.actions(rot, rot_back));
			CCAction forever11 = forever1.copy();

			CCAction forever2 = forever1.copy();
			CCAction forever21 = forever1.copy();

			addChild(sp1, 0, kTagSprite1);
			sp1.addChild(sp11);
			addChild(sp2, 0, kTagSprite2);
			sp2.addChild(sp21);

			sp1.runAction(forever1);
			sp11.runAction(forever11);
			sp2.runAction(forever2);
			sp21.runAction(forever21);

			schedule("addAndRemove", 2.0f);
		}

		public void addAndRemove(float dt) {
			CCNode sp1 = getChildByTag(kTagSprite1);
			CCNode sp2 = getChildByTag(kTagSprite2);

			removeChild(sp1, false);
			removeChild(sp2, true);

			addChild(sp1, 0, kTagSprite1);
			addChild(sp2, 0, kTagSprite2);
		}


		public String title() {
			return "remove/cleanup with children";
		}
	}

	static class StressTest1 extends TestDemo {
		public StressTest1() {
			super();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			addChild(sp1, 0, kTagSprite1);

			sp1.setPosition(CGPoint.ccp(s.width/2, s.height/2));

			schedule("shouldNotCrash", 1.0f);
		}

		public void shouldNotCrash(float delta) {
			unschedule("shouldNotCrash");

			CGSize s = CCDirector.sharedDirector().winSize();

			// if the node has timers, it crashes
			CCNode explosion = CCParticleSun.node();

			// if it doesn't, it works Ok.
			//    	CocosNode *explosion = [Sprite spriteWithFile:@"grossinis_sister2.png"];

			explosion.setPosition(CGPoint.ccp(s.width/2, s.height/2));

			runAction(CCSequence.actions(
				CCRotateBy.action(2.0f, 360),
				CCCallFuncN.action(this, "removeMe")
				));

			addChild(explosion);
		}

		// remove
		public void removeMe(Object node) {
			this.getParent().removeChild((CCNode)node, true);
			nextCallback(this);
		}

		public String title(){
			return "stress test #1: no crashes";
		}
	}

	static class StressTest2 extends TestDemo {
		public StressTest2() {
			//
			// Purpose of this test:
			// Objects should be released when a layer is removed
			//

			super();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCLayer sublayer = CCLayer.node();

			CCSprite sp1 = CCSprite.sprite("grossinis_sister1.png");
			sp1.setPosition(CGPoint.ccp(80, s.height/2));

			CCIntervalAction move = CCMoveBy.action(3.0f, CGPoint.ccp(350,0));
			CCIntervalAction move_ease_inout3 = CCEaseInOut.action(move.copy(), 2.0f);
			CCIntervalAction move_ease_inout_back3 = move_ease_inout3.reverse();
			CCIntervalAction seq3 = CCSequence.actions(move_ease_inout3, move_ease_inout_back3);
			sp1.runAction(CCRepeatForever.action(seq3));
			sublayer.addChild(sp1, 1);

			CCParticleFire fire = CCParticleFire.node();
			fire.setPosition(CGPoint.ccp(80, s.height/2-50));
			CCIntervalAction copy_seq3 = seq3.copy();
			fire.runAction(CCRepeatForever.action(copy_seq3));
			sublayer.addChild(fire, 2);

			schedule("shouldNotLeak", 6.0f);

			addChild(sublayer, 0, kTagSprite1);
		}

		public void shouldNotLeak(float dt) {
			unschedule("shouldNotLeak");
			CCNode sublayer = getChildByTag(kTagSprite1);
			sublayer.removeAllChildren(true);
		}

		public String title() {
			return "stress test #2: no leaks";
		}
	}

	static class CustomNode extends CCNode {
		public static CustomNode node() {
			return new CustomNode();
		}
		
		public void doSomething(float dt) {
			ccMacros.CCLOG("CustomNode", "do something...");
		}
	}

	static class SchedulerTest1 extends TestDemo {
		public SchedulerTest1() {
			//
			// Purpose of this test:
			// Scheduler should be released
			//
            super();

            CCNode layer =  CustomNode.node();
            // ccMacros.CCLog(@"retain count after init is %d", [layer retainCount]);                // 1

            addChild(layer, 0);
            // ccMacros.CCLog(@"retain count after addChild is %d", [layer retainCount]);      // 2

            layer.schedule("doSomething");
            // ccMacros.CCLog(@"retain count after schedule is %d", [layer retainCount]);      // 3

            layer.unschedule("doSomething");
            // ccMacros.CCLog(@"retain count after unschedule is %d", [layer retainCount]);		// STILL 3!
		}

		public String title() {
			return "cocosnode scheduler test #1";
		}
	}

	static class NodeToWorld extends TestDemo {
		public NodeToWorld() {
            super();

            //
            // This code tests that nodeToParent works OK:
            //  - It tests different anchor Points
            //  - It tests different children anchor points

            CCSprite back = CCSprite.sprite("background3.png");
            addChild(back, -10);
            back.setAnchorPoint(CGPoint.ccp(0,0));
            CGSize backSize = back.getContentSize();

            CCMenuItem item = CCMenuItemImage.item("btn-play-normal.png", "btn-play-selected.png");
            CCMenu menu = CCMenu.menu(item);
            menu.alignItemsVertically();
            menu.setPosition(CGPoint.ccp(backSize.width/2, backSize.height/2));
            back.addChild(menu);

            CCIntervalAction rot = CCRotateBy.action(5, 360);
            CCRepeatForever fe = CCRepeatForever.action(rot);
            item.runAction(fe);

            CCIntervalAction move = CCMoveBy.action(3.0f, CGPoint.ccp(200,0));
            CCIntervalAction move_back = move.reverse();
            CCSequence seq = CCSequence.actions(move, move_back);
            CCRepeatForever fe2 = CCRepeatForever.action(seq);
            back.runAction(fe2);
        }

		public String title() {
			return "nodeToParent transform";
		}
	}

	static class CameraOrbitTest extends TestDemo {
		public void onEnter() {
            super.onEnter();
			CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection3D);
		}

		public void onExit() {
			CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection2D);
            super.onExit();
		}

        public CameraOrbitTest() {
            super();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite p = CCSprite.sprite("background3.png");
            addChild(p, 0);
            p.setPosition(CGPoint.ccp(s.width/2, s.height/2));
            p.setOpacity(128);

            CCSprite sprite = null;
            CCOrbitCamera orbit = null;

            // LEFT
            sprite = CCSprite.sprite("grossini.png");
            sprite.setScale(0.5f);
            p.addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/4*1, s.height/2));
            orbit = CCOrbitCamera.action(2.0f, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // CENTER
            sprite = CCSprite.sprite("grossini.png");
            sprite.setScale(1.0f);
            p.addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/4*2, s.height/2));
            orbit = CCOrbitCamera.action(2.0f, 1, 0, 0, 360, 45, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // RIGHT
            sprite = CCSprite.sprite("grossini.png");
            sprite.setScale(2.0f);
            p.addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/4*3, s.height/2));
            orbit = CCOrbitCamera.action(2.0f, 1, 0, 0, 360, 90, -45);
            sprite.runAction(CCRepeatForever.action(orbit));

            // PARENT
            orbit = CCOrbitCamera.action(10.0f, 1, 0, 0, 360, 0, 90);
            p.runAction(CCRepeatForever.action(orbit));

            this.setScale(1.0f);
        }

		public String title() {
			return "Camera Orbit test";
		}
	}

	static class CameraZoomTest extends TestDemo {
		public void onEnter() {
			super.onEnter();
			CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection3D);
		}

		public void onExit() {
			CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection2D);
			super.onExit();
		}

        public CameraZoomTest() {
            super();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sprite = null;
            CCCamera cam = null;

            // LEFT
            sprite = CCSprite.sprite("grossini.png");
            addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/4*1, s.height/2));
            cam = sprite.getCamera();
            cam.setEye(0, 0, 415);

            // CENTER
            sprite = CCSprite.sprite("grossini.png");
            addChild(sprite, 0, 40);
            sprite.setPosition(CGPoint.ccp(s.width/4*2, s.height/2));
            //    		cam = [sprite camera];
            //    		[cam setEyeX:0 eyeY:0 eyeZ:415/2];

            // RIGHT
            sprite = CCSprite.sprite("grossini.png");
            addChild(sprite, 0, 20);
            sprite.setPosition(CGPoint.ccp(s.width/4*3, s.height/2));
            //    		cam = [sprite camera];
            //    		[cam setEyeX:0 eyeY:0 eyeZ:-485];
            //    		[cam setCenterX:0 centerY:0 centerZ:0];


            schedule(new UpdateCallback() {
				@Override
				public void update(float d) {
					updateEye(d);
				}
			});
        }

        static float z = 0;

		public void updateEye(float dt) {
			CCNode sprite = null;
			CCCamera cam = null;

			z += dt * 100;

			sprite = getChildByTag(20);
			cam = sprite.getCamera();
			cam.setEye(0, 0, z);

			sprite = getChildByTag(40);
			cam = sprite.getCamera();
			cam.setEye(0, 0, z);
		}

		public String title() {
			return "Camera Zoom test";
		}
	}

	static class CameraCenterTest extends TestDemo {

        public CameraCenterTest() {
            super();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCSprite sprite = null;
            CCOrbitCamera orbit = null;

            // LEFT-TOP
            sprite = CCSprite.sprite("grossini.png");
            addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/5*1, s.height/5*1));
            sprite.setColor(ccColor3B.ccRED);
            sprite.setTextureRect(CGRect.make(0, 0, 120, 50));
            orbit = CCOrbitCamera.action(10, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // LEFT-BOTTOM
            sprite = CCSprite.sprite("grossinis_sister1.png");
            addChild(sprite, 0, 40);
            sprite.setPosition(CGPoint.ccp(s.width/5*1, s.height/5*4));
            sprite.setColor(ccColor3B.ccBLUE);
            sprite.setTextureRect(CGRect.make(0, 0, 120, 50));
            orbit = CCOrbitCamera.action(10.0f, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // RIGHT-TOP
            sprite = CCSprite.sprite("grossinis_sister2.png");
            addChild(sprite, 0);
            sprite.setPosition(CGPoint.ccp(s.width/5*4, s.height/5*1));
            sprite.setColor(ccColor3B.ccYELLOW);
            sprite.setTextureRect(CGRect.make(0, 0, 120, 50));
            orbit = CCOrbitCamera.action(10.0f, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // RIGHT-BOTTOM
            sprite = CCSprite.sprite("smoke.png");
            addChild(sprite, 0, 40);
            sprite.setPosition(CGPoint.ccp(s.width/5*4, s.height/5*4));
            sprite.setColor(ccColor3B.ccGREEN);
            sprite.setTextureRect(CGRect.make(0, 0, 120, 50));
            orbit = CCOrbitCamera.action(10.0f, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));

            // CENTER
            sprite = CCSprite.sprite("snow.png");
            addChild(sprite, 0, 40);
            sprite.setPosition(CGPoint.ccp(s.width/2, s.height/2));
            sprite.setColor(ccColor3B.ccWHITE);
            sprite.setTextureRect(CGRect.make(0, 0, 120, 50));
            orbit = CCOrbitCamera.action(10.0f, 1, 0, 0, 360, 0, 0);
            sprite.runAction(CCRepeatForever.action(orbit));
        }

		public String title() {
			return "Camera Center test";
		}

		public String subtitle() {
			return "Sprites should rotate at the same speed";
		}
	}
}
