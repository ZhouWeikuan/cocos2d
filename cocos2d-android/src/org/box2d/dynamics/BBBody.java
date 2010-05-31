package org.box2d.dynamics;

import org.box2d.collision.BBBroadPhase;
import org.box2d.collision.shapes.BBShape;
import static org.box2d.collision.shapes.BBShape.BBMassData;
import static org.box2d.common.BBMath.*;
import org.box2d.common.BBSweep;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;
import static org.box2d.dynamics.BBFixture.BBFixtureDef;
import org.box2d.dynamics.contacts.BBContact;
import static org.box2d.dynamics.contacts.BBContact.BBContactEdge;
import static org.box2d.dynamics.joints.BBJoint.BBJointEdge;


public class BBBody {
    public static class BBBodyDef {
        /// This finalructor sets the body definition default values.
        public BBBodyDef() {
            userData = null;
            position.set(0.0f, 0.0f);
            angle = 0.0f;
            linearVelocity.set(0.0f, 0.0f);
            angularVelocity = 0.0f;
            linearDamping = 0.0f;
            angularDamping = 0.0f;
            allowSleep = true;
            isSleeping = false;
            fixedRotation = false;
            isBullet = false;
        }


        /// Use this to store application specific body data.
        public Object userData;

        /// The world position of the body. Avoid creating bodies at the origin
        /// since this can lead to many overlapping shapes.
        public BBVec2 position = new BBVec2();

        /// The world angle of the body in radians.
        public float angle;

        /// The linear velocity of the body in world co-ordinates.
        public BBVec2 linearVelocity = new BBVec2();

        /// The angular velocity of the body.
        public float angularVelocity;

        /// Linear damping is use to reduce the linear velocity. The damping parameter
        /// can be larger than 1.0f but the damping effect becomes sensitive to the
        /// time step when the damping parameter is large.
        public float linearDamping;

        /// Angular damping is use to reduce the angular velocity. The damping parameter
        /// can be larger than 1.0f but the damping effect becomes sensitive to the
        /// time step when the damping parameter is large.
        public float angularDamping;

        /// set this flag to false if this body should never fall asleep. Note that
        /// this increases CPU usage.
        public boolean allowSleep;

        /// Is this body initially sleeping?
        public boolean isSleeping;

        /// Should this body be prevented from rotating? Useful for characters.
        public boolean fixedRotation;

        /// Is this a fast moving body that should be prevented from tunneling through
        /// other moving bodies? Note that all bodies are prevented from tunneling through
        /// static bodies.
        /// @warning You should use this flag sparingly since it increases processing time.
        public boolean isBullet;
    }

    /// A rigid body. These are created via BBWorld.createBody.
    public BBBody(final BBBodyDef bd, BBWorld world) {
        m_flags = 0;

        if (bd.isBullet) {
            m_flags |= e_bulletFlag;
        }
        if (bd.fixedRotation) {
            m_flags |= e_fixedRotationFlag;
        }
        if (bd.allowSleep) {
            m_flags |= e_allowSleepFlag;
        }
        if (bd.isSleeping) {
            m_flags |= e_sleepFlag;
        }

        m_world = world;

        m_xf.position = bd.position;
        m_xf.R.set(bd.angle);

        m_sweep.localCenter.setZero();
        m_sweep.t0 = 1.0f;
        m_sweep.a0 = m_sweep.a = bd.angle;
        m_sweep.c0 = m_sweep.c = mul(m_xf, m_sweep.localCenter);

        m_jointList = null;
        m_contactList = null;
        m_prev = null;
        m_next = null;

        m_linearVelocity = bd.linearVelocity;
        m_angularVelocity = bd.angularVelocity;

        m_linearDamping = bd.linearDamping;
        m_angularDamping = bd.angularDamping;

        m_force.set(0.0f, 0.0f);
        m_torque = 0.0f;

        m_sleepTime = 0.0f;

        m_mass = 0.0f;
        m_invMass = 0.0f;
        m_I = 0.0f;
        m_invI = 0.0f;

        m_type = e_staticType;

        m_userData = bd.userData;

        m_fixtureList = null;
        m_fixtureCount = 0;
    }

