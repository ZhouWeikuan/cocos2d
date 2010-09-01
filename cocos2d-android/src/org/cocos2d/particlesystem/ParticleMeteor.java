package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleMeteor extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleMeteor();
    }

    protected ParticleMeteor() {
        this(150);
    }

    protected ParticleMeteor(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = -200;
        gravity.y = 200;

        // angle
        angle = 90;
        angleVar = 360;

        // speed of particles
        speed = 15;
        speedVar = 5;

        // radial
        radialAccel = 0;
        radialAccelVar = 0;

        // tagential
        tangentialAccel = 0;
        tangentialAccelVar = 0;

        // emitter position
        setPosition(CGPoint.make(160, 240));
        posVar.x = 0;
        posVar.y = 0;

        // life of particles
        life = 2;
        lifeVar = 1;

        // size, in pixels
        size = 60.0f;
        sizeVar = 10.0f;

        // emits per second
        emissionRate = totalParticles / life;

        // color of particles
        startColor.r = 0.2f;
        startColor.g = 0.4f;
        startColor.b = 0.7f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.2f;
        startColorVar.a = 0.1f;
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
// ParticleMeteor
//
@implementation CCParticleMeteor
-(id) init
{
	return [self initWithTotalParticles:150];
}

-(id) initWithTotalParticles:(int) p
{
	if( (self=[super initWithTotalParticles:p]) ) {

		// duration
		duration = kCCParticleDurationInfinity;
		
		// Gravity Mode
		self.emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		self.gravity = ccp(-200,200);

		// Gravity Mode: speed of particles
		self.speed = 15;
		self.speedVar = 5;
		
		// Gravity Mode: radial
		self.radialAccel = 0;
		self.radialAccelVar = 0;
		
		// Gravity Mode: tagential
		self.tangentialAccel = 0;
		self.tangentialAccelVar = 0;
		
		// angle
		angle = 90;
		angleVar = 360;
		
		// emitter position
		CGSize winSize = [[CCDirector sharedDirector] winSize];
		self.position = ccp(winSize.width/2, winSize.height/2);
		posVar = CGPointZero;
		
		// life of particles
		life = 2;
		lifeVar = 1;
		
		// size, in pixels
		startSize = 60.0f;
		startSizeVar = 10.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per second
		emissionRate = totalParticles/life;
		
		// color of particles
		startColor.r = 0.2f;
		startColor.g = 0.4f;
		startColor.b = 0.7f;
		startColor.a = 1.0f;
		startColorVar.r = 0.0f;
		startColorVar.g = 0.0f;
		startColorVar.b = 0.2f;
		startColorVar.a = 0.1f;
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


