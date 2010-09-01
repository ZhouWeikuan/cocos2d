package org.cocos2d.particlesystem;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

public class ParticleSnow extends ParticleSystem {

    public static ParticleSystem node() {
        return new ParticleSnow();
    }

    protected ParticleSnow() {
        this(700);
    }

    protected ParticleSnow(int p) {
        super(p);


        // duration
        duration = -1;

        // gravity
        gravity.x = 0;
        gravity.y = -1;

        // angle
        angle = -90;
        angleVar = 5;

        // speed of particles
        speed = 5;
        speedVar = 1;

        // radial
        radialAccel = 0;
        radialAccelVar = 1;

        // tagential
        tangentialAccel = 0;
        tangentialAccelVar = 1;

        // emitter position
        setPosition(CGPoint.make(CCDirector.sharedDirector().winSize().width / 2,
                CCDirector.sharedDirector().winSize().height + 10));
        posVar.x = CCDirector.sharedDirector().winSize().width / 2;
        posVar.y = 0;

        // life of particles
        life = 45;
        lifeVar = 15;

        // size, in pixels
        size = 10.0f;
        sizeVar = 5.0f;

        // emits per second
        emissionRate = 10;

        // color of particles
        startColor.r = 1.0f;
        startColor.g = 1.0f;
        startColor.b = 1.0f;
        startColor.a = 1.0f;
        startColorVar.r = 0.0f;
        startColorVar.g = 0.0f;
        startColorVar.b = 0.0f;
        startColorVar.a = 0.0f;
        endColor.r = 1.0f;
        endColor.g = 1.0f;
        endColor.b = 1.0f;
        endColor.a = 0.0f;
        endColorVar.r = 0.0f;
        endColorVar.g = 0.0f;
        endColorVar.b = 0.0f;
        endColorVar.a = 0.0f;

        texture = CCTextureCache.sharedTextureCache().addImage("fire.png");

        // additive
        blendAdditive = false;
    }

}


@implementation CCParticleSnow
-(id) init
{
	return [self initWithTotalParticles:700];
}

-(id) initWithTotalParticles:(int)p
{
	if( (self=[super initWithTotalParticles:p]) ) {
	
		// duration
		duration = kCCParticleDurationInfinity;
		
		// set gravity mode.
		self.emitterMode = kCCParticleModeGravity;

		// Gravity Mode: gravity
		self.gravity = ccp(0,-1);
		
		// Gravity Mode: speed of particles
		self.speed = 5;
		self.speedVar = 1;
		
		// Gravity Mode: radial
		self.radialAccel = 0;
		self.radialAccelVar = 1;
		
		// Gravity mode: tagential
		self.tangentialAccel = 0;
		self.tangentialAccelVar = 1;
		
		// emitter position
		self.position = (CGPoint) {
			[[CCDirector sharedDirector] winSize].width / 2,
			[[CCDirector sharedDirector] winSize].height + 10
		};
		posVar = ccp( [[CCDirector sharedDirector] winSize].width / 2, 0 );
		
		// angle
		angle = -90;
		angleVar = 5;

		// life of particles
		life = 45;
		lifeVar = 15;
		
		// size, in pixels
		startSize = 10.0f;
		startSizeVar = 5.0f;
		endSize = kCCParticleStartSizeEqualToEndSize;

		// emits per second
		emissionRate = 10;
		
		// color of particles
		startColor.r = 1.0f;
		startColor.g = 1.0f;
		startColor.b = 1.0f;
		startColor.a = 1.0f;
		startColorVar.r = 0.0f;
		startColorVar.g = 0.0f;
		startColorVar.b = 0.0f;
		startColorVar.a = 0.0f;
		endColor.r = 1.0f;
		endColor.g = 1.0f;
		endColor.b = 1.0f;
		endColor.a = 0.0f;
		endColorVar.r = 0.0f;
		endColorVar.g = 0.0f;
		endColorVar.b = 0.0f;
		endColorVar.a = 0.0f;
		
		self.texture = [[CCTextureCache sharedTextureCache] addImage: @"fire.png"];
		
		// additive
		self.blendAdditive = NO;
	}
		
	return self;
}
@end

