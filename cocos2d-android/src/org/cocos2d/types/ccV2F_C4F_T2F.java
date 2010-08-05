package org.cocos2d.types;

//! a Point with a vertex point, a tex coord point and a color 4F
public class ccV2F_C4F_T2F {
    //! vertices (2F)
    public CGPoint			vertices;
    //! colors (4F)
    public ccColor4F		colors;
    //! tex coords (2F)
    public ccTex2F			texCoords;
    
    public ccV2F_C4F_T2F() {
    	vertices = CGPoint.zero();
    	colors = new ccColor4F();
    	texCoords = new ccTex2F(0, 0);
    }
}

