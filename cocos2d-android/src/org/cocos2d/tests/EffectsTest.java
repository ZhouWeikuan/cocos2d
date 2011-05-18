package org.cocos2d.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.grid.CCFlipX3D;
import org.cocos2d.actions.grid.CCFlipY3D;
import org.cocos2d.actions.grid.CCLens3D;
import org.cocos2d.actions.grid.CCLiquid;
import org.cocos2d.actions.grid.CCPageTurn3D;
import org.cocos2d.actions.grid.CCRipple3D;
import org.cocos2d.actions.grid.CCShaky3D;
import org.cocos2d.actions.grid.CCTwirl;
import org.cocos2d.actions.grid.CCWaves;
import org.cocos2d.actions.grid.CCWaves3D;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.tile.CCFadeOutBLTiles;
import org.cocos2d.actions.tile.CCFadeOutDownTiles;
import org.cocos2d.actions.tile.CCFadeOutTRTiles;
import org.cocos2d.actions.tile.CCFadeOutUpTiles;
import org.cocos2d.actions.tile.CCJumpTiles3D;
import org.cocos2d.actions.tile.CCShakyTiles3D;
import org.cocos2d.actions.tile.CCShatteredTiles3D;
import org.cocos2d.actions.tile.CCShuffleTiles;
import org.cocos2d.actions.tile.CCSplitCols;
import org.cocos2d.actions.tile.CCSplitRows;
import org.cocos2d.actions.tile.CCTurnOffTiles;
import org.cocos2d.actions.tile.CCWavesTiles3D;
import org.cocos2d.layers.CCColorLayer;
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
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccGridSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// EffectsAdvancedTest, there is a downloadable demo here:
// http://code.google.com/p/cocos2d-android-1/downloads/detail?name=CCTextureAtlas%20and%20CCBitmapFontAtlas.3gp&can=2&q=#makechanges
//
public class EffectsTest extends Activity {
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
		scene.addChild(new TextLayer(), 0, kTagTextLayer);
				
		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

	static int actionIdx=0;
	static Class<?> actionList[] = {
		Shaky3DDemo.class,
		Waves3DDemo.class,
		FlipX3DDemo.class,
		FlipY3DDemo.class,
		Lens3DDemo.class,
		Ripple3DDemo.class,
		LiquidDemo.class,
		WavesDemo.class,
		TwirlDemo.class,
		ShakyTiles3DDemo.class,
		ShatteredTiles3DDemo.class,
		ShuffleTilesDemo.class,
		FadeOutTRTilesDemo.class,
		FadeOutBLTilesDemo.class,
		FadeOutUpTilesDemo.class,
		FadeOutDownTilesDemo.class,
		TurnOffTilesDemo.class,
		WavesTiles3DDemo.class,
		JumpTiles3DDemo.class,
		SplitRowsDemo.class,
		SplitColsDemo.class,
		PageTurn3DDemo.class,
	};
	
