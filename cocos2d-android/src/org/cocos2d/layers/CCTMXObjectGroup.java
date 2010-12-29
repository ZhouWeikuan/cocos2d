package org.cocos2d.layers;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.types.CGPoint;

/*
 *
 *
 * TMX Tiled Map support:
 * http://www.mapeditor.org
 *
 */


/** CCTMXObjectGroup represents the TMX object group.
@since v0.99.0
*/
public class CCTMXObjectGroup  {
	/** name of the group */
	public String groupName;

	/** offset position of child objects */
	public CGPoint	positionOffset;

	/** array of the objects */
	public ArrayList<HashMap<String, String> > objects;


	/** list of properties stored in a dictionary */
	public HashMap<String, String> properties;


	/** return the value for the specific property name */
	public Object propertyNamed(String propertyName) {
		return properties.get(propertyName);
	}


	/** return the dictionary for the specific object name.
	 It will return the 1st object found on the array for the given name.
	 */
	public HashMap<String, String> objectNamed(String objectName) {
		for (HashMap<String, String> object : objects) {
			if (object.get("name").equals(objectName))
				return object;
			}

		// object not found
		return null;
	}

	public CCTMXObjectGroup() {
        super();

		groupName = null;
		positionOffset = CGPoint.zero();
		objects = new ArrayList<HashMap<String, String> >();
		properties = new HashMap<String, String>();
	}

}
