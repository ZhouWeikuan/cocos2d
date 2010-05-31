package org.box2d.dynamics.joints;

import org.box2d.common.BBMat22;
import org.box2d.common.BBMat33;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBVec2;
import org.box2d.common.BBVec3;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;

public class BBRevoluteJoint extends BBJoint {
    /// Revolute joint definition. This requires defining an
    /// anchor point where the bodies are joined. The definition
    /// uses local anchor points so that the initial configuration
    /// can violate the constraint slightly. You also need to
    /// specify the initial relative angle for joint limits. This
    /// helps when saving and loading a game.
    /// The local anchor points are measured from the body's origin
    /// rather than the center of mass because:
    /// 1. you might not know where the center of mass will be.
    /// 2. if you add/remove shapes from a body and recompute the mass,
    ///    the joints will be broken.
    public static class BBRevoluteJointDef extends BBJointDef {
        public BBRevoluteJointDef() {
            type = e_revoluteJoint;
            localAnchor1.set(0.0f, 0.0f);
            localAnchor2.set(0.0f, 0.0f);
            referenceAngle = 0.0f;
            lowerAngle = 0.0f;
            upperAngle = 0.0f;
            maxMotorTorque = 0.0f;
            motorSpeed = 0.0f;
            enableLimit = false;
            enableMotor = false;
        }

        /// initialize the bodies, anchors, and reference angle using the world
        /// anchor.
        void initialize(BBBody b1, BBBody b2, final BBVec2 anchor) {
            body1 = b1;
            body2 = b2;
            localAnchor1 = body1.getLocalPoint(anchor);
            localAnchor2 = body2.getLocalPoint(anchor);
            referenceAngle = body2.getAngle() - body1.getAngle();
        }

        /// The local anchor point relative to body1's origin.
        BBVec2 localAnchor1 = new BBVec2();

        /// The local anchor point relative to body2's origin.
        BBVec2 localAnchor2 = new BBVec2();

        /// The body2 angle minus body1 angle in the reference state (radians).
        float referenceAngle;

        /// A flag to enable joint limits.
        boolean enableLimit;

        /// The lower angle for the joint limit (radians).
        float lowerAngle;

        /// The upper angle for the joint limit (radians).
        float upperAngle;

        /// A flag to enable the joint motor.
        boolean enableMotor;

        /// The desired motor speed. Usually in radians per second.
        float motorSpeed;

        /// The maximum motor torque used to achieve the desired motor speed.
        /// Usually in N-m.
        float maxMotorTorque;
    }

/// A revolute joint finalrains to bodies to share a common point while they
/// are free to rotate about the point. The relative rotation about the shared
/// point is the joint angle. You can limit the relative rotation with
/// a joint limit that specifies a lower and upper angle. You can use a motor
/// to drive the relative rotation about the shared point. A maximum motor torque
/// is provided so that infinite forces are not generated.

