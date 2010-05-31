package org.box2d.dynamics.contacts;

import static org.box2d.collision.BBCollide.collidePolygons;
import org.box2d.collision.shapes.BBPolygonShape;
import org.box2d.collision.shapes.BBShape;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBFixture;

public class BBPolygonContact extends BBContact {
    public static BBContact create(BBFixture fixtureA, BBFixture fixtureB) {
        assert (fixtureA.getType() == BBShape.e_polygon);
        assert (fixtureB.getType() == BBShape.e_polygon);
        return new BBPolygonContact(fixtureA, fixtureB);
    }

    public static void destroy(BBContact contact) {
    }


    public BBPolygonContact(BBFixture fixtureA, BBFixture fixtureB) {
        super(fixtureA, fixtureB);
    }

    public void evaluate() {
        BBBody bodyA = m_fixtureA.getBody();
        BBBody bodyB = m_fixtureB.getBody();

        collidePolygons(m_manifold,
                (BBPolygonShape) m_fixtureA.getShape(), bodyA.getTransform(),
                (BBPolygonShape) m_fixtureB.getShape(), bodyB.getTransform());
    }

}
