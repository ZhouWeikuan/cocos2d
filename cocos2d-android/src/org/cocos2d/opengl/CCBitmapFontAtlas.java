package org.cocos2d.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.protocols.CCLabelProtocol;
import org.cocos2d.protocols.CCRGBAProtocol;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.collections.IntMap;
import org.cocos2d.utils.javolution.TextBuilder;

/** CCBitmapFontAtlas is a subclass of CCSpriteSheet.

Features:
- Treats each character like a CCSprite. This means that each individual character can be:
- rotated
- scaled
   - translated
   - tinted
   - chage the opacity
 - It can be used as part of a menu item.
 - anchorPoint can be used to align the "label"
 - Supports AngelCode text format
 
 Limitations:
  - All inner characters are using an anchorPoint of (0.5f, 0.5f) and it is not recommend to change it
    because it might affect the rendering
 
 CCBitmapFontAtlas implements the protocol CCLabelProtocol, like CCLabel and CCLabelAtlas.
 CCBitmapFontAtlas has the flexibility of CCLabel, the speed of CCLabelAtlas and all the features of CCSprite.
 If in doubt, use CCBitmapFontAtlas instead of CCLabelAtlas / CCLabel.
 
 Supported editors:
  - http://www.n4te.com/hiero/hiero.jnlp
  - http://slick.cokeandcode.com/demos/hiero.jnlp
  - http://www.angelcode.com/products/bmfont/
 
 @since v0.8
*/

public class CCBitmapFontAtlas extends CCSpriteSheet implements CCLabelProtocol, CCRGBAProtocol {

	// how many characters are supported
	public static final int kCCBitmapFontAtlasMaxChars = 2048; //256,

    // Equal function for targetSet.
    static class tKerningHashElement {	
        int				key;		// key for the hash. 16-bit for 1st element, 16-bit for 2nd element
        int				amount;
    }

    /** @struct ccBitmapFontDef
      bitmap font definition
      */
    static class ccBitmapFontDef {
        //! ID of the character
        int charID;
        //! origin and size of the font
        CGRect rect = CGRect.make(0, 0, 0, 0);
        //! The X amount the image should be offset when drawing the image (in pixels)
        int xOffset;
        //! The Y amount the image should be offset when drawing the image (in pixels)
        int yOffset;
        //! The amount to move the current position after drawing the character (in pixels)
        int xAdvance;
    }


    /** @struct ccBitmapFontPadding
      bitmap font padding
      @since v0.8.2
      */
    class ccBitmapFontPadding {
        /// padding left
        int	left;
        /// padding top
        int top;
        /// padding right
        int right;
        /// padding bottom
        int bottom;
    };

    /** CCBitmapFontConfiguration has parsed configuration of the the .fnt file
      @since v0.8
      */
    static class CCBitmapFontConfiguration {
        // XXX: Creating a public interface so that the bitmapFontArray[] is accesible
        // The characters building up the font
        //public ccBitmapFontDef	bitmapFontArray[] = new ccBitmapFontDef[kCCBitmapFontAtlasMaxChars];
    	public IntMap<ccBitmapFontDef>	bitmapFontArray = new IntMap<ccBitmapFontDef>();
    	
        // FNTConfig: Common Height
        public int commonHeight;

        // Padding
        public ccBitmapFontPadding	padding;

        // atlas name
        public String		atlasName;

        // values for kerning
        public IntMap<tKerningHashElement> kerningDictionary;

        /** allocates a CCBitmapFontConfiguration with a FNT file */
        public static CCBitmapFontConfiguration configuration(String FNTfile) {
        	return new CCBitmapFontConfiguration(FNTfile);
        }

        /** initializes a BitmapFontConfiguration with a FNT file */
        protected CCBitmapFontConfiguration(String FNTfile) {
            super();
            kerningDictionary = new IntMap<tKerningHashElement>();
            parseConfigFile(FNTfile);
        }

        /*
           @Override
           public void finalize() {
                // ccMacros.CCLOGINFO("cocos2d: deallocing %@", self);
                purgeKerningDictionary();
                atlasName = null;
            }
        */

