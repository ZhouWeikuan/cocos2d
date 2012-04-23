package org.cocos2d.utils;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.nodes.CCDirector;

/**
 * Class can register ContentProvider for loading resources from input stream,
 * Default is using assets provider. 
 */
public class ContentHelper {
	
    private static ContentHelper sContentHelper = new ContentHelper();

    /** singleton of the ContentHelper */
    public static ContentHelper sharedHelper() {
        return sContentHelper;
    }
    
    /**
     * loader interface for resources  
     */
    public interface StreamProvider {
    	InputStream openStream(final String path) throws IOException;
    }
    
    // external loader registered with setExternalLoader(StreamProvider)
    private StreamProvider mExternalLoader;
    
    // default provider uses assets for loading
    private StreamProvider mDefaultLoader;
    
    public void setExternalLoader(StreamProvider mExternalLoader) {
		this.mExternalLoader = mExternalLoader;
	}
    
    public ContentHelper() {
    	mDefaultLoader = new StreamProvider() {
			
			@Override
			public InputStream openStream(String path) throws IOException {
				return CCDirector.theApp.getAssets().open(path);
			}
		};
	}
    
    public InputStream openInputStream(final String path) throws IOException {
    	if(mExternalLoader != null) {
    		return mExternalLoader.openStream(path); 
    	} else {
    		return mDefaultLoader.openStream(path);
    	}
    }
}
