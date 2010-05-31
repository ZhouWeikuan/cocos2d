package org.box2d.dynamics.joints;

import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;


public class BBDistanceJoint extends BBJoint {
    /// Distance joint definition. This requires defining an
    /// anchor point on both bodies and the non-zero length of the
    /// distance joint. The definition uses local anchor points
    /// so that the initial configuration can violate the constraint
    /// slightly. This helps when saving and loading a game.
    /// @warning Do not use a zero or short length.
    public static class BBDistanceJointDef extends BBJointDef {
        public BBDistanceJointDef() {
            type = e_distanceJoint;
            localAnchor1.set(0.0f, 0.0f);
            localAnchor2.set(0.0f, 0.0f);
            length = 1.0f;
            frequencyHz = 0.0f;
            dampingRatio = 0.0f;
        }

        /// initialize the bodies, anchors, and length using the world
        /// anchors.
        void initialize(BBBody b1, BBBody b2,
                        final BBVec2 anchor1, final BBVec2 anchor2) {
            body1 = b1;
            body2 = b2;
            localAnchor1 = body1.getLocalPoint(anchor1);
            localAnchor2 = body2.getLocalPoint(anchor2);
            BBVec2 d = sub(anchor2, anchor1);
            length = d.length();
        }

        /// The local anchor point relative to body1's origin.
        BBVec2 localAnchor1;

        /// The local anchor point relative to body2's origin.
        BBVec2 localAnchor2;

        /// The equilibrium length between the anchor points.
        float length;

        /// The response speed.
        float frequencyHz;

        /// The damping ratio. 0 = no damping, 1 = critical damping.
        float dampingRatio;
    }

/// A distance joint finalrains two points on two bodies
/// to remain at a fixed distance from each other. You can view
/// this as a massless, rigid rod.

    public BBVec2 getAnchor1() {
        return m_bodyA.getWorldPoint(m_localAnchor1);
    }

    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor2);
    }

    public BBVec2 getReactionForce(float inv_dt) {
        return mul(inv_dt * m_impulse, m_u);
    }

    public float getReactionTorque(float inv_dt) {
        return 0.0f;
    }

    //--------------- Internals Below -------------------

    public BBDistanceJoint(final BBDistanceJointDef def) {
        super(def);
        m_localAnchor1 = def.localAnchor1;
        m_localAnchor2 = def.localAnchor2;
        m_length = def.length;
        m_frequencyHz = def.frequencyHz;
        m_dampingRatio = def.dampingRatio;
        m_impulse = 0.0f;
        m_gamma = 0.0f;
        m_bias = 0.0f;
    }


    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        // Compute the effective mass matrix.
        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));
        m_u = sub(sub(add(b2.m_sweep.c, r2), b1.m_sweep.c), r1);

        // Handle singularity.
        float length = m_u.length();
        if (length > linearSlop) {
            m_u.mul(1.0f / length);
        } else {
            m_u.set(0.0f, 0.0f);
        }

        float cr1u = cross(r1, m_u);
        float cr2u = cross(r2, m_u);
        float invMass = b1.m_invMass + b1.m_invI * cr1u * cr1u + b2.m_invMass + b2.m_invI * cr2u * cr2u;

        m_mass = invMass != 0.0f ? 1.0f / invMass : 0.0f;

        if (m_frequencyHz > 0.0f) {
            float C = length - m_length;

            // Frequency
            float omega = 2.0f * PI * m_frequencyHz;

            // Damping coefficient
            float d = 2.0f * m_mass * m_dampingRatio * omega;

            // Spring stiffness
            float k = m_mass * omega * omega;

            // magic formulas
            m_gamma = step.dt * (d + step.dt * k);
            m_gamma = m_gamma != 0.0f ? 1.0f / m_gamma : 0.0f;
            m_bias = C * step.dt * k * m_gamma;

            m_mass = invMass + m_gamma;
            m_mass = m_mass != 0.0f ? 1.0f / m_mass : 0.0f;
        }

        if (step.warmStarting) {
            // Scale the impulse to support a variable time step.
            m_impulse *= step.dtRatio;

            BBVec2 P = mul(m_impulse, m_u);
            b1.m_linearVelocity.sub(mul(b1.m_invMass, P));
            b1.m_angularVelocity -= b1.m_invI * cross(r1, P);
            b2.m_linearVelocity.add(mul(b2.m_invMass, P));
            b2.m_angularVelocity += b2.m_invI * cross(r2, P);
        } else {
            m_impulse = 0.0f;
        }
    }

    public void solveVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

        // Cdot = dot(u, v + cross(w, r))
        BBVec2 v1 = add(b1.m_linearVelocity, cross(b1.m_angularVelocity, r1));
        BBVec2 v2 = add(b2.m_linearVelocity, cross(b2.m_angularVelocity, r2));
        float Cdot = dot(m_u, sub(v2, v1));

        float impulse = -m_mass * (Cdot + m_bias + m_gamma * m_impulse);
        m_impulse += impulse;

        BBVec2 P = mul(impulse, m_u);
        b1.m_linearVelocity.sub(mul(b1.m_invMass, P));
        b1.m_angularVelocity -= b1.m_invI * cross(r1, P);
        b2.m_linearVelocity.add(mul(b2.m_invMass, P));
        b2.m_angularVelocity += b2.m_invI * cross(r2, P);
    }

    public boolean solvePositionConstraints(float baumgarte) {
        if (m_frequencyHz > 0.0f) {
            // There is no position correction for soft distance constraints.
            return true;
        }

        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

        BBVec2 d = sub(sub(add(b2.m_sweep.c, r2), b1.m_sweep.c), r1);

        float length = d.normalize();
        float C = length - m_length;
        C = clamp(C, -maxLinearCorrection, maxLinearCorrection);

        float impulse = -m_mass * C;
        m_u = d;
        BBVec2 P = mul(impulse, m_u);

        b1.m_sweep.c.sub(mul(b1.m_invMass, P));
        b1.m_sweep.a -= b1.m_invI * cross(r1, P);
        b2.m_sweep.c.add(mul(b2.m_invMass, P));
        b2.m_sweep.a += b2.m_invI * cross(r2, P);

        b1.synchronizeTransform();
        b2.synchronizeTransform();

        return abs(C) < linearSlop;
    }

    BBVec2 m_localAnchor1 = new BBVec2();
    BBVec2 m_localAnchor2 = new BBVec2();
    BBVec2 m_u = new BBVec2();
    float m_frequencyHz;
    float m_dampingRatio;
    float m_gamma;
    float m_bias;
    float m_impulse;
    float m_mass;        // effective mass for the constraint.
    float m_length;

}
