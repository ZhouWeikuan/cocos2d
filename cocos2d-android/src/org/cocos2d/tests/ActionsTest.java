package org.cocos2d.tests;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCFollow;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.camera.CCOrbitCamera;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.instant.CCPlace;
import org.cocos2d.actions.instant.CCShow;
import org.cocos2d.actions.instant.CCToggleVisibility;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCBezierBy;
import org.cocos2d.actions.interval.CCBezierTo;
import org.cocos2d.actions.interval.CCBlink;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCPropertyAction;
import org.cocos2d.actions.interval.CCRepeat;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.actions.interval.CCTintBy;
import org.cocos2d.actions.interval.CCTintTo;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLResourceHelper.Resource;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// ActionsTest, there is a downloadable demo here:
//  http://code.google.com/p/cocos2d-android-1/downloads/detail?name=ActionsTest.3gp&can=2&q=#makechanges
//
public class ActionsTest extends Activity {
	private static final String LOG_TAG = ActionsTest.class.getSimpleName();

	public static final int kTagAnimationDance = 1;

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
		ActionManual.class,
		ActionMove.class,
		ActionRotate.class,
		ActionScale.class,
		ActionJump.class,
		ActionBezier.class,
		ActionBlink.class,
		ActionFade.class,
		ActionTint.class,
		ActionAnimate.class,
		ActionSequence.class,
		ActionSequence2.class,
		ActionSpawn.class,
		ActionReverse.class,
		ActionDelayTime.class,
		ActionRepeat.class,
		ActionRepeatForever.class,
		ActionRotateToRepeat.class,
		ActionRotateJerk.class,
		ActionCallFunc.class,
		ActionCallFuncND.class,
		ActionReverseSequence.class,
		ActionReverseSequence2.class,
		ActionOrbit.class,
		ActionFollow.class,
		ActionProperty.class,
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


	static class ActionDemo extends CCLayer {
		CCSprite grossini;
		CCSprite tamara;
		CCSprite kathia;
		public ActionDemo()	{
			super();

			// Example:
			// You can create a sprite using a Texture2D
			
			Bitmap bmp = null;
			InputStream is;
			try {
				is = CCDirector.theApp.getAssets().open("grossini.png");
				bmp = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Keep copy for reinit texture after Activity pause
			final Bitmap bmpCopy = bmp.copy(bmp.getConfig(), false);
			bmp.recycle();
			
			CCTexture2D tex = new CCTexture2D();
			tex.setLoader(new GLResourceHelper.GLResourceLoader() {
				@Override
				public void load(Resource res) {
					Bitmap bmpForInit = bmpCopy.copy(bmpCopy.getConfig(), false);
					((CCTexture2D)res).initWithImage(bmpForInit);
				}
			});
			grossini = CCSprite.sprite(tex);
			// or just use:
			// grossini = CCSprite.sprite(bmp);
			// and better not to use this way at all(this is better for manually generated tex)
			// because you have to store bmp copy in memory.
			// use for assets:
			// CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage("grossini.png");
			// grossini = CCSprite.sprite(tex);
			

			// Example:
			// Or you can create an sprite using a filename. PNG, JPEG and BMP files are supported. Probably TIFF too
			tamara = CCSprite.sprite("grossinis_sister1.png");
			kathia = CCSprite.sprite("grossinis_sister2.png");

			addChild(grossini, 1);
			addChild(tamara, 2);
			addChild(kathia, 3);

			CGSize s = CCDirector.sharedDirector().winSize();

			grossini.setPosition(CGPoint.ccp(s.width/2, s.height/3));
			tamara.setPosition(CGPoint.ccp(s.width/2, 2*s.height/3));
			kathia.setPosition(CGPoint.ccp(s.width/2, s.height/2));

			CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
			addChild(label);
			label.setPosition(CGPoint.ccp(s.width/2, s.height-50));

			String subtitle = subtitle();
			if( subtitle != null) {
				CCLabel l = CCLabel.makeLabel(subtitle, "DroidSerif", 16);
				addChild(l, 1);
				l.setPosition(CGPoint.ccp(s.width/2, s.height-80));
			}

			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CGPoint.zero());
			item1.setPosition(CGPoint.ccp( s.width/2 - 100, 30));
			item2.setPosition(CGPoint.ccp( s.width/2, 30));
			item3.setPosition(CGPoint.ccp( s.width/2 + 100, 30));
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

		public void alignSpritesLeft(int numberOfSprites) {
			CGSize s = CCDirector.sharedDirector().winSize();

			if( numberOfSprites == 1 ) {
				tamara.setVisible(false);
				kathia.setVisible(false);
				grossini.setPosition(CGPoint.ccp(60, s.height/2));
			} else if( numberOfSprites == 2 ) {		
				kathia.setPosition(CGPoint.ccp(60, s.height/3));
				tamara.setPosition(CGPoint.ccp(60, 2*s.height/3));
				grossini.setVisible(false);
			} else if( numberOfSprites == 3 ) {
				grossini.setPosition(CGPoint.ccp(60, s.height/2));
				tamara.setPosition(CGPoint.ccp(60, 2*s.height/3));
				kathia.setPosition(CGPoint.ccp(60, s.height/3));
			} else {
				ccMacros.CCLOG(LOG_TAG, "ActionsTests: Invalid number of Sprites");
			}	
		}

		public void centerSprites(int numberOfSprites) {
			CGSize s = CCDirector.sharedDirector().winSize();

			if( numberOfSprites == 1 ) {
				tamara.setVisible(false);
				kathia.setVisible(false);
				grossini.setPosition(CGPoint.ccp(s.width/2, s.height/2));
			} else if( numberOfSprites == 2 ) {		
				kathia.setPosition(CGPoint.ccp(s.width/3, s.height/2));
				tamara.setPosition(CGPoint.ccp(2*s.width/3, s.height/2));
				grossini.setVisible(false);
			} else if( numberOfSprites == 3 ) {
				grossini.setPosition(CGPoint.ccp(s.width/2, s.height/2));
				tamara.setPosition(CGPoint.ccp(2*s.width/3, s.height/2));
				kathia.setPosition(CGPoint.ccp(s.width/3, s.height/2));
			} else {
				ccMacros.CCLOG(LOG_TAG, "ActionsTests: Invalid number of Sprites");
			}
		}
		
		public String title() {
			return "No title";
		}

		public String subtitle() {
			return null;
		}
	}


