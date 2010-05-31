package org.cocos2d.particlesystem;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.TextureManager;

public class ParticleRain extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleRain();
    }

    protected ParticleRain() {
        this(1000);
    }

    protected ParticleRain(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 10;
        gravity.y = -10;

        // angle
        angle = -90;
        angleVar = 5;

        // speed of particles
        speed = 130;
        speedVar = 30;

        // radial
        radialAccel = 0;
        radialAccelVar = 1;

        // tagential
        tangentialAccel = 0;
        tangentialAccelVar = 1;

        // emitter position
        setPosition(Director.sharedDirector().winSize().width / 2,
                Director.sharedDirector().winSize().height);
        posVar.x = Director.sharedDirector().winSize().width / 2;
        posVar.y = 0;

        // life of particles
        life = 4.5f;
        lifeVar = 0;

        // size, in pixels
        size = 4.0f;
        sizeVar = 2.0f;

        // emits per second
        emissionRate = 20;

        // color of particles
        startColor.r = 0.7f;
        startColor.g = 0.8f;
        startColor.b = 1.0f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.0f;
        startColorVar.a = 0.0f;
        endColor.r = 0.7f;
        endColor.g = 0.8f;
        endColor.b = 1.0f;
        endColor.a = 0.5f;
        endColorVar.r = 0.0f;
        endColorVar.g = 0.0f;
        endColorVar.b = 0.0f;
        endColorVar.a = 0.0f;

        texture = TextureManager.sharedTextureManager().addImage("fire.png");

        // additive
        blendAdditive = false;
    }

}
