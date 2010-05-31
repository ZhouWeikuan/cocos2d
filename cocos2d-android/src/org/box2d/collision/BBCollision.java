package org.box2d.collision;

import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBPolygonShape;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

public abstract class BBCollision {

    final byte nullFeature = (byte) 0xff;

    /// Contact ids to facilitate warm starting.
    public static class BBContactID {
        /// The features that intersect to form the contact point
        public static class Features {
            public int referenceEdge;        ///< The edge that defines the outward contact normal.
            public int incidentEdge;        ///< The edge most anti-parallel to the reference edge.
            public int incidentVertex;    ///< The vertex (0 or 1) on the incident edge that was clipped.
            public int flip;                ///< A value of 1 indicates that the reference edge is on shape2.
        }

        Features features = new Features();

        //        public int key;					///< Used to quickly compare contact ids.
        public int key() {
            return features.referenceEdge << 24 |
                    features.incidentEdge << 16 |
                    features.incidentVertex << 8 |
                    features.flip;
        }

        public void key(int value) {
            features.referenceEdge = (value >> 24) & 0xff;
            features.incidentEdge = (value >> 16) & 0xff;
            features.incidentVertex = (value >> 8) & 0xff;
            features.flip = value & 0xff;
        }
    }

    /// A manifold point is a contact point belonging to a contact
    /// manifold. It holds details related to the geometry and dynamics
    /// of the contact points.
    /// The local point usage depends on the manifold type:
    /// -e_circles: the local center of circleB
    /// -e_faceA: the local center of cirlceB or the clip point of polygonB
    /// -e_faceB: the clip point of polygonA
    /// This structure is stored across time steps, so we keep it small.
    /// Note: the impulses are used for internal caching and may not
    /// provide reliable contact forces, especially for high speed collisions.
    public static class BBManifoldPoint {
        public BBVec2 m_localPoint = new BBVec2();        ///< usage depends on manifold type
        public float m_normalImpulse;    ///< the non-penetration impulse
        public float m_tangentImpulse;    ///< the friction impulse
        public BBContactID m_id = new BBContactID();            ///< uniquely identifies a contact point between two shapes
    }

    /// A manifold for two touching convex shapes.
    /// Box2D supports multiple types of contact:
    /// - clip point versus plane with radius
    /// - point versus point with radius (circles)
    /// The local point usage depends on the manifold type:
    /// -e_circles: the local center of circleA
    /// -e_faceA: the center of faceA
    /// -e_faceB: the center of faceB
    /// Similarly the local normal usage:
    /// -e_circles: not used
    /// -e_faceA: the normal on polygonA
    /// -e_faceB: the normal on polygonB
    /// We store contacts in this way so that position correction can
    /// account for movement, which is critical for continuous physics.
    /// All contact scenarios must be expressed in one of these types.
    /// This structure is stored across time steps, so we keep it small.
    public static class BBManifold {
        public static final int e_circles = 1;
        public static final int e_faceA = 2;
        public static final int e_faceB = 3;

        public BBManifoldPoint[] m_points = new BBManifoldPoint[maxManifoldPoints];    ///< the points of contact
        public BBVec2 m_localPlaneNormal;                        ///< not use for Type.e_points
        public BBVec2 m_localPoint;                            ///< usage depends on manifold type
        public int m_type;
        public int m_pointCount;                                ///< the number of manifold points

        public BBManifold() {
            for (int i = 0; i < maxManifoldPoints; i++)
                m_points[i] = new BBManifoldPoint();
        }
    }

    /// This is used to compute the current state of a contact manifold.
    public static class BBWorldManifold {
        /// Evaluate the manifold with supplied transforms. This assumes
        /// modest motion from the original state. This does not change the
        /// point count, impulses, etc. The radii must come from the shapes
        /// that generated the manifold.
        public void initialize(final BBManifold manifold,
                               final BBTransform xfA, float radiusA,
                               final BBTransform xfB, float radiusB) {
            if (manifold.m_pointCount == 0) {
                return;
            }

            switch (manifold.m_type) {
                case BBManifold.e_circles: {
                    BBVec2 pointA = mul(xfA, manifold.m_localPoint);
                    BBVec2 pointB = mul(xfB, manifold.m_points[0].m_localPoint);
                    BBVec2 normal = new BBVec2(1.0f, 0.0f);
                    if (distanceSquared(pointA, pointB) > FLT_EPSILON * FLT_EPSILON) {
                        normal = sub(pointB, pointA);
                        normal.normalize();
                    }

                    m_normal = normal;

                    BBVec2 cA = add(pointA, mul(radiusA, normal));
                    BBVec2 cB = sub(pointB, mul(radiusB, normal));
                    m_points[0] = mul(0.5f, add(cA, cB));
                }
                break;

                case BBManifold.e_faceA: {
                    BBVec2 normal = mul(xfA.R, manifold.m_localPlaneNormal);
                    BBVec2 planePoint = mul(xfA, manifold.m_localPoint);

                    // Ensure normal points from A to B.
                    m_normal = normal;

                    for (int i = 0; i < manifold.m_pointCount; ++i) {
                        BBVec2 clipPoint = mul(xfB, manifold.m_points[i].m_localPoint);
                        BBVec2 cA = add(clipPoint, mul(radiusA - dot(sub(clipPoint, planePoint), normal), normal));
                        BBVec2 cB = sub(clipPoint, mul(radiusB, normal));
                        m_points[i] = mul(0.5f, add(cA, cB));
                    }
                }
                break;

                case BBManifold.e_faceB: {
                    BBVec2 normal = mul(xfB.R, manifold.m_localPlaneNormal);
                    BBVec2 planePoint = mul(xfB, manifold.m_localPoint);

                    // Ensure normal points from A to B.
                    m_normal = normal.neg();

                    for (int i = 0; i < manifold.m_pointCount; ++i) {
                        BBVec2 clipPoint = mul(xfA, manifold.m_points[i].m_localPoint);
                        BBVec2 cA = sub(clipPoint, mul(radiusA, normal));
                        BBVec2 cB = add(clipPoint, mul(radiusB - dot(sub(clipPoint, planePoint), normal), normal));
                        m_points[i] = mul(0.5f, add(cA, cB));
                    }
                }
                break;
            }

        }

