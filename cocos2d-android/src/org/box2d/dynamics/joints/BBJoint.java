package org.box2d.dynamics.joints;

import static org.box2d.common.BBMath.*;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBTimeStep;
import static org.box2d.dynamics.joints.BBDistanceJoint.BBDistanceJointDef;
import static org.box2d.dynamics.joints.BBGearJoint.BBGearJointDef;
import static org.box2d.dynamics.joints.BBLineJoint.BBLineJointDef;
import static org.box2d.dynamics.joints.BBMouseJoint.BBMouseJointDef;
import static org.box2d.dynamics.joints.BBPrismaticJoint.BBPrismaticJointDef;
import static org.box2d.dynamics.joints.BBPulleyJoint.BBPulleyJointDef;
import static org.box2d.dynamics.joints.BBRevoluteJoint.BBRevoluteJointDef;


public abstract class BBJoint {

//    public enum BBJointType
    //    {
    public static final int e_unknownJoint = 0;
    public static final int e_revoluteJoint = 1;
    public static final int e_prismaticJoint = 2;
    public static final int e_distanceJoint = 3;
    public static final int e_pulleyJoint = 4;
    public static final int e_mouseJoint = 5;
    public static final int e_gearJoint = 6;
    public static final int e_lineJoint = 7;
    public static final int e_fixedJoint = 8;
//    }

//    public enum BBLimitState
    //    {
    public static final int e_inactiveLimit = 0;
    public static final int e_atLowerLimit = 1;
    public static final int e_atUpperLimit = 2;
    public static final int e_equalLimits = 3;
//    }

    public static class BBJacobian {
        public BBVec2 linear1 = new BBVec2();
        public float angular1;
        public BBVec2 linear2 = new BBVec2();
        public float angular2;

        public void setZero() {
            linear1.setZero();
            angular1 = 0.0f;
            linear2.setZero();
            angular2 = 0.0f;
        }

        public void Set(final BBVec2 x1, float a1, final BBVec2 x2, float a2) {
            linear1 = x1;
            angular1 = a1;
            linear2 = x2;
            angular2 = a2;
        }

        public float Compute(final BBVec2 x1, float a1, final BBVec2 x2, float a2) {
            return dot(linear1, x1) + angular1 * a1 + dot(linear2, x2) + angular2 * a2;
        }

    }

/// A joint edge is used to connect bodies and joints together
/// in a joint graph where each body is a node and each joint
/// is an edge. A joint edge belongs to a doubly linked list
/// maintained in each attached body. Each joint has two joint

    /// nodes, one for each attached body.
    public static class BBJointEdge {
        public BBBody other;            ///< provides quick access to the other body attached.
        public BBJoint joint;            ///< the joint
        public BBJointEdge prev;        ///< the previous joint edge in the body's joint list
        public BBJointEdge next;        ///< the next joint edge in the body's joint list
    }

    /// Joint definitions are used to finalruct joints.
    public static class BBJointDef {
        public BBJointDef() {
            type = e_unknownJoint;
            userData = null;
            body1 = null;
            body2 = null;
            collideConnected = false;
        }

        /// The joint type is set automatically for concrete joint types.
        public int type;

        /// Use this to attach application specific data to your joints.
        public Object userData;

        /// The first attached body.
        public BBBody body1;

        /// The second attached body.
        public BBBody body2;

        /// set this flag to true if the attached bodies should collide.
        public boolean collideConnected;
    }

    /// The base joint class. Joints are used to constraint two bodies together in
    /// various fashions. Some joints also feature limits and motors.

    /// Get the type of the concrete joint.

    public int getType() {
        return m_type;
    }

    /// Get the first body attached to this joint.
    public BBBody getBody1() {
        return m_bodyA;
    }

    /// Get the second body attached to this joint.
    public BBBody getBody2() {
        return m_bodyB;
    }

    /// Get the anchor point on body1 in world coordinates.
    public abstract BBVec2 getAnchor1();

    /// Get the anchor point on body2 in world coordinates.
    public abstract BBVec2 getAnchor2();

    /// Get the reaction force on body2 at the joint anchor.
    public abstract BBVec2 getReactionForce(float inv_dt);

    /// Get the reaction torque on body2.
    public abstract float getReactionTorque(float inv_dt);

    /// Get the next joint the world joint list.
    public BBJoint getNext() {
        return m_next;
    }

    /// Get the user data pointer.
    public Object getUserData() {
        return m_userData;
    }

    /// set the user data pointer.
    public void setUserData(Object data) {
        m_userData = data;
    }

    public static BBJoint create(final BBJointDef def) {
        BBJoint joint = null;

        switch (def.type) {
            case e_distanceJoint: {
                joint = new BBDistanceJoint((BBDistanceJointDef) def);
            }
            break;

            case e_mouseJoint: {
                joint = new BBMouseJoint((BBMouseJointDef) def);
            }
            break;

            case e_prismaticJoint: {
                joint = new BBPrismaticJoint((BBPrismaticJointDef) def);
            }
            break;

            case e_revoluteJoint: {
                joint = new BBRevoluteJoint((BBRevoluteJointDef) def);
            }
            break;

            case e_pulleyJoint: {
                joint = new BBPulleyJoint((BBPulleyJointDef) def);
            }
            break;

            case e_gearJoint: {
                joint = new BBGearJoint((BBGearJointDef) def);
            }
            break;

            case e_lineJoint: {
                joint = new BBLineJoint((BBLineJointDef) def);
            }
            break;

            default:
                assert false;
                break;
        }

        return joint;
    }

    public static void destroy(BBJoint joint) {
        switch (joint.m_type) {
            case e_distanceJoint:
                break;

            case e_mouseJoint:
                break;

            case e_prismaticJoint:
                break;

            case e_revoluteJoint:
                break;

            case e_pulleyJoint:
                break;

            case e_gearJoint:
                break;

            case e_lineJoint:
                break;

            default:
                assert false;
                break;
        }
    }

    BBJoint(final BBJointDef def) {
        m_type = def.type;
        m_prev = null;
        m_next = null;
        m_bodyA = def.body1;
        m_bodyB = def.body2;
        m_collideConnected = def.collideConnected;
        m_islandFlag = false;
        m_userData = def.userData;

        m_edgeA.joint = null;
        m_edgeA.other = null;
        m_edgeA.prev = null;
        m_edgeA.next = null;

        m_edgeB.joint = null;
        m_edgeB.other = null;
        m_edgeB.prev = null;
        m_edgeB.next = null;
    }

    public abstract void initVelocityConstraints(final BBTimeStep step);

    public abstract void solveVelocityConstraints(final BBTimeStep step);

    // This returns true if the position errors are within tolerance.
    public abstract boolean solvePositionConstraints(float baumgarte);

    protected void computeXForm(BBTransform xf, final BBVec2 center, final BBVec2 localCenter, float angle) {
        xf.R.set(angle);
        xf.position = sub(center, mul(xf.R, localCenter));
    }

    public int m_type;
    public BBJoint m_prev;
    public BBJoint m_next;
    public BBJointEdge m_edgeA;
    public BBJointEdge m_edgeB;
    public BBBody m_bodyA;
    public BBBody m_bodyB;

    public boolean m_islandFlag;
    public boolean m_collideConnected;

    public Object m_userData;

    // Cache here per time step to reduce cache misses.
    protected BBVec2 m_localCenter1 = new BBVec2(), m_localCenter2 = new BBVec2();
    protected float m_invMass1, m_invI1;
    protected float m_invMass2, m_invI2;
}
