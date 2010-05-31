package org.box2d.dynamics.joints;

import org.box2d.common.BBMat22;
import org.box2d.common.BBMath;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;


public class BBLineJoint extends BBJoint {
/// Line joint definition. This requires defining a line of
/// motion using an axis and an anchor point. The definition uses local
/// anchor points and a local axis so that the initial configuration
/// can violate the constraint slightly. The joint translation is zero
/// when the local anchor points coincide in world space. Using local

    /// anchors and a local axis helps when saving and loading a game.
    public static class BBLineJointDef extends BBJointDef {
        public BBLineJointDef() {
            type = e_lineJoint;
            localAnchor1.setZero();
            localAnchor2.setZero();
            localAxis1.set(1.0f, 0.0f);
            enableLimit = false;
            lowerTranslation = 0.0f;
            upperTranslation = 0.0f;
            enableMotor = false;
            maxMotorForce = 0.0f;
            motorSpeed = 0.0f;
        }

        /// initialize the bodies, anchors, axis, and reference angle using the world
        /// anchor and world axis.
        void initialize(BBBody b1, BBBody b2, final BBVec2 anchor, final BBVec2 axis) {
            body1 = b1;
            body2 = b2;
            localAnchor1 = body1.getLocalPoint(anchor);
            localAnchor2 = body2.getLocalPoint(anchor);
            localAxis1 = body1.getLocalVector(axis);
        }


        /// The local anchor point relative to body1's origin.
        BBVec2 localAnchor1 = new BBVec2();

        /// The local anchor point relative to body2's origin.
        BBVec2 localAnchor2 = new BBVec2();

        /// The local translation axis in body1.
        BBVec2 localAxis1 = new BBVec2();

        /// Enable/disable the joint limit.
        boolean enableLimit;

        /// The lower translation limit, usually in meters.
        float lowerTranslation;

        /// The upper translation limit, usually in meters.
        float upperTranslation;

        /// Enable/disable the joint motor.
        boolean enableMotor;

        /// The maximum motor torque, usually in N-m.
        float maxMotorForce;

        /// The desired motor speed in radians per second.
        float motorSpeed;
    }

    /// A line joint. This joint provides one degree of freedom: translation
    /// along an axis fixed in body1. You can use a joint limit to restrict
    /// the range of motion and a joint motor to drive the motion or to
    /// model joint friction.

