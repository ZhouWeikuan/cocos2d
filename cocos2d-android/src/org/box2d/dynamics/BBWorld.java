package org.box2d.dynamics;

import org.box2d.collision.BBBroadPhase;
import static org.box2d.collision.BBCollision.*;
import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBPolygonShape;
import org.box2d.collision.shapes.BBShape;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBSweep;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBBody.BBBodyDef;
import static org.box2d.dynamics.BBWorldCallbacks.*;
import org.box2d.dynamics.contacts.BBContact;
import static org.box2d.dynamics.contacts.BBContact.BBContactEdge;
import org.box2d.dynamics.joints.BBJoint;
import org.box2d.dynamics.joints.BBJoint.*;
import org.box2d.dynamics.joints.BBPulleyJoint;

import javax.microedition.khronos.opengles.GL10;


public class BBWorld {
    /// Conclass a world object.
    /// @param gravity the world gravity vector.
    /// @param doSleep improve performance by not simulating inactive bodies.
    public BBWorld(final BBVec2 gravity, boolean doSleep) {
        m_destructionListener = null;
        m_debugDraw = null;

        m_bodyList = null;
        m_jointList = null;

        m_bodyCount = 0;
        m_jointCount = 0;

        m_warmStarting = true;
        m_continuousPhysics = true;

        m_allowSleep = doSleep;
        m_gravity = gravity;

        m_flags = 0;

        m_inv_dt0 = 0.0f;

    }

    /// Register a destruction listener.
    public void SetDestructionListener(BBDestructionListener listener) {
        m_destructionListener = listener;
    }

    /// Register a contact filter to provide specific control over collision.
    /// Otherwise the default filter is used (defaultFilter).
    public void SetContactFilter(BBContactFilter filter) {
        m_contactManager.m_contactFilter = filter;
    }

    /// Register a contact event listener
    public void SetContactListener(BBContactListener listener) {
        m_contactManager.m_contactListener = listener;
    }

    /// Register a routine for debug drawing. The debug draw functions are called
    /// inside the BBWorld.Step method, so make sure your renderer is ready to
    /// consume draw commands when you call Step().
    public void SetDebugDraw(BBDebugDraw debugDraw) {
        m_debugDraw = debugDraw;
    }

    /// create a rigid body given a definition. No reference to the definition
    /// is retained.
    /// @warning This function is locked during callbacks.
    public synchronized BBBody createBody(final BBBodyDef def) {
        assert (!IsLocked());

        if (IsLocked()) {
            return null;
        }

        BBBody b = new BBBody(def, this);

        // add to world doubly linked list.
        b.m_prev = null;
        b.m_next = m_bodyList;
        if (m_bodyList != null) {
            m_bodyList.m_prev = b;
        }
        m_bodyList = b;
        ++m_bodyCount;

        return b;
    }


    /// destroy a rigid body given a definition. No reference to the definition
    /// is retained. This function is locked during callbacks.
    /// @warning This automatically deletes all associated shapes and joints.
    /// @warning This function is locked during callbacks.
    public synchronized void destroyBody(BBBody b) {
        assert (m_bodyCount > 0);
        assert (!IsLocked());

        if (IsLocked()) {
            return;
        }

        // Delete the attached joints.
        BBJointEdge je = b.m_jointList;
        while (je != null) {
            BBJointEdge je0 = je;
            je = je.next;

            if (m_destructionListener != null) {
                m_destructionListener.SayGoodbye(je0.joint);
            }

            destroyJoint(je0.joint);
        }
        b.m_jointList = null;

        // Delete the attached contacts.
        BBContactEdge ce = b.m_contactList;
        while (ce != null) {
            BBContactEdge ce0 = ce;
            ce = ce.next;
            m_contactManager.destroy(ce0.contact);
        }
        b.m_contactList = null;

        // Delete the attached fixtures. This destroys broad-phase proxies.
        BBFixture f = b.m_fixtureList;
        while (f != null) {
            BBFixture f0 = f;
            f = f.m_next;

            if (m_destructionListener != null) {
                m_destructionListener.SayGoodbye(f0);
            }

            f0.destroy(m_contactManager.m_broadPhase);
        }
        b.m_fixtureList = null;
        b.m_fixtureCount = 0;

        // Remove world body list.
        if (b.m_prev != null) {
            b.m_prev.m_next = b.m_next;
        }

        if (b.m_next != null) {
            b.m_next.m_prev = b.m_prev;
        }

        if (b == m_bodyList) {
            m_bodyList = b.m_next;
        }

        --m_bodyCount;
    }


