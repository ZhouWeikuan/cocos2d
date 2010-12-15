package org.cocos2d.nodes;

import java.util.HashMap;
import java.util.Iterator;

import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.utils.ZwoptexParser;
import org.cocos2d.types.*;

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

    public static CCSpriteFrame spriteFrameByName(String name) {
        return CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(name);
    }

    /** Purges the cache. It releases all the Sprite Frames and the retained instance.
    */
    public static void purgeSharedSpriteFrameCache() {
    	if (sharedSpriteFrameCache_ != null) {
    		sharedSpriteFrameCache_.removeAllSpriteFrames();
    		sharedSpriteFrameCache_ = null;
    	}
    }


    /** Adds multiple Sprite Frames with a dictionary.
     * The texture will be associated with the created sprite frames.
     */
    public void addSpriteFramesWithDictionary(HashMap dictionary, CCTexture2D texture) {

        HashMap metadataDict = (HashMap)dictionary.get("metadata");
        HashMap framesDict = (HashMap)dictionary.get("frames");

        Integer format = 0;

        // get the format
        if (metadataDict != null) {
            format = (Integer)metadataDict.get("format");
        }

        // only format 2 is supported
        if (format != 2 && format != 3) {
            ccMacros.CCLOGERROR("CCSpriteFrameCache",
                "Unsupported Zwoptex plist file format.");
        }

        Iterator fi = framesDict.keySet().iterator();
        while (fi.hasNext()) {
	    		String frameDictKey = (String)fi.next();
				HashMap frameDict = (HashMap)framesDict.get(frameDictKey);
	        CCSpriteFrame spriteFrame;
	        
	        if (format == 3)
	        {
	        	CGSize spriteSize = (CGSize)frameDict.get("spriteSize");
				CGPoint spriteOffset = (CGPoint)frameDict.get("spriteOffset");
				CGSize spriteSourceSize = (CGSize)frameDict.get("spriteSourceSize");
				CGRect textureRect = (CGRect)frameDict.get("textureRect");
				Boolean textureRotated = (Boolean)frameDict.get("textureRotated");
				
				spriteFrame = CCSpriteFrame.frame(texture, 
						CGRect.make(textureRect.origin.x, textureRect.origin.y, spriteSize.width, spriteSize.height),
						textureRotated, spriteOffset, spriteSourceSize);
	        } else
	        {
	        	// default behavior  
	        	CGRect frame = (CGRect)frameDict.get("frame");
	            CGPoint offset = (CGPoint)frameDict.get("offset");
	            CGSize sourceSize = (CGSize)frameDict.get("sourceSize");
	
	            spriteFrame =
	                CCSpriteFrame.frame(texture, frame, offset, sourceSize);
	        }
	
	        spriteFrames.put(frameDictKey, spriteFrame);

        }
    }

    /** Adds multiple Sprite Frames from a plist file.
     * A texture will be loaded automatically.
     * The texture name will composed by replacing the .plist suffix with .png
     * If you want to use another texture, you should use the addSpriteFramesWithFile:texture method.
     */
    public void addSpriteFrames(String plist) {
        String texturePath = null;
        int i = plist.lastIndexOf('.');
        if (i > 0 && i <= plist.length() - 2)
		      texturePath = plist.substring(0, i) + ".png";
        CCTexture2D texture =
            CCTextureCache.sharedTextureCache().addImage(texturePath);
        addSpriteFrames(plist, texture);
    }

    /** Adds multiple Sprite Frames from a plist file.
     * The texture will be associated with the created sprite frames.
     */
    public void addSpriteFrames(String plist, CCTexture2D texture) {
		  try {
            HashMap dict = ZwoptexParser.parseZwoptex(plist);
            addSpriteFramesWithDictionary(dict, texture);
        } catch (Exception e) {
                ccMacros.CCLOG("CCSpriteFrameCache",
					     "Unable to read Zwoptex plist: " + e);
		  }
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
    public void removeAllSpriteFrames() {
        // Don't know what to do here.
    	spriteFrames.clear();
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
            ccMacros.CCLOG("CCSpriteFrameCache", "Frame not found: " + name);

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

