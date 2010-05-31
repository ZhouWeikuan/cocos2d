package org.box2d.dynamics;

import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBVec2;
import org.box2d.common.BBMath;
import static org.box2d.dynamics.BBWorldCallbacks.BBContactImpulse;
import static org.box2d.dynamics.BBWorldCallbacks.BBContactListener;
import org.box2d.dynamics.contacts.BBContact;
import org.box2d.dynamics.contacts.BBContactSolver;
import static org.box2d.dynamics.contacts.BBContactSolver.BBContactConstraint;
import org.box2d.dynamics.joints.BBJoint;

public class BBIsland {
    /// This is an internal structure.
    public static class BBPosition {
        public BBVec2 x = new BBVec2();
        public float a;
    }

    /// This is an internal structure.
    public static class BBVelocity {
        BBVec2 v = new BBVec2();
        float w;
    }

    /// This is an internal class.

    public BBIsland(int bodyCapacity, int contactCapacity, int jointCapacity,
                    BBContactListener listener) {
        m_bodyCapacity = bodyCapacity;
        m_contactCapacity = contactCapacity;
        m_jointCapacity = jointCapacity;
        m_bodyCount = 0;
        m_contactCount = 0;
        m_jointCount = 0;

        m_listener = listener;

        m_bodies = new BBBody[bodyCapacity];
        m_contacts = new BBContact[contactCapacity];
        m_joints = new BBJoint[jointCapacity];

        m_velocities = new BBVelocity[m_bodyCapacity];
        m_positions = new BBPosition[m_bodyCapacity];
    }


    public void Clear() {
        m_bodyCount = 0;
        m_contactCount = 0;
        m_jointCount = 0;
    }

    public void solve(final BBTimeStep step, final BBVec2 gravity, boolean allowSleep) {
        // Integrate velocities and apply damping.
        for (int i = 0; i < m_bodyCount; ++i) {
            BBBody b = m_bodies[i];

            if (b.isStatic())
                continue;

            // Integrate velocities.
            b.m_linearVelocity.add(mul(step.dt, BBMath.add(gravity, mul(b.m_invMass, b.m_force))));
            b.m_angularVelocity += step.dt * b.m_invI * b.m_torque;

            // Reset forces.
            b.m_force.set(0.0f, 0.0f);
            b.m_torque = 0.0f;

            // Apply damping.
            // ODE: dv/dt + c * v = 0
            // Solution: v(t) = v0 * exp(-c * t)
            // Time step: v(t + dt) = v0 * exp(-c * (t + dt)) = v0 * exp(-c * t) * exp(-c * dt) = v * exp(-c * dt)
            // v2 = exp(-c * dt) * v1
            // Taylor expansion:
            // v2 = (1.0f - c * dt) * v1
            b.m_linearVelocity.mul(clamp(1.0f - step.dt * b.m_linearDamping, 0.0f, 1.0f));
            b.m_angularVelocity *= clamp(1.0f - step.dt * b.m_angularDamping, 0.0f, 1.0f);
        }

        BBContactSolver contactSolver = new BBContactSolver(step, m_contacts, m_contactCount);

        // initialize velocity constraints.
        contactSolver.initVelocityConstraints(step);

        for (int i = 0; i < m_jointCount; ++i) {
            m_joints[i].initVelocityConstraints(step);
        }

        // solve velocity constraints.
        for (int i = 0; i < step.velocityIterations; ++i) {
            for (int j = 0; j < m_jointCount; ++j) {
                m_joints[j].solveVelocityConstraints(step);
            }

            contactSolver.solveVelocityConstraints();
        }

        // Post-solve (store impulses for warm starting).
        contactSolver.FinalizeVelocityConstraints();

        // Integrate positions.
        for (int i = 0; i < m_bodyCount; ++i) {
            BBBody b = m_bodies[i];

            if (b.isStatic())
                continue;

            // Check for large velocities.
            BBVec2 translation = mul(step.dt, b.m_linearVelocity);
            if (dot(translation, translation) > maxTranslationSquared) {
                translation.normalize();
                b.m_linearVelocity = mul((maxTranslation * step.inv_dt), translation);
            }

            float rotation = step.dt * b.m_angularVelocity;
            if (rotation * rotation > maxRotationSquared) {
                if (rotation < 0.0) {
                    b.m_angularVelocity = -step.inv_dt * maxRotation;
                } else {
                    b.m_angularVelocity = step.inv_dt * maxRotation;
                }
            }

            // Store positions for continuous collision.
            b.m_sweep.c0 = b.m_sweep.c;
            b.m_sweep.a0 = b.m_sweep.a;

            // Integrate
            b.m_sweep.c.add(mul(step.dt, b.m_linearVelocity));
            b.m_sweep.a += step.dt * b.m_angularVelocity;

            // Compute new transform
            b.synchronizeTransform();

            // Note: shapes are synchronized later.
        }

        // Iterate over constraints.
        for (int i = 0; i < step.positionIterations; ++i) {
            boolean contactsOkay = contactSolver.SolvePositionConstraints(contactBaumgarte);

            boolean jointsOkay = true;
            for (int j = 0; j < m_jointCount; ++j) {
                boolean jointOkay = m_joints[j].solvePositionConstraints(contactBaumgarte);
                jointsOkay = jointsOkay & jointOkay;
            }

            if (contactsOkay & jointsOkay) {
                // Exit early if the position errors are small.
                break;
            }
        }

        report(contactSolver.m_constraints);

        if (allowSleep) {
            float minSleepTime = FLT_MAX;

            final float linTolSqr = linearSleepTolerance * linearSleepTolerance;
            final float angTolSqr = angularSleepTolerance * angularSleepTolerance;

            for (int i = 0; i < m_bodyCount; ++i) {
                BBBody b = m_bodies[i];
                if (b.m_invMass == 0.0f) {
                    continue;
                }

                if ((b.m_flags & BBBody.e_allowSleepFlag) == 0) {
                    b.m_sleepTime = 0.0f;
                    minSleepTime = 0.0f;
                }

                if ((b.m_flags & BBBody.e_allowSleepFlag) == 0 ||
                        b.m_angularVelocity * b.m_angularVelocity > angTolSqr ||
                        dot(b.m_linearVelocity, b.m_linearVelocity) > linTolSqr) {
                    b.m_sleepTime = 0.0f;
                    minSleepTime = 0.0f;
                } else {
                    b.m_sleepTime += step.dt;
                    minSleepTime = min(minSleepTime, b.m_sleepTime);
                }
            }

            if (minSleepTime >= timeToSleep) {
                for (int i = 0; i < m_bodyCount; ++i) {
                    BBBody b = m_bodies[i];
                    b.m_flags |= BBBody.e_sleepFlag;
                    b.m_linearVelocity = vec2_zero;
                    b.m_angularVelocity = 0.0f;
                }
            }
        }
    }


