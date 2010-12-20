package org.cocos2d.layers;

import java.util.ArrayList;
import java.util.Arrays;

/** CCMultipleLayer is a CCLayer with the ability to multiplex it's children.
 Features:
   - It supports one or more children
   - Only one children will be active a time
 */
public class CCMultiplexLayer extends CCLayer {
    private ArrayList<CCLayer> layers;

    private int enabledLayer;

    /** creates a CCMultiplexLayer with one or more layers
     * using a variable argument list.
    */ 
    public static CCMultiplexLayer node(CCLayer... params) {
        return new CCMultiplexLayer(params);
    }

    /** initializes a MultiplexLayer with one or more layers
     * using a variable argument list.
    */
    protected CCMultiplexLayer(CCLayer... params) {
        layers = new ArrayList<CCLayer>();

        layers.addAll(Arrays.asList(params));

        enabledLayer = 0;
        addChild(layers.get(enabledLayer));
    }

    /** switches to a certain layer indexed by n.
     * The current (old) layer will be removed from it's parent with 'cleanup:YES'.
    */
    public void switchTo(int n) {
        assert n < layers.size(): "Invalid index passed to MultiplexLayer.switchTo";

        removeChild(layers.get(enabledLayer), true);
        enabledLayer = n;
        addChild(layers.get(enabledLayer));
    }

    /** release the current layer and switches to another layer indexed by n.
      The current (old) layer will be removed from it's parent with 'cleanup:YES'.
      */
    public void switchToAndReleaseMe(int n) {

        assert n < layers.size() : "Invalid index in MultiplexLayer switchTo message" ;
        removeChild(layers.get(enabledLayer), true);
        layers.add(enabledLayer, null);
        enabledLayer = n;
        addChild(layers.get(n));		
    }
}

