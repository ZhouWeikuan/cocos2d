package org.box2d.dynamics.contacts;

import static org.box2d.collision.BBCollision.*;
import static org.box2d.collision.BBTimeOfImpact.BBTOIInput;
import static org.box2d.collision.BBTimeOfImpact.timeOfImpact;
import org.box2d.collision.shapes.BBShape;
import static org.box2d.common.BBSettings.linearSlop;
import org.box2d.common.BBSweep;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBFixture;
import static org.box2d.dynamics.BBWorldCallbacks.BBContactListener;

import java.lang.reflect.Method;

public abstract class BBContact {

    public static class BBContactRegister {
        Class<?> clazz;
        boolean primary;
    }

    /// A contact edge is used to connect bodies and contacts together
    /// in a contact graph where each body is a node and each contact
    /// is an edge. A contact edge belongs to a doubly linked list
    /// maintained in each attached body. Each contact has two contact
    /// nodes, one for each attached body.
    public static class BBContactEdge {
        public BBBody other;            ///< provides quick access to the other body attached.
        public BBContact contact;        ///< the contact
        public BBContactEdge prev;    ///< the previous contact edge in the body's contact list
        public BBContactEdge next;    ///< the next contact edge in the body's contact list
    }

    /// The class manages contact between two shapes. A contact exists for each overlapping
    /// AABB in the broad-phase (except if filtered). Therefore a contact object may exist
    /// that has no contact points.

    /// Get the contact manifold. Do not set the point count to zero. Instead
    /// call disable.

    public BBManifold getManifold() {
        return m_manifold;
    }

    /// Get the world manifold.
    public void getWorldManifold(BBWorldManifold worldManifold) {
        final BBBody bodyA = m_fixtureA.getBody();
        final BBBody bodyB = m_fixtureB.getBody();
        final BBShape shapeA = m_fixtureA.getShape();
        final BBShape shapeB = m_fixtureB.getShape();

        worldManifold.initialize(m_manifold, bodyA.getTransform(), shapeA.m_radius, bodyB.getTransform(), shapeB.m_radius);
    }

    /// Is this contact solid? Returns false if the shapes are separate,
    /// sensors, or the contact has been disabled.
    /// @return true if this contact should generate a response.
    public boolean isSolid() {
        int nonSolid = e_sensorFlag | e_disabledFlag;
        return (m_flags & nonSolid) == 0;
    }

    /// Is this contact touching.
    public boolean isTouching() {
        return (m_flags & e_touchingFlag) != 0;
    }

    /// Does this contact generate TOI events for continuous simulation?
    public boolean isContinuous() {
        return (m_flags & e_continuousFlag) != 0;
    }

    /// Change this to be a sensor or non-sensor contact.
    public void setAsSensor(boolean sensor) {
        if (sensor) {
            m_flags |= e_sensorFlag;
        } else {
            m_flags &= ~e_sensorFlag;
        }
    }

    /// disable this contact. This can be used inside the pre-solve
    /// contact listener. The contact is only disabled for the current
    /// time step (or sub-step in continuous collisions).
    public void disable() {
        m_flags |= e_disabledFlag;
    }

    /// Get the next contact in the world's contact list.
    public BBContact GetNext() {
        return m_next;
    }

    /// Get the first fixture in this contact.
    public BBFixture getFixtureA() {
        return m_fixtureA;
    }

    /// Get the second fixture in this contact.
    public BBFixture getFixtureB() {
        return m_fixtureB;
    }

    /// Flag this contact for filtering. Filtering will occur the next time step.
    public void FlagForFiltering() {
        m_flags |= e_filterFlag;
    }

    //--------------- Internals Below -------------------

    // m_flags

    // This contact should not participate in solve
    // The contact equivalent of sensors
    protected static final int e_sensorFlag = 0x0001;
    // Generate TOI events
    protected static final int e_continuousFlag = 0x0002;
    // Used when crawling contact graph when forming islands.
    public static final int e_islandFlag = 0x0004;
    // Used in solveTOI to indicate the cached toi value is still valid.
    public static final int e_toiFlag = 0x0008;
    // set when the shapes are touching.
    protected static final int e_touchingFlag = 0x0010;
    // Disabled (by user)
    protected static final int e_disabledFlag = 0x0020;
    // This contact needs filtering because a fixture filter was changed.
    public static final int e_filterFlag = 0x0040;

    protected static void AddType(Class<?> clazz, int type1, int type2) {
        assert (BBShape.e_unknown < type1 && type1 < BBShape.e_typeCount);
        assert (BBShape.e_unknown < type2 && type2 < BBShape.e_typeCount);

        s_registers[type1][type2] = new BBContactRegister();
        s_registers[type1][type2].clazz = clazz;
        s_registers[type1][type2].primary = true;

        if (type1 != type2) {
            s_registers[type2][type1] = new BBContactRegister();
            s_registers[type2][type1].clazz = clazz;
            s_registers[type2][type1].primary = false;
        }
    }


    protected static void InitializeRegisters() {
        AddType(BBCircleContact.class, BBShape.e_circle, BBShape.e_circle);
        AddType(BBPolygonAndCircleContact.class, BBShape.e_polygon, BBShape.e_circle);
        AddType(BBPolygonContact.class, BBShape.e_polygon, BBShape.e_polygon);
    }


