package org.cocos2d.opengl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.GLResourceHelper.Resource;
import org.cocos2d.types.CGSize;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 RenderTexture is a generic rendering target. To render things into it,
 simply construct a render target, call begin on it, call visit on any cocos
 scenes or objects to render them, and call end. For convienience, render texture
 adds a sprite as it's display child with the results, so you can simply add
 the render texture to your scene and treat it like any other CocosNode.
 There are also functions for saving the render texture to disk in PNG or JPG format.
 
 @since v0.8.1
 */
public class CCRenderTexture extends CCNode {
	public static final int kImageFormatJPG = 0;
	public static final int kImageFormatPNG = 1;


	int			 [] fbo_ = new int[1];
	int			 [] oldFBO_ = new int[1];
	CCTexture2D		texture_;

    /** sprite being used */
	protected CCSprite		sprite_;
	public CCSprite getSprite() {
		return sprite_;
	}

    /** creates a RenderTexture object with width and height */
    public static CCRenderTexture renderTexture(int width, int height) {
        return new CCRenderTexture(width, height);
    }

    /** initializes a RenderTexture object with width and height */
    protected CCRenderTexture(int width, int height) {
        GL11ExtensionPack egl = (GL11ExtensionPack)CCDirector.gl;
		egl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO_, 0);
		// int format = CCNode.kCCTexture2DPixelFormat_RGBA8888;  
		// textures must be power of two squared
		int pow = 8;
		while (pow < width || pow < height) {
			pow*=2;
		}
    
		final int finPow = pow;
		texture_ = new CCTexture2D();
		texture_.setLoader(new GLResourceHelper.GLResourceLoader() {
			@Override
			public void load(Resource res) {
				Bitmap bmp = Bitmap.createBitmap(finPow, finPow, Config.ARGB_8888);
				((CCTexture2D)res).initWithImage(bmp);
			}
		});

    
		// generate FBO
		egl.glGenRenderbuffersOES(1, fbo_, 0);
		egl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo_[0]);
    
		// associate texture with FBO
		egl.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, texture_.name(), 0);
    
		// check if it worked (probably worth doing :) )
		int status = egl.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
		if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
			ccMacros.CCLOG("Render Texture", "Could not attach texture to framebuffer");
			return ;
		}
		sprite_ = CCSprite.sprite(texture_);
