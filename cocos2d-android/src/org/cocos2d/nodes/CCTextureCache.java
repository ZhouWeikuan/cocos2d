package org.cocos2d.nodes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.cocos2d.opengl.CCTexture2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;


/** Singleton that handles the loading of textures
 * Once the texture is loaded, the next time it will return
 * a reference of the previously loaded texture reducing GPU & CPU memory
 */
public class CCTextureCache {
    private HashMap<String, CCTexture2D> textures;

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
            textures = new HashMap<String, CCTexture2D>(10);
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

        CCTexture2D tex = textures.get(path);

        if (tex == null) {
            tex = createTextureFromFilePath(path);
            textures.put(path, tex);
        }
        return tex;
    }

    /** Returns a Texture2D object given an CGImageRef image
     * If the image was not previously loaded, it will create a new CCTexture2D object and it will return it.
     * Otherwise it will return a reference of a previously loaded image
     * The "key" parameter will be used as the "key" for the cache.
     * If "key" is nil, then a new texture will be created each time.
     * @since v0.8
    */
    public CCTexture2D addImage(Bitmap image) {
        assert (image != null) : "TextureCache: image must not be null";
        String key = image.toString();
        CCTexture2D tex = textures.get(key);
        if (tex == null) {
            tex = createTextureFromBitmap(image);
            textures.put(key, tex);
        }
        return tex;
    }


    /** Purges the dictionary of loaded textures.
     * Call this method if you receive the "Memory Warning"
     * In the short term: it will free some resources preventing your app from being killed
     * In the medium term: it will allocate more resources
     * In the long term: it will be the same
    */
    public void removeAllTextures() {
    	/* Do nothing, or do all.*/
    	for (CCTexture2D tex : textures.values()) {
    		tex.releaseTexture(CCDirector.gl);    		
    	}
    	// textures.clear();
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
    	textures.put(String.valueOf(tex.hashCode()), tex);
    }

    /** Deletes a texture from the cache given a its key name
      @since v0.99.4
      */
    public void removeTexture(String textureKeyName) {
        if (textureKeyName == null)
            return ;
        textures.remove(textureKeyName);
    }

    private static CCTexture2D createTextureFromFilePath(String path) {
        try {
        	InputStream is = CCDirector.sharedDirector().getActivity().getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return  createTextureFromBitmap(bmp);            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static CCTexture2D createTextureFromBitmap(Bitmap bmp) {
        return new CCTexture2D(bmp);
    }

	public void reloadTextures() {
		Set<String> keys = textures.keySet();
		for(String key : keys) {
			CCTexture2D tex = textures.get(key);
			
			tex.setTextureName(key);
		}
	}
}


