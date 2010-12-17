package org.cocos2d.nodes;

import java.util.ArrayList;

import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

/** an Animation object used within Sprites to perform animations */
public class CCAnimation {
    /** name of the animation */
    private String name_;
    /** delay between frames in seconds. */
    private float delay_;
    /** array of frames */
    ArrayList<CCSpriteFrame> frames_;

    public String name() {
        return name_;
    }

    public float delay() {
        return delay_;
    }

    public ArrayList<CCSpriteFrame> frames() {
        return frames_;
    }

    /** Creates a CCAnimation with a name
      @since v0.99.3
      */
    public static CCAnimation animation(String name) {
        return new CCAnimation(name);
    }

    /** Creates a CCAnimation with a name and frames
      @since v0.99.3
      */
    public static CCAnimation animation(String name, ArrayList<CCSpriteFrame> frames) {
        return new CCAnimation(name, frames);
    }

    /** Creates a CCAnimation with a name and delay between frames. */
    public static CCAnimation animation(String name, float delay) {
        return new CCAnimation(name, delay);
    }

    /** Creates a CCAnimation with a name, delay and an array of CCSpriteFrames. */
    public static CCAnimation animation(String name, float delay, ArrayList<CCSpriteFrame> frames) {
        return new CCAnimation(name, delay, frames);
    }

    /** Initializes a CCAnimation with a name
      @since v0.99.3
      */
    protected CCAnimation(String name) {
        this(name, (ArrayList<CCSpriteFrame>)null);
    }


    /** Initializes a CCAnimation with a name and frames
      @since v0.99.3
      */
    protected CCAnimation(String name, ArrayList<CCSpriteFrame> frames) {
        this(name, 0, frames);
    }

    /** Initializes a CCAnimation with a name and delay between frames. */
    protected CCAnimation(String name, float delay) {
        this(name, delay, (ArrayList<CCSpriteFrame>)null);
    }

    /** Initializes a CCAnimation with a name, delay and an array of CCSpriteFrames. */
    protected CCAnimation(String name, float delay, ArrayList<CCSpriteFrame> frames) {
        delay_ = delay;
        name_ = name;
        frames_ = new ArrayList<CCSpriteFrame>();
        if (frames != null)
        	frames_.addAll(frames);
    }

    /** Adds a frame with an image filename.
     * Internally it will create a CCSpriteFrame and it will add it.
     * Added to facilitate the migration from v0.8 to v0.9.
     */
    public void addFrame(String filename) {
        CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(filename);
        CGRect rect = CGRect.make(0, 0, 0, 0);
        rect.size = tex.getContentSize();
        CCSpriteFrame frame = CCSpriteFrame.frame(tex, rect, CGPoint.zero());
        frames_.add(frame);
    }

    public void addFrame(CCTexture2D tex) {
        CGRect rect = CGRect.make(0, 0, 0, 0);
        rect.size = tex.getContentSize();
        CCSpriteFrame frame = CCSpriteFrame.frame(tex, rect, CGPoint.zero());
        frames_.add(frame);
    }

    /*
     * Bad for texture handling method. There is no analog in cocos2d-iphone.
     *
    public CCAnimation(String n, float d, Bitmap... images) {
        name_ = n;
        frames_ = new ArrayList<CCSpriteFrame>();
        delay_ = d;

        if (images != null) {
            for (Bitmap bitmap : images) {
                CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(bitmap);
                addFrame(tex);
            }
        }
    }
     */
    
    /** Adds a frame to a CCAnimation. */
    public void addFrame(CCSpriteFrame frame) {
        frames_.add(frame);
    }

    public CCAnimation(String n, float d, CCTexture2D... textures) {
        name_ = n;
        frames_ = new ArrayList<CCSpriteFrame>();
        delay_ = d;

        if (textures != null) {
        	for (CCTexture2D tex : textures) {
        		addFrame(tex);
        	}
        }
    }

    /** Adds a frame with a texture and a rect.
     * Internally it will create a CCSpriteFrame and it will add it.
     * Added to facilitate the migration from v0.8 to v0.9.
     */
    public void addFrame(CCTexture2D tex, CGRect rect) {
        CCSpriteFrame frame = CCSpriteFrame.frame(tex, rect, CGPoint.zero());
        frames_.add(frame);
    }

}

