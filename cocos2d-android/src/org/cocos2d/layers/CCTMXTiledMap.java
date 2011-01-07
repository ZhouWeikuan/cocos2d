package org.cocos2d.layers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.CCFormatter;

/*
 * TMX Tiled Map support:
 * http://www.mapeditor.org
 *
 */

/** CCTMXTiledMap knows how to parse and render a TMX map.

 It adds support for the TMX tiled map format used by http://www.mapeditor.org
 It supports isometric, hexagonal and orthogonal tiles.
 It also supports object groups, objects, and properties.

 Features:
 - Each tile will be treated as an CCSprite
 - The sprites are created on demand. They will be created only when you call "[layer tileAt:]"
 - Each tile can be rotated / moved / scaled / tinted / "opacitied", since each tile is a CCSprite
 - Tiles can be added/removed in runtime
 - The z-order of the tiles can be modified in runtime
 - Each tile has an anchorPoint of (0,0)
 - The anchorPoint of the TMXTileMap is (0,0)
 - The TMX layers will be added as a child
 - The TMX layers will be aliased by default
 - The tileset image will be loaded using the CCTextureCache
 - Each tile will have a unique tag
 - Each tile will have a unique z value. top-left: z=1, bottom-right: z=max z
 - Each object group will be treated as an NSMutableArray
 - Object class which will contain all the properties in a dictionary
 - Properties can be assigned to the Map, Layer, Object Group, and Object

 Limitations:
 - It only supports one tileset per layer.
 - Embeded images are not supported
 - It only supports the XML format (the JSON format is not supported)

 Technical description:
   Each layer is created using an CCTMXLayer (subclass of CCSpriteSheet). If you have 5 layers, then 5 CCTMXLayer will be created,
   unless the layer visibility is off. In that case, the layer won't be created at all.
   You can obtain the layers (CCTMXLayer objects) at runtime by:
  - [map getChildByTag: tag_number];  // 0=1st layer, 1=2nd layer, 2=3rd layer, etc...
  - [map layerNamed: name_of_the_layer];

   Each object group is created using a CCTMXObjectGroup which is a subclass of NSMutableArray.
   You can obtain the object groups at runtime by:
   - [map objectGroupNamed: name_of_the_object_group];

   Each object is a CCTMXObject.

   Each property is stored as a key-value pair in an NSMutableDictionary.
   You can obtain the properties at runtime by:

		[map propertyNamed: name_of_the_property];
		[layer propertyNamed: name_of_the_property];
		[objectGroup propertyNamed: name_of_the_property];
		[object propertyNamed: name_of_the_property];

 @since v0.8.1
 */
public class CCTMXTiledMap extends CCNode {
	public final static String LOG_TAG = CCTMXTiledMap.class.getSimpleName();
	/** Possible oritentations of the TMX map */
	/** Orthogonal orientation */
	public final static int CCTMXOrientationOrtho = 0;

	/** Hexagonal orientation */
	public final static int CCTMXOrientationHex = 1;

	/** Isometric orientation */
	public final static int CCTMXOrientationIso = 2;

	/** the map's size property measured in tiles */
	CGSize				mapSize_;
	public final CGSize getMapSize() {
		return mapSize_;
	}

	/** the tiles's size property measured in pixels */
	CGSize				tileSize_;
	public final CGSize getTileSize() {
		return tileSize_;
	}

	/** map orientation */
	int					mapOrientation_;
	public final int getMapOrientation() {
		return mapOrientation_;
	}


	public ArrayList<CCTMXObjectGroup>		objectGroups;

	HashMap<?, ?>		properties_;
	public final HashMap<?, ?> getProperties() {
		return properties_;
	}

	public void setProperties(HashMap<?, ?> h) {
		properties_ = h;
	}

	HashMap<String, HashMap<String, String> >	tileProperties_;

	/** creates a TMX Tiled Map with a TMX file.*/
	public static CCTMXTiledMap tiledMap(String tmxFile) {
		return new CCTMXTiledMap(tmxFile);
	}

