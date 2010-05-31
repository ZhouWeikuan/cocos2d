package org.cocos2d.tests;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.base.RepeatForever;
import org.cocos2d.actions.ease.EaseElasticOut;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.actions.interval.TintBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.layers.MultiplexLayer;
import org.cocos2d.menus.*;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCRect;
import org.cocos2d.types.CCSize;

public class MenuTest extends Activity {
    public static final int kTagMenu = 1;
    public static final int kTagMenu0 = 0;
    public static final int kTagMenu1 = 1;


    private static final String LOG_TAG = MenuTest.class.getSimpleName();
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
        Director.sharedDirector().attachInView(mGLSurfaceView);

        // set landscape mode
        Director.sharedDirector().setLandscape(false);

        // show FPS
        Director.sharedDirector().setDisplayFPS(false);

        // frames per second
        Director.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
        scene.addChild(new Layer1());

        // Make the Scene active
        Director.sharedDirector().runWithScene(scene);

    }

    @Override
    public void onPause() {
        super.onPause();

        Director.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        Director.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        TextureManager.sharedTextureManager().removeAllTextures();
    }

    static class Layer1 extends Layer {

        public Layer1() {
            MenuItemFont.setFontSize(30);
            MenuItemFont.setFontName("DroidSansMono");

            // Font Item
            // AtlasSprite Item
            AtlasSpriteManager mgr = new AtlasSpriteManager("menuitemsprite.png");
            addChild(mgr);

            AtlasSprite spriteNormal = AtlasSprite.sprite(CCRect.make(0, 23 * 2, 115, 23), mgr);
            AtlasSprite spriteSelected = AtlasSprite.sprite(CCRect.make(0, 23 * 1, 115, 23), mgr);
            AtlasSprite spriteDisabled = AtlasSprite.sprite(CCRect.make(0, 23 * 0, 115, 23), mgr);
            mgr.addChild(spriteNormal);
            mgr.addChild(spriteSelected);
            mgr.addChild(spriteDisabled);
            MenuItemSprite item1 = MenuItemAtlasSprite.item(spriteNormal, spriteSelected, spriteDisabled, this, "menuCallback");

            // Image Item
            MenuItem item2 = MenuItemImage.item("SendScoreButton.png", "SendScoreButtonPressed.png", this, "menuCallback2");

            // Label Item (LabelAtlas)
            LabelAtlas labelAtlas = LabelAtlas.label("0123456789", "fps_images.png", 16, 24, '.');
            MenuItemLabel item3 = MenuItemLabel.item(labelAtlas, this, "menuCallbackDisabled");
            item3.setDisabledColor(new CCColor3B(32, 32, 64));
            item3.setColor(new CCColor3B(200, 200, 255));


            // Font Item
            MenuItem item4 = MenuItemFont.item("I toggle enable items", this, "menuCallbackEnable");

            // Label Item (BitmapFontAtlas)
//            BitmapFontAtlas label = new BitmapFontAtlas("configuration", "bitmapFontTest3.fnt");
//            MenuItemLabel item5 = new MenuItemLabel(label, this, "menuCallbackConfig");

            // Font Item
            MenuItemFont item6 = MenuItemFont.item("Quit", this, "onQuit");

            IntervalAction color_action = TintBy.action(0.5f, (byte) 0, (byte) 255, (byte) 255);
            IntervalAction color_back = color_action.reverse();
            IntervalAction seq = Sequence.actions(color_action, color_back);
            item6.runAction(RepeatForever.action(seq));

            Menu menu = Menu.menu(item1, item2, item3, item4, /*item5,*/ item6);
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
            mgr.setPosition(menu.getPositionX(), menu.getPositionY());

            disabledItem = item3;
            disabledItem.setIsEnabled(false);

            addChild(menu);

        }

        MenuItem disabledItem;

        public void menuCallbackDisabled() {
        }

        public void menuCallbackEnable() {
            disabledItem.setIsEnabled(!disabledItem.isEnabled());
        }

        public void menuCallback() {
        }

        public void menuCallback2() {
            ((MultiplexLayer) parent).switchTo(2);
        }

        public void onQuit() {
            Director.sharedDirector().end();
        }
    }

    static class Layer2 extends Layer {
        CCPoint centeredMenu;
        boolean alignedH;

        public void menuCallbackBack() {
        }

        public void menuCallbackOpacity() {
        }

        public void menuCallbackAlign() {
        }
    }

    static class Layer3 extends Layer {
        MenuItem disabledItem;

        public void menuCallback() {
        }

        public void menuCallback2() {
        }
    }

    static class Layer4 extends Layer {
    }
}
