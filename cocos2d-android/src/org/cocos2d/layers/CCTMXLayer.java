package org.cocos2d.layers;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.CCFormatter;

/*
 * TMX Tiled Map support:
 * http://www.mapeditor.org
 *
 */

/** CCTMXLayer represents the TMX layer.

 It is a subclass of CCSpriteSheet. By default the tiles are rendered using a CCTextureAtlas.
 If you mofify a tile on runtime, then, that tile will become a CCSprite, otherwise no CCSprite objects are created.
 The benefits of using CCSprite objects as tiles are:
 - tiles (CCSprite) can be rotated/scaled/moved with a nice API

 If the layer contains a property named "cc_vertexz" with an integer (in can be positive or negative),
 then all the tiles belonging to the layer will use that value as their OpenGL vertex Z for depth.

 On the other hand, if the "cc_vertexz" property has the "automatic" value, then the tiles will use an automatic vertex Z value.
 Also before drawing the tiles, GL_ALPHA_TEST will be enabled, and disabled after drawing them. The used alpha func will be:

    glAlphaFunc( GL_GREATER, value )

 "value" by default is 0, but you can change it from Tiled by adding the "cc_alpha_func" property to the layer.
 The value 0 should work for most cases, but if you have tiles that are semi-transparent, then you might want to use a differnt
 value, like 0.5.

 For further information, please see the programming guide:

	http://www.cocos2d-iphone.org/wiki/doku.php/prog_guide:tiled_maps

 @since v0.8.1
 */
public class CCTMXLayer extends CCSpriteSheet {
	/** Tilset information for the layer */
	public	CCTMXTilesetInfo	tileset;

	/** name of the layer */
	public	String				layerName;

	/** size of the layer in tiles */
	public 	CGSize				layerSize;

	/** size of the map's tile (could be differnt from the tile's size) */
	public	CGSize				mapTileSize;

	/** pointer to the map of tiles */
	public	IntBuffer			tiles;


	/** Layer orientation, which is the same as the map orientation */
	public	int					layerOrientation_;

	/** properties from the layer. They can be added using Tiled */
	public	HashMap<String, String>		properties;

	int					opacity_; // TMX Layer supports opacity

	int					minGID_;
	int					maxGID_;

	// Only used when vertexZ is used
	int					vertexZvalue_;
	boolean				useAutomaticVertexZ_;
	float				alphaFuncValue_;

	// used for optimization
	CCSprite			reusedTile_;
	ArrayList<Integer>		atlasIndexArray_;


	/** creates a CCTMXLayer with an tileset info, a layer info and a map info */
	public static CCTMXLayer layer(CCTMXTilesetInfo tilesetInfo, CCTMXLayerInfo layerInfo, CCTMXMapInfo mapInfo) {
		return new CCTMXLayer(tilesetInfo, layerInfo, mapInfo);
	}
	/** initializes a CCTMXLayer with a tileset info, a layer info and a map info */
	protected CCTMXLayer(CCTMXTilesetInfo tilesetInfo, CCTMXLayerInfo layerInfo, CCTMXMapInfo mapInfo) {
		super(tilesetInfo==null?null:CCTextureCache.sharedTextureCache().addImage(tilesetInfo.sourceImage),
				(int) ((layerInfo.layerSize.width*layerInfo.layerSize.height)*0.35f + 1));

			// layerInfo
			layerName = layerInfo.name;
			layerSize = layerInfo.layerSize;
			tiles = layerInfo.tiles;
			minGID_ = layerInfo.minGID;
			maxGID_ = layerInfo.maxGID;
			opacity_ = layerInfo.opacity;
			properties = new HashMap<String, String>(layerInfo.properties);

			// tilesetInfo
			tileset = tilesetInfo;

			// mapInfo
			mapTileSize = mapInfo.tileSize;
			layerOrientation_ = mapInfo.orientation;

			// offset (after layer orientation is set);
			CGPoint offset = calculateLayerOffset(layerInfo.offset);
			setPosition(offset);

			atlasIndexArray_ =  new ArrayList<Integer>();
			int totalNumberOfTiles = (int) ((layerInfo.layerSize.width*layerInfo.layerSize.height)*0.35f + 1);
			atlasIndexArray_.ensureCapacity(totalNumberOfTiles);

			setContentSize(CGSize.make(layerSize.width * mapTileSize.width, layerSize.height * mapTileSize.height ));

			useAutomaticVertexZ_= false;
			vertexZvalue_ = 0;
			alphaFuncValue_ = 0;

	}