	static String effectsList[] = {
		"Shaky3D",
		"Waves3D",
		"FlipX3D",
		"FlipY3D",
		"Lens3D",
		"Ripple3D",
		"Liquid",
		"Waves",
		"Twirl",
		"ShakyTiles3D",
		"ShatteredTiles3D",
		"ShuffleTiles",
		"FadeOutTRTiles",
		"FadeOutBLTiles",
		"FadeOutUpTiles",
		"FadeOutDownTiles",
		"TurnOffTiles",
		"WavesTiles3D",
		"JumpTiles3D",
		"SplitRows",
		"SplitCols",
		"PageTurn3D",
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
	public static final int kTagBackground = 1;
	public static final int kTagLabel = 2;

	static Class<?> nextAction() {	
		actionIdx++;
		actionIdx = actionIdx % actionList.length;
		Class<?> c = actionList[actionIdx];
		return c;
	}

	static Class<?> backAction() {
		actionIdx--;
		int total = actionList.length;
		if( actionIdx < 0 )
			actionIdx += total;
		Class<?> c = actionList[actionIdx];
		return c;
	}

	static Class<?> restartAction() {
		Class<?> c = actionList[actionIdx];
		return c;
	}

	static class TextLayer extends CCColorLayer {
		public TextLayer() {
			super(ccColor4B.ccc4(32, 32, 32, 255));
			float x,y;

			CGSize size = CCDirector.sharedDirector().winSize();
			x = size.width;
			y = size.height;

			CCSprite bg = CCSprite.sprite("background3.png");
			bg.setPosition(CGPoint.ccp(size.width/2, size.height/2));
			
			Class<?> effectClass = restartAction();
			
			try {
				Method method = effectClass.getMethod("action", new Class[] {Float.TYPE} );
				CCIntervalAction act = (CCIntervalAction) method.invoke(null, 3.0f);
				bg.runAction(act);
			} catch (NoSuchMethodException e) {
        		e.printStackTrace();
        	} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}		
			
			addChild(bg, 0, kTagBackground);			

			CCSprite grossini = CCSprite.sprite("grossinis_sister2.png");
			bg.addChild(grossini, 1);
			grossini.setPosition(CGPoint.ccp(x/3,y/2));
			CCIntervalAction sc = CCScaleBy.action(2, 5);
			CCIntervalAction sc_back = sc.reverse();
			grossini.runAction(CCRepeatForever.action(CCSequence.actions(sc, sc_back)));

			CCSprite tamara = CCSprite.sprite("grossinis_sister1.png");
			bg.addChild(tamara, 1);
			tamara.setPosition(CGPoint.ccp(2*x/3,y/2));
			CCIntervalAction sc2 = CCScaleBy.action(2, 5);
			CCIntervalAction sc2_back = sc2.reverse();
			tamara.runAction(CCRepeatForever.action(CCSequence.actions(sc2, sc2_back)));

			CCLabel label = CCLabel.makeLabel(effectsList[actionIdx], "DroidSans", 24);

			label.setPosition(CGPoint.ccp(x/2,y-80));
			addChild(label);
			label.setTag(kTagLabel);

			// menu
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");
			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CGPoint.zero());
			item1.setPosition(CGPoint.ccp(size.width/2-100,30));
			item2.setPosition(CGPoint.ccp(size.width/2, 30));
			item3.setPosition(CGPoint.ccp(size.width/2+100,30));
			addChild(menu, 1);

			this.schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					checkAnim(d);
				}
			});
		}

		public void checkAnim(float t) {
			CCNode s2 = getChildByTag(kTagBackground);
			if ( s2.numberOfRunningActions() == 0 && s2.getGrid() != null )
				s2.setGrid(null);
		}

		public void newOrientation() {
			boolean landscape = CCDirector.sharedDirector().getLandscape();
            CCDirector.sharedDirector().setLandscape(!landscape);
		}

		public void newScene() {
			CCScene s = CCScene.node();
			CCNode child = new TextLayer();
			s.addChild(child);
			CCDirector.sharedDirector().replaceScene(s);
		}
		
		public void restartCallback(Object sender) {
			this.newOrientation();
			this.newScene();
		}

		public void nextCallback(Object sender) {
			nextAction();
			this.newScene();
		}

		public void backCallback(Object sender) {
			backAction();
			this.newScene();
		}

	}

	static class Shaky3DDemo extends CCShaky3D { 
		public Shaky3DDemo(int range, boolean sz, ccGridSize gSize, float d) {
			super(range, sz, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCShaky3D action(float t) {
			return new CCShaky3D(5, true, ccGridSize.ccg(15,10), t);
		}
	}
	
	static class Waves3DDemo extends CCWaves3D { 
		public Waves3DDemo(int wav, float amp, ccGridSize gSize, float d) {
			super(wav, amp, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCWaves3D action(float t) {
			return new CCWaves3D(5, 40, ccGridSize.ccg(15,10), t);
		}
	}
	
	static class FlipX3DDemo extends CCFlipX3D {
		public FlipX3DDemo(float duration) {
			super(duration);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCIntervalAction flipx  = CCFlipX3D.action(t);
			CCIntervalAction flipx_back = flipx.reverse();
			CCDelayTime delay = CCDelayTime.action(2);
			
			return CCSequence.actions(flipx, delay, flipx_back);	
		}
	}
	
	static class FlipY3DDemo extends CCFlipY3D {
		protected FlipY3DDemo(float duration) {
			super(duration);
			// TODO Auto-generated constructor stub
		}

		public static CCIntervalAction action(float t) {
			CCIntervalAction flipy = CCFlipY3D.action(t);
			CCIntervalAction flipy_back = flipy.reverse();
			CCDelayTime delay = CCDelayTime.action(2);
			
			return CCSequence.actions(flipy, delay, flipy_back);
		}
	}	
	
	static class Lens3DDemo extends CCLens3D {
		public Lens3DDemo(CGPoint pos, float r, ccGridSize gridSize, float d) {
			super(pos, r, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCLens3D action(float t) {
			CGSize size = CCDirector.sharedDirector().winSize();
			return CCLens3D.action(CGPoint.ccp(size.width/2,size.height/2), 240, ccGridSize.ccg(15,10), t);
		}
	}
	
	static class Ripple3DDemo extends CCRipple3D {
		public Ripple3DDemo(CGPoint pos, float r, int wav, float amp,
				ccGridSize gSize, float d) {
			super(pos, r, wav, amp, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCRipple3D action(float t) {
			CGSize size = CCDirector.sharedDirector().winSize();
			return CCRipple3D.action(CGPoint.ccp(size.width/2,size.height/2), 240, 4, 160, ccGridSize.ccg(32,24), t);
		}
	}
	
	static class LiquidDemo extends CCLiquid { 
		public LiquidDemo(int wav, float amp, ccGridSize gSize, float d) {
			super(wav, amp, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCLiquid action(float t) {
			return new CCLiquid(4, 20, ccGridSize.ccg(16,12), t);
		}
	}
	
	static class WavesDemo extends CCWaves {
		public WavesDemo(int wav, float amp, boolean h, boolean v,
				ccGridSize gSize, float d) {
			super(wav, amp, h, v, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCWaves action(float t) {
			return new CCWaves(4, 20, true, true, ccGridSize.ccg(16,12), t);
		}
	}
	
	static class TwirlDemo extends CCTwirl {
		public TwirlDemo(CGPoint pos, int t, float amp, ccGridSize gSize,
				float d) {
			super(pos, t, amp, gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCTwirl action(float t) {
			CGSize size = CCDirector.sharedDirector().winSize();
			return CCTwirl.action(CGPoint.ccp(size.width/2, size.height/2), 1, 2.5f, ccGridSize.ccg(12,8), t);
		}
	}

	static class ShakyTiles3DDemo extends CCShakyTiles3D {
		protected ShakyTiles3DDemo(int range, boolean sz, ccGridSize gridSize, float d) {
			super(range, sz, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCShakyTiles3D action(float t) {
			return CCShakyTiles3D.action(5, true, ccGridSize.ccg(16,12), t);
		}
	}
	
	static class ShatteredTiles3DDemo extends CCShatteredTiles3D { 
		public ShatteredTiles3DDemo(int range, boolean sz, ccGridSize gridSize,
				float d) {
			super(range, sz, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCShatteredTiles3D action(float t) {
			return new CCShatteredTiles3D(5, true, ccGridSize.ccg(16,12), t);
		}
	}
	
	static class ShuffleTilesDemo extends CCShuffleTiles { 
		public ShuffleTilesDemo(int s, ccGridSize gridSize, float d) {
			super(s, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCShuffleTiles shuffle = CCShuffleTiles.action(25, ccGridSize.ccg(16,12), t);
			CCIntervalAction shuffle_back = shuffle.reverse();
			CCDelayTime delay = CCDelayTime.action(2);

			return CCSequence.actions(shuffle, delay, shuffle_back);
		}
	}
	
	static class FadeOutTRTilesDemo extends CCFadeOutTRTiles { 
		protected FadeOutTRTilesDemo(ccGridSize gSize, float d) {
			super(gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCFadeOutTRTiles fadeout = CCFadeOutTRTiles.action(ccGridSize.ccg(16,12), t);
			CCIntervalAction back = fadeout.reverse();
			CCDelayTime delay = CCDelayTime.action(0.5f);

			return CCSequence.actions(fadeout, delay, back);
		}
	}
	
	static class FadeOutBLTilesDemo extends CCFadeOutBLTiles { 
		protected FadeOutBLTilesDemo(ccGridSize gSize, float d) {
			super(gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCFadeOutBLTiles fadeout = CCFadeOutBLTiles.action(ccGridSize.ccg(16,12), t);
			CCIntervalAction back = fadeout.reverse();
			CCDelayTime delay = CCDelayTime.action(0.5f);
			
			return CCSequence.actions(fadeout, delay, back);
		}
	}
	
	static class FadeOutUpTilesDemo extends CCFadeOutUpTiles { 
		protected FadeOutUpTilesDemo(ccGridSize gSize, float d) {
			super(gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence actionWithDuration(float t) {
			CCFadeOutUpTiles fadeout = CCFadeOutUpTiles.action(ccGridSize.ccg(16,12), t);
			CCIntervalAction back = fadeout.reverse();
			CCDelayTime delay = CCDelayTime.action(0.5f);
			
			return CCSequence.actions(fadeout, delay, back);
		}
	}


	static class FadeOutDownTilesDemo extends CCFadeOutDownTiles { 
		protected FadeOutDownTilesDemo(ccGridSize gSize, float d) {
			super(gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCFadeOutDownTiles fadeout = CCFadeOutDownTiles.action(ccGridSize.ccg(16,12), t);
			CCIntervalAction back = fadeout.reverse();
			CCDelayTime delay = CCDelayTime.action(0.5f);

			return CCSequence.actions(fadeout, delay, back);
		}
	}
	
	static class TurnOffTilesDemo extends CCTurnOffTiles { 
		protected TurnOffTilesDemo(int s, ccGridSize gridSize, float d) {
			super(s, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSequence action(float t) {
			CCTurnOffTiles action = CCTurnOffTiles.action(25, ccGridSize.ccg(48,32), t);
			CCIntervalAction back = action.reverse();
			CCDelayTime delay = CCDelayTime.action(0.5f);
			
			return CCSequence.actions(action, delay, back);
		}
	}
	
	static class WavesTiles3DDemo extends CCWavesTiles3D { 
		public WavesTiles3DDemo(int wav, float amp, ccGridSize gridSize, float d) {
			super(wav, amp, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCWavesTiles3D action(float t) {
			return CCWavesTiles3D.action(4, 120, ccGridSize.ccg(15,10), t);
		}
	}
	

	static class JumpTiles3DDemo extends CCJumpTiles3D { 
		protected JumpTiles3DDemo(int j, float amp, ccGridSize gridSize, float d) {
			super(j, amp, gridSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCJumpTiles3D actionWithDuration(float t) {
			return CCJumpTiles3D.action(2, 30, ccGridSize.ccg(15,10), t);
		}
	}
	
	static class SplitRowsDemo extends CCSplitRows {
		protected SplitRowsDemo(int r, float d) {
			super(r, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSplitRows action(float t) {
			return CCSplitRows.action(9, t);
		}
	}
	
	static class SplitColsDemo extends CCSplitCols {
		protected SplitColsDemo(int c, float d) {
			super(c, d);
			// TODO Auto-generated constructor stub
		}

		public static CCSplitCols action(float t) {
			return CCSplitCols.action(9, t);
		}
	}


	static class PageTurn3DDemo extends CCPageTurn3D {
		protected PageTurn3DDemo(ccGridSize gSize, float d) {
			super(gSize, d);
			// TODO Auto-generated constructor stub
		}

		public static CCPageTurn3D action(float t) {
			return CCPageTurn3D.action(ccGridSize.ccg(15,10), t);
		}
	}

}

