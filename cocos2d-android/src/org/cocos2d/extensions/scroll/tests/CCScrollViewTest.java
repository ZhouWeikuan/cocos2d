/*
 *	Sample code for CCScrollView 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll.tests;

import java.util.ArrayList;
import java.util.Random;

import org.cocos2d.extensions.scroll.CCScrollView;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

public class CCScrollViewTest extends CCLayer{

	CCColorLayer backgroundLayer;
	CCScrollView scrollView;
	ArrayList<Object> contents;
	
	public static CCScene Scene()
	{
		CCScene scene = CCScene.node();
	    
	    
	    /* TABLE VIEW TESTING */
	    
	    //TableView testing
	    ArrayList<String> array = new ArrayList<String>();
	    String iconName;
	    for (int i = 0; i < 30; i++) {
	        iconName = new String("Icon.png");
	        array.add(iconName);
	    }
	    
	    //ScrollTableViewMenu menu = ScrollTableViewMenu.menu(array);
	    
		//scene.addChild(menu);
		scene.addChild(new CCScrollViewTest());
	
		return scene;
	}
	
	CCScrollViewTest()
	{
		CCColorLayer layer = CCColorLayer.node(ccColor4B.ccc4(55, 55, 55, 255));
		scrollView = CCScrollView.view(CGSize.zero());
		contents = new ArrayList<Object>();
	
		backgroundLayer = CCColorLayer.node(ccColor4B.ccc4(255, 255, 255, 255));
	
		//scrollView->setMaxZoomScale(2.0f);
		//scrollView->setMinZoomScale(0.5f);
		scrollView.setContentSize(CGSize.make(1000, 1000)); // You need to set contentSize to enable scrolling.
		// scrollView.bounces = NO;
	    scrollView.bounces = true;
	//    scrollView->setClipsToBounds(false);
	    //scrollView->setDirection(SWScrollViewDirectionVertical);
		layer.setContentSize(scrollView.getContentSize());
	
		scrollView.addChild(layer);
		// prepare contents
		for (int i=0; i<100; i++) {
	        //sprintf(text, "Test Item: %i", i);
			CCLabel label = CCLabel.makeLabel("Test",
												"Marker Felt",
												10.0f);
	
			contents.add(label);
			Random rnd = new Random();
			label.setPosition(CGPoint.ccp(1000.0f * rnd.nextFloat(), 1000.0f * rnd.nextFloat()));
			scrollView.addChild(label);
		}
	
		addChild(backgroundLayer, 0);
		addChild(scrollView, 10);
	    
	    CGSize winSize = CCDirector.sharedDirector().winSize();
		scrollView.setViewSize(CGSize.make(winSize.width - 100, winSize.height - 100));
	    scrollView.setPosition(CGPoint.ccp(50, 50));
	    //scrollView->setClipsToBounds(true);
		//scrollView->setPosition(ccp((winSize.width - scrollView->getViewSize().width) * 0.5f,
		//						  (winSize.height - scrollView->getViewSize().height) * 0.5f)); // position scroll at the center.
	    
	    
	}
}
