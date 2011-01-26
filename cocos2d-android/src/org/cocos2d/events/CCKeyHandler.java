package org.cocos2d.events;

import org.cocos2d.protocols.CCKeyDelegateProtocol;

import android.view.KeyEvent;

public class CCKeyHandler implements CCKeyDelegateProtocol {
	private CCKeyDelegateProtocol delegate_;
	boolean enabledSelectors_;
    private int priority_;


	public CCKeyDelegateProtocol getDelegate() {
		return delegate_;
	}

	public void setSelectorFlag(boolean sf) {
		enabledSelectors_ = sf;
	}

	public boolean getSelectorFlag() {
		return enabledSelectors_;
	}

    public int getPriority() {
        return priority_;
    }

    public void setPriority(int prio) {
        priority_ = prio;
    }

	public static CCKeyHandler makeHandler(CCKeyDelegateProtocol delegate, int priority) {
		return new CCKeyHandler(delegate, priority);
	}

	public CCKeyHandler(CCKeyDelegateProtocol delegate, int priority) {
        assert delegate !=null : "Key delegate may not be nil";
		delegate_ = delegate;
		enabledSelectors_ = false;
		priority_ = priority;
	}

	@Override
	public boolean ccKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
        if( delegate_ != null )
        	return delegate_.ccKeyDown(keyCode, event);
        else
        	return CCKeyDispatcher.kEventIgnored;
	}

	@Override
	public boolean ccKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
        if( delegate_ != null )
        	return delegate_.ccKeyUp(keyCode, event);
        else
        	return CCKeyDispatcher.kEventIgnored;
	}

}
