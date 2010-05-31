package org.cocos2d.nodes;

import android.util.Log;
import org.cocos2d.opengl.TGA;
import org.cocos2d.types.*;
import org.cocos2d.utils.CCFormatter;

import java.io.IOException;
import java.util.HashMap;

/**
 * TileMapAtlas is a subclass of AtlasNode.
 * <p/>
 * It knows how to render a map based of tiles.
 * The tiles must be in a .PNG format while the map must be a .TGA file.
 * <p/>
 * For more information regarding the format, please see this post:
 * http://blog.sapusmedia.com/2008/12/how-to-use-tilemap-editor-for-cocos2d.html
 * <p/>
 * All features from AtlasNode are valid in TileMapAtlas
 */
public class TileMapAtlas extends AtlasNode {
    /// info about the map file
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


    public static TileMapAtlas tilemap(String tile, String map, int w, int h) {
        return new TileMapAtlas(tile, map, w, h);
    }
    /**
     * initializes the TileMap with a tile file (atlas) with a map file and the width and height of each tile
     */
    protected TileMapAtlas(String tile, String map, int w, int h) {
        super(tile, w, h, 0);

        loadTGAfile(map);
        calculateItemsToRender();

        // now that itemsToRender is initialized, update texture atlas
        // workaround for super() must be the first statement in constructor
        textureAtlas_.resizeCapacity(itemsToRender);

        posToAtlasIndex = new HashMap<String, Integer>(itemsToRender);

        updateAtlasValues();

        setContentSize(tgaInfo.width * itemWidth, tgaInfo.height * itemHeight);

    }

    private void calculateItemsToRender() {
        assert tgaInfo != null : "tgaInfo must be non-null";

        itemsToRender = 0;
        for (int x = 0; x < tgaInfo.width; x++) {
            for (int y = 0; y < tgaInfo.height; y++) {
                CCRGBB value = new CCRGBB(tgaInfo.imageData[x + 0 + y * tgaInfo.width],
                        tgaInfo.imageData[x + 1 + y * tgaInfo.width],
                        tgaInfo.imageData[x + 2 + y * tgaInfo.width]);
                if (value.r != 0)
                    itemsToRender++;
            }
        }
    }

    private void loadTGAfile(String file) {
        assert file != null : "file must be non-null";

//        String path = FileUtils.fullPathFromRelativePath(file);
        try {
            tgaInfo = TGA.load(Director.sharedDirector().getActivity().getAssets().open(file));
        } catch (IOException e) {
        }
    }

    /**
     * returns a tile from position x,y.
     * For the moment only channel R is used
     */
    public CCRGBB tile(CCGridSize pos) {
        assert tgaInfo != null : "tgaInfo must not be null";
        assert pos.x < tgaInfo.width : "Invalid position.x";
        assert pos.y < tgaInfo.height : "Invalid position.y";

        CCRGBB value = new CCRGBB(tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width]);

        return value;
    }

    /**
     * sets a tile at position x,y.
     * For the moment only channel R is used
     */
    public void setTile(CCRGBB tile, CCGridSize pos) {
        assert tgaInfo != null : "tgaInfo must not be null";
        assert posToAtlasIndex != null : "posToAtlasIndex must not be nil";
        assert pos.x < tgaInfo.width : "Invalid position.x";
        assert pos.y < tgaInfo.height : "Invalid position.y";
        assert tile.r != 0 : "R component must be non-zero";

        CCRGBB value = new CCRGBB(tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width],
                tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width]);

        if (value.r == 0) {
            Log.w(null, "Value.r must be non-zero.");
        } else {
            tgaInfo.imageData[pos.x + 0 + pos.y * tgaInfo.width] = tile.r;
            tgaInfo.imageData[pos.x + 1 + pos.y * tgaInfo.width] = tile.g;
            tgaInfo.imageData[pos.x + 2 + pos.y * tgaInfo.width] = tile.b;

            // TODO: this method consumes a lot of memory
            // a tree of something like that shall be implemented
            int num = posToAtlasIndex.get(new CCFormatter().format("%d,%d", pos.x, pos.y));
            updateAtlas(pos, tile, num);
        }
    }

    private void updateAtlas(CCGridSize pos, CCRGBB value, int idx) {
        CCQuad2 texCoord = new CCQuad2();
        CCQuad3 vertex = new CCQuad3();
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
                    CCRGBB value = new CCRGBB(tgaInfo.imageData[x + 0 + y * tgaInfo.width],
                            tgaInfo.imageData[x + 1 + y * tgaInfo.width],
                            tgaInfo.imageData[x + 2 + y * tgaInfo.width]);

                    if (value.r != 0) {
                        updateAtlas(CCGridSize.ccg(x, y), value, total);

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

}
