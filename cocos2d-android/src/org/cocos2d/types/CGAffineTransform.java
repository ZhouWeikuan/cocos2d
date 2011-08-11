package org.cocos2d.types;

import java.io.IOException;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Denis M. Kishenko
 * @version $Revision$
 */
// | m[0] m[4] m[8]  m[12] |     | m11 m21 m31 m41 |     | a c 0 tx |       |m00 m01   m02 |
// | m[1] m[5] m[9]  m[13] |     | m12 m22 m32 m42 |     | b d 0 ty |       |m10 m11   m12 |
// | m[2] m[6] m[10] m[14] | <=> | m13 m23 m33 m43 | <=> | 0 0 1  0 |   <=> |              |
// | m[3] m[7] m[11] m[15] |     | m14 m24 m34 m44 |     | 0 0 0  1 |       |              |

/**
 * The Class AffineTransform represents a linear transformation (rotation,
 * scaling, or shear) followed by a translation that acts on a coordinate space.
 * It preserves collinearity of points and ratios of distances between collinear
 * points: so if A, B, and C are on a line, then after the space has been
 * transformed via the affine transform, the images of the three points will
 * still be on a line, and the ratio of the distance from A to B with the
 * distance from B to C will be the same as the corresponding ratio in the image
 * space.
 *
 * @since Android 1.0
 */
public class CGAffineTransform {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1330973210523860834L;

    /**
     * The Constant TYPE_IDENTITY.
     */
    public static final int TYPE_IDENTITY = 0;

    /**
     * The Constant TYPE_TRANSLATION.
     */
    public static final int TYPE_TRANSLATION = 1;

    /**
     * The Constant TYPE_UNIFORM_SCALE.
     */
    public static final int TYPE_UNIFORM_SCALE = 2;

    /**
     * The Constant TYPE_GENERAL_SCALE.
     */
    public static final int TYPE_GENERAL_SCALE = 4;

    /**
     * The Constant TYPE_QUADRANT_ROTATION.
     */
    public static final int TYPE_QUADRANT_ROTATION = 8;

    /**
     * The Constant TYPE_GENERAL_ROTATION.
     */
    public static final int TYPE_GENERAL_ROTATION = 16;

    /**
     * The Constant TYPE_GENERAL_TRANSFORM.
     */
    public static final int TYPE_GENERAL_TRANSFORM = 32;

    /**
     * The Constant TYPE_FLIP.
     */
    public static final int TYPE_FLIP = 64;

    /**
     * The Constant TYPE_MASK_SCALE.
     */
    public static final int TYPE_MASK_SCALE = TYPE_UNIFORM_SCALE | TYPE_GENERAL_SCALE;

    /**
     * The Constant TYPE_MASK_ROTATION.
     */
    public static final int TYPE_MASK_ROTATION = TYPE_QUADRANT_ROTATION | TYPE_GENERAL_ROTATION;

    /**
     * The <code>TYPE_UNKNOWN</code> is an initial type value.
     */
    static final int TYPE_UNKNOWN = -1;

    /**
     * The min value equivalent to zero. If absolute value less then ZERO it
     * considered as zero.
     */
    public static final double ZERO = 1E-10;

    /**
     * The values of transformation matrix.
     */
    public double m00;

    /**
     * The m10.
     */
    public double m10;

    /**
     * The m01.
     */
    public double m01;

    /**
     * The m11.
     */
    public double m11;

    /**
     * The m02.
     */
    public double m02;

    /**
     * The m12.
     */
    public double m12;

    /**
     * The transformation <code>type</code>.
     */
    transient int type;

    public static CGAffineTransform identity() {
        return new CGAffineTransform();
    }
  
    /**
     * Instantiates a new affine transform of type <code>TYPE_IDENTITY</code>
     * (which leaves coordinates unchanged).
     */
    public CGAffineTransform() {
        type = TYPE_IDENTITY;
        m00 = m11 = 1.0;
        m10 = m01 = m02 = m12 = 0.0;
    }

    /**
     * Instantiates a new affine transform that has the same data as the given
     * AffineTransform.
     *
     * @param t the transform to copy.
     */
    public CGAffineTransform(CGAffineTransform t) {
        this.type = t.type;
        this.m00 = t.m00;
        this.m10 = t.m10;
        this.m01 = t.m01;
        this.m11 = t.m11;
        this.m02 = t.m02;
        this.m12 = t.m12;
    }

