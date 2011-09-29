package org.cocos2d.layers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/* CCTMXMapInfo contains the information about the map like:
 - Map orientation (hexagonal, isometric or orthogonal)
 - Tile size
 - Map size

 And it also contains:
 - Layers (an array of TMXLayerInfo objects)
 - Tilesets (an array of TMXTilesetInfo objects)
 - ObjectGroups (an array of TMXObjectGroupInfo objects)

 This information is obtained from the TMX file.

 */
public class CCTMXMapInfo {
	public final static String LOG_TAG = CCTMXMapInfo.class.getSimpleName();


	public final static int TMXLayerAttribNone = 1 << 0;
	public final static int TMXLayerAttribBase64 = 1 << 1;
	public final static int TMXLayerAttribGzip = 1 << 2;

	public final static int TMXPropertyNone = 0;
	public final static int TMXPropertyMap  = 1;
	public final static int TMXPropertyLayer= 2;
	public final static int TMXPropertyObjectGroup = 3;
	public final static int TMXPropertyObject = 4;
	public final static int TMXPropertyTile = 5;

	protected StringBuilder	    currentString;
	protected boolean		storingCharacters;
	protected int			layerAttribs;
	protected int			parentElement;
	protected int		    parentGID;


	// tmx filename
	public String filename;

	// map orientation
	public int	orientation;

	// map width & height
	public CGSize	mapSize;

	// tiles width & height
	public CGSize	tileSize;

	// Layers
	public ArrayList<CCTMXLayerInfo> layers;

	// tilesets
	public ArrayList<CCTMXTilesetInfo> tilesets;

	// ObjectGroups
	public ArrayList<CCTMXObjectGroup> objectGroups;

	// properties
	public HashMap<String, String> properties;

	// tile properties
	public HashMap<String, HashMap<String, String>> tileProperties;


	/** creates a TMX Format with a tmx file */
	public static CCTMXMapInfo formatWithTMXFile(String tmxFile) {
		return new CCTMXMapInfo(tmxFile);
	}

	/** initializes a TMX format witha  tmx file */
	protected CCTMXMapInfo(String tmxFile) {
		super();

		tilesets= new ArrayList<CCTMXTilesetInfo>();
		layers	= new ArrayList<CCTMXLayerInfo>();
		filename = tmxFile;
		objectGroups 	= new ArrayList<CCTMXObjectGroup>();
		properties 		= new HashMap<String, String>();
		tileProperties 	= new HashMap<String, HashMap<String, String> >();

		// tmp vars
		currentString = new StringBuilder();
		storingCharacters = false;
		layerAttribs 	= TMXLayerAttribNone;
		parentElement 	= TMXPropertyNone;

		parseXMLFile(filename);
	}

