package org.cocos2d.events;

import android.view.MotionEvent;

public class TouchHandler implements TouchDelegate {

    private TouchDelegate delegate_;

    public TouchDelegate getDelegate() {
        return delegate_;
    }
    private int priority_;

    public int getPriority() {
        return priority_;
    }

    public void setPriority(int prio) {
        priority_ = prio;
    }

    public TouchHandler(TouchDelegate delegate, int priority) {
        delegate_ = delegate;
        priority_ = priority;
    }

    public boolean ccTouchesBegan(MotionEvent event)
    {
        if( delegate_ != null )
            return delegate_.ccTouchesBegan(event);
        return TouchDispatcher.kEventIgnored;
    }

    public boolean ccTouchesMoved(MotionEvent event)
    {
        if( delegate_ != null )
            return delegate_.ccTouchesMoved(event);
        return TouchDispatcher.kEventIgnored;
    }

    public boolean ccTouchesEnded(MotionEvent event)
    {
        if( delegate_ != null )
            return delegate_.ccTouchesEnded(event);
        return TouchDispatcher.kEventIgnored;
    }

    public boolean ccTouchesCancelled(MotionEvent event)
    {
        if( delegate_ != null )
            return delegate_.ccTouchesCancelled(event);
        return TouchDispatcher.kEventIgnored;
    }
}
