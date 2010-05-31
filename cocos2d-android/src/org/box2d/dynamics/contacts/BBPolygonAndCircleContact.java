package org.box2d.dynamics.contacts;

import static org.box2d.collision.BBCollide.collidePolygonAndCircle;
import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBPolygonShape;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBFixture;

public class BBPolygonAndCircleContact extends BBContact {
    public static BBContact create(BBFixture fixtureA, BBFixture fixtureB) {
        return new BBPolygonAndCircleContact(fixtureA, fixtureB);
    }

    public static void destroy(BBContact contact) {
    }

    BBPolygonAndCircleContact(BBFixture fixtureA, BBFixture fixtureB) {
        super(fixtureA, fixtureB);
    }

    public void evaluate() {
        BBBody bodyA = m_fixtureA.getBody();
        BBBody bodyB = m_fixtureB.getBody();

        collidePolygonAndCircle(m_manifold,
                (BBPolygonShape) m_fixtureA.getShape(), bodyA.getTransform(),
                (BBCircleShape) m_fixtureB.getShape(), bodyB.getTransform());
    }

}
