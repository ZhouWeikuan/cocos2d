package org.cocos2d.actions;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.utils.FastFloatBuffer;


/**
 CCProgresstimer is a subclass of CCNode.
 It renders the inner sprite according to the percentage.
 The progress can be Radial, Horizontal or vertical.
 @since v0.99.1
 */
public class CCProgressTimer extends CCNode {

    public static final int kProgressTextureCoordsCount = 4;
    //  kProgressTextureCoords holds points {0,0} {0,1} {1,1} {1,0} we can represent it as bits
    public static final char kProgressTextureCoords = 0x1e;

    /** Types of progress
      @since v0.99.1
      */
    /// Radial Counter-Clockwise 
    public static final int kCCProgressTimerTypeRadialCCW = 0;
    /// Radial ClockWise
    public static final int kCCProgressTimerTypeRadialCW = 1;
    /// Horizontal Left-Right
    public static final int kCCProgressTimerTypeHorizontalBarLR = 2;
    /// Horizontal Right-Left
    public static final int kCCProgressTimerTypeHorizontalBarRL = 3;
    /// Vertical Bottom-top
    public static final int kCCProgressTimerTypeVerticalBarBT = 4;
    /// Vertical Top-Bottom
    public static final int kCCProgressTimerTypeVerticalBarTB = 5;


	//CCProgressTimerType	type_;
    /**	Change the percentage to change progress. */
    // @property (nonatomic, readwrite) CCProgressTimerType type;
    int                 type_;
    public int getType() {
        return type_;
    }

    /** Percentages are from 0 to 100 */
    // @property (nonatomic, readwrite) float percentage;
	float				percentage_;
    public float getPercentage() {
        return percentage_;
    }

    /** The image to show the progress percentage */
    // @property (nonatomic, readwrite, retain) CCSprite *sprite;
	CCSprite			sprite_;
    public CCSprite  getSprite() {
        return sprite_;
    }

    protected FastFloatBuffer textureCoordinates	= null;
    protected FastFloatBuffer vertexCoordinates		= null;
    protected FastFloatBuffer colors				= null;
	protected int		  vertexDataCount_		= 0;
	// ccV2F_C4F_T2F	[]  vertexData_;

	protected void setVertexDataCount(int cnt) {
		vertexDataCount_ = cnt;
		
		textureCoordinates = new FastFloatBuffer(2 * vertexDataCount_);
		vertexCoordinates  = new FastFloatBuffer(2 * vertexDataCount_);
        colors    = new FastFloatBuffer(4 * vertexDataCount_);
	}
	
	protected void resetVertex() {
        if (vertexCoordinates != null) {
        	vertexCoordinates = null;
        	colors 			  = null;
        	textureCoordinates= null;
            vertexDataCount_  = 0;
        }
	}

    /** Creates a progress timer with an image filename as the shape the timer goes through */
    public static CCProgressTimer progress(String filename) {
        return new CCProgressTimer(filename);
    }

    /** Initializes  a progress timer with an image filename as the shape the timer goes through */
    protected CCProgressTimer (String filename) {
        this(CCTextureCache.sharedTextureCache().addImage(filename));
    }

    /** Creates a progress timer with the texture as the shape the timer goes through */
    public static CCProgressTimer progress(CCTexture2D texture) {
        return new CCProgressTimer(texture);
    }

    /** Creates a progress timer with the texture as the shape the timer goes through */
    protected CCProgressTimer (CCTexture2D texture) {
        super();
        sprite_ = CCSprite.sprite(texture);
        percentage_ = 0.f;
        vertexDataCount_ = 0;
        setAnchorPoint(CGPoint.ccp(.5f,.5f));
        setContentSize(sprite_.getContentSize());
        type_ = kCCProgressTimerTypeRadialCCW;
    }