    /**
     * Instantiates a new affine transform by specifying the values of the 2x3
     * transformation matrix as floats. The type is set to the default type:
     * <code>TYPE_UNKNOWN</code>
     *
     * @param m00 the m00 entry in the transformation matrix.
     * @param m10 the m10 entry in the transformation matrix.
     * @param m01 the m01 entry in the transformation matrix.
     * @param m11 the m11 entry in the transformation matrix.
     * @param m02 the m02 entry in the transformation matrix.
     * @param m12 the m12 entry in the transformation matrix.
     */
    public CGAffineTransform(float m00, float m10, float m01, float m11, float m02, float m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    /**
     * Instantiates a new affine transform by specifying the values of the 2x3
     * transformation matrix as doubles. The type is set to the default type:
     * <code>TYPE_UNKNOWN</code>
     *
     * @param m00 the m00 entry in the transformation matrix.
     * @param m10 the m10 entry in the transformation matrix.
     * @param m01 the m01 entry in the transformation matrix.
     * @param m11 the m11 entry in the transformation matrix.
     * @param m02 the m02 entry in the transformation matrix.
     * @param m12 the m12 entry in the transformation matrix.
     */
    public CGAffineTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }
    
    public static CGAffineTransform make(double m00, double m10, double m01, double m11, double m02, double m12) {
        return new CGAffineTransform(m00, m10, m01, m11, m02, m12);
    }

    /**
     * Instantiates a new affine transform by reading the values of the
     * transformation matrix from an array of floats. The mapping from the array
     * to the matrix starts with <code>matrix[0]</code> giving the top-left
     * entry of the matrix and proceeds with the usual left-to-right and
     * top-down ordering.
     * <p/>
     * If the array has only four entries, then the two entries of the last row
     * of the transformation matrix default to zero.
     *
     * @param matrix the array of four or six floats giving the values of the
     *               matrix.
     * @throws ArrayIndexOutOfBoundsException if the size of the array is 0, 1, 2, 3, or 5.
     */
    public CGAffineTransform(float[] matrix) {
        this.type = TYPE_UNKNOWN;
        m00 = matrix[0];
        m10 = matrix[1];
        m01 = matrix[2];
        m11 = matrix[3];
        if (matrix.length > 4) {
            m02 = matrix[4];
            m12 = matrix[5];
        }
    }

    /**
     * Instantiates a new affine transform by reading the values of the
     * transformation matrix from an array of doubles. The mapping from the
     * array to the matrix starts with <code>matrix[0]</code> giving the
     * top-left entry of the matrix and proceeds with the usual left-to-right
     * and top-down ordering.
     * <p/>
     * If the array has only four entries, then the two entries of the last row
     * of the transformation matrix default to zero.
     *
     * @param matrix the array of four or six doubles giving the values of the
     *               matrix.
     * @throws ArrayIndexOutOfBoundsException if the size of the array is 0, 1, 2, 3, or 5.
     */
    public CGAffineTransform(double[] matrix) {
        this.type = TYPE_UNKNOWN;
        m00 = matrix[0];
        m10 = matrix[1];
        m01 = matrix[2];
        m11 = matrix[3];
        if (matrix.length > 4) {
            m02 = matrix[4];
            m12 = matrix[5];
        }
    }

    /**
     * Returns type of the affine transformation.
     * <p/>
     * The type is computed as follows: Label the entries of the transformation
     * matrix as three rows (m00, m01), (m10, m11), and (m02, m12). Then if the
     * original basis vectors are (1, 0) and (0, 1), the new basis vectors after
     * transformation are given by (m00, m01) and (m10, m11), and the
     * translation vector is (m02, m12).
     * <p/>
     * The types are classified as follows: <br/> TYPE_IDENTITY - no change<br/>
     * TYPE_TRANSLATION - The translation vector isn't zero<br/>
     * TYPE_UNIFORM_SCALE - The new basis vectors have equal length<br/>
     * TYPE_GENERAL_SCALE - The new basis vectors dont' have equal length<br/>
     * TYPE_FLIP - The new basis vector orientation differs from the original
     * one<br/> TYPE_QUADRANT_ROTATION - The new basis is a rotation of the
     * original by 90, 180, 270, or 360 degrees<br/> TYPE_GENERAL_ROTATION - The
     * new basis is a rotation of the original by an arbitrary angle<br/>
     * TYPE_GENERAL_TRANSFORM - The transformation can't be inverted.<br/>
     * <p/>
     * Note that multiple types are possible, thus the types can be combined
     * using bitwise combinations.
     *
     * @return the type of the Affine Transform.
     */
    public int getType() {
        if (type != TYPE_UNKNOWN) {
            return type;
        }

        int type = 0;

        if (m00 * m01 + m10 * m11 != 0.0) {
            type |= TYPE_GENERAL_TRANSFORM;
            return type;
        }

        if (m02 != 0.0 || m12 != 0.0) {
            type |= TYPE_TRANSLATION;
        } else if (m00 == 1.0 && m11 == 1.0 && m01 == 0.0 && m10 == 0.0) {
            type = TYPE_IDENTITY;
            return type;
        }

        if (m00 * m11 - m01 * m10 < 0.0) {
            type |= TYPE_FLIP;
        }

        double dx = m00 * m00 + m10 * m10;
        double dy = m01 * m01 + m11 * m11;
        if (dx != dy) {
            type |= TYPE_GENERAL_SCALE;
        } else if (dx != 1.0) {
            type |= TYPE_UNIFORM_SCALE;
        }

        if ((m00 == 0.0 && m11 == 0.0) || (m10 == 0.0 && m01 == 0.0 && (m00 < 0.0 || m11 < 0.0))) {
            type |= TYPE_QUADRANT_ROTATION;
        } else if (m01 != 0.0 || m10 != 0.0) {
            type |= TYPE_GENERAL_ROTATION;
        }

        return type;
    }

