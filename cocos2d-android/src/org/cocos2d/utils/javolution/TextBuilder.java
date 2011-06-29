/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2006 - Javolution (http://javolution.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.cocos2d.utils.javolution;


import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Text;

/**
 * <p> This class represents an {@link Appendable} text whose capacity expands 
 *     gently without incurring expensive resize/copy operations ever.</p>
 *     
 * <p> This class is not intended for large documents manipulations which 
 *     should be performed with the {@link Text} class directly 
 *     (<code>O(Log(n))</code> {@link Text#insert insertion} and 
 *     {@link Text#delete deletion} capabilities).</p>
 *     
 * <p> This implementation is not synchronized.</p>
 *     
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.3, January 20, 2008
 */
public class TextBuilder implements Appendable,
        CharSequence {
	// We do a full resize (and copy) only when the capacity is less than C1.
    // For large collections, multi-dimensional arrays are employed.
    private static final int B0 = 5; // Initial capacity in bits.
    private static final int C0 = 1 << B0; // Initial capacity (32)
    private static final int B1 = 10; // Low array maximum capacity in bits.
    private static final int C1 = 1 << B1; // Low array maximum capacity (1024).
    private static final int M1 = C1 - 1; // Mask.
    // Resizes up to 1024 maximum (32, 64, 128, 256, 512, 1024). 
    private char[] _low;
    // For larger capacity use multi-dimensional array.
    private char[][] _high;
    /**
     * Holds the current length.
     */
    private int _length;
    /**
     * Holds current capacity.
     */
    private int _capacity;

    /**
     * Creates a text builder of small initial capacity.
     */
    public TextBuilder() {
        _capacity = C0;
        _low = new char[C0];
        _high = new char[1][];
        _high[0] = _low;
    }

    /**
     * Creates a text builder holding the specified <code>String</code>
     * (convenience method).
     * 
     * @param str the initial string content of this text builder.
     */
    public TextBuilder(String str) {
        this();
        append(str);
    }

    /**
     * Creates a text builder of specified initial capacity.
     * Unless the text length exceeds the specified capacity, operations 
     * on this text builder will not allocate memory.
     * 
     * @param capacity the initial capacity.
     */
    public TextBuilder(int capacity) {
        this();
        while (capacity > _capacity) {
            increaseCapacity();
        }
    }

    /**
     * Returns the length (character count) of this text builder.
     *
     * @return the number of characters (16-bits Unicode).
     */
    public final int length() {
        return _length;
    }

    /**
     * Returns the character at the specified index.
     *
     * @param  index the index of the character.
     * @return the character at the specified index.
     * @throws IndexOutOfBoundsException if 
     *         <code>(index < 0) || (index >= this.length())</code>.
     */
    public final char charAt(int index) {
        if (index >= _length)
            throw new IndexOutOfBoundsException();
        return index < C1 ? _low[index] : _high[index >> B1][index & M1];
    }

    /**
     * Copies the character from this text builder into the destination
     * character array. 
     *
     * @param srcBegin this text start index.
     * @param srcEnd this text end index (not included).
     * @param dst the destination array to copy the data into.
     * @param dstBegin the offset into the destination array. 
     * @throws IndexOutOfBoundsException if <code>(srcBegin < 0) ||
     *  (dstBegin < 0) || (srcBegin > srcEnd) || (srcEnd > this.length())
     *  || ((dstBegin + srcEnd - srcBegin) >  dst.length)</code>
     */
    public final void getChars(int srcBegin, int srcEnd, char[] dst,
            int dstBegin) {
        if ((srcBegin < 0) || (srcBegin > srcEnd) || (srcEnd > this._length))
            throw new IndexOutOfBoundsException();
        for (int i = srcBegin, j = dstBegin; i < srcEnd;) {
            char[] chars0 = _high[i >> B1];
            int i0 = i & M1;
            int length = Math.min(C1 - i0, srcEnd - i);
            System.arraycopy(chars0, i0, dst, j, length);
            i += length;
            j += length;
        }
    }

    /**
     * Sets the character at the specified position.
     *
     * @param index the index of the character to modify.
     * @param c the new character. 
     * @throws IndexOutOfBoundsException if <code>(index < 0) || 
     *          (index >= this.length())</code>
     */
    public final void setCharAt(int index, char c) {
        if ((index < 0) || (index >= _length))
            throw new IndexOutOfBoundsException();
        _high[index >> B1][index & M1] = c;
    }

    /**
     * Convenience method equivalent to {@link #setLength(int, char)
     *  setLength(newLength, '\u0000')}.
     *
     * @param newLength the new length of this builder.
     * @throws IndexOutOfBoundsException if <code>(newLength < 0)</code>
     */
    public final void setLength(int newLength) {
        setLength(newLength, '\u0000');
    }

    /**
     * Sets the length of this character builder.
     * If the length is greater than the current length; the 
     * specified character is inserted.
     *
     * @param newLength the new length of this builder.
     * @param fillChar the character to be appended if required.
     * @throws IndexOutOfBoundsException if <code>(newLength < 0)</code>
     */
    public final void setLength(int newLength, char fillChar) {
        if (newLength < 0)
            throw new IndexOutOfBoundsException();
        if (newLength <= _length)
            _length = newLength;
        else
            for (int i = _length; i++ < newLength;) {
                append(fillChar);
            }
    }

    /**
     * Returns a {@link java.lang.CharSequence} corresponding 
     * to the character sequence between the specified indexes.
     *
     * @param  start the index of the first character inclusive.
     * @param  end the index of the last character exclusive.
     * @return a character sequence.
     * @throws IndexOutOfBoundsException if <code>(start < 0) || (end < 0) ||
     *         (start > end) || (end > this.length())</code>
     */
    public final java.lang.CharSequence subSequence(int start, int end) {
        if ((start < 0) || (end < 0) || (start > end) || (end > _length))
            throw new IndexOutOfBoundsException();
        int length = end - start;
        char[] arr = new char[length];
        getChars(start, end, arr, 0);
        return new String(arr);
    }

    /**
     * Appends the specified character.
     *
     * @param  c the character to append.
     * @return <code>this</code>
     */
    public final  TextBuilder  append(char c) {
        if (_length >= _capacity)
            increaseCapacity();
        _high[_length >> B1][_length & M1] = c;
        _length++;
        return this;
    }

    /**
     * Appends the textual representation of the specified object. 
     * This method is equivalent to <code>append(Text.valueOf(obj))</code>
     *
     * @param obj the object to represent or <code>null</code>.
     * @return <code>this</code>
     * @see Text#valueOf(Object)
     */
    public final TextBuilder append(Object obj) {
        if (obj instanceof String)
            return append((String) obj);
        if (obj instanceof Number)
            return appendNumber((Number)obj);
        return append(String.valueOf(obj));
    }

    // For Integer, Long, Float and Double use direct formatting.
    private TextBuilder appendNumber(Object num) {
        if (num instanceof Integer)
            return append(((Integer) num).intValue());
        if (num instanceof Long)
            return append(((Long) num).longValue());
        if (num instanceof Float)
            return append(((Float) num).floatValue());
        if (num instanceof Double)
            return append(((Double) num).doubleValue());
        return append(String.valueOf(num));
    }

    /**
     * Appends the specified character sequence. If the specified character
     * sequence is <code>null</code> this method is equivalent to
     * <code>append("null")</code>.
     *
     * @param  csq the character sequence to append or <code>null</code>.
     * @return <code>this</code>
     */
    public final  TextBuilder  append(CharSequence csq) {
        return (csq == null) ? append("null") : append(csq, 0, csq.length());
    }

    /**
     * Appends a subsequence of the specified character sequence.
     * If the specified character sequence is <code>null</code> this method 
     * is equivalent to <code>append("null")</code>. 
     *
     * @param  csq the character sequence to append or <code>null</code>.
     * @param  start the index of the first character to append.
     * @param  end the index after the last character to append.
     * @return <code>this</code>
     * @throws IndexOutOfBoundsException if <code>(start < 0) || (end < 0) 
     *         || (start > end) || (end > csq.length())</code>
     */
    public final  TextBuilder  append(CharSequence csq, int start, int end) {
        if (csq == null)
            return append("null");
        if ((start < 0) || (end < 0) || (start > end) || (end > csq.length()))
            throw new IndexOutOfBoundsException();
        for (int i = start; i < end;) {
            append(csq.charAt(i++));
        }
        return this;
    }

    /**
     * Appends the specified string to this text builder. 
     * If the specified string is <code>null</code> this method 
     * is equivalent to <code>append("null")</code>. 
     *
     * @param str the string to append or <code>null</code>.
     * @return <code>this</code>
     */
    public final TextBuilder append(String str) {
        return (str == null) ? append("null") : append(str, 0, str.length());
    }

    /**
     * Appends a subsequence of the specified string.
     * If the specified character sequence is <code>null</code> this method 
     * is equivalent to <code>append("null")</code>. 
     *
     * @param  str the string to append or <code>null</code>.
     * @param  start the index of the first character to append.
     * @param  end the index after the last character to append.
     * @return <code>this</code>
     * @throws IndexOutOfBoundsException if <code>(start < 0) || (end < 0) 
     *         || (start > end) || (end > str.length())</code>
     */
    public final TextBuilder append(String str, int start, int end) {
        if (str == null)
            return append("null");
        if ((start < 0) || (end < 0) || (start > end) || (end > str.length()))
            throw new IndexOutOfBoundsException("start: " + start + ", end: " + end + ", str.length(): " + str.length());
        int newLength = _length + end - start;
        while (_capacity < newLength) {
            increaseCapacity();
        }
        for (int i = start, j = _length; i < end;) {
            char[] chars = _high[j >> B1];
            int dstBegin = j & M1;
            int inc = Math.min(C1 - dstBegin, end - i);
            str.getChars(i, (i += inc), chars, dstBegin);
            j += inc;
        }
        _length = newLength;
        return this;
    }

    /**
     * Appends the characters from the char array argument.
     *
     * @param  chars the character array source.
     * @return <code>this</code>
     */
    public final TextBuilder append(char chars[]) {
        append(chars, 0, chars.length);
        return this;
    }

    /**
     * Appends the characters from a subarray of the char array argument.
     *
     * @param  chars the character array source.
     * @param  offset the index of the first character to append.
     * @param  length the number of character to append.
     * @return <code>this</code>
     * @throws IndexOutOfBoundsException if <code>(offset < 0) || 
     *         (length < 0) || ((offset + length) > chars.length)</code>
     */
    public final TextBuilder append(char chars[], int offset, int length) {
        final int end = offset + length;
        if ((offset < 0) || (length < 0) || (end > chars.length))
            throw new IndexOutOfBoundsException();
        int newLength = _length + length;
        while (_capacity < newLength) {
            increaseCapacity();
        }
        for (int i = offset, j = _length; i < end;) {
            char[] dstChars = _high[j >> B1];
            int dstBegin = j & M1;
            int inc = Math.min(C1 - dstBegin, end - i);
            System.arraycopy(chars, i, dstChars, dstBegin, inc);
            i += inc;
            j += inc;
        }
        _length = newLength;
        return this;
    }

    /**
     * Appends the textual representation of the specified <code>boolean</code>
     * argument.
     *
     * @param  b the <code>boolean</code> to format.
     * @return <code>this</code>
     * @see    TypeFormat
     */
    public final TextBuilder append(boolean b) {
        return b ? append("true") : append("false");
    }

    /**
     * Appends the decimal representation of the specified <code>int</code>
     * argument.
     *
     * @param  i the <code>int</code> to format.
     * @return <code>this</code>
     */
    public final TextBuilder append(int i) {
        if (i <= 0) {
            if (i == 0)
                return append("0");
            if (i == Integer.MIN_VALUE) // Negation would overflow.
                return append("-2147483648");
            append('-');
            i = -i;
        }
        int digits = MathLib.digitLength(i);
        if (_capacity < _length + digits)
            increaseCapacity();
        _length += digits;
        for (int index = _length - 1;; index--) {
            int j = i / 10;
            _high[index >> B1][index & M1] = (char) ('0' + i - (j * 10));
            if (j == 0)
                return this;
            i = j;
        }
    }

    /**
     * Appends the radix representation of the specified <code>int</code>
     * argument.
     *
     * @param  i the <code>int</code> to format.
     * @param  radix the radix (e.g. <code>16</code> for hexadecimal).
     * @return <code>this</code>
     */
    public final TextBuilder append(int i, int radix) {
        if (radix == 10)
            return append(i); // Faster.
        if (radix < 2 || radix > 36)
            throw new IllegalArgumentException("radix: " + radix);
        if (i < 0) {
            append('-');
            if (i == Integer.MIN_VALUE) { // Negative would overflow.
                appendPositive(-(i / radix), radix);
                return (TextBuilder) append(DIGIT_TO_CHAR[-(i % radix)]);
            }
            i = -i;
        }
        appendPositive(i, radix);
        return this;
    }

    private void appendPositive(int l1, int radix) {
        if (l1 >= radix) {
            int l2 = l1 / radix;
            // appendPositive(l2, radix);
            if (l2 >= radix) {
                int l3 = l2 / radix;
                // appendPositive(l3, radix);
                if (l3 >= radix) {
                    int l4 = l3 / radix;
                    appendPositive(l4, radix);
                    append(DIGIT_TO_CHAR[l3 - (l4 * radix)]);
                } else
                    append(DIGIT_TO_CHAR[l3]);
                append(DIGIT_TO_CHAR[l2 - (l3 * radix)]);
            } else
                append(DIGIT_TO_CHAR[l2]);
            append(DIGIT_TO_CHAR[l1 - (l2 * radix)]);
        } else
            append(DIGIT_TO_CHAR[l1]);
    }
    private final static char[] DIGIT_TO_CHAR = {'0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
        'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z'
    };

    /**
     * Appends the decimal representation of the specified <code>long</code>
     * argument.
     *
     * @param  l the <code>long</code> to format.
     * @return <code>this</code>
     */
    public final TextBuilder append(long l) {
        if (l <= 0) {
            if (l == 0)
                return append("0");
            if (l == Long.MIN_VALUE) // Negation would overflow.
                return append("-9223372036854775808");
            append('-');
            l = -l;
        }
        if (l <= Integer.MAX_VALUE)
            return append((int) l);
        append(l / 1000000000);
        int i = (int) (l % 1000000000);
        int digits = MathLib.digitLength(i);
        append("000000000", 0, 9 - digits);
        return append(i);
    }

    /**
     * Appends the radix representation of the specified <code>long</code>
     * argument.
     *
     * @param  l the <code>long</code> to format.
     * @param  radix the radix (e.g. <code>16</code> for hexadecimal).
     * @return <code>this</code>
     */
    public final TextBuilder append(long l, int radix) {
        if (radix == 10)
            return append(l); // Faster.
        if (radix < 2 || radix > 36)
            throw new IllegalArgumentException("radix: " + radix);
        if (l < 0) {
            append('-');
            if (l == Long.MIN_VALUE) { // Negative would overflow.
                appendPositive(-(l / radix), radix);
                return (TextBuilder) append(DIGIT_TO_CHAR[(int) -(l % radix)]);
            }
            l = -l;
        }
        appendPositive(l, radix);
        return this;
    }

    private void appendPositive(long l1, int radix) {
        if (l1 >= radix) {
            long l2 = l1 / radix;
            // appendPositive(l2, radix);
            if (l2 >= radix) {
                long l3 = l2 / radix;
                // appendPositive(l3, radix);
                if (l3 >= radix) {
                    long l4 = l3 / radix;
                    appendPositive(l4, radix);
                    append(DIGIT_TO_CHAR[(int) (l3 - (l4 * radix))]);
                } else
                    append(DIGIT_TO_CHAR[(int) l3]);
                append(DIGIT_TO_CHAR[(int) (l2 - (l3 * radix))]);
            } else
                append(DIGIT_TO_CHAR[(int) l2]);
            append(DIGIT_TO_CHAR[(int) (l1 - (l2 * radix))]);
        } else
            append(DIGIT_TO_CHAR[(int) l1]);
    }

    /**
     * Appends the textual representation of the specified <code>float</code>.
     *
     * @param  f the <code>float</code> to format.
     * @return <code>append(f, 10, (abs(f) &gt;= 1E7) || (abs(f) &lt; 0.001), false)</code>
     */
    public final TextBuilder append(float f) {
        return append(f, 10, (Math.abs(f) >= 1E7) || (Math.abs(f) < 0.001), false);
    }

    /**
     * Appends the textual representation of the specified <code>double</code>;
     * the number of digits is 17 or 16 when the 16 digits representation 
     * can be parsed back to the same <code>double</code> (mimic the standard
     * library formatting).
     * 
     * @param  d the <code>double</code> to format.
     * @return <code>append(d, -1, (MathLib.abs(d) >= 1E7) ||
     *        (MathLib.abs(d) < 0.001), false)</code>
     */
    public final TextBuilder append(double d) {
        return append(d, -1, (Math.abs(d) >= 1E7) ||
                (Math.abs(d) < 0.001), false);
    }

    /**
     * Appends the textual representation of the specified <code>double</code>
     * according to the specified formatting arguments.
     *
     * @param  d the <code>double</code> value.
     * @param  digits the number of significative digits (excludes exponent) or
     *         <code>-1</code> to mimic the standard library (16 or 17 digits).
     * @param  scientific <code>true</code> to forces the use of the scientific 
     *         notation (e.g. <code>1.23E3</code>); <code>false</code> 
     *         otherwise. 
     * @param  showZero <code>true</code> if trailing fractional zeros are 
     *         represented; <code>false</code> otherwise.
     * @return <code>TypeFormat.format(d, digits, scientific, showZero, this)</code>
     * @throws IllegalArgumentException if <code>(digits &gt; 19)</code>)
     */
    public final TextBuilder append(double d, int digits,
            boolean scientific, boolean showZero) {
        if (digits > 19)
            throw new IllegalArgumentException("digits: " + digits);
        if (d != d) // NaN
            return append("NaN");
        if (d == Double.POSITIVE_INFINITY)
            return append("Infinity");
        if (d == Double.NEGATIVE_INFINITY)
            return append("-Infinity");
        if (d == 0.0) { // Zero.
            if (digits < 0)
                return append("0.0");
            append('0');
            if (showZero) {
                append('.');
                for (int j = 1; j < digits; j++) {
                    append('0');
                }
            }
            return this;
        }
        if (d < 0) { // Work with positive number.
            d = -d;
            append('-');
        }

        // Find the exponent e such as: value == x.xxx * 10^e
        int e = MathLib.floorLog10(d);

        long m;
        if (digits < 0) { // Use 16 or 17 digits.
            // Try 17 digits.
            long m17 = MathLib.toLongPow10(d, (17 - 1) - e);
            // Check if we can use 16 digits.
            long m16 = m17 / 10;
            double dd = MathLib.toDoublePow10(m16, e - 16 + 1);
            if (dd == d) { // 16 digits is enough.
                digits = 16;
                m = m16;
            } else { // We cannot remove the last digit.
                digits = 17;
                m = m17;
            }
        } else // Use the specified number of digits.
            m = MathLib.toLongPow10(d, (digits - 1) - e);

        // Formats.
        if (scientific || (e >= digits)) {
            // Scientific notation has to be used ("x.xxxEyy").
            long pow10 = POW10_LONG[digits - 1];
            int k = (int) (m / pow10); // Single digit.
            append((char) ('0' + k));
            m = m - pow10 * k;
            appendFraction(m, digits - 1, showZero);
            append('E');
            append(e);
        } else { // Dot within the string ("xxxx.xxxxx").
            int exp = digits - e - 1;
            if (exp < POW10_LONG.length) {
                long pow10 = POW10_LONG[exp];
                long l = m / pow10;
                append(l);
                m = m - pow10 * l;
            } else
                append('0'); // Result of the division by a power of 10 larger than any long.
            appendFraction(m, exp, showZero);
        }
        return this;
    }

    private final void appendFraction(long l, int digits, boolean showZero) {
        append('.');
        if (l == 0)
            if (showZero)
                for (int i = 0; i < digits; i++) {
                    append('0');
                }
            else
                append('0');
        else { // l is different from zero.
            int length = MathLib.digitLength(l);
            for (int j = length; j < digits; j++) {
                append('0'); // Add leading zeros.
            }
            if (!showZero)
                while (l % 10 == 0) {
                    l /= 10; // Remove trailing zeros.
                }
            append(l);
        }
    }
    private static final long[] POW10_LONG = new long[]{1L, 10L, 100L, 1000L,
        10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L,
        10000000000L, 100000000000L, 1000000000000L, 10000000000000L,
        100000000000000L, 1000000000000000L, 10000000000000000L,
        100000000000000000L, 1000000000000000000L};

    /**
     * Inserts the specified character sequence at the specified location.
     *
     * @param index the insertion position.
     * @param csq the character sequence being inserted.
     * @return <code>this</code>
     * @throws IndexOutOfBoundsException if <code>(index < 0) || 
     *         (index > this.length())</code>
     */
    public final TextBuilder insert(int index, java.lang.CharSequence csq) {
        if ((index < 0) || (index > _length))
            throw new IndexOutOfBoundsException("index: " + index);
        final int shift = csq.length();
        _length += shift;
        while (_length >= _capacity) {
            increaseCapacity();
        }
        for (int i = _length - shift; --i >= index;) {
            this.setCharAt(i + shift, this.charAt(i));
        }
        for (int i = csq.length(); --i >= 0;) {
            this.setCharAt(index + i, csq.charAt(i));
        }
        return this;
    }

    /**
     * Removes all the characters of this text builder 
     * (equivalent to <code>this.delete(start, this.length())</code>).
     * 
     * @return <code>this.delete(0, this.length())</code>
     */
    public final TextBuilder clear() {
        _length = 0;
        return this;
    }

    /**
     * Removes the characters between the specified indices.
     * 
     * @param start the beginning index, inclusive.
     * @param end the ending index, exclusive.
     * @return <code>this</code>
     * @throws IndexOutOfBoundsException if <code>(start < 0) || (end < 0) 
     *         || (start > end) || (end > this.length())</code>
     */
    public final TextBuilder delete(int start, int end) {
        if ((start < 0) || (end < 0) || (start > end) || (end > this.length()))
            throw new IndexOutOfBoundsException();
        for (int i = end, j = start; i < _length;) {
            this.setCharAt(j++, this.charAt(i++));
        }
        _length -= end - start;
        return this;
    }

    /**
     * Reverses this character sequence.
     *
     * @return <code>this</code>
     */
    public final TextBuilder reverse() {
        final int n = _length - 1;
        for (int j = (n - 1) >> 1; j >= 0;) {
            char c = charAt(j);
            setCharAt(j, charAt(n - j));
            setCharAt(n - j--, c);
        }
        return this;
    }

    /**
     * Returns the <code>String</code> representation of this 
     * {@link TextBuilder}.
     *
     * @return the <code>java.lang.String</code> for this text builder.
     */
    public final String toString() {
        char[] data = new char[_length];
        this.getChars(0, _length, data, 0);
        return new String(data, 0, _length);
    }

    /**
     * Resets this text builder for reuse (equivalent to {@link #clear}).
     */
    public final void reset() {
        _length = 0;
    }

    /**
     * Returns the hash code for this text builder.
     *
     * @return the hash code value.
     */
    public final int hashCode() {
        int h = 0;
        for (int i = 0; i < _length;) {
            h = 31 * h + charAt(i++);
        }
        return h;
    }

    /**
     * Compares this text builder against the specified object for equality.
     * Returns <code>true</code> if the specified object is a text builder 
     * having the same character content.
     * 
     * @param  obj the object to compare with or <code>null</code>.
     * @return <code>true</code> if that is a text builder with the same 
     *         character content as this text; <code>false</code> otherwise.
     */
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TextBuilder))
            return false;
        TextBuilder that = (TextBuilder) obj;
        if (this._length != that._length)
            return false;
        for (int i = 0; i < _length;) {
            if (this.charAt(i) != that.charAt(i++))
                return false;
        }
        return true;
    }


    /**
     * Prints out this text builder to the specified writer.
     * 
     * @param writer the destination writer.
     */
    public void print(Writer writer) throws IOException {
        for (int i = 0; i < _length; i += C1) {
            char[] chars = _high[i >> B1];
            writer.write(chars, 0, Math.min(C1, _length - i));
        }
    }

    /**
     * Prints out this text builder to the specified writer and then terminates 
     * the line.
     * 
     * @param writer the destination writer.
     */
    public void println(Writer writer) throws IOException {
        print(writer);
        writer.write('\n');
    }

    /**
     * Indicates if this text builder has the same character content as the 
     * specified character sequence.
     *
     * @param csq the character sequence to compare with.
     * @return <code>true</code> if the specified character sequence has the 
     *        same character content as this text; <code>false</code> otherwise.
     */
    public final boolean contentEquals(java.lang.CharSequence csq) {
        if (csq.length() != _length)
            return false;
        for (int i = 0; i < _length;) {
            char c = i < C1 ? _low[i] : _high[i >> B1][i & M1];
            if (csq.charAt(i++) != c)
                return false;
        }
        return true;
    }

    /**
     * Equivalent to {@link #contentEquals(CharSequence)} 
     * (for J2ME compability).
     *
     * @param csq the string character sequence to compare with.
     * @return <code>true</code> if the specified string has the 
     *        same character content as this text; <code>false</code> otherwise.
     */
    public final boolean contentEquals(String csq) {
        if (csq.length() != _length)
            return false;
        for (int i = 0; i < _length;) {
            char c = i < C1 ? _low[i] : _high[i >> B1][i & M1];
            if (csq.charAt(i++) != c)
                return false;
        }
        return true;
    }

    /**
     * Increases this text builder capacity.
     */
    private void increaseCapacity() {
        if (_capacity < C1) { // For small capacity, resize.
            _capacity <<= 1;
            char[] tmp = new char[_capacity];
            System.arraycopy(_low, 0, tmp, 0, _length);
            _low = tmp;
            _high[0] = tmp;
        } else { // Add a new low block of 1024 elements.
            int j = _capacity >> B1;
            if (j >= _high.length) { // Resizes _high.
                char[][] tmp = new char[_high.length * 2][];
                System.arraycopy(_high, 0, tmp, 0, _high.length);
                _high = tmp;
            }
            _high[j] = new char[C1];
            _capacity += C1;
        }
    }
}