        public BBVec2 m_normal;                        ///< world vector pointing from A to B
        public BBVec2[] m_points = new BBVec2[maxManifoldPoints];    ///< world contact point (point of intersection)
    }

    /// This is used for determining the state of contact points.
//    public enum bBPointState
    //    {
    public static final int nullState = 0;        ///< point does not exist
    public static final int addState = 1;        ///< point was added in the update
    public static final int persistState = 2;    ///< point persisted across the update
    public static final int removeState = 3;   ///< point was removed in the update
//    }

    /// Compute the point states given two manifolds. The states pertain to the transition from manifold1
    /// to manifold2. So state1 is either persist or remove while state2 is either add or persist.

    public void getPointStates(int[] state1, int[] state2,
                        final BBManifold manifold1, final BBManifold manifold2) {
        for (int i = 0; i < maxManifoldPoints; ++i) {
            state1[i] = nullState;
            state2[i] = nullState;
        }

        // Detect persists and removes.
        for (int i = 0; i < manifold1.m_pointCount; ++i) {
            BBContactID id = manifold1.m_points[i].m_id;

            state1[i] = removeState;

            for (int j = 0; j < manifold2.m_pointCount; ++j) {
                if (manifold2.m_points[j].m_id.key() == id.key()) {
                    state1[i] = persistState;
                    break;
                }
            }
        }

        // Detect persists and adds.
        for (int i = 0; i < manifold2.m_pointCount; ++i) {
            BBContactID id = manifold2.m_points[i].m_id;

            state2[i] = addState;

            for (int j = 0; j < manifold1.m_pointCount; ++j) {
                if (manifold1.m_points[j].m_id.key() == id.key()) {
                    state2[i] = persistState;
                    break;
                }
            }
        }

    }

    /// Used for computing contact manifolds.
    public static class BBClipVertex {
        BBVec2 v = new BBVec2();
        BBContactID id = new BBContactID();
    }

    /// Ray-cast input data.
    public static class BBRayCastInput {
        public BBVec2 p1 = new BBVec2();
        public BBVec2 p2 = new BBVec2();
        public float maxFraction;
    }

    /// Ray-cast output data.
    public static class BBRayCastOutput {
        public BBVec2 normal = new BBVec2();
        public float fraction;
        public boolean hit;
    }

    /// A line segment.
    public static class BBSegment {
        /// Ray cast against this segment with another segment.
        boolean testSegment(float[] lambda, BBVec2[] normal, final BBSegment segment, float maxLambda) {
            BBVec2 s = segment.p1;
            BBVec2 r = sub(segment.p2, s);
            BBVec2 d = sub(p2, p1);
            BBVec2 n = cross(d, 1.0f);

            final float k_slop = 100.0f * FLT_EPSILON;
            float denom = -dot(r, n);

            // Cull back facing collision and ignore parallel segments.
            if (denom > k_slop) {
                // Does the segment intersect the infinite line associated with this segment?
                BBVec2 b = sub(s, p1);
                float a = dot(b, n);

                if (0.0f <= a & a <= maxLambda * denom) {
                    float mu2 = -r.x * b.y + r.y * b.x;

                    // Does the segment intersect this segment?
                    if (-k_slop * denom <= mu2 & mu2 <= denom * (1.0f + k_slop)) {
                        a /= denom;
                        n.normalize();
                        lambda[0] = a;
                        normal[0] = n;
                        return true;
                    }
                }
            }

            return false;

        }

        BBVec2 p1 = new BBVec2();    ///< the starting point
        BBVec2 p2 = new BBVec2();    ///< the ending point
    }

    /// An axis aligned bounding box.
    public static class BBAABB {
        /// Verify that the bounds are sorted.
        boolean isValid() {
            BBVec2 d = sub(upperBound, lowerBound);
            boolean valid = d.x >= 0.0f & d.y >= 0.0f;
            valid = valid & lowerBound.isValid() & upperBound.isValid();
            return valid;
        }

