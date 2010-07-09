package org.cocos2d.types;

//! a Point with a vertex point, a tex coord point and a color 4B
class ccV3F_C4B_T2F {
    //! vertices (3F)
    ccVertex3F		vertices;			// 12 bytes
    //	char __padding__[4];

    //! colors (4B)
    ccColor4B		colors;				// 4 bytes
    //	char __padding2__[4];

    // tex coords (2F)
    ccTex2F			texCoords;			// 8 byts
}

