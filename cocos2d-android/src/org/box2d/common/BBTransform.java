package org.box2d.common;

/// A transform contains translation and rotation. It is used to represent

/// the position and orientation of rigid frames.
public class BBTransform {
    /// The default finalructor does nothing (for performance).
    public BBTransform() {
        position = new BBVec2();
        R = new BBMat22();
    }

    /// initialize using a position vector and a rotation matrix.
    public BBTransform(final BBVec2 position, final BBMat22 R) {
        this.position = position;
        this.R = R;
    }

    /// set this to the identity transform.
    public void setIdentity() {
        position.setZero();
        R.setIdentity();
    }

    /// set this based on the position and angle.
    public void set(final BBVec2 p, float angle) {
        position = p;
        R.set(angle);
    }

    /// Calculate the angle that the rotation matrix represents.
    public float getAngle() {
        return BBMath.atan2(R.col1.y, R.col1.x);
    }

    public BBVec2 position = new BBVec2();
    public BBMat22 R = new BBMat22();
}