    /**
     * Gets the scale x entry of the transformation matrix (the upper left
     * matrix entry).
     *
     * @return the scale x value.
     */
    public double getScaleX() {
        return m00;
    }

    /**
     * Gets the scale y entry of the transformation matrix (the lower right
     * entry of the linear transformation).
     *
     * @return the scale y value.
     */
    public double getScaleY() {
        return m11;
    }

    /**
     * Gets the shear x entry of the transformation matrix (the upper right
     * entry of the linear transformation).
     *
     * @return the shear x value.
     */
    public double getShearX() {
        return m01;
    }

    /**
     * Gets the shear y entry of the transformation matrix (the lower left entry
     * of the linear transformation).
     *
     * @return the shear y value.
     */
    public double getShearY() {
        return m10;
    }

    /**
     * Gets the x coordinate of the translation vector.
     *
     * @return the x coordinate of the translation vector.
     */
    public double getTranslateX() {
        return m02;
    }

    /**
     * Gets the y coordinate of the translation vector.
     *
     * @return the y coordinate of the translation vector.
     */
    public double getTranslateY() {
        return m12;
    }

    /**
     * Checks if the AffineTransformation is the identity.
     *
     * @return true, if the AffineTransformation is the identity.
     */
    public boolean isIdentity() {
        return getType() == TYPE_IDENTITY;
    }

    /**
     * Writes the values of the transformation matrix into the given array of
     * doubles. If the array has length 4, only the linear transformation part
     * will be written into it. If it has length greater than 4, the translation
     * vector will be included as well.
     *
     * @param matrix the array to fill with the values of the matrix.
     * @throws ArrayIndexOutOfBoundsException if the size of the array is 0, 1, 2, 3, or 5.
     */
    public void getMatrix(double[] matrix) {
        matrix[0] = m00;
        matrix[1] = m10;
        matrix[2] = m01;
        matrix[3] = m11;
        if (matrix.length > 4) {
            matrix[4] = m02;
            matrix[5] = m12;
        }
    }

    /**
     * Gets the determinant of the linear transformation matrix.
     *
     * @return the determinant of the linear transformation matrix.
     */
    public double getDeterminant() {
        return m00 * m11 - m01 * m10;
    }

