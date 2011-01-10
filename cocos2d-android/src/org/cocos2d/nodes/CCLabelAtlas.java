package org.cocos2d.nodes;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.types.util.PoolHolder;
import org.cocos2d.utils.javolution.TextBuilder;

/** CCLabelAtlas is a subclass of CCAtlasNode.
 
 It can be as a replacement of CCLabel since it is MUCH faster.
 
 CCLabelAtlas versus CCLabel:
 - CCLabelAtlas is MUCH faster than CCLabel
 - CCLabelAtlas "characters" have a fixed height and width
 - CCLabelAtlas "characters" can be anything you want since they are taken from an image file
 
 A more flexible class is CCBitmapFontAtlas. It supports variable width characters and it also has a nice editor.
 */
public class CCLabelAtlas extends CCAtlasNode 
	    implements CCLabelProtocol, CCNode.CocosNodeSize {
    /// string to render
    TextBuilder string_;

    /// the first char in the charmap
    char mapStartChar;

    /** creates the CCLabelAtlas with a string,
     * a char map file(the atlas), the width and height of each element
     * and the starting char of the atlas */
    public static CCLabelAtlas label(CharSequence theString, String charmapfile, int w, int h, char c) {
        return new CCLabelAtlas(theString, charmapfile, w, h, c);
    }

    /** initializes the CCLabelAtlas with a string, a char map file(the atlas), the width and height of each element and the starting char of the atlas */
    protected CCLabelAtlas(CharSequence theString, String charmapfile, int w, int h, char c) {
        super(charmapfile, w, h, theString.length());

        string_ = new TextBuilder(theString.length());
        string_.append(theString);
        mapStartChar = c;

        updateAtlasValues();
    }

    @Override
    public void updateAtlasValues() {
        int n = string_.length();

        PoolHolder holder = PoolHolder.getInstance();
        ccQuad2 texCoord = holder.getccQuad2Pool().get(); 
        ccQuad3 vertex   = holder.getccQuad3Pool().get(); 

//        String s = string_;
        for (int i = 0; i < n; i++) {
            int a = string_.charAt(i) - mapStartChar;
            float row = (a % itemsPerRow) * texStepX;
            float col = (a / itemsPerRow) * texStepY;

            texCoord.bl_x = row;                        // A - x
            texCoord.bl_y = col;                        // A - y
            texCoord.br_x = row + texStepX;                // B - x
            texCoord.br_y = col;                        // B - y
            texCoord.tl_x = row;                        // C - x
            texCoord.tl_y = col + texStepY;                // C - y
            texCoord.tr_x = row + texStepX;                // D - x
            texCoord.tr_y = col + texStepY;                // D - y

            vertex.bl_x = i * itemWidth;                // A - x
            vertex.bl_y = 0;                            // A - y
            vertex.bl_z = 0;                            // A - z
            vertex.br_x = i * itemWidth + itemWidth;    // B - x
            vertex.br_y = 0;                            // B - y
            vertex.br_z = 0;                            // B - z
            vertex.tl_x = i * itemWidth;                // C - x
            vertex.tl_y = itemHeight;                    // C - y
            vertex.tl_z = 0;                            // C - z
            vertex.tr_x = i * itemWidth + itemWidth;    // D - x
            vertex.tr_y = itemHeight;                    // D - y
            vertex.tr_z = 0;                            // D - z

            textureAtlas_.updateQuad(texCoord, vertex, i);
        }
        
        holder.getccQuad2Pool().free(texCoord);
        holder.getccQuad3Pool().free(vertex);
    }

    public void setString(CharSequence newString) {
        if (newString.length() > textureAtlas_.getTotalQuads())
            textureAtlas_.resizeCapacity(newString.length());

        string_.reset();
        string_.append(newString);
        updateAtlasValues();

        setContentSize(string_.length() * itemWidth, itemHeight);
    }

    @Override
    public void draw(GL10 gl) {
        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Unneeded states: GL_COLOR_ARRAY
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	    gl.glColor4f(color_.r/255.f, color_.g/255.f, color_.b/255.f, opacity_/255.f);

        boolean newBlend = false;
        if( blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST ) {
            newBlend = true;
            gl.glBlendFunc( blendFunc_.src, blendFunc_.dst );
        }

        textureAtlas_.draw(gl, string_.length());
	
	    if( newBlend )
		    gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
	
	    // Restore Default GL state. Enable GL_COLOR_ARRAY
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        /*
        if (ccConfig.CC_LABELATLAS_DEBUG_DRAW) {
            CGSize s = [self contentSize];
            CGPoint vertices[4]={
                ccp(0,0),ccp(s.width,0),
                ccp(s.width,s.height),ccp(0,s.height),
            };
            ccDrawPoly(vertices, 4, YES);
        } // CC_LABELATLAS_DEBUG_DRAW
        */
    }

    @Override
    public float getWidth() {
        return string_.length() * itemWidth;
    }

    @Override
    public float getHeight() {
        return itemHeight;
    }

	@Override
	public ccBlendFunc getBlendFunc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlendFunc(ccBlendFunc blendFunc) {
		// TODO Auto-generated method stub
		
	}
}

