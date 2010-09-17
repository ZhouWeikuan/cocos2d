package org.cocos2d.tests;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCTintBy;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCMultiplexLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemFont;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MenuTest extends Activity {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean landscape = (newConfig.orientation != Configuration.ORIENTATION_UNDEFINED &&
                ((newConfig.orientation & Configuration.ORIENTATION_LANDSCAPE) != 0));
//        Director.sharedDirector().setLandscape(landscape);
    }

    @Override
    public void onStart() {
        super.onStart();

        // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        CCDirector.sharedDirector().setLandscape(false);

        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(false);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = CCScene.node();
        scene.addChild(new Layer1());

        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);

    }

    @Override
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CCTextureCache.sharedTextureCache().removeAllTextures();
    }

    static class Layer1 extends CCLayer {

        public Layer1() {
            CCMenuItemFont.setFontSize(30);
            CCMenuItemFont.setFontName("DroidSansMono");

            // Font Item
            // AtlasSprite Item
            CCSpriteFrameCache mgr = new CCSpriteFrameCache("menuitemsprite.png");
            addChild(mgr);

            AtlasSprite spriteNormal = AtlasSprite.sprite(CGRect.make(0, 23 * 2, 115, 23), mgr);
            AtlasSprite spriteSelected = AtlasSprite.sprite(CGRect.make(0, 23 * 1, 115, 23), mgr);
            AtlasSprite spriteDisabled = AtlasSprite.sprite(CGRect.make(0, 23 * 0, 115, 23), mgr);
            mgr.addChild(spriteNormal);
            mgr.addChild(spriteSelected);
            mgr.addChild(spriteDisabled);
            CCMenuItemSprite item1 = CCMenuItemSprite.item(spriteNormal, spriteSelected, spriteDisabled, this, "menuCallback");

            // Image Item
            CCMenuItem item2 = CCMenuItemImage.item("SendScoreButton.png", "SendScoreButtonPressed.png", this, "menuCallback2");

            // Label Item (LabelAtlas)
            CCLabelAtlas labelAtlas = CCLabelAtlas.label("0123456789", "fps_images.png", 16, 24, '.');
            CCMenuItemLabel item3 = CCMenuItemLabel.item(labelAtlas, this, "menuCallbackDisabled");
            item3.setDisabledColor(new ccColor3B(32, 32, 64));
            item3.setColor(new ccColor3B(200, 200, 255));


            // Font Item
            toggleItem = CCMenuItemLabel.item("I toggle enable items", this, "menuCallbackEnable");
            CCMenuItem item4 = toggleItem;
            // Label Item (BitmapFontAtlas)
//            BitmapFontAtlas label = new BitmapFontAtlas("configuration", "bitmapFontTest3.fnt");
//            MenuItemLabel item5 = new MenuItemLabel(label, this, "menuCallbackConfig");

            // Font Item
            CCMenuItemFont item6 = CCMenuItemFont.item("Quit", this, "onQuit");

            CCIntervalAction color_action = CCTintBy.action(0.5f, ccColor3B.ccc3((byte) 0, (byte) 255, (byte) 255));
            CCIntervalAction color_back = color_action.reverse();
            CCIntervalAction seq = CCSequence.actions(color_action, color_back);
            item6.runAction(CCRepeatForever.action(seq));

            CCMenu menu = CCMenu.menu(item1, item2, item3, item4, /*item5,*/ item6);
            menu.alignItemsVertically();


            // elastic effect
//            CCSize s = Director.sharedDirector().winSize();
//            int i = 0;
//            for (CocosNode child : menu.getChildren()) {
//                int offset = (int) (s.width / 2 + 50);
//                if (i % 2 == 0)
//                    offset = -offset;
//                child.setPosition(child.getPositionX() + offset, child.getPositionY());
//                child.runAction(EaseElasticOut.action(MoveBy.action(2, child.getPositionX() - offset, 0), 0.35f));
//                i++;
//            }


            // IMPORTANT
            // If you are going to use AtlasSprite as items, you should
            // re-position the AtlasSpriteManager AFTER modifying the menu position
            mgr.setPosition(menu.getPosition());

            disabledItem = item3;
            disabledItem.setIsEnabled(false);

            addChild(menu);

        }

        CCMenuItem disabledItem;
        CCMenuItemLabel toggleItem;

        public void menuCallbackDisabled() {
        }

        public void menuCallbackEnable() {
            disabledItem.setIsEnabled(!disabledItem.isEnabled());
            if (disabledItem.isEnabled()) {
            	toggleItem.getLabel().setString("I toggle disable items");
            } else {
            	toggleItem.getLabel().setString("I toggle enable items");
            }
        }

        public void menuCallback() {
        }

        public void menuCallback2() {
            ((CCMultiplexLayer) parent_).switchTo(2);
        }

        public void onQuit() {
            CCDirector.sharedDirector().end();
        }
    }

    static class Layer2 extends CCLayer {
        CGPoint centeredMenu;
        boolean alignedH;

        public void menuCallbackBack() {
        }

        public void menuCallbackOpacity() {
        }

        public void menuCallbackAlign() {
        }
    }

    static class Layer3 extends CCLayer {
        CCMenuItem disabledItem;

        public void menuCallback() {
        }

        public void menuCallback2() {
        }
    }

    static class Layer4 extends CCLayer {
    }
}
