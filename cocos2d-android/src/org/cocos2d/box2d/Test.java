package org.cocos2d.box2d;

import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBWorld;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.contacts.BBContact;
import org.box2d.dynamics.joints.BBMouseJoint;
import org.box2d.dynamics.joints.BBJoint;

import static org.box2d.dynamics.BBWorldCallbacks.*;
import static org.box2d.collision.BBCollision.*;
import static org.box2d.dynamics.BBBody.*;

public abstract class Test extends BBContactListener {

    public static final int k_maxContactPoints = 2048;

    public Test() {
        m_debugDraw = new GLESDebugDraw();
        BBVec2 gravity = new BBVec2();
        gravity.set(0.0f, -10.0f);
        boolean doSleep = true;
        m_world = new BBWorld(gravity, doSleep);
        m_bomb = null;
        m_textLine = 30;
        m_mouseJoint = null;
        m_pointCount = 0;

        m_destructionListener.test = this;
        m_world.SetDestructionListener(m_destructionListener);
        m_world.SetContactListener(this);
        m_world.SetDebugDraw(m_debugDraw);

        int flags = 0;
        flags += BBDebugDraw.e_shapeBit;
        flags += BBDebugDraw.e_jointBit;
//	flags += b2DebugDraw.e_aabbBit;
//	flags += b2DebugDraw.e_pairBit;
        flags += BBDebugDraw.e_centerOfMassBit;
        m_debugDraw.setFlags(flags);

        m_bombSpawning = false;

        m_stepCount = 0;

        BBBodyDef bodyDef = new BBBodyDef();
        m_groundBody = m_world.createBody(bodyDef);
        
    }

    public abstract void SetGravity(float x,float y);

    public void SetTextLine(int line) {
        m_textLine = line;
    }
    public void drawTitle(int x, int y, String string) {

    }

    public abstract void Step(Settings settings);

    public abstract void Keyboard(char key);

    public abstract void ShiftMouseDown(final BBVec2 p);

    public abstract boolean MouseDown(final BBVec2 p);

    public abstract void MouseUp(final BBVec2 p);

    public abstract void MouseMove(final BBVec2 p);

    public abstract void LaunchBomb();

    public abstract void LaunchBomb(final BBVec2 position, final BBVec2 velocity);

    public abstract void SpawnBomb(final BBVec2 worldPt);

    public abstract void CompleteBombSpawn(final BBVec2 p);

    // Let derived tests know that a joint was destroyed.
    public abstract void JointDestroyed(BBJoint joint);

    // Callbacks for derived classes.
    public abstract void beginContact(BBContact contact);

    public abstract void endContact(BBContact contact);

    public abstract void PreSolve(BBContact contact, final BBManifold oldManifold);

    public abstract void PostSolve(BBContact contact, final BBContactImpulse impulse);

    public BBWorld m_world;

    public BBMouseJoint m_mouseJoint;

    protected BBBody m_groundBody;
    protected BBAABB m_worldAABB;
    protected ContactPoint[] m_points = new ContactPoint[k_maxContactPoints];
    protected int m_pointCount;
    protected DestructionListener m_destructionListener;
    protected GLESDebugDraw m_debugDraw;
    protected int m_textLine;
    protected BBBody m_bomb;
    protected BBVec2 m_bombSpawnPoint;
    protected boolean m_bombSpawning;
    protected BBVec2 m_mouseWorld;
    protected int m_stepCount;

}