	/** dealloc the map that contains the tile position from memory.
	 Unless you want to know at runtime the tiles positions, you can safely call this method.
	 If you are going to call [layer tileGIDAt:] then, don't release the map
	 */
	public void releaseMap() {
		if (tiles != null) {
			tiles = null;
		}

		if (atlasIndexArray_ != null) {
			atlasIndexArray_ = null;
		}
	}



	/** returns the tile (CCSprite) at a given a tile coordinate.
	 The returned CCSprite will be already added to the CCTMXLayer. Don't add it again.
	 The CCSprite can be treated like any other CCSprite: rotated, scaled, translated, opacity, color, etc.
	 You can remove either by calling:
		- [layer removeChild:sprite cleanup:cleanup];
		- or [layer removeTileAt:ccp(x,y)];
	 */
	public CCSprite tileAt(CGPoint pos) {
		assert(pos.x < layerSize.width && pos.y < layerSize.height && pos.x >=0 && pos.y >=0):"TMXLayer: invalid position";
		assert(tiles!=null && atlasIndexArray_!=null):"TMXLayer: the tiles map has been released";

		CCSprite tile = null;
		int gid = tileGIDAt(pos);

		// if GID == 0, then no tile is present
		if (gid != 0) {
			int z = (int) (pos.x + pos.y * layerSize.width);
			tile = (CCSprite)getChildByTag(z);

			// tile not created yet. create it
			if (tile == null) {
				CGRect rect = tileset.rectForGID(gid);
				tile = CCSprite.sprite(this, rect);
				tile.setPosition(positionAt(pos));
				tile.setVertexZ(vertexZForPos(pos));
				tile.setAnchorPoint(CGPoint.zero());
				tile.setOpacity(opacity_);

				int indexForZ = atlasIndexForExistantZ(z);
				addSpriteWithoutQuad(tile, indexForZ, z);
			}
		}
		return tile;
	}


	/** returns the tile gid at a given tile coordinate.
	 if it returns 0, it means that the tile is empty.
	 This method requires the the tile map has not been previously released (eg. don't call [layer releaseMap])
	 */
	public int tileGIDAt(CGPoint pos) {
		assert( pos.x < layerSize.width && pos.y < layerSize.height && pos.x >=0 && pos.y >=0): "TMXLayer: invalid position";
		assert( tiles!=null && atlasIndexArray_!=null) :"TMXLayer: the tiles map has been released";

		int idx = (int) (pos.x + pos.y * layerSize.width);
		return tiles.get(idx);
	}


	/** sets the tile gid (gid = tile global id) at a given tile coordinate.
	 The Tile GID can be obtained by using the method "tileGIDAt" or by using the TMX editor -> Tileset Mgr +1.
	 If a tile is already placed at that position, then it will be removed.
	 */
	public void setTileGID(int gid, CGPoint pos) {
		assert( pos.x < layerSize.width && pos.y < layerSize.height && pos.x >=0 && pos.y >=0): "TMXLayer: invalid position";
		assert( tiles!=null && atlasIndexArray_!=null):"TMXLayer: the tiles map has been released";

		int currentGID = tileGIDAt(pos);

		if (currentGID != gid ) {

			// setting gid=0 is equal to remove the tile
			if( gid == 0 )
				removeTileAt(pos);

			// empty tile. create a new one
			else if( currentGID == 0 )
				insertTileForGID(gid, pos);

			// modifying an existing tile with a non-empty tile
			else {
				int z = (int) (pos.x + pos.y * layerSize.width);
				CCSprite sprite = (CCSprite) getChildByTag(z);
				if (sprite != null) {
					CGRect rect = tileset.rectForGID(gid);
					sprite.setTextureRect(rect);
					tiles.put(z, gid);
				} else {
					updateTileForGID(gid, pos);
				}
			}
		}
	}


