package org.box2d.dynamics.joints;

import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.linearSlop;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;


public class BBGearJoint extends BBJoint {
    /// Gear joint definition. This definition requires two existing
    /// revolute or prismatic joints (any combination will work).
    /// The provided joints must attach a dynamic body to a static body.
    public static class BBGearJointDef extends BBJointDef {
        BBGearJointDef() {
            type = e_gearJoint;
            joint1 = null;
            joint2 = null;
            ratio = 1.0f;
        }

        /// The first revolute/prismatic joint attached to the gear joint.
        BBJoint joint1;

        /// The second revolute/prismatic joint attached to the gear joint.
        BBJoint joint2;

        /// The gear ratio.
        /// @see b2GearJoint for explanation.
        float ratio;
    }

    /// A gear joint is used to connect two joints together. Either joint
    /// can be a revolute or prismatic joint. You specify a gear ratio
    /// to bind the motions together:
    /// coordinate1 + ratio * coordinate2 = finalant
    /// The ratio can be negative or positive. If one joint is a revolute joint
    /// and the other joint is a prismatic joint, then the ratio will have units
    /// of length or units of 1/length.
    /// @warning The revolute and prismatic joints must be attached to
    /// fixed bodies (which must be body1 on those joints).

    public BBVec2 getAnchor1() {
        return m_bodyA.getWorldPoint(m_localAnchor1);
    }

    public BBVec2 getAnchor2() {
        return m_bodyB.getWorldPoint(m_localAnchor2);
    }

    public BBVec2 getReactionForce(float inv_dt) {
        // TODO ERIN not tested
        BBVec2 P = mul(m_impulse, m_J.linear2);
        return mul(inv_dt, P);
    }

    public float getReactionTorque(float inv_dt) {
        // TODO_ERIN not tested
        BBVec2 r = mul(m_bodyB.getTransform().R, sub(m_localAnchor2, m_bodyB.getLocalCenter()));
        BBVec2 P = mul(m_impulse, m_J.linear2);
        float L = m_impulse * m_J.angular2 - cross(r, P);
        return inv_dt * L;
    }


    /// Get the gear ratio.
    public float GetRatio() {
        return m_ratio;
    }


    //--------------- Internals Below -------------------

    BBGearJoint(final BBGearJointDef def) {
        super(def);
        int type1 = def.joint1.getType();
        int type2 = def.joint2.getType();

        assert (type1 == e_revoluteJoint || type1 == e_prismaticJoint);
        assert (type2 == e_revoluteJoint || type2 == e_prismaticJoint);
        assert (def.joint1.getBody1().isStatic());
        assert (def.joint2.getBody1().isStatic());

        m_revolute1 = null;
        m_prismatic1 = null;
        m_revolute2 = null;
        m_prismatic2 = null;

        float coordinate1, coordinate2;

        m_ground1 = def.joint1.getBody1();
        m_bodyA = def.joint1.getBody2();
        if (type1 == e_revoluteJoint) {
            m_revolute1 = (BBRevoluteJoint) def.joint1;
            m_groundAnchor1 = m_revolute1.m_localAnchor1;
            m_localAnchor1 = m_revolute1.m_localAnchor2;
            coordinate1 = m_revolute1.getJointAngle();
        } else {
            m_prismatic1 = (BBPrismaticJoint) def.joint1;
            m_groundAnchor1 = m_prismatic1.m_localAnchor1;
            m_localAnchor1 = m_prismatic1.m_localAnchor2;
            coordinate1 = m_prismatic1.GetJointTranslation();
        }

        m_ground2 = def.joint2.getBody1();
        m_bodyB = def.joint2.getBody2();
        if (type2 == e_revoluteJoint) {
            m_revolute2 = (BBRevoluteJoint) def.joint2;
            m_groundAnchor2 = m_revolute2.m_localAnchor1;
            m_localAnchor2 = m_revolute2.m_localAnchor2;
            coordinate2 = m_revolute2.getJointAngle();
        } else {
            m_prismatic2 = (BBPrismaticJoint) def.joint2;
            m_groundAnchor2 = m_prismatic2.m_localAnchor1;
            m_localAnchor2 = m_prismatic2.m_localAnchor2;
            coordinate2 = m_prismatic2.GetJointTranslation();
        }

        m_ratio = def.ratio;

        m_finalant = coordinate1 + m_ratio * coordinate2;

        m_impulse = 0.0f;
    }


