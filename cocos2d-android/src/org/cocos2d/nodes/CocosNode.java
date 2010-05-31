package org.cocos2d.nodes;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import org.cocos2d.actions.ActionManager;
import org.cocos2d.actions.Scheduler;
import org.cocos2d.actions.base.Action;
import org.cocos2d.grid.GridBase;
import org.cocos2d.opengl.Texture2D;
import org.cocos2d.opengl.Camera;
import org.cocos2d.types.*;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.HashMap;


public class CocosNode {
    private static final String LOG_TAG = CocosNode.class.getSimpleName();

    // private static final boolean COCOSNODE_DEBUG = false;

    public static final int INVALID_TAG = -1;

    private float rotation_;

    public float getRotation() {
        return rotation_;
    }

    public void setRotation(float rot) {
        rotation_ = rot;
        isTransformDirty_ = isInverseDirty_ = true;
    }

    private float scaleX_;

    public float getScaleX() {
        return scaleX_;
    }

    public void setScaleX(float sx) {
        scaleX_ = sx;
        isTransformDirty_ = isInverseDirty_ = true;
    }

    private float scaleY_;

    public float getScaleY() {
        return scaleY_;
    }

    public void setScaleY(float sy) {
        scaleY_ = sy;
        isTransformDirty_ = isInverseDirty_ = true;
    }

    public void setScale(float s) {
        scaleX_ = scaleY_ = s;
        isTransformDirty_ = isInverseDirty_ = true;
    }

    private CCPoint transformAnchor_;

    public float getTransformAnchorX() {
        return transformAnchor_.x;
    }

    public float getTransformAnchorY() {
        return transformAnchor_.y;
    }

    private boolean relativeAnchorPoint_;

    public void setRelativeAnchorPoint(boolean newValue) {
        relativeAnchorPoint_ = newValue;
        isTransformDirty_ = isInverseDirty_ = true;
    }


    // anchor point
    private PointF anchorPoint_;

    // untransformed size of the node
    private CCSize contentSize_;

    public void setAnchorPoint(float x, float y) {
        if (!((anchorPoint_.x == x) && (anchorPoint_.y == y))) {
            anchorPoint_.x = x;
            anchorPoint_.y = y;
            transformAnchor_.x = contentSize_.width * anchorPoint_.x;
            transformAnchor_.y = contentSize_.height * anchorPoint_.y;
        }
    }

    public float getAnchorPointX() {
        return anchorPoint_.x;
    }

    public float getAnchorPointY() {
        return anchorPoint_.y;
    }

    private CCAffineTransform transform_, inverse_;
    private boolean isTransformDirty_, isInverseDirty_;

    public void setContentSize(float w, float h) {
        if ((contentSize_.width != w) || (contentSize_.height != h)) {
            contentSize_.width = w;
            contentSize_.height = h;
            transformAnchor_.x = contentSize_.width * anchorPoint_.x;
            transformAnchor_.y = contentSize_.height * anchorPoint_.y;
        }
    }

    public float getWidth() {
        return contentSize_.width;
    }

    public float getHeight() {
        return contentSize_.height;
    }

    public CCRect getBoundingBox()
    {
        CCRect rect = CCRect.make(0, 0, contentSize_.width, contentSize_.height);
        return convertRectUsingMatrix(rect, nodeToParentTransform());
    }

    private static CCRect convertRectUsingMatrix(CCRect aRect, CCAffineTransform matrix)
    {
      CCRect r = CCRect.make(0, 0, 0, 0);
      CCPoint[] p = new CCPoint[4];

      for (int i = 0; i < 4; i++) {
          p[i] = CCPoint.make(aRect.origin.x, aRect.origin.y);
      }

      p[1].x += aRect.size.width;
      p[2].y += aRect.size.height;
      p[3].x += aRect.size.width;
      p[3].y += aRect.size.height;

      for (int i = 0; i < 4; i++) {
        p[i] = CCPoint.applyAffineTransform(p[i], matrix);
      }

      CCPoint min = CCPoint.make(p[0].x, p[0].y),
              max = CCPoint.make(p[0].x, p[0].y);
      for (int i = 1; i < 4; i++) {
          min.x = Math.min(min.x, p[i].x);
          min.y = Math.min(min.y, p[i].y);
          max.x = Math.max(max.x, p[i].x);
          max.y = Math.max(max.y, p[i].y);
      }

      r.origin.x = min.x; r.origin.y = min.y;
      r.size.width = max.x - min.x; r.size.height = max.y - min.y;

      return r;
    }

    private CCPoint position_;

    public float getPositionX() {
        return position_.x;
    }

    public float getPositionY() {
        return position_.y;
    }