	/** removes a tile at given tile coordinate */
	public void removeTileAt(CGPoint pos) {
		assert(pos.x < layerSize.width && pos.y < layerSize.height && pos.x >=0 && pos.y >=0):"TMXLayer: invalid position";
		assert(tiles!=null && atlasIndexArray_!=null):"TMXLayer: the tiles map has been released";

		int gid = tileGIDAt(pos);

		if (gid != 0) {
			int z = (int) (pos.x + pos.y * layerSize.width);
			int atlasIndex = atlasIndexForExistantZ(z);

			// remove tile from GID map
			tiles.put(z, 0);

			// remove tile from atlas position array
			atlasIndexArray_.remove(atlasIndex);

			// remove it from sprites and/or texture atlas
			CCSprite sprite = (CCSprite) getChildByTag(z);
			if (sprite!=null)
				super.removeChild(sprite, true);
			else {
				textureAtlas_.removeQuad(atlasIndex);

				// update possible children
				for (CCNode node : children_) {
					CCSprite s = (CCSprite)node;
					int ai = s.atlasIndex;
					if( ai >= atlasIndex) {
						s.atlasIndex = ai-1;
					}
				}
			}
		}
	}

	/** returns the position in pixels of a given tile coordinate */
	public CGPoint positionAt(CGPoint pos) {
		CGPoint ret = CGPoint.zero();
		switch( layerOrientation_ ) {
			case CCTMXTiledMap.CCTMXOrientationOrtho:
				ret = positionForOrthoAt(pos);
				break;
			case CCTMXTiledMap.CCTMXOrientationIso:
				ret = positionForIsoAt(pos);
				break;
			case CCTMXTiledMap.CCTMXOrientationHex:
				ret = positionForHexAt(pos);
				break;
		}
		return ret;
	}


	/** return the value for the specific property name */
	public String propertyNamed(String propertyName) {
		return properties.get(propertyName);
	}


	/** Creates the tiles */
	public void setupTiles() {
		// Optimization: quick hack that sets the image size on the tileset
		tileset.imageSize = textureAtlas_.getTexture().getContentSize();
		if (tileset.imageSize == null) {
			tileset.imageSize = CGSize.zero();
		}

		// By default all the tiles are aliased
		// pros:
		//  - easier to render
		// cons:
		//  - difficult to scale / rotate / etc.
		textureAtlas_.getTexture().setAliasTexParameters();

		// Parse cocos2d properties
		parseInternalProperties();

		for (int y=0; y < layerSize.height; y++) {
			for (int x=0; x < layerSize.width; x++) {
				int pos = (int) (x + layerSize.width * y);
				int gid = tiles.get(pos);

				// gid are stored in little endian.
				// if host is big endian, then swap
				// Java is big endian
				// if( o == CFByteOrderBigEndian )
				gid = CCFormatter.swapIntToLittleEndian(gid);

				// XXX: gid == 0 --> empty tile
				if( gid != 0 ) {
					appendTileForGID(gid, CGPoint.ccp(x,y));

					// Optimization: update min and max GID rendered by the layer
					minGID_ = (gid < minGID_? gid : minGID_);
					maxGID_ = (gid > maxGID_? gid : maxGID_);
				}
			}
		}

		assert( maxGID_ >= tileset.firstGid &&
				 minGID_ >= tileset.firstGid): "TMX: Only 1 tilset per layer is supported";
	}



	/** CCTMXLayer doesn't support adding a CCSprite manually.
	 @warning addchild:z:tag: is not supported on CCTMXLayer. Instead of setTileGID:at:/tileAt:
	 */
	public CCNode addChild(CCNode node, int z, int tag) {
		assert(false):"addChild: is not supported on CCTMXLayer. Instead use setTileGID:at:/tileAt:";
		return null;
	}

