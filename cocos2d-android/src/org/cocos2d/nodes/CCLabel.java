package org.cocos2d.nodes;

import java.util.HashSet;

import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.types.CGSize;

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
    private boolean _active = false;
    private int _dirtyId = -1;

    private static int _globalDirtyId = 0;
    private static HashSet<CCLabel> _activeLabels = new HashSet<CCLabel>();

    /** creates a CCLabel from a fontname, alignment, dimension and font size */
    public static CCLabel makeLabel(String string, final CGSize dimensions, TextAlignment alignment, 
                                    String fontname, float fontsize) {
        return new CCLabel(string, dimensions, alignment, fontname, fontsize);
    }

    /** creates a CCLabel from a fontname and font size */
    public static CCLabel makeLabel(String string, String fontname, float fontsize) {
        return new CCLabel(string, CGSize.make(0, 0), TextAlignment.CENTER, fontname, fontsize);
    }

    /** initializes the CCLabel with a font name and font size */
    protected CCLabel(String string, String fontname, float fontsize) {
        this(string, CGSize.make(0,0), TextAlignment.CENTER, fontname, fontsize);
    }

    /** initializes the CCLabel with a font name, alignment, dimension and font size */
    protected CCLabel(String string, final CGSize dimensions, TextAlignment alignment,
                        String name, float size) {
    	super();
        _dimensions = dimensions;
        _alignment = alignment;
        _fontName = name;
        _fontSize = size;

        setString(string);
    }

    /** changes the string to render
     * @warning Changing the string is as expensive as creating a new CCLabel.
        To obtain better performance use CCLabelAtlas
     */
    public void setString(String string) {
	if (_string == null || !_string.equals(string))
	{
		_string = string;
		_dirtyId = -1;
		updateTexture();
	}
    }

    private void updateTexture()
    {
	if (_dirtyId == _globalDirtyId)
		return;

	CCTexture2D tex = null;
        if (CGSize.equalToSize(_dimensions, CGSize.getZero())) {
            tex = new CCTexture2D(_string, _fontName, _fontSize);
        } else {
            tex = new CCTexture2D(_string, _dimensions, _alignment, _fontName, _fontSize);
        }
//        CCTextureCache.sharedTextureCache().addTexture(tex);
        setTexture(tex);

	    CGSize size = texture_.getContentSize();
	    setTextureRect(0, 0, size.width, size.height);

	    _dirtyId = _globalDirtyId;
    }

    @Override
    public void onEnter()
    {
	super.onEnter();
	_active = true;
	_activeLabels.add(this);
	updateTexture();
    }

    @Override
    public void onExit()
    {
	super.onExit();
	_activeLabels.remove(this);
	_active = false;
    }

    // this is a temporary solution for white texture problem,
    public static void reloadTextures()
    {
	_globalDirtyId += 1;
	for (CCLabel label : _activeLabels)
	{
		label.updateTexture();
	}
    }

    public String toString() {
        return "CCLabel <" + CCLabel.class.getSimpleName() + " = " + this.hashCode()
                + " | FontName = " + _fontName + ", FontSize = " + _fontSize + ">";
    }

}