    public void setPosition(float x, float y) {
        position_.x = x;
        position_.y = y;
        isTransformDirty_ = isInverseDirty_ = true;
    }

    private Camera camera_;

    public Camera getCamera() {
        if (camera_ == null)
            camera_ = new Camera();
        return camera_;
    }

    private GridBase grid_;

    public GridBase getGrid() {
        return grid_;
    }

    public void setGrid(GridBase grid) {
        this.grid_ = grid;
    }

    protected boolean visible_;

    public boolean isVisible() {
        return visible_;
    }

    public void setVisible(boolean visible) {
        this.visible_ = visible;
    }

    public CocosNode parent;

    public CocosNode getParent() {
        return parent;
    }

    public void setParent(CocosNode parent) {
        this.parent = parent;
    }

    private int tag_;

    public int getTag() {
        return tag_;
    }

    private float vertexZ_;

    public float getVertexZ() {
        return vertexZ_;
    }

    public void setVertexZ(float z) {
        vertexZ_ = z;
    }

    private int zOrder_;

    public int getZOrder() {
        return zOrder_;
    }

    public void setZOrder(int z) {
        zOrder_ = z;
    }

    protected ArrayList<CocosNode> children;

    public ArrayList<CocosNode> getChildren() {
        return children;
    }

    // user data field
    private Object userData;

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object data) {
        userData = data;
    }


    private boolean isRunning_;

    public boolean isRunning() {
        return isRunning_;
    }

    private HashMap<String, Scheduler.Timer> scheduledSelectors;


    public static CocosNode node() {
        return new CocosNode();
    }

    protected CocosNode() {
        isRunning_ = false;

        rotation_ = 0.0f;
        scaleX_ = 1.0f;
        scaleY_ = 1.0f;
        position_ = CCPoint.ccp(0, 0);


        // "whole screen" objects. like Scenes and Layers, should set relativeAnchorPoint to false        
        relativeAnchorPoint_ = true;

        transformAnchor_ = CCPoint.ccp(0, 0);
        anchorPoint_ = new PointF();
        contentSize_ = CCSize.zero();

        isTransformDirty_ = isInverseDirty_ = true;

        vertexZ_ = 0;

        grid_ = null;

        visible_ = true;

        tag_ = INVALID_TAG;

        zOrder_ = 0;

        camera_ = null;

        children = null;

        scheduledSelectors = null;

        userData = null;

    }

    public CocosNode addChild(CocosNode child, int z, int tag) {
        assert child != null;
        assert child.parent == null;

        if (children == null)
            childrenAlloc();

        insertChild(child, z);
        child.tag_ = tag;
        child.setParent(this);
        if (isRunning_) {
            child.onEnter();
        }
        return this;
    }

    public CocosNode addChild(CocosNode child, int z) {
        assert child != null;

        return addChild(child, z, child.tag_);
    }

    public CocosNode addChild(CocosNode child) {
        assert child != null;

        return addChild(child, child.zOrder_, child.tag_);
    }

    public void removeChild(CocosNode child, boolean cleanup) {
        if (child == null)
            return;

        if (children.contains(child))
            detachChild(child, cleanup);
    }

    public void removeChild(int tag, boolean cleanup) {
        assert tag != INVALID_TAG;

        CocosNode child = getChild(tag);
        if (child == null)
            Log.w(LOG_TAG, "removeChild: child not found");
        else
            removeChild(child, cleanup);
    }

    public void removeAllChildren(boolean cleanup) {
        for (int i = 0; i < children.size(); i++) {
            CocosNode child = children.get(i);
            if (isRunning_)
                child.onExit();

            if (cleanup)
                child.cleanup();

            child.setParent(null);
        }
        children.clear();
    }

    public CocosNode getChild(int tag) {
        assert tag != INVALID_TAG : "Invalid tag_";

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                if (child.tag_ == tag) {
                    return child;
                }
            }

        return null;
    }

    private CCPoint absolutePosition() {
        CCPoint ret = CCPoint.ccp(position_.x, position_.y);
        CocosNode cn = this;
        while (cn.parent != null) {
            cn = cn.parent;
            ret.x += cn.position_.x;
            ret.y += cn.position_.y;
        }
        return ret;
    }

    private void detachChild(CocosNode child, boolean doCleanup) {
        if (isRunning_)
            child.onExit();

        if (doCleanup)
            child.cleanup();

        child.setParent(null);

        children.remove(child);
    }

    public void reorderChild(CocosNode child, int zOrder) {
        assert child != null : "Child must be non-null";
        this.insertChild(child, zOrder);
        children.remove(child);
    }

    public void draw(GL10 gl) {
        // Do nothing by default
    }

    public void visit(GL10 gl) {
        if (!visible_)
            return;

        gl.glPushMatrix();

        if (grid_ != null && grid_.isActive()) {
            grid_.beforeDraw(gl);
            transformAncestors(gl);
        }

        transform(gl);

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                if (child.zOrder_ < 0) {
                    child.visit(gl);
                } else
                    break;
            }

        draw(gl);


        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                if (child.zOrder_ >= 0) {
                    child.visit(gl);
                }
            }


        if (grid_ != null && grid_.isActive()) {
            grid_.afterDraw(gl, getCamera());
        }

        gl.glPopMatrix();
    }

    public void transform(GL10 gl) {

        if (!(grid_ != null && grid_.isActive())) {
            getCamera().locate(gl);
        }

        //
        // transformations
        //

        // translate
        if (relativeAnchorPoint_ && (transformAnchor_.x != 0 || transformAnchor_.y != 0)) {
            gl.glTranslatef(-transformAnchor_.x, -transformAnchor_.y, vertexZ_);
        }

        if (transformAnchor_.x != 0 || transformAnchor_.y != 0) {
            gl.glTranslatef(position_.x + transformAnchor_.x, position_.y + transformAnchor_.y, vertexZ_);
        } else if (position_.x != 0 || position_.y != 0) {
            gl.glTranslatef(position_.x, position_.y, vertexZ_);
        }

        if (rotation_ != 0.0f) {
            gl.glRotatef(-rotation_, 0.0f, 0.0f, 1.0f);
        }

        // rotate
        if (scaleX_ != 1.0f || scaleY_ != 1.0f) {
            gl.glScalef(scaleX_, scaleY_, 1.0f);
        }

        // restore and reposition_ point
        if (transformAnchor_.x != 0 || transformAnchor_.y != 0.0f) {
            gl.glTranslatef(-transformAnchor_.x, -transformAnchor_.y, vertexZ_);
        }
    }

    public void transformAncestors(GL10 gl) {
        if (parent != null) {
            parent.transformAncestors(gl);
            parent.transform(gl);
        }
    }

    public Action runAction(Action action) {
        assert action != null : "Argument must be non-null";

        ActionManager.sharedManager().addAction(action, this, !isRunning_);
        return action;
    }

    public void stopAllActions() {
        ActionManager.sharedManager().removeAllActions(this);
    }

    public void stopAction(Action action) {
        ActionManager.sharedManager().removeAction(action);
    }

    public void stopAction(int tag) {
        assert tag != INVALID_TAG : "Invalid tag_";
        ActionManager.sharedManager().removeAction(tag, this);
    }

    public Action getAction(int tag) {
        assert tag != INVALID_TAG : "Invalid tag_";

        return ActionManager.sharedManager().getAction(tag, this);
    }

    public int numberOfRunningActions() {
        return ActionManager.sharedManager().numberOfRunningActions(this);
    }

    public void schedule(String selector) {
        schedule(selector, 0);
    }

    public void schedule(String selector, float interval) {
        assert selector != null : "Argument selector must be non-null";
        assert interval >= 0 : "Argument interval must be positive";

        if (scheduledSelectors == null)
            timerAlloc();

        String key = this.getClass().getName() + "." + selector + "(float)";
        if (scheduledSelectors.containsKey(key)) {
            return;
        }

        Scheduler.Timer timer = new Scheduler.Timer(this, selector, interval);

        if (isRunning_)
            Scheduler.sharedScheduler().schedule(timer);

        scheduledSelectors.put(key, timer);

    }

    public void unschedule(String selector) {
        // explicit null handling
        if (selector == null)
            return;

        if (scheduledSelectors == null)
            return;

        Scheduler.Timer timer;

        String key = this.getClass().getName() + "." + selector + "(float)";
        if ((timer = scheduledSelectors.get(key)) == null) {
            return;
        }

        scheduledSelectors.remove(key);
        if (isRunning_)
            Scheduler.sharedScheduler().unschedule(timer);
    }

    private void activateTimers() {
        if (scheduledSelectors != null)
            for (String key : scheduledSelectors.keySet())
                Scheduler.sharedScheduler().schedule(scheduledSelectors.get(key));

        ActionManager.sharedManager().resumeAllActions(this);
    }

    private void deactivateTimers() {
        if (scheduledSelectors != null)
            for (String key : scheduledSelectors.keySet())
                Scheduler.sharedScheduler().unschedule(scheduledSelectors.get(key));

        ActionManager.sharedManager().pauseAllActions(this);
    }

    // CocosNode Transform

    private CCAffineTransform nodeToParentTransform() {
        if (isTransformDirty_) {

            transform_ = CCAffineTransform.identity();

            if (!relativeAnchorPoint_) {
                transform_.translate((int) transformAnchor_.x, (int) transformAnchor_.y);
            }

            transform_.translate((int) position_.x, (int) position_.y);
            transform_.rotate(-CCMacros.CC_DEGREES_TO_RADIANS(rotation_));
            transform_.scale(scaleX_, scaleY_);

            transform_.translate(-(int) transformAnchor_.x, -(int) transformAnchor_.y);

            isTransformDirty_ = false;
        }

        return transform_;
    }

    private CCAffineTransform parentToNodeTransform() {
        if (isInverseDirty_) {
            inverse_ = nodeToWorldTransform().createInverse();

            isInverseDirty_ = false;
        }

        return inverse_;
    }

    private CCAffineTransform nodeToWorldTransform() {
        CCAffineTransform t = nodeToParentTransform();

        for (CocosNode p = parent; p != null; p = p.parent)
            t.concatenate(p.nodeToParentTransform());

        return t;
    }

    private CCAffineTransform worldToNodeTransform() {
        return nodeToWorldTransform().createInverse();
    }

    public CCPoint convertToNodeSpace(float x, float y) {
        CCPoint worldPoint = CCPoint.make(x, y);
        return CCPoint.applyAffineTransform(worldPoint, worldToNodeTransform());

    }

    public CCPoint convertToWorldSpace(float x, float y) {
        CCPoint nodePoint = CCPoint.make(x, y);
        return CCPoint.applyAffineTransform(nodePoint, nodeToWorldTransform());
    }

    public CCPoint convertToNodeSpaceAR(float x, float y) {
        CCPoint nodePoint = convertToNodeSpace(x, y);
        return CCPoint.ccpSub(nodePoint, transformAnchor_);
    }

    public CCPoint convertToWorldSpaceAR(float x, float y) {
        CCPoint nodePoint = CCPoint.make(x, y);
        nodePoint = CCPoint.ccpAdd(nodePoint, transformAnchor_);
        return convertToWorldSpace(nodePoint.x, nodePoint.y);
    }

    // convenience methods which take a MotionEvent instead of PointF

    public CCPoint convertTouchToNodeSpace(MotionEvent event) {
        CCPoint point = Director.sharedDirector().convertCoordinate(event.getX(), event.getY());
        return convertToNodeSpace(point.x, point.y);
    }

    public CCPoint convertTouchToNodeSpaceAR(MotionEvent event) {
        CCPoint point = Director.sharedDirector().convertCoordinate(event.getX(), event.getY());
        return convertToNodeSpaceAR(point.x, point.y);
    }

    public interface CocosNodeSize {
        public float getWidth();

        public float getHeight();
    }

    public interface CocosNodeRGBA {

        public void setColor(CCColor3B color);

        public CCColor3B getColor();

        public int getOpacity();

        public void setOpacity(int opacity);
    }

    /**
     * CocosNodes that uses a Texture2D to render the images.
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
        public Texture2D getTexture();

        /**
         * sets a new texture. it will be retained
         */
        public void setTexture(Texture2D texture);
        /** set the source blending function for the texture */