//		texture_ = null;
		sprite_.setScaleY(-1);
		addChild(sprite_);
		egl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO_[0]);
    }

    public void finalize() throws Throwable {
    	GL11ExtensionPack egl = (GL11ExtensionPack)CCDirector.gl;
	    egl.glDeleteFramebuffersOES(1, fbo_, 0);

        super.finalize();
    }

    public void begin() {
    	GL10 gl = CCDirector.gl;
        ccMacros.CC_DISABLE_DEFAULT_GL_STATES(gl);
        // Save the current matrix
        gl.glPushMatrix();

        CGSize texSize = texture_.getContentSize();

        // Calculate the adjustment ratios based on the old and new projections
        CGSize size = CCDirector.sharedDirector().displaySize();
        float widthRatio = size.width / texSize.width;
        float heightRatio = size.height / texSize.height;

        // Adjust the orthographic propjection and viewport
        gl.glOrthof((float)-1.0 / widthRatio,  (float)1.0 / widthRatio, (float)-1.0 / heightRatio, (float)1.0 / heightRatio, -1,1);
        gl.glViewport(0, 0, (int)texSize.width, (int)texSize.height);

        gl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO_, 0);
        ((GL11ExtensionPack)gl).glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo_[0]);//Will direct drawing to the frame buffer created above

        ccMacros.CC_ENABLE_DEFAULT_GL_STATES(gl);
    }

    public void end() {
    	GL10 gl = CCDirector.gl;
    	GL11ExtensionPack egl = (GL11ExtensionPack)CCDirector.gl;
        egl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO_[0]);
        // Restore the original matrix and viewport
        gl.glPopMatrix();
        CGSize size = CCDirector.sharedDirector().displaySize();
        gl.glViewport(0, 0, (int)size.width, (int)size.height);

        gl.glColorMask(true, true, true, true);
    }

    /* get buffer as UIImage */
    /*
    public Bitmap getImageFromBuffer() {
        int tx = (int) texture_.getContentSize().getWidth();
        int ty = (int) texture_.getContentSize().getHeight();

        int bitsPerComponent			= 8;
        int bitsPerPixel				= 32;
        int bytesPerPixel				= (bitsPerComponent * 4)/8;
        int bytesPerRow					= bytesPerPixel * tx;
        int myDataLength				= bytesPerRow * ty;

        NSMutableData *buffer	= [[NSMutableData alloc] initWithCapacity:myDataLength];
        NSMutableData *pixels	= [[NSMutableData alloc] initWithCapacity:myDataLength];

        if( ! (buffer && pixels) ) {
            CCLOG(@"cocos2d: CCRenderTexture#getUIImageFromBuffer: not enough memory");
            [buffer release];
            [pixels release];
            return nil;
        }

        this.begin();
        glReadPixels(0,0,tx,ty,GL_RGBA,GL_UNSIGNED_BYTE, [buffer mutableBytes]);
        this.end();

        // make data provider with data.

        CGBitmapInfo bitmapInfo			= kCGImageAlphaPremultipliedLast | kCGBitmapByteOrderDefault;
        CGDataProviderRef provider		= CGDataProviderCreateWithData(NULL, [buffer mutableBytes], myDataLength, NULL);
        CGColorSpaceRef colorSpaceRef	= CGColorSpaceCreateDeviceRGB();
        CGImageRef iref					= CGImageCreate(tx, ty,
                bitsPerComponent, bitsPerPixel, bytesPerRow,
                colorSpaceRef, bitmapInfo, provider,
                NULL, false,
                kCGRenderingIntentDefault);
                */
        /* Create a bitmap context. The context draws into a bitmap which is `width'
           pixels wide and `height' pixels high. The number of components for each
           pixel is specified by `colorspace', which may also specify a destination
           color profile. The number of bits for each component of a pixel is
           specified by `bitsPerComponent'. The number of bytes per pixel is equal
           to `(bitsPerComponent * number of components + 7)/8'. Each row of the
           bitmap consists of `bytesPerRow' bytes, which must be at least `width *
           bytes per pixel' bytes; in addition, `bytesPerRow' must be an integer
           multiple of the number of bytes per pixel. `data' points a block of
           memory at least `bytesPerRow * height' bytes. `bitmapInfo' specifies
           whether the bitmap should contain an alpha channel and how it's to be
           generated, along with whether the components are floating-point or
           integer.

           CGContextRef CGBitmapContextCreate(void *data, size_t width,
           size_t height, size_t bitsPerComponent, size_t bytesPerRow,
           CGColorSpaceRef colorspace, CGBitmapInfo bitmapInfo)
           */
    /*
        CGContextRef context			= CGBitmapContextCreate([pixels mutableBytes], tx,
                ty, CGImageGetBitsPerComponent(iref), CGImageGetBytesPerRow(iref),
                CGImageGetColorSpace(iref), bitmapInfo);
        CGContextTranslateCTM(context, 0.0f, ty);
        CGContextScaleCTM(context, 1.0f, -1.0f);
        CGContextDrawImage(context, CGRectMake(0.0f, 0.0f, tx, ty), iref);   
        CGImageRef outputRef			= CGBitmapContextCreateImage(context);
        UIImage* image					= [[UIImage alloc] initWithCGImage:outputRef];

        CGImageRelease(iref);
        CGContextRelease(context);
        CGColorSpaceRelease(colorSpaceRef);
        CGDataProviderRelease(provider);
        CGImageRelease(outputRef);

        [pixels release];
        [buffer release];

        return [image autorelease];
    }*/

    /** saves the texture into a file */
    /*
    public boolean saveBuffer(String name) {
        UIImage *myImage				= [self getUIImageFromBuffer];

        NSArray *paths					= NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory	= [paths objectAtIndex:0];
        NSString *fullPath				= [documentsDirectory stringByAppendingPathComponent:fileName];

        NSData *data;

        if (format == kImageFormatPNG)
            data = UIImagePNGRepresentation(myImage);
        else
            data = UIImageJPEGRepresentation(myImage, 1.0f);

        return [data writeToFile:fullPath atomically:YES];
    }*/

    /** saves the texture into a file. The format can be JPG or PNG */
    /*
    public boolean saveBuffer(String name, int format) {
	    return [self saveBuffer:name format:kImageFormatJPG];
    }*/

    /** clears the texture with a color */
    public void clear(float r, float g, float b, float a) {
        begin();
        GL10 gl = CCDirector.gl;
        gl.glClearColor(r, g, b, a);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glColorMask(true, true, true, false);
        end();
    }

}