    /// Creates a fixture and attach it to this body. Use this function if you need
    /// to set some fixture parameters, like friction. Otherwise you can create the
    /// fixture directly from a shape.
    /// This function automatically updates the mass of the body.
    /// @param def the fixture definition.
    /// @warning This function is locked during callbacks.
    public BBFixture createFixture(final BBFixtureDef def) {
        assert (!m_world.IsLocked());
        if (m_world.IsLocked()) {
            return null;
        }

        BBBroadPhase broadPhase = m_world.m_contactManager.m_broadPhase;

        BBFixture fixture = new BBFixture();
        fixture.create(broadPhase, this, m_xf, def);

        fixture.m_next = m_fixtureList;
        m_fixtureList = fixture;
        ++m_fixtureCount;

        fixture.m_body = this;

        boolean needMassUpdate = fixture.m_massData.mass > 0.0f || fixture.m_massData.I > 0.0f;

        // Adjust mass properties if needed.
        if (needMassUpdate) {
            resetMass();
        }

        // Let the world know we have a new fixture. This will cause new contacts
        // to be created at the beginning of the next time step.
        m_world.m_flags |= BBWorld.e_newFixture;

        return fixture;
    }

    /// Creates a fixture from a shape and attach it to this body.
    /// This is a convenience function. Use BBFixtureDef if you need to set parameters
    /// like friction, restitution, user data, or filtering.
    /// This function automatically updates the mass of the body.
    /// @param shape the shape to be cloned.
    /// @param density the shape density (set to zero for static bodies).
    /// @warning This function is locked during callbacks.
    public BBFixture createFixture(final BBShape shape) {
        return createFixture(shape, 0.0f);
    }

    public BBFixture createFixture(final BBShape shape, float density) {
        BBFixtureDef def = new BBFixtureDef();
        def.shape = shape;
        def.density = density;

        return createFixture(def);
    }


    /// destroy a fixture. This removes the fixture from the broad-phase and
    /// therefore destroys any contacts associated with this fixture. All fixtures
    /// attached to a body are implicitly destroyed when the body is destroyed.
    /// @param fixture the fixture to be removed.
    /// @warning This function is locked during callbacks.
    public void destroyFixture(BBFixture fixture) {
        assert (!m_world.IsLocked());
        if (m_world.IsLocked()) {
            return;
        }

        assert (fixture.m_body == this);

        // Remove the fixture from this body's singly linked list.
        assert (m_fixtureCount > 0);
        BBFixture node = m_fixtureList;
        boolean found = false;
        while (node != null) {
            if (node == fixture) {
                node = fixture.m_next;
                found = true;
                break;
            }

            node = node.m_next;
        }

        // You tried to remove a shape that is not attached to this body.
        assert (found);

        // destroy any contacts associated with the fixture.
        BBContactEdge edge = m_contactList;
        while (edge != null) {
            BBContact c = edge.contact;
            edge = edge.next;

            BBFixture fixtureA = c.getFixtureA();
            BBFixture fixtureB = c.getFixtureB();

            if (fixture == fixtureA || fixture == fixtureB) {
                // This destroys the contact and removes it from
                // this body's contact list.
                m_world.m_contactManager.destroy(c);
            }
        }

        boolean needMassUpdate = fixture.m_massData.mass > 0.0f || fixture.m_massData.I > 0.0f;

        BBBroadPhase broadPhase = m_world.m_contactManager.m_broadPhase;

        fixture.destroy(broadPhase);
        fixture.m_body = null;
        fixture.m_next = null;

        --m_fixtureCount;

        // Adjust mass properties if needed.
        if (needMassUpdate) {
            resetMass();
        }
    }

    /// set the position of the body's origin and rotation.
    /// This breaks any contacts and wakes the other bodies.
    /// @param position the world position of the body's local origin.
    /// @param angle the world rotation in radians.
    public void setTransform(final BBVec2 position, float angle) {
        assert (!m_world.IsLocked());
        if (m_world.IsLocked()) {
            return;
        }

        m_xf.R.set(angle);
        m_xf.position = position;

        m_sweep.c0 = m_sweep.c = mul(m_xf, m_sweep.localCenter);
        m_sweep.a0 = m_sweep.a = angle;

        BBBroadPhase broadPhase = m_world.m_contactManager.m_broadPhase;
        for (BBFixture f = m_fixtureList; f != null; f = f.m_next) {
            f.synchronize(broadPhase, m_xf, m_xf);
        }

        m_world.m_contactManager.findNewContacts();
    }

    /// Get the body transform for the body's origin.
    /// @return the world transform of the body's origin.
    public final BBTransform getTransform() {
        return m_xf;
    }

    /// Get the world body origin position.
    /// @return the world position of the body's origin.
    public final BBVec2 getPosition() {
        return m_xf.position;
    }


    /// Get the angle in radians.
    /// @return the current world rotation angle in radians.
    public float getAngle() {
        return m_sweep.a;
    }

    /// Get the world position of the center of mass.
    public final BBVec2 getWorldCenter() {
        return m_sweep.c;
    }

