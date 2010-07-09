package org.cocos2d.types;

//! delta time type
public class ccTime {
    //! if you want more resolution redefine it as a double
    //double val;
    float val;

    public ccTime(float f){
        val = f;
    }

    public float toFloat() {
        return val;
    }
}
