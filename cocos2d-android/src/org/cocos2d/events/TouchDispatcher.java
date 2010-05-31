package org.cocos2d.events;

import android.view.MotionEvent;

import java.util.concurrent.CopyOnWriteArrayList;

public class TouchDispatcher {

    public static final boolean kEventHandled = true;
    public static final boolean kEventIgnored = false;
    
    private CopyOnWriteArrayList<TouchHandler> touchHandlers;
    private boolean dispatchEvents;

    public boolean getDispatchEvents() {
        return dispatchEvents;
    }

    public void setDispatchEvents(boolean b) {
        dispatchEvents = b;
    }

    private static TouchDispatcher _sharedDispatcher;

    public static TouchDispatcher sharedDispatcher() {
        synchronized (TouchDispatcher.class) {
            if (_sharedDispatcher == null) {
                _sharedDispatcher = new TouchDispatcher();
            }
            return _sharedDispatcher;
        }
    }

    
    protected TouchDispatcher()
    {
            dispatchEvents = true;
            touchHandlers = new CopyOnWriteArrayList<TouchHandler>();
    }

    //
    // handlers management
    //

    private void addHandler(TouchHandler handler)
    {
        int i = 0;
        for( TouchHandler h : touchHandlers ) {
            if( h.getPriority() < handler.getPriority() )
                i++;

            if( h.getDelegate() == handler.getDelegate() )
                throw new RuntimeException("Delegate already added to touch dispatcher.");
        }
        touchHandlers.add(i, handler);
    }

    public void addDelegate(TouchDelegate delegate, int prio) {
        addHandler(new TouchHandler(delegate, prio));
    }
    
    public void removeDelegate(TouchDelegate delegate)
    {
        if( delegate == null )
            return;

        for( TouchHandler handler : touchHandlers ) {
            if( handler.getDelegate() == delegate ) {
                touchHandlers.remove(handler);
                break;
            }
        }
    }

    public void removeAllDelegates()
    {
        touchHandlers.clear();
    }

    public void setPriority(int priority, TouchHandler delegate)
    {
        if( delegate == null )
            throw new RuntimeException("Got null touch delegate");

        int i = 0;
        for( TouchHandler handler : touchHandlers ) {
            if( handler.getDelegate() == delegate ) break;
            i++;
        }

        if( i == touchHandlers.size() )
            throw new RuntimeException("Touch delegate not found");

        TouchHandler handler = touchHandlers.get(i);

        if( handler.getPriority() != priority ) {
            handler.setPriority(priority);

            touchHandlers.remove(handler);
            addHandler(handler);
        }
    }


    //
    // dispatch events
    //
    public void touchesBegan(MotionEvent event)
    {
        if( dispatchEvents )  {

            for( TouchHandler handler : touchHandlers ) {
                if( handler.ccTouchesBegan(event) == kEventHandled )
                    break;
            }
        }
    }

    public void touchesMoved(MotionEvent event)
    {
        if( dispatchEvents )  {

            for( TouchHandler handler : touchHandlers ) {
                if( handler.ccTouchesMoved(event) == kEventHandled )
                    break;
            }
        }
    }

    public void touchesEnded(MotionEvent event)
    {
        if( dispatchEvents )  {

            for( TouchHandler handler : touchHandlers ) {
                if( handler.ccTouchesEnded(event) == kEventHandled )
                    break;
            }
        }
    }
    public void touchesCancelled(MotionEvent event)
    {
        if( dispatchEvents )  {

            for( TouchHandler handler : touchHandlers ) {
                if( handler.ccTouchesCancelled(event) == kEventHandled )
                    break;
            }
        }
    }



}
