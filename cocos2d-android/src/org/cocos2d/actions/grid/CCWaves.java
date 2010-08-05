package org.cocos2d.actions.grid;

import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

////////////////////////////////////////////////////////////

/** CCWaves action */
public class CCWaves extends CCGrid3DAction {
	int		waves;
	/** amplitude */
	float	amplitude;
	/** amplitude rate */	
	float	amplitudeRate;
	boolean	vertical;
	boolean	horizontal;


	/** initializes the action with amplitude, horizontal sin, vertical sin, a grid and duration */
	public static CCWaves action(int wav, float amp, boolean h, boolean v, ccGridSize gridSize, float d) {
		return new CCWaves(wav, amp, h, v, gridSize, d);
	}

	/** creates the action with amplitude, horizontal sin, vertical sin, a grid and duration */
	public CCWaves(int wav, float amp, boolean h, boolean v, ccGridSize gSize, float d) {
		super(gSize, d);
		waves = wav;
		amplitude = amp;
		amplitudeRate = 1.0f;
		horizontal = h;
		vertical = v;
	}

	@Override
	public void update(float time) {
		int i, j;
		
		for( i = 0; i < (gridSize.x+1); i++ ) {
			for( j = 0; j < (gridSize.y+1); j++ ) {
				CCVertex3D	v = originalVertex(ccGridSize.ccg(i,j));
				
				if ( vertical )
					v.x = (float)(v.x + (Math.sin(time*Math.PI*waves*2 + v.y * .01f) * amplitude * amplitudeRate));
				
				if ( horizontal )
					v.y = (float)(v.y + (Math.sin(time*Math.PI*waves*2 + v.x * .01f) * amplitude * amplitudeRate));
						
				setVertex(ccGridSize.ccg(i,j), v);
			}
		}
	}

	@Override
	public CCWaves copy() {
		CCWaves copy = new CCWaves(waves, amplitude, horizontal, vertical, gridSize, duration);
		return copy;
	}

}
