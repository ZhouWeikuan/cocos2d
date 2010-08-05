package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCLiquid action */
public class CCLiquid extends CCGrid3DAction {
	int waves;
	/** amplitude */
	float amplitude;
	/** amplitude rate */
	float amplitudeRate;
	
	/** creates the action with amplitude, a grid and duration */
	public static CCLiquid action(int wav, float amp, ccGridSize gridSize, float d) {
		return new CCLiquid(wav, amp, gridSize, d);
	}

	/** initializes the action with amplitude, a grid and duration */
	public CCLiquid(int wav, float amp, ccGridSize gSize, float d) {
		super(gSize, d);
		waves = wav;
		amplitude = amp;
		amplitudeRate = 1.0f;
	}

	@Override
	public void update(float time) {
		int i, j;
		
		for( i = 1; i < gridSize.x; i++ ) {
			for( j = 1; j < gridSize.y; j++ ) {
				CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
				v.x = (float)(v.x + (Math.sin(time*Math.PI*waves*2 + v.x * .01f) * amplitude * amplitudeRate));
				v.y = (float)(v.y + (Math.sin(time*Math.PI*waves*2 + v.y * .01f) * amplitude * amplitudeRate));
				setVertex(ccGridSize.ccg(i,j), v);
			}
		}
	}	

	@Override
	public CCLiquid copy() {
		CCLiquid copy = new CCLiquid(waves, amplitude, gridSize, duration);
		return copy;
	}
}
