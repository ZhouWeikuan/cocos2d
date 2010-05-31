package org.cocos2d.particlesystem;

import org.cocos2d.nodes.TextureManager;

public class ParticleSmoke extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleSmoke();
    }

    protected ParticleSmoke() {
        this(200);
    }

    protected ParticleSmoke(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 5;

        // radial acceleration
        radialAccel = 0;
        radialAccelVar = 0;

        // emitter position
        setPosition(160, 0);
        posVar.x = 20;
        posVar.y = 0;

        // life of particles
        life = 4;
        lifeVar = 1;

        // speed of particles
        speed = 25;
        speedVar = 10;

        // size, in pixels
        size = 60.0f;
        sizeVar = 10.0f;

        // emits per frame
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.8f;
        startColor.g = 0.8f;
        startColor.b = 0.8f;
        startColor.a = 1.0f;
        startColorVar.r = 0.02f;
        startColorVar.g = 0.02f;
        startColorVar.b = 0.02f;
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
        blendAdditive = false;
    }

}
