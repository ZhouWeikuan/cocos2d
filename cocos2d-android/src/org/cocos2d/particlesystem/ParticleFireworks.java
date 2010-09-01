package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleFireworks extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleFireworks();
    }

    protected ParticleFireworks() {
        this(1500);
    }

    protected ParticleFireworks(int p) {
        super(p);

        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = -90;

        // angle
        angle = 90;
        angleVar = 20;

        // radial
        radialAccel = 0;
        radialAccelVar = 0;

        // speed of particles
        speed = 180;
        speedVar = 50;

        // emitter position
        setPosition(CGPoint.make(160, 160));

        // life of particles
        life = 3.5f;
        lifeVar = 1;

        // emits per frame
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.5f;
        startColor.g = 0.5f;
        startColor.b = 0.5f;
        startColor.a = 1.0f;
        startColorVar.r = 0.5f;
        startColorVar.g = 0.5f;
        startColorVar.b = 0.5f;
        startColorVar.a = 0.1f;
        endColor.r = 0.1f;
        endColor.g = 0.1f;
        endColor.b = 0.1f;
        endColor.a = 0.2f;
        endColorVar.r = 0.1f;
        endColorVar.g = 0.1f;
        endColorVar.b = 0.1f;
        endColorVar.a = 0.2f;

        // size, in pixels
        size = 8.0f;
        sizeVar = 2.0f;

        texture = CCTextureCache.sharedTextureCache().addImage("fire.png");

        // additive
        blendAdditive = false;
    }

}

//
// ParticleFireworks
//
@implementation CCParticleFireworks
-(id) init
{
	return [self initWithTotalParticles:1500];
}

-(id) initWithTotalParticles:(int)p
{
	if( (self=[super initWithTotalParticles:p]) ) {
		// duration
		duration = kCCParticleDurationInfinity;

		// Gravity Mode
		self.emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		self.gravity = ccp(0,-90);
		
		// Gravity Mode:  radial
		self.radialAccel = 0;
		self.radialAccelVar = 0;

		//  Gravity Mode: speed of particles
		self.speed = 180;
		self.speedVar = 50;
		
		// emitter position
		CGSize winSize = [[CCDirector sharedDirector] winSize];
		self.position = ccp(winSize.width/2, winSize.height/2);
		
		// angle
		angle = 90;
		angleVar = 20;
				
		// life of particles
		life = 3.5f;
		lifeVar = 1;
			
		// emits per frame
		emissionRate = totalParticles/life;
		
		// color of particles
		startColor.r = 0.5f;
		startColor.g = 0.5f;
		startColor.b = 0.5f;
		startColor.a = 1.0f;
		startColorVar.r = 0.5f;
		startColorVar.g = 0.5f;
		startColorVar.b = 0.5f;
		startColorVar.a = 0.1f;
		endColor.r = 0.1f;
		endColor.g = 0.1f;
		endColor.b = 0.1f;
		endColor.a = 0.2f;
		endColorVar.r = 0.1f;
		endColorVar.g = 0.1f;
		endColorVar.b = 0.1f;
		endColorVar.a = 0.2f;
		
		// size, in pixels
		startSize = 8.0f;
		startSizeVar = 2.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		self.texture = [[CCTextureCache sharedTextureCache] addImage: @"fire.png"];

		// additive
		self.blendAdditive = NO;
	}
	
	return self;
}
@end


