package org.box2d.common;

/// A 2D column vector with 3 elements.
public class BBVec3 {
    /// Default finalructor does nothing (for performance).
    public BBVec3() {
    }

    /// Conclass using coordinates.
    public BBVec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /// set this vector to all zeros.
    public void setZero() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
    }

    /// set this vector to some specified coordinates.
    public void Set(float x_, float y_, float z_) {
        x = x_;
        y = y_;
        z = z_;
    }

    /// Negate this vector.
    public BBVec3 neg() {
        BBVec3 v = new BBVec3();
        v.Set(-x, -y, -z);
        return v;
    }

    /// Add a vector to this vector.
    public BBVec3 add(final BBVec3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    /// BBVec3 a vector from this vector.
    public BBVec3 sub(final BBVec3 v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    /// Multiply this vector by a scalar.
    public BBVec3 mul(float s) {
        x *= s;
        y *= s;
        z *= s;
        return this;
    }

    public float x;
    public float y;
    public float z;
}
