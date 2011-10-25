package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleRain extends CCQuadParticleSystem {

    public static CCParticleSystem node() {
        return new CCParticleRain();
    }

    protected CCParticleRain() {
        this(1000);
    }

    protected CCParticleRain(int p) {
        super(p);

		// duration
		duration = kCCParticleDurationInfinity;
		
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		setGravity(CGPoint.ccp(10,-10));
		
		// Gravity Mode: radial
		setRadialAccel( 0 );
		setRadialAccelVar( 1 );
		
		// Gravity Mode: tagential
		setTangentialAccel( 0 );
		setTangentialAccelVar( 1 );

		// Gravity Mode: speed of particles
		setSpeed( 130 );
		setSpeedVar( 30 );
		
		// angle
		angle = -90;
		angleVar = 5;

        // emitter position
        setPosition(CGPoint.make(CCDirector.sharedDirector().winSize().width / 2,
                CCDirector.sharedDirector().winSize().height));
        posVar.x = CCDirector.sharedDirector().winSize().width / 2;
        posVar.y = 0;

        // life of particles
        life = 4.5f;
        lifeVar = 0;

        // size, in pixels
        size = 4.0f;
        sizeVar = 2.0f;

		// size, in pixels
		startSize = 4.0f;
		startSizeVar = 2.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

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

        setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

        // additive
        setBlendAdditive(false);
    }

	@Override
	public ccBlendFunc getBlendFunc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlendFunc(ccBlendFunc blendFunc) {
		// TODO Auto-generated method stub
		
	}

}

