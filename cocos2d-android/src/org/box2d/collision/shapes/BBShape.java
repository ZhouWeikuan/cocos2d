package org.box2d.collision.shapes;

import org.box2d.collision.BBCollision;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

/// A shape is used for collision detection. You can create a shape however you like.
/// Shapes used for simulation in BBWorld are created automatically when a BBFixture

/// is created.
public abstract class BBShape implements Cloneable {

    /// This holds the mass data computed for a shape.
    public static class BBMassData {
        /// The mass of the shape, usually in kilograms.
        public float mass;

        /// The position of the shape's centroid relative to the shape's origin.
        public BBVec2 center = new BBVec2();

        /// The rotational inertia of the shape. This may be about the center or local
        /// origin, depending on usage.
        public float I;
    }


    public static final int e_unknown = -1;
    public static final int e_circle = 0;
    public static final int e_polygon = 1;
    public static final int e_typeCount = 2;

    public BBShape() {
        m_type = e_unknown;
    }

    /// clone the concrete shape.
    /// Implement Object.
    @Override
    public BBShape clone() {
        try {
            return (BBShape) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /// Get the type of this shape. You can use this to down cast to the concrete shape.
    /// @return the shape type.
    public int GetType() {
        return m_type;
    }


    /// Test a point for containment in this shape. This only works for convex shapes.
    /// @param xf the shape world transform.
    /// @param p a point in world coordinates.
    public abstract boolean testPoint(final BBTransform xf, final BBVec2 p);

    /// Cast a ray against this shape.
    /// @param output the ray-cast results.
    /// @param input the ray-cast input parameters.
    /// @param transform the transform to be applied to the shape.
    public abstract void rayCast(BBCollision.BBRayCastOutput output, final BBCollision.BBRayCastInput input, final BBTransform transform);

    /// Given a transform, compute the associated axis aligned bounding box for this shape.
    /// @param aabb returns the axis aligned box.
    /// @param xf the world transform of the shape.
    public abstract void computeAABB(BBCollision.BBAABB aabb, final BBTransform xf);

    /// Compute the mass properties of this shape using its dimensions and density.
    /// The inertia tensor is computed about the local origin, not the centroid.
    /// @param massData returns the mass data for this shape.
    /// @param density the density in kilograms per meter squared.
    public abstract void computeMass(BBMassData massData, float density);

    public int m_type;
    public float m_radius;
}
