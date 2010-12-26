package org.cocos2d.tests;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SchedulerTest extends Activity {
	public static final String LOG_TAG = SchedulerTest.class.getSimpleName();

	private CCGLSurfaceView mGLSurfaceView;


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
		CCDirector.sharedDirector().setLandscape(false);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = CCScene.node();
		scene.addChild(nextAction());

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

	public static final int kTagAnimationDance = 1;

	static int sceneIdx=-1;
	static Class<?> transitions[] = {
		SchedulerAutoremove.class,
		SchedulerPauseResume.class,
		SchedulerUnscheduleAll.class,
		SchedulerUnscheduleAllHard.class,
		SchedulerSchedulesAndRemove.class,
		SchedulerUpdate.class,
		SchedulerUpdateAndCustom.class,
		SchedulerUpdateFromCustom.class,
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
		try {
			Class<?> c = transitions[sceneIdx];
			return (CCLayer) c.newInstance();
		} catch (Exception e) {
			return null;
		}
	}


	static class SchedulerTestLayer extends CCLayer {
		public SchedulerTestLayer() {
			super();

			CGSize s = CCDirector.sharedDirector().winSize();

			CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
			addChild(label);
			label.setPosition(CGPoint.make(s.width / 2, s.height / 2 - 50));

			String subtitle = subtitle();
			if (subtitle != null) {
				CCLabel l = CCLabel.makeLabel(subtitle(), "DroidSans", 16);
				addChild(l, 1);
				l.setPosition(s.width/2, s.height-80);
			}

			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(0, 0);
			item1.setPosition(s.width / 2 - 100, 30);
			item2.setPosition(s.width / 2, 30);
			item3.setPosition(s.width / 2 + 100, 30);
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
			return "No Title";
		}

		public String subtitle() {
			return null;
		}
	}

	static class SchedulerAutoremove extends SchedulerTestLayer {
		float accum;

		public SchedulerAutoremove() {
			super();

			schedule("autoremove", 0.5f);
			schedule("tick", 0.5f);
			accum = 0;
		}

		public void tick(float dt) {
			ccMacros.CCLOG(LOG_TAG, "This scheduler should not be removed");
		}

		public void autoremove(float dt) {
			accum += dt;
			String s = String.format("Time: %f", accum);
			ccMacros.CCLOG(LOG_TAG, s);

			if( accum > 3 ) {
				unschedule("autoremove");
				ccMacros.CCLOG(LOG_TAG, "scheduler removed");
			}
		}

		public String title() {
			return "Self-remove an scheduler";
		}

		public String subtitle() {
			return "1 scheduler will be autoremoved in 3 seconds. See console";
		}								 
	}

	static class SchedulerPauseResume extends SchedulerTestLayer {

		public SchedulerPauseResume() {
			super();

			schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					tick1(d);
				}
			} , 0.5f);
			schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					tick2(d);
				}
			} , 1);
			schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					pause(d);
				}
			} , 3);
		}

		public void tick1(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick1");
		}

		public void tick2(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick2");
		}

		public void pause(float dt) {
			CCScheduler.sharedScheduler().pause(this);
		}

		public String title() {
			return "Pause / Resume";
		}

		public String subtitle() {
			return "Scheduler should be paused after 3 seconds. See console";
		}								 
	}

	static class SchedulerUnscheduleAll extends SchedulerTestLayer {
		public SchedulerUnscheduleAll() {
			super();

			schedule("tick1", 0.5f);
			schedule("tick2", 1);
			schedule("tick3", 1.5f);
			schedule("tick4", 1.5f);
			schedule("unscheduleAll", 4);
		}

		public String title() {
			return "Unschedule All selectors";
		}

		public String subtitle() {
			return "All scheduled selectors will be unscheduled in 4 seconds. See console";
		}								 

		public void tick1(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick1");
		}

		public void tick2(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick2");
		}

		public void tick3(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick3");
		}

		public void tick4(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick4");
		}

		public void unscheduleAll(float dt) {
			unscheduleAllSelectors();
		}
	}

	static class SchedulerUnscheduleAllHard extends SchedulerTestLayer {
		public SchedulerUnscheduleAllHard() {
			super();

			schedule("tick1", 0.5f);
			schedule("tick2", 1);
			schedule("tick3", 1.5f);
			schedule("tick4", 1.5f);
			schedule("unscheduleAll", 4);
		}

		public String title() {
			return "Unschedule All selectors #2";
		}

		public String subtitle() {
			return "Unschedules all selectors after 4s. Uses CCScheduler. See console";
		}								 

		public void tick1(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick1");
		}

		public void tick2(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick2");
		}

		public void tick3(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick3");
		}

		public void tick4(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick4");
		}

		public void unscheduleAll(float dt) {
			CCScheduler.sharedScheduler().unscheduleAllSelectors();
		}
	}

	static class SchedulerSchedulesAndRemove extends SchedulerTestLayer {
		public SchedulerSchedulesAndRemove() {
			super();

			schedule("tick1", 0.5f);
			schedule("tick2", 1);
			schedule("scheduleAndUnschedule", 4);
		}

		public String title() {
			return "Schedule from Schedule";
		}

		public String subtitle() {
			return "Will unschedule and schedule selectors in 4s. See console";
		}								 

		public void tick1(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick1");
		}

		public void tick2(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick2");
		}

		public void tick3(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick3");
		}

		public void tick4(float dt) {
			ccMacros.CCLOG(LOG_TAG, "tick4");
		}

		public void scheduleAndUnschedule(float dt) {
			unschedule("scheduleAndUnschedule");
			unschedule("tick1");
			unschedule("tick2");
			schedule("tick3", 1);
			schedule("tick4", 1);
		}
	}

	static class TestNode extends CCNode implements UpdateCallback {
		String string_;

		public TestNode(String string, int priority) {
			super();

			string_ = string;
			scheduleUpdate(priority);
		}

		public void update(float dt) {
			ccMacros.CCLOG(LOG_TAG, string_);
		}
	}

	static class SchedulerUpdate extends SchedulerTestLayer {

		public SchedulerUpdate() {
			super();

			// schedule in different order... just another test
			TestNode d = new TestNode("---", 50);
			addChild(d);

			TestNode b = new TestNode("3rd", 0);
			addChild(b);

			TestNode a = new TestNode("1st", -10);
			addChild(a);

			TestNode c = new TestNode("4th", 10);
			addChild(c);

			TestNode e = new TestNode("5th", 20);
			addChild(e);

			TestNode f = new TestNode("2nd", -5);
			addChild(f);

			schedule("removeUpdates", 4.0f);
		}

		public String title() {
			return "Schedule update with priority";
		}

		public String subtitle() {
			return "3 scheduled updates. Priority should work. Stops in 4s. See console";
		}								 

		public void removeUpdates(float dt) {
			if (getChildren() != null) {
				for (CCNode node : getChildren()) {
					node.unscheduleAllSelectors();
				}
			}
		}
	}


	static class SchedulerUpdateAndCustom extends SchedulerTestLayer implements UpdateCallback {
		public SchedulerUpdateAndCustom() {
			super();

			scheduleUpdate();
			schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					tick(d);
				}
			});
			schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					stopSelectors(d);
				}
			}, 4.0f);
		}

		public String title() {
			return "Schedule Update + custom selector";
		}

		public String subtitle() {
			return "Update + custom selector at the same time. Stops in 4s. See console";
		}								 

		public void update(float dt) {
			String s = String.format("update called:%f", dt);
			ccMacros.CCLOG(LOG_TAG, s);
		}

		public void tick(float dt) {
			String s = String.format("custom selector called:%f", dt);
			ccMacros.CCLOG(LOG_TAG, s);
		}

		public void stopSelectors(float dt) {
			unscheduleAllSelectors();
		}
	}

	static class SchedulerUpdateFromCustom extends SchedulerTestLayer implements UpdateCallback {
		public SchedulerUpdateFromCustom() {
            super();

			schedule("schedUpdate", 2.0f);
		}

		public String title() {
			return "Schedule Update in 2 sec";
		}

		public String subtitle() {
			return "Update schedules in 2 secs. Stops 2 sec later. See console";
		}								 

		public void update(float dt) {
			String s = String.format("update called:%f", dt);
			ccMacros.CCLOG(LOG_TAG, s);
		}

		public void stopUpdate(float dt){
			unscheduleUpdate();
			unschedule("stopUpdate");
		}

		public void schedUpdate(float dt) {
			unschedule("schedUpdate");
			scheduleUpdate();
			schedule("stopUpdate", 2.0f);
		}
	}
}

