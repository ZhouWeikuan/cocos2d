package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

/** CCWaves3D action */
public class CCWaves3D extends CCGrid3DAction {
	int waves;
	/** amplitude of the wave */
	float amplitude;
	/** amplitude rate of the wave */
	float amplitudeRate;
	
	public static CCWaves3D action(int wav, float amp, ccGridSize gSize, float d) {
		return new CCWaves3D(wav, amp, gSize, d);
	}

	public CCWaves3D(int wav, float amp, ccGridSize gSize, float d) {
		super(gSize, d);
		waves = wav;
		amplitude = amp;
		amplitudeRate = 1.0f;		
	}

	@Override
	public CCWaves3D copy() {
		CCWaves3D copy = new CCWaves3D(waves, amplitude, gridSize, duration);
		return copy;
	}

	CCVertex3D	v;
	ccGridSize gs = ccGridSize.ccg(0,0);
	int i, j;
	@Override
	public void update(float time) {
		
		for( i = 0; i < (gridSize.x+1); i++ ) {
			gs.x = i;
			for( j = 0; j < (gridSize.y+1); j++ ) {
				gs.y = j;
				v = originalVertex(gs);
				v.z += (float)(Math.sin(Math.PI*time*waves*2 + (v.y+v.x) * .01f) * amplitude * amplitudeRate);
				setVertex(gs, v);
			}
		}
	}
}

