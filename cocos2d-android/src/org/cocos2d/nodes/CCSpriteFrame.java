package org.cocos2d.nodes;

import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;


/** A CCSpriteFrame has:
  - texture: A CCTexture2D that will be used by the CCSprite
  - rectangle: A rectangle of the texture


  You can modify the frame of a CCSprite by doing:

  CCSpriteFrame *frame = [CCSpriteFrame frameWithTexture:texture rect:rect offset:offset];
  [sprite setDisplayFrame:frame];
  */
public class CCSpriteFrame {
    /** rect of the frame */
    CGRect			rect_;
    public CGRect getRect() {
    	return CGRect.make(rect_);
    }
    
    /** offset of the frame */
    CGPoint			offset_;
    public CGPoint getOffset() {
    	return CGPoint.make(offset_.x, offset_.y);
    }
    
    /** original size of the trimmed image */
    CGSize			originalSize_;
    /** texture of the frame */
    CCTexture2D		texture_;
    public CCTexture2D getTexture() {
    	return texture_;
    }

    /** Create a CCSpriteFrame with a texture, rect and offset.
      It is assumed that the frame was not trimmed.
      */
    public static CCSpriteFrame frame(CCTexture2D texture, CGRect rect, CGPoint offset) {
        return new CCSpriteFrame(texture, rect, offset);
    }

    /** Create a CCSpriteFrame with a texture, rect, offset and originalSize.
      The originalSize is the size in pixels of the frame before being trimmed.
      */
    public static CCSpriteFrame frame(CCTexture2D texture, CGRect rect, CGPoint offset, CGSize originalSize ){
        return new CCSpriteFrame(texture, rect, offset, originalSize);
    }

    /** Initializes a CCSpriteFrame with a texture, rect and offset.
      It is assumed that the frame was not trimmed.
      */
    protected CCSpriteFrame (CCTexture2D texture, CGRect rect, CGPoint offset) {
        this(texture, rect, offset, rect.size);
    }

    /** Initializes a CCSpriteFrame with a texture, rect, offset and originalSize.
      The originalSize is the size in pixels of the frame before being trimmed.
      */
    protected CCSpriteFrame (CCTexture2D texture, CGRect rect, CGPoint offset, CGSize originalSize) {
        texture_ = texture;
        offset_ = offset;
        rect_ = rect;
        originalSize_ = originalSize;
    }

    public CCSpriteFrame copy() {
        CCSpriteFrame copy = new CCSpriteFrame(texture_, rect_, offset_, originalSize_);
        return copy;
    }
}

