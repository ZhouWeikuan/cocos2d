package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleFlower extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleFlower();
    }

    protected ParticleFlower() {
        this(250);
    }

    protected ParticleFlower(int p) {
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
        speed = 80;
        speedVar = 10;

        // radial
        radialAccel = -60;
        radialAccelVar = 0;

        // tagential
        tangentialAccel = 15;
        tangentialAccelVar = 0;

        // emitter position
        setPosition(CGPoint.make(160, 240));
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 4;
        lifeVar = 1;

        // size, in pixels
        size = 30.0f;
        sizeVar = 10.0f;

        // emits per second
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.50f;
        startColor.g = 0.50f;
        startColor.b = 0.50f;
        startColor.a = 1.0f;
        startColorVar.r = 0.5f;
        startColorVar.g = 0.5f;
        startColorVar.b = 0.5f;
        startColorVar.a = 0.5f;
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
// ParticleFlower
//
@implementation CCParticleFlower
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
		
		// Gravity Mode: speed of particles
		self.speed = 80;
		self.speedVar = 10;
		
		// Gravity Mode: radial
		self.radialAccel = -60;
		self.radialAccelVar = 0;
		
		// Gravity Mode: tagential
		self.tangentialAccel = 15;
		self.tangentialAccelVar = 0;

		// angle
		angle = 90;
		angleVar = 360;
		
		// emitter position
		CGSize winSize = [[CCDirector sharedDirector] winSize];
		self.position = ccp(winSize.width/2, winSize.height/2);
		posVar = CGPointZero;
		
		// life of particles
		life = 4;
		lifeVar = 1;
		
		// size, in pixels
		startSize = 30.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per second
		emissionRate = totalParticles/life;
		
		// color of particles
		startColor.r = 0.50f;
		startColor.g = 0.50f;
		startColor.b = 0.50f;
		startColor.a = 1.0f;
		startColorVar.r = 0.5f;
		startColorVar.g = 0.5f;
		startColorVar.b = 0.5f;
		startColorVar.a = 0.5f;
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