	public void removeChild(CCNode node, boolean cleanup) {
		// allows removing nil objects
		if( node == null)
			return;

		assert(children_.contains(node)):"Tile does not belong to TMXLayer";

		if (node instanceof CCSprite) {
			CCSprite sprite = (CCSprite) node;
			int atlasIndex = sprite.atlasIndex;
			int zz = atlasIndexArray_.get(atlasIndex);
			tiles.put(zz, 0);
			atlasIndexArray_.remove(atlasIndex);
		}

		super.removeChild(node, true);
	}


	private CGPoint calculateLayerOffset(CGPoint pos) {
		CGPoint ret = CGPoint.zero();
		switch( layerOrientation_ ) {
			case CCTMXTiledMap.CCTMXOrientationOrtho:
				ret = CGPoint.ccp( pos.x * mapTileSize.width, -pos.y *mapTileSize.height);
				break;
			case CCTMXTiledMap.CCTMXOrientationIso:
				ret = CGPoint.ccp( (mapTileSize.width /2) * (pos.x - pos.y),
						  (mapTileSize.height /2 ) * (-pos.x - pos.y) );
				break;
			case CCTMXTiledMap.CCTMXOrientationHex:
				assert(CGPoint.equalToPoint(pos, CGPoint.zero())):"offset for hexagonal map not implemented yet";
				break;
		}
		return ret;
	}

	private CGPoint positionForOrthoAt(CGPoint pos) {
		int x = (int) (pos.x * mapTileSize.width + 0.49f);
		int y = (int) ((layerSize.height - pos.y - 1) * mapTileSize.height + 0.49f);
		return CGPoint.ccp(x,y);
	}

	private CGPoint positionForIsoAt(CGPoint pos) {
		int x = (int) (mapTileSize.width /2 * ( layerSize.width + pos.x - pos.y - 1) + 0.49f);
		int y = (int) (mapTileSize.height /2 * (( layerSize.height * 2 - pos.x - pos.y) - 2) + 0.49f);
		return CGPoint.ccp(x, y);
	}

	private CGPoint positionForHexAt(CGPoint pos) {
		float diffY = 0;
		if( (int)pos.x % 2 == 1 )
			diffY = -mapTileSize.height/2 ;

		int x =  (int) (pos.x * mapTileSize.width*3/4 + 0.49f);
		int y =  (int) ((layerSize.height - pos.y - 1) * mapTileSize.height + diffY + 0.49f);
		return CGPoint.ccp(x,y);
	}

	private int vertexZForPos(CGPoint pos) {
		int ret = 0;
		int maxVal = 0;
		if( useAutomaticVertexZ_ ) {
			switch( layerOrientation_ ) {
				case CCTMXTiledMap.CCTMXOrientationIso:
					maxVal = (int) (layerSize.width + layerSize.height);
					ret = (int) -(maxVal - (pos.x + pos.y));
					break;
				case CCTMXTiledMap.CCTMXOrientationOrtho:
					ret = (int) -(layerSize.height-pos.y);
					break;
				case CCTMXTiledMap.CCTMXOrientationHex:
					assert(false):"TMX Hexa zOrder not supported";
					break;
				default:
					assert(false):"TMX invalid value";
					break;
			}
		} else
			ret = vertexZvalue_;

		return ret;
	}

	/* optimization methos */
	// used only when parsing the map. useless after the map was parsed
	// since lot's of assumptions are no longer true
	private CCSprite appendTileForGID(int gid, CGPoint pos) {
		CGRect rect = tileset.rectForGID(gid);

		int z = (int) (pos.x + pos.y * layerSize.width);

		//if( reusedTile_ == null )
		reusedTile_ = CCSprite.sprite(this, rect);
		//else
		//	reusedTile_ initWithSpriteSheet:self rect:rect];

		reusedTile_.setPosition(positionAt(pos));
		reusedTile_.setVertexZ(vertexZForPos(pos));
		reusedTile_.setAnchorPoint(CGPoint.zero());
		reusedTile_.setOpacity(opacity_);

		// optimization:
		// The difference between appendTileForGID and insertTileforGID is that append is faster, since
		// it appends the tile at the end of the texture atlas
		int indexForZ = atlasIndexArray_.size();


		// don't add it using the "standard" way.
		addQuadFromSprite(reusedTile_, indexForZ);


		// append should be after addQuadFromSprite since it modifies the quantity values
		atlasIndexArray_.add(indexForZ, z);

		return reusedTile_;
	}

