package org.box2d.common;

/// A 2-by-2 matrix. Stored in column-major order.
public class BBMat22 {
    /// The default finalructor does nothing (for performance).
    public BBMat22() {
    }

    /// Conclass this matrix using columns.
    public BBMat22(final BBVec2 c1, final BBVec2 c2) {
        col1 = c1;
        col2 = c2;
    }

    /// Conclass this matrix using scalars.
    public BBMat22(float a11, float a12, float a21, float a22) {
        col1.x = a11;
        col1.y = a21;
        col2.x = a12;
        col2.y = a22;
    }

    /// Conclass this matrix using an angle. This matrix becomes
    /// an orthonormal rotation matrix.
    public BBMat22(float angle) {
        // TODO ERIN compute sin+cos together.
        float c = (float) Math.cos(angle), s = (float) Math.sin(angle);
        col1.x = c;
        col2.x = -s;
        col1.y = s;
        col2.y = c;
    }

    /// initialize this matrix using columns.
    public void set(final BBVec2 c1, final BBVec2 c2) {
        col1 = c1;
        col2 = c2;
    }

    /// initialize this matrix using an angle. This matrix becomes
    /// an orthonormal rotation matrix.
    public void set(float angle) {
        float c = (float) Math.cos(angle), s = (float) Math.sin(angle);
        col1.x = c;
        col2.x = -s;
        col1.y = s;
        col2.y = c;
    }

    /// set this to the identity matrix.
    public void setIdentity() {
        col1.x = 1.0f;
        col2.x = 0.0f;
        col1.y = 0.0f;
        col2.y = 1.0f;
    }

    /// set this matrix to all zeros.
    public void setZero() {
        col1.x = 0.0f;
        col2.x = 0.0f;
        col1.y = 0.0f;
        col2.y = 0.0f;
    }

    /// Extract the angle from this matrix (assumed to be
    /// a rotation matrix).
    public float getAngle() {
        return BBMath.atan2(col1.y, col1.x);
    }

    public BBMat22 getInverse() {
        float a = col1.x, b = col2.x, c = col1.y, d = col2.y;
        BBMat22 B = new BBMat22();
        float det = a * d - b * c;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        B.col1.x = det * d;
        B.col2.x = -det * b;
        B.col1.y = -det * c;
        B.col2.y = det * a;
        return B;
    }

    /// solve A * x = b, where b is a column vector. This is more efficient
    /// than computing the inverse in one-shot cases.
    public BBVec2 solve(final BBVec2 b) {
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

    public BBVec2 col1 = new BBVec2();
    public BBVec2 col2 = new BBVec2();
}
