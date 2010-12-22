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
 * ZoomFlipY Transition.
 * Flips the screen vertically doing a little zooming out/in
 * The front face is the outgoing scene and the back face is the incoming scene.
 */
public class CCZoomFlipYTransition extends CCOrientedTransitionScene {

    public static CCZoomFlipYTransition transition(float t, CCScene s, int orientation) {
        return new CCZoomFlipYTransition(t, s, orientation);
    }

    public CCZoomFlipYTransition(float t, CCScene s, int orientation) {
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
			CCOrbitCamera.action(duration/2, 1, 0, inAngleZ, inDeltaZ, 90, 0),
			CCScaleTo.action(duration/2, 1),
			CCShow.action()
				),
                CCCallFunc.action(this, "finish"));
        outA = CCSequence.actions(
			CCSpawn.actions(
				CCOrbitCamera.action(duration/2, 1, 0, outAngleZ, outDeltaZ, 90, 0),
					CCScaleTo.action(duration/2, 0.5f)
				),
                CCHide.action(),
                CCDelayTime.action(duration / 2));

        inScene.setScale(0.5f);
        inScene.runAction(inA);
        outScene.runAction(outA);
    }
}
