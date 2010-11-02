package org.cocos2d.types;

/** RGB color composed of bytes 3 bytes
@since v0.8
 */
public class ccColor3B {
    public static final int size = 3;

    //ccColor3B predefined colors
    //! White color (255,255,255)
    public static final ccColor3B ccWHITE   = ccc3(255,255,255);
    //! Yellow color (255,255,0)
    public static final ccColor3B ccYELLOW  = ccc3(255,255,0);
    //! Blue color (0,0,255)
    public static final ccColor3B ccBLUE    = ccc3(0,0,255);
    //! Green Color (0,255,0)
    public static final ccColor3B ccGREEN   = ccc3(0,255,0);
    //! Red Color (255,0,0,)
    public static final ccColor3B ccRED     = ccc3(255,0,0);
    //! Magenta Color (255,0,255)
    public static final ccColor3B ccMAGENTA = ccc3(255,0,255);
    //! Black Color (0,0,0)
    public static final ccColor3B ccBLACK   = ccc3(0,0,0);
    //! Orange Color (255,127,0)
    public static final ccColor3B ccORANGE  = ccc3(255,127,0);
    //! Gray Color (166,166,166)
    public static final ccColor3B ccGRAY    = ccc3(166,166,166);


    public int r;
    public int g;
    public int b;

    public ccColor3B(ccColor3B c) {
    	r = c.r;
    	g = c.g;
    	b = c.b;
    }
    
    public ccColor3B(int rr, int gg, int bb) {
        r = rr;
        g = gg;
        b = bb;
    }
    
	public void set(ccColor3B color) {
		r = color.r;
		g = color.g;
		b = color.b;
	}
	
    public byte[] toByteArray() {
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }

    //! helper macro that creates an ccColor3B type
    public static ccColor3B ccc3(final int r, final int g, final int b)
    {
        return new ccColor3B(r, g, b);
    }


    public String toString() {
        return "< r=" + r + ", g=" + g + ", b=" + b + " >";
    }
}

