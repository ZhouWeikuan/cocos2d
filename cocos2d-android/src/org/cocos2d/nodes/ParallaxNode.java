package org.cocos2d.nodes;

import org.cocos2d.types.CGPoint;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class ParallaxNode extends CCNode {

    private ArrayList<CCPointObject> parallaxArray_;
	private CGPoint lastPosition;

    static class CCPointObject
    {
        private float ratioX_;
        private float ratioY_;
        private float offsetX_;
        private float offsetY_;
        private CCNode child_;

        public CCPointObject(float ratioX, float ratioY, float offsetX, float offsetY) {
            ratioX_ = ratioX;
            ratioY_ = ratioY;
            offsetX_ = offsetX;
            offsetY_ = offsetY;
        }

        public CCNode getChild() {
            return child_;
        }

        public void setChild(CCNode child) {
            child_ = child;
        }

        public float getRatioX() {
            return ratioX_;
        }

        public float getRatioY() {
            return ratioY_;
        }

        public float getOffsetX() {
            return offsetX_;
        }

        public float getOffsetY() {
            return offsetY_;
        }

    }
    public static ParallaxNode node() {
        return new ParallaxNode();
    }

    protected ParallaxNode()
    {
        parallaxArray_ = new ArrayList<CCPointObject>(5);
        lastPosition = CGPoint.make(-100,-100);
    }

    @Override
    public CCNode addChild(CCNode child, int z, int tag)
    {
        assert false : "ParallaxNode: use addChild:z:parallaxRatio:positionOffset instead";
        return null;
    }

    public CCNode addChild(CCNode child, int z, float ratioX, float ratioY, float offsetX, float offsetY)
    {
        assert child != null : "Argument must be non-null";
        CCPointObject obj = new CCPointObject(ratioX, ratioY, offsetX, offsetY);
        obj.setChild(child);
        parallaxArray_.add(obj);
	
        CGPoint pnt = getPosition();
        float x = pnt.x * ratioX + offsetX;
        float y = pnt.y * ratioY + offsetY;
        child.setPosition(CGPoint.make(x, y));
	
        return super.addChild(child, z, child.getTag());
    }

    @Override
    public void removeChild(CCNode node, boolean cleanup)
    {
        for( int i=0;i < parallaxArray_.size();i++) {
            CCPointObject point = parallaxArray_.get(i);
            if( point.getChild().equals(node) ) {
                parallaxArray_.remove(i);
                break;
            }
        }
        super.removeChild(node, cleanup);
    }

    @Override
    public void removeAllChildren(boolean cleanup)
    {
        parallaxArray_.clear();
        super.removeAllChildren(cleanup);
    }

    private CGPoint absolutePosition()
    {
        CGPoint ret = getPosition();
	
        CCNode cn = this;
	
        while (cn.parent_ != null) {
            cn = cn.parent_;
            CGPoint pnt = cn.getPosition();
            ret.x += pnt.x;
            ret.y += pnt.y;
        }
	
        return ret;
    }

    /*
     The positions are updated at visit because:
       - using a timer is not guaranteed that it will called after all the positions were updated
       - overriding "draw" will only precise if the children have a z > 0
    */
    @Override
    public void visit(GL10 gl)
    {
        CGPoint pos = absolutePosition();
        if( ! CGPoint.equalToPoint(pos, lastPosition) ) {

            for(int i=0; i < parallaxArray_.size(); i++ ) {

                CCPointObject point = parallaxArray_.get(i);
                float x = -pos.x + pos.x * point.getRatioX() + point.getOffsetX();
                float y = -pos.y + pos.y * point.getRatioY() + point.getOffsetY();
                point.getChild().setPosition(CGPoint.make(x, y));
            }

            lastPosition = pos;
        }

        super.visit(gl);
    }
    
}