    /// Get the local position of the center of mass.
    public final BBVec2 getLocalCenter() {
        return m_sweep.localCenter;
    }

    /// set the linear velocity of the center of mass.
    /// @param v the new linear velocity of the center of mass.
    public void setLinearVelocity(final BBVec2 v) {
        m_linearVelocity = v;
    }

    /// Get the linear velocity of the center of mass.
    /// @return the linear velocity of the center of mass.
    public BBVec2 getLinearVelocity() {
        return m_linearVelocity;
    }

    /// set the angular velocity.
    /// @param omega the new angular velocity in radians/second.
    public void setAngularVelocity(float omega) {
        m_angularVelocity = omega;
    }

    /// Get the angular velocity.
    /// @return the angular velocity in radians/second.
    public float getAngularVelocity() {
        return m_angularVelocity;
    }

    /// Apply a force at a world point. If the force is not
    /// applied at the center of mass, it will generate a torque and
    /// affect the angular velocity. This wakes up the body.
    /// @param force the world force vector, usually in Newtons (N).
    /// @param point the world position of the point of application.
    public void applyForce(final BBVec2 force, final BBVec2 point) {
        if (isSleeping()) {
            wakeUp();
        }
        m_force.add(force);
        m_torque += cross(sub(point, m_sweep.c), force);
    }

    /// Apply a torque. This affects the angular velocity
    /// without affecting the linear velocity of the center of mass.
    /// This wakes up the body.
    /// @param torque about the z-axis (out of the screen), usually in N-m.
    public void applyTorque(float torque) {
        if (isSleeping()) {
            wakeUp();
        }
        m_torque += torque;
    }

    /// Apply an impulse at a point. This immediately modifies the velocity.
    /// It also modifies the angular velocity if the point of application
    /// is not at the center of mass. This wakes up the body.
    /// @param impulse the world impulse vector, usually in N-seconds or kg-m/s.
    /// @param point the world position of the point of application.
    public void applyImpulse(final BBVec2 impulse, final BBVec2 point) {
        if (isSleeping()) {
            wakeUp();
        }
        m_linearVelocity.add(mul(m_invMass, impulse));
        m_angularVelocity += m_invI * cross(sub(point, m_sweep.c), impulse);
    }

    /// Get the total mass of the body.
    /// @return the mass, usually in kilograms (kg).
    public float getMass() {
        return m_mass;
    }

    /// Get the central rotational inertia of the body.
    /// @return the rotational inertia, usually in kg-m^2.
    public float getInertia() {
        return m_I;
    }

    /// Get the mass data of the body. The rotational inertia is relative
    /// to the center of mass.
    /// @return a class containing the mass, inertia and center of the body.
    public void getMassData(BBMassData data) {
        data.mass = m_mass;
        data.I = m_I;
        data.center = m_sweep.localCenter;
    }

    /// set the mass properties to override the mass properties of the fixtures.
    /// Note that this changes the center of mass position. You can make the body
    /// static by using zero mass.
    /// Note that creating or destroying fixtures can also alter the mass.
    /// @warning The supplied rotational inertia is assumed to be relative to the center of mass.
    /// @param massData the mass properties.
    public void setMassData(final BBMassData massData) {
        assert (!m_world.IsLocked());
        if (m_world.IsLocked()) {
            return;
        }

        m_invMass = 0.0f;
        m_I = 0.0f;
        m_invI = 0.0f;

        m_mass = massData.mass;

        if (m_mass > 0.0f) {
            m_invMass = 1.0f / m_mass;
        }

        if (massData.I > 0.0f & (m_flags & BBBody.e_fixedRotationFlag) == 0) {
            m_I = massData.I - m_mass * dot(massData.center, massData.center);
            m_invI = 1.0f / m_I;
        }

        // Move center of mass.
        BBVec2 oldCenter = m_sweep.c;
        m_sweep.localCenter = massData.center;
        m_sweep.c0 = m_sweep.c = mul(m_xf, m_sweep.localCenter);

        // Update center of mass velocity.
        m_linearVelocity.add(cross(m_angularVelocity, sub(m_sweep.c, oldCenter)));

        int oldType = m_type;
        if (m_invMass == 0.0f & m_invI == 0.0f) {
            m_type = e_staticType;
            m_angularVelocity = 0.0f;
            m_linearVelocity.setZero();
        } else {
            m_type = e_dynamicType;
        }

        // If the body type changed, we need to flag contacts for filtering.
        if (oldType != m_type) {
            for (BBContactEdge ce = m_contactList; ce != null; ce = ce.next) {
                ce.contact.FlagForFiltering();
            }
        }
    }


