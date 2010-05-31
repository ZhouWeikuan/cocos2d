package org.cocos2d.box2d;

public class AndroidTest {

    /// Random number in range [-1,1]
    public static float randomFloat()
    {
        float r = (float)Math.random();
        r = 2.0f * r - 1.0f;
        return r;
    }

    /// Random floating point number in range [lo, hi]
    public static float randomFloat(float lo, float hi)
    {
        float r = (float)Math.random();
        r = (hi - lo) * r + lo;
        return r;
    }

}