        /*
        - (NSString*) description
        {
            return [NSString stringWithFormat:@"<%@ = %08X | Kernings:%d | Image = %@>", [self class], self,
                   HASH_COUNT(kerningDictionary),
                   [[atlasName pathComponents] lastObject] ];
        }
        */


        public void purgeKerningDictionary() {
            kerningDictionary.clear();
            kerningDictionary = null;
        }

        public void parseConfigFile(String fntFile) {	
        	InputStream in = null;
			try {
				in = CCDirector.theApp.getAssets().open(fntFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        	
            // Create a holder for each line we are going to work with
            String line;

            // Loop through all the lines in the lines array processing each one
            try {
				while( (line = reader.readLine()) != null ) {
				    // parse spacing / padding
				    if(line.startsWith("info face")) {
				        // XXX: info parsing is incomplete
				        // Not needed for the Hiero editors, but needed for the AngelCode editor
				        //			[self parseInfoArguments:line];
				    }
				    // Check to see if the start of the line is something we are interested in
				    else if(line.startsWith("common lineHeight")) {
				        parseCommonArguments(line);
				    }
				    else if(line.startsWith("page id")) {
				        parseImage(line, fntFile);
				    }
				    else if(line.startsWith("chars c")) {
				        // Ignore this line
				    }
				    else if(line.startsWith("char")) {
				        // Parse the current line and create a new CharDef
				        ccBitmapFontDef characterDefinition = new ccBitmapFontDef();
				        this.parseCharacterDefinition(line, characterDefinition);
				        
				        // Add the CharDef returned to the charArray
				        //bitmapFontArray[ characterDefinition.charID ] = characterDefinition;
				        bitmapFontArray.put(characterDefinition.charID, characterDefinition);
				    }
				    else if(line.startsWith("kernings count")) {
				        this.parseKerningCapacity(line);
				    }
				    else if(line.startsWith("kerning first")) {
				        parseKerningEntry(line);
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // Finished with lines so release it
            try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        public void parseImage(String line, String fntFile) {
            String propertyValue = null;

            // Break the values for this line up using =
            String [] values = line.split("=");
            List<String> lvalues = Arrays.asList(values);
            ListIterator<String> nse = lvalues.listIterator();

            // We need to move past the first entry in the array before we start assigning values
            nse.next();

            // page ID. Sanity check
            propertyValue = nse.next();
            assert (Integer.valueOf(propertyValue) == 0)
            	:"XXX: BitmapFontAtlas only supports 1 page";

            // file 
            propertyValue = nse.next();
            String [] array = propertyValue.split("\"");
            propertyValue = array[1];
            assert (propertyValue!=null):"BitmapFontAtlas file could not be found";

            // String textureAtlasName = [CCFileUtils fullPathFromRelativePath:propertyValue];
            // String relDirPathOfTextureAtlas = [fntFile stringByDeletingLastPathComponent];

            atlasName = propertyValue; // [relDirPathOfTextureAtlas stringByAppendingPathComponent:textureAtlasName];	
            // [atlasName retain];
        }

        public void parseInfoArguments(String line) {
            //
            // possible lines to parse:
            // info face="Script" size=32 bold=0 italic=0 charset="" unicode=1 stretchH=100 smooth=1 aa=1 padding=1,4,3,2 spacing=0,0 outline=0
            // info face="Cracked" size=36 bold=0 italic=0 charset="" unicode=0 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1
            //
            String[] values = line.split("=");
            ListIterator<String> nse = Arrays.asList(values).listIterator();	
            String propertyValue = null;

            // We need to move past the first entry in the array before we start assigning values
            nse.next();

            // face (ignore)
            nse.next();

            // size (ignore)
            nse.next();

            // bold (ignore)
            nse.next();

            // italic (ignore)
            nse.next();

            // charset (ignore)
            nse.next();

            // unicode (ignore)
            nse.next();

            // strechH (ignore)
            nse.next();

            // smooth (ignore)
            nse.next();

            // aa (ignore)
            nse.next();

            // padding (ignore)
            propertyValue = nse.next();
            {
                String[] paddingValues = propertyValue.split(",");
                ListIterator<String> paddingEnum = Arrays.asList(paddingValues).listIterator();
                // padding top
                propertyValue = paddingEnum.next();
                padding.top = Integer.valueOf(propertyValue);

                // padding right
                propertyValue = paddingEnum.next();
                padding.right = Integer.valueOf(propertyValue);

                // padding bottom
                propertyValue = paddingEnum.next();
                padding.bottom = Integer.valueOf(propertyValue);

                // padding left
                propertyValue = paddingEnum.next();
                padding.left = Integer.valueOf(propertyValue);

                // CCLOG(@"cocos2d: padding: %d,%d,%d,%d", padding.left, padding.top, padding.right, padding.bottom);
            }

            // spacing (ignore)
            nse.next();	
        }

        public void parseCommonArguments(String line) {
            //
            // line to parse:
            // common lineHeight=104 base=26 scaleW=1024 scaleH=512 pages=1 packed=0
            //
            String[] values = line.split("=");
            ListIterator<String> nse = Arrays.asList(values).listIterator();	
            String propertyValue = null;

            // We need to move past the first entry in the array before we start assigning values
            nse.next();

            // Character ID
            propertyValue = nse.next();
            propertyValue = propertyValue.split(" ", 2)[0];
            commonHeight = Integer.parseInt(propertyValue);

            // base (ignore)
            nse.next();


            // scaleW. sanity check
            propertyValue = nse.next();	
            //asssert (Integer.valueOf(propertyValue) <= ccConfig. tureSize()):"CCBitmapFontAtlas: page can't be larger than supported";

            // scaleH. sanity check
            propertyValue = nse.next();;
            // NSAssert( [propertyValue intValue] <= [[CCConfiguration sharedConfiguration] maxTextureSize], @"CCBitmapFontAtlas: page can't be larger than supported");

            // pages. sanity check
            propertyValue = nse.next();
            // NSAssert( [propertyValue intValue] == 1, @"CCBitfontAtlas: only supports 1 page");

            // packed (ignore) What does this mean ??
        }

        public void parseCharacterDefinition(String line, ccBitmapFontDef characterDefinition) {	
            // Break the values for this line up using =
            String[] values = line.split("=");
            ListIterator<String> nse = Arrays.asList(values).listIterator();	
            String propertyValue = null;

            // We need to move past the first entry in the array before we start assigning values
            nse.next();

            // Character ID
            propertyValue = nse.next();
            // propertyValue = [propertyValue substringToIndex: [propertyValue rangeOfString: @" "].location];
            propertyValue = propertyValue.substring(0, propertyValue.indexOf(" "));
            characterDefinition.charID = Integer.valueOf(propertyValue);
            // NSAssert(characterDefinition->charID < kCCBitmapFontAtlasMaxChars, @"BitmpaFontAtlas: CharID bigger than supported");

            // Character x
            propertyValue = nse.next();
            propertyValue = propertyValue.substring(0, propertyValue.indexOf(" "));
            characterDefinition.rect.origin.x = Integer.valueOf(propertyValue);
            // Character y
            propertyValue = nse.next();
            characterDefinition.rect.origin.y = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
            // Character width
            propertyValue = nse.next();
            characterDefinition.rect.size.width = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
            // Character height
            propertyValue = nse.next();
            characterDefinition.rect.size.height = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
            // Character xoffset
            propertyValue = nse.next();
            characterDefinition.xOffset = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
            // Character yoffset
            propertyValue = nse.next();
            characterDefinition.yOffset = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
            // Character xadvance
            propertyValue = nse.next();
            characterDefinition.xAdvance = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));
        }

        public void parseKerningCapacity(String line) {
            // When using uthash there is not need to parse the capacity.

            //	NSAssert(!kerningDictionary, @"dictionary already initialized");
            //	
            //	// Break the values for this line up using =
            //	NSArray *values = [line componentsSeparatedByString:@"="];
            //	NSEnumerator *nse = [values objectEnumerator];	
            //	NSString *propertyValue;
            //	
            //	// We need to move past the first entry in the array before we start assigning values
            //	[nse nextObject];
            //	
            //	// count
            //	propertyValue = [nse nextObject];
            //	int capacity = [propertyValue intValue];
            //	
            //	if( capacity != -1 )
            //		kerningDictionary = ccHashSetNew(capacity, targetSetEql);
        }

        public void parseKerningEntry(String line) {
            String[] values = line.split("=");
            ListIterator<String> nse = Arrays.asList(values).listIterator();
            
            String propertyValue = null;

            // We need to move past the first entry in the array before we start assigning values
            nse.next();

            // first
            propertyValue = nse.next();
            int first = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));

            // second
            propertyValue = nse.next();
            int second = Integer.valueOf(propertyValue.substring(0, propertyValue.indexOf(" ")));

            // second
            propertyValue = nse.next();
            int amount = Integer.valueOf(propertyValue.trim());

            tKerningHashElement element = new tKerningHashElement(); 
            element.amount = amount;
            element.key = ((first&0x00ff)<<16) | (second&0x00ff);
            kerningDictionary.put(element.key, element);
        }
    }


	// string to render
	TextBuilder		string_;
	
	static CCBitmapFontConfiguration parsed;
	CCBitmapFontConfiguration	configuration_;

	// texture RGBA
	int		opacity_;
    /** conforms to CCRGBAProtocol protocol */
    public int getOpacity() {
        return opacity_;
    }

    public void setOpacity(int o) {
        opacity_ = o;

        int len = children_.size();
        for (int i = 0; i < len; i++) {
        	CCNode child = children_.get(i);
        	CCRGBAProtocol p = (CCRGBAProtocol)child;
            p.setOpacity(opacity_);
        }
    }

    ccColor3B	color_;
    /** conforms to CCRGBAProtocol protocol */
    public ccColor3B getColor() {
        return new ccColor3B(color_);
    }

    public void setColor(ccColor3B color) {
        color_.set(color);
        int len = children_.size();
        for (int i = 0; i < len; i++) {
        	CCNode child = children_.get(i);
        	CCRGBAProtocol p = (CCRGBAProtocol)child;
            p.setColor(color);
        }
    }

    boolean opacityModifyRGB_;

    /** Purges the cached data.
      Removes from memory the cached configurations and the atlas name dictionary.
      @since v0.99.3
      */
    public static void purgeCachedData() {
        FNTConfigRemoveCache();
    }

    /** creates a bitmap font altas with an initial string and the FNT file */
    public static CCBitmapFontAtlas bitmapFontAtlas(CharSequence string, String fntFile) {
        return new CCBitmapFontAtlas(string, fntFile);
    }

    /** init a bitmap font altas with an initial string and the FNT file */
    protected CCBitmapFontAtlas(CharSequence theString, String fntFile) {
        super((parsed= FNTConfigLoadFile(fntFile)).atlasName , theString.length());
        
        configuration_  = parsed;
        // assert configuration_:"Error creating config for BitmapFontAtlas";

        opacity_ = 255;
        color_ = new ccColor3B(ccColor3B.ccWHITE);

        contentSize_ = CGSize.zero();
        opacityModifyRGB_ = textureAtlas_.getTexture().hasPremultipliedAlpha();
        anchorPoint_ = CGPoint.ccp(0.5f, 0.5f);

        string_ = new TextBuilder();
        
        setString(theString);
    }

    public static HashMap<String, CCBitmapFontConfiguration> configurations = null;
    /** Free function that parses a FNT file a place it on the cache */
    public static CCBitmapFontConfiguration FNTConfigLoadFile(String fntFile) {
        CCBitmapFontConfiguration ret = null;

        if( configurations == null )
            configurations = new HashMap<String, CCBitmapFontConfiguration>(); 

        ret = configurations.get(fntFile);
        if( ret == null ) {
            ret = CCBitmapFontConfiguration.configuration(fntFile);
            configurations.put(fntFile, ret);
        }

        return ret;
    }

    /** Purges the FNT config cache */
    public static void FNTConfigRemoveCache( ) {
        configurations.clear();
    }

    /*
    public void finalize() {
        [string_ release];
        [configuration_ release];
        [super dealloc];
    }
    */

    public int kerningAmount(int first, int second) {
        first &= 0x0ff;
        second &= 0x0ff;
        int ret = 0;
        int key = (first<<16) | (second & 0xffff);

        if( configuration_.kerningDictionary != null) {
            tKerningHashElement element = configuration_.kerningDictionary.get(key);
            if(element != null)
                ret = element.amount;
        }

        return ret;
    }


    /** updates the font chars based on the string to render */
    public void createFontChars() {
        int nextFontPositionX = 0;
        int nextFontPositionY = 0;
        char prev = (char)-1;
		int kerningAmount = 0;

		int longestLine = 0;
		int totalHeight = 0;

		int quantityOfLines = 1;

		int stringLen = string_.length();
		if(stringLen == 0)
			return;

		// quantity of lines NEEDS to be calculated before parsing the lines,
		// since the Y position needs to be calculated before hand
		for(int i=0; i < stringLen-1;i++) {
			char c = string_.charAt(i);
			if( c=='\n')
				quantityOfLines++;
		}

		totalHeight = configuration_.commonHeight * quantityOfLines;
		nextFontPositionY = -(configuration_.commonHeight - configuration_.commonHeight*quantityOfLines);

		for(int i=0; i<stringLen; i++) {
			char c = string_.charAt(i);

			if (c == '\n') {
				nextFontPositionX = 0;
				nextFontPositionY -= configuration_.commonHeight;
				continue;
			}

			kerningAmount = kerningAmount(prev, c);

			ccBitmapFontDef fontDef = configuration_.bitmapFontArray.get(c);//Integer.valueOf(c));
			if (fontDef == null)
				continue;

			CGRect rect = fontDef.rect;

			CCSprite fontChar;

			fontChar = (CCSprite)getChildByTag(i);
			if( fontChar == null ) {
				fontChar = CCSprite.sprite(this, rect);
				addChild(fontChar, 0, i);
			}
			else {
				// reusing fonts
				fontChar.setTextureRect(rect);

				// restore to default in case they were modified
				fontChar.setVisible(true);
				fontChar.setOpacity(255);
			}

			float yOffset = configuration_.commonHeight - fontDef.yOffset;
			fontChar.setPosition((float)nextFontPositionX + fontDef.xOffset + fontDef.rect.size.width*0.5f + kerningAmount,
									(float)nextFontPositionY + yOffset - rect.size.height*0.5f );

			// update kerning
			nextFontPositionX += fontDef.xAdvance + kerningAmount;
			prev = c;

			// Apply label properties
			fontChar.setOpacityModifyRGB(opacityModifyRGB_);
			// Color MUST be set before opacity, since opacity might change color if OpacityModifyRGB is on
			fontChar.setColor(color_);

			// only apply opacity if it is different than 255 )
			// to prevent modifying the color too (issue #610)
			if( opacity_ != 255 )
				fontChar.setOpacity(opacity_);

			if (longestLine < nextFontPositionX)
				longestLine = nextFontPositionX;
		}

		// using direct set
		setContentSize(longestLine, totalHeight);
    }

    public void setString(CharSequence newString) {	
        string_.reset();
        string_.append(newString);

        int len = children_.size();
        for (int i = 0; i < len; i++) {
        	CCNode child = children_.get(i);
            child.setVisible(false);
        }

        createFontChars();
    }

    public void setOpacityModifyRGB(boolean modify) {
        opacityModifyRGB_ = modify;
        int len = children_.size();
        for (int i = 0; i < len; i++) {
        	CCNode child = children_.get(i);
        	CCRGBAProtocol p = (CCRGBAProtocol)child;
            p.setOpacityModifyRGB(modify);
        }
    }

    public boolean doesOpacityModifyRGB() {
        return opacityModifyRGB_;
    }

    public void setAnchorPoint(CGPoint point) {
        if( ! CGPoint.equalToPoint(point, anchorPoint_) ) {
            super.setAnchorPoint(point);
            createFontChars();
        }
    }
}

