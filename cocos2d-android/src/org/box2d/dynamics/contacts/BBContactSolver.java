package org.box2d.dynamics.contacts;

import static org.box2d.collision.BBCollision.*;
import org.box2d.collision.shapes.BBShape;
import org.box2d.common.BBMat22;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBFixture;
import org.box2d.dynamics.BBTimeStep;

public class BBContactSolver {

    public static class BBContactConstraintPoint {
        public BBVec2 localPoint = new BBVec2();
        public BBVec2 rA = new BBVec2();
        public BBVec2 rB = new BBVec2();
        public float normalImpulse;
        public float tangentImpulse;
        public float normalMass;
        public float tangentMass;
        public float equalizedMass;
        public float velocityBias;
    }

    public static class BBContactConstraint {
        public BBContactConstraintPoint[] points = new BBContactConstraintPoint[maxManifoldPoints];
        public BBVec2 localPlaneNormal = new BBVec2();
        public BBVec2 localPoint = new BBVec2();
        public BBVec2 normal = new BBVec2();
        public BBMat22 normalMass = new BBMat22();
        public BBMat22 K = new BBMat22();
        public BBBody bodyA;
        public BBBody bodyB;
        public int type;
        public float radius;
        public float friction;
        public float restitution;
        public int pointCount;
        public BBManifold manifold = new BBManifold();

        public BBContactConstraint() {
            for (int i = 0; i < maxManifoldPoints; i++)
                points[i] = new BBContactConstraintPoint();
        }
    }

