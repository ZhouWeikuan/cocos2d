package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;

public class CCParticleSun extends CCQuadParticleSystem {

	public static CCParticleSun node() {
		return new CCParticleSun();
	}
	
	public static CCParticleSun node(int p) {
		return new CCParticleSun(p);
	}

	protected CCParticleSun() {
		this(350);
	}

	protected CCParticleSun(int p) {
		super(p);
		// additive
		setBlendAdditive(true);

		// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		this.setGravity(CGPoint.ccp(0,0));

		// Gravity mode: radial acceleration
		setRadialAccel( 0 );
		setRadialAccelVar( 0 );

		// Gravity mode: speed of particles
		setSpeed( 20 );
		setSpeedVar( 5 );

		// angle
		angle = 90;
		angleVar = 360;

		// emitter position
		CGSize winSize = CCDirector.sharedDirector().winSize();
		this.setPosition(CGPoint.ccp(winSize.width/2, winSize.height/2));
		posVar = CGPoint.zero();

		// life of particles
		life = 1;
		lifeVar = 0.5f;

		// size, in pixels
		startSize = 30.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per seconds
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
