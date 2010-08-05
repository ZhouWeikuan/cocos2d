package org.cocos2d.types;

/** A vertex composed of 2 floats: x, y
 @since v0.8
 */
public class ccVertex2F {
    CGPoint pnt;
    
    public ccVertex2F() {
    	pnt = CGPoint.zero();
    }
    
    public void setCGPoint(CGPoint p) {
    	pnt = CGPoint.make(p.x, p.y);
    }
}

