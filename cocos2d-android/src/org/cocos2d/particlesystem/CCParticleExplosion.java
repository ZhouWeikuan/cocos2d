package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleExplosion extends CCQuadParticleSystem {

    public static CCParticleSystem node() {
        return new CCParticleExplosion();
    }

    protected CCParticleExplosion() {
        this(700);
    }

    protected CCParticleExplosion(int p) {
        super(p);

    	
		// duration
		duration = 0.1f;
		
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		this.setGravity(CGPoint.ccp(0,0));
		
		// Gravity Mode: speed of particles
		setSpeed(70);
		setSpeedVar(40);
		
		// Gravity Mode: radial
		setRadialAccel( 0 );
		setRadialAccelVar( 0 );
		
		// Gravity Mode: tagential
		setTangentialAccel( 0 );
		setTangentialAccelVar( 0 );
		
		// angle
		angle = 90;
		angleVar = 360;
				
		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		
		this.setPosition(CGPoint.ccp(winSize.width/2, winSize.height/2));
		posVar = CGPoint.zero();
		
		// life of particles
		life = 5.0f;
		lifeVar = 2;
		
		// size, in pixels
		startSize = 15.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per second
		emissionRate = totalParticles/duration;
		
		// color of particles
		startColor.r = 0.7f;
		startColor.g = 0.1f;
		startColor.b = 0.2f;
		startColor.a = 1.0f;
		startColorVar.r = 0.5f;
		startColorVar.g = 0.5f;
		startColorVar.b = 0.5f;
		startColorVar.a = 0.0f;
		endColor.r = 0.5f;
		endColor.g = 0.5f;
		endColor.b = 0.5f;
		endColor.a = 0.0f;
		endColorVar.r = 0.5f;
		endColorVar.g = 0.5f;
		endColorVar.b = 0.5f;
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
