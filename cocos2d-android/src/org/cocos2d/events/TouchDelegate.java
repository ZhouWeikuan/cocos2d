package org.cocos2d.events;

import android.view.MotionEvent;

public interface TouchDelegate {

    public boolean ccTouchesBegan(MotionEvent event);

    public boolean ccTouchesMoved(MotionEvent event);

    public boolean ccTouchesEnded(MotionEvent event);

    public boolean ccTouchesCancelled(MotionEvent event);

}
