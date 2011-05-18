package org.cocos2d.tests;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXLayer;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.nodes.CCTileMapAtlas;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccGridSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

//
// cocos node tests
// a cocos2d example
// http://www.cocos2d-iphone.org
//
public class TileMapTest extends Activity {
    public static final String LOG_TAG = TileMapTest.class.getSimpleName();
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

    public static final int kTagTileMap = 1;

    static int sceneIdx = -1;
    static Class<?> transitions[] = {
        TMXIsoZorder.class,
        TMXOrthoZorder.class,
        TMXIsoVertexZ.class,
        TMXOrthoVertexZ.class,
        TMXOrthoTest.class,
        TMXOrthoTest2.class,
        TMXOrthoTest3.class,
        TMXOrthoTest4.class,
        TMXIsoTest.class,
        TMXIsoTest1.class,
        TMXIsoTest2.class,
        TMXUncompressedTest.class,
        TMXHexTest.class,
        TMXReadWriteTest.class,
        TMXTilesetTest.class,
        TMXOrthoObjectsTest.class,
        TMXIsoObjectsTest.class,
        TMXTilePropertyTest.class,
        TMXResizeTest.class,
        TMXIsoMoveLayer.class,
        TMXOrthoMoveLayer.class,

        TileMapTest1.class,
        TileMapEditTest.class,
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


    static class TileDemo extends  CCLayer {
        protected CCTextureAtlas atlas;

        public TileDemo() {
            super();

            this.setIsTouchEnabled(true);

            CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 24);
            addChild(label, 1);
            label.setPosition(s.width/2, s.height-50);

            String subtitle = subtitle();
            if (subtitle != null) {
                CCLabel l = CCLabel.makeLabel(subtitle, "DroidSerif", 14);
                addChild(l, 1);
                l.setPosition(s.width/2, s.height-80);
            }

            CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
            CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
            CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

            CCMenu menu = CCMenu.menu(item1, item2, item3);

            menu.setPosition(0, 0);
            item1.setPosition(s.width/2 - 100,30);
            item2.setPosition(s.width/2, 30);
            item3.setPosition(s.width/2 + 100,30);
            addChild(menu, 1);
        }

        public void registerWithTouchDispatcher() {
            // CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
        	CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event) {
		return false;
        }

        @Override
        public boolean ccTouchesCancelled(MotionEvent event) {
		return false;
        }

        @Override
        public boolean ccTouchesMoved(MotionEvent event) {
        	final int N = event.getHistorySize() - 1;
        	if (N <= 0)
        		return true;
            CGPoint touchLocation = CGPoint.make(event.getX(), event.getY());
            CGPoint prevLocation = CGPoint.make(event.getHistoricalX(N), event.getHistoricalY(N));

            touchLocation	= CCDirector.sharedDirector().convertToGL(touchLocation);
            prevLocation	= CCDirector.sharedDirector().convertToGL(prevLocation);

            CGPoint diff = CGPoint.ccpSub(touchLocation, prevLocation);

            CCNode node = getChildByTag(kTagTileMap);
            CGPoint currentPos = node.getPosition();
            node.setPosition(CGPoint.ccpAdd(currentPos, diff));
            return true;
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
            return "drag the screen";
        }
    }

    static class TileMapTest1 extends TileDemo {

        public TileMapTest1() {
            CCTileMapAtlas map = CCTileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);
            // Convert it to "alias" (GL_LINEAR filtering)
            map.getTexture().setAliasTexParameters();

            CGSize s = map.getContentSize();
            String str = String.format("ContentSize: %f, %f", s.width,s.height);
            ccMacros.CCLOG(LOG_TAG, str);

            // If you are not going to use the Map, you can free it now
            // NEW since v0.7
            map.releaseMap();

            addChild(map, 0, kTagTileMap);

            map.setAnchorPoint(0, 0.5f);

            //		id s = [ScaleBy actionWithDuration:4 scale:0.8f];
            //		id scaleBack = [s reverse];
            //
            //		id seq = [Sequence actions: s,
            //								scaleBack,
            //								nil];
            //
            //		[map runAction:[RepeatForever actionWithAction:seq]];
        }