    public void setPercentage(float percentage) {
        if(percentage_ != percentage){
            if(percentage_ < 0.f)
                percentage_ = 0.f;
            else if(percentage > 100.0f)
                percentage_  = 100.f;
            else
                percentage_ = percentage;

            updateProgress();
        }
    }

    public void setSprite(CCSprite newSprite) {
        if(sprite_ != newSprite){
            sprite_ = newSprite;

            //	Everytime we set a new sprite, we free the current vertex data
            this.resetVertex();
        }
    }

    public void setType(int newType) {
        if (newType != type_) {
            //	release all previous information
            this.resetVertex();
            
            type_ = newType;
        }
    }


    ///
    //	@returns the vertex position from the texture coordinate
    ///
    public CGPoint vertexFromTexCoord(CGPoint texCoord) {
    	CCTexture2D texture = sprite_.getTexture();
        if (texture != null) {
        	CGSize size = texture.getContentSize();
            return CGPoint.ccp(size.width * texCoord.x/texture.maxS(),
                    size.height * (1 - (texCoord.y/texture.maxT())));
        } else {
            return CGPoint.zero();
        }
    }

    public void updateColor() {
        ccColor4F color = ccColor4F.ccc4FFromccc3B(sprite_.getColor());
        if (sprite_.getTexture().hasPremultipliedAlpha()) {
            float op = sprite_.getOpacity()/255.f;
            color.r *= op;
            color.g *= op;
            color.b *= op;
            color.a = op;
        } else {
            color.a = sprite_.getOpacity()/255.f;
        }

        if (colors != null){
        	colors.position(0);
            for (int i=0; i < vertexDataCount_; ++i) {
            	colors.put(color.r).put(color.g)
            		  .put(color.b).put(color.a);
            }
            colors.position(0);
        }
    }

    public void updateProgress () {
        switch (type_) {
            case kCCProgressTimerTypeRadialCW:
            case kCCProgressTimerTypeRadialCCW:
                updateRadial();
                break;
            case kCCProgressTimerTypeHorizontalBarLR:
            case kCCProgressTimerTypeHorizontalBarRL:
            case kCCProgressTimerTypeVerticalBarBT:
            case kCCProgressTimerTypeVerticalBarTB:
                updateBar();
                break;
            default:
                break;
        }
    }