    public static BBContact create(BBFixture fixtureA, BBFixture fixtureB) {
        if (!s_initialized) {
            InitializeRegisters();
            s_initialized = true;
        }

        int type1 = fixtureA.getType();
        int type2 = fixtureB.getType();

        assert (BBShape.e_unknown < type1 && type1 < BBShape.e_typeCount);
        assert (BBShape.e_unknown < type2 && type2 < BBShape.e_typeCount);

        Class<?> clazz = s_registers[type1][type2].clazz;
        if (clazz != null) {
            try {
                Method method = clazz.getMethod("create", BBFixture.class, BBFixture.class);
                if (s_registers[type1][type2].primary) {
                    return (BBContact) method.invoke(null, fixtureA, fixtureB);
                } else {
                    return (BBContact) method.invoke(null, fixtureB, fixtureA);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void destroy(BBContact contact) {
        assert s_initialized;

        if (contact.m_manifold.m_pointCount > 0) {
            contact.getFixtureA().getBody().wakeUp();
            contact.getFixtureB().getBody().wakeUp();
        }

        int typeA = contact.getFixtureA().getType();
        int typeB = contact.getFixtureB().getType();

        assert (BBShape.e_unknown < typeA && typeB < BBShape.e_typeCount);
        assert (BBShape.e_unknown < typeA && typeB < BBShape.e_typeCount);

        Class<?> clazz = s_registers[typeA][typeB].clazz;
        if (clazz != null) {
            try {
                Method method = clazz.getMethod("destroy", BBContact.class);
                method.invoke(null, contact);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected BBContact() {
        m_fixtureA = m_fixtureB = null;
    }

    protected BBContact(BBFixture fA, BBFixture fB) {
        m_flags = 0;

        if (fA.IsSensor() || fB.IsSensor()) {
            m_flags |= e_sensorFlag;
        }

        BBBody bodyA = fA.getBody();
        BBBody bodyB = fB.getBody();

        if (bodyA.isStatic() || bodyA.isBullet() || bodyB.isStatic() || bodyB.isBullet()) {
            m_flags |= e_continuousFlag;
        } else {
            m_flags &= ~e_continuousFlag;
        }

        m_fixtureA = fA;
        m_fixtureB = fB;

        m_manifold.m_pointCount = 0;

        m_prev = null;
        m_next = null;

        m_nodeA.contact = null;
        m_nodeA.prev = null;
        m_nodeA.next = null;
        m_nodeA.other = null;

        m_nodeB.contact = null;
        m_nodeB.prev = null;
        m_nodeB.next = null;
        m_nodeB.other = null;
    }


    public void Update(BBContactListener listener) {
        BBManifold oldManifold = m_manifold;

        // Re-enable this contact.
        m_flags &= ~e_disabledFlag;

        if (testOverlap(m_fixtureA.m_aabb, m_fixtureB.m_aabb)) {
            evaluate();
        } else {
            m_manifold.m_pointCount = 0;
        }

        BBBody bodyA = m_fixtureA.getBody();
        BBBody bodyB = m_fixtureB.getBody();

        int oldCount = oldManifold.m_pointCount;
        int newCount = m_manifold.m_pointCount;

        if (newCount == 0 && oldCount > 0) {
            bodyA.wakeUp();
            bodyB.wakeUp();
        }

        // Slow contacts don't generate TOI events.
        if (bodyA.isStatic() || bodyA.isBullet() || bodyB.isStatic() || bodyB.isBullet()) {
            m_flags |= e_continuousFlag;
        } else {
            m_flags &= ~e_continuousFlag;
        }

        // Match old contact ids to new contact ids and copy the
        // stored impulses to warm start the solver.
        for (int i = 0; i < m_manifold.m_pointCount; ++i) {
            BBManifoldPoint mp2 = m_manifold.m_points[i];
            mp2.m_normalImpulse = 0.0f;
            mp2.m_tangentImpulse = 0.0f;
            BBContactID id2 = mp2.m_id;

            for (int j = 0; j < oldManifold.m_pointCount; ++j) {
                BBManifoldPoint mp1 = oldManifold.m_points[j];

                if (mp1.m_id.key() == id2.key()) {
                    mp2.m_normalImpulse = mp1.m_normalImpulse;
                    mp2.m_tangentImpulse = mp1.m_tangentImpulse;
                    break;
                }
            }
        }

        if (newCount > 0) {
            m_flags |= e_touchingFlag;
        } else {
            m_flags &= ~e_touchingFlag;
        }

        if (oldCount == 0 && newCount > 0) {
            listener.beginContact(this);
        }

        if (oldCount > 0 && newCount == 0) {
            listener.endContact(this);
        }

        if ((m_flags & e_sensorFlag) == 0) {
            listener.PreSolve(this, oldManifold);
        }
    }

    protected abstract void evaluate();

    public float ComputeTOI(final BBSweep sweepA, final BBSweep sweepB) {
        BBTOIInput input = new BBTOIInput();
        input.proxyA.set(m_fixtureA.getShape());
        input.proxyB.set(m_fixtureB.getShape());
        input.sweepA = sweepA;
        input.sweepB = sweepB;
        input.tolerance = linearSlop;

        return timeOfImpact(input);
    }

    static BBContactRegister[][] s_registers = new BBContactRegister[BBShape.e_typeCount][BBShape.e_typeCount];
    static boolean s_initialized = false;

    public int m_flags;

    // World pool and list pointers.
    public BBContact m_prev;
    public BBContact m_next;

    // Nodes for connecting bodies.
    public BBContactEdge m_nodeA = new BBContactEdge();
    public BBContactEdge m_nodeB = new BBContactEdge();

    BBFixture m_fixtureA;
    BBFixture m_fixtureB;

    public BBManifold m_manifold = new BBManifold();

    public float m_toi;

}
