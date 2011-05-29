package org.cocos2d.layers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.FastFloatBuffer;

//
// CCColorLayer
//
/** CCColorLayer is a subclass of CCLayer that implements the CCRGBAProtocol protocol.
 
 All features from CCLayer are valid, plus the following new features:
 - opacity
 - RGB colors
 */
public class CCColorLayer extends CCLayer 
        implements CCRGBAProtocol, CCNode.CocosNodeSize {
    /** Opacity: conforms to CCRGBAProtocol protocol */
    protected ccColor3B color_;
    /** Opacity: conforms to CCRGBAProtocol protocol */
    protected int opacity_;
    /** BlendFunction. Conforms to CCBlendProtocol protocol */
	protected ccBlendFunc	blendFunc_;

    private FastFloatBuffer squareVertices_;
    private FastFloatBuffer squareColors_;

    /** creates a CCLayer with color. Width and height are the window size. */
    public static CCColorLayer node(ccColor4B color) {
        CGSize size = CCDirector.sharedDirector().winSize();
        return new CCColorLayer(color, size.width, size.height);
    }

    /** creates a CCLayer with color, width and height */
    public static CCColorLayer node(ccColor4B color, float w, float h) {
        return new CCColorLayer(color, w, h);
    }

    /** initializes a CCLayer with color. Width and height are the window size. */
    protected CCColorLayer(ccColor4B color) {
        CGSize s = CCDirector.sharedDirector().winSize();
        init(color, s.width, s.height);
    }

    /** initializes a CCLayer with color, width and height */
    protected CCColorLayer(ccColor4B color, float w, float h) {
    	init(color, w, h);
    }
    
    protected void init(ccColor4B color, float w, float h) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        squareVertices_ = FastFloatBuffer.createBuffer(vbb);

        ByteBuffer sbb = ByteBuffer.allocateDirect(4 * 4 * 4);
        sbb.order(ByteOrder.nativeOrder());
        squareColors_ = FastFloatBuffer.createBuffer(sbb);

        color_ = new ccColor3B(color.r, color.g, color.b);
        opacity_ = color.a;
		blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        for (int i = 0; i < (4 * 2); i++) {
            squareVertices_.put(i, 0);
        }
        squareVertices_.position(0);

        updateColor();
        setContentSize(CGSize.make(w, h));
    }

    private void updateColor() {
        for (int i = 0; i < squareColors_.limit(); i++) {
            switch (i % 4) {
                case 0:
                    squareColors_.put(i, color_.r / 255f);
                    break;
                case 1:
                    squareColors_.put(i, color_.g / 255f);
                    break;
                case 2:
                    squareColors_.put(i, color_.b / 255f);
                    break;
                default:
                    squareColors_.put(i, opacity_ / 255f);
            }
            squareColors_.position(0);
        }
    }

    @Override
    public void draw(GL10 gl) {
        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, GL_COLOR_ARRAY
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices_.bytes);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, squareColors_.bytes);

        boolean newBlend = false;
        if (blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST) {
            newBlend = true;
            gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
        } else if (opacity_ != 255) {
            newBlend = true;
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (newBlend)
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        // restore default GL state
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    public ccColor3B getColor() {
        return ccColor3B.ccc3(color_.r, color_.g, color_.b);
    }

    // Color Protocol
    public void setColor(ccColor3B color) {
        color_ = ccColor3B.ccc3(color.r, color.g, color.b);
        updateColor();
    }

    // Opacity Protocol
    public void setOpacity(int o) {
        opacity_ = o;
        updateColor();
    }

    public int getOpacity() {
        return opacity_;
    }

    // Size protocol

    @Override
    public float getWidth() {
        return squareVertices_.get(2);
    }

    @Override
    public float getHeight() {
        return squareVertices_.get(5);
    }

    @Override
    public void setContentSize(CGSize size) {

        // Layer default ctor calls setContentSize priot to nio alloc
        if (squareVertices_ != null) {
            squareVertices_.put(2, size.width);
            squareVertices_.put(5, size.height);
            squareVertices_.put(6, size.width);
            squareVertices_.put(7, size.height);
        }

        super.setContentSize(size);
    }

    /** change width and height 
     * @since v0.8 */
    public void changeWidthAndHeight(float w, float h) {
        setContentSize(CGSize.make(w, h));
    }

    /** change width */
    public void changeWidth(float w) {
        setContentSize(CGSize.make(w, getHeight()));
    }

    /** change height */
    public void changeHeight(float h) {
        setContentSize(CGSize.make(getWidth(), h));
    }

	@Override
	public boolean doesOpacityModifyRGB() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOpacityModifyRGB(boolean b) {
		// TODO Auto-generated method stub
		
	}
}