    ///
    //	Update does the work of mapping the texture onto the triangles
    //	It now doesn't occur the cost of free/alloc data every update cycle.
    //	It also only changes the percentage point but no other points if they have not
    //	been modified.
    //	
    //	It now deals with flipped texture. If you run into this problem, just use the
    //	sprite property and enable the methods flipX, flipY.
    ///
    public void updateRadial() {		
        //	Texture Max is the actual max coordinates to deal with non-power of 2 textures
        CGPoint tMax = CGPoint.ccp(sprite_.getTexture().maxS(),sprite_.getTexture().maxT());

        //	Grab the midpoint
        CGPoint midpoint = CGPoint.ccpCompMult(getAnchorPoint(), tMax);

        float alpha = percentage_ / 100.f;

        //	Otherwise we can get the angle from the alpha
        float angle = 2.f*((float)Math.PI) * ( type_ == kCCProgressTimerTypeRadialCW? alpha : 1.f - alpha);

        //	We find the vector to do a hit detection based on the percentage
        //	We know the first vector is the one @ 12 o'clock (top,mid) so we rotate 
        //	from that by the progress angle around the midpoint pivot
        CGPoint topMid = CGPoint.ccp(midpoint.x, 0.f);
        CGPoint percentagePt = CGPoint.ccpRotateByAngle(topMid, midpoint, angle);


        int index = 0;
        CGPoint hit = CGPoint.zero();

        if (alpha == 0.f) {
            //	More efficient since we don't always need to check intersection
            //	If the alpha is zero then the hit point is top mid and the index is 0.
            hit = topMid;
            index = 0;
        } else if (alpha == 1.f) {
            //	More efficient since we don't always need to check intersection
            //	If the alpha is one then the hit point is top mid and the index is 4.
            hit = topMid;
            index = 4;
        } else {
            //	We run a for loop checking the edges of the texture to find the
            //	intersection point
            //	We loop through five points since the top is split in half

            float min_t = Float.MAX_VALUE;

            for (int i = 0; i <= kProgressTextureCoordsCount; ++i) {
                int pIndex = (i + (kProgressTextureCoordsCount - 1))%kProgressTextureCoordsCount;

                CGPoint edgePtA = CGPoint.ccpCompMult(boundaryTexCoord((char) (i % kProgressTextureCoordsCount)),tMax);
                CGPoint edgePtB = CGPoint.ccpCompMult(boundaryTexCoord((char) pIndex),tMax);

                //	Remember that the top edge is split in half for the 12 o'clock position
                //	Let's deal with that here by finding the correct endpoints
                if(i == 0){
                    edgePtB = CGPoint.ccpLerp(edgePtA,edgePtB,.5f);
                } else if(i == 4){
                    edgePtA = CGPoint.ccpLerp(edgePtA,edgePtB,.5f);
                }

                //	s and t are returned by ccpLineIntersect
                float s = 0, t = 0;
                CGPoint ret = CGPoint.zero();
                if(CGPoint.ccpLineIntersect(edgePtA, edgePtB, midpoint, percentagePt, ret)) {
                	s = ret.x;
                	t = ret.y;

                    //	Since our hit test is on rays we have to deal with the top edge
                    //	being in split in half so we have to test as a segment
                    if ((i == 0 || i == 4)) {
                        //	s represents the point between edgePtA--edgePtB
                        if (!(0.f <= s && s <= 1.f)) {
                            continue;
                        }
                    }
                    //	As long as our t isn't negative we are at least finding a 
                    //	correct hitpoint from midpoint to percentagePt.
                    if (t >= 0.f) {
                        //	Because the percentage line and all the texture edges are
                        //	rays we should only account for the shortest intersection
                        if (t < min_t) {
                            min_t = t;
                            index = i;
                        }
                    }
                }
            }

            //	Now that we have the minimum magnitude we can use that to find our intersection
            hit = CGPoint.ccpAdd(midpoint, CGPoint.ccpMult(CGPoint.ccpSub(percentagePt, midpoint),min_t));

        }


        //	The size of the vertex data is the index from the hitpoint
        //	the 3 is for the midpoint, 12 o'clock point and hitpoint position.

        boolean sameIndexCount = true;
        if(vertexDataCount_ != index + 3){
            sameIndexCount = false;
            
            this.resetVertex();
        }

        if (this.vertexCoordinates == null) {
            this.setVertexDataCount(index + 3);

            updateColor();
        }

        if (!sameIndexCount) {
        	CGPoint tmpPoint = null;
            //	First we populate the array with the midpoint, then all 
            //	vertices/texcoords/colors of the 12 'o clock start and edges and the hitpoint
            this.textureCoordinates.put(0, midpoint.x);
            this.textureCoordinates.put(1, midpoint.y);
            
            tmpPoint = vertexFromTexCoord(midpoint);
            this.vertexCoordinates.put(0, tmpPoint.x);
            this.vertexCoordinates.put(1, tmpPoint.y);

            this.textureCoordinates.put(2, midpoint.x);
            this.textureCoordinates.put(3, 0.0f);

            tmpPoint = vertexFromTexCoord(CGPoint.ccp(midpoint.x, 0.f));
            this.vertexCoordinates.put(2, tmpPoint.x);
            this.vertexCoordinates.put(3, tmpPoint.y);

            for(int i = 0; i < index; ++i){
                CGPoint texCoords = CGPoint.ccpCompMult(boundaryTexCoord(i), tMax);
                
                this.textureCoordinates.put((i + 2) * 2 + 0, texCoords.x);
                this.textureCoordinates.put((i + 2) * 2 + 1, texCoords.y);

                tmpPoint = vertexFromTexCoord(texCoords);
                this.vertexCoordinates.put((i + 2) * 2 + 0, tmpPoint.x);
                this.vertexCoordinates.put((i + 2) * 2 + 1, tmpPoint.y);
            }

            //	Flip the texture coordinates if set
            if (sprite_.flipY_ || sprite_.flipX_) {
                for(int i = 0; i < vertexDataCount_ - 1; ++i){
                    if (sprite_.flipX_) {
                    	textureCoordinates.put(i*2+0, tMax.x - textureCoordinates.get(i*2+0));
                    }
                    if(sprite_.flipY_){
                    	textureCoordinates.put(i*2+1, tMax.y - textureCoordinates.get(i*2+1));
                    }
                }
            }
        }

        //	hitpoint will go last
        this.textureCoordinates.put((vertexDataCount_ - 1) * 2 + 0, hit.x);
        this.textureCoordinates.put((vertexDataCount_ - 1) * 2 + 1, hit.y);

        CGPoint tmpPoint = vertexFromTexCoord(hit);
        this.vertexCoordinates.put((vertexDataCount_ - 1) * 2 + 0, tmpPoint.x);
        this.vertexCoordinates.put((vertexDataCount_ - 1) * 2 + 1, tmpPoint.y);

        if (sprite_.flipY_ || sprite_.flipX_) {
            if (sprite_.flipX_) {
                textureCoordinates.put((vertexDataCount_ - 1) * 2 + 0, tMax.x - this.textureCoordinates.get((vertexDataCount_ - 1) * 2 + 0));
            }
            if(sprite_.flipY_){
                textureCoordinates.put((vertexDataCount_ - 1) * 2 + 1, tMax.y - this.textureCoordinates.get((vertexDataCount_ - 1) * 2 + 1));
            }
        }
        
        textureCoordinates.position(0);
        vertexCoordinates.position(0);
    }

