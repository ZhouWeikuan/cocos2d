package org.cocos2d.types;

//! A 2D grid_ size
public class CCGridSize {
    public int x;
    public int y;

    //! helper function to create a ccGridSize
    public static CCGridSize ccg(final int x, final int y) {
        return new CCGridSize(x, y);
    }

    public CCGridSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