//        public void setBlendFunc(CCBlendFunc blendFunc);
        /** returns the blending function used for the texture */
//        public CCBlendFunc blendFunc();
    }


    public interface CocosNodeLabel {
        public void setString(String label);
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

    private void childrenAlloc() {
        children = new ArrayList<CocosNode>(4);
    }

    private void timerAlloc() {
        scheduledSelectors = new HashMap<String, Scheduler.Timer>(2);
    }

    private void insertChild(CocosNode node, int z) {
        boolean added = false;
        for (int i = 0; i < children.size(); i++) {
            CocosNode child = children.get(i);
            if (child.getZOrder() > z) {
                added = true;
                children.add(i, node);
                break;
            }
        }
        if (!added)
            children.add(node);
        node.setZOrder(z);
    }

    public void cleanup() {

        stopAllActions();

        scheduledSelectors = null;

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                children.get(i).cleanup();
            }
    }

    @Override
    public String toString() {
        return "<instance of " + this.getClass() + "| Tag = " + tag_ + ">";
    }

    public float scale() {

        if (scaleX_ == scaleY_) {
            return scaleX_;
        } else {
            Log.w(LOG_TAG, "CocosNode scale error: scaleX is different from scaleY");
        }
        return 0;
    }

    public void scale(float s) {
        scaleX_ = scaleY_ = s;
    }

    public void onEnter() {

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                child.onEnter();
            }
        activateTimers();
        isRunning_ = true;
    }

    public void onEnterTransitionDidFinish() {

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                child.onEnterTransitionDidFinish();
            }
    }

    public void onExit() {

        deactivateTimers();
        isRunning_ = false;

        if (children != null)
            for (int i = 0; i < children.size(); i++) {
                CocosNode child = children.get(i);
                child.onExit();
            }
    }


}

