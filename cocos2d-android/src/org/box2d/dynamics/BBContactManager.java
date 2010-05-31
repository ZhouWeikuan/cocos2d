package org.box2d.dynamics;

import org.box2d.collision.BBBroadPhase;
import static org.box2d.dynamics.BBWorldCallbacks.*;
import org.box2d.dynamics.contacts.BBContact;
import static org.box2d.dynamics.contacts.BBContact.BBContactEdge;

// Delegate of BBWorld.
public class BBContactManager {

    public static BBContactFilter defaultFilter = new BBContactFilter();
    public static BBContactListener defaultListener = new BBContactListener();

    public BBContactManager() {
        m_contactList = null;
        m_contactCount = 0;
        m_contactFilter = defaultFilter;
        m_contactListener = defaultListener;
    }


    // Broad-phase callback.
    public void addPair(Object proxyUserDataA, Object proxyUserDataB) {
        BBFixture fixtureA = (BBFixture) proxyUserDataA;
        BBFixture fixtureB = (BBFixture) proxyUserDataB;

        BBBody bodyA = fixtureA.getBody();
        BBBody bodyB = fixtureB.getBody();

        // Are the fixtures on the same body?
        if (bodyA == bodyB) {
            return;
        }

        // Are both bodies static?
        if (bodyA.isStatic() & bodyB.isStatic()) {
            return;
        }

        // Does a contact already exist?
        BBContactEdge edge = bodyB.getContactList();
        while (edge != null) {
            if (edge.other == bodyA) {
                BBFixture fA = edge.contact.getFixtureA();
                BBFixture fB = edge.contact.getFixtureB();
                if (fA == fixtureA & fB == fixtureB) {
                    // A contact already exists.
                    return;
                }

                if (fA == fixtureB & fB == fixtureA) {
                    // A contact already exists.
                    return;
                }
            }

            edge = edge.next;
        }

        // Does a joint override collision?
        if (bodyB.isConnected(bodyA)) {
            return;
        }

        // Check user filtering.
        if (!m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
            return;
        }

        // Call the factory.
        BBContact c = BBContact.create(fixtureA, fixtureB);

        // Contact creation may swap fixtures.
        fixtureA = c.getFixtureA();
        fixtureB = c.getFixtureB();
        bodyA = fixtureA.getBody();
        bodyB = fixtureB.getBody();

        // Insert into the world.
        c.m_prev = null;
        c.m_next = m_contactList;
        if (m_contactList != null) {
            m_contactList.m_prev = c;
        }
        m_contactList = c;

        // Connect to island graph.

        // Connect to body A
        c.m_nodeA.contact = c;
        c.m_nodeA.other = bodyB;

        c.m_nodeA.prev = null;
        c.m_nodeA.next = bodyA.m_contactList;
        if (bodyA.m_contactList != null) {
            bodyA.m_contactList.prev = c.m_nodeA;
        }
        bodyA.m_contactList = c.m_nodeA;

        // Connect to body B
        c.m_nodeB.contact = c;
        c.m_nodeB.other = bodyA;

        c.m_nodeB.prev = null;
        c.m_nodeB.next = bodyB.m_contactList;
        if (bodyB.m_contactList != null) {
            bodyB.m_contactList.prev = c.m_nodeB;
        }
        bodyB.m_contactList = c.m_nodeB;

        ++m_contactCount;

    }


    public void findNewContacts() {
        m_broadPhase.updatePairs(this);
    }

    public void destroy(BBContact c) {
        BBFixture fixtureA = c.getFixtureA();
        BBFixture fixtureB = c.getFixtureB();
        BBBody bodyA = fixtureA.getBody();
        BBBody bodyB = fixtureB.getBody();

        if (c.m_manifold.m_pointCount > 0) {
            m_contactListener.endContact(c);
        }

        // Remove from the world.
        if (c.m_prev != null) {
            c.m_prev.m_next = c.m_next;
        }

        if (c.m_next != null) {
            c.m_next.m_prev = c.m_prev;
        }

        if (c == m_contactList) {
            m_contactList = c.m_next;
        }

        // Remove from body 1
        if (c.m_nodeA.prev != null) {
            c.m_nodeA.prev.next = c.m_nodeA.next;
        }

        if (c.m_nodeA.next != null) {
            c.m_nodeA.next.prev = c.m_nodeA.prev;
        }

        if (c.m_nodeA == bodyA.m_contactList) {
            bodyA.m_contactList = c.m_nodeA.next;
        }

        // Remove from body 2
        if (c.m_nodeB.prev != null) {
            c.m_nodeB.prev.next = c.m_nodeB.next;
        }

        if (c.m_nodeB.next != null) {
            c.m_nodeB.next.prev = c.m_nodeB.prev;
        }

        if (c.m_nodeB == bodyB.m_contactList) {
            bodyB.m_contactList = c.m_nodeB.next;
        }

        // Call the factory.
        BBContact.destroy(c);
        --m_contactCount;
    }


    public void collide() {
        // Update awake contacts.
        BBContact c = m_contactList;
        while (c != null) {
            BBFixture fixtureA = c.getFixtureA();
            BBFixture fixtureB = c.getFixtureB();
            BBBody bodyA = fixtureA.getBody();
            BBBody bodyB = fixtureB.getBody();

            if (bodyA.isSleeping() & bodyB.isSleeping()) {
                c = c.GetNext();
                continue;
            }

            // Is this contact flagged for filtering?
            if ((c.m_flags & BBContact.e_filterFlag) != 0) {
                // Are both bodies static?
                if (bodyA.isStatic() & bodyB.isStatic()) {
                    BBContact cNuke = c;
                    c = cNuke.GetNext();
                    destroy(cNuke);
                    continue;
                }

                // Does a joint override collision?
                if (bodyB.isConnected(bodyA)) {
                    BBContact cNuke = c;
                    c = cNuke.GetNext();
                    destroy(cNuke);
                    continue;
                }

                // Check user filtering.
                if (!m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
                    BBContact cNuke = c;
                    c = cNuke.GetNext();
                    destroy(cNuke);
                    continue;
                }

                // Clear the filtering flag.
                c.m_flags &= ~BBContact.e_filterFlag;
            }

            int proxyIdA = fixtureA.m_proxyId;
            int proxyIdB = fixtureB.m_proxyId;
            boolean overlap = m_broadPhase.testOverlap(proxyIdA, proxyIdB);

            // Here we destroy contacts that cease to overlap in the broad-phase.
            if (!overlap) {
                BBContact cNuke = c;
                c = cNuke.GetNext();
                destroy(cNuke);
                continue;
            }

            // The contact persists.
            c.Update(m_contactListener);
            c = c.GetNext();
        }
    }


    BBBroadPhase m_broadPhase = new BBBroadPhase();
    BBContact m_contactList;
    int m_contactCount;
    BBContactFilter m_contactFilter;
    BBContactListener m_contactListener;

}
