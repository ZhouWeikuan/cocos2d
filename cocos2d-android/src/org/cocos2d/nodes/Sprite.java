package org.cocos2d.nodes;

import android.graphics.Bitmap;
import org.cocos2d.opengl.Texture2D;

import java.util.HashMap;

public class Sprite extends TextureNode implements CocosNode.CocosNodeFrames {
    private HashMap<String, CocosAnimation> animations;

    public static Sprite sprite(String filename) {
        return new Sprite(TextureManager.sharedTextureManager().addImage(filename));
    }

    public static Sprite sprite(Bitmap image) {
        assert image != null : "Image must not be null";

        return new Sprite(TextureManager.sharedTextureManager().addImage(image));
    }

    public static Sprite sprite(Texture2D tex) {
        return new Sprite(tex);
    }

    protected Sprite(Texture2D tex) {
        setTexture(tex);

        animations = null; // lazy alloc
    }

    private void initAnimationDictionary() {
        animations = new HashMap<String, CocosAnimation>(2);
    }

    public void setDisplayFrame(Object frame) {
        setTexture((Texture2D) frame);
    }

    public void setDisplayFrame(String animationName, int frameIndex) {
        if (animations == null)
            initAnimationDictionary();

        CocosAnimation anim = animations.get(animationName);
        Texture2D frame = (Texture2D) anim.frames().get(frameIndex);
        setDisplayFrame(frame);
    }

    public boolean isFrameDisplayed(Object frame) {
        return getTexture() == (Texture2D) frame;
    }

    public Object displayFrame() {
        return getTexture();
    }

    public void addAnimation(CocosAnimation anim) {
        // lazy alloc
        if (animations == null)
            initAnimationDictionary();

        animations.put(anim.name(), anim);
    }

    public CocosAnimation animationByName(String animationName) {
        assert animationName != null : "animationName parameter must be non null";
        return animations.get(animationName);
    }

}
