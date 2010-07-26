package org.cocos2d.nodes;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.ActionManager;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.Action;
import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.grid.GridBase;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.CCCamera;
import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

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
  - scale: (x=1,y=1)
  - contentSize: (x=0,y=0)
  - anchorPoint: (x=0,y=0)
 
 Limitations:
 - A CCNode is a "void" object. It doesn't have a texture
 
 Order in transformations with grid disabled
 -# The node will be translated (position)
 -# The node will be rotated (rotation)
 -# The node will be scaled (scale)
 -# The node will be moved according to the camera values (camera)
 
 Order in transformations with grid enabled
 -# The node will be translated (position)
 -# The node will be rotated (rotation)
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
    private float rotation_;

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
    private float scaleX_;
    private float scaleY_;

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

	// anchor point in pixels
	private CGPoint anchorPointInPixels_;	

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
    private CGPoint anchorPoint_;

	// untransformed size of the node
    private CGSize contentSize_;

    /** The untransformed size of the node.
     The contentSize remains the same no matter the node is scaled or rotated.
     All nodes has a size. Layer and Scene has the same size of the screen.
     @since v0.8
     */
    public void setContentSize(CGSize size) {
        if (! CGSize.equalToSize(contentSize_, size)) {
            contentSize_ = CGSize.make(size.width, size.height);
            anchorPointInPixels_ = CGPoint.ccp(contentSize_.width * anchorPoint_.x,
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

    /** anchorPoint is the point around which all transformations and positioning manipulations take place.
      It's like a pin in the node where it is "attached" to its parent.
      The anchorPoint is normalized, like a percentage.
        (0,0) means the bottom-left corner and (1,1) means the top-right corner.
      But you can use values higher than (1,1) and lower than (0,0) too.
      The default anchorPoint is (0.5,0.5), so it starts in the center of the node.
      @since v0.8
    */
    public void setAnchorPoint(CGPoint pnt) {
        if (!CGPoint.equalToPoint(pnt, anchorPoint_)) {
            anchorPoint_ = CGPoint.make(pnt.x, pnt.y);
            anchorPointInPixels_ = CGPoint.ccp(contentSize_.width * anchorPoint_.x, 
                                              contentSize_.height * anchorPoint_.y);

            isTransformDirty_ = isInverseDirty_ = true;
            if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
                isTransformGLDirty_ = true;
            }
        }
    }

    public CGPoint getAnchorPoint() {
        return CGPoint.make(anchorPoint_.x, anchorPoint_.y);
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
    private CGPoint position_;

    /** Position (x,y) of the node in OpenGL coordinates.
      (0,0) is the left-bottom corner.
    */
    public CGPoint getPosition() {
        return CGPoint.make(position_.x, position_.y);
    }

    public void setPosition(CGPoint pnt) {
        position_ = CGPoint.make(pnt.x, pnt.y);
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
    private GridBase grid_;

    /** A CCGrid object that is used when applying effects */
    public GridBase getGrid() {
        return grid_;
    }

    public void setGrid(GridBase grid) {
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
    public CCNode parent_;

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
    private float vertexZ_;

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
    protected ArrayList<CCNode> children_;

    public ArrayList<CCNode> getChildren() {
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
        scaleX_ = 1.0f;
        scaleY_ = 1.0f;
        position_ = CGPoint.ccp(0, 0);

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
            this.parent_.removeChild(this, true);
        }
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
    public void removeChild(int tag, boolean cleanup) {
	    assert tag != kCCNodeTagInvalid: "Invalid tag";

        CCNode child = getChild(tag);
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
        for (CCNode child: children_) {
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
    public CCNode getChild(int tag) {
        assert tag != kCCNodeTagInvalid : "Invalid tag_";

        if (children_ != null)
            for (CCNode node: children_) {
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

        if (children_ != null)
            for (CCNode child: children_) {
                if (child.zOrder_ < 0) {
                    child.visit(gl);
                } else
                    break;
            }

        draw(gl);

        if (children_ != null)
            for (CCNode child: children_) {
                if (child.zOrder_ >= 0) {
                    child.visit(gl);
                }
            }


        if (grid_ != null && grid_.isActive()) {
            grid_.afterDraw(gl, getCamera());
        }

        gl.glPopMatrix();
    }

    /**
     * performs OpenGL view-matrix transformation based on position, scale, rotation and other attributes.
    */
    public void transform(GL10 gl) {	
        // transformations

        if (ccConfig.CC_NODE_TRANSFORM_USING_AFFINE_MATRIX) {
            // BEGIN alternative -- using cached transform
            //
            if( isTransformGLDirty_ ) {
                CGAffineTransform t = nodeToParentTransform();
                CGAffineTransform.CGAffineToGL(t, transformGL_);
                isTransformGLDirty_ = false;
            }

            gl.glMultMatrixf(transformGL_, transformGL_.length);
            
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
    public Action runAction(Action action) {
        assert action != null : "Argument must be non-null";

        ActionManager.sharedManager().addAction(action, this, !isRunning_);
        return action;
    }

    /** Removes all actions from the running action list */
    public void stopAllActions() {
        ActionManager.sharedManager().removeAllActions(this);
    }

    /** Removes an action from the running action list */
    public void stopAction(Action action) {
        ActionManager.sharedManager().removeAction(action);
    }

    /** Removes an action from the running action list given its tag
      @since v0.7.1
    */
    public void stopAction(int tag) {    	
        assert tag != Action.kCCActionTagInvalid : "Invalid tag_";
        ActionManager.sharedManager().removeAction(tag, this);
    }

    /** Gets an action from the running action list given its tag
      @since v0.7.1
      @return the Action the with the given tag
    */
    public Action getAction(int tag) {
        assert tag != Action.kCCActionTagInvalid : "Invalid tag_";

        return ActionManager.sharedManager().getAction(tag, this);
    }

    /** Returns the numbers of actions that are running plus the ones that are schedule to run
               (actions in actionsToAdd and actions arrays). 
     * Composable actions are counted as 1 action. Example:
     *    If you are running 1 Sequence of 7 actions, it will return 1.
     *    If you are running 7 Sequences of 2 actions, it will return 7.
    */
    public int numberOfRunningActions() {
        return ActionManager.sharedManager().numberOfRunningActions(this);
    }

    /** schedules the "update" method.
      It will use the order number 0. This method will be called every frame.
      Scheduled methods with a lower order value will be called
            before the ones that have a higher order value.
      Only one "udpate" method could be scheduled per node.

      @since v0.99.3
    */
    public void scheduleUpdate() {
        this.scheduleUpdateWithPriority(0);
    }

    /** schedules the "update" selector with a custom priority. This selector will be called every frame.
      Scheduled selectors with a lower priority will be called before the ones that have a higher value.
      Only one "udpate" selector could be scheduled per node (You can't have 2 'update' selectors).

      @since v0.99.3
    */
    public void scheduleUpdateWithPriority(int priority) {
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

    /** unschedules a custom selector.*/
    public void unschedule(String selector) {
        // explicit null handling
        if (selector == null)
            return;

        CCScheduler.sharedScheduler().unschedule(selector, this);
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
	    ActionManager.sharedManager().resume(this);
    }

    /** pauses all scheduled selectors and actions.
      Called internally by onExit
      */
    public void pauseSchedulerAndActions() {
    	CCScheduler.sharedScheduler().pause(this);
    	ActionManager.sharedManager().pause(this);
    }

    // CocosNode Transform
    /** Returns the local affine transform matrix
      @since v0.7.1
    */
    private CGAffineTransform nodeToParentTransform() {
        if (isTransformDirty_) {

            transform_ = CGAffineTransform.identity();

            if (!isRelativeAnchorPoint_) {
                transform_.getTransformTranslate((int) anchorPointInPixels_.x, (int) anchorPointInPixels_.y);
            }

            transform_ = transform_.getTransformTranslate((int) position_.x, (int) position_.y);
            transform_ = transform_.getTransformRotate(-ccMacros.CC_DEGREES_TO_RADIANS(rotation_));
            transform_ = transform_.getTransformScale(scaleX_, scaleY_);
            transform_ = transform_.getTransformTranslate(-(int) anchorPointInPixels_.x, -(int) anchorPointInPixels_.y);

            isTransformDirty_ = false;
        }

        return transform_;
    }

    /** Returns the inverse local affine transform matrix
      @since v0.7.1
    */
    public CGAffineTransform parentToNodeTransform() {
        if (isInverseDirty_) {
            inverse_ = nodeToWorldTransform().getTransformInvert();

            isInverseDirty_ = false;
        }

        return inverse_;
    }

    /** Retrusn the world affine transform matrix
      @since v0.7.1
    */
    private CGAffineTransform nodeToWorldTransform() {
        CGAffineTransform t = nodeToParentTransform();

        for (CCNode p = parent_; p != null; p = p.parent_)
            t.getTransformConcat(p.nodeToParentTransform());

        return t;
    }

    /** Returns the inverse world affine transform matrix
      @since v0.7.1
    */
    private CGAffineTransform worldToNodeTransform() {
        return nodeToWorldTransform().getTransformInvert();
    }

    /** converts a world coordinate to local coordinate
      @since v0.7.1
    */
    public CGPoint convertToNodeSpace(float x, float y) {
        CGPoint worldPoint = CGPoint.make(x, y);
        return CGPoint.applyAffineTransform(worldPoint, worldToNodeTransform());
    }

    /** converts local coordinate to world space
      @since v0.7.1
    */
    public CGPoint convertToWorldSpace(float x, float y) {
        CGPoint nodePoint = CGPoint.make(x, y);
        return CGPoint.applyAffineTransform(nodePoint, nodeToWorldTransform());
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
        CGPoint point = CCDirector.sharedDirector().convertCoordinate(event.getX(), event.getY());
        return convertToNodeSpace(point.x, point.y);
    }

    /** converts a UITouch (world coordinates) into a local coordiante. This method is AR (Anchor Relative).
      @since v0.7.1
    */
    public CGPoint convertTouchToNodeSpaceAR(MotionEvent event) {
        CGPoint point = CCDirector.sharedDirector().convertCoordinate(event.getX(), event.getY());
        return convertToNodeSpaceAR(point.x, point.y);
    }

    public CGPoint convertToWindowSpace(CGPoint nodePoint) {
        CGPoint worldPoint = convertToWorldSpace(nodePoint.x, nodePoint.y);
        return CCDirector.sharedDirector().convertToUI(worldPoint);
    }

    public interface CocosNodeSize {
        public float getWidth();

        public float getHeight();
    }

    public interface CocosNodeRGBA {

        public void setColor(ccColor3B color);

        public ccColor3B getColor();

        public int getOpacity();

        public void setOpacity(int opacity);
    }

    /**
     * CocosNodes that uses a CCTexture2D to render the images.
     * The texture can have a blending function.
     * If the texture has alpha premultiplied the default blending function is:
     * src=GL_ONE dst= GL_ONE_MINUS_SRC_ALPHA
     * else
     * src=GL_SRC_ALPHA dst= GL_ONE_MINUS_SRC_ALPHA
     * But you can change the blending funtion at any time.
     *
     * @since v0.8
     */
    interface CocosNodeTexture {
        /**
         * returns the used texture
         */
        public CCTexture2D getTexture();

        /**
         * sets a new texture. it will be retained
         */
        public void setTexture(CCTexture2D texture);
        /** set the source blending function for the texture */
//        public void setBlendFunc(CCBlendFunc blendFunc);
        /** returns the blending function used for the texture */
//        public CCBlendFunc blendFunc();
    }


    public interface CocosAnimation {
        public ArrayList<Object> frames();

        public float delay();

        public String name();
    }

    public interface CocosNodeFrames {
        public void setDisplayFrame(Object newFrame);

        public void setDisplayFrame(String animationName, int frameIndex);

        public boolean isFrameDisplayed(Object frame);

        public Object displayFrame();

        public CocosAnimation animationByName(String animationName);

        public void addAnimation(CocosAnimation animation);
    }

    // lazy allocs
    private void childrenAlloc() {
        children_ = new ArrayList<CCNode>(4);
    }

    // helper that reorder a child
    private void insertChild(CCNode node, int z) {
        int index = 0;
        boolean added = false;
        for (CCNode child: children_) {
            if (child.getZOrder() > z) {
                added = true;
                children_.add(index, node);
                break;
            }
            ++index;
        }
        if (!added)
            children_.add(node);
        node._setZOrder(z);
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
            for (CCNode node: children_) {
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

