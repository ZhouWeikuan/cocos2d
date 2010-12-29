package org.cocos2d.utils;

import java.util.Formatter;

public class CCFormatter {
    private static StringBuilder sb = new StringBuilder(50);
    private static Formatter formatter = new Formatter(sb);

    public synchronized static String format(String s, Object... objects) {
    	sb.setLength(0);
        formatter.format(s, objects);
        return sb.toString();
    }

    public final static int swapIntToLittleEndian(int v) {
        return  (v >>> 24) | (v << 24) |
          ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
    }
}
