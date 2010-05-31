package org.box2d.common;

public class BBMath {

    public static final BBVec2 vec2_zero = new BBVec2(0.0f, 0.0f);
    public static final BBMat22 bmat22_identity = new BBMat22(1.0f, 0.0f, 0.0f, 1.0f);
    public static final BBTransform transform_identity = new BBTransform(vec2_zero, bmat22_identity);


    /// This function is used to ensure that a floating point number is
    /// not a NaN or infinity.
    public static boolean isValid(float x) {
        return !Float.isNaN(x) && Float.NEGATIVE_INFINITY < x & x < Float.POSITIVE_INFINITY;

    }

    /// This is a approximate yet fast inverse square-root.
    public static float invSqrt(float x) {
        return 1 / (float) Math.sqrt(x);
    }

    public static float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x);
    }

    public static float abs(float a) {
        return a > 0.0f ? a : -a;
    }

    /// Perform the dot product on two vectors.
    public static float dot(final BBVec2 a, final BBVec2 b) {
        return a.x * b.x + a.y * b.y;
    }

    /// Perform the cross product on two vectors. In 2D this produces a scalar.
    public static float cross(final BBVec2 a, final BBVec2 b) {
        return a.x * b.y - a.y * b.x;
    }

    /// Perform the cross product on a vector and a scalar. In 2D this produces
    /// a vector.
    public static BBVec2 cross(final BBVec2 a, float s) {
        return new BBVec2(s * a.y, -s * a.x);
    }

    /// Perform the cross product on a scalar and a vector. In 2D this produces
    /// a vector.
    public static BBVec2 cross(float s, final BBVec2 a) {
        return new BBVec2(-s * a.y, s * a.x);
    }

    /// Multiply a matrix times a vector. If a rotation matrix is provided,
    /// then this transforms the vector from one frame to another.
    public static BBVec2 mul(final BBMat22 A, final BBVec2 v) {
        return new BBVec2(A.col1.x * v.x + A.col2.x * v.y, A.col1.y * v.x + A.col2.y * v.y);
    }

    /// Multiply a matrix transpose times a vector. If a rotation matrix is provided,
    /// then this transforms the vector from one frame to another (inverse transform).
    public static BBVec2 mulT(final BBMat22 A, final BBVec2 v) {
        return new BBVec2(dot(v, A.col1), dot(v, A.col2));
    }

    /// Add two vectors component-wise.
    public static BBVec2 add(final BBVec2 a, final BBVec2 b) {
        return new BBVec2(a.x + b.x, a.y + b.y);
    }

    /// Subtract two vectors component-wise.
    public static BBVec2 sub(final BBVec2 a, final BBVec2 b) {
        return new BBVec2(a.x - b.x, a.y - b.y);
    }

    public static BBVec2 mul(float s, final BBVec2 a) {
        return new BBVec2(s * a.x, s * a.y);
    }

    public static boolean equals(final BBVec2 a, final BBVec2 b) {
        return a.x == b.x & a.y == b.y;
    }

    public static float distance(final BBVec2 a, final BBVec2 b) {
        BBVec2 c = sub(a, b);
        return c.length();
    }

    public static float distanceSquared(final BBVec2 a, final BBVec2 b) {
        BBVec2 c = sub(a, b);
        return dot(c, c);
    }

    public static BBVec3 mul(float s, final BBVec3 a) {
        return new BBVec3(s * a.x, s * a.y, s * a.z);
    }

    /// Add two vectors component-wise.
    public static BBVec3 add(final BBVec3 a, final BBVec3 b) {
        return new BBVec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /// Subtract two vectors component-wise.
    public static BBVec3 sub(final BBVec3 a, final BBVec3 b) {
        return new BBVec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /// Perform the dot product on two vectors.
    public static float dot(final BBVec3 a, final BBVec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /// Perform the cross product on two vectors.
    public static BBVec3 cross(final BBVec3 a, final BBVec3 b) {
        return new BBVec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public static BBMat22 add(final BBMat22 A, final BBMat22 B) {
        return new BBMat22(add(A.col1, B.col1), add(A.col2, B.col2));
    }

    // A * B
    public static BBMat22 mul(final BBMat22 A, final BBMat22 B) {
        return new BBMat22(mul(A, B.col1), mul(A, B.col2));
    }

    // A^T * B
    public static BBMat22 mulT(final BBMat22 A, final BBMat22 B) {
        BBVec2 c1 = new BBVec2(dot(A.col1, B.col1), dot(A.col2, B.col1));
        BBVec2 c2 = new BBVec2(dot(A.col1, B.col2), dot(A.col2, B.col2));
        return new BBMat22(c1, c2);
    }

    /// Multiply a matrix times a vector.
    public static BBVec3 mul(final BBMat33 A, final BBVec3 v) {
        return add(add(mul(v.x, A.col1), mul(v.y, A.col2)), mul(v.z, A.col3));
    }

    public static BBVec2 mul(final BBTransform T, final BBVec2 v) {
        float x = T.position.x + T.R.col1.x * v.x + T.R.col2.x * v.y;
        float y = T.position.y + T.R.col1.y * v.x + T.R.col2.y * v.y;

        return new BBVec2(x, y);
    }

    public static BBVec2 mulT(final BBTransform T, final BBVec2 v) {
        return BBMath.mulT(T.R, sub(v, T.position));
    }

    public static BBVec2 abs(final BBVec2 a) {
        return new BBVec2(abs(a.x), abs(a.y));
    }

    public static BBMat22 abs(final BBMat22 A) {
        return new BBMat22(abs(A.col1), abs(A.col2));
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static BBVec2 min(final BBVec2 a, final BBVec2 b) {
        return new BBVec2(min(a.x, b.x), min(a.y, b.y));
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static BBVec2 max(final BBVec2 a, final BBVec2 b) {
        return new BBVec2(max(a.x, b.x), max(a.y, b.y));
    }

    public static float clamp(float a, float low, float high) {
        return max(low, min(a, high));
    }

    public static BBVec2 clamp(final BBVec2 a, final BBVec2 low, final BBVec2 high) {
        return max(low, min(a, high));
    }

    public static void swap(float a, float b) {
        float tmp = a;
        a = b;
        b = tmp;
    }

    public static void swap(int a, int b) {
        int tmp = a;
        a = b;
        b = tmp;
    }

    public static <T> void swap(T a, T b) {
        T tmp = a;
        a = b;
        b = tmp;
    }

    /// "Next Largest Power of 2
    /// Given a binary integer value x, the next largest power of 2 can be computed by a SWAR algorithm
    /// that recursively "folds" the upper bits into the lower bits. This process yields a bit vector with
    /// the same most significant 1 as x, but all 1's below it. Adding 1 to that value yields the next
    /// largest power of 2. For a 32-bit value:"
    public static int nextPowerOfTwo(int x) {
        x |= (x >> 1);
        x |= (x >> 2);
        x |= (x >> 4);
        x |= (x >> 8);
        x |= (x >> 16);
        return x + 1;
    }

    public static boolean isPowerOfTwo(int x) {
        return x > 0 & (x & (x - 1)) == 0;
    }


}
