package org.cocos2d.tests;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.CCActionManager;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.camera.CCOrbitCamera;
import org.cocos2d.actions.grid.CCLens3D;
import org.cocos2d.actions.grid.CCLiquid;
import org.cocos2d.actions.grid.CCReuseGrid;
import org.cocos2d.actions.grid.CCShaky3D;
import org.cocos2d.actions.grid.CCStopGrid;
import org.cocos2d.actions.grid.CCWaves;
import org.cocos2d.actions.grid.CCWaves3D;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCReverseTime;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.tile.CCShakyTiles3D;
import org.cocos2d.actions.tile.CCShuffleTiles;
import org.cocos2d.actions.tile.CCTurnOffTiles;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccGridSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// EffectsAdvancedTest, there is a downloadable demo here:
// http://code.google.com/p/cocos2d-android-1/downloads/detail?name=CCTextureAtlas%20and%20CCBitmapFontAtlas.3gp&can=2&q=#makechanges
//
public class EffectsAdvancedTest extends Activity {
	// private static final String LOG_TAG = EffectsAdvancedTest.class.getSimpleName();
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
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = CCScene.node();
		scene.addChild(nextAction());

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

	static int sceneIdx = -1;
	static Class<?> transitions[] = {
		Effect1.class,
		Effect2.class,
		Effect3.class,
		Effect4.class,
		Effect5.class,
		Issue631.class,
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


	public static final int kTagTextLayer = 1;
	public static final int	kTagSprite1 = 1;
	public static final int kTagSprite2 = 2;
	public static final int kTagBackground = 1;
	public static final int kTagLabel = 2;

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

	static abstract class TextLayer extends CCLayer {
		public TextLayer() {
			super();

			float x,y;

			CGSize size = CCDirector.sharedDirector().winSize();
			x = size.width;
			y = size.height;

			CCSprite bg = CCSprite.sprite("background3.png");
			this.addChild(bg, 0, kTagBackground);
			
			//        		bg.anchorPoint = CGPointZero;
			bg.setPosition(CGPoint.ccp(x/2,y/2));

			CCSprite grossini = CCSprite.sprite("grossinis_sister2.png");
			bg.addChild(grossini, 1, kTagSprite1);
			grossini.setPosition(CGPoint.ccp(x/3.0f,200));
			CCIntervalAction sc = CCScaleBy.action(2.0f, 5);
			CCIntervalAction sc_back = sc.reverse();

			grossini.runAction(CCRepeatForever.action(CCSequence.actions(sc, sc_back)));

			CCSprite tamara = CCSprite.sprite("grossinis_sister1.png");
			bg.addChild(tamara, 1, kTagSprite2);
			tamara.setPosition(CGPoint.ccp(2*x/3.0f,200));
			CCIntervalAction sc2 = CCScaleBy.action(2.0f, 5);
			CCIntervalAction sc2_back = sc2.reverse();
			tamara.runAction(CCRepeatForever.action(CCSequence.actions(sc2, sc2_back)));

			CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);

			label.setPosition(CGPoint.ccp(x/2,y-40));
			addChild(label, 100);
			label.setTag(kTagLabel);

			String subtitle = subtitle();
			if( subtitle != null ) {
				CCLabel l = CCLabel.makeLabel(subtitle(), "DroidSerif", 16);
				addChild(l, 101);
				l.setPosition(CGPoint.ccp(size.width/2, size.height-80));
			}

			// menu
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");
			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CGPoint.zero());
			item1.setPosition(CGPoint.ccp(size.width/2-100,30));
			item2.setPosition(CGPoint.ccp(size.width/2, 30));
			item3.setPosition(CGPoint.ccp(size.width/2+100,30));
			addChild(menu, 101);
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

	static class Effect1 extends TextLayer {
		public void onEnter() {
			super.onEnter();

			CCNode target = getChildByTag(kTagBackground);

			// To reuse a grid the grid size and the grid type must be the same.
			// in this case:
			//     Lens3D is Grid3D and it's size is (15,10)
			//     Waves3D is Grid3D and it's size is (15,10)

			CGSize size = CCDirector.sharedDirector().winSize();
			CCIntervalAction lens = CCLens3D.action(CGPoint.ccp(size.width/2,size.height/2), 240, ccGridSize.ccg(15,10), 0.0f);
			CCIntervalAction waves = CCWaves3D.action(18, 15, ccGridSize.ccg(15,10), 10);

			CCReuseGrid reuse = CCReuseGrid.action(1);
			CCIntervalAction delay = CCDelayTime.action(8);

			CCOrbitCamera orbit = CCOrbitCamera.action(5, 1, 2, 0, 180, 0, -90);
			CCReverseTime orbit_back = orbit.reverse();

			target.runAction(CCRepeatForever.action(CCSequence.actions(orbit, orbit_back)));
			target.runAction(CCSequence.actions(lens, delay, reuse, waves));	
		}
		
		public String title() {
			return "Lens + Waves3d and CCOrbitCamera";
		}
	}

	static class Effect2 extends TextLayer {
		public void onEnter() {
			super.onEnter();

			CCNode target = getChildByTag(kTagBackground);

			// To reuse a grid the grid size and the grid type must be the same.
			// in this case:
			//     ShakyTiles is TiledGrid3D and it's size is (15,10)
			//     Shuffletiles is TiledGrid3D and it's size is (15,10)
			//	   TurnOfftiles is TiledGrid3D and it's size is (15,10)
			CCShakyTiles3D shaky = CCShakyTiles3D.action(4, false, ccGridSize.ccg(15,10), 5);
			CCShuffleTiles shuffle = CCShuffleTiles.action(0, ccGridSize.ccg(15,10), 3);
			CCTurnOffTiles turnoff = CCTurnOffTiles.action(0, ccGridSize.ccg(15,10), 3);
			CCIntervalAction turnon = turnoff.reverse();

			// reuse 2 times:
			//   1 for shuffle
			//   2 for turn off
			//   turnon tiles will use a new grid
			CCReuseGrid reuse = CCReuseGrid.action(2);

			CCDelayTime delay = CCDelayTime.action(1);

			//    	id orbit = [CCOrbitCamera actionWithDuration:5 radius:1 deltaRadius:2 angleZ:0 deltaAngleZ:180 angleX:0 deltaAngleX:-90];
			//    	id orbit_back = [orbit reverse];
			//
			//    	[target runAction: [RepeatForever actionWithAction: [Sequence actions: orbit, orbit_back, nil]]];
			target.runAction(CCSequence.actions(shaky, delay, reuse, shuffle, delay.copy(), turnoff, turnon));
		}

		public String title() {
			return "ShakyTiles + ShuffleTiles + TurnOffTiles";
		}
	}

	static class Effect3 extends TextLayer {
		public void onEnter() {
			super.onEnter();

			CCNode bg = getChildByTag(kTagBackground);
			CCNode target1 = bg.getChildByTag(kTagSprite1);
			CCNode target2 = bg.getChildByTag(kTagSprite2);	

			CCWaves waves = CCWaves.action(5, 20, true, false, ccGridSize.ccg(15,10), 5);
			CCShaky3D shaky = CCShaky3D.action(4, false, ccGridSize.ccg(15,10), 5);

			target1.runAction(CCRepeatForever.action(waves));
			target2.runAction(CCRepeatForever.action(shaky));

			// moving background. Testing issue #244
			CCMoveBy move = CCMoveBy.action(3, CGPoint.ccp(200,0));
			bg.runAction(CCRepeatForever.action(CCSequence.actions(move, move.reverse())));	
		}

		public String title() {
			return "Effects on 2 sprites";
		}
	}

	static class Effect4 extends TextLayer {
		public void onEnter() {
			super.onEnter();

			CCLens3D lens = CCLens3D.action(CGPoint.ccp(100,180), 150, ccGridSize.ccg(32,24), 10);
			//    	id move = [MoveBy actionWithDuration:5 position:ccp(400,0)];
			CCJumpBy move = CCJumpBy.action(5, CGPoint.ccp(380,0), 100, 4);
			CCJumpBy move_back = move.reverse();
			CCSequence seq = CCSequence.actions(move, move_back);
			// we should add support for CCLens3D to be action-able.
			// CCActionManager.sharedManager().addAction(seq, lens, false);
			//
			CCActionManager.sharedManager().addAction(seq, this, false);
			
			this.runAction(lens);
		}
		
		public String title() {
			return "Jumpy Lens3D";
		}
	}

	static class Effect5 extends TextLayer {
		@Override
		public void onEnter() {
			super.onEnter();

			CCIntervalAction effect = CCLiquid.action(1, 20, ccGridSize.ccg(32,24), 2);	
			CCIntervalAction delay  = CCDelayTime.action(2);
			CCSequence stopEffect = CCSequence.actions(
				effect,
				delay,
				CCStopGrid.action(),
				delay.copy(),
				effect.copy()
			);

			CCNode bg = getChildByTag(kTagBackground);
			bg.runAction(stopEffect);
		}

		public String title()
		{
			return "Test Stop-Copy-Restart";
		}
	}

	static class Issue631 extends TextLayer {
		public void onEnter() {
			super.onEnter();

			//    	id effect = [CCLiquid actionWithWaves:1 amplitude:20 grid:ccg(32,24) duration:2];
			//    	id effect = [CCShaky3D actionWithRange:16 shakeZ:NO grid:ccg(5, 5) duration:5.0f];
			CCSequence effect = CCSequence.actions(CCDelayTime.action(2.0f), CCShaky3D.action(16, false, ccGridSize.ccg(5, 5), 5.0f));

			// cleanup
			CCNode bg = getChildByTag(kTagBackground);
			removeChild(bg, true);

			// background
			CCColorLayer layer = CCColorLayer.node(ccColor4B.ccc4(255,0,0,255));
			addChild(layer, -10);
			CCSprite sprite = CCSprite.sprite("grossini.png");
			sprite.setPosition(CGPoint.ccp(50,80));
			layer.addChild(sprite, 10);

			// foreground
			CCColorLayer layer2 = CCColorLayer.node(ccColor4B.ccc4(0, 255,0,255));
			CCSprite fog = CCSprite.sprite("Fog.png");
			fog.setBlendFunc(new ccBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA));
			layer2.addChild(fog, 1);
			addChild(layer2, 1);

			layer2.runAction(CCRepeatForever.action(effect));
		}

		public String title() {
			return "Testing Opacity";
		}

		public String subtitle() {
			return "Effect image should be 100% opaque. Testing issue #631";
		}
	}
}

