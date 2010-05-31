package org.box2d.collision.shapes;

import org.box2d.collision.BBCollision;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

/// A convex polygon. It is assumed that the interior of the polygon is to

/// the left of each edge.
public class BBPolygonShape extends BBShape implements Cloneable {
    public BBPolygonShape() {
        m_type = e_polygon;
        m_radius = polygonRadius;
        m_vertexCount = 0;
        m_centroid.setZero();
    }

    /// Implement BBShape.
    @Override
    public BBShape clone() {
        return super.clone();
    }

    /// Copy vertices. This assumes the vertices define a convex polygon.
    /// It is assumed that the exterior is the the right of each edge.
    public void set(final BBVec2[] vertices, int vertexCount) {
        assert (2 <= vertexCount & vertexCount <= maxPolygonVertices);
        m_vertexCount = vertexCount;

        // Copy vertices.
        System.arraycopy(vertices, 0, m_vertices, 0, m_vertexCount);

        // Compute normals. Ensure the edges have non-zero length.
        for (int i = 0; i < m_vertexCount; ++i) {
            int i1 = i;
            int i2 = i + 1 < m_vertexCount ? i + 1 : 0;
            BBVec2 edge = sub(m_vertices[i2], m_vertices[i1]);
            assert (edge.lengthSquared() > FLT_EPSILON * FLT_EPSILON);
            m_normals[i] = cross(edge, 1.0f);
            m_normals[i].normalize();
        }


        // Compute the polygon centroid.
        m_centroid = computeCentroid(m_vertices, m_vertexCount);
    }

    /// Build vertices to represent an axis-aligned box.
    /// @param hx the half-width.
    /// @param hy the half-height.
    public void setAsBox(float hx, float hy) {
        m_vertexCount = 4;
        for (int i = 0; i < m_vertexCount; i++) {
            m_vertices[i] = new BBVec2();
            m_normals[i] = new BBVec2();
        }
        m_vertices[0].set(-hx, -hy);
        m_vertices[1].set(hx, -hy);
        m_vertices[2].set(hx, hy);
        m_vertices[3].set(-hx, hy);
        m_normals[0].set(0.0f, -1.0f);
        m_normals[1].set(1.0f, 0.0f);
        m_normals[2].set(0.0f, 1.0f);
        m_normals[3].set(-1.0f, 0.0f);
        m_centroid.setZero();
    }

    /// Build vertices to represent an oriented box.
    /// @param hx the half-width.
    /// @param hy the half-height.
    /// @param center the center of the box in local coordinates.
    /// @param angle the rotation of the box in local coordinates.
    public void setAsBox(float hx, float hy, final BBVec2 center, float angle) {
        m_vertexCount = 4;
        for (int i = 0; i < m_vertexCount; i++) {
            m_vertices[i] = new BBVec2();
            m_normals[i] = new BBVec2();
        }
        m_vertices[0].set(-hx, -hy);
        m_vertices[1].set(hx, -hy);
        m_vertices[2].set(hx, hy);
        m_vertices[3].set(-hx, hy);
        m_normals[0].set(0.0f, -1.0f);
        m_normals[1].set(1.0f, 0.0f);
        m_normals[2].set(0.0f, 1.0f);
        m_normals[3].set(-1.0f, 0.0f);
        m_centroid = center;

        BBTransform xf = new BBTransform();
        xf.position = center;
        xf.R.set(angle);

        // Transform vertices and normals.
        for (int i = 0; i < m_vertexCount; ++i) {
            m_vertices[i] = mul(xf, m_vertices[i]);
            m_normals[i] = mul(xf.R, m_normals[i]);
        }
    }

    /// set this as a single edge.
    public void setAsEdge(final BBVec2 v1, final BBVec2 v2) {
        m_vertexCount = 2;
        m_vertices[0] = v1;
        m_vertices[1] = v2;
        m_centroid = mul(0.5f, add(v1, v2));
        m_normals[0] = cross(sub(v2, v1), 1.0f);
        m_normals[0].normalize();
        m_normals[1] = m_normals[0].neg();
    }

