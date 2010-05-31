package org.cocos2d.nodes;

import android.graphics.Bitmap;
import org.cocos2d.opengl.Texture2D;

import java.util.ArrayList;
import java.util.Arrays;

public class Animation implements CocosNode.CocosAnimation {
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
        this(n, d, new Texture2D[]{});
    }

    public Animation(String n, float d, String... filenames) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (filenames != null) {
            for (String filename : filenames) {
                Texture2D tex = TextureManager.sharedTextureManager().addImage(filename);
                frames.add(tex);
            }
        }
    }

    public void addFrame(String filename) {
        Texture2D tex = TextureManager.sharedTextureManager().addImage(filename);
        frames.add(tex);
    }

    public Animation(String n, float d, Bitmap... images) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (images != null) {
            for (Bitmap bitmap : images) {
                Texture2D tex = TextureManager.sharedTextureManager().addImage(bitmap);
                frames.add(tex);
            }
        }
    }

    public void addFrame(Bitmap bitmap) {
        Texture2D tex = TextureManager.sharedTextureManager().addImage(bitmap);
        frames.add(tex);
    }

    public Animation(String n, float d, Texture2D... textures) {
        name = n;
        frames = new ArrayList<Object>();
        delay = d;

        if (textures != null) {
            frames.addAll(Arrays.asList(textures));
        }
    }

    public void addFrame(Texture2D tex) {
        frames.add(tex);
    }

}
