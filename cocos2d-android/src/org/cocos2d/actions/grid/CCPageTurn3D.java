package org.cocos2d.actions.grid;

import org.cocos2d.config.ccMacros;
import org.cocos2d.types.CCVertex3D;
import org.cocos2d.types.ccGridSize;

/**
 * This action simulates a page turn from the bottom right hand corner of the screen
 * It's not much use by itself but is used by the PageTurnTransition.
 *
 * Based on an original paper by L Hong et al.
 * http://www.parc.com/publication/1638/turning-pages-of-3d-electronic-books.html
 * 
 * @since v0.8.2
 */
public class CCPageTurn3D extends CCGrid3DAction {
	public static CCPageTurn3D action(ccGridSize gSize, float d) {
		return new CCPageTurn3D(gSize, d);
	}
	
    protected CCPageTurn3D(ccGridSize gSize, float d) {
        super(gSize, d);
    }
    
    /*
     * Update each tick
     * Time is the percentage of the way through the duration
     */
    @Override
    public void update(float time) {
        float tt = Math.max(0, time - 0.25f );
        float deltaAy = ( tt * tt * 500);
        float ay = -100 - deltaAy;

        float deltaTheta = - ccMacros.M_PI_2 * (float)Math.sqrt(time) ;
        float theta = /*0.01f*/ + ccMacros.M_PI_2 +deltaTheta;

        float sinTheta = (float)Math.sin(theta);
        float cosTheta = (float)Math.cos(theta);

        for( int i = 0; i <=gridSize.x; i++ ) {
            for( int j = 0; j <= gridSize.y; j++ ) {
                // Get original vertex
                CCVertex3D	p = originalVertex(ccGridSize.ccg(i,j));

                float R = (float)Math.sqrt((p.x*p.x) + ((p.y - ay)*(p.y - ay)));
                float r = R * sinTheta;
                float alpha = (float)Math.asin( p.x / R );
                float beta = alpha / sinTheta;
                float cosBeta = (float)Math.cos( beta );

                // If beta > PI then we've wrapped around the cone
                // Reduce the radius to stop these points interfering with others
                if( beta <= Math.PI)
                {
                    p.x = (float)( r * Math.sin(beta));
                    p.y = ( R + ay - ( r*(1 - cosBeta)*sinTheta));

                    // We scale z here to avoid the animation being
                    // too much bigger than the screen due to perspectve transform
                    p.z = (r * ( 1 - cosBeta ) * cosTheta) / 100;
                }
                else
                {
                    // Force X = 0 to stop wrapped
                    // points
                    p.x = 0;
                    p.y = ( R + ay - ( r*(1 - cosBeta)*sinTheta));
                    p.z = 0.001f;
                }

                // Stop z coord from dropping beneath underlying page in a transition
                // issue #751
                if( p.z<0.9f )
                    p.z = 0.9f;

                // Set new coords
                setVertex(ccGridSize.ccg(i,j), p);

            }
        }
    }

}

