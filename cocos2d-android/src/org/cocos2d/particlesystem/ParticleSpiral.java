package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleSpiral extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleSpiral();
    }

    protected ParticleSpiral() {
        this(500);
    }

    protected ParticleSpiral(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = 0;

        // angle
        angle = 90;
        angleVar = 0;

        // speed of particles
        speed = 150;
        speedVar = 0;

        // radial
        radialAccel = -380;
        radialAccelVar = 0;

        // tagential
        tangentialAccel = 45;
        tangentialAccelVar = 0;

        // emitter position
        setPosition(CGPoint.make(160, 240));
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 12;
        lifeVar = 0;

        // size, in pixels
        size = 20.0f;
        sizeVar = 0.0f;

        // emits per second
        emissionRate = totalParticles / life;

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

}

//
// ParticleSpiral
//
@implementation CCParticleSpiral
-(id) init
{
	return [self initWithTotalParticles:500];
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
		self.speed = 150;
		self.speedVar = 0;
		
		// Gravity Mode: radial
		self.radialAccel = -380;
		self.radialAccelVar = 0;
		
		// Gravity Mode: tagential
		self.tangentialAccel = 45;
		self.tangentialAccelVar = 0;
		
		// angle
		angle = 90;
		angleVar = 0;
		
		// emitter position
		CGSize winSize = [[CCDirector sharedDirector] winSize];
		self.position = ccp(winSize.width/2, winSize.height/2);
		posVar = CGPointZero;
		
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
		
		self.texture = [[CCTextureCache sharedTextureCache] addImage: @"fire.png"];

		// additive
		self.blendAdditive = NO;
	}
	
	return self;
}
@end


