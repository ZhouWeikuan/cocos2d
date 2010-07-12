package org.cocos2d.utils;

import org.cocos2d.types.CGAffineTransform;

public class TransformUtils {

    public static void CCAffineToGL(final CGAffineTransform t, float[] m)
    {
        // | m[0] m[4] m[8]  m[12] |     | m11 m21 m31 m41 |     | a c 0 tx |
        // | m[1] m[5] m[9]  m[13] |     | m12 m22 m32 m42 |     | b d 0 ty |
        // | m[2] m[6] m[10] m[14] | <=> | m13 m23 m33 m43 | <=> | 0 0 1  0 |
        // | m[3] m[7] m[11] m[15] |     | m14 m24 m34 m44 |     | 0 0 0  1 |

        m[2] = m[3] = m[6] = m[7] = m[8] = m[9] = m[11] = m[14] = 0.0f;
        m[10] = m[15] = 1.0f;
        m[0] = (float)t.m00; m[4] = (float)t.m01; m[12] = (float)t.m02;
        m[1] = (float)t.m10; m[5] = (float)t.m11; m[13] = (float)t.m12;
    }

    void GLToCGAffine(final float[] m, CGAffineTransform t)
    {
        t.m00 = m[0]; t.m01 = m[4]; t.m02 = m[12];
        t.m10 = m[1]; t.m11 = m[5]; t.m12 = m[13];
    }
}