        /// Get the center of the AABB.
        BBVec2 getCenter() {
            return mul(0.5f, add(lowerBound, upperBound));
        }

        /// Get the extents of the AABB (half-widths).
        BBVec2 getExtents() {
            return mul(0.5f, sub(upperBound, lowerBound));
        }

        /// combine two AABBs into this one.
        public void combine(final BBAABB aabb1, final BBAABB aabb2) {
            lowerBound = min(aabb1.lowerBound, aabb2.lowerBound);
            upperBound = max(aabb1.upperBound, aabb2.upperBound);
        }

        /// Does this aabb contain the provided AABB.
        boolean contains(final BBAABB aabb) {
            boolean result = true;
            result = result & lowerBound.x <= aabb.lowerBound.x;
            result = result & lowerBound.y <= aabb.lowerBound.y;
            result = result & aabb.upperBound.x <= upperBound.x;
            result = result & aabb.upperBound.y <= upperBound.y;
            return result;
        }

        void rayCast(BBRayCastOutput output, final BBRayCastInput input) {
            float tmin = -FLT_MAX;
            float tmax = FLT_MAX;

            output.hit = false;

            BBVec2 p = input.p1;
            BBVec2 d = sub(input.p2, input.p1);
            BBVec2 absD = abs(d);

            BBVec2 normal = new BBVec2();

            for (int i = 0; i < 2; ++i) {
                if (absD.idx(i) < FLT_EPSILON) {
                    // Parallel.
                    if (p.idx(i) < lowerBound.idx(i) || upperBound.idx(i) < p.idx(i)) {
                        return;
                    }
                } else {
                    float inv_d = 1.0f / d.idx(i);
                    float t1 = (lowerBound.idx(i) - p.idx(i)) * inv_d;
                    float t2 = (upperBound.idx(i) - p.idx(i)) * inv_d;

                    // Sign of the normal vector.
                    float s = -1.0f;

                    if (t1 > t2) {
                        swap(t1, t2);
                        s = 1.0f;
                    }

                    // Push the min up
                    if (t1 > tmin) {
                        normal.setZero();
                        normal.idx(i, s);
                        tmin = t1;
                    }

                    // Pull the max down
                    tmax = min(tmax, t2);

                    if (tmin > tmax) {
                        return;
                    }
                }
            }

            // Does the ray start inside the box?
            // Does the ray intersect beyond the max fraction?
            if (tmin < 0.0f || input.maxFraction < tmin) {
                return;
            }

            // Intersection.
            output.fraction = tmin;
            output.normal = normal;
            output.hit = true;
        }

        public BBVec2 lowerBound;    ///< the lower vertex
        public BBVec2 upperBound;    ///< the upper vertex
    }

    /// Compute the collision manifold between two circles.
    public abstract void collideCircles(BBManifold manifold,
                                        final BBCircleShape circle1, final BBTransform xf1,
                                        final BBCircleShape circle2, final BBTransform xf2);

    /// Compute the collision manifold between a polygon and a circle.
    public abstract void collidePolygonAndCircle(BBManifold manifold,
                                                 final BBPolygonShape polygon, final BBTransform xf1,
                                                 final BBCircleShape circle, final BBTransform xf2);

    /// Compute the collision manifold between two polygons.
    public abstract void collidePolygons(BBManifold manifold,
                                         final BBPolygonShape polygon1, final BBTransform xf1,
                                         final BBPolygonShape polygon2, final BBTransform xf2);

    /// Clipping for contact manifolds.
    public static int clipSegmentToLine(BBClipVertex[] vOut, final BBClipVertex[] vIn,
                                        final BBVec2 normal, float offset) {
        // Start with no output points
        int numOut = 0;

        // Calculate the distance of end points to the line
        float distance0 = dot(normal, vIn[0].v) - offset;
        float distance1 = dot(normal, vIn[1].v) - offset;

        // If the points are behind the plane
        if (distance0 <= 0.0f) vOut[numOut++] = vIn[0];
        if (distance1 <= 0.0f) vOut[numOut++] = vIn[1];

        // If the points are on different sides of the plane
        if (distance0 * distance1 < 0.0f) {
            // Find intersection point of edge and plane
            float interp = distance0 / (distance0 - distance1);
            vOut[numOut].v = add(vIn[0].v, mul(interp, sub(vIn[1].v, vIn[0].v)));
            if (distance0 > 0.0f) {
                vOut[numOut].id = vIn[0].id;
            } else {
                vOut[numOut].id = vIn[1].id;
            }
            ++numOut;
        }

        return numOut;

    }

    public static boolean testOverlap(final BBAABB a, final BBAABB b) {
        BBVec2 d1, d2;
        d1 = sub(b.lowerBound, a.upperBound);
        d2 = sub(a.lowerBound, b.upperBound);

        return !(d1.x > 0.0f || d1.y > 0.0f) && !(d2.x > 0.0f || d2.y > 0.0f);

    }
}
