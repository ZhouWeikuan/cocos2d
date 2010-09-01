package org.cocos2d.nodes;

import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCTexture2D;

/*
 * To create sprite frames and texture atlas, use this tool:
 * http://zwoptex.zwopple.com/
 */

/** Singleton that handles the loading of the sprite frames.
 It saves in a cache the sprite frames.
 @since v0.9
 */
public class CCSpriteFrameCache {
    HashMap<String, CCSpriteFrame> spriteFrames;

    private static CCSpriteFrameCache sharedSpriteFrameCache_= null;

    /** Retruns ths shared instance of the Sprite Frame cache */
    public static CCSpriteFrameCache sharedSpriteFrameCache() {
        if (sharedSpriteFrameCache_ == null) {
            sharedSpriteFrameCache_ = new CCSpriteFrameCache();
        }
        return sharedSpriteFrameCache_;
    }

    protected CCSpriteFrameCache() {
        spriteFrames = new HashMap<String, CCSpriteFrame>();
    }

    /** Purges the cache. It releases all the Sprite Frames and the retained instance.
    */
    public void purgeSharedSpriteFrameCache() {
        sharedSpriteFrameCache_ = null;
    }


    /** Adds multiple Sprite Frames with a dictionary.
     * The texture will be associated with the created sprite frames.
     */
    public void addSpriteFrames(HashMap<Object, Object> dictionary, CCTexture2D texture) {
        /*
           Supported Zwoptex Formats:
           enum {
           ZWTCoordinatesListXMLFormat_Legacy = 0
           ZWTCoordinatesListXMLFormat_v1_0,
           };
           */
        /*
        NSDictionary *metadataDict = [dictionary objectForKey:@"metadata"];
        NSDictionary *framesDict = [dictionary objectForKey:@"frames"];
        int format = 0;

        // get the format
        if(metadataDict != nil) {
            format = [[metadataDict objectForKey:@"format"] intValue];
        }

        // check the format
        if(format < 0 || format > 1) {
            NSAssert(NO,@"cocos2d: WARNING: format is not supported for CCSpriteFrameCache addSpriteFramesWithDictionary:texture:");
            return;
        }

        for(NSString *frameDictKey in framesDict) {
            NSDictionary *frameDict = [framesDict objectForKey:frameDictKey];
            CCSpriteFrame *spriteFrame;
            if(format == 0) {
                float x = [[frameDict objectForKey:@"x"] floatValue];
                float y = [[frameDict objectForKey:@"y"] floatValue];
                float w = [[frameDict objectForKey:@"width"] floatValue];
                float h = [[frameDict objectForKey:@"height"] floatValue];
                float ox = [[frameDict objectForKey:@"offsetX"] floatValue];
                float oy = [[frameDict objectForKey:@"offsetY"] floatValue];
                int ow = [[frameDict objectForKey:@"originalWidth"] intValue];
                int oh = [[frameDict objectForKey:@"originalHeight"] intValue];
                // check ow/oh
                if(!ow || !oh) {
                    CCLOG(@"cocos2d: WARNING: originalWidth/Height not found on the CCSpriteFrame. AnchorPoint won't work as expected. Regenerate the .plist");
                }
                // abs ow/oh
                ow = abs(ow);
                oh = abs(oh);
                // create frame
                spriteFrame = [CCSpriteFrame frameWithTexture:texture rect:CGRectMake(x, y, w, h) offset:CGPointMake(ox, oy) originalSize:CGSizeMake(ow, oh)];
            } else if(format == 1) {
                CGRect frame = CGRectFromString([frameDict objectForKey:@"frame"]);
                CGPoint offset = CGPointFromString([frameDict objectForKey:@"offset"]);
                CGSize sourceSize = CGSizeFromString([frameDict objectForKey:@"sourceSize"]);
                // create frame
                spriteFrame = [CCSpriteFrame frameWithTexture:texture rect:frame offset:offset originalSize:sourceSize];
            } else {
                CCLOG(@"cocos2d: Unsupported Zwoptex version. Update cocos2d");
            }

            // add sprite frame
            [spriteFrames setObject:spriteFrame forKey:frameDictKey];
        }
        */
    }

    /** Adds multiple Sprite Frames from a plist file.
     * A texture will be loaded automatically.
     * The texture name will composed by replacing the .plist suffix with .png
     * If you want to use another texture, you should use the addSpriteFramesWithFile:texture method.
     */
    public void addSpriteFrames(String plist) {
        /*
        NSString *path = [CCFileUtils fullPathFromRelativePath:plist];
        NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:path];

        NSString *texturePath = [NSString stringWithString:plist];
        texturePath = [texturePath stringByDeletingPathExtension];
        texturePath = [texturePath stringByAppendingPathExtension:@"png"];

        CCTexture2D *texture = [[CCTextureCache sharedTextureCache] addImage:texturePath];

        return [self addSpriteFramesWithDictionary:dict texture:texture];
        */
    }

    /** Adds multiple Sprite Frames from a plist file.
     * The texture will be associated with the created sprite frames.
     */
    public void addSpriteFrames(String plist, CCTexture2D texture) {
        /*
        NSString *path = [CCFileUtils fullPathFromRelativePath:plist];
        NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:path];

        return [self addSpriteFramesWithDictionary:dict texture:texture];
        */
    }

    /** Adds an sprite frame with a given name.
      If the name already exists, then the contents of the old name will be replaced with the new one.
      */
    public void addSpriteFrame(CCSpriteFrame frame, String frameName) {
	    spriteFrames.put(frameName, frame);
    }

    /** Purges the dictionary of loaded sprite frames.
     * Call this method if you receive the "Memory Warning".
     * In the short term: it will free some resources preventing your app from being killed.
     * In the medium term: it will allocate more resources.
     * In the long term: it will be the same.
     */
    public void removeSpriteFrames() {
        spriteFrames.clear();
    }

    /** Removes unused sprite frames.
     * Sprite Frames that have a retain count of 1 will be deleted.
     * It is convinient to call this method after when starting a new Scene.
     */
    public void removeUnusedSpriteFrames() {
        // Don't know what to do here.
    }

    /** Deletes an sprite frame from the sprite frame cache.
    */
    public void removeSpriteFrame(String name) {
	    spriteFrames.remove(name);
    }

    /** Returns an Sprite Frame that was previously added.
      If the name is not found it will return nil.
      You should retain the returned copy if you are going to use it.
      */
    public CCSpriteFrame getSpriteFrame(String name) {
        CCSpriteFrame frame = spriteFrames.get(name);

        if( frame == null )
            ccMacros.CCLOG("cocos2d: CCSpriteFrameCache: Frame '%s' not found", name);

        return frame;
    }

    /** Creates an sprite with the name of an sprite frame.
      The created sprite will contain the texture, rect and offset of the sprite frame.
      It returns an autorelease object.
      @deprecated use [CCSprite spriteWithSpriteFrameName:name]. This method will be removed on final v0.9
      */
    public CCSprite createSprite(String name) {
        CCSpriteFrame frame = spriteFrames.get(name);
        return CCSprite.sprite(frame);
    }
}

