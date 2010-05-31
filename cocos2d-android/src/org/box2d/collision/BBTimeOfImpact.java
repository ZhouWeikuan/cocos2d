package org.box2d.collision;

import static org.box2d.collision.BBDistance.*;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.FLT_EPSILON;
import org.box2d.common.BBSweep;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

public class BBTimeOfImpact {

    /// Input parameters for BBTimeOfImpact
    public static class BBTOIInput {
        public BBDistanceProxy proxyA = new BBDistanceProxy();
        public BBDistanceProxy proxyB = new BBDistanceProxy();
        public BBSweep sweepA = new BBSweep();
        public BBSweep sweepB = new BBSweep();
        public float tolerance;
    }

    /// Compute the time when two shapes begin to touch or touch at a closer distance.
    /// TOI considers the shape radii. It attempts to have the radii overlap by the tolerance.
    /// Iterations terminate with the overlap is within 0.5 * tolerance. The tolerance should be
    /// smaller than sum of the shape radii.
    /// @warning the sweeps must have the same time interval.
    /// @return the fraction between [0,1] in which the shapes first touch.
    /// fraction=0 means the shapes begin touching/overlapped, and fraction=1 means the shapes don't touch.
    public static float timeOfImpact(final BBTOIInput input) {
        ++toiCalls;

        final BBDistanceProxy proxyA = input.proxyA;
        final BBDistanceProxy proxyB = input.proxyB;

        BBSweep sweepA = input.sweepA;
        BBSweep sweepB = input.sweepB;

        assert (sweepA.t0 == sweepB.t0);
        assert (1.0f - sweepA.t0 > FLT_EPSILON);

        float radius = proxyA.m_radius + proxyB.m_radius;
        float tolerance = input.tolerance;

        float alpha = 0.0f;

        final int k_maxIterations = 1000;    // TODO_ERIN BBSettings
        int iter = 0;
        float target = 0.0f;

        // Prepare input for distance query.
        BBSimplexCache cache = new BBSimplexCache();
        cache.count = 0;
        BBDistanceInput distanceInput = new BBDistanceInput();
        distanceInput.proxyA = input.proxyA;
        distanceInput.proxyB = input.proxyB;
        distanceInput.useRadii = false;

        for (; ;) {
            BBTransform xfA = new BBTransform(), xfB = new BBTransform();
            sweepA.getTransform(xfA, alpha);
            sweepB.getTransform(xfB, alpha);

            // Get the distance between shapes.
            distanceInput.transformA = xfA;
            distanceInput.transformB = xfB;
            BBDistanceOutput distanceOutput = new BBDistanceOutput();
            BBDistance.distance(distanceOutput, cache, distanceInput);

            if (distanceOutput.distance <= 0.0f) {
                alpha = 1.0f;
                break;
            }

            BBSeparationFunction fcn = new BBSeparationFunction();
            fcn.initialize(cache, proxyA, xfA, proxyB, xfB);

            float separation = fcn.evaluate(xfA, xfB);
            if (separation <= 0.0f) {
                alpha = 1.0f;
                break;
            }

            if (iter == 0) {
                // Compute a reasonable target distance to give some breathing room
                // for conservative advancement. We take advantage of the shape radii
                // to create additional clearance.
                if (separation > radius) {
                    target = max(radius - tolerance, 0.75f * radius);
                } else {
                    target = max(separation - tolerance, 0.02f * radius);
                }
            }

            if (separation - target < 0.5f * tolerance) {
                if (iter == 0) {
                    alpha = 1.0f;
                    break;
                }

                break;
            }

            // Compute 1D root of: f(x) - target = 0
            float newAlpha = alpha;
            {
                float x1 = alpha, x2 = 1.0f;

                float f1 = separation;

                sweepA.getTransform(xfA, x2);
                sweepB.getTransform(xfB, x2);
                float f2 = fcn.evaluate(xfA, xfB);

                // If intervals don't overlap at t2, then we are done.
                if (f2 >= target) {
                    alpha = 1.0f;
                    break;
                }

                // Determine when intervals intersect.
                int rootIterCount = 0;
                for (; ;) {
                    // Use a mix of the secant rule and bisection.
                    float x;
                    if ((rootIterCount & 1) != 0) {
                        // Secant rule to improve convergence.
                        x = x1 + (target - f1) * (x2 - x1) / (f2 - f1);
                    } else {
                        // Bisection to guarantee progress.
                        x = 0.5f * (x1 + x2);
                    }

                    sweepA.getTransform(xfA, x);
                    sweepB.getTransform(xfB, x);

                    float f = fcn.evaluate(xfA, xfB);

                    if (abs(f - target) < 0.025f * tolerance) {
                        newAlpha = x;
                        break;
                    }

                    // Ensure we continue to bracket the root.
                    if (f > target) {
                        x1 = x;
                        f1 = f;
                    } else {
                        x2 = x;
                        f2 = f;
                    }

                    ++rootIterCount;
                    ++toiRootIters;

                    if (rootIterCount == 50) {
                        break;
                    }
                }

                toiMaxRootIters = max(toiMaxRootIters, rootIterCount);
            }

            // Ensure significant advancement.
            if (newAlpha < (1.0f + 100.0f * FLT_EPSILON) * alpha) {
                break;
            }

            alpha = newAlpha;

            ++iter;
            ++toiIters;

            if (iter == k_maxIterations) {
                break;
            }
        }

        toiMaxIters = max(toiMaxIters, iter);

        return alpha;
    }

