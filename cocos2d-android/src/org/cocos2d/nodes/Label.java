package org.cocos2d.nodes;

import org.cocos2d.opengl.Texture2D;
import org.cocos2d.types.CCSize;

public class Label extends TextureNode implements CocosNode.CocosNodeLabel, CocosNode.CocosNodeSize {

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private CCSize _dimensions;
    private TextAlignment _alignment;
    private String _fontName;
    private float _fontSize;

    public static Label label(String string, String fontname, float fontsize) {
        return new Label(string, 0, 0, TextAlignment.CENTER, fontname, fontsize);
    }

    protected Label(String string, String fontname, float fontsize) {
        this(string, 0, 0, TextAlignment.CENTER, fontname, fontsize);
    }

    public static Label node(String string, float w, float h, TextAlignment alignment, String name, float size) {
        return new Label(string, w, h, alignment, name, size);
    }

    protected Label(String string, float w, float h, TextAlignment alignment, String name, float size) {
        _dimensions = CCSize.make(w, h);
        _alignment = alignment;
        _fontName = name;
        _fontSize = size;

        setString(string);
    }

    public void setString(String string) {
        if (CCSize.equalToSize(_dimensions, CCSize.zero())) {
            setTexture(new Texture2D(string, _fontName, _fontSize));
        } else
            setTexture(new Texture2D(string, _dimensions, _alignment, _fontName, _fontSize));
    }

}
