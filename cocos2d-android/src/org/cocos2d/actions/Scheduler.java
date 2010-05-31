package org.cocos2d.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Scheduler {
    ArrayList<Timer> scheduledMethods;
    ArrayList<Timer> methodsToRemove;
    ArrayList<Timer> methodsToAdd;

    private float timeScale_;

    public float getTimeScale() {
        return timeScale_;
    }

    public void setTimeScale(float ts) {
        timeScale_ = ts;
    }

    private static Scheduler _sharedScheduler = null;

    // Singleton
    public static Scheduler sharedScheduler() {
        synchronized (Scheduler.class) {
            if (_sharedScheduler == null) {
                _sharedScheduler = new Scheduler();
            }
            return _sharedScheduler;
        }
    }

    private Scheduler() {
        scheduledMethods = new ArrayList<Timer>(50);
        methodsToRemove = new ArrayList<Timer>(20);
        methodsToAdd = new ArrayList<Timer>(20);

        timeScale_ = 1.0f;
    }

    public void schedule(Timer t) {
        // it is possible that sometimes (in transitions in particular) an scene unschedule a timer
        // and before the timer is deleted, it is re-scheduled
        if (methodsToRemove.contains(t)) {
            methodsToRemove.remove(t);
            return;
        }

        if (scheduledMethods.contains(t) || methodsToAdd.contains(t)) {
            //Log("Scheduler.schedulerTimer: timer %s already scheduled", t);
            throw new SchedulerTimerAlreadyScheduled("Scheduler.scheduleTimer already scheduled");
        }

        methodsToAdd.add(t);
    }

    public void unschedule(Timer t) {
        // someone wants to remove it before it was added
        if (methodsToAdd.contains(t)) {
            methodsToAdd.remove(t);
            return;
        }

        if (!scheduledMethods.contains(t)) {
            //Log("Scheduler.unscheduleTimer: timer not scheduled");
            throw new SchedulerTimerNotFound("Scheduler.unscheduleTimer not found");
        }

        methodsToRemove.add(t);
    }

    public void tick(float dt) {
        if (timeScale_ != 1.0f)
            dt *= timeScale_;

        for (Timer k : methodsToRemove)
            scheduledMethods.remove(k);
        methodsToRemove.clear();

        for (Timer k : methodsToAdd)
            scheduledMethods.add(k);
        methodsToAdd.clear();

        for (Timer t : scheduledMethods) {
            t.fire(dt);
        }
    }

    static class SchedulerTimerAlreadyScheduled extends RuntimeException {
        public SchedulerTimerAlreadyScheduled(String reason) {
            super(reason);
        }
    }

    static class SchedulerTimerNotFound extends RuntimeException {
        public SchedulerTimerNotFound(String reason) {
            super(reason);
        }
    }

    public static class Timer {
        private Object target;
        private String selector;
        private Method invocation;

        public float interval;
        float elapsed;

        public Timer(Object t, String s) {
            this(t, s, 0);
        }

        public Timer(Object t, String s, float seconds) {
            target = t;
            selector = s;

            interval = seconds;

            try {
                Class<?> cls = target.getClass();
                invocation = cls.getMethod(s, new Class[]{Float.TYPE});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setInterval(float i) {
            interval = i;
        }

        public float getInterval() {
            return interval;
        }

        public void fire(float dt) {
            elapsed += dt;
            if (elapsed >= interval) {
                try {
                    invocation.invoke(target, new Object[]{elapsed});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                elapsed = 0;
            }
        }

    }

}

