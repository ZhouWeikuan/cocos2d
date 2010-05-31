package org.cocos2d.box2d;

public class Settings {
    
    public Settings() {
        hz = 60.0f;
        velocityIterations = 8;
        positionIterations = 3;
        drawStats = 0;
        drawShapes = 1;
        drawJoints = 1;
        drawAABBs = 0;
        drawPairs = 0;
        drawContactPoints = 0;
        drawContactNormals = 0;
        drawContactForces = 0;
        drawFrictionForces = 0;
        drawCOMs = 0;
        enableWarmStarting = 1;
        enableContinuous = 1;
        pause = 0;
        singleStep = 0;
    }
	
    public float hz;
    public int velocityIterations;
    public int positionIterations;
    public int drawShapes;
    public int drawJoints;
    public int drawAABBs;
    public int drawPairs;
    public int drawContactPoints;
    public int drawContactNormals;
    public int drawContactForces;
    public int drawFrictionForces;
    public int drawCOMs;
    public int drawStats;
    public int enableWarmStarting;
    public int enableContinuous;
    public int pause;
    public int singleStep;
    
}