    /// This resets the mass properties to the sum of the mass properties of the fixtures.
    /// This normally does not need to be called unless you called setMassData to override
    /// the mass and you later want to reset the mass.
    public void resetMass() {
        // Compute mass data from shapes. Each shape has its own density.
        m_mass = 0.0f;
        m_invMass = 0.0f;
        m_I = 0.0f;
        m_invI = 0.0f;

        BBVec2 center = vec2_zero;
        for (BBFixture f = m_fixtureList; f != null; f = f.m_next) {
            final BBMassData massData = f.getMassData();
            m_mass += massData.mass;
            center.add(mul(massData.mass, massData.center));
            m_I += massData.I;
        }

        // Compute center of mass.
        if (m_mass > 0.0f) {
            m_invMass = 1.0f / m_mass;
            center.mul(m_invMass);
        }

        if (m_I > 0.0f & (m_flags & e_fixedRotationFlag) == 0) {
            // Center the inertia about the center of mass.
            m_I -= m_mass * dot(center, center);
            assert (m_I > 0.0f);
            m_invI = 1.0f / m_I;
        } else {
            m_I = 0.0f;
            m_invI = 0.0f;
        }

        // Move center of mass.
        BBVec2 oldCenter = m_sweep.c;
        m_sweep.localCenter = center;
        m_sweep.c0 = m_sweep.c = mul(m_xf, m_sweep.localCenter);

        // Update center of mass velocity.
        m_linearVelocity.add(cross(m_angularVelocity, sub(m_sweep.c, oldCenter)));

        // Determine the new body type.
        int oldType = m_type;
        if (m_invMass == 0.0f & m_invI == 0.0f) {
            m_type = e_staticType;
        } else {
            m_type = e_dynamicType;
        }

        // If the body type changed, we need to flag contacts for filtering.
        if (oldType != m_type) {
            for (BBContactEdge ce = m_contactList; ce != null; ce = ce.next) {
                ce.contact.FlagForFiltering();
            }
        }
    }


    /// Get the world coordinates of a point given the local coordinates.
    /// @param localPoint a point on the body measured relative the the body's origin.
    /// @return the same point expressed in world coordinates.
    public BBVec2 getWorldPoint(final BBVec2 localPoint) {
        return mul(m_xf, localPoint);
    }

    /// Get the world coordinates of a vector given the local coordinates.
    /// @param localVector a vector fixed in the body.
    /// @return the same vector expressed in world coordinates.
    public BBVec2 getWorldVector(final BBVec2 localVector) {
        return mul(m_xf.R, localVector);
    }

    /// Gets a local point relative to the body's origin given a world point.
    /// @param a point in world coordinates.
    /// @return the corresponding local point relative to the body's origin.
    public BBVec2 getLocalPoint(final BBVec2 worldPoint) {
        return mulT(m_xf, worldPoint);
    }

    /// Gets a local vector given a world vector.
    /// @param a vector in world coordinates.
    /// @return the corresponding local vector.
    public BBVec2 getLocalVector(final BBVec2 worldVector) {
        return mulT(m_xf.R, worldVector);
    }

    /// Get the world linear velocity of a world point attached to this body.
    /// @param a point in world coordinates.
    /// @return the world velocity of a point.
    public BBVec2 getLinearVelocityFromWorldPoint(final BBVec2 worldPoint) {
        return add(m_linearVelocity, cross(m_angularVelocity, sub(worldPoint, m_sweep.c)));
    }


    /// Get the world velocity of a local point.
    /// @param a point in local coordinates.
    /// @return the world velocity of a point.
    public BBVec2 getLinearVelocityFromLocalPoint(final BBVec2 localPoint) {
        return getLinearVelocityFromWorldPoint(getWorldPoint(localPoint));
    }

    /// Get the linear damping of the body.
    public float getLinearDamping() {
        return m_linearDamping;
    }

    /// set the linear damping of the body.
    public void setLinearDamping(float linearDamping) {
        m_linearDamping = linearDamping;
    }

    /// Get the angular damping of the body.
    public float getAngularDamping() {
        return m_angularDamping;
    }

    /// set the angular damping of the body.
    public void setAngularDamping(float angularDamping) {
        m_angularDamping = angularDamping;
    }

    /// Is this body treated like a bullet for continuous collision detection?
    public boolean isBullet() {
        return (m_flags & e_bulletFlag) == e_bulletFlag;
    }

    /// Should this body be treated like a bullet for continuous collision detection?
    void setBullet(boolean flag) {
        if (flag) {
            m_flags |= e_bulletFlag;
        } else {
            m_flags &= ~e_bulletFlag;
        }
    }

