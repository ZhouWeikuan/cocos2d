package org.cocos2d.layers;

import org.cocos2d.events.CCKeyDispatcher;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.protocols.CCKeyDelegateProtocol;
import org.cocos2d.protocols.CCTouchDelegateProtocol;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


//
// CCLayer
//
/** CCLayer is a subclass of CCNode that implements the TouchEventsDelegate protocol.
 
 All features from CCNode are valid, plus the following new features:
 - It can receive iPhone Touches
 - It can receive Accelerometer input
*/
public class CCLayer extends CCNode 
    implements CCTouchDelegateProtocol, CCKeyDelegateProtocol, SensorEventListener {

    /** whether or not it will receive Touch events. 
     * You can enable / disable touch events with this property. 
     * Only the touches of this node will be affected. 
     * This "method" is not propagated to it's children. 
     * @since v0.8.1
     */
    protected boolean isTouchEnabled_;

    /** 
     * whether or not it will receive Accelerometer events 
     * You can enable / disable accelerometer events with this property.
     * @since v0.8.1
     */
    protected boolean isAccelerometerEnabled_;

    protected int accelerometerUpdateRate = SensorManager.SENSOR_DELAY_GAME;

    protected final SensorManager sensorManager;
    protected final Sensor accelerometer;
    
    public boolean isTouchEnabled() {
        return isTouchEnabled_;
    }

    public void setIsTouchEnabled(boolean enabled) {
        if( isTouchEnabled_ != enabled ) {
            isTouchEnabled_ = enabled;
            if( isRunning() ) {
                if( enabled )
                    registerWithTouchDispatcher();
                else
                    CCTouchDispatcher.sharedDispatcher().removeDelegate(this);
            }
        }
    }

    public boolean isAccelerometerEnabled() {
        return isAccelerometerEnabled_;
    }

    public void setIsAccelerometerEnabled(boolean enabled) {
        if( isAccelerometerEnabled_ != enabled ) {
        	isAccelerometerEnabled_ = enabled;
            if( isRunning() ) {
                if( enabled )
                    registerWithAccelerometer();
                else
                    unregisterWithAccelerometer();
            }
        }
    }
    
    //added by DustinEwan
    public void enableAccelerometerWithRate(int rate) {
    	accelerometerUpdateRate = rate;
    	setIsAccelerometerEnabled(true);
    }
    
	//added by Ishaq 
	protected boolean isKeyEnabled_;

	//added by Ishaq 
	public boolean isKeyEnabled() {
		return isKeyEnabled_;
	}

	//added by Ishaq 
	public void setIsKeyEnabled(boolean enabled) {
		if (isKeyEnabled_ != enabled) {
			isKeyEnabled_ = enabled;
			if (enabled)
				CCKeyDispatcher.sharedDispatcher().addDelegate(this, 0);
			else
				CCKeyDispatcher.sharedDispatcher().removeDelegate(this);
		}
	}

    public static CCLayer node() {
        return new CCLayer();
    }

    protected CCLayer() {
    	// get the SensorManager from the Activity
    	sensorManager = (SensorManager) CCDirector.sharedDirector().getActivity().getSystemService(Context.SENSOR_SERVICE);
    	
    	// if we have a SensorManager then get the accelerometer Sensor
    	if (sensorManager != null) 
    		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	else
    		accelerometer = null;
    	
        CGSize s = CCDirector.sharedDirector().winSize();
        setRelativeAnchorPoint(false);

        setAnchorPoint(CGPoint.make(0.5f, 0.5f));
        setContentSize(s);
        setRelativeAnchorPoint(false);

        isTouchEnabled_ = false;
        isAccelerometerEnabled_ = false;
    }

    /**
     * If isTouchEnabled, this method is called onEnter. Override it to change the
          way CCLayer receives touch events.
          ( Default: [[TouchDispatcher sharedDispatcher] addStandardDelegate:self priority:0] )
        @since v0.8.0
    */
    protected void registerWithTouchDispatcher() {
        CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
    }
    
    protected void registerWithAccelerometer() {
    	if (accelerometer != null) {
    		boolean registered = sensorManager.registerListener(this, accelerometer, accelerometerUpdateRate);
    		if (!registered) {
    	        Log.e("Layer", "Could not register accelerometer sensor listener!");
    		}
    	}
    }

    protected void unregisterWithAccelerometer() {
    	if (accelerometer != null) {
    		sensorManager.unregisterListener(this, accelerometer);
    	}
    	
    	/** reset the poll rate to GAME default */
    	accelerometerUpdateRate = SensorManager.SENSOR_DELAY_GAME;
    }
    
    @Override
    public void onEnter() {

        // register 'parent' nodes first
        // since events are propagated in reverse order
        if (isTouchEnabled_)
            registerWithTouchDispatcher();

        // then iterate over all the children
        super.onEnter();

        if( isAccelerometerEnabled_ )
        	registerWithAccelerometer();

		//added by Ishaq 
		if (isKeyEnabled_)
			CCKeyDispatcher.sharedDispatcher().addDelegate(this, 0);
    }

    @Override
    public void onExit() {

        if (isTouchEnabled_)
            CCTouchDispatcher.sharedDispatcher().removeDelegate(this);

        if( isAccelerometerEnabled_ )
        	unregisterWithAccelerometer();

		//added by Ishaq 
		if (isKeyEnabled_)
			CCKeyDispatcher.sharedDispatcher().removeDelegate(this);

        super.onExit();
    }

    public boolean ccTouchesBegan(MotionEvent event) {
	    assert false:"Layer#ccTouchBegan override me";
        return CCTouchDispatcher.kEventHandled;  // TODO Auto-generated method stub
    }

    public boolean ccTouchesMoved(MotionEvent event) {
        return CCTouchDispatcher.kEventIgnored;  // TODO Auto-generated method stub
    }

    public boolean ccTouchesEnded(MotionEvent event) {
        return CCTouchDispatcher.kEventIgnored;  // TODO Auto-generated method stub
    }

    public boolean ccTouchesCancelled(MotionEvent event) {
        return CCTouchDispatcher.kEventIgnored;  // TODO Auto-generated method stub
    }

    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
		// Override to process accelerometer events.
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Override to process sensor accuracy changes (for any registered sensors).
	}

	public void onSensorChanged(SensorEvent event) {
		// Override to process other sensor change events.
		// Make sure you this base implementation if you want accelerometer events passed.
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			ccAccelerometerChanged(event.values[0], event.values[1], event.values[2]);
		}
	}

	//added by Ishaq 
	public boolean ccKeyDown(int keyCode, KeyEvent event) {
		assert false : "Layer# ccKeyDown override me";
		return CCKeyDispatcher.kEventHandled;
	}

	//added by Ishaq 
	public boolean ccKeyUp(int keyCode, KeyEvent event) {
		assert false : "Layer# ccKeyUp override me";
		return CCKeyDispatcher.kEventHandled;
	}
}

