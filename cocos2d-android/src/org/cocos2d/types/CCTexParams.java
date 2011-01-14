package org.cocos2d.types;

public class CCTexParams {
    public int minFilter;
    public int magFilter;
    public int wrapS;
    public int wrapT;

    public CCTexParams(int min, int mag, int s, int t) {
        minFilter = min;
        magFilter = mag;
        wrapS = s;
        wrapT = t;
    }

    public CCTexParams copy() {
        return new CCTexParams(minFilter, magFilter, wrapS, wrapT);
    }

	public void set(int min, int mag, int s, int t) {
        minFilter = min;
        magFilter = mag;
        wrapS = s;
        wrapT = t;
	}
	
	public void set(CCTexParams texParams) {
		set(texParams.minFilter, texParams.magFilter, texParams.wrapS, texParams.wrapT);
	}
}

