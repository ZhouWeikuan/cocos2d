package org.cocos2d.tests;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTextureAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.CCFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// AtlasTest, there is a downloadable demo here:
// http://code.google.com/p/cocos2d-android-1/downloads/detail?name=CCTextureAtlas%20and%20CCBitmapFontAtlas.3gp&can=2&q=#makechanges
//
public class AtlasTest extends Activity {
    // private static final String LOG_TAG = AtlasTest.class.getSimpleName();
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
    	Atlas1.class,
    	LabelAtlasTest.class,
    	LabelAtlasColorTest.class,
    	Atlas3.class,
    	Atlas4.class,
    	Atlas5.class,
    	Atlas6.class,
    	AtlasBitmapColor.class,
    	AtlasFastBitmap.class,
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

    public static final int kTagTileMap = 1;
    public static final int kTagSpriteManager = 1;
    public static final int kTagAnimation1 = 1;
    public static final int kTagBitmapAtlas1 = 1;
    public static final int kTagBitmapAtlas2 = 2;
    public static final int kTagBitmapAtlas3 = 3;

    public static final int kTagSprite1 = 0;
    public static final int kTagSprite2 = 1;
    public static final int kTagSprite3 = 2;
    public static final int kTagSprite4 = 3;
    public static final int kTagSprite5 = 4;
    public static final int kTagSprite6 = 5;
    public static final int kTagSprite7 = 6;
    public static final int kTagSprite8 = 7;    

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

    static abstract class AtlasDemo extends CCLayer {
        CCTextureAtlas atlas;

        public AtlasDemo() {

        	CGSize s = CCDirector.sharedDirector().winSize();

            CCLabel label = CCLabel.makeLabel(title(), "DroidSans", 32);
            addChild(label, 1);
            label.setPosition(CGPoint.make(s.width / 2, s.height / 2 - 50));
            
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

            menu.setPosition(CGPoint.make(0, 0));
            item1.setPosition(CGPoint.make(s.width / 2 - 100, 30));
            item2.setPosition(CGPoint.make(s.width / 2, 30));
            item3.setPosition(CGPoint.make(s.width / 2 + 100, 30));
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
        	return "No title";
        }

        public String subtitle() {
        	return null;
        }
    }

    static class Atlas1 extends AtlasDemo {
        CCTextureAtlas textureAtlas;

        public Atlas1() {
        	super();
        	
            textureAtlas = new CCTextureAtlas("atlastest.png", 3);
            CGSize s = CCDirector.sharedDirector().winSize();

            ccQuad2 texCoords[] = new ccQuad2[]{
                    new ccQuad2(0.0f,1.0f, 1.0f,1.0f, 0.0f,0.0f, 1.0f,0.0f),
                    new ccQuad2(0.0f,0.2f, 0.5f,0.2f, 0.0f,0.0f, 0.5f,0.0f),
                    new ccQuad2(0.0f,1.0f, 1.0f,1.0f, 0.0f,0.0f, 1.0f,0.0f),
            };

            ccQuad3 vertices[] = new ccQuad3[]{
                    new ccQuad3(0,0,0, s.width,0,0, 0,s.height,0, s.width,s.height,0),
                    new ccQuad3(40,40,0, 120,80,0, 40,160,0, 160,160,0),
                    new ccQuad3(s.width/2,40,0, s.width,40,0, s.width/2-50,200,0, s.width,100,0),
            };
            
            ccColor4B colors[][] = new ccColor4B[][] {
        		{ ccColor4B.ccc4(0,0,255,255), ccColor4B.ccc4(0,0,255,0),
        			ccColor4B.ccc4(0,0,255,0), ccColor4B.ccc4(0,0,255,255) },
        		{ ccColor4B.ccc4(255,255,255,255), ccColor4B.ccc4(255,0,0,255),
        			ccColor4B.ccc4(255,255,255,255), ccColor4B.ccc4(0,255,0,255) },
        		{ ccColor4B.ccc4(255,0,0,255), ccColor4B.ccc4(0,255,0,255), 
        			ccColor4B.ccc4(0,0,255,255), ccColor4B.ccc4(255,255,0,255) },
            };

            for (int i = 0; i < 3; i++) {
                textureAtlas.updateQuad(texCoords[i], vertices[i], i);
                textureAtlas.updateColor(colors[i], i);
            }
        }

