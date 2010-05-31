package org.box2d.dynamics.joints;

import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;


public class BBPulleyJoint extends BBJoint {

    private static final float minPulleyLength = 2.0f;

    /// Pulley joint definition. This requires two ground anchors,
    /// two dynamic body anchor points, max lengths for each side,
    /// and a pulley ratio.
    public static class BBPulleyJointDef extends BBJointDef {
        BBPulleyJointDef() {
            type = e_pulleyJoint;
            groundAnchor1.set(-1.0f, 1.0f);
            groundAnchor2.set(1.0f, 1.0f);
            localAnchor1.set(-1.0f, 0.0f);
            localAnchor2.set(1.0f, 0.0f);
            length1 = 0.0f;
            maxLength1 = 0.0f;
            length2 = 0.0f;
            maxLength2 = 0.0f;
            ratio = 1.0f;
            collideConnected = true;
        }

        /// initialize the bodies, anchors, lengths, max lengths, and ratio using the world anchors.
        void initialize(BBBody b1, BBBody b2,
                        final BBVec2 ga1, final BBVec2 ga2,
                        final BBVec2 anchor1, final BBVec2 anchor2,
                        float r) {
            body1 = b1;
            body2 = b2;
            groundAnchor1 = ga1;
            groundAnchor2 = ga2;
            localAnchor1 = body1.getLocalPoint(anchor1);
            localAnchor2 = body2.getLocalPoint(anchor2);
            BBVec2 d1 = sub(anchor1, ga1);
            length1 = d1.length();
            BBVec2 d2 = sub(anchor2, ga2);
            length2 = d2.length();
            ratio = r;
            assert (ratio > FLT_EPSILON);
            float C = length1 + ratio * length2;
            maxLength1 = C - ratio * minPulleyLength;
            maxLength2 = (C - minPulleyLength) / ratio;
        }


        /// The first ground anchor in world coordinates. This point never moves.
        BBVec2 groundAnchor1 = new BBVec2();

        /// The second ground anchor in world coordinates. This point never moves.
        BBVec2 groundAnchor2 = new BBVec2();

        /// The local anchor point relative to body1's origin.
        BBVec2 localAnchor1 = new BBVec2();

        /// The local anchor point relative to body2's origin.
        BBVec2 localAnchor2 = new BBVec2();

        /// The a reference length for the segment attached to body1.
        float length1;

        /// The maximum length of the segment attached to body1.
        float maxLength1;

        /// The a reference length for the segment attached to body2.
        float length2;

        /// The maximum length of the segment attached to body2.
        float maxLength2;

        /// The pulley ratio, used to simulate a block-and-tackle.
        float ratio;
    }

    /// The pulley joint is connected to two bodies and two fixed ground points.
    /// The pulley supports a ratio such that:
    /// length1 + ratio * length2 <= finalant
    /// Yes, the force transmitted is scaled by the ratio.
    /// The pulley also enforces a maximum length limit on both sides. This is
    /// useful to prevent one side of the pulley hitting the top.

