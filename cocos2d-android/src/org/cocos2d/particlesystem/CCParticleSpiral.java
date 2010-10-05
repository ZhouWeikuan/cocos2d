package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleSpiral extends CCQuadParticleSystem {

    public static CCParticleSystem node() {
        return new CCParticleSpiral();
    }

    protected CCParticleSpiral() {
        this(500);
    }

    protected CCParticleSpiral(int p) {
        super(p);

    	// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		emitterMode = kCCParticleModeGravity;
		
		// Gravity Mode: gravity
		this.setGravity(CGPoint.zero());
		
		// Gravity Mode: speed of particles
		speed = 150;
		speedVar = 0;
		
		// Gravity Mode: radial
		radialAccel = -380;
		radialAccelVar = 0;
		
		// Gravity Mode: tagential
		tangentialAccel = 45;
		tangentialAccelVar = 0;
		
		// angle
		angle = 90;
		angleVar = 0;
		
		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		this.setPosition(CGPoint.ccp(winSize.width/2, winSize.height/2));
		posVar = CGPoint.zero();
		
		// life of particles
		life = 12;
		lifeVar = 0;
		
		// size, in pixels
		startSize = 20.0f;
		startSizeVar = 0.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per second
		emissionRate = totalParticles/life;
		
		// color of particles
		startColor.r = 0.5f;
		startColor.g = 0.5f;
		startColor.b = 0.5f;
		startColor.a = 1.0f;
		startColorVar.r = 0.5f;
		startColorVar.g = 0.5f;
		startColorVar.b = 0.5f;
		startColorVar.a = 0.0f;
		endColor.r = 0.5f;
		endColor.g = 0.5f;
		endColor.b = 0.5f;
		endColor.a = 1.0f;
		endColorVar.r = 0.5f;
		endColorVar.g = 0.5f;
		endColorVar.b = 0.5f;
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
