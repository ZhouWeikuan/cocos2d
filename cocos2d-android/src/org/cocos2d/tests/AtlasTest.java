package org.cocos2d.tests;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.*;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.types.*;
import org.cocos2d.utils.CCFormatter;

import javax.microedition.khronos.opengles.GL10;

public class AtlasTest extends Activity {
    private static final String LOG_TAG = AtlasTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLSurfaceView = new CCGLSurfaceView(this);
        CCDirector director = CCDirector.sharedDirector();
        director.attachInView(mGLSurfaceView);
        director.setLandscape(true);
        setContentView(mGLSurfaceView);
    }

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
            Atlas1.class,
            Atlas2.class,
            Atlas3.class,
            Atlas4.class,
    };

    @Override
    public void onStart() {
        super.onStart();
        // show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = Scene.node();
        scene.addChild(nextAction());

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

    public static final int kTagTileMap = 1;
    public static final int kTagSpriteManager = 1;
    public static final int kTagAnimation1 = 1;

    enum TagSprites {
        kTagSprite1,
        kTagSprite2,
        kTagSprite3,
        kTagSprite4,
        kTagSprite5,
        kTagSprite6,
        kTagSprite7,
        kTagSprite8,
    }

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

    static abstract class AtlasDemo extends CCLayer {
        CCTextureAtlas atlas;

        public AtlasDemo() {

        	CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 32);
            addChild(label, 1);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2));

            MenuItemImage item1 = MenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            MenuItemImage item2 = MenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            MenuItemImage item3 = MenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            Menu menu = Menu.menu(item1, item2, item3);

            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
            addChild(menu, 1);
        }

        public static void restartCallback() {
            Scene s = Scene.node();
            s.addChild(restartAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            Scene s = Scene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            Scene s = Scene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public abstract String title();
    }

    static class Atlas1 extends AtlasDemo {
        CCTextureAtlas textureAtlas;

        public Atlas1() {
            textureAtlas = new CCTextureAtlas("atlastest.png", 3);

            ccQuad2 texCoords[] = new ccQuad2[]{
                    new ccQuad2(0.0f, 0.2f, 0.5f, 0.2f, 0.0f, 0.0f, 0.5f, 0.0f),
                    new ccQuad2(0.2f, 0.6f, 0.6f, 0.6f, 0.2f, 0.2f, 0.6f, 0.2f),
                    new ccQuad2(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f),
            };

            ccQuad3 vertices[] = new ccQuad3[]{
                    new ccQuad3(40, 40, 0, 120, 80, 0, 40, 160, 0, 160, 160, 0),
                    new ccQuad3(240, 80, 0, 480, 80, 0, 180, 120, 0, 420, 120, 0),
                    new ccQuad3(240, 140, 0, 360, 200, 0, 240, 250, 0, 360, 310, 0),
            };

            for (int i = 0; i < 3; i++) {
                textureAtlas.updateQuad(texCoords[i], vertices[i], i);
            }

            textureAtlas.removeQuad(2);
        }

        public void draw(GL10 gl) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            gl.glEnable(GL10.GL_TEXTURE_2D);

            textureAtlas.drawQuads(gl);

//	textureAtlas_.draw(gl, 3);

            gl.glDisable(GL10.GL_TEXTURE_2D);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        @Override
        public String title() {
            return "Atlas: TextureAtlas";
        }
    }

    static class Atlas2 extends AtlasDemo {
        CCLabelAtlas label;
        float time;

        public Atlas2() {
        	//The size of the texture should be a power of 2
            label = CCLabelAtlas.label("123 Test", "tuffy_bold_italic-charmap_s.png", 16, 21, ' ');

            addChild(label);

            label.setPosition(CGPoint.make(10, 100));

            schedule("step");

        }

        public void step(float dt) {
            time += dt;
            label.setString(new CCFormatter().format("%2.2f Test", time));
        }

        @Override
        public String title() {
            return "Atlas: LabelAtlas";
        }
    }

    static class Atlas3 extends AtlasDemo {
        public Atlas3() {
            Log.i(LOG_TAG, "Atlas3 starts");

            // TODO: This needs to be done when binding
            // Create an aliased Atlas
            CCTexture2D.saveTexParameters();
            CCTexture2D.setAliasTexParameters();

            TileMapAtlas tilemap = TileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);

            CCTexture2D.restoreTexParameters();

            addChild(tilemap, 0, kTagTileMap);

            tilemap.setAnchorPoint(CGPoint.make(0, 0));

            CCIntervalAction s = CCScaleBy.action(4, 0.8f);
            CCIntervalAction scaleBack = s.reverse();
            CCIntervalAction go = CCMoveBy.action(8, CGPoint.make(-1650, 0));
            CCIntervalAction goBack = go.reverse();

            CCIntervalAction seq = CCSequence.actions(s,
                    go,
                    goBack,
                    scaleBack);

            tilemap.runAction(seq);
            Log.i(LOG_TAG, "Atlas3 ends");
        }

        @Override
        public String title() {
            return "Atlas: TileMapAtlas";
        }
    }

    static class Atlas4 extends AtlasDemo {
        public Atlas4() {
            Log.i(LOG_TAG, "Atlas4 starts");

            // Create an Aliased Atlas
            CCTexture2D.saveTexParameters();
            CCTexture2D.setAliasTexParameters();

            TileMapAtlas tilemap = TileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);

            CCTexture2D.restoreTexParameters();

            // If you are not going to use the Map, you can free it now
            // tilemap.releaseMap();
            // And if you are going to use, it you can access the data with:
            schedule("updateMap", 0.2f);

            addChild(tilemap, 0, kTagTileMap);

            tilemap.setAnchorPoint(CGPoint.make(0, 0));
            tilemap.setPosition(CGPoint.make(-20, -200));

            Log.i(LOG_TAG, "Atlas4 starts");

        }

        public void updateMap(float dt) {
            // IMPORTANT
            //   The only limitation is that you cannot change an empty, or assign an empty tile to a tile
            //   The value 0 is not rendered so don't assign or change a tile with value 0

            TileMapAtlas tilemap = (TileMapAtlas) getChild(kTagTileMap);

            // For example you can iterate over all the tiles
            // using this code, but try to avoid the iteration
            // over all your tiles in every frame. It's very expensive
//            	for(int x=0; x < tilemap.tgaInfo.width; x++) {
//            		for(int y=0; y < tilemap.tgaInfo.height; y++) {
//            			CCRGBB c = tilemap.tile(CCGridSize.ccg(x,y));
//            			if( c.r != 0 ) {
//            				Log.w(null, "%d,%d = %d", x,y,c.r);
//            			}
//            		}
//            	}

            CCRGBB c = tilemap.tile(ccGridSize.ccg(13, 21));
            c.r++;
            c.r %= 50;
            if (c.r == 0)
                c.r = 1;

            tilemap.setTile(c, ccGridSize.ccg(13, 21));

        }

        @Override
        public String title() {
            return "Atlas: Editable TileMapAtlas";
        }
    }

}
