package org.cocos2d.config;

import javax.microedition.khronos.opengles.GL10;

/**
  @file
  cocos2d (cc) configuration file
  */
public class ccConfig {
    public static final String cocos2dVersion = "cocos2d v0.99.4";

    /** @def COCOS2D_DEBUG
      Define the log level for cocos2d-android
      if 0, no log outputs
      if 1, error logs output
      if 2, all logs output
     */
    public static final int COCOS2D_DEBUG = 2;

    /** @def CC_FONT_LABEL_SUPPORT
      If enabled, FontLabel will be used to render .ttf files.
      If the .ttf file is not found, then it will use the standard UIFont class
      If disabled, the standard UIFont class will be used.

      To enable set it to a value different than 0. Enabled by default.
      */
    public static final int CC_FONT_LABEL_SUPPORT   =	1;

    /** @def CC_DIRECTOR_FAST_FPS
      If enabled, then the FPS will be drawn using CCLabelAtlas (fast rendering).
      You will need to add the fps_images.png to your project.
      If disabled, the FPS will be rendered using CCLabel (slow rendering)

      To enable set it to a value different than 0. Enabled by default.
      */
    public static final boolean CC_DIRECTOR_FAST_FPS    =	true;

    /** @def CC_DIRECTOR_FPS_INTERVAL
      Senconds between FPS updates.
      0.5 seconds, means that the FPS number will be updated every 0.5 seconds.
      Having a bigger number means a more reliable FPS

      Default value: 0.1f
      */
    public static final float CC_DIRECTOR_FPS_INTERVAL = 0.1f;

    /** @def CC_DIRECTOR_DISPATCH_FAST_EVENTS
      If enabled, and only when it is used with CCFastDirector, the main loop will wait 0.04 seconds to
      dispatch all the events, even if there are not events to dispatch.
      If your game uses lot's of events (eg: touches) it might be a good idea to enable this feature.
      Otherwise, it is safe to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.

      @warning This feature is experimental
      */
    public static final int CC_DIRECTOR_DISPATCH_FAST_EVENTS = 0;

    /** @def CC_COCOSNODE_RENDER_SUBPIXEL
      If enabled, the CCNode objects (CCSprite, CCLabel,etc) will be able to render in subpixels.
      If disabled, integer pixels will be used.

      To enable set it to a value different than 0. Enabled by default.
      */
    public static final boolean CC_COCOSNODE_RENDER_SUBPIXEL    =   true;

    /** @def CC_SPRITESHEET_RENDER_SUBPIXEL
      If enabled, the CCSprite objects rendered with CCSpriteSheet will be able to render in subpixels.
      If disabled, integer pixels will be used.

      To enable set it to a value different than 0. Enabled by default.
      */
    public static final int CC_SPRITESHEET_RENDER_SUBPIXEL	=   1;

    /** @def CC_TEXTURE_ATLAS_USES_VBO
      If enabled, the CCTextureAtlas object will use VBO instead of vertex list (VBO is recommended by Apple)

      To enable set it to a value different than 0. Enabled by default.

      @since v0.99.0
      */
    public static final int CC_TEXTURE_ATLAS_USES_VBO   =   1;

    /** @def CC_NODE_TRANSFORM_USING_AFFINE_MATRIX
      If enabled, CCNode will transform the nodes using a cached Affine matrix.
      If disabled, the node will be transformed using glTranslate,glRotate,glScale.
      Using the affine matrix only requires 2 GL calls.
      Using the translate/rotate/scale requires 5 GL calls.
      But computing the Affine matrix is relative expensive.
      But according to performance tests, Affine matrix performs better.
      This parameter doesn't affect SpriteSheet nodes.

      To enable set it to a value different than 0. Enabled by default.

    */
    public static final boolean CC_NODE_TRANSFORM_USING_AFFINE_MATRIX  = true;

    /** @def CC_TEXTURE_ATLAS_USE_TRIANGLE_STRIP
      Use GL_TRIANGLE_STRIP instead of GL_TRIANGLES when rendering the texture atlas.
      It seems it is the recommend way, but it is much slower, so, enable it at your own risk

      To enable set it to a value different than 0. Disabled by default.

    */
    public static final boolean CC_TEXTURE_ATLAS_USE_TRIANGLE_STRIP  = false;

    /** @def CC_TEXTURE_NPOT_SUPPORT
      If enabled, NPOT textures will be used where available. Only 3rd gen (and newer) devices support NPOT textures.
      NPOT textures have the following limitations:
      - They can't have mipmaps
      - They only accept GL_CLAMP_TO_EDGE in GL_TEXTURE_WRAP_{S,T}

      To enable set it to a value different than 0. Disabled by default.

      @since v0.99.2
      */
    public static final int CC_TEXTURE_NPOT_SUPPORT  = 0;

    /** @def CC_SPRITE_DEBUG_DRAW
      If enabled, all subclasses of CCSprite will draw a bounding box
      Useful for debugging purposes only. It is recommened to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final int CC_SPRITE_DEBUG_DRAW =  0;

    /** @def CC_SPRITESHEET_DEBUG_DRAW
      If enabled, all subclasses of CCSprite that are rendered using an CCSpriteSheet draw a bounding box.
      Useful for debugging purposes only. It is recommened to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final boolean CC_SPRITESHEET_DEBUG_DRAW  = false;

    /** @def CC_BITMAPFONTATLAS_DEBUG_DRAW
      If enabled, all subclasses of BitmapFontAtlas will draw a bounding box
      Useful for debugging purposes only. It is recommened to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final int CC_BITMAPFONTATLAS_DEBUG_DRAW = 0;

    /** @def CC_LABELATLAS_DEBUG_DRAW
      If enabled, all subclasses of LabeltAtlas will draw a bounding box
      Useful for debugging purposes only. It is recommened to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final int CC_LABELATLAS_DEBUG_DRAW = 0;

    /** @def CC_ENABLE_PROFILERS
      If enabled, will activate various profilers withing cocos2d. This statistical data will be output to the console
      once per second showing average time (in milliseconds) required to execute the specific routine(s).
      Useful for debugging purposes only. It is recommened to leave it disabled.

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final boolean CC_ENABLE_PROFILERS = false;

    /** @def CC_COMPATIBILITY_WITH_0_8
      Enable it if you want to support v0.8 compatbility.
      Basically, classes without namespaces will work.
      It is recommended to disable compatibility once you have migrated your game to v0.9 to avoid class name polution

      To enable set it to a value different than 0. Disabled by default.
      */
    public static final int CC_COMPATIBILITY_WITH_0_8 = 0;


    /** @def CC_BLEND_SRC
      default gl blend src function. Compatible with premultiplied alpha images.
    */
    // default gl blend src function
    // public static final int CC_BLEND_SRC = GL10.GL_SRC_ALPHA;
    public static final int CC_BLEND_SRC = GL10.GL_ONE;

    /** @def CC_BLEND_DST
      default gl blend dst function. Compatible with premultiplied alpha images.
    */
    // default gl blend dst function
    public static final int CC_BLEND_DST = GL10.GL_ONE_MINUS_SRC_ALPHA;

}