    static int toiCalls, toiIters, toiMaxIters;
    static int toiRootIters, toiMaxRootIters;

    public static class BBSeparationFunction {
//        public enum Type
        //        {
        public static final int e_points = 0;
        public static final int e_faceA = 1;
        public static final int e_faceB = 2;
//        }

        public void initialize(final BBSimplexCache cache,
                               final BBDistanceProxy proxyA, final BBTransform transformA,
                               final BBDistanceProxy proxyB, final BBTransform transformB) {
            m_proxyA = proxyA;
            m_proxyB = proxyB;
            int count = cache.count;
            assert (0 < count & count < 3);

            if (count == 1) {
                m_type = e_points;
                BBVec2 localPointA = m_proxyA.GetVertex(cache.indexA[0]);
                BBVec2 localPointB = m_proxyB.GetVertex(cache.indexB[0]);
                BBVec2 pointA = mul(transformA, localPointA);
                BBVec2 pointB = mul(transformB, localPointB);
                m_axis = sub(pointB, pointA);
                m_axis.normalize();
            } else if (cache.indexB[0] == cache.indexB[1]) {
                // Two points on A and one on B
                m_type = e_faceA;
                BBVec2 localPointA1 = m_proxyA.GetVertex(cache.indexA[0]);
                BBVec2 localPointA2 = m_proxyA.GetVertex(cache.indexA[1]);
                BBVec2 localPointB = m_proxyB.GetVertex(cache.indexB[0]);
                m_localPoint = mul(0.5f, add(localPointA1, localPointA2));
                m_axis = cross(sub(localPointA2, localPointA1), 1.0f);
                m_axis.normalize();

                BBVec2 normal = mul(transformA.R, m_axis);
                BBVec2 pointA = mul(transformA, m_localPoint);
                BBVec2 pointB = mul(transformB, localPointB);

                float s = dot(sub(pointB, pointA), normal);
                if (s < 0.0f) {
                    m_axis = m_axis.neg();
                }
            } else if (cache.indexA[0] == cache.indexA[1]) {
                // Two points on B and one on A.
                m_type = e_faceB;
                BBVec2 localPointA = proxyA.GetVertex(cache.indexA[0]);
                BBVec2 localPointB1 = proxyB.GetVertex(cache.indexB[0]);
                BBVec2 localPointB2 = proxyB.GetVertex(cache.indexB[1]);
                m_localPoint = mul(0.5f, add(localPointB1, localPointB2));
                m_axis = cross(sub(localPointB2, localPointB1), 1.0f);
                m_axis.normalize();

                BBVec2 normal = mul(transformB.R, m_axis);
                BBVec2 pointB = mul(transformB, m_localPoint);
                BBVec2 pointA = mul(transformA, localPointA);

                float s = dot(sub(pointA, pointB), normal);
                if (s < 0.0f) {
                    m_axis = m_axis.neg();
                }
            } else {
                // Two points on B and two points on A.
                // The faces are parallel.
                BBVec2 localPointA1 = m_proxyA.GetVertex(cache.indexA[0]);
                BBVec2 localPointA2 = m_proxyA.GetVertex(cache.indexA[1]);
                BBVec2 localPointB1 = m_proxyB.GetVertex(cache.indexB[0]);
                BBVec2 localPointB2 = m_proxyB.GetVertex(cache.indexB[1]);

                BBVec2 pA = mul(transformA, localPointA1);
                BBVec2 dA = mul(transformA.R, sub(localPointA2, localPointA1));
                BBVec2 pB = mul(transformB, localPointB1);
                BBVec2 dB = mul(transformB.R, sub(localPointB2, localPointB1));

                float a = dot(dA, dA);
                float e = dot(dB, dB);
                BBVec2 r = sub(pA, pB);
                float c = dot(dA, r);
                float f = dot(dB, r);

                float b = dot(dA, dB);
                float denom = a * e - b * b;

                float s = 0.0f;
                if (denom != 0.0f) {
                    s = clamp((b * f - c * e) / denom, 0.0f, 1.0f);
                }

                float t = (b * s + f) / e;

                if (t < 0.0f) {
                    t = 0.0f;
                    s = clamp(-c / a, 0.0f, 1.0f);
                } else if (t > 1.0f) {
                    t = 1.0f;
                    s = clamp((b - c) / a, 0.0f, 1.0f);
                }

                BBVec2 localPointA = add(localPointA1, mul(s, sub(localPointA2, localPointA1)));
                BBVec2 localPointB = add(localPointB1, mul(t, sub(localPointB2, localPointB1)));

                if (s == 0.0f || s == 1.0f) {
                    m_type = e_faceB;
                    m_axis = cross(sub(localPointB2, localPointB1), 1.0f);
                    m_axis.normalize();

                    m_localPoint = localPointB;

                    BBVec2 normal = mul(transformB.R, m_axis);
                    BBVec2 pointA = mul(transformA, localPointA);
                    BBVec2 pointB = mul(transformB, localPointB);

                    float sgn = dot(sub(pointA, pointB), normal);
                    if (sgn < 0.0f) {
                        m_axis = m_axis.neg();
                    }
                } else {
                    m_type = e_faceA;
                    m_axis = cross(sub(localPointA2, localPointA1), 1.0f);
                    m_axis.normalize();

                    m_localPoint = localPointA;

                    BBVec2 normal = mul(transformA.R, m_axis);
                    BBVec2 pointA = mul(transformA, localPointA);
                    BBVec2 pointB = mul(transformB, localPointB);

                    float sgn = dot(sub(pointB, pointA), normal);
                    if (sgn < 0.0f) {
                        m_axis = m_axis.neg();
                    }
                }
            }
        }

