package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleSun extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleSun();
    }

    protected ParticleSun() {
        this(350);
    }

    protected ParticleSun(int p) {
        super(p);

        // additive
        blendAdditive = true;

        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 360;

        // radial acceleration
        radialAccel = 0;
        radialAccelVar = 0;

        // emitter position
        setPosition(160, 240);
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 1;
        lifeVar = 0.5f;

        // speed of particles
        speed = 20;
        speedVar = 5;

        // size, in pixels
        size = 30.0f;
        sizeVar = 10.0f;

        // emits per seconds
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
    }

}