    /// @see BBShape.TestPoint
    public boolean testPoint(final BBTransform xf, final BBVec2 p) {
        BBVec2 pLocal = mulT(xf.R, sub(p, xf.position));

        for (int i = 0; i < m_vertexCount; ++i) {
            float dot = dot(m_normals[i], sub(pLocal, m_vertices[i]));
            if (dot > 0.0f) {
                return false;
            }
        }

        return true;
    }

    /// Implement BBShape.
    public void rayCast(BBCollision.BBRayCastOutput output, final BBCollision.BBRayCastInput input, final BBTransform xf) {
        float lower = 0.0f, upper = input.maxFraction;

        // Put the ray into the polygon's frame of reference.
        BBVec2 p1 = mulT(xf.R, sub(input.p1, xf.position));
        BBVec2 p2 = mulT(xf.R, sub(input.p2, xf.position));
        BBVec2 d = sub(p2, p1);
        int index = -1;

        output.hit = false;

        for (int i = 0; i < m_vertexCount; ++i) {
            // p = p1 + a * d
            // dot(normal, p - v) = 0
            // dot(normal, p1 - v) + a * dot(normal, d) = 0
            float numerator = dot(m_normals[i], sub(m_vertices[i], p1));
            float denominator = dot(m_normals[i], d);

            if (denominator == 0.0f) {
                if (numerator < 0.0f) {
                    return;
                }
            } else {
                // Note: we want this predicate without division:
                // lower < numerator / denominator, where denominator < 0
                // Since denominator < 0, we have to flip the inequality:
                // lower < numerator / denominator <==> denominator * lower > numerator.
                if (denominator < 0.0f & numerator < lower * denominator) {
                    // Increase lower.
                    // The segment enters this half-space.
                    lower = numerator / denominator;
                    index = i;
                } else if (denominator > 0.0f & numerator < upper * denominator) {
                    // Decrease upper.
                    // The segment exits this half-space.
                    upper = numerator / denominator;
                }
            }

            if (upper < lower) {
                return;
            }
        }

        assert (0.0f <= lower & lower <= input.maxFraction);

        if (index >= 0) {
            output.hit = true;
            output.fraction = lower;
            output.normal = mul(xf.R, m_normals[index]);
        }
    }

    /// @see BBShape.computeAABB
    public void computeAABB(BBCollision.BBAABB aabb, final BBTransform xf) {
        BBVec2 lower = mul(xf, m_vertices[0]);
        BBVec2 upper = lower;

        for (int i = 1; i < m_vertexCount; ++i) {
            BBVec2 v = mul(xf, m_vertices[i]);
            lower = min(lower, v);
            upper = max(upper, v);
        }

        BBVec2 r = new BBVec2(m_radius, m_radius);
        aabb.lowerBound = sub(lower, r);
        aabb.upperBound = add(upper, r);
    }

