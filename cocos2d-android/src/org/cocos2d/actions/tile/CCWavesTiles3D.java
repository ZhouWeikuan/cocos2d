package org.cocos2d.actions.tile;

import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad3;

////////////////////////////////////////////////////////////

/** CCWavesTiles3D action. */
public class CCWavesTiles3D extends CCTiledGrid3DAction {
	int waves;
/** waves amplitude */
	float amplitude;
/** waves amplitude rate */
	float amplitudeRate;


    /** creates the action with a number of waves, the waves amplitude, the grid size and the duration */
    public static CCWavesTiles3D action(int wav, float amp, ccGridSize gridSize, float d) {
        return new CCWavesTiles3D(wav, amp, gridSize, d);
    }

    /** initializes the action with a number of waves, the waves amplitude, the grid size and the duration */
    public CCWavesTiles3D(int wav, float amp, ccGridSize gridSize, float d) {
        super(gridSize, d);
        waves = wav;
        amplitude = amp;
        amplitudeRate = 1.0f;
    }

    @Override
    public CCWavesTiles3D copy() {
        return new CCWavesTiles3D(waves, amplitude, gridSize, duration);
    }

    @Override
    public void update(float time) {
        int i, j;

        for( i = 0; i < gridSize.x; i++ ) {
            for( j = 0; j < gridSize.y; j++ ) {
                ccQuad3 coords = originalTile(ccGridSize.ccg(i,j));

                coords.bl_z = (float)(Math.sin(time*Math.PI*waves*2 + (coords.bl_y+coords.bl_x) * .01f)
                                * amplitude * amplitudeRate );
                coords.br_z	= coords.bl_z;
                coords.tl_z = coords.bl_z;
                coords.tr_z = coords.bl_z;

                setTile(ccGridSize.ccg(i,j), coords);
            }
        }
    }
}