    /// create a joint to finalrain bodies together. No reference to the definition
    /// is retained. This may cause the connected bodies to cease colliding.
    /// @warning This function is locked during callbacks.
    public synchronized BBJoint createJoint(final BBJointDef def) {
        assert (!IsLocked());

        if (IsLocked()) {
            return null;
        }

        BBJoint j = BBJoint.create(def);

        // Connect to the world list.
        j.m_prev = null;
        j.m_next = m_jointList;
        if (m_jointList != null) {
            m_jointList.m_prev = j;
        }
        m_jointList = j;
        ++m_jointCount;

        // Connect to the bodies' doubly linked lists.
        j.m_edgeA.joint = j;
        j.m_edgeA.other = j.m_bodyB;
        j.m_edgeA.prev = null;
        j.m_edgeA.next = j.m_bodyA.m_jointList;
        if (j.m_bodyA.m_jointList != null) j.m_bodyA.m_jointList.prev = j.m_edgeA;
        j.m_bodyA.m_jointList = j.m_edgeA;

        j.m_edgeB.joint = j;
        j.m_edgeB.other = j.m_bodyA;
        j.m_edgeB.prev = null;
        j.m_edgeB.next = j.m_bodyB.m_jointList;
        if (j.m_bodyB.m_jointList != null) j.m_bodyB.m_jointList.prev = j.m_edgeB;
        j.m_bodyB.m_jointList = j.m_edgeB;

        BBBody bodyA = def.body1;
        BBBody bodyB = def.body2;

        boolean staticA = bodyA.isStatic();
        boolean staticB = bodyB.isStatic();

        // If the joint prevents collisions, then flag any contacts for filtering.
        if (!def.collideConnected & (!staticA || !staticB)) {
            // Ensure we iterate over contacts on a dynamic body (usually have less contacts
            // than a static body). Ideally we will have a contact count on both bodies.
            if (staticB) {
                swap(bodyA, bodyB);
            }

            BBContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    // Flag the contact for filtering at the next time step (where either
                    // body is awake).
                    edge.contact.FlagForFiltering();
                }

                edge = edge.next;
            }
        }

        // Note: creating a joint doesn't wake the bodies.

        return j;
    }


    /// destroy a joint. This may cause the connected bodies to begin colliding.
    /// @warning This function is locked during callbacks.
    public synchronized void destroyJoint(BBJoint j) {
        assert !IsLocked();

        if (IsLocked()) {
            return;
        }

        boolean collideConnected = j.m_collideConnected;

        // Remove from the doubly linked list.
        if (j.m_prev != null) {
            j.m_prev.m_next = j.m_next;
        }

        if (j.m_next != null) {
            j.m_next.m_prev = j.m_prev;
        }

        if (j == m_jointList) {
            m_jointList = j.m_next;
        }

        // Disconnect from island graph.
        BBBody bodyA = j.m_bodyA;
        BBBody bodyB = j.m_bodyB;

        // Wake up connected bodies.
        bodyA.wakeUp();
        bodyB.wakeUp();

        // Remove from body 1.
        if (j.m_edgeA.prev != null) {
            j.m_edgeA.prev.next = j.m_edgeA.next;
        }

        if (j.m_edgeA.next != null) {
            j.m_edgeA.next.prev = j.m_edgeA.prev;
        }

        if (j.m_edgeA == bodyA.m_jointList) {
            bodyA.m_jointList = j.m_edgeA.next;
        }

        j.m_edgeA.prev = null;
        j.m_edgeA.next = null;

        // Remove from body 2
        if (j.m_edgeB.prev != null) {
            j.m_edgeB.prev.next = j.m_edgeB.next;
        }

        if (j.m_edgeB.next != null) {
            j.m_edgeB.next.prev = j.m_edgeB.prev;
        }

        if (j.m_edgeB == bodyB.m_jointList) {
            bodyB.m_jointList = j.m_edgeB.next;
        }

        j.m_edgeB.prev = null;
        j.m_edgeB.next = null;

        BBJoint.destroy(j);

        assert (m_jointCount > 0);
        --m_jointCount;

        // If the joint prevents collisions, then flag any contacts for filtering.
        if (!collideConnected) {
            BBContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    // Flag the contact for filtering at the next time step (where either
                    // body is awake).
                    edge.contact.FlagForFiltering();
                }

                edge = edge.next;
            }
        }
    }


    /// Take a time step. This performs collision detection, integration,
    /// and constraint solution.
    /// @param timeStep the amount of time to simulate, this should not vary.
    /// @param velocityIterations for the velocity constraint solver.
    /// @param positionIterations for the position constraint solver.
    public void Step(float dt, int velocityIterations, int positionIterations) {
        int height = m_contactManager.m_broadPhase.computeHeight();

        // If new fixtures were added, we need to find the new contacts.
        if ((m_flags & e_newFixture) != 0) {
            m_contactManager.findNewContacts();
            m_flags &= ~e_newFixture;
        }
        synchronized (this) {
            m_flags |= e_locked;

            BBTimeStep step = new BBTimeStep();
            step.dt = dt;
            step.velocityIterations = velocityIterations;
            step.positionIterations = positionIterations;

            if (dt > 0.0f) {
                step.inv_dt = 1.0f / dt;
            } else {
                step.inv_dt = 0.0f;
            }

            step.dtRatio = m_inv_dt0 * dt;

            step.warmStarting = m_warmStarting;

            // Update contacts. This is where some contacts are destroyed.
            m_contactManager.collide();

            // Integrate velocities, solve velocity constraints, and integrate positions.
            if (step.dt > 0.0f) {
                Solve(step);
            }

            // Handle TOI events.
            if (m_continuousPhysics & step.dt > 0.0f) {
                solveTOI(step);
            }

            if (step.dt > 0.0f) {
                m_inv_dt0 = step.inv_dt;
            }

            m_flags &= ~e_locked;
        }
    }


    /// Call this to draw shapes and other debug draw data.
    public void DrawDebugData(GL10 gl) {
        if (m_debugDraw == null) {
            return;
        }

        int flags = m_debugDraw.getFlags();

        if ((flags & BBDebugDraw.e_shapeBit) != 0) {
            for (BBBody b = m_bodyList; b != null; b = b.getNext()) {
                final BBTransform xf = b.getTransform();
                for (BBFixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                    if (b.isStatic()) {
                        drawShape(gl, f, xf, new BBColor(0.5f, 0.9f, 0.5f));
                    } else if (b.isSleeping()) {
                        drawShape(gl, f, xf, new BBColor(0.5f, 0.5f, 0.9f));
                    } else {
                        drawShape(gl, f, xf, new BBColor(0.9f, 0.9f, 0.9f));
                    }
                }
            }
        }

        if ((flags & BBDebugDraw.e_jointBit) != 0) {
            for (BBJoint j = m_jointList; j != null; j = j.getNext()) {
                if (j.getType() != BBJoint.e_mouseJoint) {
                    drawJoint(gl, j);
                }
            }
        }

        if ((flags & BBDebugDraw.e_pairBit) != 0) {
            // TODO_ERIN
        }

        if ((flags & BBDebugDraw.e_aabbBit) != 0) {
            BBColor color = new BBColor(0.9f, 0.3f, 0.9f);
            BBBroadPhase bp = m_contactManager.m_broadPhase;

            for (BBBody b = m_bodyList; b != null; b = b.getNext()) {
                for (BBFixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                    BBAABB aabb = bp.getFatAABB(f.m_proxyId);
                    BBVec2[] vs = new BBVec2[4];
                    vs[0] = new BBVec2();
                    vs[0].set(aabb.lowerBound.x, aabb.lowerBound.y);
                    vs[1] = new BBVec2();
                    vs[1].set(aabb.upperBound.x, aabb.lowerBound.y);
                    vs[2] = new BBVec2();
                    vs[2].set(aabb.upperBound.x, aabb.upperBound.y);
                    vs[3] = new BBVec2();
                    vs[3].set(aabb.lowerBound.x, aabb.upperBound.y);

                    m_debugDraw.drawPolygon(gl, vs, 4, color);
                }
            }
        }

        if ((flags & BBDebugDraw.e_centerOfMassBit) != 0) {
            for (BBBody b = m_bodyList; b != null; b = b.getNext()) {
                BBTransform xf = b.getTransform();
                xf.position = b.getWorldCenter();
                m_debugDraw.drawXForm(gl, xf);
            }
        }
    }


    /// query the world for all fixtures that potentially overlap the
    /// provided AABB.
    /// @param callback a user implemented callback class.
    /// @param aabb the query box.
    public void QueryAABB(BBQueryCallback callback, final BBAABB aabb) {
        BBWorldQueryWrapper wrapper = new BBWorldQueryWrapper();
        wrapper.broadPhase = m_contactManager.m_broadPhase;
        wrapper.callback = callback;
        m_contactManager.m_broadPhase.query(wrapper, aabb);
    }


    /// Ray-cast the world for all fixtures in the path of the ray. Your callback
    /// controls whether you get the closest point, any point, or n-points.
    /// The ray-cast ignores shapes that contain the starting point.
    /// @param callback a user implemented callback class.
    /// @param point1 the ray starting point
    /// @param point2 the ray ending point
    public void rayCast(BBRayCastCallback callback, final BBVec2 point1, final BBVec2 point2) {
        BBWorldRayCastWrapper wrapper = new BBWorldRayCastWrapper();
        wrapper.broadPhase = m_contactManager.m_broadPhase;
        wrapper.callback = callback;
        BBRayCastInput input = new BBRayCastInput();
        input.maxFraction = 1.0f;
        input.p1 = point1;
        input.p2 = point2;
        m_contactManager.m_broadPhase.rayCast(wrapper, input);
    }


    /// Get the world body list. With the returned body, use BBBody.getNext to get
    /// the next body in the world list. A null body indicates the end of the list.
    /// @return the head of the world body list.
    public BBBody GetBodyList() {
        return m_bodyList;
    }

    /// Get the world joint list. With the returned joint, use BBJoint.getNext to get
    /// the next joint in the world list. A null joint indicates the end of the list.
    /// @return the head of the world joint list.
    BBJoint GetJointList() {
        return m_jointList;
    }


    /// Get the world contact list. With the returned contact, use BBContact.getNext to get
    /// the next contact in the world list. A null contact indicates the end of the list.
    /// @return the head of the world contact list.
    /// @warning contacts are
    public BBContact GetContactList() {
        return m_contactManager.m_contactList;
    }

    /// Enable/disable warm starting. For testing.
    public void SetWarmStarting(boolean flag) {
        m_warmStarting = flag;
    }

    /// Enable/disable continuous physics. For testing.
    public void SetContinuousPhysics(boolean flag) {
        m_continuousPhysics = flag;
    }

    /// Get the number of broad-phase proxies.
    public int GetProxyCount() {
        return m_contactManager.m_broadPhase.GetProxyCount();
    }

    /// Get the number of bodies.
    public int GetBodyCount() {
        return m_bodyCount;
    }

    /// Get the number of joints.
    public int GetJointCount() {
        return m_jointCount;
    }

    /// Get the number of contacts (each may have 0 or more contact points).
    public int GetContactCount() {
        return m_contactManager.m_contactCount;
    }

    /// Change the global gravity vector.
    public void SetGravity(final BBVec2 gravity) {
        m_gravity = gravity;
    }

    /// Get the global gravity vector.
    public BBVec2 GetGravity() {
        return m_gravity;
    }

    /// Is the world locked (in the middle of a time step).
    public boolean IsLocked() {
        return (m_flags & e_locked) == e_locked;
    }

    // m_flags
    public static final int e_newFixture = 0x0001;
    public static final int e_locked = 0x0002;

    private void Solve(final BBTimeStep step) {
        // Size the island for the worst case.
        BBIsland island = new BBIsland(m_bodyCount,
                m_contactManager.m_contactCount,
                m_jointCount,
                m_contactManager.m_contactListener);

        // Clear all the island flags.
        for (BBBody b = m_bodyList; b != null; b = b.m_next) {
            b.m_flags &= ~BBBody.e_islandFlag;
        }
        for (BBContact c = m_contactManager.m_contactList; c != null; c = c.m_next) {
            c.m_flags &= ~BBContact.e_islandFlag;
        }
        for (BBJoint j = m_jointList; j != null; j = j.m_next) {
            j.m_islandFlag = false;
        }

        // Build and simulate all awake islands.
        int stackSize = m_bodyCount;
        BBBody[] stack = new BBBody[stackSize];
        for (BBBody seed = m_bodyList; seed != null; seed = seed.m_next) {
            if ((seed.m_flags & (BBBody.e_islandFlag | BBBody.e_sleepFlag)) != 0) {
                continue;
            }

            if (seed.isStatic()) {
                continue;
            }

            // Reset island and stack.
            island.Clear();
            int stackCount = 0;
            stack[stackCount++] = seed;
            seed.m_flags |= BBBody.e_islandFlag;

            // Perform a depth first search (DFS) on the constraint graph.
            while (stackCount > 0) {
                // Grab the next body off the stack and add it to the island.
                BBBody b = stack[--stackCount];
                island.add(b);

                // Make sure the body is awake.
                b.m_flags &= ~BBBody.e_sleepFlag;

                // To keep islands as small as possible, we don't
                // propagate islands across static bodies.
                if (b.isStatic()) {
                    continue;
                }

                // Search all contacts connected to this body.
                for (BBContactEdge ce = b.m_contactList; ce != null; ce = ce.next) {
                    // Has this contact already been added to an island?
                    if ((ce.contact.m_flags & BBContact.e_islandFlag) != 0) {
                        continue;
                    }

                    // Is this contact solid and touching?
                    if (!ce.contact.isSolid() || !ce.contact.isTouching()) {
                        continue;
                    }

                    island.add(ce.contact);
                    ce.contact.m_flags |= BBContact.e_islandFlag;

                    BBBody other = ce.other;

                    // Was the other body already added to this island?
                    if ((other.m_flags & BBBody.e_islandFlag) != 0) {
                        continue;
                    }

                    assert (stackCount < stackSize);
                    stack[stackCount++] = other;
                    other.m_flags |= BBBody.e_islandFlag;
                }

                // Search all joints connect to this body.
                for (BBJointEdge je = b.m_jointList; je != null; je = je.next) {
                    if (je.joint.m_islandFlag) {
                        continue;
                    }

                    island.add(je.joint);
                    je.joint.m_islandFlag = true;

                    BBBody other = je.other;
                    if ((other.m_flags & BBBody.e_islandFlag) != 0) {
                        continue;
                    }

                    assert (stackCount < stackSize);
                    stack[stackCount++] = other;
                    other.m_flags |= BBBody.e_islandFlag;
                }
            }

            island.solve(step, m_gravity, m_allowSleep);

            // Post solve cleanup.
            for (int i = 0; i < island.m_bodyCount; ++i) {
                // Allow static bodies to participate in other islands.
                BBBody b = island.m_bodies[i];
                if (b.isStatic()) {
                    b.m_flags &= ~BBBody.e_islandFlag;
                }
            }
        }

        // synchronize fixtures, check for out of range bodies.
        for (BBBody b = m_bodyList; b != null; b = b.getNext()) {
            if ((b.m_flags & BBBody.e_sleepFlag) != 0) {
                continue;
            }

            if (b.isStatic()) {
                continue;
            }

            // Update fixtures (for broad-phase).
            b.synchronizeFixtures();
        }

        // Look for new contacts.
        m_contactManager.findNewContacts();
    }

    public void solveTOI(final BBTimeStep step) {
        // Reserve an island and a queue for TOI island solution.
        BBIsland island = new BBIsland(m_bodyCount,
                maxTOIContactsPerIsland,
                maxTOIJointsPerIsland,
                m_contactManager.m_contactListener);

        //Simple one pass queue
        //Relies on the fact that we're only making one pass
        //through and each body can only be pushed/popped once.
        //To push:
        //  queue[queueStart+queueSize++] = newElement;
        //To pop:
        //	poppedElement = queue[queueStart++];
        //  --queueSize;
        int queueCapacity = m_bodyCount;
        BBBody[] queue = new BBBody[queueCapacity];

        for (BBBody b = m_bodyList; b != null; b = b.m_next) {
            b.m_flags &= ~BBBody.e_islandFlag;
            b.m_sweep.t0 = 0.0f;
        }

        for (BBContact c = m_contactManager.m_contactList; c != null; c = c.m_next) {
            // Invalidate TOI
            c.m_flags &= ~(BBContact.e_toiFlag | BBContact.e_islandFlag);
        }

        for (BBJoint j = m_jointList; j != null; j = j.m_next) {
            j.m_islandFlag = false;
        }

        // Find TOI events and solve them.
        for (; ;) {
            // Find the first TOI.
            BBContact minContact = null;
            float minTOI = 1.0f;

            for (BBContact c = m_contactManager.m_contactList; c != null; c = c.m_next) {
                // Can this contact generate a solid TOI contact?
                if (!c.isSolid() || !c.isContinuous()) {
                    continue;
                }

                // TODO_ERIN keep a counter on the contact, only respond to M TOIs per contact.

                float toi = 1.0f;
                if ((c.m_flags & BBContact.e_toiFlag) != 0) {
                    // This contact has a valid cached TOI.
                    toi = c.m_toi;
                } else {
                    // Compute the TOI for this contact.
                    BBFixture s1 = c.getFixtureA();
                    BBFixture s2 = c.getFixtureB();
                    BBBody b1 = s1.getBody();
                    BBBody b2 = s2.getBody();

                    if ((b1.isStatic() || b1.isSleeping()) & (b2.isStatic() || b2.isSleeping())) {
                        continue;
                    }

                    // Put the sweeps onto the same time interval.
                    float t0 = b1.m_sweep.t0;

                    if (b1.m_sweep.t0 < b2.m_sweep.t0) {
                        t0 = b2.m_sweep.t0;
                        b1.m_sweep.advance(t0);
                    } else if (b2.m_sweep.t0 < b1.m_sweep.t0) {
                        t0 = b1.m_sweep.t0;
                        b2.m_sweep.advance(t0);
                    }

                    assert (t0 < 1.0f);

                    // Compute the time of impact.
                    toi = c.ComputeTOI(b1.m_sweep, b2.m_sweep);

                    assert (0.0f <= toi & toi <= 1.0f);

                    // If the TOI is in range ...
                    if (0.0f < toi & toi < 1.0f) {
                        // Interpolate on the actual range.
                        toi = min((1.0f - toi) * t0 + toi, 1.0f);
                    }


                    c.m_toi = toi;
                    c.m_flags |= BBContact.e_toiFlag;
                }

                if (FLT_EPSILON < toi & toi < minTOI) {
                    // This is the minimum TOI found so far.
                    minContact = c;
                    minTOI = toi;
                }
            }

            if (minContact == null || 1.0f - 100.0f * FLT_EPSILON < minTOI) {
                // No more TOI events. Done!
                break;
            }

            // advance the bodies to the TOI.
            BBFixture s1 = minContact.getFixtureA();
            BBFixture s2 = minContact.getFixtureB();
            BBBody b1 = s1.getBody();
            BBBody b2 = s2.getBody();

            BBSweep backup1 = b1.m_sweep;
            BBSweep backup2 = b2.m_sweep;

            b1.advance(minTOI);
            b2.advance(minTOI);

            // The TOI contact likely has some new contact points.
            minContact.Update(m_contactManager.m_contactListener);
            minContact.m_flags &= ~BBContact.e_toiFlag;

            // Is the contact solid?
            if (!minContact.isSolid()) {
                // Restore the sweeps.
                b1.m_sweep = backup1;
                b2.m_sweep = backup2;
                b1.synchronizeTransform();
                b2.synchronizeTransform();
                continue;
            }

            // Did numerical issues prevent a contact point from being generated?
            if (!minContact.isTouching()) {
                // Give up on this TOI.
                continue;
            }

            // Build the TOI island. We need a dynamic seed.
            BBBody seed = b1;
            if (seed.isStatic()) {
                seed = b2;
            }

            // Reset island and queue.
            island.Clear();

            int queueStart = 0; // starting index for queue
            int queueSize = 0;  // elements in queue
            queue[queueStart + queueSize++] = seed;
            seed.m_flags |= BBBody.e_islandFlag;

            // Perform a breadth first search (BFS) on the contact/joint graph.
            while (queueSize > 0) {
                // Grab the next body off the stack and add it to the island.
                BBBody b = queue[queueStart++];
                --queueSize;

                island.add(b);

                // Make sure the body is awake.
                b.m_flags &= ~BBBody.e_sleepFlag;

                // To keep islands as small as possible, we don't
                // propagate islands across static bodies.
                if (b.isStatic()) {
                    continue;
                }

                // Search all contacts connected to this body.
                for (BBContactEdge cEdge = b.m_contactList; cEdge != null; cEdge = cEdge.next) {
                    // Does the TOI island still have space for contacts?
                    if (island.m_contactCount == island.m_contactCapacity) {
                        break;
                    }

                    // Has this contact already been added to an island?
                    if ((cEdge.contact.m_flags & BBContact.e_islandFlag) != 0) {
                        continue;
                    }

                    // Skip separate, sensor, or disabled contacts.
                    if (!cEdge.contact.isSolid() || !cEdge.contact.isTouching()) {
                        continue;
                    }

                    island.add(cEdge.contact);
                    cEdge.contact.m_flags |= BBContact.e_islandFlag;

                    // Update other body.
                    BBBody other = cEdge.other;

                    // Was the other body already added to this island?
                    if ((other.m_flags & BBBody.e_islandFlag) != 0) {
                        continue;
                    }

                    // March forward, this can do no harm since this is the min TOI.
                    if (!other.isStatic()) {
                        other.advance(minTOI);
                        other.wakeUp();
                    }

                    assert (queueStart + queueSize < queueCapacity);
                    queue[queueStart + queueSize] = other;
                    ++queueSize;
                    other.m_flags |= BBBody.e_islandFlag;
                }

                for (BBJointEdge jEdge = b.m_jointList; jEdge != null; jEdge = jEdge.next) {
                    if (island.m_jointCount == island.m_jointCapacity) {
                        continue;
                    }

                    if (jEdge.joint.m_islandFlag) {
                        continue;
                    }

                    island.add(jEdge.joint);

                    jEdge.joint.m_islandFlag = true;

                    BBBody other = jEdge.other;

                    if ((other.m_flags & BBBody.e_islandFlag) != 0) {
                        continue;
                    }

                    if (!other.isStatic()) {
                        other.advance(minTOI);
                        other.wakeUp();
                    }

                    assert (queueStart + queueSize < queueCapacity);
                    queue[queueStart + queueSize] = other;
                    ++queueSize;
                    other.m_flags |= BBBody.e_islandFlag;
                }
            }

            BBTimeStep subStep = new BBTimeStep();
            subStep.warmStarting = false;
            subStep.dt = (1.0f - minTOI) * step.dt;
            subStep.inv_dt = 1.0f / subStep.dt;
            subStep.dtRatio = 0.0f;
            subStep.velocityIterations = step.velocityIterations;
            subStep.positionIterations = step.positionIterations;

            island.solveTOI(subStep);

            // Post solve cleanup.
            for (int i = 0; i < island.m_bodyCount; ++i) {
                // Allow bodies to participate in future TOI islands.
                BBBody b = island.m_bodies[i];
                b.m_flags &= ~BBBody.e_islandFlag;

                if ((b.m_flags & BBBody.e_sleepFlag) != 0) {
                    continue;
                }

                if (b.isStatic()) {
                    continue;
                }

                // Update fixtures (for broad-phase).
                b.synchronizeFixtures();

                // Invalidate all contact TOIs associated with this body. Some of these
                // may not be in the island because they were not touching.
                for (BBContactEdge ce = b.m_contactList; ce != null; ce = ce.next) {
                    ce.contact.m_flags &= ~BBContact.e_toiFlag;
                }
            }

            for (int i = 0; i < island.m_contactCount; ++i) {
                // Allow contacts to participate in future TOI islands.
                BBContact c = island.m_contacts[i];
                c.m_flags &= ~(BBContact.e_toiFlag | BBContact.e_islandFlag);
            }

            for (int i = 0; i < island.m_jointCount; ++i) {
                // Allow joints to participate in future TOI islands.
                BBJoint j = island.m_joints[i];
                j.m_islandFlag = false;
            }

            // Commit fixture proxy movements to the broad-phase so that new contacts are created.
            // Also, some contacts can be destroyed.
            m_contactManager.findNewContacts();
        }

    }


    private void drawJoint(GL10 gl, BBJoint joint) {
        BBBody b1 = joint.getBody1();
        BBBody b2 = joint.getBody2();
        final BBTransform xf1 = b1.getTransform();
        final BBTransform xf2 = b2.getTransform();
        BBVec2 x1 = xf1.position;
        BBVec2 x2 = xf2.position;
        BBVec2 p1 = joint.getAnchor1();
        BBVec2 p2 = joint.getAnchor2();

        BBColor color = new BBColor(0.5f, 0.8f, 0.8f);

        switch (joint.getType()) {
            case BBJoint.e_distanceJoint:
                m_debugDraw.drawSegment(gl, p1, p2, color);
                break;

            case BBJoint.e_pulleyJoint: {
                BBPulleyJoint pulley = (BBPulleyJoint) joint;
                BBVec2 s1 = pulley.GetGroundAnchor1();
                BBVec2 s2 = pulley.GetGroundAnchor2();
                m_debugDraw.drawSegment(gl, s1, p1, color);
                m_debugDraw.drawSegment(gl, s2, p2, color);
                m_debugDraw.drawSegment(gl, s1, s2, color);
            }
            break;

            case BBJoint.e_mouseJoint:
                // don't draw this
                break;

            default:
                m_debugDraw.drawSegment(gl, x1, p1, color);
                m_debugDraw.drawSegment(gl, p1, p2, color);
                m_debugDraw.drawSegment(gl, x2, p2, color);
        }
    }

    private void drawShape(GL10 gl, BBFixture fixture, final BBTransform xf, final BBColor color) {
        BBColor coreColor = new BBColor(0.9f, 0.6f, 0.6f);

        switch (fixture.getType()) {
            case BBShape.e_circle: {
                BBCircleShape circle = (BBCircleShape) fixture.getShape();

                BBVec2 center = mul(xf, circle.m_p);
                float radius = circle.m_radius;
                BBVec2 axis = xf.R.col1;

                m_debugDraw.drawSolidCircle(gl, center, radius, axis, color);
            }
            break;

            case BBShape.e_polygon: {
                BBPolygonShape poly = (BBPolygonShape) fixture.getShape();
                int vertexCount = poly.m_vertexCount;
                assert (vertexCount <= maxPolygonVertices);
                BBVec2[] vertices = new BBVec2[maxPolygonVertices];

                for (int i = 0; i < vertexCount; ++i) {
                    vertices[i] = mul(xf, poly.m_vertices[i]);
                }

                m_debugDraw.drawSolidPolygon(gl, vertices, vertexCount, color);
            }
            break;
        }
    }


    public int m_flags;

    public BBContactManager m_contactManager = new BBContactManager();

    private BBBody m_bodyList;
    private BBJoint m_jointList;

    private int m_bodyCount;
    private int m_jointCount;

    private BBVec2 m_gravity = new BBVec2();
    private boolean m_allowSleep;

    // private BBBody m_groundBody;

    private BBDestructionListener m_destructionListener;
    private BBDebugDraw m_debugDraw;

    // This is used to compute the time step ratio to
    // support a variable time step.
    private float m_inv_dt0;

    // This is for debugging the solver.
    private boolean m_warmStarting;

    // This is for debugging the solver.
    private boolean m_continuousPhysics;


    public static class BBWorldQueryWrapper {
        public boolean queryCallback(int proxyId) {
            BBFixture fixture = (BBFixture) broadPhase.getUserData(proxyId);
            return callback.reportFixture(fixture);
        }

        BBBroadPhase broadPhase;
        BBQueryCallback callback;
    }


    public static class BBWorldRayCastWrapper {
        public float RayCastCallback(final BBRayCastInput input, int proxyId) {
            Object userData = broadPhase.getUserData(proxyId);
            BBFixture fixture = (BBFixture) userData;
            BBRayCastOutput output = new BBRayCastOutput();
            fixture.rayCast(output, input);

            if (output.hit) {
                float fraction = output.fraction;
                BBVec2 point = add(mul((1.0f - fraction), input.p1), mul(fraction, input.p2));
                return callback.reportFixture(fixture, point, output.normal, fraction);
            }

            return input.maxFraction;
        }

        BBBroadPhase broadPhase;
        BBRayCastCallback callback;
    }
}