    /**
     * Sets the transform in terms of a list of double values.
     *
     * @param m00 the m00 coordinate of the transformation matrix.
     * @param m10 the m10 coordinate of the transformation matrix.
     * @param m01 the m01 coordinate of the transformation matrix.
     * @param m11 the m11 coordinate of the transformation matrix.
     * @param m02 the m02 coordinate of the transformation matrix.
     * @param m12 the m12 coordinate of the transformation matrix.
     */
    public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    /**
     * Sets the transform's data to match the data of the transform sent as a
     * parameter.
     *
     * @param t the transform that gives the new values.
     */
    public void setTransform(CGAffineTransform t) {
        setTransform(t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
        type = t.type;
    }

    /**
     * Sets the transform to the identity transform.
     */
    public void setToIdentity() {
        type = TYPE_IDENTITY;
        m00 = m11 = 1.0;
        m10 = m01 = m02 = m12 = 0.0;
    }

    /**
     * Sets the transformation to a translation alone. Sets the linear part of
     * the transformation to identity and the translation vector to the values
     * sent as parameters. Sets the type to <code>TYPE_IDENTITY</code> if the
     * resulting AffineTransformation is the identity transformation, otherwise
     * sets it to <code>TYPE_TRANSLATION</code>.
     *
     * @param mx the distance to translate in the x direction.
     * @param my the distance to translate in the y direction.
     */
    public void setToTranslation(double mx, double my) {
        m00 = m11 = 1.0;
        m01 = m10 = 0.0;
        m02 = mx;
        m12 = my;
        if (mx == 0.0 && my == 0.0) {
            type = TYPE_IDENTITY;
        } else {
            type = TYPE_TRANSLATION;
        }
    }

    /**
     * Sets the transformation to being a scale alone, eliminating rotation,
     * shear, and translation elements. Sets the type to
     * <code>TYPE_IDENTITY</code> if the resulting AffineTransformation is the
     * identity transformation, otherwise sets it to <code>TYPE_UNKNOWN</code>.
     *
     * @param scx the scaling factor in the x direction.
     * @param scy the scaling factor in the y direction.
     */
    public void setToScale(double scx, double scy) {
        m00 = scx;
        m11 = scy;
        m10 = m01 = m02 = m12 = 0.0;
        if (scx != 1.0 || scy != 1.0) {
            type = TYPE_UNKNOWN;
        } else {
            type = TYPE_IDENTITY;
        }
    }

    /**
     * Sets the transformation to being a shear alone, eliminating rotation,
     * scaling, and translation elements. Sets the type to
     * <code>TYPE_IDENTITY</code> if the resulting AffineTransformation is the
     * identity transformation, otherwise sets it to <code>TYPE_UNKNOWN</code>.
     *
     * @param shx the shearing factor in the x direction.
     * @param shy the shearing factor in the y direction.
     */
    public void setToShear(double shx, double shy) {
        m00 = m11 = 1.0;
        m02 = m12 = 0.0;
        m01 = shx;
        m10 = shy;
        if (shx != 0.0 || shy != 0.0) {
            type = TYPE_UNKNOWN;
        } else {
            type = TYPE_IDENTITY;
        }
    }

    /**
     * Sets the transformation to being a rotation alone, eliminating shearing,
     * scaling, and translation elements. Sets the type to
     * <code>TYPE_IDENTITY</code> if the resulting AffineTransformation is the
     * identity transformation, otherwise sets it to <code>TYPE_UNKNOWN</code>.
     *
     * @param angle the angle of rotation in radians.
     */
    public void setToRotation(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        if (Math.abs(cos) < ZERO) {
            cos = 0.0;
            sin = sin > 0.0 ? 1.0 : -1.0;
        } else if (Math.abs(sin) < ZERO) {
            sin = 0.0;
            cos = cos > 0.0 ? 1.0 : -1.0;
        }
        m00 = m11 = cos;
        m01 = -sin;
        m10 = sin;
        m02 = m12 = 0.0;
        type = TYPE_UNKNOWN;
    }

    /**
     * Sets the transformation to being a rotation followed by a translation.
     * Sets the type to <code>TYPE_UNKNOWN</code>.
     *
     * @param angle the angle of rotation in radians.
     * @param px    the distance to translate in the x direction.
     * @param py    the distance to translate in the y direction.
     */
    public void setToRotation(double angle, double px, double py) {
        setToRotation(angle);
        m02 = px * (1.0 - m00) + py * m10;
        m12 = py * (1.0 - m00) - px * m10;
        type = TYPE_UNKNOWN;
    }

    /**
     * Creates a new AffineTransformation that is a translation alone with the
     * translation vector given by the values sent as parameters. The new
     * transformation's type is <code>TYPE_IDENTITY</code> if the
     * AffineTransformation is the identity transformation, otherwise it's
     * <code>TYPE_TRANSLATION</code>.
     *
     * @param mx the distance to translate in the x direction.
     * @param my the distance to translate in the y direction.
     * @return the new AffineTransformation.
     */
    public static CGAffineTransform makeTranslation(double mx, double my) {
        CGAffineTransform t = new CGAffineTransform();
        t.setToTranslation(mx, my);
        return t;
    }

    /**
     * Creates a new AffineTransformation that is a scale alone. The new
     * transformation's type is <code>TYPE_IDENTITY</code> if the
     * AffineTransformation is the identity transformation, otherwise it's
     * <code>TYPE_UNKNOWN</code>.
     *
     * @param scx the scaling factor in the x direction.
     * @param scY the scaling factor in the y direction.
     * @return the new AffineTransformation.
     */
    public static CGAffineTransform makeScale(double scx, double scY) {
        CGAffineTransform t = new CGAffineTransform();
        t.setToScale(scx, scY);
        return t;
    }

    /**
     * Creates a new AffineTransformation that is a shear alone. The new
     * transformation's type is <code>TYPE_IDENTITY</code> if the
     * AffineTransformation is the identity transformation, otherwise it's
     * <code>TYPE_UNKNOWN</code>.
     *
     * @param shx the shearing factor in the x direction.
     * @param shy the shearing factor in the y direction.
     * @return the new AffineTransformation.
     */
    public static CGAffineTransform makeShear(double shx, double shy) {
        CGAffineTransform m = new CGAffineTransform();
        m.setToShear(shx, shy);
        return m;
    }

    /**
     * Creates a new AffineTransformation that is a rotation alone. The new
     * transformation's type is <code>TYPE_IDENTITY</code> if the
     * AffineTransformation is the identity transformation, otherwise it's
     * <code>TYPE_UNKNOWN</code>.
     *
     * @param angle the angle of rotation in radians.
     * @return the new AffineTransformation.
     */
    public static CGAffineTransform makeRotation(double angle) {
        CGAffineTransform t = new CGAffineTransform();
        t.setToRotation(angle);
        return t;
    }

    /**
     * Creates a new AffineTransformation that is a rotation followed by a
     * translation. Sets the type to <code>TYPE_UNKNOWN</code>.
     *
     * @param angle the angle of rotation in radians.
     * @param x     the distance to translate in the x direction.
     * @param y     the distance to translate in the y direction.
     * @return the new AffineTransformation.
     */
    public static CGAffineTransform makeRotation(double angle, double x, double y) {
        CGAffineTransform t = new CGAffineTransform();
        t.setToRotation(angle, x, y);
        return t;
    }

    /**
     * Applies a translation to this AffineTransformation.
     *
     * @param mx the distance to translate in the x direction.
     * @param my the distance to translate in the y direction.
     */
    public CGAffineTransform getTransformTranslate(double mx, double my) {
        return getTransformConcat(CGAffineTransform.makeTranslation(mx, my));
    }

    /**
     * Applies a scaling transformation to this AffineTransformation.
     *
     * @param scx the scaling factor in the x direction.
     * @param scy the scaling factor in the y direction.
     */
    public CGAffineTransform getTransformScale(double scx, double scy) {
        return getTransformConcat(CGAffineTransform.makeScale(scx, scy));
    }

    /**
     * Applies a shearing transformation to this AffineTransformation.
     *
     * @param shx the shearing factor in the x direction.
     * @param shy the shearing factor in the y direction.
     */
    public void shear(double shx, double shy) {
        getTransformConcat(CGAffineTransform.makeShear(shx, shy));
    }

    /**
     * Applies a rotation transformation to this AffineTransformation.
     *
     * @param angle the angle of rotation in radians.
     */
    public CGAffineTransform getTransformRotate(double angle) {
        return getTransformConcat(CGAffineTransform.makeRotation(angle));
    }

    /**
     * Applies a rotation and translation transformation to this
     * AffineTransformation.
     *
     * @param angle the angle of rotation in radians.
     * @param px    the distance to translate in the x direction.
     * @param py    the distance to translate in the y direction.
     */
    public void rotate(double angle, double px, double py) {
        getTransformConcat(CGAffineTransform.makeRotation(angle, px, py));
    }

    /**
     * Multiplies the matrix representations of two AffineTransform objects.
     *
     * @param t1 - the AffineTransform object is a multiplicand
     * @param t2 - the AffineTransform object is a multiplier
     * @return an AffineTransform object that is the result of t1 multiplied by
     *         the matrix t2.
     */
    public static CGAffineTransform multiply(CGAffineTransform t1, CGAffineTransform t2) {
        return new CGAffineTransform(t1.m00 * t2.m00 + t1.m10 * t2.m01, // m00
                t1.m00 * t2.m10 + t1.m10 * t2.m11, // m01
                t1.m01 * t2.m00 + t1.m11 * t2.m01, // m10
                t1.m01 * t2.m10 + t1.m11 * t2.m11, // m11
                t1.m02 * t2.m00 + t1.m12 * t2.m01 + t2.m02, // m02
                t1.m02 * t2.m10 + t1.m12 * t2.m11 + t2.m12);// m12
    }

    /**
     * Applies the given AffineTransform to this AffineTransform via matrix
     * multiplication.
     *
     * @param t the AffineTransform to apply to this AffineTransform.
     */
    public CGAffineTransform getTransformConcat(CGAffineTransform t) {
        return multiply(t, this);
    }
    

    /**
     * Changes the current AffineTransform the one obtained by taking the
     * transform t and applying this AffineTransform to it.
     *
     * @param t the AffineTransform that this AffineTransform is multiplied
     *          by.
     */
    public CGAffineTransform preConcatenate(CGAffineTransform t) {
        return multiply(this, t);
    }

    /**
     * Creates an AffineTransform that is the inverse of this transform.
     *
     * @return the affine transform that is the inverse of this AffineTransform.
     * @throws NoninvertibleTransformException
     *          if this AffineTransform cannot be inverted (the determinant
     *          of the linear transformation part is zero).
     */
    public CGAffineTransform getTransformInvert() /*throws NoninvertibleTransformException*/ {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
//            throw new NoninvertibleTransformException("Determinant is zero");
            return this;
        }
        return new CGAffineTransform(m11 / det, // m00
                -m10 / det, // m10
                -m01 / det, // m01
                m00 / det, // m11
                (m01 * m12 - m11 * m02) / det, // m02
                (m10 * m02 - m00 * m12) / det // m12
        );
    }