    public BBContactSolver(final BBTimeStep step, BBContact[] contacts, int contactCount) {
        m_step = step;

        m_constraintCount = contactCount;
        m_constraints = new BBContactConstraint[m_constraintCount];

        for (int i = 0; i < m_constraintCount; ++i) {
            m_constraints[i] = new BBContactConstraint();

            BBContact contact = contacts[i];

            BBFixture fixtureA = contact.m_fixtureA;
            BBFixture fixtureB = contact.m_fixtureB;
            BBShape shapeA = fixtureA.getShape();
            BBShape shapeB = fixtureB.getShape();
            float radiusA = shapeA.m_radius;
            float radiusB = shapeB.m_radius;
            BBBody bodyA = fixtureA.getBody();
            BBBody bodyB = fixtureB.getBody();
            BBManifold manifold = contact.getManifold();

            float friction = mixFriction(fixtureA.getFriction(), fixtureB.getFriction());
            float restitution = mixRestitution(fixtureA.getRestitution(), fixtureB.getRestitution());

            BBVec2 vA = bodyA.m_linearVelocity;
            BBVec2 vB = bodyB.m_linearVelocity;
            float wA = bodyA.m_angularVelocity;
            float wB = bodyB.m_angularVelocity;

            assert (manifold.m_pointCount > 0);

            BBWorldManifold worldManifold = new BBWorldManifold();
            worldManifold.initialize(manifold, bodyA.m_xf, radiusA, bodyB.m_xf, radiusB);

            BBContactConstraint cc = m_constraints[i];
            cc.bodyA = bodyA;
            cc.bodyB = bodyB;
            cc.manifold = manifold;
            cc.normal = worldManifold.m_normal;
            cc.pointCount = manifold.m_pointCount;
            cc.friction = friction;
            cc.restitution = restitution;

            cc.localPlaneNormal = manifold.m_localPlaneNormal;
            cc.localPoint = manifold.m_localPoint;
            cc.radius = radiusA + radiusB;
            cc.type = manifold.m_type;

            for (int j = 0; j < cc.pointCount; ++j) {
                BBManifoldPoint cp = manifold.m_points[j];
                BBContactConstraintPoint ccp = cc.points[j];

                ccp.normalImpulse = cp.m_normalImpulse;
                ccp.tangentImpulse = cp.m_tangentImpulse;

                ccp.localPoint = cp.m_localPoint;

                ccp.rA = sub(worldManifold.m_points[j], bodyA.m_sweep.c);
                ccp.rB = sub(worldManifold.m_points[j], bodyB.m_sweep.c);

                float rnA = cross(ccp.rA, cc.normal);
                float rnB = cross(ccp.rB, cc.normal);
                rnA *= rnA;
                rnB *= rnB;

                float kNormal = bodyA.m_invMass + bodyB.m_invMass + bodyA.m_invI * rnA + bodyB.m_invI * rnB;

                assert (kNormal > FLT_EPSILON);
                ccp.normalMass = 1.0f / kNormal;

                float kEqualized = bodyA.m_mass * bodyA.m_invMass + bodyB.m_mass * bodyB.m_invMass;
                kEqualized += bodyA.m_mass * bodyA.m_invI * rnA + bodyB.m_mass * bodyB.m_invI * rnB;

                assert (kEqualized > FLT_EPSILON);
                ccp.equalizedMass = 1.0f / kEqualized;

                BBVec2 tangent = cross(cc.normal, 1.0f);

                float rtA = cross(ccp.rA, tangent);
                float rtB = cross(ccp.rB, tangent);
                rtA *= rtA;
                rtB *= rtB;

                float kTangent = bodyA.m_invMass + bodyB.m_invMass + bodyA.m_invI * rtA + bodyB.m_invI * rtB;

                assert (kTangent > FLT_EPSILON);
                ccp.tangentMass = 1.0f / kTangent;

                // Setup a velocity bias for restitution.
                ccp.velocityBias = 0.0f;
                float vRel = dot(cc.normal, sub(sub(add(vB, cross(wB, ccp.rB)), vA), cross(wA, ccp.rA)));
                if (vRel < -velocityThreshold) {
                    ccp.velocityBias = -cc.restitution * vRel;
                }
            }

            // If we have two points, then prepare the block solver.
            if (cc.pointCount == 2) {
                BBContactConstraintPoint ccp1 = cc.points[0];
                BBContactConstraintPoint ccp2 = cc.points[1];

                float invMassA = bodyA.m_invMass;
                float invIA = bodyA.m_invI;
                float invMassB = bodyB.m_invMass;
                float invIB = bodyB.m_invI;

                float rn1A = cross(ccp1.rA, cc.normal);
                float rn1B = cross(ccp1.rB, cc.normal);
                float rn2A = cross(ccp2.rA, cc.normal);
                float rn2B = cross(ccp2.rB, cc.normal);

                float k11 = invMassA + invMassB + invIA * rn1A * rn1A + invIB * rn1B * rn1B;
                float k22 = invMassA + invMassB + invIA * rn2A * rn2A + invIB * rn2B * rn2B;
                float k12 = invMassA + invMassB + invIA * rn1A * rn2A + invIB * rn1B * rn2B;

                // Ensure a reasonable condition number.
                final float k_maxConditionNumber = 100.0f;
                if (k11 * k11 < k_maxConditionNumber * (k11 * k22 - k12 * k12)) {
                    // K is safe to invert.
                    cc.K.col1.set(k11, k12);
                    cc.K.col2.set(k12, k22);
                    cc.normalMass = cc.K.getInverse();
                } else {
                    // The constraints are redundant, just use one.
                    // TODO_ERIN use deepest?
                    cc.pointCount = 1;
                }
            }
        }
    }

    public void initVelocityConstraints(final BBTimeStep step) {
        // Warm start.
        for (int i = 0; i < m_constraintCount; ++i) {
            BBContactConstraint c = m_constraints[i];

            BBBody bodyA = c.bodyA;
            BBBody bodyB = c.bodyB;
            float invMassA = bodyA.m_invMass;
            float invIA = bodyA.m_invI;
            float invMassB = bodyB.m_invMass;
            float invIB = bodyB.m_invI;
            BBVec2 normal = c.normal;
            BBVec2 tangent = cross(normal, 1.0f);

            if (step.warmStarting) {
                for (int j = 0; j < c.pointCount; ++j) {
                    BBContactConstraintPoint ccp = c.points[j];
                    ccp.normalImpulse *= step.dtRatio;
                    ccp.tangentImpulse *= step.dtRatio;
                    BBVec2 P = add(mul(ccp.normalImpulse, normal), mul(ccp.tangentImpulse, tangent));
                    bodyA.m_angularVelocity -= invIA * cross(ccp.rA, P);
                    bodyA.m_linearVelocity.sub(mul(invMassA, P));
                    bodyB.m_angularVelocity += invIB * cross(ccp.rB, P);
                    bodyB.m_linearVelocity.add(mul(invMassB, P));
                }
            } else {
                for (int j = 0; j < c.pointCount; ++j) {
                    BBContactConstraintPoint ccp = c.points[j];
                    ccp.normalImpulse = 0.0f;
                    ccp.tangentImpulse = 0.0f;
                }
            }
        }
    }

