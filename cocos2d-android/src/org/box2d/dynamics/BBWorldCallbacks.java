package org.box2d.dynamics;

import static org.box2d.collision.BBCollision.*;
import static org.box2d.common.BBSettings.maxManifoldPoints;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.contacts.BBContact;
import org.box2d.dynamics.joints.BBJoint;

import javax.microedition.khronos.opengles.GL10;

public class BBWorldCallbacks {

    /// Joints and fixtures are destroyed when their associated
    /// body is destroyed. Implement this listener so that you
    /// may nullify references to these joints and shapes.
    public interface BBDestructionListener {
        /// Called when any joint is about to be destroyed due
        /// to the destruction of one of its attached bodies.
        public void SayGoodbye(BBJoint joint);

        /// Called when any fixture is about to be destroyed due
        /// to the destruction of its parent body.
        public void SayGoodbye(BBFixture fixture);
    }

    /// Implement this class to provide collision filtering. In other words, you can implement
    /// this class if you want finer control over contact creation.
    public static class BBContactFilter {
        /// Return true if contact calculations should be performed between these two shapes.
        /// @warning for performance reasons this is only called when the AABBs begin to overlap.
        public boolean shouldCollide(BBFixture fixtureA, BBFixture fixtureB) {
            final BBFixture.BBFilter filterA = fixtureA.getFilterData();
            final BBFixture.BBFilter filterB = fixtureB.getFilterData();

            if (filterA.groupIndex == filterB.groupIndex & filterA.groupIndex != 0) {
                return filterA.groupIndex > 0;
            }

            return (filterA.maskBits & filterB.categoryBits) != 0 & (filterA.categoryBits & filterB.maskBits) != 0;
        }

        /// Return true if the given shape should be considered for ray intersection
        public boolean RayCollide(Object userData, BBFixture fixture) {
            // By default, cast userData as a fixture, and then collide if the shapes would collide
            return userData == null || shouldCollide((BBFixture) userData, fixture);

        }

    }

    /// Contact impulses for reporting. Impulses are used instead of forces because
    /// sub-step forces may approach infinity for rigid body collisions. These
    /// match up one-to-one with the contact points in BBManifold.
    public static class BBContactImpulse {
        float[] normalImpulses = new float[maxManifoldPoints];
        float[] tangentImpulses = new float[maxManifoldPoints];
    }

    /// Implement this class to get contact information. You can use these results for
    /// things like sounds and game logic. You can also get contact results by
    /// traversing the contact lists after the time step. However, you might miss
    /// some contacts because continuous physics leads to sub-stepping.
    /// Additionally you may receive multiple callbacks for the same contact in a
    /// single time step.
    /// You should strive to make your callbacks efficient because there may be
    /// many callbacks per time step.
    /// @warning You cannot create/destroy Box2D entities inside these callbacks.
    public static class BBContactListener {

        /// Called when two fixtures begin to touch.
        public void beginContact(BBContact contact) {}

        /// Called when two fixtures cease to touch.
        public void endContact(BBContact contact) {}

        /// This is called after a contact is updated. This allows you to inspect a
        /// contact before it goes to the solver. If you are careful, you can modify the
        /// contact manifold (e.g. disable contact).
        /// A copy of the old manifold is provided so that you can detect changes.
        /// Note: this is called only for awake bodies.
        /// Note: this is called even when the number of contact points is zero.
        /// Note: this is not called for sensors.
        /// Note: if you set the number of contact points to zero, you will not
        /// get an endContact callback. However, you may get a beginContact callback
        /// the next step.
        public void PreSolve(BBContact contact, final BBManifold oldManifold) {}

        /// This lets you inspect a contact after the solver is finished. This is useful
        /// for inspecting impulses.
        /// Note: the contact manifold does not include time of impact impulses, which can be
        /// arbitrarily large if the sub-step is small. Hence the impulse is provided explicitly
        /// in a separate data structure.
        /// Note: this is only called for contacts that are touching, solid, and awake.
        public void PostSolve(BBContact contact, final BBContactImpulse impulse) {}
    }

    /// Callback class for AABB queries.
    /// See BBWorld.query
    public interface BBQueryCallback {
        /// Called for each fixture found in the query AABB.
        /// @return false to terminate the query.
        public boolean reportFixture(BBFixture fixture);
    }

    /// Callback class for ray casts.
    /// See BBWorld.rayCast
    public interface BBRayCastCallback {

        /// Called for each fixture found in the query. You control how the ray proceeds
        /// by returning a float that indicates the fractional length of the ray. By returning
        /// 0, you set the ray length to zero. By returning the current fraction, you proceed
        /// to find the closest point. By returning 1, you continue with the original ray
        /// clipping.
        /// @param fixture the fixture hit by the ray
        /// @param point the point of initial intersection
        /// @param normal the normal vector at the point of intersection
        /// @return 0 to terminate, fraction to clip the ray for
        /// closest hit, 1 to continue
        public float reportFixture(BBFixture fixture, final BBVec2 point,
                                   final BBVec2 normal, float fraction);
    }

    /// Color for debug drawing. Each value has the range [0,1].
    public static class BBColor {
        public BBColor() {
        }

        public BBColor(float ri, float gi, float bi) {
            r = ri;
            g = gi;
            b = bi;
        }

        public void set(float ri, float gi, float bi) {
            r = ri;
            g = gi;
            b = bi;
        }

        public float r, g, b;
    }

    /// Implement and register this class with a BBWorld to provide debug drawing of physics
    /// entities in your game.
    public static abstract class BBDebugDraw {
        public static final int e_shapeBit = 0x0001; ///< draw shapes
        public static final int e_jointBit = 0x0002; ///< draw joint connections
        public static final int e_aabbBit = 0x0004; ///< draw axis aligned bounding boxes
        public static final int e_pairBit = 0x0008; ///< draw broad-phase pairs
        public static final int e_centerOfMassBit = 0x0010; ///< draw center of mass frame

        public BBDebugDraw() {
            m_drawFlags = 0;
        }

        /// set the drawing flags.
        public void setFlags(int flags) {
            m_drawFlags = flags;
        }

        /// Get the drawing flags.
        public int getFlags() {
            return m_drawFlags;
        }

        /// Append flags to the current flags.
        public void appendFlags(int flags) {
            m_drawFlags |= flags;
        }

        /// Clear flags from the current flags.
        public void clearFlags(int flags) {
            m_drawFlags &= ~flags;
        }

        /// Draw a closed polygon provided in CCW order.
        public abstract void drawPolygon(GL10 gl, final BBVec2[] vertices, int vertexCount, final BBColor color);

        /// Draw a solid closed polygon provided in CCW order.
        public abstract void drawSolidPolygon(GL10 gl, final BBVec2[] vertices, int vertexCount, final BBColor color);

        /// Draw a circle.
        public abstract void drawCircle(GL10 gl, final BBVec2 center, float radius, final BBColor color);

        /// Draw a solid circle.
        public abstract void drawSolidCircle(GL10 gl, final BBVec2 center, float radius, final BBVec2 axis, final BBColor color);

        /// Draw a line segment.
        public abstract void drawSegment(GL10 gl, final BBVec2 p1, final BBVec2 p2, final BBColor color);

        /// Draw a transform. Choose your own length scale.
        /// @param xf a transform.
        public abstract void drawXForm(GL10 gl, final BBTransform xf);

        protected int m_drawFlags;
    }


}