    /// @see BBShape.computeMass
    public void computeMass(BBMassData massData, float density) {
        // Polygon mass, centroid, and inertia.
        // Let rho be the polygon density in mass per unit area.
        // Then:
        // mass = rho * int(dA)
        // centroid.x = (1/mass) * rho * int(x * dA)
        // centroid.y = (1/mass) * rho * int(y * dA)
        // I = rho * int((x*x + y*y) * dA)
        //
        // We can compute these integrals by summing all the integrals
        // for each triangle of the polygon. To evaluate the integral
        // for a single triangle, we make a change of variables to
        // the (u,v) coordinates of the triangle:
        // x = x0 + e1x * u + e2x * v
        // y = y0 + e1y * u + e2y * v
        // where 0 <= u & 0 <= v & u + v <= 1.
        //
        // We integrate u from [0,1-v] and then v from [0,1].
        // We also need to use the Jacobian of the transformation:
        // D = cross(e1, e2)
        //
        // Simplification: triangle centroid = (1/3) * (p1 + p2 + p3)
        //
        // The rest of the derivation is handled by computer algebra.

        assert (m_vertexCount >= 2);

        // A line segment has zero mass.
        if (m_vertexCount == 2) {
            massData.center = mul(0.5f, add(m_vertices[0], m_vertices[1]));
            massData.mass = 0.0f;
            massData.I = 0.0f;
            return;
        }

        BBVec2 center = new BBVec2();
        center.set(0.0f, 0.0f);
        float area = 0.0f;
        float I = 0.0f;

        // pRef is the reference point for forming triangles.
        // It's location doesn't change the result (except for rounding error).
        BBVec2 pRef = new BBVec2(0.0f, 0.0f);

        final float k_inv3 = 1.0f / 3.0f;

        for (int i = 0; i < m_vertexCount; ++i) {
            // Triangle vertices.
            BBVec2 p1 = pRef;
            BBVec2 p2 = m_vertices[i];
            BBVec2 p3 = i + 1 < m_vertexCount ? m_vertices[i + 1] : m_vertices[0];

            BBVec2 e1 = sub(p2, p1);
            BBVec2 e2 = sub(p3, p1);

            float D = cross(e1, e2);

            float triangleArea = 0.5f * D;
            area += triangleArea;

            // Area weighted centroid
            center.add(mul(triangleArea, mul(k_inv3, add(p1, add(p2, p3)))));

            float px = p1.x, py = p1.y;
            float ex1 = e1.x, ey1 = e1.y;
            float ex2 = e2.x, ey2 = e2.y;

            float intx2 = k_inv3 * (0.25f * (ex1 * ex1 + ex2 * ex1 + ex2 * ex2) + (px * ex1 + px * ex2)) + 0.5f * px * px;
            float inty2 = k_inv3 * (0.25f * (ey1 * ey1 + ey2 * ey1 + ey2 * ey2) + (py * ey1 + py * ey2)) + 0.5f * py * py;

            I += D * (intx2 + inty2);
        }

        // Total mass
        massData.mass = density * area;

        // Center of mass
        assert (area > FLT_EPSILON);
        center.mul(1.0f / area);
        massData.center = center;

        // Inertia tensor relative to the local origin.
        massData.I = density * I;
    }

    /// Get the supporting vertex index in the given direction.
    public int getSupport(final BBVec2 d) {
        int bestIndex = 0;
        float bestValue = dot(m_vertices[0], d);
        for (int i = 1; i < m_vertexCount; ++i) {
            float value = dot(m_vertices[i], d);
            if (value > bestValue) {
                bestIndex = i;
                bestValue = value;
            }
        }

        return bestIndex;
    }

    /// Get the supporting vertex in the given direction.
    public BBVec2 getSupportVertex(final BBVec2 d) {
        int bestIndex = 0;
        float bestValue = dot(m_vertices[0], d);
        for (int i = 1; i < m_vertexCount; ++i) {
            float value = dot(m_vertices[i], d);
            if (value > bestValue) {
                bestIndex = i;
                bestValue = value;
            }
        }

        return m_vertices[bestIndex];
    }

    /// Get the vertex count.
    public int getVertexCount() {
        return m_vertexCount;
    }

    /// Get a vertex by index.
    public BBVec2 getVertex(int index) {
        assert (0 <= index & index < m_vertexCount);
        return m_vertices[index];
    }


    public BBVec2 m_centroid = new BBVec2();
    public BBVec2[] m_vertices = new BBVec2[maxPolygonVertices];
    public BBVec2[] m_normals = new BBVec2[maxPolygonVertices];
    public int m_vertexCount;


    public BBVec2 computeCentroid(final BBVec2[] vs, int count) {
        assert (count >= 2);

        BBVec2 c = new BBVec2();
        c.set(0.0f, 0.0f);
        float area = 0.0f;

        if (count == 2) {
            c = mul(0.5f, (add(vs[0], vs[1])));
            return c;
        }

        // pRef is the reference point for forming triangles.
        // It's location doesn't change the result (except for rounding error).
        BBVec2 pRef = new BBVec2(0.0f, 0.0f);

        final float inv3 = 1.0f / 3.0f;

        for (int i = 0; i < count; ++i) {
            // Triangle vertices.
            BBVec2 p1 = pRef;
            BBVec2 p2 = vs[i];
            BBVec2 p3 = i + 1 < count ? vs[i + 1] : vs[0];

            BBVec2 e1 = sub(p2, p1);
            BBVec2 e2 = sub(p3, p1);

            float D = cross(e1, e2);

            float triangleArea = 0.5f * D;
            area += triangleArea;

            // Area weighted centroid
            c.add(mul(triangleArea, mul(inv3, add(p1, add(p2, p3)))));
        }

        // Centroid
        assert (area > FLT_EPSILON);
        c.mul(1.0f / area);
        return c;
    }


}
