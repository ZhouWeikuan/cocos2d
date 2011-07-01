package org.cocos2d.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ZwoptexParser extends DefaultHandler {

	private HashMap<String, Object> frames;
	private HashMap<String, Object> metadata;

	private Integer dict_depth;
	private Boolean mode_set_key;
	private Boolean mode_set_string;
	private Boolean mode_set_integer;
	private Boolean mode_set_real;		//ADD BY NGLOOM

	private String section;
	private String metadata_key;

	private String f_key;
	private String f_filename;
	private CGRect f_frame;
	private CGPoint f_offset;
	private Boolean f_rotated;
	private CGSize f_source_size;
	
	private CGSize f_spriteSize;
	private CGPoint f_spriteOffset;
	private CGSize f_spriteSourceSize;
	private CGRect f_textureRect;
	private Boolean f_textureRotated;
	//private Object[] f_aliases; // not supported now
	
	private Integer f_format = 2;

	// returns a HashMap with root keys 'frames' and 'metadata'
	public static HashMap<String, Object> parseZwoptex(String filename)
		throws Exception {

		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

		XMLReader xr = XMLReaderFactory.createXMLReader();
		ZwoptexParser handler = new ZwoptexParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		try {
			InputStream in = CCDirector.theApp.getAssets().open(filename);
			BufferedReader reader =
				new BufferedReader(new InputStreamReader(in), 8192);
			xr.parse(new InputSource(reader));
			return handler.getResults();
		} catch (Exception e) {
			ccMacros.CCLOGERROR("ZwoptexParser", "Unable to parse plist file.");
		}
		return null;

	}

	public ZwoptexParser() {
		super();
		this.frames = new HashMap<String, Object>();
		this.metadata = new HashMap<String, Object>();
	}

	public HashMap<String, Object> getResults() {
		HashMap<String, Object> results = new HashMap<String, Object>();
		results.put("frames", frames);
		metadata.put("format", f_format);	//for the format check NGLOOM
		results.put("metadata", metadata);
		return results;
	}

	public void startDocument() {
		mode_set_key = false;
		mode_set_string = false;
		mode_set_integer = false;
		mode_set_real = false;
		dict_depth = 0;
		frameReset();
	}

	public void frameReset() {
		f_key = "";
		f_filename = "";
		f_frame = null;
		f_offset = null;
		f_rotated = false;
		f_source_size = null;
	}

	public void endDocument() {
	//	ccMacros.CCLOG("ZwoptexParser", "Done parsing plist.");
	}

	public void startElement (String uri, String name,
		String qName, Attributes attrs)
	{
		if ("plist".equals(name)) {
			// plist_version
		} else if ("dict".equals(name)) {
			++dict_depth;
		} else if ("key".equals(name)) {
			mode_set_key = true;
		} else if ("string".equals(name)) {
			mode_set_string = true;
		} else if ("integer".equals(name)) {
			mode_set_integer = true;
		} else if("real".equals(name)) {
			mode_set_real = true;
		}else if ("true".equals(name)) {
			if ("textureRotated".equals(f_key))
				f_textureRotated = true;
		}else if("false".equals(name)) {
			if ("textureRotated".equals(f_key))
				f_textureRotated = false; 
		}
	}

	public void endElement (String uri, String name, String qName)
	{
		if ("dict".equals(name)) {
			// add the frame
			if (dict_depth == 3) {
				HashMap<String, Object> f = new HashMap<String, Object>();
				f.put("frame", f_frame);
				f.put("offset", f_offset);
				f.put("rotated", f_rotated);
				f.put("sourceSize", f_source_size);
				// version 3
				f.put("spriteSize", f_spriteSize);
				f.put("spriteOffset", f_spriteOffset);
				f.put("spriteSourceSize", f_spriteSourceSize);
				f.put("textureRect", f_textureRect);
				f.put("textureRotated", f_textureRotated);
				
				this.frames.put(f_filename, f);
				frameReset();
			}
			--dict_depth;
		}
		mode_set_key = false;
		mode_set_string = false;
		mode_set_integer = false;
		mode_set_real = false;
	}

	int tmpX = 0;
	int tmpY = 0;
	int tmpWidth = 0;
	int tmpHeight = 0;
	float tmpOffsetX = 0;
	float tmpOffsetY = 0;
	public void characters (char ch[], int start, int length) {
		String s = new String(ch, start, length);

		// section
		if (mode_set_key == true && dict_depth == 1)
		{
			section = s;
		}

//		// metadata string //texture elements
//		if ("texture".equals(section) &&
//			mode_set_string == true && dict_depth == 2)
//		{
//			if (metadata_key.equals("size"))
//				metadata.put(metadata_key, parseCoords(s));
//		}

		// metadata key
		if ("texture".equals(section) &&
			mode_set_key == true && dict_depth == 2)
		{
			metadata_key = s;
		}
		
		// metadata integer
		if ("texture".equals(section) &&
			mode_set_integer == true && dict_depth == 2)
		{
			metadata.put(metadata_key, Integer.parseInt(s));
		}
		
		if ("metadata".equals(section) && mode_set_integer == true
				&& dict_depth == 2 && "format".equals(f_key))
		{
			f_format = Integer.parseInt(s);
		}
		
		if ("metadata".equals(section) && mode_set_key == true && dict_depth == 2)
		{
			f_key = s;
		}

		// filename
		if ("frames".equals(section) && mode_set_key == true && dict_depth == 2)
		{
			f_filename = s;
		}

		// frame key
		if ("frames".equals(section) && mode_set_key == true && dict_depth == 3)
		{
			f_key = s;
		}

		// strings
		if ("frames".equals(section) &&
			mode_set_string == true && dict_depth == 3)
		{
			// version 2
			if ("frame".equals(f_key)) {
				f_frame = parseCoordsRect(s);
			} else if ("offset".equals(f_key)) {
				f_offset = parseCoords(s);
			} else if ("rotated".equals(f_key)) {
				f_rotated = Boolean.parseBoolean(s);
	//		} else if ("sourceColorRect".equals(f_key)) {
	//			f_source_color_rect = parseCoordsRect(s);
			} else if ("sourceSize".equals(f_key)) {
				f_source_size = parseCoordsSize(s);
			}
			
			// version 3
			if ("textureRect".equals(f_key)) {
				f_textureRect = parseCoordsRect(s);
			} else if ("spriteOffset".equals(f_key)) {
				f_spriteOffset = parseCoords(s);
			} else if ("spriteSourceSize".equals(f_key)) {
				f_spriteSourceSize = parseCoordsSize(s);
			} else if ("spriteSize".equals(f_key)) {
				f_spriteSize = parseCoordsSize(s);
			}
		}
		
		if("frames".equals(section) &&
				(mode_set_integer || mode_set_real) && dict_depth == 3)
		{
			if("x".equals(f_key))
			{
				tmpX = Integer.parseInt(s);
			}else if("y".equals(f_key))
			{
				tmpY = Integer.parseInt(s);				
			}
			else if("width".equals(f_key))
			{
				tmpWidth = Integer.parseInt(s);
			}
			else if("height".equals(f_key))
			{
				tmpHeight = Integer.parseInt(s);
				f_frame = CGRect.make(tmpX, tmpY, tmpWidth, tmpHeight);
			}
			else if("offsetX".equals(f_key))
			{
				tmpOffsetX  = Float.parseFloat(s);
			}
			else if("offsetY".equals(f_key))
			{
				tmpOffsetY = Float.parseFloat(s);
				f_offset = CGPoint.ccp(tmpOffsetX, tmpOffsetY);
			}
			else if("originalWidth".equals(f_key))
			{
				tmpWidth = Integer.parseInt(s);
			}
			else if("originalHeight".equals(f_key))
			{
				tmpHeight = Integer.parseInt(s);
				f_source_size = CGSize.make(tmpWidth,tmpHeight);
			}		
			
		}
		

	}

	private CGPoint parseCoords(String str)
	{
		String coords = str.replaceAll("[{|}]", "");
		String c[] = coords.split(",");
		return CGPoint.make(Float.parseFloat(c[0]), Float.parseFloat(c[1]));
	}

	private CGSize parseCoordsSize(String str)
	{
		String coords = str.replaceAll("[{|}]", "");
		String c[] = coords.split(",");
		return CGSize.make(Float.parseFloat(c[0]), Float.parseFloat(c[1]));
	}

	private CGRect parseCoordsRect(String str)
	{
		String c[] = str.replaceAll("[{|}]", "").split(",");
		return CGRect.make(Float.parseFloat(c[0]),
			Float.parseFloat(c[1]),
			Float.parseFloat(c[2]),
			Float.parseFloat(c[3]));
	}

}
