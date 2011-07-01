package org.cocos2d.nodes;

import java.io.IOException;
import java.util.HashMap;

import org.cocos2d.opengl.TGA;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccGridSize;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.CCFormatter;

import android.util.Log;

/** CCTileMapAtlas is a subclass of CCAtlasNode.
 
 It knows how to render a map based of tiles.
 The tiles must be in a .PNG format while the map must be a .TGA file.
 
 For more information regarding the format, please see this post:
 http://www.cocos2d-iphone.org/archives/27
 
 All features from CCAtlasNode are valid in CCTileMapAtlas
 
 IMPORTANT:
 This class is deprecated. It is maintained for compatibility reasons only.
 You SHOULD not use this class.
 Instead, use the newer TMX file format: CCTMXTiledMap
 */
public class CCTileMapAtlas extends CCAtlasNode {
    /** TileMap info */
    public TGA.ImageTGA tgaInfo;

    /// x,y to altas dicctionary
    private HashMap<String, Integer> posToAtlasIndex;

    /// numbers of tiles to render
    private int itemsToRender;

    /**
     * TileMap info
     */
    public TGA.ImageTGA tgaInfo() {
        return tgaInfo;
    }

    /** creates a CCTileMap with a tile file (atlas) with a map file and the width and height of each tile.
      The tile file will be loaded using the TextureMgr.
    */
    public static CCTileMapAtlas tilemap(String tile, String map, int w, int h) {
        return new CCTileMapAtlas(tile, map, w, h);
    }

    /** initializes a CCTileMap with a tile file (atlas)
     * with a map file and the width and height of each tile.
     * The file will be loaded using the TextureMgr.
     */
    protected CCTileMapAtlas(String tile, String map, int w, int h) {
        super(tile, w, h, 0);

        loadTGAfile(map);
        calculateItemsToRender();

        // now that itemsToRender is initialized, update texture atlas
        // workaround for super() must be the first statement in constructor
        textureAtlas_.resizeCapacity(itemsToRender);

        posToAtlasIndex = new HashMap<String, Integer>(itemsToRender);
        updateAtlasValues();
        setContentSize(CGSize.make(tgaInfo.width * itemWidth, tgaInfo.height * itemHeight));
    }

    private void calculateItemsToRender() {
        assert tgaInfo != null : "tgaInfo must be non-null";

        itemsToRender = 0;
        for (int x = 0; x < tgaInfo.width; x++) {
            for (int y = 0; y < tgaInfo.height; y++) {
            	int p = x + y * tgaInfo.width;
                ccColor3B value = new ccColor3B(tgaInfo.imageData[p + 0],
                        tgaInfo.imageData[p + 1],
                        tgaInfo.imageData[p + 2]);
                if (value.r != 0)
                    itemsToRender++;
            }
        }
    }

    private void loadTGAfile(String file) {
        assert file != null : "file must be non-null";

//        String path = FileUtils.fullPathFromRelativePath(file);
        try {
            tgaInfo = TGA.load(CCDirector.sharedDirector().getActivity().getAssets().open(file));
        } catch (IOException e) {
        }
    }

    /**
     * returns a tile from position x,y.
     * For the moment only channel R is used
     */
    public ccColor3B tile(ccGridSize pos) {
        assert tgaInfo != null : "tgaInfo must not be null";
        assert pos.x < tgaInfo.width : "Invalid position.x";
        assert pos.y < tgaInfo.height : "Invalid position.y";

        /*
        ccColor3B *ptr = (ccColor3B*) tgaInfo->imageData;
        ccColor3B value = ptr[pos.x + pos.y * tgaInfo->width];
        */

        ccColor3B value = new ccColor3B(tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width]);

