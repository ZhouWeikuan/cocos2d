package org.cocos2d.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.CCActionManager;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.grid.CCGridBase;
import org.cocos2d.opengl.CCCamera;
import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.util.CGAffineTransformUtil;
import org.cocos2d.types.util.CGPointUtil;
import org.cocos2d.types.util.PoolHolder;
import org.cocos2d.utils.Util5;
import org.cocos2d.utils.javolution.MathLib;
import org.cocos2d.utils.pool.OneClassPool;

import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

/** CCNode is the main element. 
 Anything thats gets drawn or contains things that get drawn is a CCNode.
 The most popular CCNodes are: CCScene, CCLayer, CCSprite, CCMenu.
 
 The main features of a CCNode are:
 - They can contain other CCNode nodes (addChild, getChildByTag, removeChild, etc)
 - They can schedule periodic callback (schedule, unschedule, etc)
 - They can execute actions (runAction, stopAction, etc)
 
 Some CCNode nodes provide extra functionality for them or their children.
 
 Subclassing a CCNode usually means (one/all) of:
 - overriding init to initialize resources and schedule callbacks
 - create callbacks to handle the advancement of time
 - overriding draw to render the node
 
 Features of CCNode:
 - position
 - scale (x, y)
 - skew (x by degrees, y by degrees)
 - rotation (in degrees, clockwise)
 - CCCamera (an interface to gluLookAt )
 - CCGridBase (to do mesh transformations)
 - anchor point
 - size
 - visible
 - z-order
 - openGL z position
 
 Default values:
  - rotation: 0
  - position: (x=0,y=0)
  - skew: (x=0, y=0)
  - scale: (x=1,y=1)
  - contentSize: (x=0,y=0)
  - anchorPoint: (x=0,y=0)
 
 Limitations:
 - A CCNode is a "void" object. It doesn't have a texture
 
 Order in transformations with grid disabled
 -# The node will be translated (position)
 -# The node will be rotated (rotation)
 -# The node will be skewed (skew)
 -# The node will be scaled (scale)
 -# The node will be moved according to the camera values (camera)
 
 Order in transformations with grid enabled
 -# The node will be translated (position)
 -# The node will be rotated (rotation)
 -# The node will be skewed (skew)
 -# The node will be scaled (scale)
 -# The grid will capture the screen
 -# The node will be moved according to the camera values (camera)
 -# The grid will render the captured screen
 
 Camera:
 - Each node has a camera. By default it points to the center of the CCNode.
*/ 
public class CCNode {
    private static final String LOG_TAG = CCNode.class.getSimpleName();

    // private static final boolean COCOSNODE_DEBUG = false;

    public static final int kCCNodeTagInvalid = -1;

	// rotation angle
    protected float rotation_;

    /** The rotation (angle) of the node in degrees.
     0 is the default rotation angle. Positive values rotate node CW.
    */
    public float getRotation() {
        return rotation_;
    }