        public float evaluate(final BBTransform transformA, final BBTransform transformB) {
            switch (m_type) {
                case e_points: {
                    BBVec2 axisA = mulT(transformA.R, m_axis);
                    BBVec2 axisB = mulT(transformB.R, m_axis.neg());
                    BBVec2 localPointA = m_proxyA.getSupportVertex(axisA);
                    BBVec2 localPointB = m_proxyB.getSupportVertex(axisB);
                    BBVec2 pointA = mul(transformA, localPointA);
                    BBVec2 pointB = mul(transformB, localPointB);
                    return dot(sub(pointB, pointA), m_axis);
                }

                case e_faceA: {
                    BBVec2 normal = mul(transformA.R, m_axis);
                    BBVec2 pointA = mul(transformA, m_localPoint);

                    BBVec2 axisB = mulT(transformB.R, normal.neg());

                    BBVec2 localPointB = m_proxyB.getSupportVertex(axisB);
                    BBVec2 pointB = mul(transformB, localPointB);

                    return dot(sub(pointB, pointA), normal);
                }

                case e_faceB: {
                    BBVec2 normal = mul(transformB.R, m_axis);
                    BBVec2 pointB = mul(transformB, m_localPoint);

                    BBVec2 axisA = mulT(transformA.R, normal.neg());

                    BBVec2 localPointA = m_proxyA.getSupportVertex(axisA);
                    BBVec2 pointA = mul(transformA, localPointA);

                    return dot(sub(pointA, pointB), normal);
                }

                default:
                    assert false;
                    return 0.0f;
            }
        }

        BBDistanceProxy m_proxyA = new BBDistanceProxy();
        BBDistanceProxy m_proxyB = new BBDistanceProxy();
        int m_type;
        BBVec2 m_localPoint = new BBVec2();
        BBVec2 m_axis = new BBVec2();
    }

}
