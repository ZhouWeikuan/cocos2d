package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleFire extends CCQuadParticleSystem {

    public static CCParticleFire node() {
        return new CCParticleFire();
    }

    protected CCParticleFire() {
        this(250);
    }

    protected CCParticleFire(int p) {
        super(p);


		// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		this.setGravity(CGPoint.ccp(0,0));
		
		// Gravity Mode: radial acceleration
		setRadialAccel( 0 );
		setRadialAccelVar( 0 );
		
		// Gravity Mode: speed of particles
		setSpeed(60);
		setSpeedVar(20);		
		
		// starting angle
		angle = 90;
		angleVar = 10;
		
		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		this.setPosition(CGPoint.ccp(winSize.width/2, 60));
		posVar = CGPoint.ccp(40, 20);
		
		// life of particles
		life = 3;
		lifeVar = 0.25f;
		
			
		// size, in pixels
		startSize = 54.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per frame
		emissionRate = totalParticles/life;
		
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

		setTexture(CCTextureCache.sharedTextureCache().addImage("fire.png"));

        // additive
        setBlendAdditive(true);
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

