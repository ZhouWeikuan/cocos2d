package org.cocos2d.nodes;

import org.cocos2d.types.CCRect;

import java.util.ArrayList;
import java.util.Arrays;

public class AtlasAnimation implements CocosNode.CocosAnimation {
    String name;
    float delay;
    ArrayList<Object> frames;


    public ArrayList<Object> frames() {
        return frames;
    }

    public String name() {
        return name;
    }

    public float delay() {
        return delay;
    }

    public AtlasAnimation(String n, float d) {
        this(n, d, new AtlasSpriteFrame[]{});
    }


    /* initializes an AtlasAnimation with an AtlasSpriteManager, a name, and the frames from AtlasSpriteFrames */
    public AtlasAnimation(String t, float d, AtlasSpriteFrame... f) {
        name = t;
        frames = new ArrayList<Object>();
        delay = d;

        frames.addAll(Arrays.asList(f));
    }

    public void addFrame(CCRect rect) {
        AtlasSpriteFrame frame = new AtlasSpriteFrame(rect);
        frames.add(frame);
    }

}