	/* initalises parsing of an XML file, either a tmx (Map) file or tsx (Tileset) file */
	private void parseXMLFile(String xmlFilename) {
		try {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser parser = saxFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			InputStream is = CCDirector.sharedDirector().getActivity().getResources().getAssets().open(xmlFilename);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			CCTMXXMLParser handler = new CCTMXXMLParser();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(in));
		} catch(Exception e) {
			Log.e(LOG_TAG, e.getStackTrace().toString());
		}
		/*
		// we'll do the parsing
		[parser setDelegate:self];
		[parser setShouldProcessNamespaces:NO];
		[parser setShouldReportNamespacePrefixes:NO];
		[parser setShouldResolveExternalEntities:NO];
		[parser parse];

		NSAssert1( ! [parser parserError], @"Error parsing file: %@.", xmlFilename );

		[parser release];
		 */
	}



	/*
	 * Internal TMX parser
	 *
	 * IMPORTANT: These classed should not be documented using doxygen strings
	 * since the user should not use them.
	 *
	 */
	class CCTMXXMLParser extends DefaultHandler {

		@Override
		public void startDocument() {
			// myList = new ArrayList<HashMap<String, String>>();
		}

		@Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) 
			throws SAXException {

			if (localName.equals("map")) {
				String version = attributes.getValue("version");
				if (! version.equals("1.0")) {
					ccMacros.CCLOG(LOG_TAG, "cocos2d: TMXFormat: Unsupported TMX version: " + version);
				}

				String orientationStr = attributes.getValue("orientation");
				if (orientationStr.equals("orthogonal")) {
					orientation = CCTMXTiledMap.CCTMXOrientationOrtho;
				} else if (orientationStr.equals("isometric")) {
					orientation = CCTMXTiledMap.CCTMXOrientationIso;
				} else if (orientationStr.equals("hexagonal")) {
					orientation = CCTMXTiledMap.CCTMXOrientationHex;
				} else {
					ccMacros.CCLOG(LOG_TAG, "cocos2d: TMXFomat: Unsupported orientation: " + orientation);
				}

				mapSize = CGSize.make(Integer.parseInt(attributes.getValue("width")),
						Integer.parseInt(attributes.getValue("height")));
				tileSize = CGSize.make(Integer.parseInt(attributes.getValue("tilewidth")),
						Integer.parseInt(attributes.getValue("tileheight")));

				// The parent element is now "map"
				parentElement = TMXPropertyMap;
			} else if (localName.equals("tileset")) {

				// If this is an external tileset then start parsing that
				String externalTilesetFilename = attributes.getValue("source");
				if (externalTilesetFilename != null) {
					// Tileset file will be relative to the map file. So we need to convert it to an absolute path
					String dir =  filename.substring(0, filename.lastIndexOf("/"));
					externalTilesetFilename = dir + "/" + externalTilesetFilename;	// Append path to tileset file

					CCTMXMapInfo.this.parseXMLFile(externalTilesetFilename);
				} else {
					CCTMXTilesetInfo tileset = new CCTMXTilesetInfo();
					tileset.name 		= attributes.getValue("name");
					tileset.firstGid 	= Integer.parseInt(attributes.getValue("firstgid"));
                    String value        = attributes.getValue("spacing"); 
                    tileset.spacing     = value== null?0:Integer.parseInt(value);
                    value               = attributes.getValue("margin") ;
                    tileset.margin      = value== null?0:Integer.parseInt(value);
                    CGSize s = CGSize.zero();
					s.width = Integer.parseInt(attributes.getValue("tilewidth"));
					s.height = Integer.parseInt(attributes.getValue("tileheight"));
					tileset.tileSize = s;

					tilesets.add(tileset);
				}

			} else if (localName.equals("tile")) {
				CCTMXTilesetInfo info = tilesets.get(tilesets.size()-1);
				HashMap<String, String> dict = new HashMap<String, String>();
				parentGID =  info.firstGid + Integer.parseInt(attributes.getValue("id"));
				tileProperties.put(String.valueOf(parentGID), dict);
				parentElement = TMXPropertyTile;

			} else if (localName.equals("layer")) {
				CCTMXLayerInfo layer = new CCTMXLayerInfo();
				layer.name = attributes.getValue("name");

				CGSize s = CGSize.zero();
				s.width = Integer.parseInt(attributes.getValue("width"));
				s.height = Integer.parseInt(attributes.getValue("height"));
				layer.layerSize = s;

				String visible = attributes.getValue("visible");
				layer.visible = visible==null||!(visible.equals("0"));

				if (attributes.getValue("opacity") != null) {
					layer.opacity = (int) (255 * Float.parseFloat(attributes.getValue("opacity")));
				} else {
					layer.opacity = 255;
				}

				try {
					int x = Integer.parseInt(attributes.getValue("x"));
					int y = Integer.parseInt(attributes.getValue("y"));

					layer.offset = CGPoint.ccp(x,y);
				} catch (Exception e) {
					layer.offset = CGPoint.zero();
				}

				layers.add(layer);

				// The parent element is now "layer"
				parentElement = TMXPropertyLayer;

			} else if (localName.equals("objectgroup")) {

				CCTMXObjectGroup objectGroup = new CCTMXObjectGroup();
				objectGroup.groupName = attributes.getValue("name");
				CGPoint positionOffset = CGPoint.zero();
				try {
					positionOffset.x = Integer.parseInt(attributes.getValue("x")) * tileSize.width;
					positionOffset.y = Integer.parseInt(attributes.getValue("y")) * tileSize.height;
				} catch (Exception e) {
				}
				objectGroup.positionOffset = positionOffset;

				objectGroups.add(objectGroup);

				// The parent element is now "objectgroup"
				parentElement = TMXPropertyObjectGroup;

			} else if (localName.equals("image")) {
				CCTMXTilesetInfo tileset = tilesets.get(tilesets.size()-1);

				// build full path
				String imagename = attributes.getValue("source");
				int idx = filename.lastIndexOf("/");
				if (idx != -1) {
					String path = filename.substring(0, idx);
					tileset.sourceImage = path + "/" + imagename;
				} else {
					tileset.sourceImage = imagename;
				}			

			} else if (localName.equals("data")) {
				String encoding = attributes.getValue("encoding");
				String compression = attributes.getValue("compression");

				if (encoding.equals("base64")) {
					layerAttribs |= TMXLayerAttribBase64;
					storingCharacters = true;

					assert (compression==null || compression.equals("gzip")): "TMX: unsupported compression method";

					if (compression.equals("gzip")) {
						layerAttribs |= TMXLayerAttribGzip;
					}
				}

				assert (layerAttribs != TMXLayerAttribNone): "TMX tile map: Only base64 and/or gzip maps are supported";

			} else if (localName.equals("object")) {

				CCTMXObjectGroup objectGroup = objectGroups.get(objectGroups.size()-1);

				// The value for "type" was blank or not a valid class name
				// Create an instance of TMXObjectInfo to store the object and its properties
				HashMap<String, String>  dict = new HashMap<String, String>();

				// Set the name of the object to the value for "name"
				dict.put("name", attributes.getValue("name"));

				// Assign all the attributes as key/name pairs in the properties dictionary
				dict.put("type", attributes.getValue("type"));

				int x = (int) (Integer.parseInt(attributes.getValue("x")) + objectGroup.positionOffset.x);
				dict.put("x", String.valueOf(x));

				int y = (int) (Integer.parseInt(attributes.getValue("y")) + objectGroup.positionOffset.y);
				// Correct y position. (Tiled uses Flipped, cocos2d uses Standard)
				y = (int) ((mapSize.height * tileSize.height) - y - Integer.parseInt(attributes.getValue("height")));
				dict.put("y", String.valueOf(y));

				dict.put("width", attributes.getValue("width"));
				dict.put("height", attributes.getValue("height"));

				// Add the object to the objectGroup
				objectGroup.objects.add(dict);

				// The parent element is now "object"
				parentElement = TMXPropertyObject;

			} else if(localName.equals("property")) {
				String name = attributes.getValue("name");
				String value= attributes.getValue("value");
				if ( parentElement == TMXPropertyNone ) {

					ccMacros.CCLOG(LOG_TAG,
							"TMX tile map: Parent element is unsupported. Cannot add property named '" + name + "' with value '" + value + "'");
				} else if ( parentElement == TMXPropertyMap ) {

					// The parent element is the map
					properties.put(name, value);

				} else if ( parentElement == TMXPropertyLayer ) {

					// The parent element is the last layer
					CCTMXLayerInfo layer = layers.get(layers.size()-1);
					// Add the property to the layer
					layer.properties.put(name, value);

				} else if ( parentElement == TMXPropertyObjectGroup ) {

					// The parent element is the last object group
					CCTMXObjectGroup objectGroup = objectGroups.get(objectGroups.size()-1);
					objectGroup.properties.put(name, value);

				} else if ( parentElement == TMXPropertyObject ) {

					// The parent element is the last object
					CCTMXObjectGroup objectGroup = objectGroups.get(objectGroups.size()-1);
					HashMap<String, String>  dict = objectGroup.objects.get(objectGroup.objects.size()-1);
					dict.put(name, value);
				} else if ( parentElement == TMXPropertyTile ) {

					HashMap<String, String> dict = tileProperties.get(String.valueOf(parentGID));
					dict.put(name, value);
				}
			}
		}


		@Override
		public void endElement(String uri, String elementName, String qName)
		throws SAXException {
			if (elementName.equals("data") && (layerAttribs&TMXLayerAttribBase64) != 0) {
				storingCharacters = false;

				CCTMXLayerInfo layer = layers.get(layers.size()-1);

				byte[] buffer = null;
				try {
					buffer = Base64.decode(currentString.toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (buffer == null ) {
					ccMacros.CCLOG(LOG_TAG, "cocos2d: TiledMap: decode data error");
					return;
				}

				/*if ((layerAttribs & TMXLayerAttribGzip) != 0) {
					try {
						byte  deflated[] = new byte [1024];
						GZIPInputStream gzi = new GZIPInputStream(new ByteArrayInputStream(buffer));
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						int ln;
						while ((ln = gzi.read(deflated)) > 0) {
							out.write(deflated, 0, ln);
						}
						gzi.close();
						out.close();

						deflated = out.toByteArray();

						ByteBuffer b = ByteBuffer.wrap(deflated);
						layer.tiles = b.asIntBuffer().array();
					} catch (Exception e) {
						ccMacros.CCLOG(LOG_TAG, "cocos2d: TiledMap: inflate data error");
						return;
					}

				} else { */
				// automatically ungzip, so we can make use of it directly.
				try {
					ByteBuffer b = ByteBuffer.wrap(buffer);
					layer.tiles = b.asIntBuffer();
				} catch (Exception e) {
					ccMacros.CCLOG(LOG_TAG, "cocos2d: TiledMap: inflate data error");
				}

				currentString = new StringBuilder();
			} else if (elementName.equals("map")) {
				// The map element has ended
				parentElement = TMXPropertyNone;

			} else if (elementName.equals("layer")) {
				// The layer element has ended
				parentElement = TMXPropertyNone;

			} else if (elementName.equals("objectgroup")) {
				// The objectgroup element has ended
				parentElement = TMXPropertyNone;

			} else if (elementName.equals("object")) {
				// The object element has ended
				parentElement = TMXPropertyNone;
			}

		}

		@Override
		public void characters(char[] ch, int start, int length)
		throws SAXException {
			if (storingCharacters) {
				currentString.append(ch, start, length);
			}
		}

		@Override
		public void   	error(SAXParseException e) {
			ccMacros.CCLOG(LOG_TAG, e.getLocalizedMessage());
		}

		@Override
		public void 	fatalError(SAXParseException e) {
			ccMacros.CCLOG(LOG_TAG, e.getLocalizedMessage());
		}

	}

}
