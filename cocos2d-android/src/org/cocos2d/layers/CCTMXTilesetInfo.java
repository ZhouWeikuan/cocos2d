package org.cocos2d.layers;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

/**
 * CCTMXTilesetInfo contains the information about the tilesets like:
 - Tileset name
 - Tilset spacing
 - Tileset margin
 - size of the tiles
 - Image used for the tiles
 - Image size

 This information is obtained from the TMX file.
 */
public class CCTMXTilesetInfo {
	String			name;
	int				firstGid;
	CGSize			tileSize;
	int				spacing;
	int				margin;

	// filename containing the tiles (should be spritesheet / texture atlas)
	String			sourceImage;

	// size in pixels of the image
	CGSize			imageSize;


	public CGRect rectForGID(int gid) {
		CGRect rect = CGRect.make(CGPoint.zero(), tileSize);

		gid = gid - firstGid;

		int max_x = (int) ((imageSize.width - margin*2 + spacing) / (tileSize.width + spacing));
		if (max_x == 0) {
			max_x = 1;
		}

		rect.origin.x = (gid % max_x) * (tileSize.width + spacing) + margin;
		rect.origin.y = (gid / max_x) * (tileSize.height + spacing) + margin;

		return rect;
	}

}