	static class  ActionManual extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			CGSize s = CCDirector.sharedDirector().winSize();

			tamara.setScaleX(2.5f);
			tamara.setScaleY(-1.0f);
			tamara.setPosition(CGPoint.ccp(100,70));
			tamara.setOpacity(128);

			grossini.setRotation(120);
			grossini.setPosition(CGPoint.ccp(s.width/2, s.height/2));
			grossini.setColor(ccColor3B.ccc3( 255,0,0));


			kathia.setPosition(CGPoint.ccp(s.width-100, s.height/2));
			kathia.setColor(ccColor3B.ccBLUE);
		}

		public String title() {
			return "Manual Transformation";
		}
	}


	static class ActionMove extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CGSize s = CCDirector.sharedDirector().winSize();


			CCMoveTo actionTo = CCMoveTo.action(2, CGPoint.ccp(s.width-40, s.height-40));

			CCMoveBy actionBy = CCMoveBy.action(2, CGPoint.ccp(80,80));
			CCMoveBy actionByBack = actionBy.reverse();

			tamara.runAction(actionTo);
			grossini.runAction(CCSequence.actions(actionBy, actionByBack));
			kathia.runAction(CCMoveTo.action(1, CGPoint.ccp(40,40)));
		}

		public String title() {
			return "MoveTo / MoveBy";
		}
	}

	static class ActionRotate extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CCRotateTo actionTo = CCRotateTo.action(2, 45);
			CCRotateTo actionTo2 = CCRotateTo.action(2, -45);
			CCRotateTo actionTo0 = CCRotateTo.action(2, 0);
			tamara.runAction(CCSequence.actions(actionTo, actionTo0));

			CCRotateBy actionBy = CCRotateBy.action(2, 360);
			CCRotateBy actionByBack = actionBy.reverse();
			grossini.runAction(CCSequence.actions(actionBy, actionByBack));

			kathia.runAction(CCSequence.actions(actionTo2, actionTo0.copy()));
		}

		public String title() {
			return "RotateTo / RotateBy";
		}

	}

	static class ActionScale extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CCScaleTo actionTo = CCScaleTo.action(2, 0.5f);
			CCScaleBy actionBy = CCScaleBy.action(2, 2);
			CCScaleBy actionBy2 = CCScaleBy.action(2, 0.25f, 4.5f);
			CCScaleBy actionByBack = actionBy.reverse();

			tamara.runAction(actionTo);
			grossini.runAction(CCSequence.actions(actionBy, actionByBack));

			kathia.runAction(CCSequence.actions(actionBy2, actionBy2.reverse()));
		}

		public String title() {
			return "ScaleTo / ScaleBy";
		}

	}

	static class ActionJump extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			CCJumpTo actionTo = CCJumpTo.action(2, CGPoint.ccp(300,300), 50, 4);
			CCJumpBy actionBy = CCJumpBy.action(2, CGPoint.ccp(300,0), 50, 4);
			CCJumpBy actionUp = CCJumpBy.action(2, CGPoint.ccp(0,0), 8, 4);
			CCJumpBy actionByBack = actionBy.reverse();

			tamara.runAction(actionTo);
			grossini.runAction(CCSequence.actions(actionBy, actionByBack));
			kathia.runAction(CCRepeatForever.action(actionUp));
		}

		public String title() {
			return "JumpTo / JumpBy";
		}
	}

	static class ActionBezier extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			CGSize s = CCDirector.sharedDirector().winSize();

			//
			// startPosition can be any coordinate, but since the movement
			// is relative to the Bezier curve, make it (0,0)
			//

			// sprite 1
			CCBezierConfig bezier = new CCBezierConfig();
			bezier.controlPoint_1 = CGPoint.ccp(0, s.height/2);
			bezier.controlPoint_2 = CGPoint.ccp(300, -s.height/2);
			bezier.endPosition = CGPoint.ccp(300,100);

			CCBezierBy bezierForward = CCBezierBy.action(3, bezier);
			CCBezierBy bezierBack = bezierForward.reverse();	
			CCSequence seq = CCSequence.actions(bezierForward, bezierBack);
			CCRepeatForever rep = CCRepeatForever.action(seq);

			// sprite 2
			tamara.setPosition(CGPoint.ccp(80,160));
			CCBezierConfig bezier2 = new CCBezierConfig();
			bezier2.controlPoint_1 = CGPoint.ccp(100, s.height/2);
			bezier2.controlPoint_2 = CGPoint.ccp(200, -s.height/2);
			bezier2.endPosition = CGPoint.ccp(240,160);

			CCBezierTo bezierTo1 = CCBezierTo.action(2, bezier2);	

			// sprite 3
			kathia.setPosition(CGPoint.ccp(400,160));
			CCBezierTo bezierTo2 = CCBezierTo.action(2, bezier2);

			grossini.runAction(rep);
			tamara.runAction(bezierTo1);
			kathia.runAction(bezierTo2);
		}
		
		public String title() {
			return "BezierBy / BezierTo";
		}
	}


	static class ActionBlink extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(2);

			CCBlink action1 = CCBlink.action(2, 10);
			CCBlink action2 = CCBlink.action(2, 5);

			tamara.runAction(action1);
			kathia.runAction(action2);
		}
		
		public String title() {
			return "Blink";
		}
	}

	static class ActionFade extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(2);

			tamara.setOpacity(0);
			CCFadeIn action1 = CCFadeIn.action(1.0f);
			CCFadeOut action1Back = action1.reverse();

			CCFadeOut action2 = CCFadeOut.action(1.0f);
			CCFadeIn action2Back = action2.reverse();

			tamara.runAction(CCSequence.actions(action1, action1Back));
			kathia.runAction(CCSequence.actions(action2, action2Back));
		}
		
		public String title() {
			return "FadeIn / FadeOut";
		}
	}

	static class ActionTint extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(2);

			CCTintTo action1 = CCTintTo.action(2, ccColor3B.ccc3(255, 0, 255));
			CCTintBy action2 = CCTintBy.action(2, ccColor3B.ccc3(-127, -255, -127));
			CCTintBy action2Back = action2.reverse();

			tamara.runAction(action1);
			kathia.runAction(CCSequence.actions(action2, action2Back));
		}
		
		public String title() {
			return "TintTo / TintBy";
		}
	}

	static class ActionAnimate extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(1);

			CCAnimation animation = CCAnimation.animation("dance");
			for( int i=1;i<15;i++)
				animation.addFrame(String.format("grossini_dance_%02d.png", i));

			CCAnimate action = CCAnimate.action(3, animation, false);
			CCAnimate action_back = action.reverse();

			grossini.runAction(CCSequence.actions(action, action_back));
		}
		
		public String title() {
			return "Animation";
		}
	}


	static class ActionSequence extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			CCSequence action = CCSequence.actions(
				CCMoveBy.action(2, CGPoint.ccp(240,0)),
				CCRotateBy.action(2, 540)
				);

			grossini.runAction(action);
		}
		
		public String title() {
			return "Sequence: Move + Rotate";
		}
	}

	static class ActionSequence2 extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			grossini.setVisible(false);

			CCSequence action = CCSequence.actions(
				CCPlace.action(CGPoint.ccp(200,200)),
				CCShow.action(),
				CCMoveBy.action(1, CGPoint.ccp(100,0)),
				CCCallFunc.action(this, "callback1"),
				CCCallFuncN.action(this, "callback2"),
				CCCallFuncND.action(this, "callback3", Float.valueOf(1.0f))
				);

			grossini.runAction(action);
		}

		public void callback1() {
			CGSize s = CCDirector.sharedDirector().winSize();
			CCLabel label = CCLabel.makeLabel("callback 1 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*1,s.height/2));

			addChild(label);
		}

		public void callback2(Object sender) {
			CGSize s = CCDirector.sharedDirector().winSize();
			CCLabel label = CCLabel.makeLabel("callback 2 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*2,s.height/2));

			addChild(label);
		}

		public void callback3(Object sender, Object data) {
			CGSize s = CCDirector.sharedDirector().winSize();
			CCLabel label = CCLabel.makeLabel("callback 3 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*3,s.height/2));

			addChild(label);
		}

		public String title() {
			return "Sequence of InstantActions";
		}
	}

	static class ActionSpawn extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			CCAction action = CCSpawn.actions(
				CCJumpBy.action(2, CGPoint.ccp(300,0), 50, 4),
				CCRotateBy.action(2, 720));

			grossini.runAction(action);
		}
		
		public String title() {
			return "Spawn: Jump + Rotate";
		}
	}

	static class ActionRepeatForever extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(1);

			CCSequence action = CCSequence.actions(
				CCDelayTime.action(1),
				CCCallFuncN.action(this, "repeatForever")
				);

			grossini.runAction(action);
		}

		public void repeatForever(Object sender) {
			CCRepeatForever repeat = CCRepeatForever.action(CCRotateBy.action(1.0f, 360));

			((CCNode)sender).runAction(repeat);
		}

		public String title() {
			return "CallFuncN + RepeatForever";
		}
	}

	static class ActionRotateToRepeat extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(2);

			CCRotateTo act1 = CCRotateTo.action(1, 90);
			CCRotateTo act2 = CCRotateTo.action(1, 0);
			CCSequence seq = CCSequence.actions(act1, act2);
			CCRepeatForever rep1 = CCRepeatForever.action(seq);
			CCRepeat rep2 = CCRepeat.action(seq.copy(), 10);

			tamara.runAction(rep1);
			kathia.runAction(rep2);
		}

		public String title() {
			return "Repeat/RepeatForever + RotateTo";
		}
		
		public String subtitle() {
			return "You should see smooth movements (no jerks). issue #390";
		}

	}

	static class ActionRotateJerk extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(2);

			CCSequence seq = CCSequence.actions(
				CCRotateTo.action(0.5f, -20),
				CCRotateTo.action(0.5f, 20)
				);

			CCRepeat rep1 = CCRepeat.action(seq, 10);
			CCRepeatForever rep2 = CCRepeatForever.action(seq.copy());

			tamara.runAction(rep1);
			kathia.runAction(rep2);
		}

		public String title() {
			return "RepeatForever / Repeat + Rotate";
		}
		
		public String subtitle() {
			return "You should see smooth movements (no jerks). issue #390";
		}
	}


	static class ActionReverse extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			CCJumpBy jump = CCJumpBy.action(2, CGPoint.ccp(300,0), 50, 4);
			CCSequence action = CCSequence.actions(jump, jump.reverse());

			grossini.runAction(action);
		}
		
		public String title() {
			return "Reverse an action";
		}
	}

	static class ActionDelayTime extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			CCMoveBy move = CCMoveBy.action(1, CGPoint.ccp(150,0));
			CCSequence action = CCSequence.actions(move, CCDelayTime.action(2), move);

			grossini.runAction(action);
		}
		
		public String title() {
			return "DelayTime: m + delay + m";
		}
	}

	static class ActionReverseSequence extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(1);

			CCMoveBy move1 = CCMoveBy.action(1, CGPoint.ccp(250,0));
			CCMoveBy move2 = CCMoveBy.action(1, CGPoint.ccp(0,50));
			CCSequence seq = CCSequence.actions(move1, move2, move1.reverse());
			CCSequence action = CCSequence.actions(seq, seq.reverse());

			grossini.runAction(action);
		}
		
		public String title() {
			return "Reverse a sequence";
		}
	}

	static class ActionReverseSequence2 extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(2);


			// Test:
			//   Sequence should work both with IntervalAction and InstantActions

			CCMoveBy move1 = CCMoveBy.action(1, CGPoint.ccp(250,0));
			CCMoveBy move2 = CCMoveBy.action(1, CGPoint.ccp(0,50));
			CCToggleVisibility tog1 = CCToggleVisibility.action();
			CCToggleVisibility tog2 = CCToggleVisibility.action();
			CCSequence seq = CCSequence.actions(move1, tog1, move2, tog2, move1.reverse());
			CCRepeat action = CCRepeat.action(
					CCSequence.actions(seq, seq.reverse()), 3);

			// Test:
			//   Also test that the reverse of Hide is Show, and vice-versa
			kathia.runAction(action);

			CCMoveBy move_tamara = CCMoveBy.action(1, CGPoint.ccp(100,0));
			CCMoveBy move_tamara2 = CCMoveBy.action(1, CGPoint.ccp(50,0));
			CCHide hide = CCHide.action();
			CCSequence seq_tamara = CCSequence.actions(move_tamara, hide, move_tamara2);
			CCSequence seq_back = seq_tamara.reverse();
			tamara.runAction(CCSequence.actions(seq_tamara, seq_back));
		}
		
		public String title() {
			return "Reverse sequence 2";
		}
	}


	static class ActionRepeat extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			alignSpritesLeft(2);

			CCMoveBy a1 = CCMoveBy.action(1, CGPoint.ccp(150,0));
			CCRepeat action1 = CCRepeat.action(
				CCSequence.actions(CCPlace.action(CGPoint.ccp(60,60)), a1), 3);

			CCRepeatForever action2 = CCRepeatForever.action(
				CCSequence.actions( a1.copy(), a1.reverse()));

			kathia.runAction(action1);
			tamara.runAction(action2);
		}
		
		public String title() {
			return "Repeat / RepeatForever actions";
		}
	}

	static class ActionCallFunc extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CCSequence action = CCSequence.actions(
				CCMoveBy.action(2, CGPoint.ccp(200,0)),
				CCCallFunc.action(this, "callback1")
				);

			CCSequence action2 = CCSequence.actions(
				CCScaleBy.action(2, 2),
				CCFadeOut.action(2),
				CCCallFuncN.action(this, "callback2")
				);

			CCSequence action3 = CCSequence.actions(
				CCRotateBy.action(3, 360),
				CCFadeOut.action(2),
				CCCallFuncND.action(this, "callback3", Float.valueOf(1.0f))
				);

			grossini.runAction(action);
			tamara.runAction(action2);
			kathia.runAction(action3);
		}

		public void callback1() {
			// ccMacros.CCLOG(LOG_TAG,"callback 1 called");
			CGSize s = CCDirector.sharedDirector().winSize();
			CCLabel label = CCLabel.makeLabel("callback 1 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*1,s.height/2));

			addChild(label);
		}
		
		public void callback2(Object sender) {
			// ccMacros.CCLOG(LOG_TAG, "callback 2 called from:" + String.valueOf(sender));
			CGSize s = CCDirector.sharedDirector(). winSize();
			CCLabel label = CCLabel.makeLabel("callback 2 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*2,s.height/2));

			addChild(label);
		}
		
		public void callback3(Object sender, Object data) {
			// NSLog(@"callback 3 called from:%@ with data:%x",sender,(int)data);
			CGSize s = CCDirector.sharedDirector().winSize();
			CCLabel label = CCLabel.makeLabel("callback 3 called", "DroidSans", 16);
			label.setPosition(CGPoint.ccp( s.width/4*3,s.height/2));
			addChild(label);
		}

		public String title() {
			return "Callbacks: CallFunc and friends";
		}
	}

	static class ActionCallFuncND extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(1);


			CCSequence action = CCSequence.actions(
				CCMoveBy.action(2, CGPoint.ccp(200,0)),
				CCCallFuncND.action(this, "callbackND", "a different implemetation of CCCallFuncND from iphone version")
				);
			grossini.runAction(action);
		}
		
		public void callbackND(Object sender, Object data) {
			// the action runner, grossini, is the sender.
			CCSprite obj = (CCSprite)sender;
			String msg   = (String)data;
			
			obj.removeFromParentAndCleanup(true);
			ccMacros.CCLOGINFO("callbackND", msg);
		}

		public String title() {
			return "CallFuncND + auto remove";
		}

		public String subtitle() {
			return "CallFuncND + remove sprite. Grossini dissapears in 2s";
		}

	}


	static class ActionOrbit extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CCOrbitCamera orbit1 = CCOrbitCamera.action(2, 1, 0, 0, 180, 0, 0);
			CCSequence action1 = CCSequence.actions(
				orbit1,
				orbit1.reverse()
				);

			CCOrbitCamera orbit2 = CCOrbitCamera.action(2, 1, 0, 0, 180, -45, 0);
			CCSequence action2 = CCSequence.actions(
				orbit2,
				orbit2.reverse()
				);

			CCOrbitCamera orbit3 = CCOrbitCamera.action(2, 1, 0, 0, 180, 90, 0);
			CCSequence action3 = CCSequence.actions(
				orbit3,
				orbit3.reverse()
				);

			kathia.runAction(CCRepeatForever.action(action1));
			tamara.runAction(CCRepeatForever.action(action2));
			grossini.runAction(CCRepeatForever.action(action3));

			CCMoveBy move = CCMoveBy.action(3, CGPoint.ccp(100,-100));
			CCMoveBy move_back = move.reverse();
			CCSequence seq = CCSequence.actions(move, move_back);
			CCRepeatForever rfe = CCRepeatForever.action(seq);
			kathia.runAction(rfe);
			tamara.runAction(rfe.copy());
			grossini.runAction(rfe.copy());
		}

		public String title() {
			return "OrbitCamera action";
		}
	}

	static class ActionFollow extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(1);

			CGSize winSize = CCDirector.sharedDirector().winSize();

			grossini.setPosition(CGPoint.ccp(-200, winSize.height/2));

			CCMoveBy move = CCMoveBy.action(2, CGPoint.ccp(winSize.width*3,0));
			CCMoveBy move_back = move.reverse();
			CCSequence seq = CCSequence.actions(move, move_back);
			CCRepeatForever rep = CCRepeatForever.action(seq);

			grossini.runAction(rep);

			this.runAction(
				CCFollow.action(grossini, CGRect.make(0, 0, (winSize.width*2)-100, winSize.height))
			);
		}

		public void draw(GL10 gl) {
			CGSize winSize = CCDirector.sharedDirector().winSize();

			float x = winSize.width*2 - 100;
			float y = winSize.height;

			CGPoint vertices[] = {
					CGPoint.ccp(5,5), CGPoint.ccp(x-5,5),
					CGPoint.ccp(x-5,y-5), CGPoint.ccp(5,y-5)
			};
			
			CCDrawingPrimitives.ccDrawPoly(gl, vertices, 4, true);
		}

		public String title() {
			return "Follow action";
		}

		public String subtitle() {
			return "The sprite should be centered, even though it is being moved";
		}

	}

	static class ActionProperty extends ActionDemo {
		public void onEnter() {
			super.onEnter();

			centerSprites(3);

			CCPropertyAction rot = CCPropertyAction.action(2, "setRotation", 0, -270);
			CCPropertyAction rot_back = rot.reverse();
			CCSequence rot_seq = CCSequence.actions(rot, rot_back);

			CCPropertyAction scale = CCPropertyAction.action(2, "setScale", 1, 3);
			CCPropertyAction scale_back = scale.reverse();
			CCSequence scale_seq = CCSequence.actions(scale, scale_back);

			CCPropertyAction opacity = CCPropertyAction.action(2, "setOpacity", 255, 0);
			CCPropertyAction opacity_back = opacity.reverse();
			CCSequence opacity_seq = CCSequence.actions(opacity, opacity_back);

			grossini.runAction(rot_seq);
			tamara.runAction(scale_seq);
			kathia.runAction(opacity_seq);
		}

		public String title() {
			return "PropertyAction";
		}

		public String subtitle() {
			return "Simulates Rotation, Scale and Opacity using a generic action";
		}

	}

}