    void solveTOI(BBTimeStep subStep) {
        BBContactSolver contactSolver = new BBContactSolver(subStep, m_contacts, m_contactCount);

        // No warm starting is needed for TOI events because warm
        // starting impulses were applied in the discrete solver.

        // Warm starting for joints is off for now, but we need to
        // call this function to compute Jacobians.
        for (int i = 0; i < m_jointCount; ++i) {
            m_joints[i].initVelocityConstraints(subStep);
        }

        // solve velocity constraints.
        for (int i = 0; i < subStep.velocityIterations; ++i) {
            contactSolver.solveVelocityConstraints();
            for (int j = 0; j < m_jointCount; ++j) {
                m_joints[j].solveVelocityConstraints(subStep);
            }
        }

        // Don't store the TOI contact forces for warm starting
        // because they can be quite large.

        // Integrate positions.
        for (int i = 0; i < m_bodyCount; ++i) {
            BBBody b = m_bodies[i];

            if (b.isStatic())
                continue;

            // Check for large velocities.
            BBVec2 translation = mul(subStep.dt, b.m_linearVelocity);
            if (dot(translation, translation) > maxTranslationSquared) {
                translation.normalize();
                b.m_linearVelocity = mul((maxTranslation * subStep.inv_dt), translation);
            }

            float rotation = subStep.dt * b.m_angularVelocity;
            if (rotation * rotation > maxRotationSquared) {
                if (rotation < 0.0) {
                    b.m_angularVelocity = -subStep.inv_dt * maxRotation;
                } else {
                    b.m_angularVelocity = subStep.inv_dt * maxRotation;
                }
            }

            // Store positions for continuous collision.
            b.m_sweep.c0 = b.m_sweep.c;
            b.m_sweep.a0 = b.m_sweep.a;

            // Integrate
            b.m_sweep.c.add(mul(subStep.dt, b.m_linearVelocity));
            b.m_sweep.a += subStep.dt * b.m_angularVelocity;

            // Compute new transform
            b.synchronizeTransform();

            // Note: shapes are synchronized later.
        }


        // solve position constraints.
        final float k_toiBaumgarte = 0.75f;
        for (int i = 0; i < subStep.positionIterations; ++i) {
            boolean contactsOkay = contactSolver.SolvePositionConstraints(k_toiBaumgarte);
            boolean jointsOkay = true;
            for (int j = 0; j < m_jointCount; ++j) {
                boolean jointOkay = m_joints[j].solvePositionConstraints(k_toiBaumgarte);
                jointsOkay = jointsOkay & jointOkay;
            }

            if (contactsOkay & jointsOkay) {
                break;
            }
        }

        report(contactSolver.m_constraints);
    }


    void add(BBBody body) {
        assert (m_bodyCount < m_bodyCapacity);
        body.m_islandIndex = m_bodyCount;
        m_bodies[m_bodyCount++] = body;
    }

    void add(BBContact contact) {
        assert (m_contactCount < m_contactCapacity);
        m_contacts[m_contactCount++] = contact;
    }

    void add(BBJoint joint) {
        assert (m_jointCount < m_jointCapacity);
        m_joints[m_jointCount++] = joint;
    }

    void report(final BBContactConstraint[] constraints) {
        if (m_listener == null) {
            return;
        }

        for (int i = 0; i < m_contactCount; ++i) {
            BBContact c = m_contacts[i];

            final BBContactConstraint cc = constraints[i];

            BBContactImpulse impulse = new BBContactImpulse();
            for (int j = 0; j < cc.pointCount; ++j) {
                impulse.normalImpulses[j] = cc.points[j].normalImpulse;
                impulse.tangentImpulses[j] = cc.points[j].tangentImpulse;
            }

            m_listener.PostSolve(c, impulse);
        }
    }

    BBContactListener m_listener;

    BBBody[] m_bodies;
    BBContact[] m_contacts;
    BBJoint[] m_joints;

    BBPosition[] m_positions;
    BBVelocity[] m_velocities;

    int m_bodyCount;
    int m_jointCount;
    int m_contactCount;

    int m_bodyCapacity;
    int m_contactCapacity;
    int m_jointCapacity;

    int m_positionIterationCount;
}
