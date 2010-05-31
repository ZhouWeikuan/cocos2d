package org.box2d.collision;

import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBPolygonShape;
import org.box2d.collision.shapes.BBShape;
import org.box2d.common.BBMath;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.FLT_EPSILON;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

public class BBDistance {

    // GJK using Voronoi regions (Christer Ericson) and Barycentric coordinates.
    static int gjkCalls, gjkIters, gjkMaxIters;


    public static class BBDistanceProxy {
        BBDistanceProxy() {
            m_vertices = null;
            m_count = 0;
            m_radius = 0.0f;
        }

        /// initialize the proxy using the given shape. The shape
        /// must remain in scope while the proxy is in use.
        public void set(final BBShape shape) {
            switch (shape.GetType()) {
                case BBShape.e_circle: {
                    final BBCircleShape circle = (BBCircleShape) shape;
                    m_vertices = new BBVec2[]{circle.m_p};
                    m_count = 1;
                    m_radius = circle.m_radius;
                }
                break;

                case BBShape.e_polygon: {
                    final BBPolygonShape polygon = (BBPolygonShape) shape;
                    m_vertices = polygon.m_vertices;
                    m_count = polygon.m_vertexCount;
                    m_radius = polygon.m_radius;
                }
                break;

                default:
                    assert false;
            }
        }


        /// Get the supporting vertex index in the given direction.
        int getSupport(final BBVec2 d) {
            int bestIndex = 0;
            float bestValue = dot(m_vertices[0], d);
            for (int i = 1; i < m_count; ++i) {
                float value = dot(m_vertices[i], d);
                if (value > bestValue) {
                    bestIndex = i;
                    bestValue = value;
                }
            }

            return bestIndex;
        }

        /// Get the supporting vertex in the given direction.
        final BBVec2 getSupportVertex(final BBVec2 d) {
            int bestIndex = 0;
            float bestValue = dot(m_vertices[0], d);
            for (int i = 1; i < m_count; ++i) {
                float value = dot(m_vertices[i], d);
                if (value > bestValue) {
                    bestIndex = i;
                    bestValue = value;
                }
            }

            return m_vertices[bestIndex];
        }

        /// Get the vertex count.
        int GetVertexCount() {
            return m_count;
        }

        /// Get a vertex by index. Used by distance.
        final BBVec2 GetVertex(int index) {
            assert (0 <= index & index < m_count);
            return m_vertices[index];
        }

        BBVec2[] m_vertices;
        int m_count;
        float m_radius;
    }

/// Used to warm start distance.

    /// set count to zero on first call.
    public static class BBSimplexCache {
        float metric;        ///< length or area
        int count;
        byte[] indexA = new byte[3];    ///< vertices on shape A
        byte[] indexB = new byte[3];    ///< vertices on shape B
    }

/// Input for distance.
/// You have to option to use the shape radii

    /// in the computation. Even
    public static class BBDistanceInput {
        BBDistanceProxy proxyA;
        BBDistanceProxy proxyB;
        BBTransform transformA;
        BBTransform transformB;
        boolean useRadii;
    }

    /// Output for distance.
    public static class BBDistanceOutput {
        BBVec2 pointA = new BBVec2();        ///< closest point on shapeA
        BBVec2 pointB = new BBVec2();        ///< closest point on shapeB
        float distance;
        int iterations;    ///< number of GJK iterations used
    }

/// Compute the closest points between two shapes. Supports any combination of:
/// BBCircleShape, BBPolygonShape, BBEdgeShape. The simplex cache is input/output.