        return value;
    }
    
    /**
     * sets a tile at position x,y.
     * For the moment only channel R is used
     */
    public void setTile(ccColor3B tile, ccGridSize pos) {
        assert tgaInfo != null : "tgaInfo must not be null";
        assert posToAtlasIndex != null : "posToAtlasIndex must not be nil";
        assert pos.x < tgaInfo.width : "Invalid position.x";
        assert pos.y < tgaInfo.height : "Invalid position.y";
        assert tile.r != 0 : "R component must be non-zero";

        ccColor3B value = new ccColor3B(tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width]);

        if (value.r == 0) {
            Log.w(null, "Value.r must be non-zero.");
        } else {
            tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width] = (byte) tile.r;
            tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width] = (byte) tile.g;
            tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width] = (byte) tile.b;

            // TODO: this method consumes a lot of memory
            // a tree of something like that shall be implemented
            int num = posToAtlasIndex.get(new CCFormatter().format("%d,%d", pos.x, pos.y));
            updateAtlas(pos, tile, num);
        }
    }
    
    private void updateAtlas(ccGridSize pos, ccColor3B value, int idx) {
        ccQuad2 texCoord = new ccQuad2();
        ccQuad3 vertex = new ccQuad3();
        int x = pos.x;
        int y = pos.y;
        float row = (value.r % itemsPerRow) * texStepX;
        float col = (value.r / itemsPerRow) * texStepY;

        texCoord.bl_x = row;                        // A - x
        texCoord.bl_y = col;                        // A - y
        texCoord.br_x = row + texStepX;                // B - x
        texCoord.br_y = col;                        // B - y
        texCoord.tl_x = row;                        // C - x
        texCoord.tl_y = col + texStepY;                // C - y
        texCoord.tr_x = row + texStepX;                // D - x
        texCoord.tr_y = col + texStepY;                // D - y

        vertex.bl_x = x * itemWidth;                // A - x
        vertex.bl_y = y * itemHeight;                // A - y
        vertex.bl_z = 0.0f;                            // A - z
        vertex.br_x = x * itemWidth + itemWidth;    // B - x
        vertex.br_y = y * itemHeight;                // B - y
        vertex.br_z = 0.0f;                            // B - z
        vertex.tl_x = x * itemWidth;                // C - x
        vertex.tl_y = y * itemHeight + itemHeight;    // C - y
        vertex.tl_z = 0.0f;                            // C - z
        vertex.tr_x = x * itemWidth + itemWidth;    // D - x
        vertex.tr_y = y * itemHeight + itemHeight;    // D - y
        vertex.tr_z = 0.0f;                            // D - z

        textureAtlas_.updateQuad(texCoord, vertex, idx);
    }
    
    public void updateAtlasValues() {
        assert tgaInfo != null : "tgaInfo must be non-nil";

        int total = 0;

        for (int x = 0; x < tgaInfo.width; x++) {
            for (int y = 0; y < tgaInfo.height; y++) {
                if (total < itemsToRender) {
                    ccColor3B value = new ccColor3B(tgaInfo.imageData[x + 0 + y * tgaInfo.width],
                            tgaInfo.imageData[x + 1 + y * tgaInfo.width],
                            tgaInfo.imageData[x + 2 + y * tgaInfo.width]);

                    if (value.r != 0) {
                        updateAtlas(ccGridSize.ccg(x, y), value, total);

                        String key = new CCFormatter().format("%d,%d", x, y);
                        posToAtlasIndex.put(key, total);

                        total++;
                    }
                }
            }
        }
    }


    /**
     * dealloc the map from memory
     */
    public void releaseMap() {
        if (tgaInfo != null)
            TGA.destroy(tgaInfo);
        tgaInfo = null;

        posToAtlasIndex = null;
    }
    
    @Override
    public void finalize()  throws Throwable {
    	if( tgaInfo != null )
    		TGA.destroy(tgaInfo);
    	tgaInfo = null;
    	posToAtlasIndex = null;

		super.finalize();
    }

	@Override
	public ccBlendFunc getBlendFunc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlendFunc(ccBlendFunc blendFunc) {
		// TODO Auto-generated method stub
		
	}

}