        public String title() {
            return "TileMapAtlas";
        }

    }

    static class TileMapEditTest extends TileDemo {
        public TileMapEditTest() {
            super();

            CCTileMapAtlas map = CCTileMapAtlas.tilemap("tiles.png", "levelmap.tga", 16, 16);

            // Create an Aliased Atlas
            map.getTexture().setAliasTexParameters();

            CGSize s = map.getContentSize();
            String str = String.format("ContentSize: %f, %f", s.width, s.height);
            ccMacros.CCLOG(LOG_TAG, str);

            // If you are not going to use the Map, you can free it now
            // [tilemap releaseMap];
            // And if you are going to use, it you can access the data with:
            schedule("updateMap", 0.2f);

            addChild(map, 0, kTagTileMap);

            map.setAnchorPoint(0, 0);
            map.setPosition(-20,-200);
        }

        public void updateMap(float dt) {
            // IMPORTANT
            //   The only limitation is that you cannot change an empty, or assign an empty tile to a tile
            //   The value 0 not rendered so don't assign or change a tile with value 0

            CCTileMapAtlas tilemap = (CCTileMapAtlas)getChildByTag(kTagTileMap);

            //
            // For example you can iterate over all the tiles
            // using this code, but try to avoid the iteration
            // over all your tiles in every frame. It's very expensive
            //	for(int x=0; x < tilemap.tgaInfo->width; x++) {
            //		for(int y=0; y < tilemap.tgaInfo->height; y++) {
            //			ccColor3B c =[tilemap tileAt:ccg(x,y)];
            //			if( c.r != 0 ) {
            //				NSLog(@"%d,%d = %d", x,y,c.r);
            //			}
            //		}
            //	}

            // NEW since v0.7
            ccColor3B c = tilemap.tile(ccGridSize.ccg(13,21));
            c.r++;
            c.r %= 50;
            if( c.r==0)
                c.r=1;

            // NEW since v0.7
            tilemap.setTile(c, ccGridSize.ccg(13,21));
        }

        public String title() {
            return "Editable TileMapAtlas";
        }
    }


    static class TMXOrthoTest extends TileDemo {
        public TMXOrthoTest() {
            super();

            //
            // Test orthogonal with 3d camera and anti-alias textures
            //
            // it should not flicker. No artifacts should appear
            //

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test2.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            String str = String.format("ContentSize: %f, %f", s.width,s.height);
            ccMacros.CCLOG(LOG_TAG, str);

            for (CCNode child : map.getChildren()) {
		CCSpriteSheet css = (CCSpriteSheet)child;
                css.getTexture().setAntiAliasTexParameters();
            }

            float [] x = new float[1];
            float [] y = new float[1];
            float [] z = new float[1];
            map.getCamera().getEye(x, y, z);
            map.getCamera().setEye(x[0]-200, y[0], z[0]+300);
        }

        public void onEnter() {
            super.onEnter();
            CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection3D);
        }

        public void onExit() {
            CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection2D);
            super.onExit();
        }


        public String title() {
            return "TMX Orthogonal test";
        }
    }

    static class TMXOrthoTest2 extends TileDemo {

        public TMXOrthoTest2() {
            super();
            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test1.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            for (CCNode node : map.getChildren() ) {
		CCSpriteSheet child =  (CCSpriteSheet)node;
                child.getTexture().setAntiAliasTexParameters();
            }

            map.runAction(CCScaleBy.action(2, 0.5f));
        }

        public String title() {
            return "TMX Ortho test2";
        }
    }

    static class TMXOrthoTest3 extends TileDemo {

        public TMXOrthoTest3() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test3.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + ", " + s.height);

            for (CCNode node : map.getChildren()) {
		CCSpriteSheet child = (CCSpriteSheet)node;
                child.getTexture().setAntiAliasTexParameters();
            }

            map.setScale(0.2f);
            map.setAnchorPoint(0.5f, 0.5f);
        }

        public String title() {
            return "TMX anchorPoint test";
        }

    }

    static class TMXOrthoTest4 extends TileDemo {

        public TMXOrthoTest4() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test4.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s1 = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s1.width + ", " + s1.height);

            for (CCNode node : map.getChildren()) {
		CCSpriteSheet child = (CCSpriteSheet)node;
                child.getTexture().setAntiAliasTexParameters();
            }

            map.setAnchorPoint(0, 0);

            CCTMXLayer layer = map.layerNamed("Layer 0");
            CGSize s = layer.layerSize;

            CCSprite sprite = null;
            sprite = layer.tileAt(CGPoint.ccp(0,0));
            sprite.setScale(2);
            sprite = layer.tileAt(CGPoint.ccp(s.width-1,0));
            sprite.setScale(2);
            sprite = layer.tileAt(CGPoint.ccp(0,s.height-1));
            sprite.setScale(2);
            sprite = layer.tileAt(CGPoint.ccp(s.width-1,s.height-1));
            sprite.setScale(2);

            schedule("removeSprite", 2);
        }

        public void removeSprite(float dt) {
            unschedule("removeSprite");

            CCTMXTiledMap map = (CCTMXTiledMap) getChildByTag(kTagTileMap);
            CCTMXLayer layer = map.layerNamed("Layer 0");
            CGSize s = layer.layerSize;

            CCSprite sprite = layer.tileAt(CGPoint.ccp(s.width-1,0));
            layer.removeChild(sprite, true);
        }

        public String title() {
            return "TMX width/height test";
        }

    }

    static class TMXReadWriteTest extends TileDemo {
        int gid;
        int gid2;

        public TMXReadWriteTest() {
            super();

            gid = 0;
            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test2.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + ", " + s.height);

            CCTMXLayer layer = map.layerNamed("Layer 0");
            layer.getTexture().setAntiAliasTexParameters();

            map.setScale(1);

            CCSprite tile0 = layer.tileAt(CGPoint.ccp(1,63));
            CCSprite tile1 = layer.tileAt(CGPoint.ccp(2,63));
            CCSprite tile2 = layer.tileAt(CGPoint.ccp(1,62));
            CCSprite tile3 = layer.tileAt(CGPoint.ccp(2,62));
            tile0.setAnchorPoint(0.5f, 0.5f);
            tile1.setAnchorPoint(0.5f, 0.5f);
            tile2.setAnchorPoint(0.5f, 0.5f);
            tile3.setAnchorPoint(0.5f, 0.5f);

            CCIntervalAction move   = CCMoveBy.action(0.5f, CGPoint.ccp(0,160));
            CCIntervalAction rotate = CCRotateBy.action(2, 360);
            CCIntervalAction scale  = CCScaleBy.action(2, 5);
            CCIntervalAction opacity= CCFadeOut.action(2);
            CCIntervalAction fadein = CCFadeIn.action(2);
            CCIntervalAction scaleback = CCScaleTo.action(1, 1);
            CCCallFuncN finish = CCCallFuncN.action(this, "removeSprite");
            CCIntervalAction seq0 = CCSequence.actions(move,
                            rotate, scale, opacity,
                            fadein, scaleback, finish);
            CCIntervalAction seq1 = seq0.copy();
            CCIntervalAction seq2 = seq0.copy();
            CCIntervalAction seq3 = seq0.copy();

            tile0.runAction(seq0);
            tile1.runAction(seq1);
            tile2.runAction(seq2);
            tile3.runAction(seq3);

            gid = layer.tileGIDAt(CGPoint.ccp(0,63));
            ccMacros.CCLOG(LOG_TAG, "Tile GID at:(0,63) is: " + gid);

            schedule("updateCol", 2.0f);
            schedule("repaintWithGID", 2);
            schedule("removeTiles", 1);

            ccMacros.CCLOG(LOG_TAG, "++++atlas quantity: " + layer.getTextureAtlas().getTotalQuads());
            ccMacros.CCLOG(LOG_TAG, "++++children: " + layer.getChildren().size());

            gid2 = 0;
        }

        public void removeSprite(Object sender) {
            ccMacros.CCLOG(LOG_TAG, "removing tile: " + sender.toString());
            CCTMXLayer p = (CCTMXLayer)((CCNode)sender).getParent();
            p.removeChild((CCNode)sender, true);
            ccMacros.CCLOG(LOG_TAG, "atlas quantity: " + p.getTextureAtlas().getTotalQuads());
        }

        public void updateCol(float dt) {
            CCNode map = getChildByTag(kTagTileMap);
            CCTMXLayer layer = (CCTMXLayer) map.getChildByTag(0);

            ccMacros.CCLOG(LOG_TAG, "++++atlas quantity: " + layer.getTextureAtlas().getTotalQuads());
            ccMacros.CCLOG(LOG_TAG, "++++children: " + layer.getChildren().size());

            CGSize s = layer.layerSize;
            for( int y=0; y<s.height; y++ ) {
                layer.setTileGID(gid2, CGPoint.ccp(3,y));
            }
            gid2 = (gid2 + 1) % 80;
        }

        public void repaintWithGID(float dt) {
            CCNode map = getChildByTag(kTagTileMap);
            CCTMXLayer layer = (CCTMXLayer) map.getChildByTag(0);

            CGSize s = layer.layerSize;
            for( int x=0; x<s.width; x++) {
                int y = (int) (s.height-1);
                int tmpgid = layer.tileGIDAt(CGPoint.ccp(x,y));
                layer.setTileGID(tmpgid+1, CGPoint.ccp(x,y));
            }
        }

        public void removeTiles(float dt) {
            unschedule("removeTiles");

            CCNode map = getChildByTag(kTagTileMap);
            CCTMXLayer layer = (CCTMXLayer) map.getChildByTag(0);
            CGSize s = layer.layerSize;
            for (int y=0; y<s.height; y++) {
                layer.removeTileAt(CGPoint.ccp(5,y));
            }
        }

        public String title() {
            return "TMX Read/Write test";
        }
    }

    static class TMXHexTest extends TileDemo {

        public TMXHexTest() {
            super();

            CCColorLayer color = CCColorLayer.node(ccColor4B.ccc4(64,64,64,255));
            addChild(color, -1);

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("hexa-test.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);
        }

        public String title() {
            return "TMX Hex test";
        }

    }

    static class TMXIsoTest extends TileDemo {

        public TMXIsoTest() {
            super();

            CCColorLayer color = CCColorLayer.node(ccColor4B.ccc4(64,64,64,255));
            addChild(color, -1);

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test.tmx");
            addChild(map, 0, kTagTileMap);

            // move map to the center of the screen
            CGSize ms = map.getMapSize();
            CGSize ts = map.getTileSize();
            map.runAction(CCMoveTo.action(1.0f,
                            CGPoint.ccp(-ms.width * ts.width/2, -ms.height * ts.height/2)));
        }

        public String title() {
            return "TMX Isometric test 0";
        }

    }

    static class TMXIsoTest1 extends TileDemo {
        public TMXIsoTest1() {
            super();

            CCColorLayer color = CCColorLayer.node(ccColor4B.ccc4(64,64,64,255));
            addChild(color, -1);

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test1.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            map.setAnchorPoint(0.5f, 0.5f);
        }

        public String title() {
            return "TMX Isometric test + anchorPoint";
        }

    }

    static class TMXIsoTest2 extends TileDemo {

        public TMXIsoTest2() {
            super();

            CCColorLayer color = CCColorLayer.node(ccColor4B.ccc4(64,64,64,255));
            addChild(color, -1);

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test2.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            // move map to the center of the screen
            CGSize ms = map.getMapSize();
            CGSize ts = map.getTileSize();
            map.runAction(CCMoveTo.action(1.0f,
                    CGPoint.ccp( -ms.width * ts.width/2, -ms.height * ts.height/2)));

        }

        public String title() {
            return "TMX Isometric test 2";
        }

    }

    static class TMXUncompressedTest extends TileDemo {
        public TMXUncompressedTest() {
            super();

            CCColorLayer color = CCColorLayer.node(ccColor4B.ccc4(64,64,64,255));
            addChild(color, -1);

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test2-uncompressed.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            // move map to the center of the screen
            CGSize ms = map.getMapSize();
            CGSize ts = map.getTileSize();
            map.runAction(CCMoveTo.action(1.0f,
                    CGPoint.ccp( -ms.width * ts.width/2, -ms.height * ts.height/2 )));

            // testing release map
            for (CCNode node: map.getChildren()) {
                CCTMXLayer layer = (CCTMXLayer)node;
                layer.releaseMap();
            }

        }

        public String title() {
            return "TMX Uncompressed test";
        }

    }

    static class TMXTilesetTest extends TileDemo {
        public TMXTilesetTest() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test5.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            CCTMXLayer layer = null;
            layer = map.layerNamed("Layer 0");
            layer.getTexture().setAntiAliasTexParameters();

            layer = map.layerNamed("Layer 1");
            layer.getTexture().setAntiAliasTexParameters();

            layer = map.layerNamed("Layer 2");
            layer.getTexture().setAntiAliasTexParameters();
        }

        public String title() {
            return "TMX Tileset test";
        }

    }

    static class TMXOrthoObjectsTest extends TileDemo {
        public TMXOrthoObjectsTest() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("ortho-objects.tmx");
            addChild(map, -1, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            ccMacros.CCLOG(LOG_TAG, "----> Iterating over all the group objets");
            CCTMXObjectGroup group = map.objectGroupNamed("Object Group 1");
            for (HashMap<String, String> dict : group.objects) {
                ccMacros.CCLOG(LOG_TAG, "object: " + dict.toString());
            }

            ccMacros.CCLOG(LOG_TAG, "----> Fetching 1 object by name");
            HashMap<String, String> platform = group.objectNamed("platform");
            ccMacros.CCLOG(LOG_TAG, "platform: " + platform);
        }

        public void draw(GL10 gl) {
            CCTMXTiledMap map = (CCTMXTiledMap) getChildByTag(kTagTileMap);
            CCTMXObjectGroup group = map.objectGroupNamed("Object Group 1");
            for (HashMap<String, String> dict : group.objects) {
                int x = Integer.parseInt(dict.get("x"));
                int y = Integer.parseInt(dict.get("y"));
                int width = Integer.parseInt(dict.get("width"));
                int height = Integer.parseInt(dict.get("height"));

                gl.glLineWidth(3);

                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x,y), CGPoint.ccp(x+width,y) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x+width,y), CGPoint.ccp(x+width,y+height) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x+width,y+height), CGPoint.ccp(x,y+height) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x,y+height), CGPoint.ccp(x,y) );

                gl.glLineWidth(1);
            }
        }

        public String title() {
            return "TMX Ortho object test";
        }

        public String subtitle() {
            return "You should see a white box around the 3 platforms";
        }

    }

    static class TMXIsoObjectsTest extends TileDemo {

        public TMXIsoObjectsTest() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test-objectgroup.tmx");
            addChild(map, -1, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            CCTMXObjectGroup group = map.objectGroupNamed("Object Group 1");
            for (HashMap<String,String> dict : group.objects) {
                ccMacros.CCLOG(LOG_TAG, "object: " + dict);
            }
        }

        public void draw(GL10 gl) {
            CCTMXTiledMap map = (CCTMXTiledMap) getChildByTag(kTagTileMap);
            CCTMXObjectGroup group = map.objectGroupNamed("Object Group 1");
            for (HashMap<String, String> dict : group.objects) {
                int x = Integer.parseInt(dict.get("x"));
                int y = Integer.parseInt(dict.get("y"));
                int width = Integer.parseInt(dict.get("width"));
                int height =Integer.parseInt(dict.get("height"));

                gl.glLineWidth(3);

                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x,y),  CGPoint.ccp(x+width,y) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x+width,y), CGPoint.ccp(x+width,y+height) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x+width,y+height), CGPoint.ccp(x,y+height) );
                CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(x,y+height), CGPoint.ccp(x,y) );

                gl.glLineWidth(1);
            }
        }

        public String title() {
            return "TMX Iso object test";
        }

        public String subtitle() {
            return "You need to parse them manually. See bug #810";
        }

    }

    static class TMXResizeTest extends TileDemo {
        public TMXResizeTest() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test5.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            CCTMXLayer layer = null;
            layer = map.layerNamed("Layer 0");

            CGSize ls = layer.layerSize;
            for (int y = 0; y < ls.height; y++) {
                for (int x = 0; x < ls.width; x++) {
                    layer.setTileGID(1, CGPoint.ccp( x, y ));
                }
            }
        }

        public String title() {
            return "TMX resize test";
        }

        public String subtitle() {
            return "Should not crash. Testing issue #740";
        }
    }

    static class TMXIsoZorder extends TileDemo {
        CCSprite tamara;

        public TMXIsoZorder() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test-zorder.tmx");
            addChild(map, 0, kTagTileMap);

            map.setPosition(-1000,-50);
            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            tamara = CCSprite.sprite("grossinis_sister1.png");
            int z = (map.getChildren()!=null?map.getChildren().size():0);
            map.addChild(tamara, z);
            int mapWidth = (int) (map.getMapSize().width * map.getTileSize().width);
            tamara.setPosition( mapWidth/2, 0);
            tamara.setAnchorPoint(0.5f, 0);

            CCMoveBy move = CCMoveBy.action(10, CGPoint.ccp(300,250));
            CCMoveBy back = move.reverse();
            CCSequence seq = CCSequence.actions(move, back);
            tamara.runAction(CCRepeatForever.action(seq));

            schedule("repositionSprite");
        }

        public void repositionSprite(float dt) {
            CGPoint p = tamara.getPosition();
            CCNode map = getChildByTag(kTagTileMap);

            // there are only 4 layers. (grass and 3 trees layers)
            // if tamara < 48, z=4
            // if tamara < 96, z=3
            // if tamara < 144,z=2

            int newZ = (int) (4 - (p.y / 48));
            newZ = (newZ > 0 ? newZ : 0);

            map.reorderChild(tamara, newZ);
        }

        public String title() {
            return "TMX Iso Zorder";
        }

        public String subtitle() {
            return "Sprite should hide behind the trees";
        }

    }


    static class TMXOrthoZorder extends TileDemo {
        CCSprite tamara;

        public TMXOrthoZorder() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test-zorder.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            tamara = CCSprite.sprite("grossinis_sister1.png");
            map.addChild(tamara, map.getChildren().size());
            tamara.setAnchorPoint(0.5f,0);

            CCMoveBy move = CCMoveBy.action(10, CGPoint.ccp(400,450));
            CCMoveBy back = move.reverse();
            CCSequence seq = CCSequence.actions(move, back);
            tamara.runAction(CCRepeatForever.action(seq));

            schedule("repositionSprite");
        }

        public void repositionSprite(float dt) {
            CGPoint p = tamara.getPosition();
            CCNode map = getChildByTag(kTagTileMap);

            // there are only 4 layers. (grass and 3 trees layers)
            // if tamara < 81, z=4
            // if tamara < 162, z=3
            // if tamara < 243,z=2

            // -10: customization for this particular sample
            int newZ = (int) (4 - ( (p.y-10) / 81));
            newZ = Math.max(newZ, 0);

            map.reorderChild(tamara, newZ);
        }

        public String title() {
            return "TMX Ortho Zorder";
        }

        public String subtitle() {
            return "Sprite should hide behind the trees";
        }

    }

    static class TMXIsoVertexZ extends TileDemo {
        CCSprite tamara;

        public TMXIsoVertexZ() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test-vertexz.tmx");
            addChild(map, 0, kTagTileMap);

            map.setPosition(-700,-50);
            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            // because I'm lazy, I'm reusing a tile as an sprite, but since this method uses vertexZ, you
            // can use any CCSprite and it will work OK.
            CCTMXLayer layer = map.layerNamed("Trees");
            tamara = layer.tileAt(CGPoint.ccp(29,29));

            CCMoveBy move = CCMoveBy.action(10, CGPoint.ccp(300,250));
            CCMoveBy back = move.reverse();
            CCSequence seq = CCSequence.actions(move, back);
            tamara.runAction(CCRepeatForever.action(seq));

            schedule("repositionSprite");
        }

        public void repositionSprite(float dt) {
            // tile height is 64x32
            // map size: 30x30
            CGPoint p = tamara.getPosition();
            tamara.setVertexZ(-( (p.y+32) /16));
        }

        public void onEnter() {
            super.onEnter();

            // TIP: 2d projection should be used
            CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection2D);
        }

        public void onExit() {
            // At exit use any other projection.
            //	[[CCDirector sharedDirector] setProjection:kCCDirectorProjection3D];
            super.onExit();
        }

        public String title() {
            return "TMX Iso VertexZ";
        }

        public String subtitle() {
            return "Sprite should hide behind the trees";
        }

    }

    static class TMXOrthoVertexZ extends TileDemo {
        CCSprite tamara;

        public TMXOrthoVertexZ() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test-vertexz.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);

            // because I'm lazy, I'm reusing a tile as an sprite, but since this method uses vertexZ, you
            // can use any CCSprite and it will work OK.
            CCTMXLayer layer = map.layerNamed("trees");
            tamara = layer.tileAt(CGPoint.ccp(0,11));

            CCMoveBy move = CCMoveBy.action(10, CGPoint.ccp(400,450));
            CCMoveBy back = move.reverse();
            CCSequence seq = CCSequence.actions(move, back);
            tamara.runAction(CCRepeatForever.action(seq));

            schedule("repositionSprite");
        }

        public void repositionSprite(float dt) {
            // tile height is 101x81
            // map size: 12x12
            CGPoint p = tamara.getPosition();
            tamara.setVertexZ( -( (p.y+81) /81) );
        }

        public void onEnter() {
            super.onEnter();

            // TIP: 2d projection should be used
            CCDirector.sharedDirector().setProjection(CCDirector.kCCDirectorProjection2D);
        }

        public void onExit() {
            // At exit use any other projection.
            //	[[CCDirector sharedDirector] setProjection:kCCDirectorProjection3D];
            super.onExit();
        }

        public String title() {
            return "TMX Ortho vertexZ";
        }

        public String subtitle() {
            return "Sprite should hide behind the trees";
        }
    }

    static class TMXIsoMoveLayer extends TileDemo {
        public TMXIsoMoveLayer() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("iso-test-movelayer.tmx");
            addChild(map, 0, kTagTileMap);

            map.setPosition(-700,-50);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);
        }

        public String title() {
            return "TMX Iso Move Layer";
        }

        public String subtitle() {
            return "Trees should be horizontally aligned";
        }

    }

    static class TMXOrthoMoveLayer extends TileDemo {
        public TMXOrthoMoveLayer() {
            super();

            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("orthogonal-test-movelayer.tmx");
            addChild(map, 0, kTagTileMap);

            CGSize s = map.getContentSize();
            ccMacros.CCLOG(LOG_TAG, "ContentSize: " + s.width + "," + s.height);
        }

        public String title() {
            return "TMX Ortho Move Layer";
        }

        public String subtitle() {
            return "Trees should be horizontally aligned";
        }

    }

    static class TMXTilePropertyTest extends TileDemo {
        public TMXTilePropertyTest() {
            super();
            CCTMXTiledMap map = CCTMXTiledMap.tiledMap("ortho-tile-property.tmx");
            addChild(map, 0, kTagTileMap);

            for (int i=1;i<=20;i++){
                ccMacros.CCLOG(LOG_TAG, "GID:" + i + ", Properties:" + map.propertiesForGID(i));
            }
        }

        public String title() {
            return "TMX Tile Property Test";
        }

        public String subtitle() {
            return "In the console you should see tile properties";
        }

    }


}