    /**
     * Apply the current AffineTransform to the point.
     *
     * @param src the original point.
     * @param dst Point2D object to be filled with the destination coordinates
     *            (where the original point is sent by this AffineTransform).
     *            May be null.
     * @return the point in the AffineTransform's image space where the original
     *         point is sent.
     */
    public CGPoint applyTransform(CGPoint src) {
        CGPoint dst = CGPoint.make(0, 0);

        float x = src.x;
        float y = src.y;

        dst.x = (float) (x * m00 + y * m01 + m02);
        dst.y = (float) (x * m10 + y * m11 + m12);
        return dst;
    }

    /**
     * Applies this AffineTransform to an array of points.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length > src.length</code> or
     *                                        <code>dstOff + length > dst.length</code>.
     */
    public void transform(CGPoint[] src, int srcOff, CGPoint[] dst, int dstOff, int length) {
        while (--length >= 0) {
            CGPoint srcPoint = src[srcOff++];
            double x = srcPoint.x;
            double y = srcPoint.y;
            CGPoint dstPoint = dst[dstOff];
            if (dstPoint == null) {
                dstPoint = CGPoint.zero();
            }
            dstPoint.x = (float) (x * m00 + y * m01 + m02);
            dstPoint.y = (float) (x * m10 + y * m11 + m12);
            dst[dstOff++] = dstPoint;
        }
    }

