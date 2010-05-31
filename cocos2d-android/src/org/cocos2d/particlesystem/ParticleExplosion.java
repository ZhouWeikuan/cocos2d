package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleExplosion extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleExplosion();
    }

    protected ParticleExplosion() {
        this(700);
    }

    protected ParticleExplosion(int p) {
        super(p);


        // duration
        duration = 0.1f;

        // gravity
        gravity.x = 0;
        gravity.y = -100;

        // angle
        angle = 90;
        angleVar = 360;

        // speed of particles
        speed = 70;
        speedVar = 40;

        // radial
        radialAccel = 0;
        radialAccelVar = 0;

        // tagential
        tangentialAccel = 0;
        tangentialAccelVar = 0;

        // emitter position
        setPosition(160, 240);
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 5.0f;
        lifeVar = 2;

        // size, in pixels
        size = 15.0f;
        sizeVar = 10.0f;

        // emits per second
        emissionRate = totalParticles / duration;

        // color of particles
        startColor.r = 0.7f;
        startColor.g = 0.1f;
        startColor.b = 0.2f;
        startColor.a = 1.0f;
        startColorVar.r = 0.5f;
        startColorVar.g = 0.5f;
        startColorVar.b = 0.5f;
        startColorVar.a = 0.0f;
        endColor.r = 0.5f;
        endColor.g = 0.5f;
        endColor.b = 0.5f;
        endColor.a = 0.0f;
        endColorVar.r = 0.5f;
        endColorVar.g = 0.5f;
        endColorVar.b = 0.5f;
        endColorVar.a = 0.0f;

        texture = TextureManager.sharedTextureManager().addImage("fire.png");

        // additive
        blendAdditive = false;
    }

}