	private CCSprite insertTileForGID(int gid, CGPoint pos)	{
		CGRect rect = tileset.rectForGID(gid);

		int z = (int) (pos.x + pos.y * layerSize.width);

		//if ( ! reusedTile_ )
		reusedTile_ = CCSprite.sprite(this, rect);
		//else
		//	[reusedTile_ initWithSpriteSheet:self rect:rect];

		reusedTile_.setPosition(positionAt(pos));
		reusedTile_.setVertexZ(vertexZForPos(pos));
		reusedTile_.setAnchorPoint(CGPoint.zero());
		reusedTile_.setOpacity(opacity_);

		// get atlas index
		int indexForZ = atlasIndexForNewZ(z);

		// Optimization: add the quad without adding a child
		addQuadFromSprite(reusedTile_, indexForZ);

		// insert it into the local atlasindex array
		atlasIndexArray_.add(indexForZ, z);

		// update possible children
		for (CCNode node : children_) {
			CCSprite sprite = (CCSprite)node;
			int ai = sprite.atlasIndex;
			if( ai >= indexForZ)
				sprite.atlasIndex = ai+1;
		}

		tiles.put(z, gid);

		return reusedTile_;
	}

	private CCSprite updateTileForGID(int gid, CGPoint pos) {
		CGRect rect = tileset.rectForGID(gid);

		int z = (int) (pos.x + pos.y * layerSize.width);

		//if( ! reusedTile_ )
		reusedTile_ = CCSprite.sprite(this, rect);
		//else
		//	[reusedTile_ initWithSpriteSheet:self rect:rect];

		reusedTile_.setPosition(positionAt(pos));
		reusedTile_.setVertexZ(vertexZForPos(pos));
		reusedTile_.setAnchorPoint(CGPoint.zero());
		reusedTile_.setOpacity(opacity_);

		// get atlas index
		int indexForZ = atlasIndexForExistantZ(z);

		reusedTile_.atlasIndex = indexForZ;
		reusedTile_.updateTransform();
		tiles.put(z, gid);

		return reusedTile_;
	}

	/* The layer recognizes some special properties, like cc_vertez */
	private void parseInternalProperties() {
		// if cc_vertex=automatic, then tiles will be rendered using vertexz

		String vertexz = propertyNamed("cc_vertexz");
		if( vertexz != null) {
			if (vertexz.equals("automatic"))
				useAutomaticVertexZ_ = true;
			else
				vertexZvalue_ = Integer.parseInt(vertexz);
		}

		String alphaFuncVal = propertyNamed("cc_alpha_func");
		alphaFuncValue_ = alphaFuncVal==null?0.0f:Float.parseFloat(alphaFuncVal);
	}

	// index
	private int atlasIndexForExistantZ(int z) {
		// int key=z;
		// int *item = bsearch((void*)&key, (void*)&atlasIndexArray_->arr[0], atlasIndexArray_->num, sizeof(void*), compareInts);
		// assert(item!=null):"TMX atlas index not found. Shall not happen";
		// int index = ((int)item - (int)atlasIndexArray_->arr) / sizeof(void*);
		int index = atlasIndexArray_.indexOf(z);
		return index;
	}

	private int atlasIndexForNewZ(int z) {
		// XXX: This can be improved with a sort of binary search
		int i=0;
		for( i=0; i< atlasIndexArray_.size() ; i++) {
			int val = (int) atlasIndexArray_.get(i);
			if (z < val)
				break;
		}
		return i;
	}

	public int compareInts (final int a, final int b) {
		return (a - b );
	}

	@Override
	public void draw(GL10 gl) {
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		if( useAutomaticVertexZ_ ) {
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL10.GL_GREATER, alphaFuncValue_);
		}

		super.draw(gl);

		if( useAutomaticVertexZ_ ) {
			gl.glDisable(GL10.GL_ALPHA_TEST);
		}
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	}

}
