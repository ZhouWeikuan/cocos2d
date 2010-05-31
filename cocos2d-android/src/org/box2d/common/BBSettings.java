package org.box2d.common;

public class BBSettings {

    public static float FLT_MAX = Float.MAX_VALUE;
    public static float FLT_EPSILON = .0000001192092896f;
    public static float PI = 3.14159265359f;


    // Collision

    /// The maximum number of contact points between two convex shapes.
    public static int maxManifoldPoints = 2;

    /// The maximum number of vertices on a convex polygon.
    public static int maxPolygonVertices = 8;

    /// This is used to fatten AABBs in the dynamic tree. This allows proxies
    /// to move by a small amount without triggering a tree adjustment.
    /// This is in meters.
    public static float aabbExtension = 0.1f;

    /// This is used to fatten AABBs in the dynamic tree. This is used to predict
    /// the future position based on the current displacement.
    /// This is a dimensionless multiplier.
    public static float aabbMultiplier = 2.0f;

    /// A small length used as a collision and constraint tolerance. Usually it is
    /// chosen to be numerically significant, but visually insignificant.
    public static float linearSlop = 0.005f;

    /// A small angle used as a collision and constraint tolerance. Usually it is
    /// chosen to be numerically significant, but visually insignificant.
    public static float angularSlop = (2.0f / 180.0f * PI);

    /// The radius of the polygon/edge shape skin. This should not be modified. Making
    /// this smaller means polygons will have and insufficient for continuous collision.
    /// Making it larger may create artifacts for vertex collision.
    public static float polygonRadius = (2.0f * linearSlop);


    // Dynamics

    /// Maximum number of contacts to be handled to solve a TOI island.
    public static int maxTOIContactsPerIsland = 32;

    /// Maximum number of joints to be handled to solve a TOI island.
    public static int maxTOIJointsPerIsland = 32;

    /// A velocity threshold for elastic collisions. Any collision with a relative linear
    /// velocity below this threshold will be treated as inelastic.
    public static float velocityThreshold = 1.0f;

    /// The maximum linear position correction used when solving constraints. This helps to
    /// prevent overshoot.
    public static float maxLinearCorrection = 0.2f;

    /// The maximum angular position correction used when solving constraints. This helps to
    /// prevent overshoot.
    public static float maxAngularCorrection = (8.0f / 180.0f * PI);

    /// The maximum linear velocity of a body. This limit is very large and is used
    /// to prevent numerical problems. You shouldn't need to adjust this.
    public static float maxTranslation = 2.0f;
    public static float maxTranslationSquared = (maxTranslation * maxTranslation);

    /// The maximum angular velocity of a body. This limit is very large and is used
    /// to prevent numerical problems. You shouldn't need to adjust this.
    public static float maxRotation = (0.5f * PI);
    public static float maxRotationSquared = (maxRotation * maxRotation);

    /// This scale factor controls how fast overlap is resolved. Ideally this would be 1 so
    /// that overlap is removed in one time step. However using values close to 1 often lead
    /// to overshoot.
    public static float contactBaumgarte = 0.2f;

    // Sleep

    /// The time that a body must be still before it will go to sleep.
    public static float timeToSleep = 0.5f;

    /// A body cannot sleep if its linear velocity is above this tolerance.
    public static float linearSleepTolerance = 0.01f;

    /// A body cannot sleep if its angular velocity is above this tolerance.
    public static float angularSleepTolerance = (2.0f / 180.0f * PI);


    /// Version numbering scheme.
    /// See http://en.wikipedia.org/wiki/Software_versioning
    public static class BBVersion {
        int major;        ///< significant changes
        int minor;        ///< incremental changes
        int revision;        ///< bug fixes

        public BBVersion(int a, int b, int c) {
            major = a;
            minor = b;
            revision = c;
        }
    }

    /// Current version.
    public static BBVersion version = new BBVersion(2, 1, 0);

    /// Friction mixing law. Feel free to customize this.
    public static float mixFriction(float friction1, float friction2) {
        return (float) Math.sqrt(friction1 * friction2);
    }

    /// Restitution mixing law. Feel free to customize this.
    public static float mixRestitution(float restitution1, float restitution2) {
        return restitution1 > restitution2 ? restitution1 : restitution2;
    }


}
