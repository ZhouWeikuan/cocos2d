package org.cocos2d.utils;

public class CCFormatter {
    private StringBuilder sb;
    private java.util.Formatter formatter;

    public CCFormatter() {
           sb = new StringBuilder();
           formatter = new java.util.Formatter(sb);
    }

    public String format(java.lang.String s, java.lang.Object... objects) {
        formatter.format(s, objects);
        return sb.toString();
    }
}
