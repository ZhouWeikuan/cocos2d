package org.cocos2d.actions;

import android.util.Log;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/** CCActionManager is a singleton that manages all the actions.
 Normally you won't need to use this singleton directly. 99% of the cases you will use the CCNode interface,
 which uses this singleton.
 But there are some cases where you might need to use this singleton.
 Examples:
	- When you want to run an action where the target is different from a CCNode. 
	- When you want to pause / resume the actions
 
 @since v0.8
 */
public class CCActionManager {
    private static final String LOG_TAG = CCActionManager.class.getSimpleName();

    static class HashElement {
        CopyOnWriteArrayList<CCAction> actions;
        CCNode target;
        int actionIndex;
        CCAction currentAction;
        boolean currentActionSalvaged;
        boolean paused;

        HashElement(CCNode t, boolean p) {
            target = t;
            paused = p;
        }

        public String toString() {
            String s = "target=" + target + ", paused=" + paused + ", actions=" + actions + "\n";
            for (CCAction a : actions) {
                s += a.toString() + "\n";
            }
            return s;
        }
    }


    /**
     * ActionManager is a singleton that manages all the actions.
     * Normally you won't need to use this singleton directly. 99% of the cases you will use the CocosNode interface,
     * which uses this singleton.
     * But there are some cases where you might need to use this singleton.
     * Examples:
     * - When you want to run an action where the target is different from a CocosNode.
     * - When you want to pause / resume the actions
     *
     * @since v0.8
     */

    private ConcurrentHashMap<CCNode, HashElement> targets;
//    private HashElement	currentTarget;
//    private boolean currentTargetSalvaged;

    /**
     * returns a shared instance of the ActionManager
     */
    private static CCActionManager _sharedManager = null;

    /** returns a shared instance of the CCActionManager */
    public static CCActionManager sharedManager() {
    	if (_sharedManager != null)
    		return _sharedManager;
        synchronized (CCActionManager.class) {
            if (_sharedManager == null) {
                _sharedManager = new CCActionManager();
            }
            return _sharedManager;
        }
    }

    private CCActionManager() {
    	CCScheduler.sharedScheduler().scheduleUpdate(this, 0, false);
    	targets = new ConcurrentHashMap<CCNode, HashElement>(131);
    }
    
    @Override
    public void finalize () {
    	ccMacros.CCLOGINFO(LOG_TAG, "cocos2d: deallocing " + this.toString());
    	
    	this.removeAllActions();
    	_sharedManager = null;
    }

    private void deleteHashElement(HashElement element) {
        element.actions.clear();
        targets.remove(element.target);
    }

    private void actionAlloc(HashElement element) {
        if (element.actions == null)
            element.actions = new CopyOnWriteArrayList<CCAction>(); // 4 actions per node by default
    }

    private void removeAction(int index, HashElement element) {
        element.actions.remove(index);
        if (element.actionIndex >= index)
        	element.actionIndex--;
        
        if (element.actions.isEmpty()) {
            deleteHashElement(element);
        }
    }

    // actions

    // TODO figure out why the target not found
    /**
     * Pauses all actions for a certain target.
     * When the actions are paused, they won't be "ticked".
     */
    @Deprecated
    public void pauseAllActions(CCNode target) {
    	this.pause(target);
    }


    /**
     * Resumes all actions for a certain target.
     * Once the actions are resumed, they will be "ticked" in every frame.
     */
    @Deprecated
    public void resumeAllActions(CCNode target) {
       this.resume(target);
    }
  
    /** Adds an action with a target.
    If the target is already present, then the action will be added to the existing target.
    If the target is not present, a new instance of this target will be created either paused or paused, and the action will be added to the newly created target.
    When the target is paused, the queued actions won't be 'ticked'.
    */
    public void addAction(CCAction action, CCNode target, boolean paused) {
        assert action != null : "Argument action must be non-null";
        assert target != null : "Argument target must be non-null";

        HashElement element = targets.get(target);
        if (element == null) {
            element = new HashElement(target, paused);
            targets.put(target, element);
        }

        actionAlloc(element);

        assert !element.actions.contains(action) : "runAction: Action already running";

        element.actions.add(action);

        action.start(target);
    }    

    /**
     * Removes all actions from all the targers.
     */
    public void removeAllActions() {
        for (HashElement element : targets.values()) {
            removeAllActions(element.target);
        }
    }

