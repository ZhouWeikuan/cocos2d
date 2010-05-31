package org.box2d.common;

/// This describes the motion of a body/shape for TOI computation.
/// Shapes are defined with respect to the body origin, which may
/// no coincide with the center of mass. However, to support dynamics

/// we must interpolate the center of mass position.
public class BBSweep {
    /// Get the interpolated transform at a specific time.
    /// @param alpha is a factor in [0,1], where 0 indicates t0.
    public void getTransform(BBTransform xf, float alpha) {
        xf.position = BBMath.add(BBMath.mul(1.0f - alpha, c0), BBMath.mul(alpha, c));
        float angle = (1.0f - alpha) * a0 + alpha * a;
        xf.R.set(angle);

        // Shift to origin
        xf.position.sub(BBMath.mul(xf.R, localCenter));
    }

    /// advance the sweep forward, yielding a new initial state.
    /// @param t the new initial time.
    public void advance(float t) {
        if (t0 < t & 1.0f - t0 > BBSettings.FLT_EPSILON) {
            float alpha = (t - t0) / (1.0f - t0);
            c0 = BBMath.add(BBMath.mul(1.0f - alpha, c0), BBMath.mul(alpha, c));
            a0 = (1.0f - alpha) * a0 + alpha * a;
            t0 = t;
        }
    }

    public BBVec2 localCenter = new BBVec2();    ///< local center of mass position
    public BBVec2 c0 = new BBVec2();
    public BBVec2 c = new BBVec2();        ///< center world positions
    public float a0;
    public float a;        ///< world angles
    public float t0;            ///< time interval = [t0,1], where t0 is in [0,1]
}
