package org.cocos2d.events;

import java.util.ArrayList;

import org.cocos2d.protocols.CCKeyDelegateProtocol;
import org.cocos2d.utils.collections.ConcNodeCachingLinkedQueue;

import android.view.KeyEvent;

public class CCKeyDispatcher {
	public static final boolean kEventHandled = true;
	public static final boolean kEventIgnored = false;

	private ArrayList<CCKeyHandler> keyHandlers;
	private boolean dispatchEvents;

	public boolean getDispatchEvents() {
		return dispatchEvents;
	}

	public void setDispatchEvents(boolean b) {
		dispatchEvents = b;
	}

	private static CCKeyDispatcher _sharedDispatcher = new CCKeyDispatcher();

	public static CCKeyDispatcher sharedDispatcher() {
		return _sharedDispatcher;
	}

	public CCKeyDispatcher() {
		dispatchEvents = true;
		keyHandlers = new ArrayList<CCKeyHandler>();
	}

	private void addHandler(CCKeyHandler handler) {
        int i = 0;

		synchronized (keyHandlers) {
			for(int ind=0; ind<keyHandlers.size(); ind++) {
				CCKeyHandler h = keyHandlers.get(ind);
				if(h.getPriority() < handler.getPriority())
					i++;
				if(h.getDelegate() == handler.getDelegate())
					return;
			}
			keyHandlers.add(i, handler);
		}
	}

	public void addDelegate(CCKeyDelegateProtocol delegate, int prio) {
		addHandler(new CCKeyHandler(delegate, prio));
	}

	public void removeDelegate(CCKeyDelegateProtocol delegate) {
		if (delegate == null)
			return;
		synchronized (keyHandlers) {
			for (int ind = 0; ind < keyHandlers.size(); ind++) {
				CCKeyHandler handler = keyHandlers.get(ind);
				if (handler.getDelegate() == delegate) {
					keyHandlers.remove(handler);
					break;
				}
			}
		}
	}

	public void removeAllDelegates() {
		keyHandlers.clear();
	}

	private final ConcNodeCachingLinkedQueue<KeyEvent> eventQueue = new ConcNodeCachingLinkedQueue<KeyEvent>();

	public void queueMotionEvent(KeyEvent event) {
		// copy event for queue
		KeyEvent eventForQueue = new KeyEvent(event);

		eventQueue.push(eventForQueue);
	}

	public void update() {
		KeyEvent event;
		while ((event = eventQueue.poll()) != null) {
			switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				onKeyDown(event);
				break;
			case KeyEvent.ACTION_UP:
				onKeyUp(event);
				break;
			}
		}
	}

	public void onKeyDown(KeyEvent event) {
		if (dispatchEvents) {

			synchronized (keyHandlers) {
				for (int ind = 0; ind < keyHandlers.size(); ind++) {
					CCKeyHandler handler = keyHandlers.get(ind);
					if (handler.ccKeyDown(event.getKeyCode(), event) == kEventHandled)
						break;
				}
			}
		}
	}

	public void onKeyUp(KeyEvent event) {
		if (dispatchEvents) {
			synchronized (keyHandlers) {
				for (int ind = 0; ind < keyHandlers.size(); ind++) {
					CCKeyHandler handler = keyHandlers.get(ind);
					if (handler.ccKeyUp(event.getKeyCode(), event) == kEventHandled)
						break;
				}
			}
		}
	}
}