    public BBVec2 getAnchor1() {
        return m_bodyA.getWorldPoint(m_localAnchor1);
    }

    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor2);
    }

    public BBVec2 getReactionForce(float inv_dt) {
        BBVec2 P = mul(m_impulse, m_u2);
        return mul(inv_dt, P);
    }

    public float getReactionTorque(float inv_dt) {
        return 0.0f;
    }


    /// Get the first ground anchor.
    public BBVec2 GetGroundAnchor1() {
        return m_groundAnchor1;
    }

    /// Get the second ground anchor.
    public BBVec2 GetGroundAnchor2() {
        return m_groundAnchor2;
    }

    /// Get the current length of the segment attached to body1.
    public float GetLength1() {
        BBVec2 p = m_bodyA.getWorldPoint(m_localAnchor1);
        BBVec2 s = m_groundAnchor1;
        BBVec2 d = sub(p, s);
        return d.length();
    }

    /// Get the current length of the segment attached to body2.
    public float GetLength2() {
        BBVec2 p = m_bodyB.getWorldPoint(m_localAnchor2);
        BBVec2 s = m_groundAnchor2;
        BBVec2 d = sub(p, s);
        return d.length();
    }


    /// Get the pulley ratio.
    public float GetRatio() {
        return m_ratio;
    }


    //--------------- Internals Below -------------------

    public BBPulleyJoint(final BBPulleyJointDef def) {
        super(def);
        m_groundAnchor1 = def.groundAnchor1;
        m_groundAnchor2 = def.groundAnchor2;
        m_localAnchor1 = def.localAnchor1;
        m_localAnchor2 = def.localAnchor2;

        assert (def.ratio != 0.0f);
        m_ratio = def.ratio;

        m_finalant = def.length1 + m_ratio * def.length2;

        m_maxLength1 = min(def.maxLength1, m_finalant - m_ratio * minPulleyLength);
        m_maxLength2 = min(def.maxLength2, (m_finalant - minPulleyLength) / m_ratio);

        m_impulse = 0.0f;
        m_limitImpulse1 = 0.0f;
        m_limitImpulse2 = 0.0f;
    }


    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

        BBVec2 p1 = add(b1.m_sweep.c, r1);
        BBVec2 p2 = add(b2.m_sweep.c, r2);

        BBVec2 s1 = m_groundAnchor1;
        BBVec2 s2 = m_groundAnchor2;

        // Get the pulley axes.
        m_u1 = sub(p1, s1);
        m_u2 = sub(p2, s2);

        float length1 = m_u1.length();
        float length2 = m_u2.length();

        if (length1 > linearSlop) {
            m_u1.mul(1.0f / length1);
        } else {
            m_u1.setZero();
        }

        if (length2 > linearSlop) {
            m_u2.mul(1.0f / length2);
        } else {
            m_u2.setZero();
        }

        float C = m_finalant - length1 - m_ratio * length2;
        if (C > 0.0f) {
            m_state = e_inactiveLimit;
            m_impulse = 0.0f;
        } else {
            m_state = e_atUpperLimit;
        }

        if (length1 < m_maxLength1) {
            m_limitState1 = e_inactiveLimit;
            m_limitImpulse1 = 0.0f;
        } else {
            m_limitState1 = e_atUpperLimit;
        }

        if (length2 < m_maxLength2) {
            m_limitState2 = e_inactiveLimit;
            m_limitImpulse2 = 0.0f;
        } else {
            m_limitState2 = e_atUpperLimit;
        }

        // Compute effective mass.
        float cr1u1 = cross(r1, m_u1);
        float cr2u2 = cross(r2, m_u2);

        m_limitMass1 = b1.m_invMass + b1.m_invI * cr1u1 * cr1u1;
        m_limitMass2 = b2.m_invMass + b2.m_invI * cr2u2 * cr2u2;
        m_pulleyMass = m_limitMass1 + m_ratio * m_ratio * m_limitMass2;
        assert (m_limitMass1 > FLT_EPSILON);
        assert (m_limitMass2 > FLT_EPSILON);
        assert (m_pulleyMass > FLT_EPSILON);
        m_limitMass1 = 1.0f / m_limitMass1;
        m_limitMass2 = 1.0f / m_limitMass2;
        m_pulleyMass = 1.0f / m_pulleyMass;

        if (step.warmStarting) {
            // Scale impulses to support variable time steps.
            m_impulse *= step.dtRatio;
            m_limitImpulse1 *= step.dtRatio;
            m_limitImpulse2 *= step.dtRatio;

            // Warm starting.
            BBVec2 P1 = mul(-(m_impulse + m_limitImpulse1), m_u1);
            BBVec2 P2 = mul((-m_ratio * m_impulse - m_limitImpulse2), m_u2);
            b1.m_linearVelocity.add(mul(b1.m_invMass, P1));
            b1.m_angularVelocity += b1.m_invI * cross(r1, P1);
            b2.m_linearVelocity.add(mul(b2.m_invMass, P2));
            b2.m_angularVelocity += b2.m_invI * cross(r2, P2);
        } else {
            m_impulse = 0.0f;
            m_limitImpulse1 = 0.0f;
            m_limitImpulse2 = 0.0f;
        }
    }

    public void solveVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

        if (m_state == e_atUpperLimit) {
            BBVec2 v1 = add(b1.m_linearVelocity, cross(b1.m_angularVelocity, r1));
            BBVec2 v2 = add(b2.m_linearVelocity, cross(b2.m_angularVelocity, r2));

            float Cdot = -dot(m_u1, v1) - m_ratio * dot(m_u2, v2);
            float impulse = m_pulleyMass * (-Cdot);
            float oldImpulse = m_impulse;
            m_impulse = max(0.0f, m_impulse + impulse);
            impulse = m_impulse - oldImpulse;

            BBVec2 P1 = mul(-impulse, m_u1);
            BBVec2 P2 = mul(-m_ratio * impulse, m_u2);
            b1.m_linearVelocity.add(mul(b1.m_invMass, P1));
            b1.m_angularVelocity += b1.m_invI * cross(r1, P1);
            b2.m_linearVelocity.add(mul(b2.m_invMass, P2));
            b2.m_angularVelocity += b2.m_invI * cross(r2, P2);
        }

        if (m_limitState1 == e_atUpperLimit) {
            BBVec2 v1 = add(b1.m_linearVelocity, cross(b1.m_angularVelocity, r1));

            float Cdot = -dot(m_u1, v1);
            float impulse = -m_limitMass1 * Cdot;
            float oldImpulse = m_limitImpulse1;
            m_limitImpulse1 = max(0.0f, m_limitImpulse1 + impulse);
            impulse = m_limitImpulse1 - oldImpulse;

            BBVec2 P1 = mul(-impulse, m_u1);
            b1.m_linearVelocity.add(mul(b1.m_invMass, P1));
            b1.m_angularVelocity += b1.m_invI * cross(r1, P1);
        }

        if (m_limitState2 == e_atUpperLimit) {
            BBVec2 v2 = add(b2.m_linearVelocity, cross(b2.m_angularVelocity, r2));

            float Cdot = -dot(m_u2, v2);
            float impulse = -m_limitMass2 * Cdot;
            float oldImpulse = m_limitImpulse2;
            m_limitImpulse2 = max(0.0f, m_limitImpulse2 + impulse);
            impulse = m_limitImpulse2 - oldImpulse;

            BBVec2 P2 = mul(-impulse, m_u2);
            b2.m_linearVelocity.add(mul(b2.m_invMass, P2));
            b2.m_angularVelocity += b2.m_invI * cross(r2, P2);
        }
    }

    public boolean solvePositionConstraints(float baumgarte) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 s1 = m_groundAnchor1;
        BBVec2 s2 = m_groundAnchor2;

        float linearError = 0.0f;

        if (m_state == e_atUpperLimit) {
            BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

            BBVec2 p1 = add(b1.m_sweep.c, r1);
            BBVec2 p2 = add(b2.m_sweep.c, r2);

            // Get the pulley axes.
            m_u1 = sub(p1, s1);
            m_u2 = sub(p2, s2);

            float length1 = m_u1.length();
            float length2 = m_u2.length();

            if (length1 > linearSlop) {
                m_u1.mul(1.0f / length1);
            } else {
                m_u1.setZero();
            }

            if (length2 > linearSlop) {
                m_u2.mul(1.0f / length2);
            } else {
                m_u2.setZero();
            }

            float C = m_finalant - length1 - m_ratio * length2;
            linearError = max(linearError, -C);

            C = clamp(C + linearSlop, -maxLinearCorrection, 0.0f);
            float impulse = -m_pulleyMass * C;

            BBVec2 P1 = mul(-impulse, m_u1);
            BBVec2 P2 = mul(-m_ratio * impulse, m_u2);

            b1.m_sweep.c.add(mul(b1.m_invMass, P1));
            b1.m_sweep.a += b1.m_invI * cross(r1, P1);
            b2.m_sweep.c.add(mul(b2.m_invMass, P2));
            b2.m_sweep.a += b2.m_invI * cross(r2, P2);

            b1.synchronizeTransform();
            b2.synchronizeTransform();
        }

        if (m_limitState1 == e_atUpperLimit) {
            BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            BBVec2 p1 = add(b1.m_sweep.c, r1);

            m_u1 = sub(p1, s1);
            float length1 = m_u1.length();

            if (length1 > linearSlop) {
                m_u1.mul(1.0f / length1);
            } else {
                m_u1.setZero();
            }

            float C = m_maxLength1 - length1;
            linearError = max(linearError, -C);
            C = clamp(C + linearSlop, -maxLinearCorrection, 0.0f);
            float impulse = -m_limitMass1 * C;

            BBVec2 P1 = mul(-impulse, m_u1);
            b1.m_sweep.c.add(mul(b1.m_invMass, P1));
            b1.m_sweep.a += b1.m_invI * cross(r1, P1);

            b1.synchronizeTransform();
        }

        if (m_limitState2 == e_atUpperLimit) {
            BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));
            BBVec2 p2 = add(b2.m_sweep.c, r2);

            m_u2 = sub(p2, s2);
            float length2 = m_u2.length();

            if (length2 > linearSlop) {
                m_u2.mul(1.0f / length2);
            } else {
                m_u2.setZero();
            }

            float C = m_maxLength2 - length2;
            linearError = max(linearError, -C);
            C = clamp(C + linearSlop, -maxLinearCorrection, 0.0f);
            float impulse = -m_limitMass2 * C;

            BBVec2 P2 = mul(-impulse, m_u2);
            b2.m_sweep.c.add(mul(b2.m_invMass, P2));
            b2.m_sweep.a += b2.m_invI * cross(r2, P2);

            b2.synchronizeTransform();
        }

        return linearError < linearSlop;
    }


    BBVec2 m_groundAnchor1 = new BBVec2();
    BBVec2 m_groundAnchor2 = new BBVec2();
    BBVec2 m_localAnchor1 = new BBVec2();
    BBVec2 m_localAnchor2 = new BBVec2();

    BBVec2 m_u1 = new BBVec2();
    BBVec2 m_u2 = new BBVec2();

    float m_finalant;
    float m_ratio;

    float m_maxLength1;
    float m_maxLength2;

    // Effective masses
    float m_pulleyMass;
    float m_limitMass1;
    float m_limitMass2;

    // Impulses for accumulation/warm starting.
    float m_impulse;
    float m_limitImpulse1;
    float m_limitImpulse2;

    int m_state;
    int m_limitState1;
    int m_limitState2;


}