    public void solveVelocityConstraints() {
        for (int i = 0; i < m_constraintCount; ++i) {
            BBContactConstraint c = m_constraints[i];
            BBBody bodyA = c.bodyA;
            BBBody bodyB = c.bodyB;
            float wA = bodyA.m_angularVelocity;
            float wB = bodyB.m_angularVelocity;
            BBVec2 vA = bodyA.m_linearVelocity;
            BBVec2 vB = bodyB.m_linearVelocity;
            float invMassA = bodyA.m_invMass;
            float invIA = bodyA.m_invI;
            float invMassB = bodyB.m_invMass;
            float invIB = bodyB.m_invI;
            BBVec2 normal = c.normal;
            BBVec2 tangent = cross(normal, 1.0f);
            float friction = c.friction;

            assert (c.pointCount == 1 || c.pointCount == 2);

            // solve tangent constraints
            for (int j = 0; j < c.pointCount; ++j) {
                BBContactConstraintPoint ccp = c.points[j];

                // Relative velocity at contact
                BBVec2 dv = sub(sub(add(vB, cross(wB, ccp.rB)), vA), cross(wA, ccp.rA));

                // Compute tangent force
                float vt = dot(dv, tangent);
                float lambda = ccp.tangentMass * (-vt);

                // clamp the accumulated force
                float maxFriction = friction * ccp.normalImpulse;
                float newImpulse = clamp(ccp.tangentImpulse + lambda, -maxFriction, maxFriction);
                lambda = newImpulse - ccp.tangentImpulse;

                // Apply contact impulse
                BBVec2 P = mul(lambda, tangent);

                vA.sub(mul(invMassA, P));
                wA -= invIA * cross(ccp.rA, P);

                vB.add(mul(invMassB, P));
                wB += invIB * cross(ccp.rB, P);

                ccp.tangentImpulse = newImpulse;
            }

            // solve normal constraints
            if (c.pointCount == 1) {
                BBContactConstraintPoint ccp = c.points[0];

                // Relative velocity at contact
                BBVec2 dv = sub(sub(add(vB, cross(wB, ccp.rB)), vA), cross(wA, ccp.rA));

                // Compute normal impulse
                float vn = dot(dv, normal);
                float lambda = -ccp.normalMass * (vn - ccp.velocityBias);

                // clamp the accumulated impulse
                float newImpulse = max(ccp.normalImpulse + lambda, 0.0f);
                lambda = newImpulse - ccp.normalImpulse;

                // Apply contact impulse
                BBVec2 P = mul(lambda, normal);
                vA.sub(mul(invMassA, P));
                wA -= invIA * cross(ccp.rA, P);

                vB.add(mul(invMassB, P));
                wB += invIB * cross(ccp.rB, P);
                ccp.normalImpulse = newImpulse;
            } else {
                // Block solver developed in collaboration with Dirk Gregorius (back in 01/07 on Box2D_Lite).
                // Build the mini LCP for this contact patch
                //
                // vn = A * x + b, vn >= 0, , vn >= 0, x >= 0 and vn_i * x_i = 0 with i = 1..2
                //
                // A = J * W * JT and J = ( -n, -r1 x n, n, r2 x n )
                // b = vn_0 - velocityBias
                //
                // The system is solved using the "Total enumeration method" (s. Murty). The complementary constraint vn_i * x_i
                // implies that we must have in any solution either vn_i = 0 or x_i = 0. So for the 2D contact problem the cases
                // vn1 = 0 and vn2 = 0, x1 = 0 and x2 = 0, x1 = 0 and vn2 = 0, x2 = 0 and vn1 = 0 need to be tested. The first valid
                // solution that satisfies the problem is chosen.
                //
                // In order to account of the accumulated impulse 'a' (because of the iterative nature of the solver which only requires
                // that the accumulated impulse is clamped and not the incremental impulse) we change the impulse variable (x_i).
                //
                // Substitute:
                //
                // x = x' - a
                //
                // Plug into above equation:
                //
                // vn = A * x + b
                //    = A * (x' - a) + b
                //    = A * x' + b - A * a
                //    = A * x' + b'
                // b' = b - A * a;

                BBContactConstraintPoint cp1 = c.points[0];
                BBContactConstraintPoint cp2 = c.points[1];

                BBVec2 a = new BBVec2(cp1.normalImpulse, cp2.normalImpulse);
                assert (a.x >= 0.0f & a.y >= 0.0f);

                // Relative velocity at contact
                BBVec2 dv1 = sub(sub(add(vB, cross(wB, cp1.rB)), vA), cross(wA, cp1.rA));
                BBVec2 dv2 = sub(sub(add(vB, cross(wB, cp2.rB)), vA), cross(wA, cp2.rA));

                // Compute normal velocity
                float vn1 = dot(dv1, normal);
                float vn2 = dot(dv2, normal);

                BBVec2 b = new BBVec2();
                b.x = vn1 - cp1.velocityBias;
                b.y = vn2 - cp2.velocityBias;
                b.sub(mul(c.K, a));

                final float k_errorTol = 1e-3f;

                for (; ;) {
                    //
                    // Case 1: vn = 0
                    //
                    // 0 = A * x' + b'
                    //
                    // solve for x':
                    //
                    // x' = - inv(A) * b'
                    //
                    BBVec2 x = mul(c.normalMass, b).neg();

                    if (x.x >= 0.0f & x.y >= 0.0f) {
                        // Resubstitute for the incremental impulse
                        BBVec2 d = sub(x, a);

                        // Apply incremental impulse
                        BBVec2 P1 = mul(d.x, normal);
                        BBVec2 P2 = mul(d.y, normal);
                        vA.sub(mul(invMassA, add(P1, P2)));
                        wA -= invIA * (cross(cp1.rA, P1) + cross(cp2.rA, P2));

                        vB.add(mul(invMassB, add(P1, P2)));
                        wB += invIB * (cross(cp1.rB, P1) + cross(cp2.rB, P2));

                        // Accumulate
                        cp1.normalImpulse = x.x;
                        cp2.normalImpulse = x.y;

                        if (B2_DEBUG_SOLVER == true) {
                            // Postconditions
                            dv1 = sub(sub(add(vB, cross(wB, cp1.rB)), vA), cross(wA, cp1.rA));
                            dv2 = sub(sub(add(vB, cross(wB, cp2.rB)), vA), cross(wA, cp2.rA));

                            // Compute normal velocity
                            vn1 = dot(dv1, normal);
                            vn2 = dot(dv2, normal);

                            assert (abs(vn1 - cp1.velocityBias) < k_errorTol);
                            assert (abs(vn2 - cp2.velocityBias) < k_errorTol);
                        }
                        break;
                    }

                    //
                    // Case 2: vn1 = 0 and x2 = 0
                    //
                    //   0 = a11 * x1' + a12 * 0 + b1'
                    // vn2 = a21 * x1' + a22 * 0 + b2'
                    //
                    x.x = -cp1.normalMass * b.x;
                    x.y = 0.0f;
                    vn1 = 0.0f;
                    vn2 = c.K.col1.y * x.x + b.y;

                    if (x.x >= 0.0f & vn2 >= 0.0f) {
                        // Resubstitute for the incremental impulse
                        BBVec2 d = sub(x, a);

                        // Apply incremental impulse
                        BBVec2 P1 = mul(d.x, normal);
                        BBVec2 P2 = mul(d.y, normal);
                        vA.sub(mul(invMassA, add(P1, P2)));
                        wA -= invIA * (cross(cp1.rA, P1) + cross(cp2.rA, P2));

                        vB.add(mul(invMassB, add(P1, P2)));
                        wB += invIB * (cross(cp1.rB, P1) + cross(cp2.rB, P2));

                        // Accumulate
                        cp1.normalImpulse = x.x;
                        cp2.normalImpulse = x.y;

                        if (B2_DEBUG_SOLVER == true) {
                            // Postconditions
                            dv1 = sub(sub(add(vB, cross(wB, cp1.rB)), vA), cross(wA, cp1.rA));

                            // Compute normal velocity
                            vn1 = dot(dv1, normal);

                            assert (abs(vn1 - cp1.velocityBias) < k_errorTol);
                        }
                        break;
                    }


                    //
                    // Case 3: wB = 0 and x1 = 0
                    //
                    // vn1 = a11 * 0 + a12 * x2' + b1'
                    //   0 = a21 * 0 + a22 * x2' + b2'
                    //
                    x.x = 0.0f;
                    x.y = -cp2.normalMass * b.y;
                    vn1 = c.K.col2.x * x.y + b.x;
                    vn2 = 0.0f;

                    if (x.y >= 0.0f & vn1 >= 0.0f) {
                        // Resubstitute for the incremental impulse
                        BBVec2 d = sub(x, a);

                        // Apply incremental impulse
                        BBVec2 P1 = mul(d.x, normal);
                        BBVec2 P2 = mul(d.y, normal);
                        vA.sub(mul(invMassA, add(P1, P2)));
                        wA -= invIA * (cross(cp1.rA, P1) + cross(cp2.rA, P2));

                        vB.add(mul(invMassB, add(P1, P2)));
                        wB += invIB * (cross(cp1.rB, P1) + cross(cp2.rB, P2));

                        // Accumulate
                        cp1.normalImpulse = x.x;
                        cp2.normalImpulse = x.y;

                        if (B2_DEBUG_SOLVER == true) {
                            // Postconditions
                            dv2 = sub(sub(add(vB, cross(wB, cp2.rB)), vA), cross(wA, cp2.rA));

                            // Compute normal velocity
                            vn2 = dot(dv2, normal);

                            assert (abs(vn2 - cp2.velocityBias) < k_errorTol);
                        }
                        break;
                    }

                    //
                    // Case 4: x1 = 0 and x2 = 0
                    //
                    // vn1 = b1
                    // vn2 = b2;
                    x.x = 0.0f;
                    x.y = 0.0f;
                    vn1 = b.x;
                    vn2 = b.y;

                    if (vn1 >= 0.0f & vn2 >= 0.0f) {
                        // Resubstitute for the incremental impulse
                        BBVec2 d = sub(x, a);

                        // Apply incremental impulse
                        BBVec2 P1 = mul(d.x, normal);
                        BBVec2 P2 = mul(d.y, normal);
                        vA.sub(mul(invMassA, add(P1, P2)));
                        wA -= invIA * (cross(cp1.rA, P1) + cross(cp2.rA, P2));

                        vB.add(mul(invMassB, add(P1, P2)));
                        wB += invIB * (cross(cp1.rB, P1) + cross(cp2.rB, P2));

                        // Accumulate
                        cp1.normalImpulse = x.x;
                        cp2.normalImpulse = x.y;

                        break;
                    }

                    // No solution, give up. This is hit sometimes, but it doesn't seem to matter.
                    break;
                }
            }

            bodyA.m_linearVelocity = vA;
            bodyA.m_angularVelocity = wA;
            bodyB.m_linearVelocity = vB;
            bodyB.m_angularVelocity = wB;
        }
    }

