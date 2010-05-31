package org.cocos2d.layers;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiplexLayer extends Layer {
    private ArrayList<Layer> layers;

    private int enabledLayer;

    public MultiplexLayer node(Layer... params) {
        return new MultiplexLayer(params);
    }

    protected MultiplexLayer(Layer... params) {
        layers = new ArrayList<Layer>();

        layers.addAll(Arrays.asList(params));

        enabledLayer = 0;
        addChild(layers.get(enabledLayer));
    }

    public void switchTo(int n) {
        if (n >= layers.size()) {
            throw new MultiplexLayerInvalidIndex("Invalid index passed to MultiplexLayer.switchTo");
        }

        removeChild(layers.get(enabledLayer), false);

        enabledLayer = n;

        addChild(layers.get(enabledLayer));
    }

    static class MultiplexLayerInvalidIndex extends RuntimeException {
        public MultiplexLayerInvalidIndex(String reason) {
            super(reason);
        }
    }
}

