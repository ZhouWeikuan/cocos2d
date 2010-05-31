package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleMeteor extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleMeteor();
    }

    protected ParticleMeteor() {
        this(150);
    }

    protected ParticleMeteor(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = -200;
        gravity.y = 200;

        // angle
        angle = 90;
        angleVar = 360;

        // speed of particles
        speed = 15;
        speedVar = 5;

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
        life = 2;
        lifeVar = 1;

        // size, in pixels
        size = 60.0f;
        sizeVar = 10.0f;

        // emits per second
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.2f;
        startColor.g = 0.4f;
        startColor.b = 0.7f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.2f;
        startColorVar.a = 0.1f;
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