    public void FinalizeVelocityConstraints() {
        for (int i = 0; i < m_constraintCount; ++i) {
            BBContactConstraint c = m_constraints[i];
            BBManifold m = c.manifold;

            for (int j = 0; j < c.pointCount; ++j) {
                m.m_points[j].m_normalImpulse = c.points[j].normalImpulse;
                m.m_points[j].m_tangentImpulse = c.points[j].tangentImpulse;
            }
        }
    }

    static class BBPositionSolverManifold {
        void initialize(BBContactConstraint cc) {
            assert (cc.pointCount > 0);

            switch (cc.type) {
                case BBManifold.e_circles: {
                    BBVec2 pointA = cc.bodyA.getWorldPoint(cc.localPoint);
                    BBVec2 pointB = cc.bodyB.getWorldPoint(cc.points[0].localPoint);
                    if (distanceSquared(pointA, pointB) > FLT_EPSILON * FLT_EPSILON) {
                        m_normal = sub(pointB, pointA);
                        m_normal.normalize();
                    } else {
                        m_normal.set(1.0f, 0.0f);
                    }

                    m_points[0] = mul(0.5f, add(pointA, pointB));
                    m_separations[0] = dot(sub(pointB, pointA), m_normal) - cc.radius;
                }
                break;

                case BBManifold.e_faceA: {
                    m_normal = cc.bodyA.getWorldVector(cc.localPlaneNormal);
                    BBVec2 planePoint = cc.bodyA.getWorldPoint(cc.localPoint);

                    for (int i = 0; i < cc.pointCount; ++i) {
                        BBVec2 clipPoint = cc.bodyB.getWorldPoint(cc.points[i].localPoint);
                        m_separations[i] = dot(sub(clipPoint, planePoint), m_normal) - cc.radius;
                        m_points[i] = clipPoint;
                    }
                }
                break;

                case BBManifold.e_faceB: {
                    m_normal = cc.bodyB.getWorldVector(cc.localPlaneNormal);
                    BBVec2 planePoint = cc.bodyB.getWorldPoint(cc.localPoint);

                    for (int i = 0; i < cc.pointCount; ++i) {
                        BBVec2 clipPoint = cc.bodyA.getWorldPoint(cc.points[i].localPoint);
                        m_separations[i] = dot(sub(clipPoint, planePoint), m_normal) - cc.radius;
                        m_points[i] = clipPoint;
                    }

                    // Ensure normal points from A to B
                    m_normal = m_normal.neg();
                }
                break;
            }
        }

