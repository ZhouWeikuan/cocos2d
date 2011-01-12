package org.cocos2d.types.util;

import org.cocos2d.types.ccColor4F;

public final class ccColor4FUtil {
	
	public static void copy(ccColor4F src, ccColor4F dst) {
		dst.a = src.a;
		dst.r = src.r;
		dst.g = src.g;
		dst.b = src.b;
	}
	
	public static void set(ccColor4F dst, float r, float g, float b, float a) {
		dst.a = a;
		dst.r = r;
		dst.g = g;
		dst.b = b;
	}
}
