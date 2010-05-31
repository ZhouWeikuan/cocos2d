package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleGalaxy extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleGalaxy();
    }

    protected ParticleGalaxy() {
        this(200);
    }

    protected ParticleGalaxy(int p) {
        super(p);

        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 360;

        // speed of particles
        speed = 60;
        speedVar = 10;

        // radial
        radialAccel = -80;
        radialAccelVar = 0;

        // tagential
        tangentialAccel = 80;
        tangentialAccelVar = 0;

        // emitter position
        setPosition(160, 240);
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 4;
        lifeVar = 1;

        // size, in pixels
        size = 37.0f;
        sizeVar = 10.0f;

        // emits per second
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.12f;
        startColor.g = 0.25f;
        startColor.b = 0.76f;
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
