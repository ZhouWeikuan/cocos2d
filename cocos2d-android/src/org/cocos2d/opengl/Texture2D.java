package org.cocos2d.opengl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import org.cocos2d.nodes.Label;
import org.cocos2d.types.CCAffineTransform;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;
import org.cocos2d.types.CCTexParams;

import javax.microedition.khronos.opengles.GL10;
import static javax.microedition.khronos.opengles.GL10.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Texture2D {
    private static final String LOG_TAG = Texture2D.class.getSimpleName();

    public static final int kMaxTextureSize = 1024;

    /**
     * width in pixels
     */
    public int pixelsWide() {
        return mWidth;
    }

    /**
     * height in pixels
     */
    public int pixelsHigh() {
        return mHeight;
    }

    /**
     * width in pixels
     */
    public float getWidth() {
        return mSize.width;
    }

    /**
     * height in pixels
     */
    public float getHeight() {
        return mSize.height;
    }

    /**
     * texture name
     */
    public int name() {
        return _name;
    }


    /**
     * texture max S
     */
    public float maxS() {
        return _maxS;
    }

    /**
     * texture max T
     */
    public float maxT() {
        return _maxT;
    }

    // TODO: Implement me
    public boolean hasPremultipliedAlpha() {
        return false;
    }
    
    private FloatBuffer mVertices;
    private FloatBuffer mCoordinates;
//    private ShortBuffer mIndices;

    private Bitmap mBitmap;
    private int _name = -1;
    private CCSize mSize;
    private int mWidth, mHeight;
    private Bitmap.Config _format;
    private float _maxS, _maxT;
    private CCTexParams _texParams;


    public Texture2D(Bitmap image) {

        boolean sizeToFit = false;

        CCSize imageSize = CCSize.make(image.getWidth(), image.getHeight());
        CCAffineTransform transform = CCAffineTransform.identity();

        int width = (int) imageSize.width;
        if ((width != 1) && (width & (width - 1)) != 0) {
            int i = 1;
            while ((sizeToFit ? 2 * i : i) < width)
                i *= 2;
            width = i;
        }

        int height = (int) imageSize.height;
        if ((height != 1) && (height & (height - 1)) != 0) {
            int i = 1;
            while ((sizeToFit ? 2 * i : i) < height)
                i *= 2;
            height = i;
        }

        while (width > kMaxTextureSize || height > kMaxTextureSize) {
            width /= 2;
            height /= 2;
            transform.scale(0.5f, 0.5f);
            imageSize.width *= 0.5f;
            imageSize.height *= 0.5f;
        }

        if (imageSize.width != width && imageSize.height != height) {
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    image.hasAlpha() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(image, 0, 0, null);
            image.recycle();
            image = bitmap;
        }
        init(image, imageSize);
    }

    public Texture2D(Bitmap image, CCSize imageSize) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap((int) imageSize.width, (int) imageSize.height, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(image, 0, 0, new Paint());
        image.recycle();

        init(bitmap, imageSize);
    }

    private void init(Bitmap image, CCSize imageSize) {
        mBitmap = image;

        mWidth = image.getWidth();
        mHeight = image.getHeight();
        mSize = imageSize;
        _format = image.getConfig();
        _maxS = mSize.width / (float) mWidth;
        _maxT = mSize.height / (float) mHeight;
        _texParams = _gTexParams;
        ByteBuffer vfb = ByteBuffer.allocateDirect(4 * 3 * 4);
        vfb.order(ByteOrder.nativeOrder());
        mVertices = vfb.asFloatBuffer();

        ByteBuffer tfb = ByteBuffer.allocateDirect(4 * 2 * 4);
        tfb.order(ByteOrder.nativeOrder());
        mCoordinates = tfb.asFloatBuffer();

//        ByteBuffer isb = ByteBuffer.allocateDirect(6 * 2);
//        isb.order(ByteOrder.nativeOrder());
//        mIndices = isb.asShortBuffer();
    }

    public Texture2D(String text, String fontname, float fontSize) {

        this(text, calculateTextSize(text, fontname, fontSize), Label.TextAlignment.CENTER, fontname, fontSize);
    }

    private static CCSize calculateTextSize(String text, String fontname, float fontSize) {
        Typeface typeface = Typeface.create(fontname, Typeface.NORMAL);

        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(fontSize);

        int ascent = (int) Math.ceil(-textPaint.ascent());  // Paint.ascent is negative, so negate it
        int descent = (int) Math.ceil(textPaint.descent());
        int measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));

        return CCSize.make(measuredTextWidth, ascent + descent);
    }

    public Texture2D(String text, CCSize dimensions, Label.TextAlignment alignment, String fontname, float fontSize) {
        Typeface typeface = Typeface.create(fontname, Typeface.NORMAL);

        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(fontSize);

        int ascent = 0;
        int descent = 0;
        int measuredTextWidth = 0;

        ascent = (int) Math.ceil(-textPaint.ascent());  // Paint.ascent is negative, so negate it
        descent = (int) Math.ceil(textPaint.descent());
        measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));

        int textWidth = measuredTextWidth;
        int textHeight = ascent + descent;

        int width = (int) dimensions.width;
        if ((width != 1) && (width & (width - 1)) != 0) {
            int i = 1;
            while (i < width)
                i *= 2;
            width = i;
        }
        int height = (int) dimensions.height;
        if ((height != 1) && (height & (height - 1)) != 0) {
            int i = 1;
            while (i < height)
                i *= 2;
            height = i;
        }

        Bitmap.Config config = Bitmap.Config.ALPHA_8;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        int centerOffsetHeight = ((int) dimensions.height - textHeight) / 2;
        int centerOffsetWidth = ((int) dimensions.width - textWidth) / 2;

        switch (alignment) {
            case LEFT:
                centerOffsetWidth = 0;
                break;
            case CENTER:
                //centerOffsetWidth = (effectiveTextWidth - textWidth) / 2;
                break;
            case RIGHT:
                centerOffsetWidth = (int) dimensions.width - textWidth;
                break;
        }

        canvas.drawText(text,
                centerOffsetWidth,
                ascent + centerOffsetHeight,
                textPaint);

        init(bitmap, dimensions);
    }

    public void loadTexture(GL10 gl) {
        if (_name <= 0) {
            int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);

            _name = textures[0];
            gl.glBindTexture(GL_TEXTURE_2D, _name);

            applyTexParameters(gl);

//            gl.glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

            GLUtils.texImage2D(GL_TEXTURE_2D, 0, mBitmap, 0);
            mBitmap.recycle();
        }
    }

    public void drawAtPoint(GL10 gl, CCPoint point) {
        gl.glEnable(GL_TEXTURE_2D);

        loadTexture(gl);

        float width = (float) mWidth * _maxS;
        float height = (float) mHeight * _maxT;

        float vertices[] = {point.x, point.y, 0.0f,
                width + point.x, point.y, 0.0f,
                point.x, height + point.y, 0.0f,
                width + point.x, height + point.y, 0.0f};

        mVertices.put(vertices);
        mVertices.position(0);

        float coordinates[] = {0.0f, _maxT,
                _maxS, _maxT,
                0.0f, 0.0f,
                _maxS, 0.0f};

        mCoordinates.put(coordinates);
        mCoordinates.position(0);

//        short indices = { 0, 1, 2, 3, 2, 1 };
//        mIndices.put(indices);
//        mIndices.position(0);

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, _name);

        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        gl.glVertexPointer(3, GL_FLOAT, 0, mVertices);
        gl.glTexCoordPointer(2, GL_FLOAT, 0, mCoordinates);
        gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

//        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, mIndices);

        // Clear the vertex and color arrays
        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glDisable(GL_TEXTURE_2D);

    }

    //
    // Use to apply MIN/MAG filter
    //

    private static CCTexParams _gTexParams = new CCTexParams(GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);
    private static CCTexParams _texParamsCopy;

    public static void setTexParameters(CCTexParams texParams) {
        _gTexParams = texParams;
    }

    public static CCTexParams texParameters() {
        return _gTexParams;
    }

    public void applyTexParameters(GL10 gl) {
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, _texParams.minFilter );
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, _texParams.magFilter);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, _texParams.wrapS);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, _texParams.wrapT);
    }

    public static void restoreTexParameters() {
        _gTexParams = _texParamsCopy;
    }

    public static void saveTexParameters() {
        _texParamsCopy = _gTexParams.copy();
    }

    public static void setAliasTexParameters() {
        _gTexParams.magFilter = _gTexParams.minFilter = GL_NEAREST;
    }

    public static void setAntiAliasTexParameters() {
        _gTexParams.magFilter = _gTexParams.minFilter = GL_LINEAR;
    }

}
