package org.cocos2d.tests;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.ease.CCEaseElasticOut;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCTintBy;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCMultiplexLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemFont;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class FontTest extends Activity {
    public static final int kTagMenu = 1;
    public static final int kTagMenu0 = 0;
    public static final int kTagMenu1 = 1;


    // private static final String LOG_TAG = MenuTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;

    public static final int kTagSpriteManager = 1;

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

        CCMultiplexLayer layer = CCMultiplexLayer.node(new Layer1(),
                                    new Layer2(), new Layer3(), new Layer4());
        scene.addChild(layer, 0);
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


    static class Layer1 extends CCLayer {
    	CCMenuItem disabledItem;
    	
        public Layer1() {
            super();

            CCMenuItemFont.setFontSize(30);
            CCMenuItemFont.setFontName("DroidSans");

            // Font Item

            CCSprite spriteNormal = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*2,115,23));
            CCSprite spriteSelected = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*1,115,23));
            CCSprite spriteDisabled = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*0,115,23));
            CCMenuItemSprite item1 = CCMenuItemSprite.item(spriteNormal, spriteSelected, spriteDisabled, this, "menuCallback");

            // Image Item
            CCMenuItem item2 = CCMenuItemImage.item("SendScoreButton.png", "SendScoreButtonPressed.png", this, "menuCallback2");

            // Label Item (LabelAtlas)
            CCLabelAtlas labelAtlas = CCLabelAtlas.label("0123456789", "fps_images.png", 16, 24, '.');
            CCMenuItemLabel item3 = CCMenuItemLabel.item(labelAtlas, this, "menuCallbackDisabled");
            item3.setDisabledColor(ccColor3B.ccc3(32, 32, 64));
            item3.setColor(ccColor3B.ccc3(200,200,255));

            // Font Item
            CCMenuItem item4 = CCMenuItemFont.item("I toggle enable items", this, "menuCallbackEnable");

            // Label Item (BitmapFontAtlas)
            CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("configuration", "bitmapFontTest3.fnt");
            CCMenuItemLabel item5 = CCMenuItemLabel.item(label, this, "menuCallbackConfig");

            // Testing issue #500
            item5.setScale(0.8f);

            // Font Item
            CCMenuItemFont item6 = CCMenuItemFont.item("Quit", this, "onQuit");

            CCIntervalAction color_action = CCTintBy.action(0.5f, ccColor3B.ccc3(0, -255, -255));
            CCIntervalAction color_back = color_action.reverse();
            CCIntervalAction seq = CCSequence.actions(color_action, color_back);
            item6.runAction(CCRepeatForever.action(seq));

            CCMenu menu = CCMenu.menu(item1, item2, item3, item4, item5, item6);
            menu.alignItemsVertically();

            // elastic effect
            CGSize s = CCDirector.sharedDirector().winSize();
            int i=0;
            for (CCNode child : menu.getChildren()) {
                final CGPoint dstPoint = child.getPositionRef();
                int offset = (int) (s.width/2 + 20);
                if( i % 2 == 0)
                    offset = -offset;
                child.setPosition( dstPoint.x + offset, dstPoint.y);
                child.runAction( 
                    CCEaseElasticOut.action(CCMoveBy.action(2.0f, CGPoint.ccp(dstPoint.x - offset,0)),
                    		0.35f)
                    );
                i++;
            }

            disabledItem = item3;
            disabledItem.setIsEnabled(false);

            addChild(menu);
        }

        public void menuCallback(Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(1);
        }

        public void menuCallbackConfig(Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(3);
        }

        public void menuCallbackDisabled(Object sender) {
        }

        public void menuCallbackEnable(Object sender) {
            disabledItem.setIsEnabled(!disabledItem.isEnabled());
        }

        public void menuCallback2(Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(2);
        }

        public void onQuit (Object sender) {
        	ccMacros.CC_DIRECTOR_END();

        	System.exit(0);
        }
    }


    static class Layer2 extends CCLayer {
    	CGPoint centeredMenu;
    	boolean alignedH;
    	
        public void alignMenusH() {
            for(int i=0;i<2;i++) {
                CCMenu menu = (CCMenu) getChildByTag(100+i);
                menu.setPosition(centeredMenu);
                if(i==0) {
                    // TIP: if no padding, padding = 5
                    menu.alignItemsHorizontally();			
                    final CGPoint p = menu.getPositionRef();
                    menu.setPosition(CGPoint.ccpAdd(p, CGPoint.ccp(0,30)));
                } else {
                    // TIP: but padding is configurable
                    menu.alignItemsHorizontally(40);
                    final CGPoint p = menu.getPositionRef();
                    menu.setPosition(CGPoint.ccpSub(p, CGPoint.ccp(0,30)));
                }
            }
        }

        public void alignMenusV() {
            for(int i=0;i<2;i++) {
                CCMenu menu = (CCMenu) getChildByTag(100+i);
                menu.setPosition(centeredMenu);
                if(i==0) {
                    // TIP: if no padding, padding = 5
                    menu.alignItemsVertically();			
                    final CGPoint p = menu.getPositionRef();
                    menu.setPosition(CGPoint.ccpAdd(p, CGPoint.ccp(100,0)));			
                } else {
                    // TIP: but padding is configurable
                    menu.alignItemsVertically(40);	
                    final CGPoint p = menu.getPositionRef();
                    menu.setPosition(CGPoint.ccpSub(p, CGPoint.ccp(100,0)));
                }
            }
        }

        public Layer2() {
            super();

        	for( int i=0;i < 2;i++ ) {
        		CCMenuItemImage item1 = CCMenuItemImage.item("btn-play-normal.png", "btn-play-selected.png", this, "menuCallbackBack");
        		CCMenuItemImage item2 = CCMenuItemImage.item("btn-highscores-normal.png", "btn-highscores-selected.png", this, "menuCallbackOpacity");
        		CCMenuItemImage item3 = CCMenuItemImage.item("btn-about-normal.png", "btn-about-selected.png", this, "menuCallbackAlign");

        		item1.setScaleX(1.5f);
        		item2.setScaleY(0.5f);
        		item3.setScaleX(0.5f);

        		CCMenu menu = CCMenu.menu(item1, item2, item3);

        		menu.setTag(kTagMenu);

        		addChild(menu, 0, 100+i);
        		centeredMenu = menu.getPosition();
        	}

        	alignedH = true;
        	alignMenusH();
        }

        public void menuCallbackBack (Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(0);
        }

        public void menuCallbackOpacity (Object sender) {
        	CCMenuItem item = (CCMenuItem)sender;
            CCMenu menu = (CCMenu)item.getParent();
            int opacity = menu.getOpacity();
            if (opacity == 128)
                menu.setOpacity((byte) 255);
            else
                menu.setOpacity((byte) 128);	
        }

        public void menuCallbackAlign (Object sender) {
            alignedH = ! alignedH;

            if( alignedH )
                alignMenusH();
            else
                alignMenusV();
        }
    }

    static class Layer3 extends CCLayer {
        CCMenuItem disabledItem;

        public Layer3() {
            super();

            CCMenuItemFont.setFontName("DroidSans");
            CCMenuItemFont.setFontSize(28);

            CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("Enable AtlasItem", "bitmapFontTest3.fnt");
            CCMenuItemLabel item1 = CCMenuItemLabel.item(label, this, "menuCallback2");
            CCMenuItemFont item2 = CCMenuItemFont.item("--- Go Back ---", this, "menuCallback");
            
            CCSprite spriteNormal = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*2,115,23));
            CCSprite spriteSelected = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*1,115,23));
            CCSprite spriteDisabled = CCSprite.sprite("menuitemsprite.png", CGRect.make(0,23*0,115,23));
            
            CCMenuItemSprite item3 = CCMenuItemSprite.item(spriteNormal, spriteSelected, spriteDisabled, this, "menuCallback3");
            disabledItem = item3;
            disabledItem.setIsEnabled(false);
            
            CCMenu menu = CCMenu.menu(item1, item2, item3);	
            menu.setPosition(0,0);
            
            CGSize s = CCDirector.sharedDirector().winSize();
            
            item1.setPosition(s.width/2 - 150, s.height/2);
            item2.setPosition(s.width/2 - 200, s.height/2);
            item3.setPosition(s.width/2, s.height/2 - 100);
            
            CCIntervalAction jump = CCJumpBy.action(3.0f, CGPoint.ccp(400,0), 50, 4);
            item2.runAction(
                CCRepeatForever.action(CCSequence.actions(jump, jump.reverse()))
            );
            CCIntervalAction spin1 = CCRotateBy.action(3.0f, 360);
            CCIntervalAction spin2 = spin1.copy();
            CCIntervalAction spin3 = spin1.copy();
            
            item1.runAction(CCRepeatForever.action(spin1));
            item2.runAction(CCRepeatForever.action(spin2));
            item3.runAction(CCRepeatForever.action(spin3));
            
            addChild(menu);
        }

        public void menuCallback(Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(0);
        }

        public void menuCallback2(Object sender) {
            ccMacros.CCLOG("menuTest", "Label clicked. Toogling Sprite");
            disabledItem.setIsEnabled(!disabledItem.isEnabled());
            disabledItem.stopAllActions();
        }

        public void menuCallback3(Object sender) {
            ccMacros.CCLOG("menuTest", "MenuItemSprite clicked");
        }
    }

    static class Layer4 extends CCLayer {
        public Layer4() {
            super();
        	
            CCMenuItemFont.setFontName("DroidSerif");
            CCMenuItemFont.setFontSize(18);
            CCMenuItemFont title1 = CCMenuItemFont.item("Sound");
            title1.setIsEnabled(false);

            CCMenuItemFont.setFontName("DroidSans");
            CCMenuItemFont.setFontSize(34);
            CCMenuItemToggle item1 = CCMenuItemToggle.item(this, "menuCallback",
                CCMenuItemFont.item("On"),
                CCMenuItemFont.item("Off")
            );

            CCMenuItemFont.setFontName("DroidSerif");
            CCMenuItemFont.setFontSize(18);
            CCMenuItemFont title2 = CCMenuItemFont.item("Music");
            title2.setIsEnabled(false);

            CCMenuItemFont.setFontName("DroidSans");
            CCMenuItemFont.setFontSize(34);
            CCMenuItemToggle item2 = CCMenuItemToggle.item(this, "menuCallback",
                CCMenuItemFont.item("On"),
                CCMenuItemFont.item("Off")
            );

            CCMenuItemFont.setFontName("DroidSerif");
            CCMenuItemFont.setFontSize(18);
            CCMenuItemFont title3 = CCMenuItemFont.item("Quality");
            title3.setIsEnabled(false);

            CCMenuItemFont.setFontName("DroidSans");
            CCMenuItemFont.setFontSize(34);
            CCMenuItemToggle item3 = CCMenuItemToggle.item(this, "menuCallback",
                CCMenuItemFont.item("High"),
                CCMenuItemFont.item("Low")
            );

            CCMenuItemFont.setFontName("DroidSerif");
            CCMenuItemFont.setFontSize(18);
            CCMenuItemFont title4 = CCMenuItemFont.item("Orientation");
            title4.setIsEnabled(false);

            CCMenuItemFont.setFontName("DroidSans");
            CCMenuItemFont.setFontSize(34);
            CCMenuItemToggle item4 = CCMenuItemToggle.item(this, "menuCallback", CCMenuItemFont.item("Off"));

            ArrayList<CCMenuItemFont> more_items = new ArrayList<CCMenuItemFont>();
            more_items.add(CCMenuItemFont.item("33%"));
            more_items.add(CCMenuItemFont.item("66%"));
            more_items.add(CCMenuItemFont.item("100%"));

            // TIP: you can manipulate the items like any other NSMutableArray
            item4.getSubItemsRef().addAll(more_items);
            
            // you can change the one of the items by doing this
            item4.setSelectedIndex(2);

            CCMenuItemFont.setFontName("DroidSerif");
            CCMenuItemFont.setFontSize(34);
            CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("go back", "bitmapFontTest3.fnt");
            CCMenuItemLabel back = CCMenuItemLabel.item(label, this, "backCallback");

            CCMenu menu = CCMenu.menu(title1, title2, item1, item2, title3, title4, item3, item4, back); // 9 items.
            menu.alignItemsInColumns(new int[]{2, 2, 2, 2, 1});

            addChild(menu);
        }

        public void menuCallback (Object sender) {
        	// CCMenuItem item = (CCMenuItem)sender;
        	// String str = String.format("selected item: %s index:%d", item.selectedItem, item.selectedIndex);
            ccMacros.CCLOG("menuTest", "selected item");
        }

        public void backCallback (Object sender) {
            ((CCMultiplexLayer)getParent()).switchTo(0);
        }
    }
}

