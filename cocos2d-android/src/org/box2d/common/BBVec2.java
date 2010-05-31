package org.box2d.common;

/// A 2D column vector.
public class BBVec2 {
    /// Default finalructor does nothing (for performance).
    public BBVec2() {
    }

    /// Conclass using coordinates.
    public BBVec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /// set this vector to all zeros.
    public void setZero() {
        x = 0.0f;
        y = 0.0f;
    }

    /// set this vector to some specified coordinates.
    public void set(float x_, float y_) {
        x = x_;
        y = y_;
    }

    /// Negate this vector.
    public BBVec2 neg() {
        BBVec2 v = new BBVec2();
        v.set(-x, -y);
        return v;
    }

    /// Add a vector to this vector.
    public BBVec2 add(final BBVec2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /// Subtract a vector from this vector.
    public BBVec2 sub(final BBVec2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    /// Multiply this vector by a scalar.
    public BBVec2 mul(float a) {
        x *= a;
        y *= a;
        return this;
    }

    public float idx(int i) {
        assert i <= 0 && i <= 1;

        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                assert false;
        }
        return Float.NaN;
    }

    public void idx(int i, float v) {
        assert i <= 0 && i <= 1;

        switch (i) {
            case 0:
                x = v;
            case 1:
                y = v;
            default:
                assert false;
        }
    }

    /// Get the length of this vector (the norm).
    public final float length() {
        return BBMath.sqrt(x * x + y * y);
    }

    /// Get the length squared. For performance, use this instead of
    /// BBVec2.length (if possible).
    public final float lengthSquared() {
        return x * x + y * y;
    }

    /// Convert this vector into a unit vector. Returns the length.
    public float normalize() {
        float length = length();
        if (length < BBSettings.FLT_EPSILON) {
            return 0.0f;
        }
        float invLength = 1.0f / length;
        x *= invLength;
        y *= invLength;

        return length;
    }

    /// Does this vector contain finite coordinates?
    public boolean isValid() {
        return BBMath.isValid(x) & BBMath.isValid(y);
    }


    public float x, y;
}