        BBVec2 m_normal;
        BBVec2[] m_points = new BBVec2[maxManifoldPoints];
        float[] m_separations = new float[maxManifoldPoints];
    }

    public boolean SolvePositionConstraints(float baumgarte) {
        float minSeparation = 0.0f;

        for (int i = 0; i < m_constraintCount; ++i) {
            BBContactConstraint c = m_constraints[i];
            BBBody bodyA = c.bodyA;
            BBBody bodyB = c.bodyB;

            float invMassA = bodyA.m_mass * bodyA.m_invMass;
            float invIA = bodyA.m_mass * bodyA.m_invI;
            float invMassB = bodyB.m_mass * bodyB.m_invMass;
            float invIB = bodyB.m_mass * bodyB.m_invI;

            BBPositionSolverManifold psm = new BBPositionSolverManifold();
            psm.initialize(c);
            BBVec2 normal = psm.m_normal;

            // solve normal constraints
            for (int j = 0; j < c.pointCount; ++j) {
                BBContactConstraintPoint ccp = c.points[j];

                BBVec2 point = psm.m_points[j];
                float separation = psm.m_separations[j];

                BBVec2 rA = sub(point, bodyA.m_sweep.c);
                BBVec2 rB = sub(point, bodyB.m_sweep.c);

                // Track max constraint error.
                minSeparation = min(minSeparation, separation);

                // Prevent large corrections and allow slop.
                float C = clamp(baumgarte * (separation + linearSlop), -maxLinearCorrection, 0.0f);

                // Compute normal impulse
                float impulse = -ccp.equalizedMass * C;

                BBVec2 P = mul(impulse, normal);

                bodyA.m_sweep.c.sub(mul(invMassA, P));
                bodyA.m_sweep.a -= invIA * cross(rA, P);
                bodyA.synchronizeTransform();

                bodyB.m_sweep.c.add(mul(invMassB, P));
                bodyB.m_sweep.a += invIB * cross(rB, P);
                bodyB.synchronizeTransform();
            }
        }

        // We can't expect minSpeparation >= -linearSlop because we don't
        // push the separation above -linearSlop.
        return minSeparation >= -1.5f * linearSlop;
    }

    public BBTimeStep m_step;
    public BBContactConstraint[] m_constraints;
    public int m_constraintCount;


    private static boolean B2_DEBUG_SOLVER = false;

}
