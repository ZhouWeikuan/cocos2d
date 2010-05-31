package org.box2d.collision.shapes;

import org.box2d.collision.BBCollision;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.FLT_EPSILON;
import static org.box2d.common.BBSettings.PI;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;


/// A circle shape.
public class BBCircleShape extends BBShape implements Cloneable {
    public BBCircleShape() {
        m_type = e_circle;
        m_radius = 0.0f;
        m_p.setZero();
    }

    /// Implement BBShape.
    @Override
    public BBShape clone() {
        return super.clone();
    }

    /// Implement BBShape.
    public boolean testPoint(final BBTransform transform, final BBVec2 p) {
        BBVec2 center = add(transform.position, mul(transform.R, m_p));
        BBVec2 d = sub(p, center);
        return dot(d, d) <= m_radius * m_radius;
    }

    /// Implement BBShape.
    public void rayCast(BBCollision.BBRayCastOutput output, final BBCollision.BBRayCastInput input, final BBTransform transform) {
        BBVec2 position = add(transform.position, mul(transform.R, m_p));
        BBVec2 s = sub(input.p1, position);
        float b = dot(s, s) - m_radius * m_radius;

        // solve quadratic equation.
        BBVec2 r = sub(input.p2, input.p1);
        float c = dot(s, r);
        float rr = dot(r, r);
        float sigma = c * c - rr * b;

        // Check for negative discriminant and short segment.
        if (sigma < 0.0f || rr < FLT_EPSILON) {
            output.hit = false;
            return;
        }

        // Find the point of intersection of the line with the circle.
        float a = -(c + sqrt(sigma));

        // Is the intersection point on the segment?
        if (0.0f <= a & a <= input.maxFraction * rr) {
            a /= rr;
            output.hit = true;
            output.fraction = a;
            output.normal = add(s, mul(a, r));
            output.normal.normalize();
            return;
        }

        output.hit = false;
    }

    /// @see BBShape.computeAABB
    public void computeAABB(BBCollision.BBAABB aabb, final BBTransform transform) {
        BBVec2 p = add(transform.position, mul(transform.R, m_p));
        aabb.lowerBound.set(p.x - m_radius, p.y - m_radius);
        aabb.upperBound.set(p.x + m_radius, p.y + m_radius);
    }

    /// @see BBShape.computeMass
    public void computeMass(BBMassData massData, float density) {
        massData.mass = density * PI * m_radius * m_radius;
        massData.center = m_p;

        // inertia about the local origin
        massData.I = massData.mass * (0.5f * m_radius * m_radius + dot(m_p, m_p));
    }

    /// Get the supporting vertex index in the given direction.
    public int getSupport(final BBVec2 d) {
        return 0;
    }

    /// Get the supporting vertex in the given direction.
    public BBVec2 getSupportVertex(final BBVec2 d) {
        return m_p;
    }

    /// Get the vertex count.
    public int getVertexCount() {
        return 1;
    }

    /// Get a vertex by index. Used by distance.
    public BBVec2 getVertex(int index) {
        assert (index == 0);
        return m_p;
    }

    /// Position
    public BBVec2 m_p = new BBVec2();
}
