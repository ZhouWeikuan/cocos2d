package org.cocos2d.actions;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.types.ccTex2F;
import org.cocos2d.types.ccV2F_C4F_T2F;


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

    // private FloatBuffer textureCoordinates;
    // private FloatBuffer vertexCoordinates;
    // private ByteBuffer colors;
	int					vertexDataCount_;
	ccV2F_C4F_T2F	[]  vertexData_;


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
        vertexData_ = null;
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
            if (vertexData_ != null) {
                vertexData_ = null;
                vertexDataCount_ = 0;
            }
        }
    }

    public void setType(int newType) {
        if (newType != type_) {
            //	release all previous information
            if (vertexData_!= null) {
                vertexData_ = null;
                vertexDataCount_ = 0;
            }
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
        if(sprite_.getTexture().hasPremultipliedAlpha()){
            float op = sprite_.getOpacity()/255.f;
            color.r *= op;
            color.g *= op;
            color.b *= op;
            color.a = op;
        } else {
            color.a = sprite_.getOpacity()/255.f;
        }

        if(vertexData_ != null){
            for (int i=0; i < vertexDataCount_; ++i) {
                vertexData_[i].colors = color;
            }
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
            if(vertexData_ != null){
                vertexData_ = null;
                vertexDataCount_ = 0;
            }
        }


        if(vertexData_ == null) {
            vertexDataCount_ = index + 3;
            vertexData_ = new ccV2F_C4F_T2F[vertexDataCount_];
            assert (vertexData_!=null): "CCProgressTimer. Not enough memory";

            updateColor();
        }

        if (!sameIndexCount) {

            //	First we populate the array with the midpoint, then all 
            //	vertices/texcoords/colors of the 12 'o clock start and edges and the hitpoint
            vertexData_[0].texCoords = new ccTex2F(midpoint.x, midpoint.y);
            vertexData_[0].vertices  = (vertexFromTexCoord(midpoint));

            vertexData_[1].texCoords = new ccTex2F(midpoint.x, 0.f);
            vertexData_[1].vertices  = (vertexFromTexCoord(CGPoint.ccp(midpoint.x, 0.f)));

            for(int i = 0; i < index; ++i){
                CGPoint texCoords = CGPoint.ccpCompMult(boundaryTexCoord(i), tMax);

                vertexData_[i+2].texCoords = new ccTex2F(texCoords.x, texCoords.y);
                vertexData_[i+2].vertices  = (vertexFromTexCoord(texCoords));
            }

            //	Flip the texture coordinates if set
            if (sprite_.flipY_ || sprite_.flipX_) {
                for(int i = 0; i < vertexDataCount_ - 1; ++i){
                    if (sprite_.flipX_) {
                        vertexData_[i].texCoords.u = tMax.x - vertexData_[i].texCoords.u;
                    }
                    if(sprite_.flipY_){
                        vertexData_[i].texCoords.v = tMax.y - vertexData_[i].texCoords.v;
                    }
                }
            }
        }

        //	hitpoint will go last
        vertexData_[vertexDataCount_ - 1].texCoords = new ccTex2F(hit.x, hit.y);
        vertexData_[vertexDataCount_ - 1].vertices  = (vertexFromTexCoord(hit));

        if (sprite_.flipY_ || sprite_.flipX_) {
            if (sprite_.flipX_) {
                vertexData_[vertexDataCount_ - 1].texCoords.u = tMax.x - vertexData_[vertexDataCount_ - 1].texCoords.u;
            }
            if(sprite_.flipY_){
                vertexData_[vertexDataCount_ - 1].texCoords.v = tMax.y - vertexData_[vertexDataCount_ - 1].texCoords.v;
            }
        }
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
        if (vertexData_ == null) {
            vertexDataCount_ = kProgressTextureCoordsCount;
            vertexData_ = new ccV2F_C4F_T2F[vertexDataCount_];
            assert vertexData_!=null: "CCProgressTimer. Not enough memory";

            if(type_ == kCCProgressTimerTypeHorizontalBarLR){
                vertexData_[vIndexes[0] = 0].texCoords = new ccTex2F(0,0);
                vertexData_[vIndexes[1] = 1].texCoords = new ccTex2F(0, tMax.y);
            }else if (type_ == kCCProgressTimerTypeHorizontalBarRL) {
                vertexData_[vIndexes[0] = 2].texCoords = new ccTex2F(tMax.x, tMax.y);
                vertexData_[vIndexes[1] = 3].texCoords = new ccTex2F(tMax.x, 0.f);
            }else if (type_ == kCCProgressTimerTypeVerticalBarBT) {
                vertexData_[vIndexes[0] = 1].texCoords = new ccTex2F(0, tMax.y);
                vertexData_[vIndexes[1] = 3].texCoords = new ccTex2F(tMax.x, tMax.y);
            }else if (type_ == kCCProgressTimerTypeVerticalBarTB) {
                vertexData_[vIndexes[0] = 0].texCoords = new ccTex2F(0, 0);
                vertexData_[vIndexes[1] = 2].texCoords = new ccTex2F(tMax.x, 0);
            }

            char index = vIndexes[0];
            vertexData_[index].vertices = vertexFromTexCoord(CGPoint.ccp(vertexData_[index].texCoords.u, vertexData_[index].texCoords.v));

            index = vIndexes[1];
            vertexData_[index].vertices = vertexFromTexCoord(CGPoint.ccp(vertexData_[index].texCoords.u, vertexData_[index].texCoords.v));

            if (sprite_.flipY_ || sprite_.flipX_) {
                if (sprite_.flipX_) {
                    char index1 = vIndexes[0];
                    vertexData_[index1].texCoords.u = tMax.x - vertexData_[index1].texCoords.u;
                    index1 = vIndexes[1];
                    vertexData_[index1].texCoords.u = tMax.x - vertexData_[index1].texCoords.u;
                }
                if(sprite_.flipY_){
                    char index2 = vIndexes[0];
                    vertexData_[index2].texCoords.v = tMax.y - vertexData_[index2].texCoords.v;
                    index2 = vIndexes[1];
                    vertexData_[index2].texCoords.v = tMax.y - vertexData_[index2].texCoords.v;
                }
            }

            updateColor();
        }

        if(type_ == kCCProgressTimerTypeHorizontalBarLR){
            vertexData_[vIndexes[0] = 3].texCoords = new ccTex2F(tMax.x*alpha, tMax.y);
            vertexData_[vIndexes[1] = 2].texCoords = new ccTex2F(tMax.x*alpha, 0);
        }else if (type_ == kCCProgressTimerTypeHorizontalBarRL) {
            vertexData_[vIndexes[0] = 1].texCoords = new ccTex2F(tMax.x*(1.f - alpha), 0);
            vertexData_[vIndexes[1] = 0].texCoords = new ccTex2F(tMax.x*(1.f - alpha), tMax.y);
        }else if (type_ == kCCProgressTimerTypeVerticalBarBT) {
            vertexData_[vIndexes[0] = 0].texCoords = new ccTex2F(0, tMax.y*(1.f - alpha));
            vertexData_[vIndexes[1] = 2].texCoords = new ccTex2F(tMax.x, tMax.y*(1.f - alpha));
        }else if (type_ == kCCProgressTimerTypeVerticalBarTB) {
            vertexData_[vIndexes[0] = 1].texCoords = new ccTex2F(0, tMax.y*alpha);
            vertexData_[vIndexes[1] = 3].texCoords = new ccTex2F(tMax.x, tMax.y*alpha);
        }

        char index = vIndexes[0];
        vertexData_[index].vertices = vertexFromTexCoord(CGPoint.ccp(vertexData_[index].texCoords.u, vertexData_[index].texCoords.v));
        index = vIndexes[1];
        vertexData_[index].vertices = vertexFromTexCoord(CGPoint.ccp(vertexData_[index].texCoords.u, vertexData_[index].texCoords.v));

        if (sprite_.flipY_ || sprite_.flipX_) {
            if (sprite_.flipX_) {
                char index1 = vIndexes[0];
                vertexData_[index1].texCoords.u = tMax.x - vertexData_[index1].texCoords.u;
                index1 = vIndexes[1];
                vertexData_[index1].texCoords.u = tMax.x - vertexData_[index1].texCoords.u;
            }
            if(sprite_.flipY_){
                char index2 = vIndexes[0];
                vertexData_[index2].texCoords.v = tMax.y - vertexData_[index2].texCoords.v;
                index2 = vIndexes[1];
                vertexData_[index2].texCoords.v = tMax.y - vertexData_[index2].texCoords.v;
            }
        }

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
        if(vertexData_ == null)
        	return;
        if(sprite_==null)
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
        /**
         * don't know what to do in these lines yet.
        gl.glVertexPointer(2, GL10.GL_FLOAT, sizeof(ccV2F_C4F_T2F), &vertexData_[0].vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, sizeof(ccV2F_C4F_T2F), &vertexData_[0].texCoords);
        gl.glColorPointer(4, GL10.GL_FLOAT, sizeof(ccV2F_C4F_T2F), &vertexData_[0].colors);
        */
        if(type_ == kCCProgressTimerTypeRadialCCW || type_ == kCCProgressTimerTypeRadialCW){
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertexDataCount_);
        } else if (type_ == kCCProgressTimerTypeHorizontalBarLR ||
                type_ == kCCProgressTimerTypeHorizontalBarRL ||
                type_ == kCCProgressTimerTypeVerticalBarBT ||
                type_ == kCCProgressTimerTypeVerticalBarTB) {
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexDataCount_);
        }
        
        //glDrawElements(GL_TRIANGLES, indicesCount_, GL_UNSIGNED_BYTE, indices_);
        ///	========================================================================

        if( newBlend )
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
    }

}

