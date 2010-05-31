package org.box2d.dynamics;

public class BBTimeStep {
    /// This is an internal structure.
    public float dt;            // time step
    public float inv_dt;        // inverse time step (0 if dt == 0).
    public float dtRatio;    // dt * inv_dt0
    public int velocityIterations;
    public int positionIterations;
    public boolean warmStarting;
}
