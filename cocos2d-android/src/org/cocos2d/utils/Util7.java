package org.cocos2d.utils;

import org.cocos2d.nodes.CCDirector;

import android.content.pm.PackageManager;

/**
 * Methods are available in 2.1 and above only.
 */
public class Util7 {
	
	/**
	 * using:
	 *  if(Build.VERSION.SDK_INT >= 7) {
    		supportsMultiTouch = Util7.isMultiTouchSupported();
    	}
	 */
	public static boolean isMultiTouchSupported() {
		try {
			return CCDirector.sharedDirector().getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
		} catch(Exception e) { }
		return false;
	}
}
