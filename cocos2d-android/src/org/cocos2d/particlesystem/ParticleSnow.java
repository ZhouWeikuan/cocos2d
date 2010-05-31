package org.cocos2d.particlesystem;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.TextureManager;

public class ParticleSnow extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleSnow();
    }

    protected ParticleSnow() {
        this(700);
    }

    protected ParticleSnow(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = -1;

        // angle
        angle = -90;
        angleVar = 5;

        // speed of particles
        speed = 5;
        speedVar = 1;

        // radial
        radialAccel = 0;
        radialAccelVar = 1;

        // tagential
        tangentialAccel = 0;
        tangentialAccelVar = 1;

        // emitter position
        setPosition(Director.sharedDirector().winSize().width / 2,
                Director.sharedDirector().winSize().height + 10);
        posVar.x = Director.sharedDirector().winSize().width / 2;
        posVar.y = 0;

        // life of particles
        life = 45;
        lifeVar = 15;

        // size, in pixels
        size = 10.0f;
        sizeVar = 5.0f;

        // emits per second
        emissionRate = 10;

        // color of particles
        startColor.r = 1.0f;
        startColor.g = 1.0f;
        startColor.b = 1.0f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.0f;
        startColorVar.a = 0.0f;
        endColor.r = 1.0f;
        endColor.g = 1.0f;
        endColor.b = 1.0f;
        endColor.a = 0.0f;
        endColorVar.r = 0.0f;
        endColorVar.g = 0.0f;
        endColorVar.b = 0.0f;
        endColorVar.a = 0.0f;

        texture = TextureManager.sharedTextureManager().addImage("fire.png");

        // additive
        blendAdditive = false;
    }

}
