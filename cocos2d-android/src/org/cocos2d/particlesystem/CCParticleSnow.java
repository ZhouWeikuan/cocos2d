package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleSnow extends CCQuadParticleSystem {

    public static CCParticleSystem node() {
        return new CCParticleSnow();
    }

    protected CCParticleSnow() {
        this(700);
    }

    protected CCParticleSnow(int p) {
        super(p);

    	
		// duration
		duration = kCCParticleDurationInfinity;
		
		// set gravity mode.
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		this.setGravity(CGPoint.ccp(0,-1));
		
		// Gravity Mode: speed of particles
		speed = 5;
		speedVar = 1;
		
		// Gravity Mode: radial
		radialAccel = 0;
		radialAccelVar = 1;
		
		// Gravity mode: tagential
		tangentialAccel = 0;
		tangentialAccelVar = 1;
		
		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		this.setPosition(CGPoint.ccp(winSize.width/2, winSize.height + 10));

		posVar = CGPoint.ccp(winSize.width / 2, 0 );
		
		// angle
		angle = -90;
		angleVar = 5;

		// life of particles
		life = 45;
		lifeVar = 15;
		
		// size, in pixels
		startSize = 10.0f;
		startSizeVar = 5.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

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
		
        texture = CCTextureCache.sharedTextureCache().addImage("fire.png");

        // additive
        blendAdditive = false;
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