    public void initVelocityConstraints(final BBTimeStep step) {
        BBBody g1 = m_ground1;
        BBBody g2 = m_ground2;
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        float K = 0.0f;
        m_J.setZero();

        if (m_revolute1 != null) {
            m_J.angular1 = -1.0f;
            K += b1.m_invI;
        } else {
            BBVec2 ug = mul(g1.getTransform().R, m_prismatic1.m_localXAxis1);
            BBVec2 r = mul(b1.getTransform().R, sub(m_localAnchor1, b1.getLocalCenter()));
            float crug = cross(r, ug);
            m_J.linear1 = ug.neg();
            m_J.angular1 = -crug;
            K += b1.m_invMass + b1.m_invI * crug * crug;
        }

        if (m_revolute2 != null) {
            m_J.angular2 = -m_ratio;
            K += m_ratio * m_ratio * b2.m_invI;
        } else {
            BBVec2 ug = mul(g2.getTransform().R, m_prismatic2.m_localXAxis1);
            BBVec2 r = mul(b2.getTransform().R, sub(m_localAnchor2, b2.getLocalCenter()));
            float crug = cross(r, ug);
            m_J.linear2 = mul(-m_ratio, ug);
            m_J.angular2 = -m_ratio * crug;
            K += m_ratio * m_ratio * (b2.m_invMass + b2.m_invI * crug * crug);
        }

        // Compute effective mass.
        m_mass = K > 0.0f ? 1.0f / K : 0.0f;

        if (step.warmStarting) {
            // Warm starting.
            b1.m_linearVelocity.add(mul(b1.m_invMass * m_impulse, m_J.linear1));
            b1.m_angularVelocity += b1.m_invI * m_impulse * m_J.angular1;
            b2.m_linearVelocity.add(mul(b2.m_invMass * m_impulse, m_J.linear2));
            b2.m_angularVelocity += b2.m_invI * m_impulse * m_J.angular2;
        } else {
            m_impulse = 0.0f;
        }
    }

    public void solveVelocityConstraints(final BBTimeStep step) {
        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        float Cdot = m_J.Compute(b1.m_linearVelocity, b1.m_angularVelocity,
                b2.m_linearVelocity, b2.m_angularVelocity);

        float impulse = m_mass * (-Cdot);
        m_impulse += impulse;

        b1.m_linearVelocity.add(mul(b1.m_invMass, mul(impulse, m_J.linear1)));
        b1.m_angularVelocity += b1.m_invI * impulse * m_J.angular1;
        b2.m_linearVelocity.add(mul(b2.m_invMass, mul(impulse, m_J.linear2)));
        b2.m_angularVelocity += b2.m_invI * impulse * m_J.angular2;
    }

    public boolean solvePositionConstraints(float baumgarte) {
        float linearError = 0.0f;

        BBBody b1 = m_bodyA;
        BBBody b2 = m_bodyB;

        float coordinate1, coordinate2;
        if (m_revolute1 != null) {
            coordinate1 = m_revolute1.getJointAngle();
        } else {
            coordinate1 = m_prismatic1.GetJointTranslation();
        }

        if (m_revolute2 != null) {
            coordinate2 = m_revolute2.getJointAngle();
        } else {
            coordinate2 = m_prismatic2.GetJointTranslation();
        }

        float C = m_finalant - (coordinate1 + m_ratio * coordinate2);

        float impulse = m_mass * (-C);

        b1.m_sweep.c.add(mul(b1.m_invMass * impulse, m_J.linear1));
        b1.m_sweep.a += b1.m_invI * impulse * m_J.angular1;
        b2.m_sweep.c.add(mul(b2.m_invMass * impulse, m_J.linear2));
        b2.m_sweep.a += b2.m_invI * impulse * m_J.angular2;

        b1.synchronizeTransform();
        b2.synchronizeTransform();

        // TODO_ERIN not implemented
        return linearError < linearSlop;
    }

    BBBody m_ground1;
    BBBody m_ground2;

    // One of these is null.
    BBRevoluteJoint m_revolute1;
    BBPrismaticJoint m_prismatic1;

    // One of these is null.
    BBRevoluteJoint m_revolute2;
    BBPrismaticJoint m_prismatic2;

    BBVec2 m_groundAnchor1 = new BBVec2();
    BBVec2 m_groundAnchor2 = new BBVec2();

    BBVec2 m_localAnchor1 = new BBVec2();
    BBVec2 m_localAnchor2 = new BBVec2();

    BBJacobian m_J;

    float m_finalant;
    float m_ratio;

    // Effective mass
    float m_mass;

    // Impulse for accumulation/warm starting.
    float m_impulse;

}
