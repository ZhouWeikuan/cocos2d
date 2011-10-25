package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleGalaxy extends CCQuadParticleSystem {

    public static CCParticleSystem node() {
        return new CCParticleGalaxy();
    }

    protected CCParticleGalaxy() {
        this(200);
    }

    protected CCParticleGalaxy(int p) {
        super(p);

		// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		this.setGravity(CGPoint.ccp(0,0));
		
		// Gravity Mode: speed of particles
		setSpeed( 60 );
		setSpeedVar( 10 );
			
		// Gravity Mode: radial
		setRadialAccel( -80 );
		setRadialAccelVar( 0 );
		
		// Gravity Mode: tagential
		setTangentialAccel( 80 );
		setTangentialAccelVar( 0 );
		
		// angle
		angle = 90;
		angleVar = 360;
		
		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		this.setPosition(CGPoint.ccp(winSize.width/2, winSize.height/2));
		posVar = CGPoint.zero();
		
		// life of particles
		life = 4;
		lifeVar = 1;
		
		// size, in pixels
		startSize = 37.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;
		
		// emits per second
		emissionRate = totalParticles/life;
		
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

