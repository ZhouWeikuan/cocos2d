package org.box2d.dynamics;

import org.box2d.collision.BBBroadPhase;
import static org.box2d.collision.BBCollision.*;
import org.box2d.collision.shapes.BBShape;
import static org.box2d.collision.shapes.BBShape.BBMassData;
import static org.box2d.common.BBMath.sub;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.contacts.BBContact;
import static org.box2d.dynamics.contacts.BBContact.BBContactEdge;

public class BBFixture {
    /// This holds contact filtering data.
    static class BBFilter {
        /// The collision category bits. Normally you would just set one bit.
        public int categoryBits;

        /// The collision mask bits. This states the categories that this
        /// shape would accept for collision.
        public int maskBits;

        /// Collision groups allow a certain group of objects to never collide (negative)
        /// or always collide (positive). Zero means no collision group. Non-zero group
        /// filtering always wins against the mask bits.
        public int groupIndex;
    }

    /// A fixture definition is used to create a fixture. This class defines an
    /// abstract fixture definition. You can reuse fixture definitions safely.
    public static class BBFixtureDef {
        /// The finalructor sets the default fixture definition values.
        public BBFixtureDef() {
            shape = null;
            userData = null;
            friction = 0.2f;
            restitution = 0.0f;
            density = 0.0f;
            filter.categoryBits = 0x0001;
            filter.maskBits = 0xFFFF;
            filter.groupIndex = 0;
            isSensor = false;
        }

        /// The shape, this must be set. The shape will be cloned, so you
        /// can create the shape on the stack.
        public BBShape shape;

        /// Use this to store application specific fixture data.
        public Object userData;

        /// The friction coefficient, usually in the range [0,1].
        public float friction;

        /// The restitution (elasticity) usually in the range [0,1].
        public float restitution;

        /// The density, usually in kg/m^2.
        public float density;

        /// A sensor shape collects contact information but never generates a collision
        /// response.
        public boolean isSensor;

        /// Contact filtering data.
        public BBFilter filter = new BBFilter();
    }


    /// A fixture is used to attach a shape to a body for collision detection. A fixture
    /// inherits its transform from its parent. Fixtures hold additional non-geometric data
    /// such as friction, collision filters, etc.
    /// Fixtures are created via BBBody.createFixture.
    /// @warning you cannot reuse fixtures.

    /// Get the type of the child shape. You can use this to down cast to the concrete shape.
    /// @return the shape type.

    public int getType() {
        return m_shape.GetType();
    }

    /// Get the child shape. You can modify the child shape, however you should not change the
    /// number of vertices because this will crash some collision caching mechanisms.
    public BBShape getShape() {
        return m_shape;
    }

    /// Is this fixture a sensor (non-solid)?
    /// @return the true if the shape is a sensor.
    public boolean IsSensor() {
        return m_isSensor;
    }


    /// set if this fixture is a sensor.
    void SetSensor(boolean sensor) {
        if (m_isSensor == sensor) {
            return;
        }

        m_isSensor = sensor;

        if (m_body == null) {
            return;
        }

        // Flag associated contacts for filtering.
        BBContactEdge edge = m_body.getContactList();
        while (edge != null) {
            BBContact contact = edge.contact;
            BBFixture fixtureA = contact.getFixtureA();
            BBFixture fixtureB = contact.getFixtureB();
            if (fixtureA == this || fixtureB == this) {
                contact.setAsSensor(m_isSensor);
            }
        }
    }

    /// set the contact filtering data. This is an expensive operation and should
    /// not be called frequently. This will not update contacts until the next time
    /// step when either parent body is awake.
    void setFilterData(final BBFilter filter) {
        m_filter = filter;

        if (m_body == null) {
            return;
        }

        // Flag associated contacts for filtering.
        BBContactEdge edge = m_body.getContactList();
        while (edge != null) {
            BBContact contact = edge.contact;
            BBFixture fixtureA = contact.getFixtureA();
            BBFixture fixtureB = contact.getFixtureB();
            if (fixtureA == this || fixtureB == this) {
                contact.FlagForFiltering();
            }
        }
    }

    /// Get the contact filtering data.
    final BBFilter getFilterData() {
        return m_filter;
    }

