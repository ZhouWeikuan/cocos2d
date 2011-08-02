package org.cocos2d.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.cocos2d.config.ccConfig;
import org.cocos2d.utils.collections.ConcurrentArrayHashMap;

//
// CCScheduler
//
/** Scheduler is responsible of triggering the scheduled callbacks.
 You should not use NSTimer. Instead use this class.
 
 There are 2 different types of callbacks (selectors):

	- update selector: the 'update' selector will be called every frame. You can customize the priority.
	- custom selector: A custom selector will be called every frame, or with a custom interval of time
 
 The 'custom selectors' should be avoided when possible. It is faster, and consumes less memory to use the 'update selector'.

*/
public class CCScheduler {

    // A list double-linked list used for "updates with priority"
    private static class tListEntry {
        // struct	_listEntry *prev, *next;
        public Method impMethod;
        public UpdateCallback callback; // instead of method invocation
        public Object	target;				// not retained (retained by hashUpdateEntry)
        public int		priority;
        public boolean	paused;
    };

    // Hash Element used for "selectors with interval"
    private static class tHashSelectorEntry {
        ArrayList<CCTimer>    timers;
        Object			target;		// hash key (retained)
        ArrayList<tListEntry> list;
        tListEntry 		entry;
        int	            timerIndex;
        CCTimer			currentTimer;
        boolean			currentTimerSalvaged;
        boolean			paused;
        void setPaused(boolean b){
            paused = b;
            if (entry != null){
                entry.paused = b;
            }
        }
        // UT_hash_handle  hh;
    }

	//
	// "updates with priority" stuff
	//
	ArrayList<tListEntry>    updatesNeg;	// list of priority < 0
	ArrayList<tListEntry>    updates0;	// list priority == 0
	ArrayList<tListEntry>    updatesPos;	// list priority > 0
		
	// Used for "selectors with interval"
	ConcurrentArrayHashMap<Object, tHashSelectorEntry>  hashForSelectors;
	ConcurrentHashMap<Object, tHashSelectorEntry>  hashForUpdates;
    
    tListEntry							currentEntry;
    
	tHashSelectorEntry	                currentTarget;
	boolean						        currentTargetSalvaged;
	
	// Optimization
//	Method			    impMethod;
	String				updateSelector;

    /** Modifies the time of all scheduled callbacks.
      You can use this property to create a 'slow motion' or 'fast fordward' effect.
      Default is 1.0. To create a 'slow motion' effect, use values below 1.0.
      To create a 'fast fordward' effect, use values higher than 1.0.
      @since v0.8
      @warning It will affect EVERY scheduled selector / action.
    */
    private float timeScale_;

    public float getTimeScale() {
        return timeScale_;
    }

    public void setTimeScale(float ts) {
        timeScale_ = ts;
    }

    private static CCScheduler _sharedScheduler = null;

    /** returns a shared instance of the Scheduler */
    public static CCScheduler sharedScheduler() {
        if (_sharedScheduler != null) {
            return _sharedScheduler;
        }
        synchronized (CCScheduler.class) {
            if (_sharedScheduler == null) {
                _sharedScheduler = new CCScheduler();
            }
            return _sharedScheduler;
        }
    }

    /** purges the shared scheduler. It releases the retained instance.
      @since v0.99.0
      */
    public static void purgeSharedScheduler() {
        _sharedScheduler = null;
    }

    private CCScheduler() {
        timeScale_ = 1.0f;

        // used to trigger CCTimer#update
        updateSelector = "update";
//        try {
//			impMethod = CCTimer.class.getMethod(updateSelector, Float.TYPE);
//    	} catch (NoSuchMethodException e) {
//    		impMethod = null;
//    		e.printStackTrace();
//    	}

        // updates with priority
        updates0   = new ArrayList<tListEntry>();
        updatesNeg = new ArrayList<tListEntry>();
        updatesPos = new ArrayList<tListEntry>();
        hashForUpdates   = new ConcurrentHashMap<Object, tHashSelectorEntry>();
        hashForSelectors = new ConcurrentArrayHashMap<Object, tHashSelectorEntry>();

        // selectors with interval
        currentTarget = null;
        currentTargetSalvaged = false;
    }

//    private void removeHashElement(Object key, tHashSelectorEntry element){
//    	removeHashElement(element);
//        hashForSelectors.remove(key);
//    }
//    
//    private void removeHashElement(tHashSelectorEntry element)
//    {
//    	element.timers.clear();
//        element.timers = null;
//        element.target = null;
//    }