    /// On the first call set BBSimplexCache.count to zero.
    public static void distance(BBDistanceOutput output,
                                BBSimplexCache cache,
                                final BBDistanceInput input) {
        ++gjkCalls;

        final BBDistanceProxy proxyA = input.proxyA;
        final BBDistanceProxy proxyB = input.proxyB;

        BBTransform transformA = input.transformA;
        BBTransform transformB = input.transformB;

        // initialize the simplex.
        BBSimplex simplex = new BBSimplex();
        simplex.readCache(cache, proxyA, transformA, proxyB, transformB);

        // Get simplex vertices as an array.
        BBSimplexVertex[] vertices = simplex.m_vertices;
        final int k_maxIters = 20;

        // These store the vertices of the last simplex so that we
        // can check for duplicates and prevent cycling.
        int[] saveA = new int[3], saveB = new int[3];
        int saveCount = 0;

        BBVec2 closestPoint = simplex.getClosestPoint();
        float distanceSqr1 = closestPoint.lengthSquared();
        float distanceSqr2 = distanceSqr1;

        // Main iteration loop.
        int iter = 0;
        while (iter < k_maxIters) {
            // Copy simplex so we can identify duplicates.
            saveCount = simplex.m_count;
            for (int i = 0; i < saveCount; ++i) {
                saveA[i] = vertices[i].indexA;
                saveB[i] = vertices[i].indexB;
            }

            switch (simplex.m_count) {
                case 1:
                    break;

                case 2:
                    simplex.Solve2();
                    break;

                case 3:
                    simplex.Solve3();
                    break;

                default:
                    assert false;
            }

            // If we have 3 points, then the origin is in the corresponding triangle.
            if (simplex.m_count == 3) {
                break;
            }

            // Compute closest point.
            BBVec2 p = simplex.getClosestPoint();
            distanceSqr2 = p.lengthSquared();

            // Ensure progress
            if (distanceSqr2 >= distanceSqr1) {
                //break;
            }
            distanceSqr1 = distanceSqr2;

            // Get search direction.
            BBVec2 d = simplex.getSearchDirection();

            // Ensure the search direction is numerically fit.
            if (d.lengthSquared() < FLT_EPSILON * FLT_EPSILON) {
                // The origin is probably contained by a line segment
                // or triangle. Thus the shapes are overlapped.

                // We can't return zero here even though there may be overlap.
                // In case the simplex is a point, segment, or triangle it is difficult
                // to determine if the origin is contained in the CSO or very close to it.
                break;
            }

            // Compute a tentative new simplex vertex using support points.
            BBSimplexVertex vertex = vertices[simplex.m_count];
            vertex.indexA = proxyA.getSupport(mulT(transformA.R, d.neg()));
            vertex.wA = mul(transformA, proxyA.GetVertex(vertex.indexA));
            BBVec2 wBLocal;
            vertex.indexB = proxyB.getSupport(mulT(transformB.R, d));
            vertex.wB = mul(transformB, proxyB.GetVertex(vertex.indexB));
            vertex.w = sub(vertex.wB, vertex.wA);

            // Iteration count is equated to the number of support point calls.
            ++iter;
            ++gjkIters;

            // Check for duplicate support points. This is the main termination criteria.
            boolean duplicate = false;
            for (int i = 0; i < saveCount; ++i) {
                if (vertex.indexA == saveA[i] & vertex.indexB == saveB[i]) {
                    duplicate = true;
                    break;
                }
            }

            // If we found a duplicate support point we must exit to avoid cycling.
            if (duplicate) {
                break;
            }

            // New vertex is ok and needed.
            ++simplex.m_count;
        }

        gjkMaxIters = max(gjkMaxIters, iter);

        // Prepare output.

        BBVec2[] pA = new BBVec2[1], pB = new BBVec2[1];
        simplex.GetWitnessPoints(pA, pB);
        output.pointA = pA[0];
        output.pointB = pB[0];

        output.distance = BBMath.distance(output.pointA, output.pointB);
        output.iterations = iter;

        // Cache the simplex.
        simplex.writeCache(cache);

        // Apply radii if requested.
        if (input.useRadii) {
            float rA = proxyA.m_radius;
            float rB = proxyB.m_radius;

            if (output.distance > rA + rB & output.distance > FLT_EPSILON) {
                // Shapes are still no overlapped.
                // Move the witness points to the outer surface.
                output.distance -= rA + rB;
                BBVec2 normal = sub(output.pointB, output.pointA);
                normal.normalize();
                output.pointA.add(mul(rA, normal));
                output.pointB.sub(mul(rB, normal));
            } else {
                // Shapes are overlapped when radii are considered.
                // Move the witness points to the middle.
                BBVec2 p = mul(0.5f, add(output.pointA, output.pointB));
                output.pointA = p;
                output.pointB = p;
                output.distance = 0.0f;
            }
        }
    }


    static class BBSimplexVertex {
        BBVec2 wA;        // support point in proxyA
        BBVec2 wB;        // support point in proxyB
        BBVec2 w;        // wB - wA
        float a;        // barycentric coordinate for closest point
        int indexA;    // wA index
        int indexB;    // wB index
    }

    static class BBSimplex {


