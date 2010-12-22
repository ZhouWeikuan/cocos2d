package org.cocos2d.transitions;

import org.cocos2d.actions.camera.CCOrbitCamera;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.instant.CCShow;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCScene;

// TODO
/**
 * ZoomFlipX Transition.
 * Flips the screen horizontally doing a zoom out/in
 * The front face is the outgoing scene and the back face is the incoming scene.
 */
public class CCZoomFlipXTransition extends CCOrientedTransitionScene {

    public static CCZoomFlipXTransition transition(float t, CCScene s, int orientation) {
        return new CCZoomFlipXTransition(t, s, orientation);
    }

    public CCZoomFlipXTransition(float t, CCScene s, int orientation) {
        super(t, s, orientation);
    }

    public void onEnter() {
        super.onEnter();

        CCIntervalAction inA, outA;
        inScene.setVisible(false);

        float inDeltaZ, inAngleZ;
        float outDeltaZ, outAngleZ;

        if (orientation == tOrientation.kOrientationRightOver) {
            inDeltaZ = 90;
            inAngleZ = 270;
            outDeltaZ = 90;
            outAngleZ = 0;
        } else {
            inDeltaZ = -90;
            inAngleZ = 90;
            outDeltaZ = -90;
            outAngleZ = 0;
        }

        inA = CCSequence.actions(
                CCDelayTime.action(duration / 2),
                CCSpawn.actions(
				CCOrbitCamera.action(duration/2, 1, 0, inAngleZ, inDeltaZ, 0, 0),
				CCScaleTo.action(duration/2, 1),
				CCShow.action()
			),
                CCCallFunc.action(this, "finish"));
        outA = CCSequence.actions(
			CCSpawn.actions(
				CCOrbitCamera.action(duration/2, 1, 0, outAngleZ, outDeltaZ, 0, 0),
				CCScaleTo.action(duration/2, 0.5f)
			),
                CCHide.action(),
                CCDelayTime.action(duration / 2));

        inScene.setScale(0.5f);
        inScene.runAction(inA);
        outScene.runAction(outA);
    }

}