    /// Get the parent body of this fixture. This is null if the fixture is not attached.
    /// @return the parent body.
    public BBBody getBody() {
        return m_body;
    }

    /// Get the next fixture in the parent body's fixture list.
    /// @return the next shape.
    BBFixture getNext() {
        return m_next;
    }

    /// Get the user data that was assigned in the fixture definition. Use this to
    /// store your application specific data.
    Object getUserData() {
        return m_userData;
    }

    /// set the user data. Use this to store your application specific data.
    void SetUserData(Object data) {
        m_userData = data;
    }

    /// Test a point for containment in this fixture. This only works for convex shapes.
    /// @param xf the shape world transform.
    /// @param p a point in world coordinates.
    boolean testPoint(final BBVec2 p) {
        return m_shape.testPoint(m_body.getTransform(), p);
    }

    /// Cast a ray against this shape.
    /// @param output the ray-cast results.
    /// @param input the ray-cast input parameters.
    void rayCast(BBRayCastOutput output, final BBRayCastInput input) {
        m_shape.rayCast(output, input, m_body.getTransform());
    }

    /// Get the mass data for this fixture. The mass data is based on the density and
    /// the shape. The rotational inertia is about the shape's origin.
    final BBMassData getMassData() {
        return m_massData;
    }

    /// Get the coefficient of friction.
    public float getFriction() {
        return m_friction;
    }

    /// set the coefficient of friction.
    void setFriction(float friction) {
        m_friction = friction;
    }

    /// Get the coefficient of restitution.
    public float getRestitution() {
        return m_restitution;
    }

    /// set the coefficient of restitution.
    void setRestitution(float restitution) {
        m_restitution = restitution;
    }

    protected BBFixture() {
        m_userData = null;
        m_body = null;
        m_next = null;
        m_proxyId = BBBroadPhase.e_nullProxy;
        m_shape = null;
    }

    // We need separation create/destroy functions from the finalructor/destructor because
    // the destructor cannot access the allocator or broad-phase (no destructor arguments allowed by C++).
    protected void create(BBBroadPhase broadPhase, BBBody body, final BBTransform xf, final BBFixtureDef def) {
        m_userData = def.userData;
        m_friction = def.friction;
        m_restitution = def.restitution;

        m_body = body;
        m_next = null;

        m_filter = def.filter;

        m_isSensor = def.isSensor;

        m_shape = def.shape.clone();

        m_shape.computeMass(m_massData, def.density);

        // create proxy in the broad-phase.
        m_shape.computeAABB(m_aabb, xf);

        m_proxyId = broadPhase.createProxy(m_aabb, this);
    }

    protected void destroy(BBBroadPhase broadPhase) {
        // Remove proxy from the broad-phase.
        if (m_proxyId != BBBroadPhase.e_nullProxy) {
            broadPhase.destroyProxy(m_proxyId);
            m_proxyId = BBBroadPhase.e_nullProxy;
        }

        // Free the child shape.
        switch (m_shape.m_type) {
            case BBShape.e_circle:
                break;

            case BBShape.e_polygon:
                break;

            default:
                assert false;
                break;
        }

        m_shape = null;
    }

    protected void synchronize(BBBroadPhase broadPhase, final BBTransform transform1, final BBTransform transform2) {
        if (m_proxyId == BBBroadPhase.e_nullProxy) {
            return;
        }

        // Compute an AABB that covers the swept shape (may miss some rotation effect).
        BBAABB aabb1 = new BBAABB(), aabb2 = new BBAABB();
        m_shape.computeAABB(aabb1, transform1);
        m_shape.computeAABB(aabb2, transform2);

        m_aabb.combine(aabb1, aabb2);

        BBVec2 displacement = sub(transform2.position, transform1.position);

        broadPhase.moveProxy(m_proxyId, m_aabb, displacement);
    }

    public BBAABB m_aabb = new BBAABB();

    protected BBMassData m_massData = new BBMassData();

    protected BBFixture m_next;
    protected BBBody m_body;

    protected BBShape m_shape;

    protected float m_friction;
    protected float m_restitution;

    protected int m_proxyId;
    protected BBFilter m_filter = new BBFilter();

    protected boolean m_isSensor;

    protected Object m_userData;

}
