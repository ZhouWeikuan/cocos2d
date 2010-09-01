package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleFire extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleFire();
    }

    protected ParticleFire() {
        this(250);
    }

    protected ParticleFire(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 10;

        // radial acceleration
        radialAccel = 0;
        radialAccelVar = 0;

        // emitter position
        setPosition(CGPoint.make(160, 60));
        posVar.x = 40;
        posVar.y = 20;

        // life of particles
        life = 3;
        lifeVar = 0.25f;

        // speed of particles
        speed = 60;
        speedVar = 20;

        // size, in pixels
        size = 100.0f;
        sizeVar = 10.0f;

        // emits per frame
        emissionRate = totalParticles / life;

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

        texture = CCTextureCache.sharedTextureCache().addImage("fire.png");

        // additive
        blendAdditive = true;
    }

}

//
// ParticleFire
//
@implementation CCParticleFire
-(id) init
{
	return [self initWithTotalParticles:250];
}

-(id) initWithTotalParticles:(int) p
{
	if( (self=[super initWithTotalParticles:p]) ) {

		// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		self.emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		self.gravity = ccp(0,0);
		
		// Gravity Mode: radial acceleration
		self.radialAccel = 0;
		self.radialAccelVar = 0;
		
		// Gravity Mode: speed of particles
		self.speed = 60;
		self.speedVar = 20;		
		
		// starting angle
		angle = 90;
		angleVar = 10;
		
		// emitter position
		CGSize winSize = [[CCDirector sharedDirector] winSize];
		self.position = ccp(winSize.width/2, 60);
		posVar = ccp(40, 20);
		
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
		
		self.texture = [[CCTextureCache sharedTextureCache] addImage: @"fire.png"];
		
		// additive
		self.blendAdditive = YES;
	}
		
	return self;
}
@end