    /**
     * Removes all actions from a certain target.
     * All the actions that belongs to the target will be removed.
     */
    public void removeAllActions(CCNode target) {
        // explicit null handling
        if (target == null)
            return;

        HashElement element = targets.get(target);
        if (element != null) {
//            if( element.actions.contains(element.currentAction) && !element.currentActionSalvaged ) {
//                element.currentActionSalvaged = true;
//            }

            element.actions.clear();
//            if( currentTarget == element )
//                currentTargetSalvaged = true;
//            else
            deleteHashElement(element);

        } else {
            // Log.w(LOG_TAG, "removeAllActions: target not found");
        }
    }

    /**
     * Removes an action given an action reference.
     */
    public void removeAction(CCAction action) {
    	if (action == null)
    		return;
        HashElement element = targets.get(action.getOriginalTarget());
        if (element != null) {
            int i = element.actions.indexOf(action);
            if (i != -1) {
                removeAction(i, element);
            }
        } else {
            Log.w(LOG_TAG, "removeAction: target not found");
        }
    }   

    /**
     * Removes an action given its tag and the target
     */
    public void removeAction(int tag, CCNode target) {
        assert tag != CCAction.kCCActionTagInvalid : "Invalid tag";

        HashElement element = targets.get(target);
        if (element != null) {
            if (element.actions != null) {
                int limit = element.actions.size();
                for (int i = 0; i < limit; i++) {
                    CCAction a = element.actions.get(i);
                    if (a.getTag() == tag && a.getOriginalTarget() == target)
                        removeAction(i, element);
                }
            } else {
                // Log.w(LOG_TAG, "removeAction: Action not found");
            }
        } else {
            // Log.w(LOG_TAG, "removeAction: target not found");
        }
    }

    /**
     * Gets an action given its tag and a target
     *
     * @return the Action with the given tag
     */
    public CCAction getAction(int tag, CCNode target) {
        assert tag != CCAction.kCCActionTagInvalid : "Invalid tag";

        HashElement element = targets.get(target);
        if (element != null) {
            if (element.actions != null) {
                int limit = element.actions.size();
                for (int i = 0; i < limit; i++) {
                    CCAction a = element.actions.get(i);
                    if (a.getTag() == tag)
                        return a;
                }
            } else {
                // Log.w(LOG_TAG, "getAction: Action not found");
            }
        } else {
            // Log.w(LOG_TAG, "getAction: target not found");
        }
        
        return null;
    }

    /**
     * Returns the numbers of actions that are running in a certain target
     * Composable actions are counted as 1 action. Example:
     * If you are running 1 Sequence of 7 actions, it will return 1.
     * If you are running 7 Sequences of 2 actions, it will return 7.
     */
    public int numberOfRunningActions(CCNode target) {
        HashElement element = targets.get(target);
        if (element != null) {
            return element.actions != null ? element.actions.size() : 0;
        }
        
        return 0;
    }

    public void update(float dt) {
        for (HashElement currentTarget : targets.values()) {
            if (!currentTarget.paused) {
                // The 'actions' may change while inside this loop.
                for (currentTarget.actionIndex = 0; 
                	currentTarget.actionIndex < currentTarget.actions.size();
                	currentTarget.actionIndex++) {
                    
                	currentTarget.currentAction = currentTarget.actions.get(currentTarget.actionIndex);

                    currentTarget.currentAction.step(dt);
                    if (currentTarget.currentAction.isDone()) {
                        currentTarget.currentAction.stop();

                        removeAction(currentTarget.currentAction);
                    }
                    
                    currentTarget.currentAction = null;
                }
            }

            if (currentTarget.actions.isEmpty())
                deleteHashElement(currentTarget);
        }
    }

	public void resume(CCNode target) {
		HashElement element = targets.get(target);
		if (element != null)
			element.paused = false;
	}

	public void pause(CCNode target) {
		HashElement element = targets.get(target);
    	if( element != null )
	    	element.paused = true;
	}
	
    /** purges the shared action manager. It releases the retained instance.
    @since v0.99.0
    */
	public static void purgeSharedManager() {
		if (_sharedManager != null) {
			CCScheduler.sharedScheduler().unscheduleUpdate(_sharedManager);
			_sharedManager = null;
		}
	}
}