    ///
    //	Update does the work of mapping the texture onto the triangles for the bar
    //	It now doesn't occur the cost of free/alloc data every update cycle.
    //	It also only changes the percentage point but no other points if they have not
    //	been modified.
    //	
    //	It now deals with flipped texture. If you run into this problem, just use the
    //	sprite property and enable the methods flipX, flipY.
    ///
    public void updateBar() {	

        float alpha = percentage_ / 100.f;

        CGPoint tMax = CGPoint.ccp(sprite_.getTexture().maxS(),sprite_.getTexture().maxT());

        char vIndexes[] = {0,0};

        //	We know vertex data is always equal to the 4 corners
        //	If we don't have vertex data then we create it here and populate
        //	the side of the bar vertices that won't ever change.
        if (this.vertexCoordinates == null) {
            vertexDataCount_ = kProgressTextureCoordsCount;
            this.setVertexDataCount(vertexDataCount_);

            if(type_ == kCCProgressTimerTypeHorizontalBarLR){
            	vIndexes[0] = 0;
            	vIndexes[1] = 1;
            	
            	this.textureCoordinates.put(vIndexes[0]*2 + 0, 0);
            	this.textureCoordinates.put(vIndexes[0]*2 + 1, 0);
            	this.textureCoordinates.put(vIndexes[1]*2 + 0, 0);
            	this.textureCoordinates.put(vIndexes[1]*2 + 1, tMax.y);
            }else if (type_ == kCCProgressTimerTypeHorizontalBarRL) {
            	vIndexes[0] = 2;
            	vIndexes[1] = 3;
            	
            	this.textureCoordinates.put(vIndexes[0]*2 + 0, tMax.x);
            	this.textureCoordinates.put(vIndexes[0]*2 + 1, tMax.y);
            	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x);
            	this.textureCoordinates.put(vIndexes[1]*2 + 1, 0);
            }else if (type_ == kCCProgressTimerTypeVerticalBarBT) {
            	vIndexes[0] = 1;
            	vIndexes[1] = 3;
            	
            	this.textureCoordinates.put(vIndexes[0]*2 + 0, 0);
            	this.textureCoordinates.put(vIndexes[0]*2 + 1, tMax.y);
            	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x);
            	this.textureCoordinates.put(vIndexes[1]*2 + 1, tMax.y);
            }else if (type_ == kCCProgressTimerTypeVerticalBarTB) {
            	vIndexes[0] = 0;
            	vIndexes[1] = 2;
            	
            	this.textureCoordinates.put(vIndexes[0]*2 + 0, 0);
            	this.textureCoordinates.put(vIndexes[0]*2 + 1, 0);
            	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x);
            	this.textureCoordinates.put(vIndexes[1]*2 + 1, 0);
            }

            char index = vIndexes[0];
            CGPoint tmpPoint = vertexFromTexCoord(CGPoint.ccp(this.textureCoordinates.get(index*2+0), this.textureCoordinates.get(index*2+1)));
            this.vertexCoordinates.put(index*2+0, tmpPoint.x);
            this.vertexCoordinates.put(index*2+1, tmpPoint.y);

            index = vIndexes[1];
            tmpPoint = vertexFromTexCoord(CGPoint.ccp(this.textureCoordinates.get(index*2+0), this.textureCoordinates.get(index*2+1)));
            this.vertexCoordinates.put(index*2+0, tmpPoint.x);
            this.vertexCoordinates.put(index*2+1, tmpPoint.y);

            if (sprite_.flipY_ || sprite_.flipX_) {
                if (sprite_.flipX_) {
                    index = vIndexes[0];
                    this.textureCoordinates.put(index*2+0, tMax.x - this.textureCoordinates.get(index*2+0));
                    index = vIndexes[1];
                    this.textureCoordinates.put(index*2+0, tMax.x - this.textureCoordinates.get(index*2+0));
                }
                if(sprite_.flipY_){
                    index = vIndexes[0];
                    this.textureCoordinates.put(index*2+1, tMax.y - this.textureCoordinates.get(index*2+1));

                    index = vIndexes[1];
                    this.textureCoordinates.put(index*2+1, tMax.y - this.textureCoordinates.get(index*2+1));
                }
            }

            updateColor();
        }

        if (type_ == kCCProgressTimerTypeHorizontalBarLR){
        	vIndexes[0] = 3;
        	vIndexes[1] = 2;
        	
        	this.textureCoordinates.put(vIndexes[0]*2 + 0, tMax.x*alpha);
        	this.textureCoordinates.put(vIndexes[0]*2 + 1, tMax.y);
        	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x*alpha);
        	this.textureCoordinates.put(vIndexes[1]*2 + 1, 0);
        } else if (type_ == kCCProgressTimerTypeHorizontalBarRL) {
            vIndexes[0] = 1;
        	vIndexes[1] = 0;
        	
        	this.textureCoordinates.put(vIndexes[0]*2 + 0, tMax.x*(1.f - alpha));
        	this.textureCoordinates.put(vIndexes[0]*2 + 1, 0);
        	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x*(1.f - alpha));
        	this.textureCoordinates.put(vIndexes[1]*2 + 1, tMax.y);
        } else if (type_ == kCCProgressTimerTypeVerticalBarBT) {
            vIndexes[0] = 0;
        	vIndexes[1] = 2;
        	
        	this.textureCoordinates.put(vIndexes[0]*2 + 0, 0);
        	this.textureCoordinates.put(vIndexes[0]*2 + 1, tMax.y*(1.f - alpha));
        	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x);
        	this.textureCoordinates.put(vIndexes[1]*2 + 1, tMax.y*(1.f - alpha));
        } else if (type_ == kCCProgressTimerTypeVerticalBarTB) {
            vIndexes[0] = 1;
        	vIndexes[1] = 3;
        	
        	this.textureCoordinates.put(vIndexes[0]*2 + 0, 0);
        	this.textureCoordinates.put(vIndexes[0]*2 + 1, tMax.y*alpha);
        	this.textureCoordinates.put(vIndexes[1]*2 + 0, tMax.x);
        	this.textureCoordinates.put(vIndexes[1]*2 + 1, tMax.y*alpha);
        }

        char index = vIndexes[0];
        CGPoint tmpPoint = vertexFromTexCoord(CGPoint.ccp(this.textureCoordinates.get(index*2+0), this.textureCoordinates.get(index*2+1)));
        this.vertexCoordinates.put(index*2+0, tmpPoint.x);
        this.vertexCoordinates.put(index*2+1, tmpPoint.y);

        index = vIndexes[1];
        tmpPoint = vertexFromTexCoord(CGPoint.ccp(this.textureCoordinates.get(index*2+0), this.textureCoordinates.get(index*2+1)));
        this.vertexCoordinates.put(index*2+0, tmpPoint.x);
        this.vertexCoordinates.put(index*2+1, tmpPoint.y);
        
        if (sprite_.flipY_ || sprite_.flipX_) {
            if (sprite_.flipX_) {
                index = vIndexes[0];
                this.textureCoordinates.put(index*2+0, tMax.x - this.textureCoordinates.get(index*2+0));

                index = vIndexes[1];
                this.textureCoordinates.put(index*2+0, tMax.x - this.textureCoordinates.get(index*2+0));
            }
            if(sprite_.flipY_){
                index = vIndexes[0];
                this.textureCoordinates.put(index*2+1, tMax.y - this.textureCoordinates.get(index*2+1));

                index = vIndexes[1];
                this.textureCoordinates.put(index*2+1, tMax.y - this.textureCoordinates.get(index*2+1));
            }
        }
        
        this.textureCoordinates.position(0);
        this.vertexCoordinates.position(0);
    }

    public CGPoint boundaryTexCoord(int i) {
        if (i < kProgressTextureCoordsCount) {
            switch (type_) {
                case kCCProgressTimerTypeRadialCW:
                    return CGPoint.ccp((kProgressTextureCoords>>((i<<1)+1))&1,(kProgressTextureCoords>>(i<<1))&1);
                case kCCProgressTimerTypeRadialCCW:
                    return CGPoint.ccp((kProgressTextureCoords>>(7-(i<<1)))&1,(kProgressTextureCoords>>(7-((i<<1)+1)))&1);
                default:
                    break;
            }
        }
        return CGPoint.zero();
    }

    @Override
    public void draw(GL10 gl) {
        if (this.vertexCoordinates == null)
        	return;
        if (sprite_==null)
        	return;
        boolean newBlend = false;
        if( sprite_.getBlendFunc().src != ccConfig.CC_BLEND_SRC || sprite_.getBlendFunc().dst != ccConfig.CC_BLEND_DST ) {
            newBlend = true;
            gl.glBlendFunc( sprite_.getBlendFunc().src, sprite_.getBlendFunc().dst );
        }

        ///	========================================================================
        //	Replaced [texture_ drawAtPoint:CGPointZero] with my own vertexData
        //	Everything above me and below me is copied from CCTextureNode's draw
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, sprite_.getTexture().name());

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, this.vertexCoordinates.bytes);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.textureCoordinates.bytes);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colors.bytes);

        if (type_ == kCCProgressTimerTypeRadialCCW || type_ == kCCProgressTimerTypeRadialCW){
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertexDataCount_);
        } else if (type_ == kCCProgressTimerTypeHorizontalBarLR ||
                type_ == kCCProgressTimerTypeHorizontalBarRL ||
                type_ == kCCProgressTimerTypeVerticalBarBT ||
                type_ == kCCProgressTimerTypeVerticalBarTB) {
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexDataCount_);
        }
        
        //glDrawElements(GL_TRIANGLES, indicesCount_, GL_UNSIGNED_BYTE, indices_);
        ///	========================================================================

        if (newBlend)
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
    }

}