    public BBVec2 getAnchor1() {
        return m_bodyA.getWorldPoint(m_localAnchor1);
    }

    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor2);
    }

    public BBVec2 getReactionForce(float inv_dt) {
        BBVec2 P = new BBVec2(m_impulse.x, m_impulse.y);
        return mul(inv_dt, P);
    }

    public float getReactionTorque(float inv_dt) {
        return inv_dt * m_impulse.z;
    }

    /// Get the current joint angle in radians.
    public float getJointAngle() {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;
        return b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
    }

    /// Get the current joint angle speed in radians per second.
    public float GetJointSpeed() {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;
        return b2.m_angularVelocity - b1.m_angularVelocity;
    }

    /// Is the joint limit enabled?
    public boolean IsLimitEnabled() {
        return m_enableLimit;
    }

    /// Enable/disable the joint limit.
    public void EnableLimit(boolean flag) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_enableLimit = flag;
    }

    /// Get the lower joint limit in radians.
    public float GetLowerLimit() {
        return m_lowerAngle;
    }

    /// Get the upper joint limit in radians.
    public float GetUpperLimit() {
        return m_upperAngle;
    }

    /// set the joint limits in radians.
    public void SetLimits(float lower, float upper) {
        assert (lower <= upper);
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_lowerAngle = lower;
        m_upperAngle = upper;
    }


    /// Is the joint motor enabled?
    public boolean IsMotorEnabled() {
        return m_enableMotor;
    }

    /// Enable/disable the joint motor.
    public void EnableMotor(boolean flag) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_enableMotor = flag;
    }

    /// set the motor speed in radians per second.
    public void SetMotorSpeed(float speed) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_motorSpeed = speed;
    }

    /// Get the motor speed in radians per second.
    public float GetMotorSpeed() {
        return m_motorSpeed;
    }

    /// set the maximum motor torque, usually in N-m.
    public void SetMaxMotorTorque(float torque) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_maxMotorTorque = torque;
    }

    /// Get the current motor torque, usually in N-m.
    public float GetMotorTorque() {
        return m_motorImpulse;
    }

    //--------------- Internals Below -------------------
    BBRevoluteJoint(final BBRevoluteJointDef def) {
        super(def);
        m_localAnchor1 = def.localAnchor1;
        m_localAnchor2 = def.localAnchor2;
        m_referenceAngle = def.referenceAngle;

        m_impulse.setZero();
        m_motorImpulse = 0.0f;

        m_lowerAngle = def.lowerAngle;
        m_upperAngle = def.upperAngle;
        m_maxMotorTorque = def.maxMotorTorque;
        m_motorSpeed = def.motorSpeed;
        m_enableLimit = def.enableLimit;
        m_enableMotor = def.enableMotor;
        m_limitState = e_inactiveLimit;
    }

    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        if (m_enableMotor || m_enableLimit) {
            // You cannot create a rotation limit between bodies that
            // both have fixed rotation.
            assert (b1.m_invI > 0.0f || b2.m_invI > 0.0f);
        }

        // Compute the effective mass matrix.
        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

        // J = [-I -r1_skew I r2_skew]
        //     [ 0       -1 0       1]
        // r_skew = [-ry; rx]

        // Matlab
        // K = [ m1+r1y^2*i1+m2+r2y^2*i2,  -r1y*i1*r1x-r2y*i2*r2x,          -r1y*i1-r2y*i2]
        //     [  -r1y*i1*r1x-r2y*i2*r2x, m1+r1x^2*i1+m2+r2x^2*i2,           r1x*i1+r2x*i2]
        //     [          -r1y*i1-r2y*i2,           r1x*i1+r2x*i2,                   i1+i2]

        float m1 = b1.m_invMass, m2 = b2.m_invMass;
        float i1 = b1.m_invI, i2 = b2.m_invI;

        m_mass.col1.x = m1 + m2 + r1.y * r1.y * i1 + r2.y * r2.y * i2;
        m_mass.col2.x = -r1.y * r1.x * i1 - r2.y * r2.x * i2;
        m_mass.col3.x = -r1.y * i1 - r2.y * i2;
        m_mass.col1.y = m_mass.col2.x;
        m_mass.col2.y = m1 + m2 + r1.x * r1.x * i1 + r2.x * r2.x * i2;
        m_mass.col3.y = r1.x * i1 + r2.x * i2;
        m_mass.col1.z = m_mass.col3.x;
        m_mass.col2.z = m_mass.col3.y;
        m_mass.col3.z = i1 + i2;

        m_motorMass = 1.0f / (i1 + i2);

        if (!m_enableMotor) {
            m_motorImpulse = 0.0f;
        }

        if (m_enableLimit) {
            float jointAngle = b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
            if (abs(m_upperAngle - m_lowerAngle) < 2.0f * angularSlop) {
                m_limitState = e_equalLimits;
            } else if (jointAngle <= m_lowerAngle) {
                if (m_limitState != e_atLowerLimit) {
                    m_impulse.z = 0.0f;
                }
                m_limitState = e_atLowerLimit;
            } else if (jointAngle >= m_upperAngle) {
                if (m_limitState != e_atUpperLimit) {
                    m_impulse.z = 0.0f;
                }
                m_limitState = e_atUpperLimit;
            } else {
                m_limitState = e_inactiveLimit;
                m_impulse.z = 0.0f;
            }
        } else {
            m_limitState = e_inactiveLimit;
        }

        if (step.warmStarting) {
            // Scale impulses to support a variable time step.
            m_impulse.mul(step.dtRatio);
            m_motorImpulse *= step.dtRatio;

            BBVec2 P = new BBVec2(m_impulse.x, m_impulse.y);

            b1.m_linearVelocity.sub(mul(m1, P));
            b1.m_angularVelocity -= i1 * (cross(r1, P) + m_motorImpulse + m_impulse.z);

            b2.m_linearVelocity.add(mul(m2, P));
            b2.m_angularVelocity += i2 * (cross(r2, P) + m_motorImpulse + m_impulse.z);
        } else {
            m_impulse.setZero();
            m_motorImpulse = 0.0f;
        }
    }

    public void solveVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 v1 = b1.m_linearVelocity;
        float w1 = b1.m_angularVelocity;
        BBVec2 v2 = b2.m_linearVelocity;
        float w2 = b2.m_angularVelocity;

        float m1 = b1.m_invMass, m2 = b2.m_invMass;
        float i1 = b1.m_invI, i2 = b2.m_invI;

        // solve motor constraint.
        if (m_enableMotor & m_limitState != e_equalLimits) {
            float Cdot = w2 - w1 - m_motorSpeed;
            float impulse = m_motorMass * (-Cdot);
            float oldImpulse = m_motorImpulse;
            float maxImpulse = step.dt * m_maxMotorTorque;
            m_motorImpulse = clamp(m_motorImpulse + impulse, -maxImpulse, maxImpulse);
            impulse = m_motorImpulse - oldImpulse;

            w1 -= i1 * impulse;
            w2 += i2 * impulse;
        }

        // solve limit constraint.
        if (m_enableLimit & m_limitState != e_inactiveLimit) {
            BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

            // solve point-to-point constraint
            BBVec2 Cdot1 = sub(sub(add(v2, cross(w2, r2)), v1), cross(w1, r1));
            float Cdot2 = w2 - w1;
            BBVec3 Cdot = new BBVec3(Cdot1.x, Cdot1.y, Cdot2);

            BBVec3 impulse = m_mass.solve33(Cdot.neg());

            if (m_limitState == e_equalLimits) {
                m_impulse.add(impulse);
            } else if (m_limitState == e_atLowerLimit) {
                float newImpulse = m_impulse.z + impulse.z;
                if (newImpulse < 0.0f) {
                    BBVec2 reduced = m_mass.solve22(Cdot1.neg());
                    impulse.x = reduced.x;
                    impulse.y = reduced.y;
                    impulse.z = -m_impulse.z;
                    m_impulse.x += reduced.x;
                    m_impulse.y += reduced.y;
                    m_impulse.z = 0.0f;
                }
            } else if (m_limitState == e_atUpperLimit) {
                float newImpulse = m_impulse.z + impulse.z;
                if (newImpulse > 0.0f) {
                    BBVec2 reduced = m_mass.solve22(Cdot1.neg());
                    impulse.x = reduced.x;
                    impulse.y = reduced.y;
                    impulse.z = -m_impulse.z;
                    m_impulse.x += reduced.x;
                    m_impulse.y += reduced.y;
                    m_impulse.z = 0.0f;
                }
            }

            BBVec2 P = new BBVec2(impulse.x, impulse.y);

            v1.sub(mul(m1, P));
            w1 -= i1 * (cross(r1, P) + impulse.z);

            v2.add(mul(m2, P));
            w2 += i2 * (cross(r2, P) + impulse.z);
        } else {
            BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

            // solve point-to-point constraint
            BBVec2 Cdot = sub(sub(add(v2, cross(w2, r2)), v1), cross(w1, r1));
            BBVec2 impulse = m_mass.solve22(Cdot.neg());

            m_impulse.x += impulse.x;
            m_impulse.y += impulse.y;

            v1.sub(mul(m1, impulse));
            w1 -= i1 * cross(r1, impulse);

            v2.add(mul(m2, impulse));
            w2 += i2 * cross(r2, impulse);
        }

        b1.m_linearVelocity = v1;
        b1.m_angularVelocity = w1;
        b2.m_linearVelocity = v2;
        b2.m_angularVelocity = w2;
    }


    public boolean solvePositionConstraints(float baumgarte) {
        // TODO ERIN block solve with limit.

        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        float angularError = 0.0f;
        float positionError = 0.0f;

        // solve angular limit constraint.
        if (m_enableLimit & m_limitState != e_inactiveLimit) {
            float angle = b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
            float limitImpulse = 0.0f;

            if (m_limitState == e_equalLimits) {
                // Prevent large angular corrections
                float C = clamp(angle - m_lowerAngle, -maxAngularCorrection, maxAngularCorrection);
                limitImpulse = -m_motorMass * C;
                angularError = abs(C);
            } else if (m_limitState == e_atLowerLimit) {
                float C = angle - m_lowerAngle;
                angularError = -C;

                // Prevent large angular corrections and allow some slop.
                C = clamp(C + angularSlop, -maxAngularCorrection, 0.0f);
                limitImpulse = -m_motorMass * C;
            } else if (m_limitState == e_atUpperLimit) {
                float C = angle - m_upperAngle;
                angularError = C;

                // Prevent large angular corrections and allow some slop.
                C = clamp(C - angularSlop, 0.0f, maxAngularCorrection);
                limitImpulse = -m_motorMass * C;
            }

            b1.m_sweep.a -= b1.m_invI * limitImpulse;
            b2.m_sweep.a += b2.m_invI * limitImpulse;

            b1.synchronizeTransform();
            b2.synchronizeTransform();
        }

        // solve point-to-point constraint.
        {
            BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));

            BBVec2 C = sub(sub(add(b2.m_sweep.c, r2), b1.m_sweep.c), r1);
            positionError = C.length();

            float invMass1 = b1.m_invMass, invMass2 = b2.m_invMass;
            float invI1 = b1.m_invI, invI2 = b2.m_invI;

            // Handle large detachment.
            final float k_allowedStretch = 10.0f * linearSlop;
            if (C.lengthSquared() > k_allowedStretch * k_allowedStretch) {
                // Use a particle solution (no rotation).
                BBVec2 u = C;
                u.normalize();
                float k = invMass1 + invMass2;
                assert (k > FLT_EPSILON);
                float m = 1.0f / k;
                BBVec2 impulse = mul(m, C.neg());
                final float k_beta = 0.5f;
                b1.m_sweep.c.sub(mul(k_beta * invMass1, impulse));
                b2.m_sweep.c.add(mul(k_beta * invMass2, impulse));

                C = sub(sub(add(b2.m_sweep.c, r2), b1.m_sweep.c), r1);
            }

            BBMat22 K1 = new BBMat22();
            K1.col1.x = invMass1 + invMass2;
            K1.col2.x = 0.0f;
            K1.col1.y = 0.0f;
            K1.col2.y = invMass1 + invMass2;

            BBMat22 K2 = new BBMat22();
            K2.col1.x = invI1 * r1.y * r1.y;
            K2.col2.x = -invI1 * r1.x * r1.y;
            K2.col1.y = -invI1 * r1.x * r1.y;
            K2.col2.y = invI1 * r1.x * r1.x;

            BBMat22 K3 = new BBMat22();
            K3.col1.x = invI2 * r2.y * r2.y;
            K3.col2.x = -invI2 * r2.x * r2.y;
            K3.col1.y = -invI2 * r2.x * r2.y;
            K3.col2.y = invI2 * r2.x * r2.x;

            BBMat22 K = add(add(K1, K2), K3);
            BBVec2 impulse = K.solve(C.neg());

            b1.m_sweep.c.sub(mul(b1.m_invMass, impulse));
            b1.m_sweep.a -= b1.m_invI * cross(r1, impulse);

            b2.m_sweep.c.add(mul(b2.m_invMass, impulse));
            b2.m_sweep.a += b2.m_invI * cross(r2, impulse);

            b1.synchronizeTransform();
            b2.synchronizeTransform();
        }

        return positionError <= linearSlop & angularError <= angularSlop;
    }


    BBVec2 m_localAnchor1 = new BBVec2();    // relative
    BBVec2 m_localAnchor2 = new BBVec2();
    BBVec3 m_impulse = new BBVec3();
    float m_motorImpulse;

    BBMat33 m_mass = new BBMat33();            // effective mass for point-to-point constraint.
    float m_motorMass;    // effective mass for motor/limit angular constraint.

    boolean m_enableMotor;
    float m_maxMotorTorque;
    float m_motorSpeed;

    boolean m_enableLimit;
    float m_referenceAngle;
    float m_lowerAngle;
    float m_upperAngle;
    int m_limitState;

}
