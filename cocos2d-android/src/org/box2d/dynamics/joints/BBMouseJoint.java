package org.box2d.dynamics.joints;

import org.box2d.common.BBMat22;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.FLT_EPSILON;
import static org.box2d.common.BBSettings.PI;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;


public class BBMouseJoint extends BBJoint {
    /// Mouse joint definition. This requires a world target point,
    /// tuning parameters, and the time step.
    public static class BBMouseJointDef extends BBJointDef {
        public BBMouseJointDef() {
            type = e_mouseJoint;
            target.set(0.0f, 0.0f);
            maxForce = 0.0f;
            frequencyHz = 5.0f;
            dampingRatio = 0.7f;
        }

        /// The initial world target point. This is assumed
        /// to coincide with the body anchor initially.
        public BBVec2 target = new BBVec2();

        /// The maximum constraint force that can be exerted
        /// to move the candidate body. Usually you will express
        /// as some multiple of the weight (multiplier * mass * gravity).
        public float maxForce;

        /// The response speed.
        public float frequencyHz;

        /// The damping ratio. 0 = no damping, 1 = critical damping.
        public float dampingRatio;
    }

    /// A mouse joint is used to make a point on a body track a
    /// specified world point. This a soft constraint with a maximum
    /// force. This allows the constraint to stretch and without
    /// applying huge forces.
    /// NOTE: this joint is not documented in the manual because it was
    /// developed to be used in the testbed. If you want to learn how to
    /// use the mouse joint, look at the testbed.

    /// Implements BBJoint.

    public BBVec2 getAnchor1() {
        return m_target;
    }

    /// Implements BBJoint.
    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor);
    }


    /// Implements BBJoint.
    public BBVec2 getReactionForce(float inv_dt) {
        return mul(inv_dt, m_impulse);
    }

    /// Implements BBJoint.
    public float getReactionTorque(float inv_dt) {
        return inv_dt * 0.0f;
    }

    /// Use this to update the target point.
    public void SetTarget(final BBVec2 target) {
        if (m_bodyB.isSleeping()) {
            m_bodyB.wakeUp();
        }
        m_target = target;
    }


    //--------------- Internals Below -------------------

    public BBMouseJoint(final BBMouseJointDef def) {
        super(def);
        m_target = def.target;
        m_localAnchor = mulT(m_bodyB.getTransform(), m_target);

        m_maxForce = def.maxForce;
        m_impulse.setZero();

        m_frequencyHz = def.frequencyHz;
        m_dampingRatio = def.dampingRatio;

        m_beta = 0.0f;
        m_gamma = 0.0f;
    }


    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody b = m_bodyB;

        float mass = b.getMass();

        // Frequency
        float omega = 2.0f * PI * m_frequencyHz;

        // Damping coefficient
        float d = 2.0f * mass * m_dampingRatio * omega;

        // Spring stiffness
        float k = mass * (omega * omega);

        // magic formulas
        // gamma has units of inverse mass.
        // beta has units of inverse time.
        assert (d + step.dt * k > FLT_EPSILON);
        m_gamma = step.dt * (d + step.dt * k);
        if (m_gamma != 0.0f) {
            m_gamma = 1.0f / m_gamma;
        }
        m_beta = step.dt * k * m_gamma;

        // Compute the effective mass matrix.
        BBVec2 r = mul(b.getTransform().R, sub(m_localAnchor, b.getLocalCenter()));

        // K    = [(1/m1 + 1/m2) * eye(2) - skew(r1) * invI1 * skew(r1) - skew(r2) * invI2 * skew(r2)]
        //      = [1/m1+1/m2     0    ] + invI1 * [r1.y*r1.y -r1.x*r1.y] + invI2 * [r1.y*r1.y -r1.x*r1.y]
        //        [    0     1/m1+1/m2]           [-r1.x*r1.y r1.x*r1.x]           [-r1.x*r1.y r1.x*r1.x]
        float invMass = b.m_invMass;
        float invI = b.m_invI;

        BBMat22 K1 = new BBMat22();
        K1.col1.x = invMass;
        K1.col2.x = 0.0f;
        K1.col1.y = 0.0f;
        K1.col2.y = invMass;

        BBMat22 K2 = new BBMat22();
        K2.col1.x = invI * r.y * r.y;
        K2.col2.x = -invI * r.x * r.y;
        K2.col1.y = -invI * r.x * r.y;
        K2.col2.y = invI * r.x * r.x;

        BBMat22 K = add(K1, K2);
        K.col1.x += m_gamma;
        K.col2.y += m_gamma;

        m_mass = K.getInverse();

        m_C = sub(add(b.m_sweep.c, r), m_target);

        // Cheat with some damping
        b.m_angularVelocity *= 0.98f;

        // Warm starting.
        m_impulse.mul(step.dtRatio);
        b.m_linearVelocity.add(mul(invMass, m_impulse));
        b.m_angularVelocity += invI * cross(r, m_impulse);
    }

    public void solveVelocityConstraints(final BBTimeStep step) {
        BBBody b = m_bodyB;

        BBVec2 r = mul(b.getTransform().R, sub(m_localAnchor, b.getLocalCenter()));

        // Cdot = v + cross(w, r)
        BBVec2 Cdot = add(b.m_linearVelocity, cross(b.m_angularVelocity, r));
        BBVec2 impulse = mul(m_mass, add(Cdot, add(mul(m_beta, m_C), mul(m_gamma, m_impulse))).neg());

        BBVec2 oldImpulse = m_impulse;
        m_impulse.add(impulse);
        float maxImpulse = step.dt * m_maxForce;
        if (m_impulse.lengthSquared() > maxImpulse * maxImpulse) {
            m_impulse.mul(maxImpulse / m_impulse.length());
        }
        impulse = sub(m_impulse, oldImpulse);

        b.m_linearVelocity.add(mul(b.m_invMass, impulse));
        b.m_angularVelocity += b.m_invI * cross(r, impulse);
    }

    public boolean solvePositionConstraints(float baumgarte) {
        return true;
    }

    BBVec2 m_localAnchor = new BBVec2();
    BBVec2 m_target = new BBVec2();
    BBVec2 m_impulse = new BBVec2();

    BBMat22 m_mass = new BBMat22();        // effective mass for point-to-point constraint.
    BBVec2 m_C = new BBVec2();                // position error
    float m_maxForce;
    float m_frequencyHz;
    float m_dampingRatio;
    float m_beta;
    float m_gamma;

}
