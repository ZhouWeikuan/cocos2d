package org.cocos2d.layers;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.protocols.CCTouchDelegateProtocol;
import org.cocos2d.types.CCSize;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;

public class Layer extends CocosNode implements CCTouchDelegateProtocol, SensorEventListener {
	
    // used to control registration of Touch events
    protected boolean isTouchEnabled_;

    // used to control registration of Accelerometer events
    protected boolean isAccelerometerEnabled_;
    protected int accelerometerUpdateRate = SensorManager.SENSOR_DELAY_GAME;

    protected final SensorManager sensorManager;
    protected final Sensor accelerometer;
    
    public boolean isTouchEnabled()
    {
        return isTouchEnabled_;
    }

    public void setIsTouchEnabled(boolean enabled)
    {
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

    public boolean isAccelerometerEnabled()
    {
        return isAccelerometerEnabled_;
    }

    public void setIsAccelerometerEnabled(boolean enabled)
    {
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
    
    public static Layer node() {
        return new Layer();
    }

    protected Layer() {
    	// get the SensorManager from the Activity
    	sensorManager = (SensorManager) Director.sharedDirector().getActivity().getSystemService(Context.SENSOR_SERVICE);
    	
    	// if we have a SensorManager then get the accelerometer Sensor
    	if (sensorManager != null) 
    		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	else
    		accelerometer = null;
    	
        CCSize s = Director.sharedDirector().winSize();
        setRelativeAnchorPoint(false);

        setAnchorPoint(0.5f, 0.5f);
        setContentSize(s.width, s.height);
        setRelativeAnchorPoint(false);

        isTouchEnabled_ = false;
        isAccelerometerEnabled_ = false;
    }

    protected void registerWithTouchDispatcher() {
        CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
    }
    
    protected void registerWithAccelerometer()
    {
    	if (accelerometer != null) {
    		boolean registered = sensorManager.registerListener(this, accelerometer, accelerometerUpdateRate);
    		if (!registered) {
    	        Log.e("Layer", "Could not register accelerometer sensor listener!");
    		}
    	}
    }

    protected void unregisterWithAccelerometer()
    {
    	if (accelerometer != null) {
    		sensorManager.unregisterListener(this, accelerometer);
    	}
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
    }

    @Override
    public void onExit() {

        if (isTouchEnabled_)
            CCTouchDispatcher.sharedDispatcher().removeDelegate(this);

        if( isAccelerometerEnabled_ )
        	unregisterWithAccelerometer();

        super.onExit();
    }

    public boolean ccTouchesBegan(MotionEvent event) {
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
}