    /**
     * Applies this AffineTransform to a set of points given as an array of
     * double values where every two values in the array give the coordinates of
     * a point; the even-indexed values giving the x coordinates and the
     * odd-indexed values giving the y coordinates.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     */
    public void transform(double[] src, int srcOff, double[] dst, int dstOff, int length) {
        int step = 2;
        if (src == dst && srcOff < dstOff && dstOff < srcOff + length * 2) {
            srcOff = srcOff + length * 2 - 2;
            dstOff = dstOff + length * 2 - 2;
            step = -2;
        }
        while (--length >= 0) {
            double x = src[srcOff + 0];
            double y = src[srcOff + 1];
            dst[dstOff + 0] = x * m00 + y * m01 + m02;
            dst[dstOff + 1] = x * m10 + y * m11 + m12;
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Applies this AffineTransform to a set of points given as an array of
     * float values where every two values in the array give the coordinates of
     * a point; the even-indexed values giving the x coordinates and the
     * odd-indexed values giving the y coordinates.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     */
    public void transform(float[] src, int srcOff, float[] dst, int dstOff, int length) {
        int step = 2;
        if (src == dst && srcOff < dstOff && dstOff < srcOff + length * 2) {
            srcOff = srcOff + length * 2 - 2;
            dstOff = dstOff + length * 2 - 2;
            step = -2;
        }
        while (--length >= 0) {
            double x = src[srcOff + 0];
            double y = src[srcOff + 1];
            dst[dstOff + 0] = (float) (x * m00 + y * m01 + m02);
            dst[dstOff + 1] = (float) (x * m10 + y * m11 + m12);
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Applies this AffineTransform to a set of points given as an array of
     * float values where every two values in the array give the coordinates of
     * a point; the even-indexed values giving the x coordinates and the
     * odd-indexed values giving the y coordinates. The destination coordinates
     * are given as values of type <code>double</code>.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     */
    public void transform(float[] src, int srcOff, double[] dst, int dstOff, int length) {
        while (--length >= 0) {
            float x = src[srcOff++];
            float y = src[srcOff++];
            dst[dstOff++] = x * m00 + y * m01 + m02;
            dst[dstOff++] = x * m10 + y * m11 + m12;
        }
    }

    /**
     * Applies this AffineTransform to a set of points given as an array of
     * double values where every two values in the array give the coordinates of
     * a point; the even-indexed values giving the x coordinates and the
     * odd-indexed values giving the y coordinates. The destination coordinates
     * are given as values of type <code>float</code>.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     */
    public void transform(double[] src, int srcOff, float[] dst, int dstOff, int length) {
        while (--length >= 0) {
            double x = src[srcOff++];
            double y = src[srcOff++];
            dst[dstOff++] = (float) (x * m00 + y * m01 + m02);
            dst[dstOff++] = (float) (x * m10 + y * m11 + m12);
        }
    }

    /**
     * Transforms the point according to the linear transformation part of this
     * AffineTransformation (without applying the translation).
     *
     * @param src the original point.
     * @param dst the point object where the result of the delta transform is
     *            written.
     * @return the result of applying the delta transform (linear part only) to
     *         the original point.
     */
    // TODO: is this right? if dst is null, we check what it's an
    // instance of? Shouldn't it be src instanceof Point2D.Double?
    public CGPoint deltaTransform(CGPoint src, CGPoint dst) {
        if (dst == null) {
            dst = CGPoint.make(0, 0);
        }

        double x = src.x;
        double y = src.y;

        dst.x = (float) (x * m00 + y * m01);
        dst.y = (float) (x * m10 + y * m11);
        return dst;
    }

    /**
     * Applies the linear transformation part of this AffineTransform (ignoring
     * the translation part) to a set of points given as an array of double
     * values where every two values in the array give the coordinates of a
     * point; the even-indexed values giving the x coordinates and the
     * odd-indexed values giving the y coordinates.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the delta transformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     */
    public void deltaTransform(double[] src, int srcOff, double[] dst, int dstOff, int length) {
        while (--length >= 0) {
            double x = src[srcOff++];
            double y = src[srcOff++];
            dst[dstOff++] = x * m00 + y * m01;
            dst[dstOff++] = x * m10 + y * m11;
        }
    }

    /**
     * Transforms the point according to the inverse of this
     * AffineTransformation.
     *
     * @param src the original point.
     * @param dst the point object where the result of the inverse transform is
     *            written (may be null).
     * @return the result of applying the inverse transform. Inverse transform.
     * @throws NoninvertibleTransformException
     *          if this AffineTransform cannot be inverted (the determinant
     *          of the linear transformation part is zero).
     */
    public CGPoint inverseTransform(CGPoint src, CGPoint dst)
            throws NoninvertibleTransformException {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
            throw new NoninvertibleTransformException("Determinant is zero"); //$NON-NLS-1$
        }

        if (dst == null) {
            dst = CGPoint.zero();
        }

        double x = src.x - m02;
        double y = src.y - m12;

        dst.x = (float) ((x * m11 - y * m01) / det);
        dst.y = (float) ((y * m00 - x * m10) / det);
        return dst;
    }

    /**
     * Applies the inverse of this AffineTransform to a set of points given as
     * an array of double values where every two values in the array give the
     * coordinates of a point; the even-indexed values giving the x coordinates
     * and the odd-indexed values giving the y coordinates.
     *
     * @param src    the array of points to be transformed.
     * @param srcOff the offset in the source point array of the first point to be
     *               transformed.
     * @param dst    the point array where the images of the points (after applying
     *               the inverse of the AffineTransformation) should be placed.
     * @param dstOff the offset in the destination array where the new values
     *               should be written.
     * @param length the number of points to transform.
     * @throws ArrayIndexOutOfBoundsException if <code>srcOff + length*2 > src.length</code> or
     *                                        <code>dstOff + length*2 > dst.length</code>.
     * @throws NoninvertibleTransformException
     *                                        if this AffineTransform cannot be inverted (the determinant
     *                                        of the linear transformation part is zero).
     */
    public void inverseTransform(double[] src, int srcOff, double[] dst, int dstOff, int length)
            throws NoninvertibleTransformException {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
            throw new NoninvertibleTransformException("Determinant is zero"); //$NON-NLS-1$
        }

        while (--length >= 0) {
            double x = src[srcOff++] - m02;
            double y = src[srcOff++] - m12;
            dst[dstOff++] = (x * m11 - y * m01) / det;
            dst[dstOff++] = (y * m00 - x * m10) / det;
        }
    }
    
	public void set(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
	} 
	
    public void translate(double mx, double my) {
    	type = TYPE_UNKNOWN;
    	
        m02 = mx * m00 + my * m01 + m02;
        m12 = mx * m10 + my * m11 + m12;
    }
    
    public void rotate(double angle) {
    	double sin = Math.sin(angle);
    	double cos = Math.cos(angle);
    	if (Math.abs(cos) < ZERO) {
    		cos = 0.0;
    		sin = sin > 0.0 ? 1.0 : -1.0;
    	} else if (Math.abs(sin) < ZERO) {
    		sin = 0.0;
    		cos = cos > 0.0 ? 1.0 : -1.0;
    	}
    	
    	type = TYPE_UNKNOWN;	
    	
    	double m00 =   cos * this.m00 + sin * this.m01;
    	double m10 =   cos * this.m10 + sin * this.m11;
        double m01 =  -sin * this.m00 + cos * this.m01;
        double m11 =  -sin * this.m10 + cos * this.m11;
    	
    	this.m00 = m00;
    	this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }  
    
    public void scale(double scx, double scy) {
		type = TYPE_UNKNOWN;	
		
		m00 = scx * m00;
		double m10 = scx * this.m10;
	    double m01 = scy * this.m01;
	    m11 = scy * m11;

		this.m01 = m01;
	    this.m10 = m10;
	}
    
    public void multiply(CGAffineTransform t) {
    	double m00 = this.m00 * t.m00 + this.m10 * t.m01;
		double m01 = this.m00 * t.m10 + this.m10 * t.m11;
		double m10 = this.m01 * t.m00 + this.m11 * t.m01;
		double m11 = this.m01 * t.m10 + this.m11 * t.m11;
		double m02 = this.m02 * t.m00 + this.m12 * t.m01 + t.m02;
		double m12 = this.m02 * t.m10 + this.m12 * t.m11 + t.m12;

		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
    }

//    /**
//     * Creates a new shape whose data is given by applying this AffineTransform
//     * to the specified shape.
//     *
//     * @param src
//     *            the original shape whose data is to be transformed.
//     * @return the new shape found by applying this AffineTransform to the
//     *         original shape.
//     */
//    public Shape createTransformedShape(Shape src) {
//        if (src == null) {
//            return null;
//        }
//        if (src instanceof GeneralPath) {
//            return ((GeneralPath)src).createTransformedShape(this);
//        }
//        PathIterator path = src.getPathIterator(this);
//        GeneralPath dst = new GeneralPath(path.getWindingRule());
//        dst.append(path, false);
//        return dst;
//    }

    @Override
    public String toString() {
        return getClass().getName() + "[[" + m00 + ", " + m01 + ", " + m02 + "], [" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + m10 + ", " + m11 + ", " + m12 + "]]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public int hashCode() {
        HashCode hash = new HashCode();
        hash.append(m00);
        hash.append(m01);
        hash.append(m02);
        hash.append(m10);
        hash.append(m11);
        hash.append(m12);
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CGAffineTransform) {
            CGAffineTransform t = (CGAffineTransform) obj;
            return m00 == t.m00 && m01 == t.m01 && m02 == t.m02 && m10 == t.m10 && m11 == t.m11
                    && m12 == t.m12;
        }
        return false;
    }

    /**
     * Writes the AffineTrassform object to the output steam.
     *
     * @param stream - the output stream.
     * @throws java.io.IOException - if there are I/O errors while writing to the output stream.
     */
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Read the AffineTransform object from the input stream.
     *
     * @param stream - the input stream.
     * @throws IOException            - if there are I/O errors while reading from the input
     *                                stream.
     * @throws ClassNotFoundException - if class could not be found.
     */
    private void readObject(java.io.ObjectInputStream stream) throws IOException,
            ClassNotFoundException {
        stream.defaultReadObject();
        type = TYPE_UNKNOWN;
    }

    public static void CGAffineToGL(CGAffineTransform t, float []m) {
        // | m[0] m[4] m[8]  m[12] |     | m11 m21 m31 m41 |     | a c 0 tx |
        // | m[1] m[5] m[9]  m[13] |     | m12 m22 m32 m42 |     | b d 0 ty |
        // | m[2] m[6] m[10] m[14] | <=> | m13 m23 m33 m43 | <=> | 0 0 1  0 |
        // | m[3] m[7] m[11] m[15] |     | m14 m24 m34 m44 |     | 0 0 0  1 |

        m[2] = m[3] = m[6] = m[7] = m[8] = m[9] = m[11] = m[14] = 0.0f;
        m[10] = m[15] = 1.0f;
        m[0] = (float)t.m00; m[4] = (float)t.m01; m[12] = (float)t.m02;
        m[1] = (float)t.m10; m[5] = (float)t.m11; m[13] = (float)t.m12;
    }

    public static void GLToCGAffine(float []m, CGAffineTransform t) {
        t.m00 = m[0]; t.m01 = m[4]; t.m02 = m[12];
        t.m10 = m[1]; t.m11 = m[5]; t.m12 = m[13];
    }    
}

final class HashCode {

    private final HashCode hashCode = new HashCode();

    /**
     * Returns accumulated hashCode
     */
    public final int hashCode() {
        return hashCode.hashCode();
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(int value) {
        return hashCode.append(value);
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(long value) {
        return hashCode.append(value);
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(float value) {
        return hashCode.append(value);
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(double value) {
        return hashCode.append(value);
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(boolean value) {
        return hashCode.append(value);
    }

    /**
     * Appends value's hashCode to the current hashCode.
     *
     * @param value new element
     * @return this
     */
    public final HashCode append(Object value) {
        return hashCode.append(value);
    }
}
