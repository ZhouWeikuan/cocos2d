package org.box2d.dynamics.contacts;

import static org.box2d.collision.BBCollide.collideCircles;
import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBShape;
import org.box2d.dynamics.BBBody;
import org.box2d.dynamics.BBFixture;

public class BBCircleContact extends BBContact {
    public static BBContact create(BBFixture fixtureA, BBFixture fixtureB) {
        assert (fixtureA.getType() == BBShape.e_circle);
        assert (fixtureB.getType() == BBShape.e_circle);
        return new BBCircleContact(fixtureA, fixtureB);
    }

    public static void destroy(BBContact contact) {
    }

    public BBCircleContact(BBFixture fixtureA, BBFixture fixtureB) {
        super(fixtureA, fixtureB);
    }


    public void evaluate() {
        BBBody bodyA = m_fixtureA.getBody();
        BBBody bodyB = m_fixtureB.getBody();

        collideCircles(m_manifold,
                (BBCircleShape) m_fixtureA.getShape(), bodyA.getTransform(),
                (BBCircleShape) m_fixtureB.getShape(), bodyB.getTransform());
    }

}