    public BBVec2 getAnchor1() {
        return m_bodyA.getWorldPoint(m_localAnchor1);
    }

    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor2);
    }

    public BBVec2 getReactionForce(float inv_dt) {
        return mul(inv_dt, add(mul(m_impulse.x, m_perp), mul(m_motorImpulse + m_impulse.y, m_axis)));
    }


    public float getReactionTorque(float inv_dt) {
        return 0.0f;
    }

    /// Get the current joint translation, usually in meters.
    public float getJointTranslation() {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 p1 = b1.getWorldPoint(m_localAnchor1);
        BBVec2 p2 = b2.getWorldPoint(m_localAnchor2);
        BBVec2 d = sub(p2, p1);
        BBVec2 axis = b1.getWorldVector(m_localXAxis1);

        return BBMath.dot(d, axis);
    }


    /// Get the current joint translation speed, usually in meters per second.
    public float getJointSpeed() {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 r1 = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
        BBVec2 r2 = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));
        BBVec2 p1 = add(b1.m_sweep.c, r1);
        BBVec2 p2 = add(b2.m_sweep.c, r2);
        BBVec2 d = sub(p2, p1);
        BBVec2 axis = b1.getWorldVector(m_localXAxis1);

        BBVec2 v1 = b1.m_linearVelocity;
        BBVec2 v2 = b2.m_linearVelocity;
        float w1 = b1.m_angularVelocity;
        float w2 = b2.m_angularVelocity;

        return BBMath.dot(d, BBMath.cross(w1, axis)) + BBMath.dot(axis, sub(sub(add(v2, BBMath.cross(w2, r2)), v1), BBMath.cross(w1, r1)));
    }


    /// Is the joint limit enabled?
    public boolean isLimitEnabled() {
        return m_enableLimit;
    }

    /// Enable/disable the joint limit.
    public void enableLimit(boolean flag) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_enableLimit = flag;
    }

    /// Get the lower joint limit, usually in meters.
    public float getLowerLimit() {
        return m_lowerTranslation;
    }

    /// Get the upper joint limit, usually in meters.
    public float GetUpperLimit() {
        return m_upperTranslation;
    }

    /// set the joint limits, usually in meters.
    public void setLimits(float lower, float upper) {
        assert (lower <= upper);
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_lowerTranslation = lower;
        m_upperTranslation = upper;
    }

    /// Is the joint motor enabled?
    public boolean isMotorEnabled() {
        return m_enableMotor;
    }

    /// Enable/disable the joint motor.
    public void enableMotor(boolean flag) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_enableMotor = flag;
    }


    /// set the motor speed, usually in meters per second.
    public void setMotorSpeed(float speed) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_motorSpeed = speed;
    }

    /// Get the motor speed, usually in meters per second.
    public float getMotorSpeed() {
        return m_motorSpeed;
    }


    /// set the maximum motor force, usually in N.
    public void setMaxMotorForce(float force) {
        m_bodyA.wakeUp();
        m_bodyB.wakeUp();
        m_maxMotorForce = force;
    }

    /// Get the current motor force, usually in N.
    public float getMotorForce() {
        return m_motorImpulse;
    }


    //--------------- Internals Below -------------------

    public BBLineJoint(final BBLineJointDef def) {
        super(def);
        m_localAnchor1 = def.localAnchor1;
        m_localAnchor2 = def.localAnchor2;
        m_localXAxis1 = def.localAxis1;
        m_localYAxis1 = BBMath.cross(1.0f, m_localXAxis1);

        m_impulse.setZero();
        m_motorMass = 0.0f;
        m_motorImpulse = 0.0f;

        m_lowerTranslation = def.lowerTranslation;
        m_upperTranslation = def.upperTranslation;
        m_maxMotorForce = def.maxMotorForce;
        m_motorSpeed = def.motorSpeed;
        m_enableLimit = def.enableLimit;
        m_enableMotor = def.enableMotor;
        m_limitState = e_inactiveLimit;

        m_axis.setZero();
        m_perp.setZero();
    }


    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        m_localCenter1 = b1.getLocalCenter();
        m_localCenter2 = b2.getLocalCenter();

        BBTransform xf1 = b1.getTransform();
        BBTransform xf2 = b2.getTransform();

        // Compute the effective masses.
        BBVec2 r1 = mul(xf1.R, sub(m_localAnchor1, m_localCenter1));
        BBVec2 r2 = mul(xf2.R, sub(m_localAnchor2, m_localCenter2));
        BBVec2 d = sub(sub(add(b2.m_sweep.c, r2), b1.m_sweep.c), r1);

        m_invMass1 = b1.m_invMass;
        m_invI1 = b1.m_invI;
        m_invMass2 = b2.m_invMass;
        m_invI2 = b2.m_invI;

        // Compute motor Jacobian and effective mass.
        {
            m_axis = mul(xf1.R, m_localXAxis1);
            m_a1 = BBMath.cross(add(d, r1), m_axis);
            m_a2 = BBMath.cross(r2, m_axis);

            m_motorMass = m_invMass1 + m_invMass2 + m_invI1 * m_a1 * m_a1 + m_invI2 * m_a2 * m_a2;
            if (m_motorMass > FLT_EPSILON) {
                m_motorMass = 1.0f / m_motorMass;
            } else {
                m_motorMass = 0.0f;
            }
        }

        // Prismatic constraint.
        {
            m_perp = mul(xf1.R, m_localYAxis1);

            m_s1 = BBMath.cross(add(d, r1), m_perp);
            m_s2 = BBMath.cross(r2, m_perp);

            float m1 = m_invMass1, m2 = m_invMass2;
            float i1 = m_invI1, i2 = m_invI2;

            float k11 = m1 + m2 + i1 * m_s1 * m_s1 + i2 * m_s2 * m_s2;
            float k12 = i1 * m_s1 * m_a1 + i2 * m_s2 * m_a2;
            float k22 = m1 + m2 + i1 * m_a1 * m_a1 + i2 * m_a2 * m_a2;

            m_K.col1.set(k11, k12);
            m_K.col2.set(k12, k22);
        }

        // Compute motor and limit terms.
        if (m_enableLimit) {
            float jointTranslation = BBMath.dot(m_axis, d);
            if (abs(m_upperTranslation - m_lowerTranslation) < 2.0f * linearSlop) {
                m_limitState = e_equalLimits;
            } else if (jointTranslation <= m_lowerTranslation) {
                if (m_limitState != e_atLowerLimit) {
                    m_limitState = e_atLowerLimit;
                    m_impulse.y = 0.0f;
                }
            } else if (jointTranslation >= m_upperTranslation) {
                if (m_limitState != e_atUpperLimit) {
                    m_limitState = e_atUpperLimit;
                    m_impulse.y = 0.0f;
                }
            } else {
                m_limitState = e_inactiveLimit;
                m_impulse.y = 0.0f;
            }
        } else {
            m_limitState = e_inactiveLimit;
        }

        if (!m_enableMotor) {
            m_motorImpulse = 0.0f;
        }

        if (step.warmStarting) {
            // Account for variable time step.
            m_impulse.mul(step.dtRatio);
            m_motorImpulse *= step.dtRatio;

            BBVec2 P = add(mul(m_impulse.x, m_perp), mul((m_motorImpulse + m_impulse.y), m_axis));
            float L1 = m_impulse.x * m_s1 + (m_motorImpulse + m_impulse.y) * m_a1;
            float L2 = m_impulse.x * m_s2 + (m_motorImpulse + m_impulse.y) * m_a2;

            b1.m_linearVelocity.sub(mul(m_invMass1, P));
            b1.m_angularVelocity -= m_invI1 * L1;

            b2.m_linearVelocity.add(mul(m_invMass2, P));
            b2.m_angularVelocity += m_invI2 * L2;
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

        // solve linear motor constraint.
        if (m_enableMotor & m_limitState != e_equalLimits) {
            float Cdot = BBMath.dot(m_axis, sub(v2, v1)) + m_a2 * w2 - m_a1 * w1;
            float impulse = m_motorMass * (m_motorSpeed - Cdot);
            float oldImpulse = m_motorImpulse;
            float maxImpulse = step.dt * m_maxMotorForce;
            m_motorImpulse = clamp(m_motorImpulse + impulse, -maxImpulse, maxImpulse);
            impulse = m_motorImpulse - oldImpulse;

            BBVec2 P = mul(impulse, m_axis);
            float L1 = impulse * m_a1;
            float L2 = impulse * m_a2;

            v1.sub(mul(m_invMass1, P));
            w1 -= m_invI1 * L1;

            v2.add(mul(m_invMass2, P));
            w2 += m_invI2 * L2;
        }

        float Cdot1 = BBMath.dot(m_perp, sub(v2, v1)) + m_s2 * w2 - m_s1 * w1;

        if (m_enableLimit & m_limitState != e_inactiveLimit) {
            // solve prismatic and limit constraint in block form.
            float Cdot2 = BBMath.dot(m_axis, sub(v2, v1)) + m_a2 * w2 - m_a1 * w1;
            BBVec2 Cdot = new BBVec2(Cdot1, Cdot2);

            BBVec2 f1 = m_impulse;
            BBVec2 df = m_K.solve(Cdot.neg());
            m_impulse.add(df);

            if (m_limitState == e_atLowerLimit) {
                m_impulse.y = max(m_impulse.y, 0.0f);
            } else if (m_limitState == e_atUpperLimit) {
                m_impulse.y = min(m_impulse.y, 0.0f);
            }

            // f2(1) = invK(1,1) * (-Cdot(1) - K(1,2) * (f2(2) - f1(2))) + f1(1)
            float b = -Cdot1 - (m_impulse.y - f1.y) * m_K.col2.x;
            float f2r;
            if (m_K.col1.x != 0.0f) {
                f2r = b / m_K.col1.x + f1.x;
            } else {
                f2r = f1.x;
            }

            m_impulse.x = f2r;

            df = sub(m_impulse, f1);

            BBVec2 P = add(mul(df.x, m_perp), mul(df.y, m_axis));
            float L1 = df.x * m_s1 + df.y * m_a1;
            float L2 = df.x * m_s2 + df.y * m_a2;

            v1.sub(mul(m_invMass1, P));
            w1 -= m_invI1 * L1;

            v2.add(mul(m_invMass2, P));
            w2 += m_invI2 * L2;
        } else {
            // Limit is inactive, just solve the prismatic constraint in block form.
            float df;
            if (m_K.col1.x != 0.0f) {
                df = -Cdot1 / m_K.col1.x;
            } else {
                df = 0.0f;
            }
            m_impulse.x += df;

            BBVec2 P = mul(df, m_perp);
            float L1 = df * m_s1;
            float L2 = df * m_s2;

            v1.sub(mul(m_invMass1, P));
            w1 -= m_invI1 * L1;

            v2.add(mul(m_invMass2, P));
            w2 += m_invI2 * L2;
        }

        b1.m_linearVelocity = v1;
        b1.m_angularVelocity = w1;
        b2.m_linearVelocity = v2;
        b2.m_angularVelocity = w2;
    }

    public boolean solvePositionConstraints(float baumgarte) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        BBVec2 c1 = b1.m_sweep.c;
        float a1 = b1.m_sweep.a;

        BBVec2 c2 = b2.m_sweep.c;
        float a2 = b2.m_sweep.a;

        // solve linear limit constraint.
        float linearError = 0.0f, angularError = 0.0f;
        boolean active = false;
        float C2 = 0.0f;

        BBMat22 R1 = new BBMat22(a1), R2 = new BBMat22(a2);

        BBVec2 r1 = mul(R1, sub(m_localAnchor1, m_localCenter1));
        BBVec2 r2 = mul(R2, sub(m_localAnchor2, m_localCenter2));
        BBVec2 d = sub(sub(add(c2, r2), c1), r1);

        if (m_enableLimit) {
            m_axis = mul(R1, m_localXAxis1);

            m_a1 = BBMath.cross(add(d, r1), m_axis);
            m_a2 = BBMath.cross(r2, m_axis);

            float translation = BBMath.dot(m_axis, d);
            if (abs(m_upperTranslation - m_lowerTranslation) < 2.0f * linearSlop) {
                // Prevent large angular corrections
                C2 = clamp(translation, -maxLinearCorrection, maxLinearCorrection);
                linearError = abs(translation);
                active = true;
            } else if (translation <= m_lowerTranslation) {
                // Prevent large linear corrections and allow some slop.
                C2 = clamp(translation - m_lowerTranslation + linearSlop, -maxLinearCorrection, 0.0f);
                linearError = m_lowerTranslation - translation;
                active = true;
            } else if (translation >= m_upperTranslation) {
                // Prevent large linear corrections and allow some slop.
                C2 = clamp(translation - m_upperTranslation - linearSlop, 0.0f, maxLinearCorrection);
                linearError = translation - m_upperTranslation;
                active = true;
            }
        }

        m_perp = mul(R1, m_localYAxis1);

        m_s1 = BBMath.cross(add(d, r1), m_perp);
        m_s2 = BBMath.cross(r2, m_perp);

        BBVec2 impulse = new BBVec2();
        float C1;
        C1 = BBMath.dot(m_perp, d);

        linearError = max(linearError, abs(C1));
        angularError = 0.0f;

        if (active) {
            float m1 = m_invMass1, m2 = m_invMass2;
            float i1 = m_invI1, i2 = m_invI2;

            float k11 = m1 + m2 + i1 * m_s1 * m_s1 + i2 * m_s2 * m_s2;
            float k12 = i1 * m_s1 * m_a1 + i2 * m_s2 * m_a2;
            float k22 = m1 + m2 + i1 * m_a1 * m_a1 + i2 * m_a2 * m_a2;

            m_K.col1.set(k11, k12);
            m_K.col2.set(k12, k22);

            BBVec2 C = new BBVec2();
            C.x = C1;
            C.y = C2;

            impulse = m_K.solve(C.neg());
        } else {
            float m1 = m_invMass1, m2 = m_invMass2;
            float i1 = m_invI1, i2 = m_invI2;

            float k11 = m1 + m2 + i1 * m_s1 * m_s1 + i2 * m_s2 * m_s2;

            float impulse1;
            if (k11 != 0.0f) {
                impulse1 = -C1 / k11;
            } else {
                impulse1 = 0.0f;
            }

            impulse.x = impulse1;
            impulse.y = 0.0f;
        }

        BBVec2 P = add(mul(impulse.x, m_perp), mul(impulse.y, m_axis));
        float L1 = impulse.x * m_s1 + impulse.y * m_a1;
        float L2 = impulse.x * m_s2 + impulse.y * m_a2;

        c1.sub(mul(m_invMass1, P));
        a1 -= m_invI1 * L1;
        c2.add(mul(m_invMass2, P));
        a2 += m_invI2 * L2;

        // TODO_ERIN remove need for this.
        b1.m_sweep.c = c1;
        b1.m_sweep.a = a1;
        b2.m_sweep.c = c2;
        b2.m_sweep.a = a2;
        b1.synchronizeTransform();
        b2.synchronizeTransform();

        return linearError <= linearSlop & angularError <= angularSlop;
    }


    BBVec2 m_localAnchor1 = new BBVec2();
    BBVec2 m_localAnchor2 = new BBVec2();
    BBVec2 m_localXAxis1 = new BBVec2();
    BBVec2 m_localYAxis1 = new BBVec2();

    BBVec2 m_axis = new BBVec2(), m_perp = new BBVec2();
    float m_s1, m_s2;
    float m_a1, m_a2;

    BBMat22 m_K = new BBMat22();
    BBVec2 m_impulse = new BBVec2();

    float m_motorMass;            // effective mass for motor/limit translational constraint.
    float m_motorImpulse;

    float m_lowerTranslation;
    float m_upperTranslation;
    float m_maxMotorForce;
    float m_motorSpeed;

    boolean m_enableLimit;
    boolean m_enableMotor;
    int m_limitState;


}
