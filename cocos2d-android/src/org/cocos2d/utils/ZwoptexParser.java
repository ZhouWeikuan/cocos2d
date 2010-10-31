package org.cocos2d.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.config.ccMacros;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public class ZwoptexParser extends DefaultHandler {

	private HashMap frames;
	private HashMap metadata;

	private Integer dict_depth;
	private Boolean mode_set_key;
	private Boolean mode_set_string;
	private Boolean mode_set_integer;

	private String section;
	private String metadata_key;

	private String f_key;
	private String f_filename;
	private CGRect f_frame;
	private CGPoint f_offset;
	private Boolean f_rotated;
	private CGSize f_source_size;

	// returns a HashMap with root keys 'frames' and 'metadata'
	public static HashMap parseZwoptex(String filename)
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
		this.frames = new HashMap();
		this.metadata = new HashMap();
	}

	public HashMap getResults() {
		HashMap results = new HashMap();
		results.put("frames", frames);
		results.put("metadata", metadata);
		return results;
	}

	public void startDocument() {
		mode_set_key = false;
		mode_set_string = false;
		mode_set_integer = false;
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
		}
	}

	public void endElement (String uri, String name, String qName)
	{
		if ("dict".equals(name)) {
			// add the frame
			if (dict_depth == 3) {
				HashMap f = new HashMap();
				f.put("frame", f_frame);
				f.put("offset", f_offset);
				f.put("rotated", f_rotated);
				f.put("sourceSize", f_source_size);
				this.frames.put(f_filename, f);
				frameReset();
			}
			--dict_depth;
		}
		mode_set_key = false;
		mode_set_string = false;
		mode_set_integer = false;
	}

	public void characters (char ch[], int start, int length) {
		String s = new String(ch, start, length);

		// section
		if (mode_set_key == true && dict_depth == 1)
		{
			section = s;
		}

		// metadata string
		if ("metadata".equals(section) &&
			mode_set_string == true && dict_depth == 2)
		{
			if (metadata_key.equals("size"))
				metadata.put(metadata_key, parseCoords(s));
		}

		// metadata integer
		if ("metadata".equals(section) &&
			mode_set_integer == true && dict_depth == 2)
		{
			metadata.put(metadata_key, Integer.parseInt(s));
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

		// metadata key
		if ("metadata".equals(section) &&
			mode_set_key == true && dict_depth == 2)
		{
			metadata_key = s;
		}

		// strings
		if ("frames".equals(section) &&
			mode_set_string == true && dict_depth == 3)
		{
			
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
