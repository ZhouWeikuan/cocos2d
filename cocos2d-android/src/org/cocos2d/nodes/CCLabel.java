package org.cocos2d.nodes;

import java.lang.ref.WeakReference;

import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.graphics.Typeface;

/** CCLabel is a subclass of CCTextureNode that knows how to render text labels
 *
 * All features from CCTextureNode are valid in CCLabel
 *
 * CCLabel objects are slow. Consider using CCLabelAtlas or CCBitmapFontAtlas instead.
 */

public class CCLabel extends CCSprite implements CCLabelProtocol {

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private CGSize _dimensions;
    private TextAlignment _alignment;
    private String _fontName;
    private float _fontSize;
    private String _string;
    // style for standart fonts
    private int _fontStyle = Typeface.NORMAL;

    /** creates a CCLabel from a fontname, alignment, dimension and font size */
    public static CCLabel makeLabel(String string, final CGSize dimensions, TextAlignment alignment, 
                                    String fontname, float fontsize) {
        return new CCLabel(string, dimensions, alignment, fontname, fontsize);
    }

    /** creates a CCLabel from a fontname, alignment, dimension, font size and font style */
    public static CCLabel makeLabel(String string, final CGSize dimensions, TextAlignment alignment, 
                                    String fontname, float fontsize, int fontStyle) {
        return new CCLabel(string, dimensions, alignment, fontname, fontsize, fontStyle);
    }

    /** creates a CCLabel from a fontname and font size */
    public static CCLabel makeLabel(String string, String fontname, float fontsize) {
        return new CCLabel(string, CGSize.make(0, 0), TextAlignment.CENTER, fontname, fontsize);
    }
    /** creates a CCLabel from a fontname and font size and font style */
    public static CCLabel makeLabel(String string, String fontname, float fontsize, int fontStyle) {
    	return new CCLabel(string, CGSize.make(0, 0), TextAlignment.CENTER, fontname, fontsize, fontStyle);
    }
    
    /** initializes the CCLabel with a font name and font size and style */
    protected CCLabel(CharSequence string, String fontname, float fontsize) {
    	this(string, CGSize.make(0,0), TextAlignment.CENTER, fontname, fontsize);
    }

    /** initializes the CCLabel with a font name and font size */
    protected CCLabel(CharSequence string, String fontname, float fontsize, int fontStyle) {
        this(string, CGSize.make(0,0), TextAlignment.CENTER, fontname, fontsize, fontStyle);
    }

    /** initializes the CCLabel with a font name, alignment, dimension and font size */
    protected CCLabel(CharSequence string, final CGSize dimensions, TextAlignment alignment,
                        String name, float size) {
    	super();
        _dimensions = dimensions;
        _alignment = alignment;
        _fontName = name;
        _fontSize = size;

        setString(string);
    }
    
    protected CCLabel(CharSequence string, final CGSize dimensions, TextAlignment alignment,
            String name, float size, int fontStyle) {
    	this(string, dimensions, alignment, name, size);
    	_fontStyle = fontStyle;
    }

    private static class StringReloader implements GLResourceHelper.GLResourceLoader {
    	
    	private WeakReference<CCLabel> label;
    	
    	public StringReloader(CCLabel holder) {
    		label = new WeakReference<CCLabel>(holder);
		}
    	
    	@Override
		public void load(GLResourceHelper.Resource res) {
    		CCLabel thisp = label.get();
    		if(thisp == null)
    			return;
    		
	    	if (CGSize.equalToSize(thisp._dimensions, CGSize.zero())) {
	    		((CCTexture2D)res).initWithText(thisp._string, thisp._fontName, thisp._fontSize, thisp._fontStyle);
	    	} else {
	    		((CCTexture2D)res).initWithText(thisp._string, thisp._dimensions, thisp._alignment, thisp._fontName, thisp._fontSize, thisp._fontStyle);
	    	}
	        
		    CGSize size = thisp.texture_.getContentSize();
		    thisp.setTextureRect(CGRect.make(0, 0, size.width, size.height));
		}
    }
    
    /** changes the string to render
     * @warning Changing the string is as expensive as creating a new CCLabel.
        To obtain better performance use CCLabelAtlas
     */
    public void setString(CharSequence seq) {   	
    	if(_string != null && _string.equals(seq))
    		return;
    		
    	final String string = seq.toString();
    	_string = string;
    	CCTexture2D texture = new CCTexture2D();
    	setTexture(texture);
    	texture.setLoader(new StringReloader(this));
    }
    
    public String getString() {
    	return _string;
    }
    
    public String toString() {
        return "CCLabel <" + CCLabel.class.getSimpleName() + " = " + this.hashCode()
                + " | FontName = " + _fontName + ", FontSize = " + _fontSize + ">";
    }

}
