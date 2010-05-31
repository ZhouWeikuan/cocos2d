package org.box2d.common;

/// A 3-by-3 matrix. Stored in column-major order.
public class BBMat33 {
    /// The default finalructor does nothing (for performance).
    public BBMat33() {
        col1 = new BBVec3();
        col2 = new BBVec3();
        col3 = new BBVec3();
    }

    /// Conclass this matrix using columns.
    public BBMat33(final BBVec3 c1, final BBVec3 c2, final BBVec3 c3) {
        col1 = c1;
        col2 = c2;
        col3 = c3;
    }

    /// set this matrix to all zeros.
    public void setZero() {
        col1.setZero();
        col2.setZero();
        col3.setZero();
    }

    /// solve A * x = b, where b is a column vector. This is more efficient
    /// than computing the inverse in one-shot cases.
    public BBVec3 solve33(final BBVec3 b) {
        float det = BBMath.dot(col1, BBMath.cross(col2, col3));
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        BBVec3 x = new BBVec3();
        x.x = det * BBMath.dot(b, BBMath.cross(col2, col3));
        x.y = det * BBMath.dot(col1, BBMath.cross(b, col3));
        x.z = det * BBMath.dot(col1, BBMath.cross(col2, b));
        return x;
    }


    /// solve A * x = b, where b is a column vector. This is more efficient
    /// than computing the inverse in one-shot cases. solve only the upper
    /// 2-by-2 matrix equation.
    public BBVec2 solve22(final BBVec2 b) {
        float a11 = col1.x, a12 = col2.x, a21 = col1.y, a22 = col2.y;
        float det = a11 * a22 - a12 * a21;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        BBVec2 x = new BBVec2();
        x.x = det * (a22 * b.x - a12 * b.y);
        x.y = det * (a11 * b.y - a21 * b.x);
        return x;
    }

    public BBVec3 col1 = new BBVec3();
    public BBVec3 col2 = new BBVec3();
    public BBVec3 col3 = new BBVec3();
}