        void readCache(final BBSimplexCache cache,
                       final BBDistanceProxy proxyA, final BBTransform transformA,
                       final BBDistanceProxy proxyB, final BBTransform transformB) {
            assert 0 <= cache.count & cache.count <= 3;

            m_vertices[0] = new BBSimplexVertex();
            m_vertices[1] = new BBSimplexVertex();
            m_vertices[2] = new BBSimplexVertex();

            // Copy data from cache.
            m_count = cache.count;
            BBSimplexVertex[] vertices = m_vertices;
            for (int i = 0; i < m_count; ++i) {
                BBSimplexVertex v = vertices[i];
                v.indexA = cache.indexA[i];
                v.indexB = cache.indexB[i];
                BBVec2 wALocal = proxyA.GetVertex(v.indexA);
                BBVec2 wBLocal = proxyB.GetVertex(v.indexB);
                v.wA = mul(transformA, wALocal);
                v.wB = mul(transformB, wBLocal);
                v.w = sub(v.wB, v.wA);
                v.a = 0.0f;
            }

            // Compute the new simplex metric, if it is substantially different than
            // old metric then flush the simplex.
            if (m_count > 1) {
                float metric1 = cache.metric;
                float metric2 = GetMetric();
                if (metric2 < 0.5f * metric1 || 2.0f * metric1 < metric2 || metric2 < FLT_EPSILON) {
                    // Reset the simplex.
                    m_count = 0;
                }
            }

            // If the cache is empty or invalid ...
            if (m_count == 0) {
                BBSimplexVertex v = vertices[0];
                v.indexA = 0;
                v.indexB = 0;
                BBVec2 wALocal = proxyA.GetVertex(0);
                BBVec2 wBLocal = proxyB.GetVertex(0);
                v.wA = mul(transformA, wALocal);
                v.wB = mul(transformB, wBLocal);
                v.w = sub(v.wB, v.wA);
                m_count = 1;
            }

            m_v1 = m_vertices[0];
            m_v2 = m_vertices[1];
            m_v3 = m_vertices[2];
        }

        void writeCache(BBSimplexCache cache) {
            m_vertices[0] = m_v1;
            m_vertices[1] = m_v2;
            m_vertices[2] = m_v3;

            cache.metric = GetMetric();
            cache.count = m_count;
            final BBSimplexVertex[] vertices = m_vertices;
            for (int i = 0; i < m_count; ++i) {
                cache.indexA[i] = (byte) (vertices[i].indexA);
                cache.indexB[i] = (byte) (vertices[i].indexB);
            }
        }

        BBVec2 getSearchDirection() {
            switch (m_count) {
                case 1:
                    return m_v1.w.neg();

                case 2: {
                    BBVec2 e12 = sub(m_v2.w, m_v1.w);
                    float sgn = cross(e12, m_v1.w.neg());
                    if (sgn > 0.0f) {
                        // Origin is left of e12.
                        return cross(1.0f, e12);
                    } else {
                        // Origin is right of e12.
                        return cross(e12, 1.0f);
                    }
                }

                default:
                    assert false;
                    return vec2_zero;
            }
        }

        BBVec2 getClosestPoint() {
            switch (m_count) {
                case 0:
                    assert false;
                    return vec2_zero;

                case 1:
                    return m_v1.w;

                case 2:
                    return add(mul(m_v1.a, m_v1.w), mul(m_v2.a, m_v2.w));

                case 3:
                    return vec2_zero;

                default:
                    assert false;
                    return vec2_zero;
            }
        }

        void GetWitnessPoints(BBVec2[] pA, BBVec2[] pB) {
            switch (m_count) {
                case 0:
                    assert false;
                    break;

                case 1:
                    pA[0] = m_v1.wA;
                    pB[0] = m_v1.wB;
                    break;

                case 2:
                    pA[0] = add(mul(m_v1.a, m_v1.wA), mul(m_v2.a, m_v2.wA));
                    pB[0] = add(mul(m_v1.a, m_v1.wB), mul(m_v2.a, m_v2.wB));
                    break;

                case 3:
                    pA[0] = add(mul(m_v1.a, m_v1.wA), add(mul(m_v2.a, m_v2.wA), mul(m_v3.a, m_v3.wA)));
                    pB[0] = pA[0];
                    break;

                default:
                    assert false;
                    break;
            }
        }

        float GetMetric() {
            switch (m_count) {
                case 0:
                    assert false;
                    return 0.0f;

                case 1:
                    return 0.0f;

                case 2:
                    return BBMath.distance(m_v1.w, m_v2.w);

                case 3:
                    return cross(sub(m_v2.w, m_v1.w), sub(m_v3.w, m_v1.w));

                default:
                    assert false;
                    return 0.0f;
            }
        }