    // getters synthesized, setters explicit
    public void setRotation(float rot) {
        rotation_ = rot;
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }
    }

	// scaling factors
    protected float scaleX_;
    protected float scaleY_;
    

    /** The scale factor of the node. 
       1.0 is the default scale factor. It only modifies the X scale factor.
    */
    public float getScaleX() {
        return scaleX_;
    }

    public void setScaleX(float sx) {
        scaleX_ = sx;
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }
    }

    /** The scale factor of the node. 
        1.0 is the default scale factor. It only modifies the Y scale factor.
    */
    public float getScaleY() {
        return scaleY_;
    }

    public void setScaleY(float sy) {
        scaleY_ = sy;
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }	
    }

    /** The scale factor of the node.
        1.0 is the default scale factor. It modifies the X and Y scale at the same time.
    */
    public void setScale(float s) {
        scaleX_ = scaleY_ = s;
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }
    }

    public float getScale() {
        if (scaleX_ == scaleY_) {
            return scaleX_;
        } else {
            Log.w(LOG_TAG, "CCNode#scale. ScaleX != ScaleY. Don't know which one to return");
        }
        return 0;
    }
    
    // skewing factors
    
	private float skewX_;
	private float skewY_;
    
    /** The horizontal skew factor of the node.
     *  0.0 is the default skew factor.
     */
    public void setSkewX(float s) {
    	skewX_ = s;
    	isTransformDirty_ = isInverseDirty_ = true;
    	if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
    		isTransformGLDirty_ = true;
    	}
    }
    
    public float getSkewX() {
    	return skewX_;
    }
    
    /** The vertical skew factor of the node.
     *  0.0 is the default skew factor.
     */
    public void setSkewY(float s) {
    	skewY_ = s;
    	isTransformDirty_ = isInverseDirty_ = true;
    	if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
    		isTransformGLDirty_ = true;
    	}
    }
    
    public float getSkewY() {
    	return skewY_;
    }
    
	// anchor point in pixels
	protected CGPoint anchorPointInPixels_;	

    /** The anchorPoint in absolute pixels.
      Since v0.8 you can only read it. If you wish to modify it, use anchorPoint instead
    */
    public CGPoint getAnchorPointInPixels() {
        return CGPoint.make(anchorPointInPixels_.x, anchorPointInPixels_.y);
    }
    
	// If YES the transformtions will be relative to (-transform.x, -transform.y).
	// Sprites, Labels and any other "small" object uses it.
	// Scenes, Layers and other "whole screen" object don't use it.
    private boolean isRelativeAnchorPoint_;

    /** If YES the transformtions will be relative to it's anchor point.
       Sprites, Labels and any other sizeble object use it have it enabled by default.
       Scenes, Layers and other "whole screen" object don't use it, have it disabled by default.
    */
    public void setRelativeAnchorPoint(boolean newValue) {
        isRelativeAnchorPoint_ = newValue;
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }
    }

    public boolean getRelativeAnchorPoint() {
        return isRelativeAnchorPoint_;
    }

	// anchor point normalized
    protected CGPoint anchorPoint_;

	// untransformed size of the node
    protected CGSize contentSize_;

    /** The untransformed size of the node.
     The contentSize remains the same no matter the node is scaled or rotated.
     All nodes has a size. Layer and Scene has the same size of the screen.
     @since v0.8
     */
    public void setContentSize(CGSize size) {
    	setContentSize(size.width, size.height);
    }
	
	public void setContentSize(float w, float h) {
        if ( !(contentSize_.width == w && contentSize_.height == h) ) {
            contentSize_.set(w, h);// = CGSize.make(size.width, size.height);
            anchorPointInPixels_.set(contentSize_.width * anchorPoint_.x,
                                              contentSize_.height * anchorPoint_.y);
            isTransformDirty_ = isInverseDirty_ = true;
            if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
                isTransformGLDirty_ = true;
            }

        }
    }
	
    public CGSize getContentSize() {
        return CGSize.make(contentSize_.width, contentSize_.height);
    }
    
    public CGSize getContentSizeRef() {
        return contentSize_;
    }

    /** anchorPoint is the point around which all transformations and positioning manipulations take place.
      It's like a pin in the node where it is "attached" to its parent.
      The anchorPoint is normalized, like a percentage.
        (0,0) means the bottom-left corner and (1,1) means the top-right corner.
      But you can use values higher than (1,1) and lower than (0,0) too.
      The default anchorPoint is (0.5,0.5), so it starts in the center of the node.
      @since v0.8
    */
    public void setAnchorPoint(CGPoint pnt) {
    	setAnchorPoint(pnt.x, pnt.y);
    }
    
    public void setAnchorPoint(float x, float y) {
        if (!(x == anchorPoint_.x && y == anchorPoint_.y)) {
            anchorPoint_.set(x, y);// = CGPoint.make(pnt.x, pnt.y);
            anchorPointInPixels_.set(contentSize_.width * anchorPoint_.x,// = CGPoint.ccp(contentSize_.width * anchorPoint_.x, 
            						contentSize_.height * anchorPoint_.y);//   contentSize_.height * anchorPoint_.y);

            isTransformDirty_ = isInverseDirty_ = true;
            if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
                isTransformGLDirty_ = true;
            }
        }
    }
   
    public CGPoint getAnchorPoint() {
        return CGPoint.make(anchorPoint_.x, anchorPoint_.y);
    }
    
    public CGPoint getAnchorPointRef() {
        return anchorPoint_;
    }
    
    // #if	CC_NODE_TRANSFORM_USING_AFFINE_MATRIX
	private float []	transformGL_; // [16];
	
    // #endif
    private CGAffineTransform transform_, inverse_;

	// To reduce memory, place BOOLs that are not properties here:
    private boolean isTransformDirty_;
    private boolean isInverseDirty_;

    //#if	CC_NODE_TRANSFORM_USING_AFFINE_MATRIX
	private boolean isTransformGLDirty_;
    //#endif

    /** returns a "local" axis aligned bounding box of the node.
      The returned box is relative only to its parent.
      @since v0.8.2
    */
    public CGRect getBoundingBox() {
        CGRect rect = CGRect.make(0, 0, contentSize_.width, contentSize_.height);
        return CGRect.applyAffineTransform(rect, nodeToParentTransform());
    }

	// position of the node
    protected CGPoint position_;

    /** Position (x,y) of the node in OpenGL coordinates.
      (0,0) is the left-bottom corner.
    */
    public CGPoint getPosition() {
        return CGPoint.make(position_.x, position_.y);
    }

    public CGPoint getPositionRef() {
        return position_;
    }
    
    public void setPosition(CGPoint pnt) {
    	setPosition(pnt.x, pnt.y);
    }
    
	public void setPosition(float x, float y) {
        position_.set(x, y);// = CGPoint.make(pnt.x, pnt.y);
        isTransformDirty_ = isInverseDirty_ = true;
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            isTransformGLDirty_ = true;
        }
    }	
	
    /** A CCCamera object that lets you move the node using a gluLookAt */
    private CCCamera camera_;

    // camera: lazy alloc
    public CCCamera getCamera() {
        if (camera_ == null)
            camera_ = new CCCamera();

		// by default, center camera at the Sprite's anchor point
		//		[camera_ setCenterX:anchorPointInPixels_.x centerY:anchorPointInPixels_.y centerZ:0];
		//		[camera_ setEyeX:anchorPointInPixels_.x eyeY:anchorPointInPixels_.y eyeZ:1];
		
		//		[camera_ setCenterX:0 centerY:0 centerZ:0];
		//		[camera_ setEyeX:0 eyeY:0 eyeZ:1];
	
        return camera_;
    }

	// a Grid
    protected CCGridBase grid_;

    /** A CCGrid object that is used when applying effects */
    public CCGridBase getGrid() {
        return grid_;
    }

    public void setGrid(CCGridBase grid) {
        this.grid_ = grid;
    }

	// is visible
    protected boolean visible_;

    /** Whether of not the node is visible. Default is YES */
    public boolean getVisible() {
        return visible_;
    }

    public void setVisible(boolean visible) {
        this.visible_ = visible;
    }

	// weakref to parent
    protected CCNode parent_;

    /** A weak reference to the parent */
    public CCNode getParent() {
        return parent_;
    }

    public void setParent(CCNode parent) {
        parent_ = parent;
    }

	// a tag. any number you want to assign to the node
    private int tag_;

    /** A tag used to identify the node easily */
    public int getTag() {
        return tag_;
    }

    public void setTag(int tag) {
        tag_ = tag;
    }

	// openGL real Z vertex
    protected float vertexZ_;

    /** The real openGL Z vertex.
     Differences between openGL Z vertex and cocos2d Z order:
       - OpenGL Z modifies the Z vertex, and not the Z order in the relation between parent-children
       - OpenGL Z might require to set 2D projection
       - cocos2d Z order works OK if all the nodes uses the same openGL Z vertex. eg: vertexZ = 0
     @warning: Use it at your own risk since it might break the cocos2d parent-children z order
     @since v0.8
     */
    public float getVertexZ() {
        return vertexZ_;
    }

    public void setVertexZ(float z) {
        vertexZ_ = z;
    }


	// z-order value
    private int zOrder_;

    /** The z order of the node relative to it's "brothers": children of the same parent */
    public int getZOrder() {
        return zOrder_;
    }

    // used internally to alter the zOrder variable. DON'T call this method manually
    private void _setZOrder(int z) {
        zOrder_ = z;
    }

	// array of children
    protected List<CCNode> children_;

    public List<CCNode> getChildren() {
        return children_;
    }

    // user data field
    private Object userData;

    /** A custom user data pointer */
    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object data) {
        userData = data;
    }

	// Is running
    private boolean isRunning_;

    /** whether or not the node is running */
    public boolean isRunning() {
        return isRunning_;
    }

    // initializators
    /** allocates and initializes a node.
      The node will be created as "autorelease".
    */
    public static CCNode node() {
        return new CCNode();
    }

    /** initializes the node */
    protected CCNode() {
    	transformGL_ = new float[16];
    	
        isRunning_ = false;

        rotation_ = 0.0f;
        scaleX_ = scaleY_ = 1.0f;
        skewX_ = skewY_ = 0.0f;
        position_ = CGPoint.ccp(0, 0);

        transform_ = CGAffineTransform.identity();
        inverse_ = CGAffineTransform.identity();
        
        // "whole screen" objects. like Scenes and Layers, should set relativeAnchorPoint to false        
        isRelativeAnchorPoint_ = true;

		anchorPointInPixels_ = CGPoint.ccp(0,0);
        anchorPoint_ = CGPoint.ccp(0,0);
        contentSize_ = CGSize.zero();

        isTransformDirty_ = isInverseDirty_ = true;
	
        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
		    isTransformGLDirty_ = true;
        }	
		
		zOrder_ = 0;
        vertexZ_ = 0;
        grid_ = null;
        visible_ = true;
		tag_ = kCCNodeTagInvalid;

		// lazy alloc
        camera_ = null;
        
		// children (lazy allocs)
        children_ = null;

		// userData is always inited as nil
        userData = null;
    }


    /** Adds a child to the container with z order and tag
      It returns self, so you can chain several addChilds.
      @since v0.7.1
    */
    /* "add" logic MUST only be on this method
     * If a class want's to extend the 'addChild' behaviour it only needs
     * to override this method
     */
    public CCNode addChild(CCNode child, int z, int tag) {
	    assert child != null : "Argument must be non-nil";
	    assert child.parent_ == null: "child already added. It can't be added again";

        if (children_ == null)
            childrenAlloc();

        insertChild(child, z);
        child.tag_ = tag;
        child.setParent(this);
        if (isRunning_) {
            child.onEnter();
        }
        return this;
    }

    /** Adds a child to the container with a z-order
      It returns self, so you can chain several addChilds.
      @since v0.7.1
    */
    public CCNode addChild(CCNode child, int z) {
        assert child != null: "Argument must be non-nil" ;

        return addChild(child, z, child.tag_);
    }

    /** Adds a child to the container with z-order as 0.
      It returns self, so you can chain several addChilds.
      @since v0.7.1
    */
    public CCNode addChild(CCNode child) {
        assert child != null: "Argument must be non-nil" ;

        return addChild(child, child.zOrder_, child.tag_);
    }

    /** Remove itself from its parent node.
      If cleanup is YES, then also remove all actions and callbacks.
      If the node orphan, then nothing happens.
      @since v0.99.3
    */
    public void removeFromParentAndCleanup(boolean cleanup) {
        if (this.parent_ != null) {
            this.parent_.removeChild(this, cleanup);
        }
    }
    
    /**
     * Remove myself from the parent, for action CCCallFunc
     */
    public void removeSelf() {
    	this.removeFromParentAndCleanup(true);
    }

    /** Removes a child from the container.
       It will also cleanup all running actions depending on the cleanup parameter.
      @since v0.7.1
    */
    /* "remove" logic MUST only be on this method
     * If a class want's to extend the 'removeChild' behavior it only needs
     * to override this method
     */
    public void removeChild(CCNode child, boolean cleanup) {
        // explicit nil handling
        if (child == null)
            return;

        if (children_.contains(child))
            detachChild(child, cleanup);
    }

    /** Removes a child from the container by tag value.
      It will also cleanup all running actions depending on the cleanup parameter
      @since v0.7.1
    */
    public void removeChildByTag(int tag, boolean cleanup) {
	    assert tag != kCCNodeTagInvalid: "Invalid tag";

        CCNode child = getChildByTag(tag);
        if (child == null)
            Log.w(LOG_TAG, "removeChild: child not found");
        else
            removeChild(child, cleanup);
    }

    /** Removes all children from the container and do a cleanup all running actions
                   depending on the cleanup parameter.
      @since v0.7.1
    */
    public void removeAllChildren(boolean cleanup) {
	    // not using detachChild improves speed here
    	if (children_ == null)
    		return;
    	
    	for (int i=0; i<children_.size(); ++i) {
    		CCNode child = children_.get(i);
    		if (isRunning_)
    			child.onExit();

    		if (cleanup)
    			child.cleanup();

    		child.setParent(null);
    	}
    	children_.clear();

    }

    /** Gets a child from the container given its tag
      @return returns a CCNode object
      @since v0.7.1
    */
    public CCNode getChildByTag(int tag) {
        assert tag != kCCNodeTagInvalid : "Invalid tag_";

        if (children_ != null)
            for (int i=0; i<children_.size(); ++i) {
            	CCNode node = children_.get(i);
                if (node.tag_ == tag) {
                    return node;
                }
            }

        return null;
    }

    private void detachChild(CCNode child, boolean doCleanup) {
        // IMPORTANT:
        //  -1st do onExit
        //  -2nd cleanup
        if (isRunning_)
            child.onExit();

        // If you don't do cleanup, the child's actions will not get removed and the
        // its scheduledSelectors_ dict will not get released!
        if (doCleanup)
            child.cleanup();

	    // set parent nil at the end (issue #476)
        child.setParent(null);

        children_.remove(child);
    }

    /** Reorders a child according to a new z value.
        The child MUST be already added.
    */
    public void reorderChild(CCNode child, int zOrder) {
        assert child != null : "Child must be non-null";
        children_.remove(child);
        this.insertChild(child, zOrder);
    }

    /** Override this method to draw your own node.
      The following GL states will be enabled by default:
      - glEnableClientState(GL_VERTEX_ARRAY);
      - glEnableClientState(GL_COLOR_ARRAY);
      - glEnableClientState(GL_TEXTURE_COORD_ARRAY);
      - glEnable(GL_TEXTURE_2D);

      AND YOU SHOULD NOT DISABLE THEM AFTER DRAWING YOUR NODE

      But if you enable any other GL state, you should disable it after drawing your node.
    */
    public void draw(GL10 gl) {
        // override me
        // Only use this function to draw your staff.
        // DON'T draw your stuff outside this method
    }

    /**
      recursive method that visit its children and draw them
    */
    public void visit(GL10 gl) {
	    // quick return if not visible
        if (!visible_)
            return;

        gl.glPushMatrix();

        if (grid_ != null && grid_.isActive()) {
            grid_.beforeDraw(gl);
            transformAncestors(gl);
        }

        transform(gl);

        if (children_ != null) {
        	for (int i=0; i<children_.size(); ++i) {
        		CCNode child = children_.get(i);
        		if (child.zOrder_ < 0) {
        			child.visit(gl);
        		} else
        			break;
        	}
        }

        draw(gl);

        if (children_ != null) {
        	for (int i=0; i<children_.size(); ++i) {
        		CCNode child = children_.get(i);
        		if (child.zOrder_ >= 0) {
        			child.visit(gl);
        		}
        	}
        }


        if (grid_ != null && grid_.isActive()) {
            grid_.afterDraw(gl, this);
        }

        gl.glPopMatrix();
    }

    /**
     * performs OpenGL view-matrix transformation based on position, scale, rotation and other attributes.
    */
    public void transform(GL10 gl) {	
        // transformations

        if ( ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX ) {
            // BEGIN alternative -- using cached transform
            //
            if( isTransformGLDirty_ ) {
                CGAffineTransform t = nodeToParentTransform();
                CGAffineTransform.CGAffineToGL(t, transformGL_);
                isTransformGLDirty_ = false;
            }

            // gl.glMultMatrixf(transformGL_, transformGL_.length);
            gl.glMultMatrixf(transformGL_, 0);
            
            if( vertexZ_ != 0)
                gl.glTranslatef(0, 0, vertexZ_);

            // XXX: Expensive calls. Camera should be integrated into the cached affine matrix
            if ( camera_ != null && !(grid_ != null && grid_.isActive() ) ) {
                boolean translate = (anchorPointInPixels_.x != 0.0f || anchorPointInPixels_.y != 0.0f);

                if( translate )
                    gl.glTranslatef(RENDER_IN_SUBPIXEL(anchorPointInPixels_.x), RENDER_IN_SUBPIXEL(anchorPointInPixels_.y), 0);

                camera_.locate(gl);

                if( translate )
                    gl.glTranslatef(RENDER_IN_SUBPIXEL(-anchorPointInPixels_.x), RENDER_IN_SUBPIXEL(-anchorPointInPixels_.y), 0);
            }


            // END alternative

        } else {
            // BEGIN original implementation
            // 
            // translate
            if ( isRelativeAnchorPoint_ && (anchorPointInPixels_.x != 0 || anchorPointInPixels_.y != 0 ) )
                gl.glTranslatef( RENDER_IN_SUBPIXEL(-anchorPointInPixels_.x), RENDER_IN_SUBPIXEL(-anchorPointInPixels_.y), 0);

            if (anchorPointInPixels_.x != 0 || anchorPointInPixels_.y != 0)
                gl.glTranslatef( RENDER_IN_SUBPIXEL(position_.x + anchorPointInPixels_.x), RENDER_IN_SUBPIXEL(position_.y + anchorPointInPixels_.y), vertexZ_);
            else if ( position_.x !=0 || position_.y !=0 || vertexZ_ != 0)
                gl.glTranslatef( RENDER_IN_SUBPIXEL(position_.x), RENDER_IN_SUBPIXEL(position_.y), vertexZ_ );

            // rotate
            if (rotation_ != 0.0f )
                gl.glRotatef( -rotation_, 0.0f, 0.0f, 1.0f );

            // scale
            if (scaleX_ != 1.0f || scaleY_ != 1.0f)
                gl.glScalef( scaleX_, scaleY_, 1.0f );

            if ( camera_!=null && !(grid_!=null && grid_.isActive()) )
                camera_.locate(gl);

            // restore and re-position point
            if (anchorPointInPixels_.x != 0.0f || anchorPointInPixels_.y != 0.0f)
                gl.glTranslatef(RENDER_IN_SUBPIXEL(-anchorPointInPixels_.x), RENDER_IN_SUBPIXEL(-anchorPointInPixels_.y), 0);

            //
            // END original implementation
        }
    }


    /** performs OpenGL view-matrix transformation of it's ancestors.
      Generally the ancestors are already transformed, but in certain cases (eg: attaching a FBO)
      it's necessary to transform the ancestors again.
      @since v0.7.2
    */
    public void transformAncestors(GL10 gl) {
        if (parent_ != null) {
            parent_.transformAncestors(gl);
            parent_.transform(gl);
        }
    }

    /** Executes an action, and returns the action that is executed.
      The node becomes the action's target.
      @warning Starting from v0.8 actions don't retain their target anymore.
      @since v0.7.1
      @return An Action pointer
    */
    public CCAction runAction(CCAction action) {
        assert action != null : "Argument must be non-null";

        CCActionManager.sharedManager().addAction(action, this, !isRunning_);
        return action;
    }

    /** Removes all actions from the running action list */
    public void stopAllActions() {
        CCActionManager.sharedManager().removeAllActions(this);
    }

    /** Removes an action from the running action list */
    public void stopAction(CCAction action) {
        CCActionManager.sharedManager().removeAction(action);
    }

    /** Removes an action from the running action list given its tag
      @since v0.7.1
    */
    public void stopAction(int tag) {    	
        assert tag != CCAction.kCCActionTagInvalid : "Invalid tag_";
        CCActionManager.sharedManager().removeAction(tag, this);
    }

    /** Gets an action from the running action list given its tag
      @since v0.7.1
      @return the Action the with the given tag
    */
    public CCAction getAction(int tag) {
        assert tag != CCAction.kCCActionTagInvalid : "Invalid tag_";

        return CCActionManager.sharedManager().getAction(tag, this);
    }

    /** Returns the numbers of actions that are running plus the ones that are schedule to run
               (actions in actionsToAdd and actions arrays). 
     * Composable actions are counted as 1 action. Example:
     *    If you are running 1 Sequence of 7 actions, it will return 1.
     *    If you are running 7 Sequences of 2 actions, it will return 7.
    */
    public int numberOfRunningActions() {
        return CCActionManager.sharedManager().numberOfRunningActions(this);
    }

    /** schedules the "update" method.
      It will use the order number 0. This method will be called every frame.
      Scheduled methods with a lower order value will be called
            before the ones that have a higher order value.
      Only one "udpate" method could be scheduled per node.

      @since v0.99.3
    */
    public void scheduleUpdate() {
        this.scheduleUpdate(0);
    }

    /** schedules the "update" selector with a custom priority. This selector will be called every frame.
      Scheduled selectors with a lower priority will be called before the ones that have a higher value.
      Only one "update" selector could be scheduled per node (You can't have 2 'update' selectors).

      @since v0.99.3
    */
    public void scheduleUpdate(int priority) {
        CCScheduler.sharedScheduler().scheduleUpdate(this, priority, !isRunning_);
    }

    /** unschedules the "update" method.

       @since v0.99.3
    */
    public void unscheduleUpdate() {
        CCScheduler.sharedScheduler().unscheduleUpdate(this);
    }

    /** schedules a selector.
      The scheduled selector will be ticked every frame
    */
    public void schedule(String selector) {
        schedule(selector, 0);
    }

    /** schedules a custom selector with an interval time in seconds.
      If time is 0 it will be ticked every frame.
      If time is 0, it is recommended to use 'scheduleUpdate' instead.
    */
    public void schedule(String selector, float interval) {
        assert selector != null : "Argument selector must be non-null";
        assert interval >= 0 : "Argument interval must be positive";
        
        CCScheduler.sharedScheduler().schedule(selector, this, interval, !isRunning_);
    }
    
    /*
     * schedules a selector.
     * The scheduled callback will be ticked every frame.
     * 
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation.  
     */
    public void schedule(UpdateCallback callback) {
        schedule(callback, 0);
    }

    /*
     * schedules a custom callback with an interval time in seconds.
     * If time is 0 it will be ticked every frame.
     * If time is 0, it is recommended to use 'scheduleUpdate' instead.
     * 
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation.  
     */
    public void schedule(UpdateCallback callback, float interval) {
        assert callback != null : "Argument callback must be non-null";
        assert interval >= 0 : "Argument interval must be positive";
        
        CCScheduler.sharedScheduler().schedule(callback, this, interval, !isRunning_);
    }
    
    /* unschedules a custom selector.*/
    public void unschedule(String selector) {
        // explicit null handling
        if (selector == null)
            return;

        CCScheduler.sharedScheduler().unschedule(selector, this);
    }
    
    /*
     * unschedules a custom callback.
     * 
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation.
     */
    public void unschedule(UpdateCallback callback) {
        // explicit null handling
        if (callback == null)
            return;

        CCScheduler.sharedScheduler().unschedule(callback, this);
    }

    /** unschedule all scheduled selectors: custom selectors, and the 'update' selector.
      Actions are not affected by this method.
      @since v0.99.3
      */
    public void unscheduleAllSelectors() {
        CCScheduler.sharedScheduler().unscheduleAllSelectors(this);
    }

    /** resumes all scheduled selectors and actions.
      Called internally by onEnter
      */
    public void resumeSchedulerAndActions() {
	    CCScheduler.sharedScheduler().resume(this);
	    CCActionManager.sharedManager().resume(this);
    }

    /** pauses all scheduled selectors and actions.
      Called internally by onExit
      */
    public void pauseSchedulerAndActions() {
    	CCScheduler.sharedScheduler().pause(this);
    	CCActionManager.sharedManager().pause(this);
    }

    // CocosNode Transform
    /** Returns the local affine transform matrix
      @since v0.7.1
    */
    private CGAffineTransform nodeToParentTransform() {
        if (isTransformDirty_) {
        	CGPoint zero = CGPoint.getZero();
            transform_.setToIdentity();

            if (!isRelativeAnchorPoint_ && !CGPoint.equalToPoint(anchorPointInPixels_, zero)) {
            	transform_.translate(anchorPointInPixels_.x, anchorPointInPixels_.y);
            }

            if (!CGPoint.equalToPoint(position_, zero))
            	transform_.translate(position_.x, position_.y);
            
            if (rotation_ != 0)
            	transform_.rotate(-ccMacros.CC_DEGREES_TO_RADIANS(rotation_));
            
            if (skewX_ != 0 || skewY_ != 0) {
            	/** create a skewed coordinate system */
            	CGAffineTransform skew = CGAffineTransform.make(1.0f, MathLib.tan(ccMacros.CC_DEGREES_TO_RADIANS(skewY_)), MathLib.tan(ccMacros.CC_DEGREES_TO_RADIANS(skewX_)), 1.0f, 0.0f, 0.0f);
            	/** apply the skew to the transform */
            	transform_ = transform_.getTransformConcat(skew);
            }
            
            if( ! (scaleX_ == 1 && scaleY_ == 1) ) 
            	transform_.scale(scaleX_, scaleY_);
           
            if (!CGPoint.equalToPoint(anchorPointInPixels_, zero))
            	transform_.translate(-anchorPointInPixels_.x, -anchorPointInPixels_.y);

            isTransformDirty_ = false;
        }

        return transform_;
    }
 
    /** Returns the inverse local affine transform matrix
      @since v0.7.1
    */
    public CGAffineTransform parentToNodeTransform() {
        if (isInverseDirty_) {
            CGAffineTransformUtil.inverse(nodeToParentTransform(), inverse_);

            isInverseDirty_ = false;
        }

        return inverse_;
    }

    /** Retrusn the world affine transform matrix
      @since v0.7.1
    */
    private CGAffineTransform nodeToWorldTransform() {
        CGAffineTransform t = new CGAffineTransform(nodeToParentTransform());

        for (CCNode p = parent_; p != null; p = p.parent_) {
            // t = t.getTransformConcat(p.nodeToParentTransform());
            t = t.preConcatenate(p.nodeToParentTransform());
        }

        return t;
    }
    
    private void nodeToWorldTransform(CGAffineTransform ret) {
        ret.setTransform(nodeToParentTransform());

        for (CCNode p = parent_; p != null; p = p.parent_) {
        	CGAffineTransformUtil.preConcate(ret, p.nodeToParentTransform());
        }
    }
    
    /** Returns the inverse world affine transform matrix
      @since v0.7.1
    */
    public CGAffineTransform worldToNodeTransform() {
        return nodeToWorldTransform().getTransformInvert();
    }
    
    /**
     * This is analog method, result is written to ret. No garbage.
     */
    private void worldToNodeTransform(CGAffineTransform ret) {
    	nodeToWorldTransform(ret);
        CGAffineTransformUtil.inverse(ret);
    }

    /** converts a world coordinate to local coordinate
      @since v0.7.1
    */
    public CGPoint convertToNodeSpace(float x, float y) {
        OneClassPool<CGAffineTransform> pool = PoolHolder.getInstance().getCGAffineTransformPool();
        
        CGAffineTransform temp = pool.get();
        worldToNodeTransform(temp);
        
        CGPoint ret = new CGPoint();
    	CGPointUtil.applyAffineTransform(x, y, temp, ret);
    	
        pool.free(temp);
        return ret;
    }
    
    /** converts a world coordinate to local coordinate
      @since v0.7.1
    */
    public CGPoint convertToNodeSpace(CGPoint p) {
    	return convertToNodeSpace(p.x, p.y);
    }
    
    /**
     * This is analog method, result is written to ret. No garbage.
     */
    public void convertToNodeSpace(CGPoint p, CGPoint ret) {
    	convertToNodeSpace(p.x, p.y, ret);
    }
    
    /**
     * This is analog method, result is written to ret. No garbage.
     */
    public void convertToNodeSpace(float x, float y, CGPoint ret) {
        OneClassPool<CGAffineTransform> pool = PoolHolder.getInstance().getCGAffineTransformPool();
        
        CGAffineTransform temp = pool.get();
        worldToNodeTransform(temp);
        
        CGPointUtil.applyAffineTransform(x, y, temp, ret);
        
        pool.free(temp);
    }

    /** converts local coordinate to world space
      @since v0.7.1
    */
    public CGPoint convertToWorldSpace(float x, float y) {
        CGPoint nodePoint = CGPoint.make(x, y);
        return CGPoint.applyAffineTransform(nodePoint, nodeToWorldTransform());
    }
    
    /**
     * This is analog method, result is written to ret. No garbage.
     */
    public void convertToWorldSpace(float x, float y , CGPoint ret) {
        OneClassPool<CGAffineTransform> pool = PoolHolder.getInstance().getCGAffineTransformPool();
        
        CGAffineTransform temp = pool.get();
        nodeToWorldTransform(temp);
        
        CGPointUtil.applyAffineTransform(x, y, temp, ret);
        
        pool.free(temp);
    }
    
    /** converts a world coordinate to local coordinate
      treating the returned/received node point as anchor relative
      @since v0.7.1
    */
    public CGPoint convertToNodeSpaceAR(float x, float y) {
        CGPoint nodePoint = convertToNodeSpace(x, y);
        return CGPoint.ccpSub(nodePoint, anchorPointInPixels_);
    }

    /** converts local coordinate to world space
      treating the returned/received node point as anchor relative
      @since v0.7.1
    */
    public CGPoint convertToWorldSpaceAR(float x, float y) {
        CGPoint nodePoint = CGPoint.make(x, y);
        nodePoint = CGPoint.ccpAdd(nodePoint, anchorPointInPixels_);
        return convertToWorldSpace(nodePoint.x, nodePoint.y);
    }

    // convenience methods which take a MotionEvent instead of PointF
    /** convenience methods which take a UITouch instead of CGPoint
      @since v0.7.1
    */
    public CGPoint convertTouchToNodeSpace(MotionEvent event) {
    	OneClassPool<CGPoint> pool = PoolHolder.getInstance().getCGPointPool();
    	CGPoint point = pool.get();
    	
    	int action = event.getAction();
		int pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        if(Build.VERSION.SDK_INT >= 5) {
        	CCDirector.sharedDirector().convertToGL(Util5.getX(event, pid), Util5.getY(event, pid), point);
        } else {
        	CCDirector.sharedDirector().convertToGL(event.getX(), event.getY(), point);
        }
    	
    	float x = point.x, y = point.y;
    	pool.free(point);
    	
        return convertToNodeSpace(x, y);
    }
    
    /**
     * This is analog method, result is written to ret. No garbage.
     */
    public void convertTouchToNodeSpace(MotionEvent event, CGPoint ret) {
    	int action = event.getAction();
		int pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        if(Build.VERSION.SDK_INT >= 5) {
        	CCDirector.sharedDirector().convertToGL(Util5.getX(event, pid), Util5.getY(event, pid), ret);
        } else {
        	CCDirector.sharedDirector().convertToGL(event.getX(), event.getY(), ret);
        }
    	
        convertToNodeSpace(ret.x, ret.y, ret);
    }

    /** converts a UITouch (world coordinates) into a local coordiante. This method is AR (Anchor Relative).
      @since v0.7.1
    */
    public CGPoint convertTouchToNodeSpaceAR(MotionEvent event) {
    	OneClassPool<CGPoint> pool = PoolHolder.getInstance().getCGPointPool();
    	CGPoint point = pool.get();
    	
    	int action = event.getAction();
		int pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        if(Build.VERSION.SDK_INT >= 5) {
        	CCDirector.sharedDirector().convertToGL(Util5.getX(event, pid), Util5.getY(event, pid), point);
        } else {
        	CCDirector.sharedDirector().convertToGL(event.getX(), event.getY(), point);
        }
    	
    	float x = point.x, y = point.y;
    	pool.free(point);
    	
        return convertToNodeSpaceAR(x, y);
    }

    public CGPoint convertToWindowSpace(CGPoint nodePoint) {
        CGPoint worldPoint = convertToWorldSpace(nodePoint.x, nodePoint.y);
        return CCDirector.sharedDirector().convertToUI(worldPoint);
    }

    public interface CocosNodeSize {
        public float getWidth();

        public float getHeight();
    }

    // lazy allocs
    private void childrenAlloc() {
        children_ = Collections.synchronizedList(new ArrayList<CCNode>(4));
    }

    private static Comparator<CCNode> zOrderComparator = new Comparator<CCNode>() {

		@Override
		public int compare(CCNode o1, CCNode o2) {
			return o1.zOrder_ - o2.zOrder_;
		}
	};
	
    // helper that reorder a child
    private void insertChild(CCNode node, int z) {
    	node._setZOrder(z);
    	int ind = Collections.binarySearch(children_, node, zOrderComparator);
    	// let's find new index
		if(ind >= 0) { // go to last if index is found
			int size = children_.size();
			CCNode prev;
			
			do {
				prev = children_.get(ind);
				ind++;
			} while(ind < size && children_.get(ind).zOrder_ == prev.zOrder_);
		} else { // index not found
			ind = -(ind + 1);
		}
		children_.add(ind, node);
//        int index = 0;
//        boolean added = false;
//        for (int i=0; i<children_.size(); ++i) {
//        	CCNode child = children_.get(i);
//            if (child.getZOrder() > z) {
//                added = true;
//                children_.add(index, node);
//                break;
//            }
//            ++index;
//        }
//        if (!added)
//            children_.add(node);
//        node._setZOrder(z);
    }

    /** Stops all running actions and schedulers
      @since v0.8
    */
    public void cleanup() {

	    // actions
        stopAllActions();
        unscheduleAllSelectors();
	
    	// timers
        if (children_ != null)
            for (int i=0; i<children_.size(); ++i) {
            	CCNode node = children_.get(i);
                node.cleanup();
            }
    }

    /** should we do this?
    @Override
    public void finalize() {
        Log.w( "cocos2d: deallocing " + self);

        // attributes
        camera_ = null;
        grid_ = null;

        // children
        if (children) {
            for (CCNode child: children) {
                child.parent = null;
            }
        }

        children_.clear();
        children_ = null;

        super.finalize();
    }*/

    @Override
    public String toString() {
        return "<instance of " + this.getClass() + "| Tag = " + tag_ + ">";
    }

    /** callback that is called every time the CCNode enters the 'stage'.
      If the CCNode enters the 'stage' with a transition, this callback is called when the transition starts.
      During onEnter you can't a "sister/brother" node.
    */
    public void onEnter() {

        if (children_ != null)
            for (CCNode child: children_) {
                child.onEnter();
            }
        resumeSchedulerAndActions();
        // activateTimers();
        isRunning_ = true;
    }

    /** callback that is called when the CCNode enters in the 'stage'.
      If the CCNode enters the 'stage' with a transition, this callback is called
            when the transition finishes.
      @since v0.8
    */
    public void onEnterTransitionDidFinish() {

        if (children_ != null)
            for (CCNode child: children_) {
                child.onEnterTransitionDidFinish();
            }
    }

    /** callback that is called every time the CCNode leaves the 'stage'.
      If the CCNode leaves the 'stage' with a transition, this callback is called
                when the transition finishes.
      During onExit you can't a "sister/brother" node.
    */
    public void onExit() {

        // deactivateTimers();
        pauseSchedulerAndActions();
        isRunning_ = false;

        if (children_ != null)
            for (CCNode child: children_) {
                child.onExit();
            }
    }


    private static final float RENDER_IN_SUBPIXEL(float f) {
        if (ccConfig.CC_COCOSNODE_RENDER_SUBPIXEL) {
            return f;
        } else {
            int i = (int) f;
            return (float)i;
        }
    }
}

