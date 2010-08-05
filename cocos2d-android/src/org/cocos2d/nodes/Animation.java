package org.cocos2d.nodes;

import android.graphics.Bitmap;
import org.cocos2d.opengl.CCTexture2D;

import java.util.ArrayList;
import java.util.Arrays;

public class Animation implements CCNode.CCAnimation {
    private String name;
    private float delay;
    ArrayList<Object> frames;

    public String name() {
        return name;
    }

    public float delay() {
        return delay;
    }

    public ArrayList<Object> frames() {
        return frames;
    }

    public Animation(String n, float d) {
        this(n, d, new CCTexture2D[]{});
    }

    public Animation(String n, float d, String... filenames) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (filenames != null) {
            for (String filename : filenames) {
                CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(filename);
                frames.add(tex);
            }
        }
    }

    public void addFrame(String filename) {
        CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(filename);
        frames.add(tex);
    }

    public Animation(String n, float d, Bitmap... images) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (images != null) {
            for (Bitmap bitmap : images) {
                CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(bitmap);
                frames.add(tex);
            }
        }
    }

    public void addFrame(Bitmap bitmap) {
        CCTexture2D tex = CCTextureCache.sharedTextureCache().addImage(bitmap);
        frames.add(tex);
    }

    public Animation(String n, float d, CCTexture2D... textures) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (textures != null) {
            frames.addAll(Arrays.asList(textures));
        }
    }

    public void addFrame(CCTexture2D tex) {
        frames.add(tex);
    }

}