        void Solve2() {
            {
                BBVec2 w1 = m_v1.w;
                BBVec2 w2 = m_v2.w;
                BBVec2 e12 = sub(w2, w1);

                // w1 region
                float d12_2 = -dot(w1, e12);
                if (d12_2 <= 0.0f) {
                    // a2 <= 0, so we clamp it to 0
                    m_v1.a = 1.0f;
                    m_count = 1;
                    return;
                }

                // w2 region
                float d12_1 = dot(w2, e12);
                if (d12_1 <= 0.0f) {
                    // a1 <= 0, so we clamp it to 0
                    m_v2.a = 1.0f;
                    m_count = 1;
                    m_v1 = m_v2;
                    return;
                }

                // Must be in e12 region.
                float inv_d12 = 1.0f / (d12_1 + d12_2);
                m_v1.a = d12_1 * inv_d12;
                m_v2.a = d12_2 * inv_d12;
                m_count = 2;
            }
        }

        void Solve3() {
            BBVec2 w1 = m_v1.w;
            BBVec2 w2 = m_v2.w;
            BBVec2 w3 = m_v3.w;

            // Edge12
            // [1      1     ][a1] = [1]
            // [w1.e12 w2.e12][a2] = [0]
            // a3 = 0
            BBVec2 e12 = sub(w2, w1);
            float w1e12 = dot(w1, e12);
            float w2e12 = dot(w2, e12);
            float d12_1 = w2e12;
            float d12_2 = -w1e12;

            // Edge13
            // [1      1     ][a1] = [1]
            // [w1.e13 w3.e13][a3] = [0]
            // a2 = 0
            BBVec2 e13 = sub(w3, w1);
            float w1e13 = dot(w1, e13);
            float w3e13 = dot(w3, e13);
            float d13_1 = w3e13;
            float d13_2 = -w1e13;

            // Edge23
            // [1      1     ][a2] = [1]
            // [w2.e23 w3.e23][a3] = [0]
            // a1 = 0
            BBVec2 e23 = sub(w3, w2);
            float w2e23 = dot(w2, e23);
            float w3e23 = dot(w3, e23);
            float d23_1 = w3e23;
            float d23_2 = -w2e23;

            // Triangle123
            float n123 = cross(e12, e13);

            float d123_1 = n123 * cross(w2, w3);
            float d123_2 = n123 * cross(w3, w1);
            float d123_3 = n123 * cross(w1, w2);

            // w1 region
            if (d12_2 <= 0.0f & d13_2 <= 0.0f) {
                m_v1.a = 1.0f;
                m_count = 1;
                return;
            }

            // e12
            if (d12_1 > 0.0f & d12_2 > 0.0f & d123_3 <= 0.0f) {
                float inv_d12 = 1.0f / (d12_1 + d12_2);
                m_v1.a = d12_1 * inv_d12;
                m_v2.a = d12_1 * inv_d12;
                m_count = 2;
                return;
            }

            // e13
            if (d13_1 > 0.0f & d13_2 > 0.0f & d123_2 <= 0.0f) {
                float inv_d13 = 1.0f / (d13_1 + d13_2);
                m_v1.a = d13_1 * inv_d13;
                m_v3.a = d13_2 * inv_d13;
                m_count = 2;
                m_v2 = m_v3;
                return;
            }

            // w2 region
            if (d12_1 <= 0.0f & d23_2 <= 0.0f) {
                m_v2.a = 1.0f;
                m_count = 1;
                m_v1 = m_v2;
                return;
            }

            // w3 region
            if (d13_1 <= 0.0f & d23_1 <= 0.0f) {
                m_v3.a = 1.0f;
                m_count = 1;
                m_v1 = m_v3;
                return;
            }

            // e23
            if (d23_1 > 0.0f & d23_2 > 0.0f & d123_1 <= 0.0f) {
                float inv_d23 = 1.0f / (d23_1 + d23_2);
                m_v2.a = d23_1 * inv_d23;
                m_v3.a = d23_2 * inv_d23;
                m_count = 2;
                m_v1 = m_v3;
                return;
            }

            // Must be in triangle123
            float inv_d123 = 1.0f / (d123_1 + d123_2 + d123_3);
            m_v1.a = d123_1 * inv_d123;
            m_v2.a = d123_2 * inv_d123;
            m_v3.a = d123_3 * inv_d123;
            m_count = 3;
        }

        BBSimplexVertex[] m_vertices = new BBSimplexVertex[3];
        BBSimplexVertex m_v1, m_v2, m_v3;
        int m_count;
    }

}
