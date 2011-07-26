package org.cocos2d.nodes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLResourceHelper.Resource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/** Singleton that handles the loading of textures
 * Once the texture is loaded, the next time it will return
 * a reference of the previously loaded texture reducing GPU & CPU memory
 */
public class CCTextureCache {
    private HashMap<String, WeakReference<CCTexture2D> > textures;

    private static CCTextureCache _sharedTextureCache;

    /** Retruns ths shared instance of the cache */
    public static CCTextureCache sharedTextureCache() {
        synchronized (CCTextureCache.class) {
            if (_sharedTextureCache == null) {
                _sharedTextureCache = new CCTextureCache();
            }
            return _sharedTextureCache;
        }
    }

    /** purges the cache. It releases the retained instance.
     @since v0.99.0
     */
    public static void purgeSharedTextureCache () {
    	if (_sharedTextureCache != null) {
    		_sharedTextureCache.removeAllTextures();
    	}
    }

    private CCTextureCache() {
        assert _sharedTextureCache == null : "Attempted to allocate a second instance of a singleton.";

        synchronized (CCTextureCache.class) {
            textures = new HashMap<String, WeakReference<CCTexture2D> >(10);
        }
    }

    /** Returns a Texture2D object given an file image
     * If the file image was not previously loaded, it will create a new CCTexture2D
     *  object and it will return it. It will use the filename as a key.
     * Otherwise it will return a reference of a previosly loaded image.
     * Supported image extensions: .png, .bmp, .tiff, .jpeg, .pvr, .gif
     */
    public CCTexture2D addImage(String path) {
        assert path != null : "TextureMgr: path must not be null";

        WeakReference<CCTexture2D> texSR = textures.get(path);
        CCTexture2D tex = null;
        if(texSR != null)
        	tex = texSR.get();

        if (tex == null) {
            tex = createTextureFromFilePath(path);
            textures.put(path, new WeakReference<CCTexture2D>(tex));
        }
        return tex;
    }
    
    /**
     * Returns a Texture2D object given an file image from external path.
     */
    public CCTexture2D addImageExternal(String path) {
        assert path != null : "TextureMgr: path must not be null";

        WeakReference<CCTexture2D> texSR = textures.get(path);
        CCTexture2D tex = null;
        if(texSR != null)
        	tex = texSR.get();

        if (tex == null) {
            tex = createTextureFromFilePathExternal(path);
            textures.put( path, new WeakReference<CCTexture2D>(tex) );
        }
        return tex;
    }

    /** Returns a Texture2D object given an CGImageRef image
     * If the image was not previously loaded, it will create a new CCTexture2D object and it will return it.
     * Otherwise it will return a reference of a previously loaded image
     * The "key" parameter will be used as the "key" for the cache.
     * If "key" is nil, then a new texture will be created each time.
     *
     * BE AWARE OF the fact that copy of image is stored in memory,
     * use assets method if you can.
     * @since v0.8
    */
    public CCTexture2D addImage(Bitmap image, String key) {
        assert (image != null) : "TextureCache: image must not be null";

        WeakReference<CCTexture2D> texSR = textures.get(key);
        CCTexture2D tex = null;
        if(texSR != null)
        	tex = texSR.get();
        
    	if( key !=null && tex != null ) {
    		return tex;
    	}
    	
    	final Bitmap copy = image.copy(image.getConfig(), false);
    	
    	if(copy != null) {
	    	CCTexture2D texNew = new CCTexture2D();
	    	texNew.setLoader(new GLResourceHelper.GLResourceLoader() {
				@Override
				public void load(Resource res) {
					Bitmap initImage = copy.copy(copy.getConfig(), false);
					((CCTexture2D)res).initWithImage(initImage);
				}
			});
	    	if( key!= null ) {
	    		textures.put(key, new WeakReference<CCTexture2D>(texNew) );
	    	}
	    	
	    	return texNew;
    	} else {
    		ccMacros.CCLOG("cocos2d", "Couldn't add Bitmap in CCTextureCache");
    		return null;
    	}
    }


    /** Purges the dictionary of loaded textures.
     * Call this method if you receive the "Memory Warning"
     * In the short term: it will free some resources preventing your app from being killed
     * In the medium term: it will allocate more resources
     * In the long term: it will be the same
    */
    public void removeAllTextures() {
    	/* Do nothing, or do all.*/
    	for (WeakReference<CCTexture2D> texSR : textures.values()) {
    		CCTexture2D tex = texSR.get();
    		if(tex != null)
    			tex.releaseTexture(CCDirector.gl);    		
    	}
    	textures.clear();
    }

    /** Removes unused textures
     * Textures that have a retain count of 1 will be deleted
     * It is convinient to call this method after when starting a new Scene
     * @since v0.8
     */
    public void removeUnusedTextures() {
        /*
        NSArray *keys = [textures allKeys];
        for( id key in keys ) {
            id value = [textures objectForKey:key];		
            if( [value retainCount] == 1 ) {
                CCLOG(@"cocos2d: CCTextureCache: removing unused texture: %@", key);
                [textures removeObjectForKey:key];
            }
        }
        */
    }

    /** 
     * Deletes a texture from the cache given a texture
    */
    public void removeTexture(CCTexture2D tex) {
        if (tex == null)
            return;

        textures.values().remove(tex);
    }
    
    /*
     * Add a texture to the cache so it gets managed
     */
    public void addTexture(CCTexture2D tex) {
    	if (tex == null)
    		return;
    	textures.put(String.valueOf(tex.hashCode()), new WeakReference<CCTexture2D>(tex));
    }
    
    public void addTexture(CCTexture2D tex, String name) {
    	if (tex == null)
    		return;
    	textures.put(name, new WeakReference<CCTexture2D>(tex));
    }

    /** Deletes a texture from the cache given a its key name
      @since v0.99.4
      */
    public void removeTexture(String textureKeyName) {
        if (textureKeyName == null)
            return ;
        textures.remove(textureKeyName);
    }

    private static CCTexture2D createTextureFromFilePath(final String path) {
            
    	CCTexture2D tex = new CCTexture2D();
        tex.setLoader(new GLResourceHelper.GLResourceLoader() {
			
			@Override
			public void load(Resource res) {
	            try {
		        	InputStream is = CCDirector.sharedDirector().getActivity().getAssets().open(path);
		        	
		        	BitmapFactory.Options opts = new BitmapFactory.Options();
		        	opts.inPreferredConfig = ((CCTexture2D)res).pixelFormat();
		        	Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
		            
					is.close();

					((CCTexture2D)res).initWithImage(bmp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        return tex;
    }
    
    private static CCTexture2D createTextureFromFilePathExternal(final String path) {
        
    	CCTexture2D tex = new CCTexture2D();
        tex.setLoader(new GLResourceHelper.GLResourceLoader() {
			
			@Override
			public void load(Resource res) {
	            try {
		        	InputStream is = new FileInputStream(path);
		        	
		        	BitmapFactory.Options opts = new BitmapFactory.Options();
		        	opts.inPreferredConfig = ((CCTexture2D)res).pixelFormat();
		            Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
//		            Bitmap bmp = BitmapFactory.decodeStream(is);
					is.close();
					((CCTexture2D)res).initWithImage(bmp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        return tex;
    }
}