        public void draw(GL10 gl) {
        	// Default client GL state:
        	// GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        	// GL_TEXTURE_2D

        	textureAtlas.drawQuads(gl);

        	// [textureAtlas drawNumberOfQuads:3];
        }

        @Override
        public String title() {
            return "CCTextureAtlas Atlas1";
        }
        
        @Override
        public String subtitle() {
        	return "Manual creation of CCTextureAtlas";
        }

    }

    static class LabelAtlasTest extends AtlasDemo {
        float time;

        public LabelAtlasTest() {
            super();
        	
            CCLabelAtlas label1 = CCLabelAtlas.label("123 Test",
                    "tuffy_bold_italic-charmap.png", 48, 64, ' ');
            addChild(label1, 0, kTagSprite1);
            label1.setPosition(CGPoint.ccp(10,100));
            label1.setOpacity(200);

            CCLabelAtlas label2 = CCLabelAtlas.label("0123456789",
                    "tuffy_bold_italic-charmap.png", 48, 64, ' ');
            addChild(label2, 0, kTagSprite2);
            label2.setPosition(CGPoint.ccp(10,200));
            label2.setOpacity(32);

            schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					step(d);
				}
			});
        }

        public void step(float dt) {
        	time += dt;
        	String string = CCFormatter.format("%2.2f Test", time);
        	CCLabelAtlas label1 = (CCLabelAtlas) getChildByTag(kTagSprite1);
        	label1.setString(string);

        	CCLabelAtlas label2 = (CCLabelAtlas) getChildByTag(kTagSprite2);
        	label2.setString(CCFormatter.format("%d", (int)time));
        }

        @Override
        public String title() {
        	return "CCLabelAtlas LabelAtlasTest";
        }

        public String subtitle() {
        	return "Updating label should be fast";
        }


    }


    static class LabelAtlasColorTest extends AtlasDemo {
        float time;

        public LabelAtlasColorTest() {
            super();

            CCLabelAtlas label1 = CCLabelAtlas.label("123 Test", 
                    "tuffy_bold_italic-charmap.png", 48, 64, ' ');
            addChild(label1, 0, kTagSprite1);
            label1.setPosition(CGPoint.ccp(10,100));
            label1.setOpacity(200);

            CCLabelAtlas label2 = CCLabelAtlas.label("0123456789", 
                    "tuffy_bold_italic-charmap.png", 48, 64, ' ');
            addChild(label2, 0, kTagSprite2);
            label2.setPosition(CGPoint.ccp(10,200));
            label2.setColor(ccColor3B.ccRED);

            CCFadeOut fade = CCFadeOut.action(1.0f);
            CCFadeIn fade_in = fade.reverse();
            CCSequence seq = CCSequence.actions(fade, fade_in);
            CCRepeatForever repeat = CCRepeatForever.action(seq);
            label2.runAction(repeat);

            schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					step(d);
				}
			});
        }

        public void step(float dt) {
            time += dt;
            String string = CCFormatter.format("%2.2f Test", time);
            CCLabelAtlas label1 = (CCLabelAtlas) getChildByTag(kTagSprite1);
            label1.setString(string);

            CCLabelAtlas label2 = (CCLabelAtlas) getChildByTag(kTagSprite2);
            label2.setString(CCFormatter.format("%d", (int)time));	
        }

        public String title() {
            return "CCLabelAtlas LabelAtlasColorTest";
        }

        public String subtitle() {
            return "Opacity + Color should work at the same time";
        }
    }


    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */
    static class Atlas3 extends AtlasDemo {
    	float time;
    	
        public Atlas3() {
            super();

            CCColorLayer col = CCColorLayer.node(ccColor4B.ccc4(128,128,128,255));
            addChild(col, -10);

            CCBitmapFontAtlas label1 = CCBitmapFontAtlas.bitmapFontAtlas("Test", "bitmapFontTest2.fnt");

            // testing anchors
            label1.setAnchorPoint(CGPoint.ccp(0,0));
            addChild(label1, 0, kTagBitmapAtlas1);
            CCFadeOut fade = CCFadeOut.action(1.0f);
            CCFadeIn fade_in = fade.reverse();
            CCSequence seq = CCSequence.actions(fade, fade_in);
            CCRepeatForever repeat = CCRepeatForever.action(seq);
            label1.runAction(repeat);

            // VERY IMPORTANT
            // color and opacity work OK because bitmapFontAltas2 loads a BMP image (not a PNG image)
            // If you want to use both opacity and color, it is recommended to use NON premultiplied images like BMP images
            // Of course, you can also tell XCode not to compress PNG images, but I think it doesn't work as expected
            CCBitmapFontAtlas label2 = CCBitmapFontAtlas.bitmapFontAtlas("Test", "bitmapFontTest2.fnt");
            // testing anchors
            label2.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
            label2.setColor(ccColor3B.ccRED);
            addChild(label2, 0, kTagBitmapAtlas2);
            label2.runAction(repeat.copy());

            CCBitmapFontAtlas label3 = CCBitmapFontAtlas.bitmapFontAtlas("Test", "bitmapFontTest2.fnt");
            // testing anchors
            label3.setAnchorPoint(CGPoint.ccp(1,1));
            addChild(label3, 0, kTagBitmapAtlas3);


            CGSize s = CCDirector.sharedDirector().winSize();	
            label1.setPosition(CGPoint.ccp(0,0));
            label2.setPosition(CGPoint.ccp( s.width/2, s.height/2));
            label3.setPosition(CGPoint.ccp( s.width, s.height));

            schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					step(d);
				}
			});
        }

        public void step(float dt) {
            time += dt;
            String string = CCFormatter.format("%2.2f Test j", time);

            CCBitmapFontAtlas label1 = (CCBitmapFontAtlas) getChildByTag(kTagBitmapAtlas1);
            label1.setString(string);

            CCBitmapFontAtlas label2 = (CCBitmapFontAtlas) getChildByTag(kTagBitmapAtlas2);
            label2.setString(string);

            CCBitmapFontAtlas label3 = (CCBitmapFontAtlas) getChildByTag(kTagBitmapAtlas3);
            label3.setString(string);
        }

        @Override
        public String title() {
            return "CCBitmapFontAtlas Atlas3";
        }

        @Override
        public String subtitle() {
            return "Testing alignment. Testing opacity + tint";
        }
    }


    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */
    static class Atlas4 extends AtlasDemo {
    	float time;
    	
        public Atlas4() {
            super();

            // Upper Label
            CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("Bitmap Font Atlas", "bitmapFontTest.fnt");
            addChild(label);

            CGSize s = CCDirector.sharedDirector().winSize();

            label.setPosition(CGPoint.ccp(s.width/2, s.height/2));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));


            CCSprite BChar = (CCSprite) label.getChildByTag(0);
            CCSprite FChar = (CCSprite) label.getChildByTag(7);
            CCSprite AChar = (CCSprite) label.getChildByTag(12);


            CCRotateBy rotate = CCRotateBy.action(2, 360);
            CCRepeatForever rot_4ever = CCRepeatForever.action(rotate);

            CCScaleBy scale = CCScaleBy.action(2, 1.5f);
            CCScaleBy scale_back = scale.reverse();
            CCSequence scale_seq = CCSequence.actions(scale, scale_back);
            CCRepeatForever scale_4ever = CCRepeatForever.action(scale_seq);

            CCJumpBy jump = CCJumpBy.action(0.5f, CGPoint.zero(), 60, 1);
            CCRepeatForever jump_4ever = CCRepeatForever.action(jump);

            CCFadeOut fade_out = CCFadeOut.action(1);
            CCFadeIn fade_in = CCFadeIn.action(1);
            CCSequence seq = CCSequence.actions(fade_out, fade_in);
            CCRepeatForever fade_4ever = CCRepeatForever.action(seq);

            BChar.runAction(rot_4ever);
            BChar.runAction(scale_4ever);
            FChar.runAction(jump_4ever);
            AChar.runAction(fade_4ever);


            // Bottom Label
            CCBitmapFontAtlas label2 = CCBitmapFontAtlas.bitmapFontAtlas("00.0", "bitmapFontTest.fnt");
            addChild(label2, 0, kTagBitmapAtlas2);
            label2.setPosition(CGPoint.ccp(s.width/2.0f, 80));

            CCSprite lastChar = (CCSprite)label2.getChildByTag(3);
            lastChar.runAction(rot_4ever.copy());

            schedule(new UpdateCallback() {
				
				@Override
				public void update(float d) {
					step(d);
				}
			}, 0.1f);
        }

        public void draw(GL10 gl) {
            CGSize s = CCDirector.sharedDirector().winSize();
            CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(0, s.height/2), CGPoint.ccp(s.width, s.height/2) );
            CCDrawingPrimitives.ccDrawLine(gl, CGPoint.ccp(s.width/2, 0), CGPoint.ccp(s.width/2, s.height) );
        }

        public void step(float dt) {
            time += dt;
            String string = CCFormatter.format("%04.1f", time);

            CCBitmapFontAtlas label1 = (CCBitmapFontAtlas) getChildByTag(kTagBitmapAtlas2);
            label1.setString(string);
        }

        @Override
        public String title() {
            return "CCBitmapFontAtlas Atlas4";
        }

        public String subtitle() {
            return "Using fonts as CCSprite objects. Some characters should rotate.";
        }


    }


    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */

    static class Atlas5 extends AtlasDemo {
        public Atlas5() {
            super();

            CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas("abcdefg", "bitmapFontTest4.fnt");
            addChild(label);

            CGSize s = CCDirector.sharedDirector().winSize();

            label.setPosition(CGPoint.ccp(s.width/2, s.height/2));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
        }

        public String title() {
            return "CCBitmapFontAtlas Atlas5";
        }

        public String subtitle() {
            return "Testing padding";
        }

    }


    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */

    static class Atlas6 extends AtlasDemo {
        public Atlas6() {
            super();

            CGSize s = CCDirector.sharedDirector().winSize();
            CCBitmapFontAtlas label = null;
            label = CCBitmapFontAtlas.bitmapFontAtlas("FaFeFiFoFu", "bitmapFontTest5.fnt");
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, s.height/2+50));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));

            label = CCBitmapFontAtlas.bitmapFontAtlas("fafefifofu", "bitmapFontTest5.fnt");
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, s.height/2));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));

            label = CCBitmapFontAtlas.bitmapFontAtlas("aeiou", "bitmapFontTest5.fnt");
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, s.height/2-50));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
        }

        public String title() {
            return "CCBitmapFontAtlas Atlas6";
        }

        public String subtitle() {
            return "Rendering should be OK. Testing offset";
        }

    }


    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */

    static class AtlasBitmapColor extends AtlasDemo {
        public AtlasBitmapColor() {
            super();

            CGSize s = CCDirector.sharedDirector().winSize();

            CCBitmapFontAtlas label = null;
            label = CCBitmapFontAtlas.bitmapFontAtlas("Blue", "bitmapFontTest5.fnt");
            label.setColor(ccColor3B.ccBLUE);
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, s.height/4));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));

            label = CCBitmapFontAtlas.bitmapFontAtlas("Red", "bitmapFontTest5.fnt");
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, 2*s.height/4));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
            label.setColor(ccColor3B.ccRED);

            label = CCBitmapFontAtlas.bitmapFontAtlas("G", "bitmapFontTest5.fnt");
            addChild(label);
            label.setPosition(CGPoint.ccp(s.width/2, 3*s.height/4));
            label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
            label.setColor(ccColor3B.ccGREEN);
            label.setString("Green");
        }

        public String title() {
            return "CCBitmapFontAtlas AtlasBitmapColor";
        }

        public String subtitle() {
            return "Testing color";
        }

    }

    /*
     * Use any of these editors to generate bitmap font atlas:
     *   http://www.n4te.com/hiero/hiero.jnlp
     *   http://slick.cokeandcode.com/demos/hiero.jnlp
     *   http://www.angelcode.com/products/bmfont/
     */

    static class AtlasFastBitmap extends AtlasDemo {
        public AtlasFastBitmap() {
            super();

            // Upper Label
            for( int i=0 ; i < 100;i ++ ) {
                CCBitmapFontAtlas label = CCBitmapFontAtlas.bitmapFontAtlas(
                        String.format("-%d-",i), "bitmapFontTest.fnt");
                addChild(label);

                CGSize s = CCDirector.sharedDirector().winSize();

                CGPoint p = CGPoint.ccp( ccMacros.CCRANDOM_0_1() * s.width, ccMacros.CCRANDOM_0_1() * s.height);
                label.setPosition(p);
                label.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
            }
        }

        public String title() {
            return "CCBitmapFontAtlas AtlasFastBitmap";
        }

        public String subtitle() {
            return "Creating several CCBitmapFontAtlas with the same .fnt file should be fast";
        }

    }

}