	/** initializes a TMX Tiled Map with a TMX file */
	protected CCTMXTiledMap(String tmxFile) {
		super();
		if (tmxFile == null) {
			ccMacros.CCLOG(LOG_TAG, "TMXTiledMap: tmx file should not bi nil");
			return;
		}

		setContentSize(CGSize.zero());

		CCTMXMapInfo mapInfo = CCTMXMapInfo.formatWithTMXFile(tmxFile);

		assert(mapInfo.tilesets.size()!=0) :"TMXTiledMap: Map not found. Please check the filename.";

		mapSize_ 	= mapInfo.mapSize;
		tileSize_ 	= mapInfo.tileSize;
		mapOrientation_ = mapInfo.orientation;
		objectGroups 	= mapInfo.objectGroups;
		properties_ 	= mapInfo.properties;
		tileProperties_ = mapInfo.tileProperties;

		int idx = 0;
		for (CCTMXLayerInfo layerInfo : mapInfo.layers ) {

			if( layerInfo.visible ) {
				CCTMXLayer child = parseLayer(layerInfo, mapInfo);
				addChild(child, idx, idx);

				// update content size with the max size
				CGSize childSize = child.getContentSize();
				CGSize currentSize = getContentSize();
				currentSize.width = (currentSize.width > childSize.width ? currentSize.width : childSize.width);
				currentSize.height = (currentSize.height > childSize.height ? currentSize.height : childSize.height);
				setContentSize(currentSize);

				idx++;
			}
		}
	}

	/** return the TMXLayer for the specific layer */
	public CCTMXLayer layerNamed(String layerName) {
		if (children_ == null)
			return null;
		for (CCNode node : children_) {
			CCTMXLayer layer = (CCTMXLayer)node;
			if (layer != null){
				if (layer.layerName.equals(layerName))
					return (CCTMXLayer)layer;
			}
		}

		// layer not found
		return null;
	}

	/** return the TMXObjectGroup for the secific group */
	public CCTMXObjectGroup objectGroupNamed(String groupName) {
		for (CCTMXObjectGroup objectGroup : objectGroups) {
			if (objectGroup.groupName.equals(groupName))
				return objectGroup;
			}

		// objectGroup not found
		return null;
	}

	/** return the TMXObjectGroup for the secific group
	 @deprecated Use map#objectGroupNamed instead
	 */
	public CCTMXObjectGroup groupNamed(String groupName) {
		return objectGroupNamed(groupName);
	}

	/** return the value for the specific property name */
	public Object propertyNamed(String propertyName) {
		return properties_.get(propertyName);
	}


	/** return properties dictionary for tile GID */
	public HashMap<String, String> propertiesForGID(int GID) {
		return tileProperties_.get(String.valueOf(GID));
	}


	// private
	private CCTMXLayer parseLayer(CCTMXLayerInfo layerInfo, CCTMXMapInfo mapInfo) {
		CCTMXTilesetInfo tileset = tilesetForLayer(layerInfo, mapInfo);
		CCTMXLayer layer = CCTMXLayer.layer(tileset, layerInfo, mapInfo);

		// tell the layerinfo to release the ownership of the tiles map.
		layerInfo.ownTiles = false;

		layer.setupTiles();

		return layer;
	}

	private CCTMXTilesetInfo tilesetForLayer(CCTMXLayerInfo layerInfo, CCTMXMapInfo mapInfo) {
		CCTMXTilesetInfo tileset = null;
		CGSize size = layerInfo.layerSize;

		ListIterator<CCTMXTilesetInfo> iter = mapInfo.tilesets.listIterator(mapInfo.tilesets.size());
		while (iter.hasPrevious()) {
			tileset = iter.previous();
			for (int y=0; y < size.height; y++ ) {
				for (int x=0; x < size.width; x++ ) {
					int pos = (int) (x + size.width * y);
					int gid = layerInfo.tiles.get(pos);

					// gid are stored in little endian.
					// if host is big endian, then swap
					// Java is big-endian
					// if( o == CFByteOrderBigEndian )
					gid = CCFormatter.swapIntToLittleEndian(gid);

					// XXX: gid == 0 --> empty tile
					if( gid != 0 ) {
						// Optimization: quick return
						// if the layer is invalid (more than 1 tileset per layer) an assert will be thrown later
						if( gid >= tileset.firstGid )
							return tileset;
					}
				}
			}
		}

		// If all the tiles are 0, return empty tileset
		ccMacros.CCLOG(LOG_TAG, "cocos2d: Warning: TMX Layer '" + layerInfo.name + "' has no tiles");
		return tileset;
	}

}
