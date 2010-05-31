package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleFire extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleFire();
    }

    protected ParticleFire() {
        this(250);
    }

    protected ParticleFire(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 10;

        // radial acceleration
        radialAccel = 0;
        radialAccelVar = 0;

        // emitter position
        setPosition(160, 60);
        posVar.x = 40;
        posVar.y = 20;

        // life of particles
        life = 3;
        lifeVar = 0.25f;

        // speed of particles
        speed = 60;
        speedVar = 20;

        // size, in pixels
        size = 100.0f;
        sizeVar = 10.0f;

        // emits per frame
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.76f;
        startColor.g = 0.25f;
        startColor.b = 0.12f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.0f;
        startColorVar.a = 0.0f;
        endColor.r = 0.0f;
        endColor.g = 0.0f;
        endColor.b = 0.0f;
        endColor.a = 1.0f;
        endColorVar.r = 0.0f;
        endColorVar.g = 0.0f;
        endColorVar.b = 0.0f;
        endColorVar.a = 0.0f;

        texture = TextureManager.sharedTextureManager().addImage("fire.png");

        // additive
        blendAdditive = true;
    }

}
