package org.cocos2d.nodes;

import android.graphics.Bitmap;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccBlendFunc;

import java.util.HashMap;

public class Sprite extends CCTextureNode implements CCNode.CocosNodeFrames {
    private HashMap<String, CCAnimation> animations;
    private CGRect rect_;
	public boolean flipY;
	public boolean flipX;
	public ccBlendFunc blendFunc;

    public void setTextureRect(CGRect rect) {
        rect_ = rect;
    }

    public CGRect getTextureRect() {
        return rect_;
    }

    public static Sprite sprite(String filename) {
        return new Sprite(CCTextureCache.sharedTextureCache().addImage(filename));
    }

    public static Sprite sprite(Bitmap image) {
        assert image != null : "Image must not be null";

        return new Sprite(CCTextureCache.sharedTextureCache().addImage(image));
    }

    public static Sprite sprite(CCTexture2D tex) {
        return new Sprite(tex);
    }

    protected Sprite() {
    	
    }
    
    protected Sprite(CCTexture2D tex) {
        setTexture(tex);

        animations = null; // lazy alloc
    }

    private void initAnimationDictionary() {
        animations = new HashMap<String, CCAnimation>(2);
    }

    public void setDisplayFrame(Object frame) {
        setTexture((CCTexture2D) frame);
    }

    public void setDisplayFrame(String animationName, int frameIndex) {
        if (animations == null)
            initAnimationDictionary();

        CCAnimation anim = animations.get(animationName);
        CCTexture2D frame = (CCTexture2D) anim.frames().get(frameIndex);
        setDisplayFrame(frame);
    }

    public boolean isFrameDisplayed(Object frame) {
        return getTexture() == (CCTexture2D) frame;
    }

    public Object displayFrame() {
        return getTexture();
    }

    public void addAnimation(CCAnimation anim) {
        // lazy alloc
        if (animations == null)
            initAnimationDictionary();

        animations.put(anim.name(), anim);
    }

    public CCAnimation animationByName(String animationName) {
        assert animationName != null : "animationName parameter must be non null";
        return animations.get(animationName);
    }

	public void setFlipX(boolean flipX) {
		// TODO Auto-generated method stub
		
	}

	public void setFlipY(boolean flipY) {
		// TODO Auto-generated method stub
		
	}

}