    /** 'tick' the scheduler.
      You should NEVER call this method, unless you know what you are doing.
    */
    public void tick(float dt) {
        if( timeScale_ != 1.0f )
            dt *= timeScale_;
        
        currentTargetSalvaged = false;
        // updates with priority < 0
        synchronized (updatesNeg) {
        	int len = updatesNeg.size();
	        for (int i = 0; i < len; i++) {
	        	tListEntry e = updatesNeg.get(i);
	        	currentEntry = e;
	            if( ! e.paused ) {
	            	if(e.callback !=null) {
	            		e.callback.update(dt);
	            	} else {
		            	try {
							e.impMethod.invoke(e.target, dt);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
	            	}
	            	if(currentTargetSalvaged) {
	            		updatesNeg.remove(i);
	            		i--;
	            		len--;
	            		currentTargetSalvaged = false;
	            	}
	            }
	        }
	        currentEntry = null;
        }

        // updates with priority == 0
        synchronized (updates0) {
        	int len = updates0.size();
	        for(int i=0; i < len; ++i) {
	        	tListEntry e = updates0.get(i);
	        	currentEntry = e;
	            if( ! e.paused ) {
	            	if(e.callback !=null) {
	            		e.callback.update(dt);
	            	} else {
		                try {
							e.impMethod.invoke(e.target, dt);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	            	}
	            	if(currentTargetSalvaged) {
	            		updates0.remove(i);
	            		i--;
	            		len--;
	            		currentTargetSalvaged = false;
	            	}
	            }
	        }
	        currentEntry = null;
        }
        
        // updates with priority > 0
        synchronized (updatesPos) {
        	int len = updatesPos.size();
	        for (int i=0; i < len; i++) {
	        	tListEntry e = updatesPos.get(i);
	        	currentEntry = e;
	            if( ! e.paused ) {
	            	if(e.callback !=null) {
	            		e.callback.update(dt);
	            	} else {
		                try {
							e.impMethod.invoke(e.target, dt);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
	            	}
	            	if(currentTargetSalvaged) {
	            		updatesPos.remove(i);
	            		i--;
	            		len--;
	            		currentTargetSalvaged = false;
	            	}
	            }
	        }
	        currentEntry = null;
        }
        
        for(ConcurrentArrayHashMap<Object, tHashSelectorEntry>.Entry e = hashForSelectors.firstValue();
        	e != null; e = hashForSelectors.nextValue(e)) {
        	tHashSelectorEntry elt = e.getValue();

        	currentTarget = elt;
            currentTargetSalvaged = false;

            if( ! currentTarget.paused && elt.timers != null) {
                // The 'timers' ccArray may change while inside this loop.
                for( elt.timerIndex = 0; elt.timerIndex < elt.timers.size(); elt.timerIndex++) {
                    elt.currentTimer = elt.timers.get(elt.timerIndex);
                    elt.currentTimerSalvaged = false;

                    elt.currentTimer.update(dt);

                    if( elt.currentTimerSalvaged ) {
                        // The currentTimer told the remove itself. To prevent the timer from
                        // accidentally deallocating itself before finishing its step, we retained
                        // it. Now that step is done, it's safe to release it.
                        elt.currentTimer = null;
                    }
                    
                    elt.currentTimer = null;
                }			
            }
	            
	            // elt, at this moment, is still valid
	            // so it is safe to ask this here (issue #490)
	            // elt=elt->hh.next;
	            
	            // only delete currentTarget if no actions were scheduled during the cycle (issue #481)
            if( currentTargetSalvaged && currentTarget.timers.isEmpty()) {
//            	removeHashElement(elt);
            	hashForSelectors.remove(elt.target);
                // [self removeHashElement:currentTarget];
            }
        }
        currentTarget = null;
//        }
    }

    static class SchedulerTimerAlreadyScheduled extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5996803998420105321L;

		public SchedulerTimerAlreadyScheduled(String reason) {
            super(reason);
        }
    }

    static class SchedulerTimerNotFound extends RuntimeException {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1912889437889458701L;

		public SchedulerTimerNotFound(String reason) {
            super(reason);
        }
    }

    /** The scheduled method will be called every 'interval' seconds.
      If paused is YES, then it won't be called until it is resumed.
      If 'interval' is 0, it will be called every frame, but if so, it recommened to use 'scheduleUpdateForTarget:' instead.

      @since v0.99.3
    */
    public void schedule(String selector, Object target, float interval, boolean paused) {
        assert selector != null: "Argument selector must be non-nil";
        assert target != null: "Argument target must be non-nil";	

        tHashSelectorEntry element = hashForSelectors.get(target);

        if( element == null ) {
            element = new tHashSelectorEntry();
            element.target = target;
            hashForSelectors.put(target, element);
            // Is this the 1st element ? Then set the pause level to all the selectors of this target
            element.paused = paused;

        } else {
            assert element.paused == paused : "CCScheduler. Trying to schedule a selector with a pause value different than the target";
        }

        if( element.timers == null) {
            element.timers = new ArrayList<CCTimer>();
        }/* else if( element.timers.size() == element.timers )
            ccArrayDoubleCapacity(element->timers);
		*/
        CCTimer timer = new CCTimer(target, selector, interval);
        element.timers.add(timer);
    }
    
    /*
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation.  
     */
    public void schedule(UpdateCallback callback, Object target, float interval, boolean paused) {
        assert callback != null: "Argument callback must be non-nil";
        assert target != null: "Argument target must be non-nil";	

        tHashSelectorEntry element = hashForSelectors.get(target);

        if( element == null ) {
            element = new tHashSelectorEntry();
            element.target = target;
            hashForSelectors.put(target, element);
            // Is this the 1st element ? Then set the pause level to all the selectors of this target
            element.paused = paused;

        } else {
            assert element.paused == paused : "CCScheduler. Trying to schedule a selector with a pause value different than the target";
        }

        if( element.timers == null) {
            element.timers = new ArrayList<CCTimer>();
        }/* else if( element.timers.size() == element.timers )
            ccArrayDoubleCapacity(element->timers);
		*/
        CCTimer timer = new CCTimer(target, callback, interval);
        element.timers.add(timer);
    }

    /** Unshedules a selector for a given target.
     If you want to unschedule the "update", use unscheudleUpdateForTarget.
     @since v0.99.3
    */
    public void unschedule(String selector, Object target) {
        // explicity handle nil arguments when removing an object
        if( target==null || selector==null)
            return;

        assert target != null: "Target MUST not be null";
        assert selector != null: "Selector MUST not be null";

        tHashSelectorEntry element = hashForSelectors.get(target);
        if( element != null ) {
            for( int i=0; i< element.timers.size(); i++ ) {
                CCTimer timer = element.timers.get(i);

                if(selector.equals(timer.getSelector())) {
                    if( timer == element.currentTimer && !element.currentTimerSalvaged ) {                        
                        element.currentTimerSalvaged = true;
                    }
                    	
                    element.timers.remove(i);

                    // update timerIndex in case we are in tick:, looping over the actions
                    if( element.timerIndex >= i )
                        element.timerIndex--;

                    if( element.timers.isEmpty()) {
                        if( currentTarget == element ) {
                            currentTargetSalvaged = true;						
                        } else {
                        	hashForSelectors.remove(element.target);
//                        	this.removeHashElement(element.target, element);
                        }
                    }
                    return;
                }
            }
        }

        // Not Found
        //	NSLog(@"CCScheduler#unscheduleSelector:forTarget: selector not found: %@", selString);
    }
    
    /*
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation.  
     */
    public void unschedule(UpdateCallback callback, Object target) {
        // explicity handle nil arguments when removing an object
        if( target==null || callback==null)
            return;

        assert target != null: "Target MUST not be null";
        assert callback != null: "Selector MUST not be null";

        tHashSelectorEntry element = hashForSelectors.get(target);
        if( element != null ) {
            for( int i=0; i< element.timers.size(); i++ ) {
                CCTimer timer = element.timers.get(i);

                if(callback == timer.getCallback()) {
                    if( timer == element.currentTimer && !element.currentTimerSalvaged ) {                        
                        element.currentTimerSalvaged = true;
                    }
                    	
                    element.timers.remove(i);

                    // update timerIndex in case we are in tick:, looping over the actions
                    if( element.timerIndex >= i )
                        element.timerIndex--;

                    if( element.timers.isEmpty()) {
                        if( currentTarget == element ) {
                            currentTargetSalvaged = true;						
                        } else {
                        	hashForSelectors.remove(element.target);
//                        	this.removeHashElement(element.target, element);
                        }
                    }
                    return;
                }
            }
        }

        // Not Found
        //	NSLog(@"CCScheduler#unscheduleSelector:forTarget: selector not found: %@", selString);
    }

    /** Unschedules the update selector for a given target
      @since v0.99.3
      */
    public void unscheduleUpdate(Object target) {
        if( target == null )
            return;
        tHashSelectorEntry entry = hashForUpdates.get(target);
        if ( entry == null )
        	return;

        synchronized (entry.list) {
        	if(currentEntry==entry.entry) {
        		currentTargetSalvaged = true;
        	} else {
        		entry.list.remove(entry.entry);
        	}
		}
        
        hashForUpdates.remove(target);
    }

    /** Unschedules all selectors for a given target.
     This also includes the "update" selector.
     @since v0.99.3
    */
	public void unscheduleAllSelectors(Object target) {
        // TODO Auto-generated method stub
        // explicit nil handling
        if( target == null )
            return;

        // Custom Selectors
        tHashSelectorEntry element = hashForSelectors.get(target);

        if( element != null) {
            if(!element.currentTimerSalvaged ) {
                // element.currentTimer retain;
                element.currentTimerSalvaged = true;
            }
            element.timers.clear();
            // ccArrayRemoveAllObjects(element->timers);
            if( currentTarget == element )
                currentTargetSalvaged = true;
            else {
            	hashForSelectors.remove(element.target);
//            	this.removeHashElement(element.target, element);
                // [self removeHashElement:element];
            }
        }

        // Update Selector
        this.unscheduleUpdate(target);
	}

    /** Unschedules all selectors from all targets.
      You should NEVER call this method, unless you know what you are doing.

      @since v0.99.3
      */
    public void unscheduleAllSelectors() {
        // Custom Selectors
        for(ConcurrentArrayHashMap<Object, tHashSelectorEntry>.Entry e = hashForSelectors.firstValue();
    				e != null; e = hashForSelectors.nextValue(e)) {
        	tHashSelectorEntry element = e.getValue();
        	
            Object target = element.target;
            unscheduleAllSelectors(target);
        }
        
        // Updates selectors        
        for (tListEntry entry:updates0) {
        	unscheduleUpdate(entry.target);
        }
        for (tListEntry entry:updatesNeg) {
        	unscheduleUpdate(entry.target);
        }
        for (tListEntry entry:updatesPos) {
        	unscheduleUpdate(entry.target);
        }
    }

    /** Resumes the target.
     The 'target' will be unpaused, so all schedule selectors/update will be 'ticked' again.
     If the target is not present, nothing happens.
     @since v0.99.3
    */
	public void resume(Object target) {
        assert  target != null: "target must be non nil";

        // Custom Selectors
        tHashSelectorEntry element = hashForSelectors.get(target);
        if( element != null )
            element.paused = false;

        // Update selector
        tHashSelectorEntry elementUpdate = hashForUpdates.get(target);
        if( elementUpdate != null) {
            assert elementUpdate.target != null: "resumeTarget: unknown error";
            elementUpdate.setPaused(false);
        }	

	}

    /** Pauses the target.
     All scheduled selectors/update for a given target won't be 'ticked' until the target is resumed.
     If the target is not present, nothing happens.
     @since v0.99.3
    */
	public void pause(Object target) {
        assert target != null: "target must be non nil";

        // Custom selectors
        tHashSelectorEntry element = hashForSelectors.get(target);
        if( element != null )
            element.paused = true;

        // Update selector
        tHashSelectorEntry elementUpdate = hashForUpdates.get(target);
        if( elementUpdate != null) {
            assert elementUpdate.target != null:"pauseTarget: unknown error";
            elementUpdate.setPaused(true);
        }

    }

    /** Schedules the 'update' selector for a given target with a given priority.
      The 'update' selector will be called every frame.
      The lower the priority, the earlier it is called.
      @since v0.99.3
    */
	public void scheduleUpdate(Object target, int priority, boolean paused) {
        // TODO Auto-generated method stub
        if (ccConfig.COCOS2D_DEBUG >= 1) {
        	tHashSelectorEntry hashElement = hashForUpdates.get(target);
            assert hashElement == null:"CCScheduler: You can't re-schedule an 'update' selector'. Unschedule it first";
        }

        // most of the updates are going to be 0, that's why there
        // is an special list for updates with priority 0
        if( priority == 0 ) {
        	this.append(updates0, target, paused);
        } else if( priority < 0 ) {
        	this.priority(updatesNeg, target, priority, paused);
        } else { // priority > 0
        	this.priority(updatesPos, target, priority, paused);
        }
	}
	
    /*
     * This is java way version, uses interface based callbacks. UpdateCallback in this case.
     * It would be preffered solution. It is more polite to Java, GC, and obfuscation. 
     * Target class must implement UpdateCallback or scheduleUpdate will be used.
     */
	public void scheduleUpdate(UpdateCallback target, int priority, boolean paused) {
        // TODO Auto-generated method stub
        if (ccConfig.COCOS2D_DEBUG >= 1) {
        	tHashSelectorEntry hashElement = hashForUpdates.get(target);
            assert hashElement == null:"CCScheduler: You can't re-schedule an 'update' selector'. Unschedule it first";
        }

        // most of the updates are going to be 0, that's way there
        // is an special list for updates with priority 0
        if( priority == 0 ) {
        	this.append(updates0, target, paused);
        } else if( priority < 0 ) {
        	this.priority(updatesNeg, target, priority, paused);
        } else { // priority > 0
        	this.priority(updatesPos, target, priority, paused);
        }
	}

    /** schedules a Timer.
     It will be fired in every frame.
     
     @deprecated Use scheduleSelector:forTarget:interval:paused instead. Will be removed in 1.0
    */
    public void scheduleTimer(CCTimer timer) {
        assert false: "Not implemented. Use scheduleSelector:forTarget:";
    }

    /** unschedules an already scheduled Timer
     
     @deprecated Use unscheduleSelector:forTarget. Will be removed in v1.0
     */
    public void unscheduleTimer(CCTimer timer) {
	    assert false: "Not implemented. Use unscheduleSelector:forTarget:";
    }

    /** unschedule all timers.
     You should NEVER call this method, unless you know what you are doing.
     
     @deprecated Use scheduleAllSelectors instead. Will be removed in 1.0
     @since v0.8
     */
    public void unscheduleAllTimers() {
	    assert false:"Not implemented. Use unscheduleAllSelectors";
    }

    @Override
    public void finalize () throws Throwable  {
        unscheduleAllSelectors();
        _sharedScheduler = null;

        super.finalize();
    }

    public void append(ArrayList<tListEntry> list, Object target, boolean paused) {
        tListEntry listElement = new tListEntry();

        listElement.target = target;
        listElement.paused = paused;
        if(target instanceof UpdateCallback) {
        	listElement.callback = (UpdateCallback)target;
        } else {
            try {
    			listElement.impMethod = target.getClass().getMethod(updateSelector, Float.TYPE);
            } catch (NoSuchMethodException e) {
        		e.printStackTrace();
        	}       	
        }

		synchronized (list) {
			list.add(listElement);			
		}

        // update hash entry for quicker access
        tHashSelectorEntry hashElement = new tHashSelectorEntry();
        hashElement.target = target;
        hashElement.list = list;
        hashElement.entry = listElement;
        hashForUpdates.put(target, hashElement);
    }

    public void priority(ArrayList<tListEntry> list, Object target, int priority, boolean paused) {
        tListEntry listElement = new tListEntry();

        listElement.target = target;
        listElement.priority = priority;
        listElement.paused = paused;
        if(target instanceof UpdateCallback) {
        	listElement.callback = (UpdateCallback)target;
        } else {
	        try {
				listElement.impMethod = target.getClass().getMethod(updateSelector, Float.TYPE);
	        } catch (NoSuchMethodException e) {
        		e.printStackTrace();
        	}
        }
		
		synchronized (list) {
			if(list.isEmpty()) {
				list.add(listElement);
			} else {
				boolean added = false;		
				
				int len = list.size();
				for( int i = 0; i < len; i++ ) {
					tListEntry elem = list.get(i);
					if( priority < elem.priority ) {
						list.add(i, listElement);
						added = true;
						break;
					}
				}
				
				// Not added? priority has the higher value. Append it.
				if( !added )
					list.add(listElement);
			}
		}

        tHashSelectorEntry hashElement = new tHashSelectorEntry();
        hashElement.target = target;
        hashElement.list = list;
        hashElement.entry = listElement;  
        hashForUpdates.put(target, hashElement);
    }
}

