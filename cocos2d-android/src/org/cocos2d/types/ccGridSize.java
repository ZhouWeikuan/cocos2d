package org.cocos2d.types;

//! A 2D grid_ size
public class ccGridSize {
    public int x;
    public int y;

    //! helper function to create a ccGridSize
    public static ccGridSize ccg(final int x, final int y) {
        return new ccGridSize(x, y);
    }

    public ccGridSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ccGridSize(ccGridSize gs) {
    	this.x = gs.x;
    	this.y = gs.y;
    }
}

