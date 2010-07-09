package org.cocos2d.nodes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.cocos2d.opengl.CCTexture2D;
import java.io.IOException;
import java.util.HashMap;


public class TextureManager {
    private HashMap<String, CCTexture2D> textures;

    private static TextureManager _sharedTextureMgr;

    public static TextureManager sharedTextureManager() {
        synchronized (TextureManager.class) {
            if (_sharedTextureMgr == null) {
                _sharedTextureMgr = new TextureManager();
            }
            return _sharedTextureMgr;
        }
    }

    private TextureManager() {
        assert _sharedTextureMgr == null : "Attempted to allocate a second instance of a singleton.";

        synchronized (TextureManager.class) {
            textures = new HashMap<String, CCTexture2D>(10);
        }
    }

    public CCTexture2D addImage(String path) {
        assert path != null : "TextureMgr: path must not be null";

        CCTexture2D tex;

        if ((tex = textures.get(path)) == null) {
            tex = createTextureFromFilePath(path);
            textures.put(path, tex);
        }
        return tex;
    }

    public CCTexture2D addImage(Bitmap image) {
        assert image != null : "TextureMgr: image must not be null";

        CCTexture2D tex;
        String key = image.toString();

        if ((tex = textures.get(key)) == null) {
            tex = createTextureFromBitmap(image);
            textures.put(key, tex);
        }
        return tex;
    }

    public void removeAllTextures() {
        textures.clear();
    }

    void removeTexture(CCTexture2D tex) {
        if (tex == null)
            return;

        textures.values().remove(tex);
    }

    public static CCTexture2D createTextureFromFilePath(String path) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(Director.sharedDirector().getActivity().getAssets().open(path));
            return createTextureFromBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CCTexture2D createTextureFromBitmap(Bitmap bmp) {
        return new CCTexture2D(bmp);
    }

}