    /// Is this body static (immovable)?
    public boolean isStatic() {
        return m_type == e_staticType;
    }


    /// Is this body dynamic (movable)?
    boolean isDynamic() {
        return m_type == e_dynamicType;
    }

    /// Is this body sleeping (not simulating).
    public boolean isSleeping() {
        return (m_flags & e_sleepFlag) == e_sleepFlag;
    }

    /// Is this body allowed to sleep
    boolean isAllowSleeping() {
        return (m_flags & e_allowSleepFlag) == e_allowSleepFlag;
    }

    /// You can disable sleeping on this body.
    void allowSleeping(boolean flag) {
        if (flag) {
            m_flags |= e_allowSleepFlag;
        } else {
            m_flags &= ~e_allowSleepFlag;
            wakeUp();
        }
    }

    /// Wake up this body so it will begin simulating.
    public void wakeUp() {
        m_flags &= ~e_sleepFlag;
        m_sleepTime = 0.0f;
    }


    /// Put this body to sleep so it will stop simulating.
    /// This also sets the velocity to zero.
    void putToSleep() {
        m_flags |= e_sleepFlag;
        m_sleepTime = 0.0f;
        m_linearVelocity.setZero();
        m_angularVelocity = 0.0f;
        m_force.setZero();
        m_torque = 0.0f;
    }

    /// Get the list of all fixtures attached to this body.
    BBFixture getFixtureList() {
        return m_fixtureList;
    }


    /// Get the list of all joints attached to this body.
    BBJointEdge getJointList() {
        return m_jointList;
    }

    /// Get the list of all contacts attached to this body.
    /// @warning this list changes during the time step and you may
    /// miss some collisions if you don't use BBContactListener.
    BBContactEdge getContactList() {
        return m_contactList;
    }


    /// Get the next body in the world's body list.
    public BBBody getNext() {
        return m_next;
    }

    /// Get the user data pointer that was provided in the body definition.
    public Object getUserData() {
        return m_userData;
    }

    /// set the user data. Use this to store your application specific data.
    void setUserData(Object data) {
        m_userData = data;
    }

    /// Get the parent world of this body.
    BBWorld GetWorld() {
        return m_world;
    }

    // m_flags
    public static final int e_islandFlag = 0x0001;
    public static final int e_sleepFlag = 0x0002;
    public static final int e_allowSleepFlag = 0x0004;
    public static final int e_bulletFlag = 0x0008;
    public static final int e_fixedRotationFlag = 0x0010;

    // m_type
    public static final int e_staticType = 1;
    public static final int e_dynamicType = 2;
    public static final int e_maxTypes = 3;


    void synchronizeFixtures() {
        BBTransform xf1 = new BBTransform();
        xf1.R.set(m_sweep.a0);
        xf1.position = sub(m_sweep.c0, mul(xf1.R, m_sweep.localCenter));

        BBBroadPhase broadPhase = m_world.m_contactManager.m_broadPhase;
        for (BBFixture f = m_fixtureList; f != null; f = f.m_next) {
            f.synchronize(broadPhase, xf1, m_xf);
        }
    }


    public void synchronizeTransform() {
        m_xf.R.set(m_sweep.a);
        m_xf.position = sub(m_sweep.c, mul(m_xf.R, m_sweep.localCenter));
    }

    // This is used to prevent connected bodies from colliding.
    // It may lie, depending on the collideConnected flag.
    boolean isConnected(final BBBody other) {
        for (BBJointEdge jn = m_jointList; jn != null; jn = jn.next) {
            if (jn.other == other) {
                return jn.joint.m_collideConnected == false;
            }
        }

        return false;
    }

    void advance(float t) {
        // advance to the new safe time.
        m_sweep.advance(t);
        m_sweep.c = m_sweep.c0;
        m_sweep.a = m_sweep.a0;
        synchronizeTransform();
    }


    int m_flags;
    int m_type;

    int m_islandIndex;

    public BBTransform m_xf = new BBTransform();        // the body origin transform
    public BBSweep m_sweep = new BBSweep();    // the swept motion for CCD

    public BBVec2 m_linearVelocity;
    public float m_angularVelocity;

    BBVec2 m_force = new BBVec2();
    float m_torque;

    BBWorld m_world;
    BBBody m_prev;
    BBBody m_next;

    BBFixture m_fixtureList;
    int m_fixtureCount;

    BBJointEdge m_jointList;
    BBContactEdge m_contactList;

    public float m_mass;
    public float m_invMass;
    public float m_I;
    public float m_invI;

    float m_linearDamping;
    float m_angularDamping;

    float m_sleepTime;

    Object m_userData;
}
